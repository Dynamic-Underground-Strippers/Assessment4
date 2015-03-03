package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.*;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.TurnListener;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.obstacle.Rumble;

/** This class displays the Game.java game state graphically to the player.*/
public class GameScreen extends ScreenAdapter {
	
	/**Stores the main instance of TaxeGame.java.*/
    final private TaxeGame game;
    
    /**Stores the instance of Stage.java that is used to hold the actors used in the Game, and is setup in the Class instantiation method.*/
    private Stage stage;
    
    /**Stores the texture used as the background of the game. This is set internally in the Class instantiation method using the gamemap.png Asset.*/
    private Texture mapTexture;
    
    /**Stores the instance of Game.java used to hold the game variable's GameLogic. This variable exists as a reference point to the instance set in
     * the Game.java class, which can be accessed statically.
     */
    private Game gameLogic;
    
    /**Stores resources for the UI, such as font, color etc.*/
    private Skin skin;
    
    /**Holds an instance of the Game map. This exists as a reference to the gameLogic variable's map instance.*/
    private Map map;
    
    /**This float tracks how long the game has been in the Animating state for. If it's value passes the constant ANIMATION_TIME then the Game stops animating and returns to it's normal state.*/
    private float timeAnimated = 0;
    
    /**This constant integer value holds how long the Game can stay in the animating state for before moving to it's next state.*/
    public static final int ANIMATION_TIME = 2;
    
    /**The instance of Tooltip used to display notifications to the player.*/
    private Tooltip tooltip;
    
    /**The Context in which the game runs. This collects the Game and all of it's controllers.*/
    private Context context;

    /**Controller for handling stations.*/
    private StationController stationController;
    
    /**Controller for handling the graphical bar at the top of the game.*/
    private SideBarController sideBarController;
    
    /**Controller for handling resources.*/
    private ResourceController resourceController;
    
    /**Controller for handling each of the players' goals.*/
    private GoalController goalController;
    
    /**Controller for handling routing between stations.*/
    private RouteController routeController;
    
    /**Controller for handling and placing obstacles.*/
	private ObstacleController obstacleController;
	
	/**Controller for handling the score.*/
	private ScoreController scoreController;

	/**Variable that is used to visibly "rumble" the game when an obstacle is placed.*/
	private Rumble rumble;
	
	/**Instantiation method. Sets up the game using the passed TaxeGame argument. 
	 *@param game The instance of TaxeGame to be passed to the GameScreen to display.
	*/
	public GameScreen(TaxeGame game) {
		this.game = game;
		stage = new Stage();
		skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		gameLogic = Game.getInstance();
		context = new Context(stage, skin, game, gameLogic);
		Gdx.input.setInputProcessor(stage);

		mapTexture = new Texture(Gdx.files.internal("gamemap.png"));
		map = gameLogic.getMap();

		tooltip = new Tooltip(skin);
		stage.addActor(tooltip);

		stationController = new StationController(context, tooltip);
		sideBarController = new SideBarController(context);
		resourceController = new ResourceController(context);
		goalController = new GoalController(context);
		routeController = new RouteController(context);
		obstacleController = new ObstacleController(context);
		scoreController = new ScoreController(context);

		context.setRouteController(routeController);
		context.setSideBarController(sideBarController);

		rumble = obstacleController.getRumble();

		gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				gameLogic.setState(GameState.ANIMATING);
				sideBarController.displayFlashMessage("Time is passing...", Color.GREEN, Color.BLACK, ANIMATION_TIME);
			}
		});

		gameLogic.subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state){
				if(gameLogic.getPlayerManager().getCurrentPlayer().getScore() >= gameLogic.TOTAL_POINTS) {
					DialogEndGame dia = new DialogEndGame(GameScreen.this.game, gameLogic.getPlayerManager(), skin);
					dia.show(stage);
				}
			}
		});

		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				// if the game is routing, set the route black when a new station is clicked
				if(gameLogic.getState() == GameState.ROUTING) {
					routeController.drawRoute(Color.BLACK);
				}
			}
		});
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (rumble.time > 0){
			Vector2 mapPosition = rumble.tick(delta);
			game.batch.begin();
			game.batch.draw(mapTexture, mapPosition.x, mapPosition.y);
			game.batch.end();
		} else {
			game.batch.begin();
			game.batch.draw(mapTexture, 0, 0);
			game.batch.end();
		}

		if(gameLogic.getState() == GameState.ANIMATING) {
			timeAnimated += delta;
			if (timeAnimated >= ANIMATION_TIME) {
				gameLogic.setState(GameState.NORMAL);
				timeAnimated = 0;
			}
		}

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if(gameLogic.getState() == GameState.NORMAL || gameLogic.getState() == GameState.PLACING){
			stationController.displayNumberOfTrainsAtStations();
		}
		
		resourceController.drawHeaderText();
		goalController.drawHeaderText();
		scoreController.drawScoreDetails();
		scoreController.drawFinalScoreDetails();
	}

	@Override
	public void show() {
		// order methods called matters for z-index!
		obstacleController.drawObstacles();
		stationController.drawConnections(map.getConnections(), Color.GRAY);
		stationController.drawStations();
		obstacleController.drawObstacleEffects();
		resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
		goalController.drawCurrentPlayerGoals();
		sideBarController.drawBackground();
		sideBarController.drawLabels();
		sideBarController.drawEndTurnButton();
	}

	@Override
	public void dispose() {
		mapTexture.dispose();
		stage.dispose();
	}
}