package gameLogic.resource;

import Util.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import fvs.taxe.GameScreen;
import fvs.taxe.controller.JellyController;
import fvs.taxe.controller.JellyMoveController;
import fvs.taxe.controller.JellyRouteController;
import gameLogic.Game;
import gameLogic.JellyListener;
import gameLogic.Player;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**This class creates and stores the Trains specified from trains.json*/
public class ResourceManager {
	/** The maximum number of resources a Player can own */
    public final int CONFIG_MAX_RESOURCES = 7;

    /** Random instance for generating random resources*/
    private Random random = new Random();

	private Jelly jelly;

	private int j = 0;

	private ArrayList<JellyListener> jellyListener = new ArrayList<JellyListener>();
    
    /** List of pairs of train names and the trains associated speed*/
    private ArrayList<Tuple<String, Integer>> trains;
    
    /** Constructor to initialise trains */
    public ResourceManager() {
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
		return new Train(train.getFirst(), train.getFirst().replaceAll(" ", "") + ".png", train.getFirst().replaceAll(" ", "") + "Right.png", train.getSecond());
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
    private void addResourceToPlayer(Player player, Resource resource) {
        if (player.getResources().size() >= CONFIG_MAX_RESOURCES || player.getSkip()) {
			return;
        }

        resource.setPlayer(player);
        player.addResource(resource);
    }

	public void jelly(){
		if (Game.getInstance().getPlayerManager().getTurnNumber() == 1 && j == 0) {
			this.j = 1;
			Jelly jelly = new Jelly("Jelly", "GreenTrain.png", "GreenTrainRight.png", 50);
			Station randStation = Game.getInstance().getMap().getRandomStation();
			jelly.setPosition(randStation.getLocation());
			jelly.addHistory(randStation,0);
			ArrayList<IPositionable> route = new ArrayList();
			Station nextStation = Game.getInstance().getMap().getConnectedStations(randStation, null).get(0);
			route.add(nextStation.getLocation());
			//Station nextStation1 = Game.getInstance().getMap().getConnectedStations(nextStation, null).get(0);
			//route.add(nextStation1.getLocation());
			/*route.add(nextStation.getLocation());
			route.add(randStation.getLocation());
			route.add(Game.getInstance().getMap().getConnectedStations(nextStation, null).get(1).getLocation());*/
			//jelly.setFinalDestination(Game.getInstance().getMap().getConnectedStations(nextStation, null).get(1));
			jelly.setRoute(Game.getInstance().getMap().createRoute(route));

			JellyController jellycontroller = new JellyController(Game.getInstance().getContext());
			jelly.setActor(jellycontroller.renderJelly(jelly));
			jelly.getActor().setVisible(true);

			JellyMoveController moveController = new JellyMoveController(Game.getInstance().getContext(), jelly);

			this.jelly = jelly;
			//System.out.println(randStation.getName() + " " + nextStation.getName() + " " + nextStation1.getName() + " " + Game.getInstance().getMap().getConnectedStations(nextStation, null).get(1).getName());
			System.out.println("new jelly in " + randStation.getName());
		} else {
			/*System.out.println("selecting additional nodes");
			List<Station> route = this.jelly.getRoute();
			System.out.println("route loaded");
			System.out.println(route);
			int index = route.indexOf(this.jelly.getLastStation());
			System.out.println("adding after "+ this.jelly.getLastStation().getName() + " index " + index);
			Station nextStation = Game.getInstance().getMap().getConnectedStations(this.jelly.getLastStation(), null).get(0);
			this.jelly.setFinalDestination(null);
			if (nextStation == this.jelly.getNextStation() || nextStation == this.jelly.getLastStation()){
				return;
			}
			System.out.println("Selected " + nextStation.getName());
			index = index + 1;
			jelly.getRoute().add(index, nextStation);
			System.out.println("Added " + nextStation.getName() + " at index " + index);
			//JellyMoveController moveController = new JellyMoveController(Game.getInstance().getContext(), jelly);
			this.jelly.setFinalDestination(null);
*/
		}
	}

	public void newJelly(JellyListener listener){
		jellyListener.add(listener);
	}

}