package fvs.taxe.controller;

import fvs.taxe.actor.ObstacleActor;
import fvs.taxe.actor.ParticleEffectActor;
import gameLogic.TurnListener;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleType;
import gameLogic.obstacle.Rumble;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

import Util.Tuple;

/**Controller for updating the game with graphics for obstacles.*/
public class ObstacleController {

	/**The context of the Game.*/
	private Context context;
	
	/**Hashmap of particle effects with a corresponding string name*/
	private ArrayList<ParticleEffectActor> effectActors;

	
	/**The Instantiation method sets up the particle effects and creates a listener for when an Obstacle is started so that it can update
	 * the graphics accordingly.
	 * @param context The context of the game.
	 */
	public ObstacleController(final Context context) {
		// take care of rendering of stations (only rendered on map creation, visibility changed when active)
		this.context = context;
		effectActors = new ArrayList<ParticleEffectActor>();
		context.getGameLogic().subscribeObstacleChanged(new ObstacleListener() {
			
			@Override
			public void started(Obstacle obstacle) {
				obstacle.start();
				obstacle.getStation().setObstacle(obstacle);
				// set the obstacle so its visible
				obstacle.getActor().setVisible(true);


				if (obstacle.getType() == ObstacleType.FLOOD) {
					ParticleEffect floodEffect = new ParticleEffect();
					floodEffect.load(Gdx.files.internal("effects/flood.p"), Gdx.files.internal("effects"));
					ParticleEffectActor floodActor = new ParticleEffectActor(floodEffect);
					floodActor.setPosition(obstacle.getPosition().getX() - 10, obstacle.getPosition().getY() + 50);
					floodActor.start();
					effectActors.add(floodActor);
				} else if (obstacle.getType() == ObstacleType.FLU) {
					ParticleEffect snowEffect = new ParticleEffect();
					snowEffect.load(Gdx.files.internal("effects/snow.p"), Gdx.files.internal("effects"));
					ParticleEffectActor fluActor = new ParticleEffectActor(snowEffect);
					fluActor.setPosition(obstacle.getPosition().getX() - 10, obstacle.getPosition().getY() + 50);
					fluActor.start();
					effectActors.add(fluActor);
				}
			}
			@Override
			public void ended(Obstacle obstacle) {
				obstacle.getActor().setVisible(false);
				obstacle.getStation().clearObstacle();
				obstacle.end();
			}
		});
		context.getGameLogic().getPlayerManager().subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				drawObstacleEffects();
			}
		});
	}

	/**This method draws obstacles when the map is created. It leaves the obstacles invisible as resources that can be used at any time.*/
	public void drawObstacles(){
		// needs to only be called once, on map creation
		// adds all obstacles to the stage but makes them invisible
		ArrayList<Obstacle> obstacles = context.getGameLogic().getObstacleManager().getObstacles();
		for (Obstacle obstacle: obstacles) {
			renderObstacle(obstacle, false);
		}
	}

	/**This method renders an obstacle as an Actor.
	 * @param obstacle The obstacle to be rendered.
	 * @param visible The visibility of the obstacle.
	 * @return The actor produced from rendering the obstacle.
	 */
	private ObstacleActor renderObstacle(Obstacle obstacle, boolean visible) {
		// render the obstacle's actor with the visibility given
		ObstacleActor obstacleActor = new ObstacleActor(obstacle);
		obstacleActor.setVisible(visible);
		obstacle.setActor(obstacleActor);
		context.getStage().addActor(obstacleActor);
		return obstacleActor;
	}
	
	/**This method draws the obstacle effects at their locations with their visibilities*/
	public void drawObstacleEffects() {
		for (ParticleEffectActor actor : effectActors) {
			context.getStage().addActor(actor);
		}
		effectActors.clear();
	}
}
