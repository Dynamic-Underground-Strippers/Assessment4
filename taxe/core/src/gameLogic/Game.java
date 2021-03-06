package gameLogic;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import fvs.taxe.actor.ObstacleActor;
import fvs.taxe.controller.Context;

import gameLogic.goal.GoalManager;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleManager;

import gameLogic.obstacle.ObstacleType;
import gameLogic.resource.Jelly;

import gameLogic.resource.NewConnection;

import gameLogic.resource.ResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.Tuple;

import com.badlogic.gdx.math.MathUtils;

/**Main Game class of the Game. Handles all of the game logic.*/
public class Game {

	private List<Obstacle> flus = new ArrayList<Obstacle>();

	/**The instance that the game is running in.*/
	private static Game instance;

	/** Context instance to draw jellies! **/

	private Context context;

	private Jelly jelly;

	/**The game's PlayerManager that handles both of the players.*/
	private PlayerManager playerManager;

	/**The game's GoalManager that handles goals for the players.*/
	private GoalManager goalManager;
	
	/**The game's ResourceManager that handles resources for the players.*/
	private ResourceManager resourceManager;
	
	/**The game's ObstacleManager that handles Obstacles in the game.*/
	private ObstacleManager obstacleManager;
	
	/**The game map for this instance.*/
	private Map map;
	
	/**The game state.*/
	private GameState state;
	
	/**List of listeners that listen to changes in game state.*/
	private List<GameStateListener> gameStateListeners = new ArrayList<GameStateListener>();
	
	/**List of listeners that listen to changes in obstacles.*/
	private List<ObstacleListener> obstacleListeners = new ArrayList<ObstacleListener>();

	/**The number of players that can play at one time.*/
	private final int CONFIG_PLAYERS = 2;
	
	/**The score a player must reach to win the game.*/
	public final int TOTAL_POINTS = 200;

	public final int MAX_TURNS = 30;


	/**The Instantiation method, sets up the players and game listeners.*/
	private Game() {
		playerManager = new PlayerManager();
		playerManager.createPlayers(CONFIG_PLAYERS);

		resourceManager = new ResourceManager();
		goalManager = new GoalManager(resourceManager);
		map = new Map();
		obstacleManager = new ObstacleManager(map);


			state = GameState.NORMAL;

			playerManager.subscribeTurnChanged(new TurnListener() {
				@Override
				public void changed() {
					Player currentPlayer = playerManager.getCurrentPlayer();
					goalManager.updatePlayerGoals(currentPlayer);
					resourceManager.addRandomResourceToPlayer(currentPlayer);
					resourceManager.addRandomResourceToPlayer(currentPlayer);
					resourceManager.createJelly();
					map.decrementBlockedConnections();
					map.blockRandomConnection();
					calculateObstacles();
					decreaseObstacleTime();
					spreadFlu();
					flu();

				}
			});
		}


	/**Returns the main game instance.*/
	public static Game getInstance() {
		return instance;
	}

	public static Game initialiseGame(){
		instance = new Game();
		instance.initialisePlayers();
		return instance;
	}

	/**Sets up the players. Only the first player is given goals and resources initially.*/
	private void initialisePlayers() {
			Player player = playerManager.getAllPlayers().get(0);
			goalManager.updatePlayerGoals(player);
			resourceManager.addRandomResourceToPlayer(player);
			resourceManager.addRandomResourceToPlayer(player);
	}

	/**@return The PlayerManager instance for this game.*/
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public void setContext(Context context){
		this.context = context;
	}

	public Context getContext(){
		return this.context;
	}


	/**@return The GoalManager instance for this game.*/
	public GoalManager getGoalManager() {
		return goalManager;
	}

	/**@return The ResourceManager instance for this game.*/
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**@return The Map instance for this game.*/
	public Map getMap() {
		return map;
	}

	/**@return The GameState instance for this game.*/
	public GameState getState() {
		return state;
	}

	/**Sets the GameState of the Game. Listeners are notified using stateChanged().*/
	public void setState(GameState state) {
			this.state = state;
		stateChanged();
	}

	/**Adds a listener for when the game state is changed.*/
	public void subscribeStateChanged(GameStateListener listener) {
		gameStateListeners.add(listener);
	}

	/**When the GameState is changed then all of the GameState Listeners are notified.*/
	private void stateChanged() {
		for(GameStateListener listener : gameStateListeners) {
			listener.changed(state);
		}
	}
	
	/**@return The ObstacleManager instance for this game.*/
	public ObstacleManager getObstacleManager(){
		return obstacleManager;
	}
	
	/**This method is called whenever an obstacle starts. All listeners are notified that this has happened.*/
	private void obstacleStarted(Obstacle obstacle) {
		// called whenever an obstacle starts, notifying all listeners that an obstacle has occured (handled by ... 
		for (ObstacleListener listener : obstacleListeners) {
			listener.started(obstacle);
		}
	}

	/**This method is called whenever an obstacle end. All listeners are notified that this has happened.*/
	private void obstacleEnded(Obstacle obstacle) {
		// called whenever an obstacle ends, notifying all listeners that an obstacle has occured (handled by ... 
		for (ObstacleListener listener : obstacleListeners) {
			listener.ended(obstacle);
		}
	}

	/**This method adds a new ObstacleListener to the game, which is notified when an Obstacle starts or ends.*/
	public void subscribeObstacleChanged(ObstacleListener obstacleListener) {
		obstacleListeners.add(obstacleListener);
	}
	
	/**This method causes one obstacle to happen at random, notifying the listeners.*/
	private void calculateObstacles() {
		//Decides whether or not to trigger the flood event, based on the probability, set at 0.1
		ArrayList<Obstacle> obstacles = obstacleManager.getObstacles();

		if (obstacles.size() > 0) {
			if (MathUtils.randomBoolean(0.1f)) {
				for (Obstacle obstacle : obstacles) {
					if (!obstacle.isActive()&&obstacle.getType()==ObstacleType.FLOOD){
						obstacleStarted(obstacle);
					}
				}
				context.getNewsFlashController().showNewsFlash("FloodDisruption.png");
			}
		}
	}
	
	/**This method decreases the remaining duration of any remaining obstacles by 1 turn. If the duration has reached 0, the obstacle is removed and all listeners are notified.*/
	private void decreaseObstacleTime() {
		// decreases any active obstacles time left active by 1
		ArrayList<Obstacle> obstacles = obstacleManager.getObstacles();
		for (int i = 0; i< obstacles.size(); i++) {
			Obstacle obstacle = obstacles.get(i);
			if (obstacle.isActive() && obstacle.getType()==ObstacleType.FLOOD) {
				boolean isTimeLeft = obstacle.decreaseTimeLeft();
				if (!isTimeLeft) {
					// if the time left = 0, then deactivate the obstacle
					obstacleEnded(obstacle);
				}
			}
		}
		
	}

	public void setJelly(Jelly jelly){
		this.jelly = jelly;
	}

	public Jelly getJelly() {
		return this.jelly;
	}



	public void flu(){
		//Generates a new flu at a random station if one does not exist
		if (flus.size()==0) {
			int rand = MathUtils.random(2);
			//50% chance for a flu to spawn
			if (rand == 0) {
				Station station = this.map.getRandomStation();
				Obstacle obstacle = obstacleManager.findFluObstacle(station);
				obstacleStarted(obstacle);
				flus.add(obstacle);
				context.getNewsFlashController().showNewsFlash("FreshersFluDistruption.png");
			}
		}
	}

	public void spreadFlu(){
		//This method either spreads or kills existing flu, based on the random number generated
		int rand;

		//Has two lists of flus to add and remove as otherwise there is concurrent list modification which should be avoided as much as possible as it can cause unexpected behaviour
		ArrayList<Obstacle> flusToAdd = new ArrayList<Obstacle>();
		ArrayList<Obstacle> flusToRemove = new ArrayList<Obstacle>();

		for (int i = 0; i<flus.size(); i++){
			rand = MathUtils.random(2);
			if(rand==0){
				//Kills the flu
				Obstacle obstacle = flus.get(i);
				obstacleEnded(obstacle);
				flusToRemove.add(obstacle);
			}else{
				rand = MathUtils.random(2);
				if(rand==0){
					//Generates a flu at a random station connected to the current station with the flu
					Obstacle obstacle = flus.get(i);
					Station station = obstacle.getStation();
					int randStation = MathUtils.random(map.getConnectedStations(station, null).size()-1);
					Station station1 = map.getConnectedStations(station, null).get(randStation);
					Obstacle newObstacle = obstacleManager.findFluObstacle(station1);
					obstacleStarted(newObstacle);
					flusToAdd.add(newObstacle);
				}
			}
		}
		//It was necessary to add and remove flus outside of the look as it is an index based loop, removing them inside the loop would cause the program to skip flus/check the newly created flus (could cause infinite spawn of flu)
		for (Obstacle flu: flusToRemove){
			flus.remove(flu);
		}
		for (Obstacle flu: flusToAdd){
			flus.add(flu);
		}
	}

}
