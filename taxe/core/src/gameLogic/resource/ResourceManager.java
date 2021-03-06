package gameLogic.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Random;

import Util.Tuple;
import fvs.taxe.controller.JellyController;
import fvs.taxe.controller.JellyMoveController;
import gameLogic.Game;
import gameLogic.JellyListener;
import gameLogic.Player;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

/**This class creates and stores the Trains specified from trains.json*/
public class ResourceManager {
	/** The maximum number of resources a Player can own */
    public final int CONFIG_MAX_RESOURCES = 7;

	private int nextAvailableID;

    /** Random instance for generating random resources*/
    private Random random = new Random();

	private Jelly jelly;

	private int j = 0;

	private ArrayList<JellyListener> jellyListener = new ArrayList<JellyListener>();
    
    /** List of pairs of train names and the trains associated speed*/
    private ArrayList<Tuple<String, Integer>> trains;
    
    /** Constructor to initialise trains */
    public ResourceManager() {
    	nextAvailableID = -1;
		initialise();
    }
    
    /** Get the trains from trains.json and store them as name, speed pairs */
    private void initialise() {
    	JsonReader jsonReader = new JsonReader();
    	JsonValue jsonVal = jsonReader.parse(Gdx.files.local("trains.json"));
    	
    	trains = new ArrayList<Tuple<String, Integer>>();
    	for(JsonValue train = jsonVal.getChild("trains"); train != null; train = train.next()) {
    		String name = "";
    		int speed = 50;
    		for(JsonValue val  = train.child; val != null; val = val.next()) {
    			if(val.name.equalsIgnoreCase("name")) {
    				name = val.asString();
    			} else {
    				speed = val.asInt();
    			}
    		}
    		trains.add(new Tuple<String, Integer>(name, speed));
    	}
    }
    
    /** Get all of the names of the trains from trains list
     * @return ArrayList of the strings of all of the created trains
     */
    public ArrayList<String> getTrainNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Tuple<String,Integer> train : trains) {
			names.add(train.getFirst());
		}
		return names;
	}
    
    /** Get the speed that is associated with a given train's name
     * @param trainName The name of the train whose speed is wanted
     * @return The speed that is associated with the train given
     */
    public int getTrainSpeed(String trainName)
    {
    	for(Tuple<String, Integer> train : trains)
    	{
    		if(train.getFirst().equals(trainName))
    		{
    			return train.getSecond();
    		}
    	}
    	return 0;
    }
	
    /** Get all of the train name, speed pairs in the class
     * @return All of the train names and their associated speeds
     */
	public ArrayList<Tuple<String, Integer>> getTrains() {
		return trains;
	}

	/** Return one random Resource from the created Trains
	 * @return A randomly selected Train object from the list of created trains, with the speed and image set
	 * according to the properties of the train defined in trains.json
	 */
	private Resource getRandomResource() {
		//Returns a random resource


		int idx = random.nextInt(10);
		if (idx == 1) {
			//1 in 10 chance to return an obstacle
			return new NewConnection();
		} else if (idx == 2) {
			return new DeleteConnection();
		} else {
			//Otherwise randomly selects a train to give the player.
			//We decided not to use the value of idx to choose the train as this allows us to change the number of trains in the system independently of this routine
			//i.e we could have 30 trains, but still retain a 1 in 10 chance to get an engineer/skip/obstacle
			return getRandomTrain();
		}
	}

	public Train getRandomTrain() {
		//Uses a random number generator to pick a random train and return the complete train class for that train.
		int index = random.nextInt(trains.size());
		Tuple<String, Integer> train = trains.get(index);
		//next available ID is incremented so that it remains a unique identifier
		nextAvailableID++;
		return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getFirst().replaceAll(" ", "") + "Right.png", train.getSecond(),index,nextAvailableID);
	}

	public Train getTrainByIndex(int index){
		Tuple<String, Integer> train = trains.get(index);
		//next available ID is incremented so that it remains a unique identifier
		nextAvailableID++;
		return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getFirst().replaceAll(" ", "") + "Right.png", train.getSecond(),index,nextAvailableID);
	}


    /** Add one randomly generated Train to the given Player
     * @param player The player that will have a randomly generated resource added to it
     * */
    public void addRandomResourceToPlayer(Player player) {
        addResourceToPlayer(player, getRandomResource());
    }

    /** Add the given Resource to the given Player
     * @param player The player with which to add the resource
     * @param resource The resource that will be added to the player
     */
    public void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() >= CONFIG_MAX_RESOURCES || player.getSkip()) {
			return;
        }

        resource.setPlayer(player);
        player.addResource(resource);

    }


	public void createJelly(){
		//This method creates the jelly
		//Checks whether it is the first turn
		if (Game.getInstance().getPlayerManager().getTurnNumber() == 1 && j == 0) {
			this.j = 1;
			//Creates the jelly with all the relevant images
			Jelly jelly = new Jelly("Jelly", "YusuCandidate.png", "YusuCandidate.png", 50);
			Station startStation;

			//if the game is replaying read in the first station from the replay, if not choose a random one

				startStation = Game.getInstance().getMap().getRandomStation();

			jelly.setPosition(startStation.getLocation());
			jelly.addHistory(startStation,0);
			ArrayList<IPositionable> route = new ArrayList();

			//If the game is replaying, read the next station from the replay, if not choose a random one
			Station nextStation;

				nextStation = Game.getInstance().getMap().getConnectedStations(startStation, null).get(0);

			route.add(nextStation.getLocation());
			jelly.setRoute(Game.getInstance().getMap().createRoute(route));

			//Generates all the relevant controllers to allow the jelly to be fully autonomous via listeners etc
			JellyController jellycontroller = new JellyController(Game.getInstance().getContext());
			jelly.setActor(jellycontroller.renderJelly(jelly));
			jelly.getActor().setVisible(true);

			JellyMoveController moveController = new JellyMoveController(Game.getInstance().getContext(), jelly);

			this.jelly = jelly;
			Game.getInstance().setJelly(jelly);
		}
	}



}