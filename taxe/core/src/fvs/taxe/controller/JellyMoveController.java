package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import fvs.taxe.actor.JellyActor;
import gameLogic.Game;
import gameLogic.Player;
import gameLogic.TurnListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Jelly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.InterruptableSequenceAction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

/**Controller for moving trains.*/
public class JellyMoveController {
    /**The chance (as a decimal) of a junction failing.*/
    private static final float JUNCTION_FAILURE_CHANCE = 0.2f;

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
                if (!jelly.getRoute().get(0).equals(station)) {
                    jelly.getActor().setRecentlyPaused(false);
                }

                jelly.addHistory(station, context.getGameLogic().getPlayerManager().getTurnNumber());

                //Uncomment to test whether or not the jelly is correctly adding stations to its history.
/*                System.out.println("Added to history: passed " + station.getName() + " on turn "
                        + context.getGameLogic().getPlayerManager().getTurnNumber());*/

                int stationIndex = jelly.getRoute().indexOf(station); //find this station in route
                int nextIndex = stationIndex + 1;

                //This checks whether or not the jelly is at its final destination by checking whether the index is still less than the list size
                if (nextIndex < jelly.getRoute().size()) {
                    Station nextStation = jelly.getRoute().get(nextIndex);

                    //Checks whether the next connection is blocked, if so the jelly is paused, if not the jelly is unpaused.
                    if (Game.getInstance().getMap().isConnectionBlocked(station, nextStation)) {
                        jelly.getActor().setPaused(true);
                        jelly.getActor().setRecentlyPaused(false);
                    } else {
                        if (jelly.getActor().isPaused()) {
                            jelly.getActor().setPaused(false);
                            jelly.getActor().setRecentlyPaused(true);
                        }
                    }
                } else {
                    //If the jelly is at its final destination then the jelly is set to unpaused so that it does not cause issues elsewhere in the program.
                    jelly.getActor().setPaused(false);
                }


            }
        };
    }

    /**This method checks whether a jelly has failed upon reaching a statement using the junction failiure chance. If it has, the movement is interrupted.*/
    private void junctionFailure(Station station) {
        // calculate if a junction failure has occured- if it has, stop the jelly at the station for that turn
        if (station instanceof CollisionStation){
            boolean junctionFailed = MathUtils.randomBoolean(JUNCTION_FAILURE_CHANCE);
            if (junctionFailed && station != jelly.getRoute().get(0)) {
                action.setInterrupt(true);
                context.getNotepadController().displayObstacleMessage("Junction failed, " + jelly.getName() + " stopped!", Color.YELLOW);
            }
        }
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

                int ran = rand.nextInt(4);

                Station nextStation = null;

                while(nextStation==null){

                    try{
                        nextStation = Game.getInstance().getMap().getConnectedStations(jelly.getFinalDestination(), null).get(ran);
                        System.out.println("selected station: "+nextStation.getName());
                    }

                    catch (Exception e){System.out.println("bad number: "+ran);}
                    try {
                        if(Game.getInstance().getMap().isConnectionBlocked(nextStation, jelly.getFinalDestination())){
                            nextStation = null;
                        }
                    } catch (Exception e){}
                    try {
                        if(Game.getInstance().getMap().doesConnectionExist(nextStation.getName(), jelly.getFinalDestination().getName())){
                            break;
                        }
                    } catch (Exception e){}
                    if(ran==0){
                        ran = 4;
                    }else {
                        ran = ran - 1;
                    }
                }


                System.out.println("Selected " + nextStation.getName());
                index = index;
                jelly.getRoute().remove(index);
                index = index;
                jelly.getRoute().add(index, nextStation);
                jelly.setFinalDestination(nextStation);
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

    /**This method tests for collisions when a jelly reaches a junction. If there is a collision, both trains are destroyed.
     * @param station The station to test.
     */
    private void collisions(Station station) {
        //test for jelly collisions at Junction point
        if(!(station instanceof CollisionStation)) {
            return;
        }
        List<Jelly> trainsToDestroy = collidedJellys();

        if(trainsToDestroy.size() > 0) {
            for(Jelly trainToDestroy : trainsToDestroy) {
                trainToDestroy.getActor().remove();
                trainToDestroy.getPlayer().removeResource(trainToDestroy);
            }

            context.getNotepadController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.BLACK, Color.RED, 4);
        }
    }

    /**This method checks if the jelly has collided with an obstacle when it reaches a station. If it has, the jelly is destroyed.*/
    private void obstacleCollision(Station station) {
        // works out if the station has an obstacle active there, whether to destroy the train
        if (station.hasObstacle() && MathUtils.randomBoolean(station.getObstacle().getDestructionChance())){
            jelly.getActor().remove();
            jelly.getPlayer().removeResource(jelly);
            context.getNotepadController().displayFlashMessage("Your jelly was hit by a natural disaster...", Color.BLACK, Color.RED, 4);
        }
    }

    /**This method returns the list of trains that the jelly has collided with at a junction.
     * @return A list of trains that the current jelly collided with.
     */
    private List<Jelly> collidedJellys() {
        List<Jelly> trainsToDestroy = new ArrayList<Jelly>();

        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
            for(Resource resource : player.getResources()) {
                if(resource instanceof Jelly) {
                    Jelly otherJelly = (Jelly) resource;
                    if(otherJelly.getActor() == null) continue;
                    if(otherJelly == jelly) continue;

                    if(jelly.getActor().getBounds().overlaps(otherJelly.getActor().getBounds())) {
                        //destroy trains that have crashed and burned
                        trainsToDestroy.add(jelly);
                        trainsToDestroy.add(otherJelly);
                    }
                }
            }
        }

        return trainsToDestroy;
    }
}
