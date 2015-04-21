package gameLogic.goal;

import Util.Node;
import Util.Tuple;
import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.Map;
import gameLogic.map.NodeType;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	 * @return
	 */
	public Goal generateRandomGoal(int turn) {
		Map map = Game.getInstance().getMap();
		Goal goal;
		if (turn <6)
			goal = generateRandomMorningGoal(turn);
		else if (turn < 14)
			goal = generateRandomMiddayGoal(turn);
		else goal = generateRandomEveningGoal(turn);
				
		//Check if we need to complicate the Goal with further constraints

			//Generate a set of constraints to add to the goal
			if (goal.getDestination()!=null) {
				ArrayList<Tuple<String, Object>> availableConstraints = generateExtraConstraints(getIdealRoute(goal.getOrigin(), goal.getDestination()), map.getRouteLength(getIdealRoute(goal.getOrigin(), goal.getDestination())));

				//Pick one of our available constraints and add it to the goal
				Tuple<String, Object> goalConstraint = availableConstraints.get(new Random().nextInt(availableConstraints.size()));
				availableConstraints.remove(goalConstraint);

				goal.addConstraint(resourceManager, goalConstraint.getFirst(), goalConstraint.getSecond());
			}
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
				//This break is necessary as a train can only complete a single goal and without it the game crashes due to an ieration error as the goal us renoved fron the iterated list
				break;
			}
		}
		System.out.println("Train arrived at final destination: " + train.getFinalDestination().getName());
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

	public Goal generateRandomMorningGoal(int turn) {
		Map map = Game.getInstance().getMap();
		Station origin = map.getRandomStationOfType(NodeType.COLLEGE);
		Random random = new Random();
		int i = random.nextInt(4);
		Station destination;
		if (i == 0) {
			destination = generateDestDifToOrigin(origin, NodeType.DEPARTMENT);
			return new Goal(origin, destination, null, turn, getIdealRoute(origin, destination));
		} else if (i == 1)
			return new Goal(origin, null, NodeType.SPORTS, turn, getIdealRouteForType(origin, NodeType.SPORTS));
		else if (i == 2) {
			destination = generateDestDifToOrigin(origin, NodeType.COLLEGE);
			return new Goal(origin, destination, null, turn, getIdealRoute(origin, destination));
		} else {
			destination = generateDestDifToOrigin(origin, NodeType.RANDOM);
			return new Goal(origin, destination, null, turn, getIdealRoute(origin, destination));

		}
	}

	public Goal generateRandomMiddayGoal(int turn){
		Station origin;
		Random random = new Random();
		int i =random.nextInt(4);
		if (i==0) origin = Game.getInstance().getMap().getRandomStationOfType(NodeType.DEPARTMENT);
		else if (i==1) origin = Game.getInstance().getMap().getRandomStationOfType(NodeType.SPORTS);
		else if (i==2) origin = Game.getInstance().getMap().getRandomStationOfType(NodeType.COLLEGE);
		else origin = Game.getInstance().getMap().getRandomStationOfType(NodeType.RANDOM);

		i=random.nextInt(3);
		if (i==0) {
			Station destination = generateDestDifToOrigin(origin, NodeType.COLLEGE);
			return new Goal (origin, destination,null, turn, getIdealRoute(origin, destination));
		}

		else if (i==1) { 	//go to a specific pub or to any, really
			i = random.nextInt(2);
			if (i==0) {
				Station destination = generateDestDifToOrigin(origin, NodeType.PUB);
				return new Goal (origin, destination,null, turn, getIdealRoute(origin, destination));
			}
			else return new Goal (origin, null, NodeType.PUB, turn, getIdealRouteForType(origin, NodeType.PUB));

		}
		else {
			Station destination = generateDestDifToOrigin(origin, NodeType.RANDOM);
			return new Goal (origin, destination,null, turn, getIdealRoute(origin, destination));
		}
	}

	public Goal generateRandomEveningGoal (int turn){
		Station origin = Game.getInstance().getMap().getRandomStation();
		Random random = new Random();
		int i = random.nextInt(3);
		Station destination;
		if (i==0) {
			destination = generateDestDifToOrigin(origin,NodeType.COLLEGE);
		}
		else if (i==1) {
			 destination = generateDestDifToOrigin(origin,NodeType.PUB);
		}
		else {
			destination = generateDestDifToOrigin(origin,NodeType.TAXI);
		}
		return new Goal(origin, destination, null, turn,getIdealRoute(origin, destination));
	}

	public List<Station> getIdealRoute (Station origin, Station destination) {
		Node<Station> originNode = new Node<Station>();
		originNode.setData(origin);
		ArrayList<Node<Station>> searchFringe = new ArrayList<Node<Station>>();
		searchFringe.add(originNode);

		return Game.getInstance().getMap().getIdealRoute(destination, searchFringe, Game.getInstance().getMap().getStationsList());
	}

	public List<Station>[] getIdealRouteForType(Station origin, NodeType type){
		Game.getInstance().getMap();
		ArrayList<List <Station>> ofThisType= new ArrayList<List<Station>>();
		for (int i=0; i< Game.getInstance().getMap().getStations().size();i++)
		{
			if (Game.getInstance().getMap().getStations().get(i).getType() == type)
				ofThisType.add(getIdealRoute(origin, Game.getInstance().getMap().getStations().get(i)));
		}

		return ofThisType.toArray(new List[ofThisType.size()]);
	}

	public Station generateDestDifToOrigin (Station station, NodeType type){
		Station dest = Game.getInstance().getMap().getRandomStationOfType(type);
		while (dest.getName()==station.getName())
			dest = Game.getInstance().getMap().getRandomStationOfType(type);
	return dest;

	}

	}



