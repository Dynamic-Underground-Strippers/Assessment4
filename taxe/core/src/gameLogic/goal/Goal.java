package gameLogic.goal;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;
import gameLogic.Game;
import gameLogic.map.Station;
import gameLogic.replay.JsonGoal;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

/**This class is a goal that is added to a player and repeatedly (?) checked for completion.*/
public class Goal {
	
	/**The score value of the goal.*/
	private int rewardScore;
	
	/**The first location of the goal.*/
	private Station origin;
	
	/**The destination of the goal.*/
	private Station destination;
	
	/**The turn in which the goal was created.*/
	private int turnIssued;
	
	/**Whether the goal is complete or not.*/
	private boolean complete = false;
	
	/**The specific train type that must complete this goal. If this is null, no train type is required.*/
	private String trainName = null;
	
	/**The number of turns the goal must be completed in. If it is -1, then there is no requirement for completing the goal in a number of turns.*/
	private int turnCount = -1;
	
	/**The number of trains that must compelte the goal. If it is -1, then only 1 train is required to complete the goal.*/
	private int trainCount = -1;
	
	/**The maximum number of locations that a train can visit between the origin and destination. If it is -1, then the train can make any number of journeys.*/
	private int locationCount = -1;
	
	/**A station that a train must specifically avoid to complete this goal. If it is null a train can take any route.*/
	private Station exclusionStation = null;
	
	/**An array that tracks trains that have already completed this goal for the trainCount constraint.*/
	private ArrayList<Train> completedTrains;
	
	/**The number of constraints on the goal. The higher it is, the higher the score.*/
	private int constraintCount = 0;
	
	/**The ideal Route to solving this goal.*/
	private List<Station> idealRoute;
	
	/**Instantiation method also generates a score value.
	 * @param origin The first location of the goal.
	 * @param destination The destination location of the goal.
	 * @param turn The turn in which the goal was issued.
	 * @param idealRoute the idealRoute for solving this goal
	 */
	public Goal(Station origin, Station destination, int turn, List<Station> idealRoute) {
		this.origin = origin;
		this.destination = destination;
		this.turnIssued = turn;
		this.idealRoute = idealRoute;
		setRewardScore();
	}

	/**This method adds a constraint to the goal. If there is a conflict (e.g. TrainType vs. TurnCount) then the constraints are adjusted. 
	 * @param resourceManager The game ResourceManager.
	 * @param name The name of the constraint to be added.
	 * @param value The value of the constraint to be added.
	 */
	public void addConstraint(ResourceManager resourceManager, String name, Object value) {
		
		if(name.equals("trainType")) {
			//CASE train type
			trainName = String.valueOf(value);
			if(turnCount >= 0) 
			{
				turnCount = (int)Math.ceil((Integer)turnCount * resourceManager.getTrainSpeed("Bullet Train") / resourceManager.getTrainSpeed(trainName)); 
			}
		}
		else if(name.equals("turnCount")) {
			//CASE turn count constraint
			//Adjust our value for the train type if it is set
			if (trainName != null){
				value = (int)Math.ceil((Integer)value * resourceManager.getTrainSpeed("Bullet Train") / resourceManager.getTrainSpeed(trainName));
			}
			int val = (Integer)value ;
			//Ensure that our value is valid
			if(val >= 0)
			{
				turnCount = val;
			}
			else
			{
				throw new RuntimeException(val + " is not a valid turn count. Must be >= 0");
			}
		}
		else if(name.equals("trainCount")) {
			//CASE train count constraint
			int val = (Integer)value;
			//Ensure that our value is valid
			if(val >= 0)
			{
				trainCount = val;
				//If the completedTrains arraylist has not be initiated yet, initiate it
				if(completedTrains == null)
				{
					completedTrains = new ArrayList<Train>();
				}
			}
			else
			{
				throw new RuntimeException(val + " is not a valid train count. Must be >= 0");
			}
		} 
		else if(name.equals("exclusionStation")){
			exclusionStation = (Station)value;
		}
		else if(name.equals("locationCount"))
		{
			locationCount = (Integer)value;
		}
		else
		{
			throw new RuntimeException(name + " is not a valid goal constraint");
		}
		constraintCount ++;
		//Refresh the score
		setRewardScore();
	}
	
	/**This method generates a score based off the length of the ideal Route for this Goal, multiplying it higher when more constraints are added.*/
	private void setRewardScore() {
		float distance = Game.getInstance().getMap().getRouteLength(idealRoute);
		//Scale score with route distant and number of constraints
		rewardScore = (int)Math.ceil((float) ((distance / 50) * (1 + constraintCount)));
	}

	/**@return The rewards score for completing this goal.*/
	public int getRewardScore() {
		return rewardScore;
	}

	/**Checks whether the goal is complete when a train arrives at a station.
	 * @param train The train that has just arrived at a station.
	 * @return True if the goal is complete, false otherwise.
	 */
	public boolean isComplete(Train train) {
		boolean passedOrigin = false;
		boolean passedExclusion = false;
		int locationCountClone = -1;
		int originTurn = 0;
		for(Tuple<Station, Integer> history: train.getHistory()) {
			if(history.getFirst().getName().equals(origin.getName()) && history.getSecond() >= turnIssued) {
				passedOrigin = true;
				originTurn = history.getSecond();
				locationCountClone = locationCount;
			}
			else
			{
				if(locationCountClone > -1)
				{
					locationCountClone--;
				}
			}
		}
		if(exclusionStation != null && passedOrigin)
		{
			for(Tuple<Station, Integer> history: train.getHistory()) {
				if(history.getFirst().getName().equals(exclusionStation.getName()) && history.getSecond() >= originTurn) {
					passedExclusion = true;
				}
			}
		}
		if(train.getFinalDestination() == destination && passedOrigin && !passedExclusion && locationCountClone <= 1) {
			if(trainName == null || trainName.equals(train.getName())) 
			{
				//The train has completed the goal criteria. Determine whether it is a single or multiple criteria
				//And act accordingly
				if(trainCount != -1)
				{
					//Check if this a train we have already had complete this goal
					for(Train t : completedTrains)
					{
						if(t.equals(train))
						{
							return false;
						}
					}
					trainCount--;
					if(trainCount == 0)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					//No train count criteria. Return true
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**This method checks whether the goal has failed on turns. If the turnCount is less than <= 0, it returns true, otherwise false.*/
	public boolean isFailed()
	{
		//We check our turn counter. If it is active, update
		if(turnCount != -1)
		{
			turnCount = turnCount - 2;
			if(turnCount <= 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**Produces a String representation of the Goal.*/
	public String toString() {
		String trainString = "train";
		if(trainName != null) {
			trainString = trainName;
		}
		String turnString = "";
		if(turnCount != -1)
		{
			turnString = " within " + turnCount + " turns";
		}
		String trainCountString = "a ";
		if(trainCount != -1)
		{
			trainCountString = trainCount + "x ";
		}
		String exclusionString = "";
		if(exclusionStation != null)
		{
			exclusionString = " avoiding " + exclusionStation.getName() ;
		}
		String journeyString = "";
		if(locationCount != -1)
		{
			journeyString = " in less than " + locationCount + " journeys";
		}
		return "Send " + trainCountString + trainString + " from " + origin.getName() + " to " + destination.getName() + exclusionString + journeyString + turnString + " - " + rewardScore + " points";
	}

	/**Sets the complete boolean*/
	public void setComplete() {
		complete = true;
	}

	/**Returns whether the goal is complete.*/
	public boolean getComplete() {
		return complete;
	}
	
	/**Sets the origin of the goal.*/
	public Station getOrigin() {
		return this.origin;
	}
	
	/**Sets the destination of the goal.*/
	public Station getDestination() {
		return this.destination;
	}

	public List<Station> getIdealRoute() {
		return idealRoute;
	}
}