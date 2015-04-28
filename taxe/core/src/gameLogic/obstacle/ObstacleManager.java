package gameLogic.obstacle;

import gameLogic.map.Map;
import gameLogic.map.Station;

import java.util.ArrayList;

import Util.Tuple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/** This class creates and stores the Obstacles from the Obstacles.json files */
public class ObstacleManager {
	/** The default value for the probability of an obstacle occurring if no probability is set in Obstacles.json*/
	private static final float DEFAULT_OBSTACLE_PROBABILITY = 1f;
	
	/** List of pairs of Obstacles and their associated probabilities of occurring */
	private ArrayList<Obstacle> obstacles;
	
	/* The map that the Obstacle's stations are connected with */
	private Map map;
	
	/** Set the map accoridngly, populate the list of obstacle, probability pairings
	 * @param map The map that the Game's stations are associated with
	 */
	public ObstacleManager(Map map) {
		this.map = map;
		initialise();
	}

	/** Get all of the obstacles, their types, stations and the probabilities of them occurring from Obstacles.json*/
	private void initialise() {
		JsonReader jsonReader = new JsonReader();
		JsonValue jsonVal = jsonReader.parse(Gdx.files.local("obstacles.json"));

		obstacles = new ArrayList<Obstacle>();
		for(JsonValue jObstacle = jsonVal.getChild("obstacles"); jObstacle != null; jObstacle = jObstacle.next()) {
			String typeName = "";
			String stationName = "";
			for(JsonValue val  = jObstacle.child; val != null; val = val.next()) {
				if(val.name.equalsIgnoreCase("type")) {
					typeName = val.asString();
				} else if (val.name.equalsIgnoreCase("station")) {
					stationName = val.asString();
				}
			}
			
			Obstacle obstacle = createObstacle(typeName, stationName);
			if (obstacle != null){
				obstacles.add(obstacle);
			}
		}

		for (Station station: map.getStations()){
			Obstacle obstacle = createObstacle("flu",station.getName());
			if (obstacle != null){
				obstacles.add(obstacle);
			}
		}
	}

	/** Create the obstacle that has given type, and is located at the station associated with the given string
	 * @param typeName The string that represents the name of the type of the obstacle (ObstacleType enum)
	 * @param stationName The string that represents the station that the obstacle is associated with
	 * @return An obstacle with type given, station given if both that type and station exist, otherwise null
	 */
	private Obstacle createObstacle(String typeName, String stationName) {
		ObstacleType type = null;
		Station station = null;

		 if (typeName.equalsIgnoreCase("flood")) {
			type = ObstacleType.FLOOD;
		}else if (typeName.equalsIgnoreCase("flu")){
			 type = ObstacleType.FLU;
		 }
		
		station = map.getStationByName(stationName);
		
		if (type != null && station != null){
			return new Obstacle(type, station);
		} else {
			return null;
		}
	}

	/** Get the list of pairs of the obstacles and the probability of that obstacle occurring
	 * @return The list of pairs of obstacles and the associated probability of that obstacle occurring
	 */
	public ArrayList<Obstacle> getObstacles() {
		return this.obstacles;
	}

	public Obstacle findFluObstacle(Station station){
		for (Obstacle obstacle: obstacles){
			if (obstacle.getStation().equals(station)&&obstacle.getType()==ObstacleType.FLU){
				return obstacle;
			}
		}
		return null;
	}
}
