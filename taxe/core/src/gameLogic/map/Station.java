package gameLogic.map;

import fvs.taxe.actor.StationActor;
import gameLogic.obstacle.Obstacle;

import java.util.ArrayList;

/**This class is used to store information about a station.*/
public class Station{
	/**The name of the station.*/
	private String name;
	
	/**The in game position of the station.*/
	private IPositionable location;
	
	/**The actor that represents the station graphically.*/
	private StationActor actor;
	
	/**The obstacle occupying the station, if any.*/
	private Obstacle obstacle;

	private NodeType type;
	private ArrayList<Station> aliases;
	/**Instantiation method.
	 * @param name The name of the station.
	 * @param location The location of the station.
	 */
	public Station(String name, IPositionable location) {
		this.name = name;
		this.location = location;
		aliases = new ArrayList<Station>();
	}

	public Station(String name, IPositionable location,NodeType type, ArrayList<Pair<String, NodeType>> aliases) {
		this.name = name;
		this.location = location;
		aliases = aliases;
		this.type = type;
	}
	
	/**@return The name of the station.*/
	public String getName() {
		return name;
	}

	/**This method sets the name of the station.
	 * @param name The new name of the station.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**@return The location of the station.*/
	public IPositionable getLocation() {
		return location;
	}
	
	/**This method sets the location of the station.
	 * @param location The new location of the station.
	 */
	public void setLocation(IPositionable location) {
		this.location = location;
	}
	
	/**This method sets the actor that graphically represents the station.
	 * @param actor The new actor of the station.
	 */
	public void setActor(StationActor actor){
		this.actor = actor;
	}
	
	/**@return The actor that represents the station.*/
	public StationActor getActor(){
		return actor;
	}

	/**This method sets an Obstacle to occupy the station.*/
	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	/**@return True if the Station has an obstacle, false otherwise.*/
	public boolean hasObstacle(){
		if (this.obstacle == null){
			return false;
		} else {
			return true;
		}
	}
	
	/**@return The obstacle occupying the station.*/
	public Obstacle getObstacle(){
		return this.obstacle;
	}
	
	/**This method removes any obstacle from the station.*/
	public void clearObstacle() {
		this.obstacle = null;
	}

	public void addAlias(Station station){this.aliases.add(station);}

	public NodeType getType () {return type;}
}

