package gameLogic;

import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
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
    
    /**The goals that this player has available to them.*/
    private List<Goal> goals;
    
    /**This player's easy goal, with 0 constraints.*/
    private Goal easyGoal;
    
    /**This player's medium goal, with 1 constraint.*/
    private Goal mediumGoal;
    
    /**This player's hard goal, with 2 constraints.*/
    private Goal hardGoal;
    
    /**The player's current score.*/
    private int score;
    
    /**This player's number, e.g. Player1, Player2.*/
    private int number;

    /**Instantiation method.
     * @param pm The PlayerManager of the Game that handles this player.
     * @param playerNumber The player number, e.g. Player 1, Player 2.
     */
    public Player(PlayerManager pm, int playerNumber) {
        goals = new ArrayList<Goal>();
        resources = new ArrayList<Resource>();
        this.pm = pm;
        number = playerNumber;
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
    public List<Resource> getActiveTrains() {
    	// get all of the players trains that are active (placed)
    	List<Resource> activeResources = new ArrayList<Resource>();
    	for (Resource resource: resources) {
    		if (resource instanceof Train) {
    			if(((Train) resource).getPosition() != null) {
    				activeResources.add(resource);
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

    /**This method adds a goal to the player's goal, checking to ensure that the maximum number of goals has not been exceeded.
     * @param goal The goal to add.
     */
    public void addGoal(Goal goal) {
    	int incompleteGoals = 0;
    	for(Goal existingGoal : goals) {
    		if(!existingGoal.getComplete()) {
    			incompleteGoals++;
    		}
    	}
        if (incompleteGoals >= GoalManager.CONFIG_MAX_PLAYER_GOALS) {
            //throw new RuntimeException("Max player goals exceeded");
        	return;
        }

        goals.add(goal);
        changed();
    }
    
    /**This method checks if the Easy Goal has expired or is complete. If it is complete, it is regenerated.
    * @param sender The GoalManager that sent the update post
    */
    private void updateEasyTierGoal(GoalManager sender)
    {
    	if(easyGoal != null)
    	{
    		if(!easyGoal.getComplete())
    		{
    			//The current Easy Goal is not complete, so bail out of the method
    			return;
    		}
    	}
    	//Generate a new Easy goal, varying the number of extra criteria
    	easyGoal = sender.generateRandomGoal(getPlayerManager().getTurnNumber(), 0);
    	addGoal(easyGoal);
    }

    /**This method checks if the Medium Goal has expired or is complete. If it is complete, it is regenerated.
    * @param sender The GoalManager that sent the update post
    */
    private void updateMediumTierGoal(GoalManager sender)
    {
    	if(mediumGoal != null)
    	{
    		if(!mediumGoal.getComplete())
    		{
    			//The current Medium Goal is not complete, so bail out of themethod
    			return;
    		}
    	}
    	//Generate a new Medium Goal
    	mediumGoal = sender.generateRandomGoal(getPlayerManager().getTurnNumber(), 1);
    	addGoal(mediumGoal);
    }

    /**This method checks if the Hard Goal has expired or is complete. If it is complete, it is regenerated.
    * @param sender The GoalManager that sent the update post
    */
    private void updateHardTierGoal(GoalManager sender)
    {
    	if(hardGoal != null)
    	{
    		if(!hardGoal.getComplete())
    		{
    			//The current Hard Goal is not complete, so bail out of themethod
    			return;
    		}
    	}
    	//Generate a new Hard Goal
    	hardGoal = sender.generateRandomGoal(getPlayerManager().getTurnNumber(), 2);
    	addGoal(hardGoal);
    }
    
    /**This method is called externally and updates all of the player's goals, clearing out any goal that has failed.
     * @param sender The GoalManager that sent the update post
     */
    public void updateGoals(GoalManager sender)
    {
    	for(Goal goal : goals)
    	{
    		if(goal.isFailed())
    		{
    			goal.setComplete();
    		}
    	}
    	updateEasyTierGoal(sender);
    	updateMediumTierGoal(sender);
    	updateHardTierGoal(sender);
    }
    
    /**This method completes a goal, giving the player the reward score and setting the goal to complete.*/
    public void completeGoal(Goal goal) {
    	addScore(goal.getRewardScore());
    	goal.setComplete();
        changed();
    }

    /**
     * Method is called whenever a property of this player changes, or one of the player's resources changes
     */
    public void changed() {
        pm.playerChanged();
        
    }

    /**Get's the player's goals.*/
    public List<Goal> getGoals() {
        return goals;
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
    
    
}
