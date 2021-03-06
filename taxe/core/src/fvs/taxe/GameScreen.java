package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import fvs.taxe.controller.ClockController;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.EndTurnController;
import fvs.taxe.controller.GoalController;
import fvs.taxe.controller.NewsFlashController;
import fvs.taxe.controller.NotepadController;
import fvs.taxe.controller.ObstacleController;
import fvs.taxe.controller.RouteController;
import fvs.taxe.controller.ScoreController;
import fvs.taxe.controller.SkillBarController;
import fvs.taxe.controller.StationController;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.Player;
import gameLogic.PlayerChangedListener;
import gameLogic.TurnListener;
import gameLogic.map.Map;
import gameLogic.map.Station;

/**
 * This class displays the Game.java game state graphically to the player.
 */
public class GameScreen extends ScreenAdapter {

	/**
	 * Stores the main instance of TaxeGame.java.
	 */
	final private TaxeGame game;

	/**
	 * Stores the instance of Stage.java that is used to hold the actors used in the Game, and is setup in the Class instantiation method.
	 */
	private Stage stage;

	/**
	 * Stores the texture used as the background of the game. This is set internally in the Class instantiation method using the gamemap.png Asset.
	 */
	private Texture mapTexture;

	private Texture mapOverlayTexture;

	/**
	 * Stores the instance of Game.java used to hold the game variable's GameLogic. This variable exists as a reference point to the instance set in
	 * the Game.java class, which can be accessed statically.
	 */
	private Game gameLogic;

	/**
	 * Stores resources for the UI, such as font, color etc.
	 */
	private Skin skin;

	/**
	 * Holds an instance of the Game map. This exists as a reference to the gameLogic variable's map instance.
	 */
	private Map map;

	/**
	 * This float tracks how long the game has been in the Animating state for. If it's value passes the constant ANIMATION_TIME then the Game stops animating and returns to it's normal state.
	 */
	private float timeAnimated = 0;

	/**
	 * This constant integer value holds how long the Game can stay in the animating state for before moving to it's next state.
	 */
	public static final int ANIMATION_TIME = 2;

	/**
	 * The instance of Tooltip used to display notifications to the player.
	 */
	private Tooltip tooltip;

	/**
	 * The Context in which the game runs. This collects the Game and all of it's controllers.
	 */
	private Context context;

	/**
	 * Controller for handling stations.
	 */
	private StationController stationController;

	/**
	 * Controller for handling the graphical bar at the top of the game.
	 */
	private NotepadController notepadController;

	/**
	 * Controller for handling resources.
	 */
//	private ResourceController resourceController;

	/**
	 * Controller for handling each of the players' goals.
	 */
	private GoalController goalController;

	/**
	 * Controller for handling routing between stations.
	 */
	private RouteController routeController;

	/**
	 * Controller for handling and placing obstacles.
	 */
	private ObstacleController obstacleController;

	/**
	 * Controller for handling the score.
	 */
	private ScoreController scoreController;



	private ClockController clockController;

	private SkillBarController skillBarController;

	private EndTurnController endTurnController;

	private NewsFlashController newsFlashController;



	/**
	 * Instantiation method. Sets up the game using the passed TaxeGame argument.
	 *
	 * @param game The instance of TaxeGame to be passed to the GameScreen to display.
	 */
	public GameScreen(TaxeGame game) {
		this.game = game;
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		gameLogic = Game.initialiseGame();
		context = new Context(stage, skin, game, gameLogic);
		this.gameLogic.setContext(context);
		Gdx.input.setInputProcessor(stage);

		mapTexture = new Texture(Gdx.files.internal("Map4.png"));
		mapOverlayTexture = new Texture(Gdx.files.internal("map_overlay.png"));
		map = gameLogic.getMap();

		tooltip = new Tooltip(skin);
		stage.addActor(tooltip);

		stationController = new StationController(context, tooltip);
		notepadController = new NotepadController(context);
//		resourceController = new ResourceController(context);
		goalController = new GoalController(context);
		routeController = new RouteController(context);
		obstacleController = new ObstacleController(context);
		scoreController = new ScoreController(context);
		clockController = new ClockController(context);
		skillBarController = new SkillBarController(context);
		endTurnController = new EndTurnController(context);
		newsFlashController = new NewsFlashController(context);



		context.setRouteController(routeController);
		context.setNotepadController(notepadController);
		context.setNewsFlashController(newsFlashController);



		gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
					gameLogic.setState(GameState.ANIMATING);
			}
		});

		gameLogic.subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (gameLogic.getPlayerManager().getTurnNumber() >= gameLogic.MAX_TURNS) {
					DialogEndGame dia = new DialogEndGame(GameScreen.this.game,
							gameLogic.getPlayerManager(), skin);
					dia.show(stage);
				}
			}
		});

		gameLogic.getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
			@Override
			public void changed() {
				goalController.drawCurrentPlayerGoals();
			}
		});


		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				// if the game is routing, set the route black when a new station is clicked
				if (gameLogic.getState() == GameState.ROUTING) {
					routeController.drawRoute(Color.BLACK);
				}
			}
		});

		skillBarController.draw();

	}

	@Override
	public void render(float delta) {
		int turn = Game.getInstance().getPlayerManager().getTurnNumber();

		//After a certain turn, turn the map dark to create the illusion of nighttime
		int darkTurn = 22;
		if (turn < darkTurn) {
			Gdx.gl.glClearColor(255, 255, 255, 1);
		} else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
		}
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			game.batch.begin();
			game.batch.draw(mapTexture, 0, 85, 1022, 561);
			game.batch.end();

//After a certain turn, turn the map dark to create the illusion of nighttime
		if (turn >= darkTurn) {
			game.batch.begin();
			game.batch.draw(mapOverlayTexture, 0, 85, 1022, 561);
			game.batch.end();
		}

		if (gameLogic.getState() == GameState.ANIMATING) {
			timeAnimated += delta;
			if (timeAnimated >= ANIMATION_TIME) {
				gameLogic.setState(GameState.NORMAL);
				timeAnimated = 0;
				displayMessagesInBuffer();
			}
		}

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if (gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING) {
			stationController.displayNumberOfTrainsAtStations();
		}

		goalController.drawHeaderText();
		scoreController.drawScoreDetails();
	}

	@Override
	public void show() {
		// order methods called matters for z-index!
		obstacleController.drawObstacles();
		stationController.drawConnections(map.getConnections(), Color.GRAY);
		stationController.drawStations();
		obstacleController.drawObstacleEffects();
		notepadController.draw();
		goalController.drawCurrentPlayerGoals();
		clockController.draw();
		endTurnController.draw();
	}

	@Override
	public void dispose() {
		mapTexture.dispose();
		stage.dispose();
	}

	public void displayMessagesInBuffer() {
		Player currentPlayer = gameLogic.getPlayerManager().getCurrentPlayer();
		for (String message : currentPlayer.getMessages()) {
			Dialog dia = new Dialog("Notice", context.getSkin());
			dia.text(message).align(Align.center);
			dia.button("OK", "OK");
			dia.show(stage);
		}
		currentPlayer.clearBuffer();
	}


}