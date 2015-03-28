package gameLogic.map;
import fvs.taxe.actor.CollisionStationActor;

/**CollisionStation is a specialised type of station*/
public class CollisionStation extends Station {
	
	private CollisionStationActor actor;

	public CollisionStation(String name, IPositionable location) {
		super(name, location);
		actor = null;
	}
	
	public void setActor(CollisionStationActor actor){
		this.actor = actor;
	}
	
	public CollisionStationActor getCollisionActor(){
		return actor;
	}

	
}
