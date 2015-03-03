package fvs.taxe.controller;

import Util.InterruptableSequenceAction;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import fvs.taxe.actor.TrainActor;
import gameLogic.Player;
import gameLogic.TurnListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**Controller for moving trains.*/
public class TrainMoveController {
	/**The chance (as a decimal) of a junction failing.*/
	private static final float JUNCTION_FAILURE_CHANCE = 0.2f;
	
	/**The context of the game.*/
	private Context context;
	
	/**The train being controlled by the controller.*/
	private Train train;
	
	/**The action being applied to the train currently being controlled.*/
	private InterruptableSequenceAction action;

	/**Instantiation adds a turn listener to interrupt the train's action when a turn changes.
	 * @param context The game context.
	 * @param train The train to be controlled.
	 */
	public TrainMoveController(final Context context, final Train train) {
		this.context = context;
		this.train = train;

		context.getGameLogic().getPlayerManager().subscribeTurnChanged(new TurnListener() {
			// only set back the interrupt so the train can move after the turn has changed (players turn ended)
			@Override
			public void changed() {
				action.setInterrupt(false);
			}
		});
		
		addMoveActions();
	}

	/**This method produces an action for the train to run before moving on the screen.
	 * @return An action where the train is set to visible and off the screen.
	 */
	private RunnableAction beforeAction() {
		return new RunnableAction() {
			public void run() {
				train.getActor().setVisible(true);
				train.setPosition(new Position(-1, -1));
			}
		};
	}

	/**This method produces an action to run every time a train reaches a station on it's route.
	 * @param station The station reached.
	 * @return An action which adds the train movement to the move history and continues the journey of the train.
	 */
	private RunnableAction perStationAction(final Station station) {
		return new RunnableAction() {
			public void run() {
				train.addHistory(station.getName(), context.getGameLogic().getPlayerManager().getTurnNumber());
				System.out.println("Added to history: passed " + station.getName() + " on turn "
						+ context.getGameLogic().getPlayerManager().getTurnNumber());
				
				junctionFailure(station);
				collisions(station);
				obstacleCollision(station);
			}

		};
	}

	/**This method checks whether a train has failed upon reaching a statement using the junction failiure chance. If it has, the movement is interrupted.*/
	private void junctionFailure(Station station) {
		// calculate if a junction failure has occured- if it has, stop the train at the station for that turn
		if (station instanceof CollisionStation){
			boolean junctionFailed = MathUtils.randomBoolean(JUNCTION_FAILURE_CHANCE);
			if (junctionFailed && station != train.getRoute().get(0)) {
				action.setInterrupt(true);
				context.getSideBarController().displayObstacleMessage("Junction failed, " + train.getName() + " stopped!", Color.YELLOW);
			}
		}
	}

	/**This method produces an action for when the train has reached it's final destination.
	 * @return A runnable action that displays a message and notifies the goal manager.
	 */
	private RunnableAction afterAction() {
		return new RunnableAction() {
			public void run() {
				ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().trainArrived(train, train.getPlayer());
				for(String message : completedGoals) {
					context.getSideBarController().displayFlashMessage(message, Color.WHITE, 2);
				}
				System.out.println(train.getFinalDestination().getLocation().getX() + "," + train.getFinalDestination().getLocation().getY());
				train.setPosition(train.getFinalDestination().getLocation());
				train.getActor().setVisible(false);
				train.setFinalDestination(null);
			}
		};
	}

	/**This method uses the current's train's routes to create a set of move actions for the train.*/
	public void addMoveActions() {
		action = new InterruptableSequenceAction();
		IPositionable current = train.getPosition();
		action.addAction(beforeAction());

		for (final Station station : train.getRoute()) {
			IPositionable next = station.getLocation();
			float duration = getDistance(current, next) / train.getSpeed();
			action.addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
			
			action.addAction(perStationAction(station));
			current = next;
		}

		action.addAction(afterAction());

		// remove previous actions to be cautious
		train.getActor().clearActions();
		train.getActor().addAction(action);
	}

	/**
	 * @param a A position.
	 * @param b A second position.
	 * @return The distance between the 2 positions.
	 */
	private float getDistance(IPositionable a, IPositionable b) {
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}

	/**This method tests for collisions when a train reaches a junction. If there is a collision, both trains are destroyed.
	 * @param station The station to test.
	 */
	private void collisions(Station station) {
		//test for train collisions at Junction point
		if(!(station instanceof CollisionStation)) {
			return;
		}
		List<Train> trainsToDestroy = collidedTrains();

		if(trainsToDestroy.size() > 0) {
			for(Train trainToDestroy : trainsToDestroy) {
				trainToDestroy.getActor().remove();
				trainToDestroy.getPlayer().removeResource(trainToDestroy);
			}

			context.getSideBarController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.BLACK, Color.RED, 4);
		}
	}

	/**This method checks if the train has collided with an obstacle when it reaches a station. If it has, the train is destroyed.*/
	private void obstacleCollision(Station station) {
		// works out if the station has an obstacle active there, whether to destroy the train
		if (station.hasObstacle() && MathUtils.randomBoolean(station.getObstacle().getDestructionChance())){
			train.getActor().remove();
			train.getPlayer().removeResource(train);
			context.getSideBarController().displayFlashMessage("Your train was hit by a natural disaster...", Color.BLACK, Color.RED, 4);
		}
	}

	/**This method returns the list of trains that the train has collided with at a junction.
	 * @return A list of trains that the current train collided with.
	 */
	private List<Train> collidedTrains() {
		List<Train> trainsToDestroy = new ArrayList<Train>();

		for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
			for(Resource resource : player.getResources()) {
				if(resource instanceof Train) {
					Train otherTrain = (Train) resource;
					if(otherTrain.getActor() == null) continue;
					if(otherTrain == train) continue;

					if(train.getActor().getBounds().overlaps(otherTrain.getActor().getBounds())) {
						//destroy trains that have crashed and burned
						trainsToDestroy.add(train);
						trainsToDestroy.add(otherTrain);
					}
				}
			}
		}

		return trainsToDestroy;
	}
}
