package fvs.taxe;

import fvs.taxe.controller.Context;
import fvs.taxe.controller.GoalController;
import fvs.taxe.controller.ObstacleController;
import fvs.taxe.controller.ResourceController;
import fvs.taxe.controller.RouteController;
import fvs.taxe.controller.ScoreController;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TopBarController;
import fvs.taxe.dialog.DialogEndGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.TurnListener;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleType;
import gameLogic.obstacle.Rumble;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** This class displays the TaxeGame.java game state graphically to the player*/
public class GameScreen extends ScreenAdapter {
	
	/**Stores the instance of TaxeGame.java that this GameScreen is displaying*/
    final private TaxeGame game;
    
    /**Stores the instance of Stage.java that is used to hold the actors used in the Game, and is setup in the Class instantiation method*/
    private Stage stage;
    
    /**Stores the texture used as the background of the game. This is set internally in the Class instantiation method using the gamemap.png Asset*/
    private Texture mapTexture;
    
    /**Stores the instance of Game.java used to hold the game variable's GameLogic. This variable exists as a reference point to the instance set in
     * the Game.java class, which can be accessed statically
     */
    private Game gameLogic;
    
    /**Stores resources for the UI, such as font, color etc.*/
    private Skin skin;
    
    /**Holds an instance of the Game map. This exists as a reference to the gameLogic variable's map instance*/
    private Map map;
    
    /**This float tracks how long the game has been in the Animating state for. If it's value passes the constant ANIMATION_TIME then the Game stops animating and returns to it's normal state*/
    private float timeAnimated = 0;
    
    /**This constant integer value holds how long the Game can stay in the animating state for before moving to it's next state*/
    public static final int ANIMATION_TIME = 2;
    
    
    private Tooltip tooltip;
    private Context context;

    private StationController stationController;
    private TopBarController topBarController;
    private ResourceController resourceController;
    private GoalController goalController;
    private RouteController routeController;
	private ObstacleController obstacleController;
    private ScoreController scoreController;
    
	private Rumble rumble;

	/**Instantiation method. Sets up the game using the passed TaxeGame argument 
	 *@param game The instance of TaxeGame to be passed to the GameScreen to display
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
        topBarController = new TopBarController(context);
        resourceController = new ResourceController(context);
        goalController = new GoalController(context);
        routeController = new RouteController(context);
        obstacleController = new ObstacleController(context);
        scoreController = new ScoreController(context);

        context.setRouteController(routeController);
        context.setTopBarController(topBarController);
        
        rumble = new Rumble();
        
        gameLogic.getPlayerManager().subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
            	System.out.println("animating called");
                gameLogic.setState(GameState.ANIMATING);
                topBarController.displayFlashMessage("Time is passing...", Color.GREEN, Color.BLACK, ANIMATION_TIME);
                //goalController.showCurrentPlayerGoals();
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
        
        gameLogic.subscribeObstacleChanged(new ObstacleListener() {
			@Override
			public void started(Obstacle obstacle) {
				// set the obstacle so its visible
				obstacle.getActor().setVisible(true);
				
				// shake the screen if the obstacle is an earthquake
				if (obstacle.getType() == ObstacleType.EARTHQUAKE) {
					rumble = new Rumble();
					rumble.rumble(context, 1f, 2f);
				}
			}
			
			@Override
			public void ended(Obstacle obstacle) {
				obstacle.getActor().setVisible(false);
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


    // called every frame
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
       
        topBarController.drawBackground();

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
        
        game.batch.begin();
        game.fontSmall.draw(game.batch, "Target: " + gameLogic.TOTAL_POINTS + " points, Turn: " + (gameLogic.getPlayerManager().getTurnNumber() + 1), (float) TaxeGame.WIDTH - 250.0f, 20.0f);
        game.batch.end();

        resourceController.drawHeaderText();
        goalController.drawHeaderText();
        scoreController.drawScoreDetails();
        
        
        
    }

    @Override
    // Called when GameScreen becomes current screen of the game
    // order methods called matters for z-index!
    public void show() {
    	obstacleController.drawObstacles();
    	stationController.renderConnections(map.getConnections(), Color.GRAY);
        stationController.renderStations();
        topBarController.addEndTurnButton();
        resourceController.drawPlayerResources(gameLogic.getPlayerManager().getCurrentPlayer());
        goalController.showCurrentPlayerGoals();
        
    }

    
    @Override
    public void dispose() {
        mapTexture.dispose();
        stage.dispose();
    }

}