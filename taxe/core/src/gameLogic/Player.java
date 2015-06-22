package gameLogic;

import Util.Tuple;
import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

/**This class holds variables and methods for a single player.*/
public class Player {
	
	/**The game's player manager. This allows the class to access other players.*/
    private PlayerManager pm;
    
    /**The resources that this player owns.*/
    private List<Resource> resources;
    
    /**The activeGoals that this player has available to them.*/
    private List<Goal> activeGoals;

    /**The player's current score.*/
    private int score;
    
    /**This player's number, e.g. Player1, Player2.*/
    private int number;

    /**Indicates whether player is to skip the next turn.*/
    private boolean skip;

    /**Stores all messages for this player on their next turn.*/
    private ArrayList<String> messageBuffer;


    /**Instantiation method.
     * @param pm The PlayerManager of the Game that handles this player.
     * @param playerNumber The player number, e.g. Player 1, Player 2.
     */
    public Player(PlayerManager pm, int playerNumber) {
        activeGoals = new ArrayList<Goal>();
        resources = new ArrayList<Resource>();
        this.pm = pm;
        number = playerNumber;
        messageBuffer = new ArrayList<String>();
    }
    
    /**@return The Player's current score.*/
    public int getScore() {
    	return score;
    }
    
    /**This method adds a integer score to the player's score.*/
    public void addScore(int score) {
    	this.score += score;
    }

    /**@return The player's array of resources.*/
    public List<Resource> getResources() {
        return resources;
    }
    
    /**@return The player's active trains.*/
    public List<Train> getActiveTrains() {
    	// get all of the players trains that are active (placed)
    	List<Train> activeResources = new ArrayList<Train>();
    	for (Resource resource: resources) {
    		if (resource instanceof Train) {
    			if(((Train) resource).getPosition() != null) {
    				activeResources.add((Train) resource);
    			}
    		}
    	}
    	return activeResources;
    }

    /**This method adds a resource to the player's resources.*/
    public void addResource(Resource resource) {
        resources.add(resource);
        changed();
    }

    /**This method removes a resource from the player's resources.*/
    public void removeResource(Resource resource) {
        resources.remove(resource);
               resource.dispose();
        changed();
    }

    /**This method adds a goal to the player's goal, checking to ensure that the maximum number of activeGoals has not been exceeded.
     * @param goal The goal to add.
     */
    public void addGoal(Goal goal) {
        //Adds the given goal to the player
    	int incompleteGoals = 0;
    	for(Goal activeGoal : activeGoals) {
    		incompleteGoals++;

    	}
        if (incompleteGoals >= GoalManager.CONFIG_MAX_PLAYER_GOALS || this.skip) {
        	return;
        }

        activeGoals.add(goal);
        //Records the goal
        changed();
    }

    
    /**This method is called externally and updates all of the player's activeGoals, clearing out any goal that has failed.
     * @param sender The GoalManager that sent the update post
     */
    public void updateGoals(GoalManager sender)
    {
        //Updates the goals, so removes failed ones, if the game is not replaying then generates a new random goal
        ArrayList<Goal> goalsToRemove = new ArrayList<Goal>();
    	for(Goal goal : activeGoals)
    	{
    		if(goal.isFailed())
    		{
    			goalsToRemove.add(goal);
    		}
    	}

        for(Goal goal: goalsToRemove){
            activeGoals.remove(goal);
        }


            addGoal(sender.generateRandomGoal(Game.getInstance().getPlayerManager().getTurnNumber()));
        }
    
    /**This method completes a goal, giving the player the reward score and setting the goal to complete.*/
    public void completeGoal(Goal goal) {
    	addScore(goal.getRewardScore());
    	activeGoals.remove(goal);
        changed();
    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's resources changes
     */
    public void changed() {
        pm.playerChanged();
        
    }

    public void removeGoal(Goal goal){
        activeGoals.remove(goal);
        changed();
    }

    /**Get's the player's activeGoals.*/
    public List<Goal> getActiveGoals() {
        return activeGoals;
    }
    
    /**Gets the PlayerManager instance used to create this player.*/
    public PlayerManager getPlayerManager() {
    	return pm;
    }
    
    /**Returns which player this is, e.g. Player 1, player 2.*/
    public int getPlayerNumber() {
    	return number;
    }

    public List<Train> getTrains() {
        //Returns all of the player's trains
        ArrayList<Train> trains = new ArrayList<Train>();
        for (Resource resource : resources) {
            if (resource instanceof Train) {
                Train train = (Train) resource;
                trains.add(train);
            }
        }
        return trains;
    }
    public void setSkip(boolean skip){
        this.skip = skip;
    }

    public boolean getSkip(){
        return skip;
    }

    public ArrayList<String> getMessages(){
        return messageBuffer;
    }

    public void addMessageToBuffer(String message){
        messageBuffer.add(message);
    }

    public void clearBuffer(){
        messageBuffer.clear();
    }

    public Train getTrainByID(int id){
        //Returns the train that matches the id passed to the method
        List<Train> allTrains = getTrains();
        for (Train train : allTrains){
            if (train.getID() == id){
                return train;
            }
        }
        return null;
    }

}
