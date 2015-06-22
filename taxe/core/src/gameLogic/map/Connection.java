package gameLogic.map;

import com.badlogic.gdx.graphics.Color;
import fvs.taxe.actor.ConnectionActor;

/**A connection describes the link between 2 stations.*/
public class Connection {
	/**The first station of the connection.*/
	private Station station1;
	
	/**The second station of the connection.*/
	private Station station2;
	
	/**The actor that represents this connection.*/
	private ConnectionActor actor;

	/**The number of turns blocked for -- 0 indicates unblocked*/
	private int blocked;
	
	/**Instantiation method.
	 * @param station1 The first station for the connection.
	 * @param station2 The second station for the connection.
	 */
	public Connection(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
		blocked = 0;
	}
	
	/**@return The first station used in this connection.*/
	public Station getStation1() {
		return this.station1;
	}

	/**@return The second station used in this connection.*/
	public Station getStation2() {
		return this.station2;
	}
	
	/**This method sets the actor that represents the connection.
	 * @param actor The new actor.
	 */
	public void setActor(ConnectionActor actor){
		this.actor = actor;
	}
	
	/**@return The actor that represents this connection.*/
	public ConnectionActor getActor(){
		return this.actor;
	}

	public boolean isBlocked() {
		return this.blocked > 0;
	}

	public void decrementBlocked() {
		//This is run every turn to decrement the turns blocked on the connection if it is greater than 0
		if (this.blocked > 0) {
			this.blocked--;
			if (this.blocked == 0)
				this.actor.setConnectionColor(Color.OLIVE);

		}
	}

	public void setBlocked(int turns) {
		this.blocked = turns;
		this.actor.setConnectionColor(Color.RED);
	}

}
