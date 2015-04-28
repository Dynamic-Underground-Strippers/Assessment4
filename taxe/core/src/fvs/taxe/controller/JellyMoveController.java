package fvs.taxe.controller;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.InterruptableSequenceAction;
import fvs.taxe.actor.JellyActor;
import gameLogic.Game;
import gameLogic.Player;
import gameLogic.TurnListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Jelly;
import gameLogic.resource.Resource;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

/**Controller for moving trains.*/
public class JellyMoveController {

    /**The context of the game.*/
    private Context context;

    /**The jelly being controlled by the controller.*/
    private Jelly jelly;

    /**The action being applied to the jelly currently being controlled.*/
    private InterruptableSequenceAction action;

    /**Instantiation adds a turn listener to interrupt the train's action when a turn changes.
     * @param context The game context.
     * @param jelly The jelly to be controlled.
     */
    public JellyMoveController(final Context context, final Jelly jelly) {
        this.context = context;
        this.jelly = jelly;

        context.getGameLogic().getPlayerManager().subscribeTurnChanged(new TurnListener() {
            // only set back the interrupt so the jelly can move after the turn has changed (players turn ended)
            @Override
            public void changed() {
                action.setInterrupt(false);
            }
        });

        addMoveActions();
    }

    /**This method produces an action for the jelly to run before moving on the screen.
     * @return An action where the jelly is set to visible and off the screen.
     */
    private RunnableAction beforeAction() {
        return new RunnableAction() {
            public void run() {
                jelly.getActor().setVisible(true);
                jelly.setPosition(new Position(-1, -1));
            }
        };
    }

    /**This method produces an action to run every time a jelly reaches a station on it's route.
     * @param station The station reached.
     * @return An action which adds the jelly movement to the move history and continues the journey of the jelly.
     */
    // this action will run every time the jelly reaches a station within a route
    private RunnableAction perStationAction(final Station station) {
        return new RunnableAction() {
            public void run() {

            }
        };
    }

    /**This method produces an action for when the jelly has reached it's final destination.
     * @return A runnable action that displays a message and notifies the goal manager.
     */
    // TODO ADD jelly stuff instead of train
    private RunnableAction afterAction() {
        return new RunnableAction() {
            public void run() {
                //ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().jellyArrived(jelly, jelly.getPlayer());
                /**for(String message : completedGoals) {
                    context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);
                }
                System.out.println(jelly.getFinalDestination().getLocation().getX() + "," + jelly.getFinalDestination().getLocation().getY());
                jelly.setPosition(jelly.getFinalDestination().getLocation());
                jelly.getActor().setVisible(false);
                jelly.setFinalDestination(null); **/

                System.out.println("selecting additional nodes");
                System.out.println("last station was: "+jelly.getLastStation().getName());
                List<Station> route = jelly.getRoute();
                //System.out.println(route);
                int index = route.indexOf(jelly.getFinalDestination());
                System.out.println("adding after "+ jelly.getFinalDestination().getName() + " index " + index);

                Random rand = new Random();

                Station nextStation = null;
                if (!Game.getInstance().getReplay()) {
                    while (nextStation == null) {
                        List<Station> connectedStations = Game.getInstance().getMap().getConnectedStations(jelly.getFinalDestination(), null);
                        nextStation = connectedStations.get(rand.nextInt(connectedStations.size()));
                        if (Game.getInstance().getMap().isConnectionBlocked(nextStation, jelly.getFinalDestination())) {
                            nextStation = null;
                        }
                    }
                }else{
                    nextStation = Game.getInstance().getReplayManager().getNextJellyDestination();
                }


                System.out.println("Selected " + nextStation.getName());
                jelly.getRoute().remove(index);
                jelly.getRoute().add(index, nextStation);
                jelly.setFinalDestination(nextStation);
                Game.getInstance().getRecorder().updateJelly(nextStation);
                System.out.println("Added " + nextStation.getName() + " at index " + index);
                addMoveActions();

            }
        };
    }

    /**This method uses the current's train's routes to create a set of move actions for the jelly.*/
    public void addMoveActions() {
        action = new InterruptableSequenceAction();
        IPositionable current = jelly.getPosition();

        //for changeRoute
        if (jelly.getPosition().getX() == -1){
            current = new Position ((int) jelly.getActor().getBounds().getX(),(int) jelly.getActor().getBounds().getY());
        }

        action.addAction(beforeAction());

        for (final Station station : jelly.getRoute()) {
            IPositionable next = station.getLocation();
            float duration = getDistance(current, next) / jelly.getSpeed();
            action.addAction(moveTo(next.getX() - JellyActor.width / 2, next.getY() - JellyActor.height / 2, duration));

            action.addAction(perStationAction(station));
            current = next;
        }

        action.addAction(afterAction());

        // remove previous actions to be cautious
        jelly.getActor().clearActions();
        jelly.getActor().addAction(action);
    }

    /**
     * @param a A position.
     * @param b A second position.
     * @return The distance between the 2 positions.
     */
    private float getDistance(IPositionable a, IPositionable b) {
        return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
    }


}
