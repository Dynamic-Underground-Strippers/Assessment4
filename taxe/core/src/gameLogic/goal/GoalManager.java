package gameLogic.goal;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.Node;
import Util.Tuple;

/**This class manages goals for the game.*/
public class GoalManager {
	/**The maximum number of goals a player can have.*/
	public final static int CONFIG_MAX_PLAYER_GOALS = 3;
	
	/**The ResourceManager for the Game.*/
	private ResourceManager resourceManager;
	
	/**The List of listeners that are notified when a goal is finished.*/
	private List<GoalListener> listeners;
	
	/**Instantiation method.
	 * @param resourceManager The ResourceManager for the Game.
	 */
	public GoalManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		listeners = new ArrayList<GoalListener>();
	}

	/**This method generates a random goal with extra constraints. It chooses a random origin and destination, and adds extra constraints to it.
	 * @param turn The turn in which the goal was created.
	 * @param extraConstraints The number of extra constraints to add. Can be zero.
	 * @return
	 */
	public Goal generateRandomGoal(int turn) {
		Map map = Game.getInstance().getMap();
		Station origin;
		do {
			origin = map.getRandomStation();
		} while (origin instanceof CollisionStation);
		Station destination;
		do {
			destination = map.getRandomStation();
		} while (destination == origin || destination instanceof CollisionStation);

		//Find the ideal solution to solving this objective
		Node<Station> originNode = new Node<Station>();
		originNode.setData(origin);
		ArrayList<Node<Station>> searchFringe = new ArrayList<Node<Station>>();
		searchFringe.add(originNode);
		List<Station> idealRoute = map.getIdealRoute(destination, searchFringe, map.getStationsList());
		
		Goal goal = new Goal(origin, destination, turn, idealRoute);
				
		//Check if we need to complicate the Goal with further constraints

			//Generate a set of constraints to add to the goal
			ArrayList<Tuple<String, Object>> availableConstraints = generateExtraConstraints(idealRoute, map.getRouteLength(idealRoute));

				//Pick one of our available constraints and add it to the goal
				Tuple<String, Object> goalConstraint = availableConstraints.get(new Random().nextInt(availableConstraints.size()));
				availableConstraints.remove(goalConstraint);

				goal.addConstraint(resourceManager, goalConstraint.getFirst(), goalConstraint.getSecond());

		return goal;
	}
	
	/**This method generates a set of extra constraints using the ideal Route.
	 * @param idealRoute The ideal route for which the constraints should be generated.
	 * @param routeLength The length of the ideal Route.
	 * @return The List of Constraints generated.
	 */
	private ArrayList<Tuple<String, Object>> generateExtraConstraints(List<Station> idealRoute, float routeLength) {
		ArrayList<Tuple<String, Object>> list =  new ArrayList<Tuple<String, Object>>();
		//Add a constraint based on number of turns, based on the time taken for a Bullet Train to complete the route of Param routeLength
		list.add(new Tuple<String, Object>("turnCount", (int)Math.ceil((routeLength / resourceManager.getTrainSpeed("Bullet Train"))/2)));
		//Add a constraint based on the train type, picking a random train type
		list.add(new Tuple<String, Object>("trainType", resourceManager.getTrainNames().get(new Random().nextInt(resourceManager.getTrainNames().size()))));
		//If the route is not linear between 2 points, then we can add an exclusion constraint from the idealRoute
		List<Station> removeAbleStations = Game.getInstance().getMap().getEditableRoute(idealRoute);
		if(removeAbleStations.size() > 0)
		{
			if(removeAbleStations.size() == 1)
			{
				list.add(new Tuple<String, Object>("exclusionStation", removeAbleStations.get(0)));
				
			}
			else
			{
				list.add(new Tuple<String, Object>("exclusionStation", removeAbleStations.get(new Random().nextInt(removeAbleStations.size()))));
			}
		}
		//Add a constraint of the maximum number of journeys a train can make to get between the 2 locations, the length of the ideal route + 1 (since the ideal route contains the origin)
		return list;
	}
	
	/**This method is called when the turn changes. The current player is updated.
	 * @param player The player to updated.
	 */
	public void updatePlayerGoals(Player player)
	{
		player.updateGoals(this);
	}

	/**This method is called when a train reaches a station. The current player's goals are checked for completion on this event.
	 * @param train The train that has triggered the event.
	 * @param player The current player.
	 * @return A list of Strings to display if the player completed any goals.
	 */
	public ArrayList<String> trainArrived(Train train, Player player) {
		ArrayList<String> completedString = new ArrayList<String>();
		for(Goal goal:player.getActiveGoals()) {
			//Check if a goal was completed by the train arrival
			if(goal.isComplete(train)) {
				player.completeGoal(goal);
				player.removeResource(train);
				completedString.add("Player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
				goalFinished(goal);
			}
		}
		System.out.println("Train arrived to final destination: " + train.getFinalDestination().getName());
		return completedString;
	}

	/** Adds a new GoalListener that is notified when a goal is finished.*/
	public void subscribeGoalFinished(GoalListener goalFinishedListener) {
		listeners.add(goalFinishedListener);
	}
	
	/**This method is called when a goal is finished. All of the GoalListeners are notified.*/
	public void goalFinished(Goal goal) {
		for (GoalListener listener : listeners){
			listener.finished(goal);
		}
	}
}
