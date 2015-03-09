package fvs.taxe.controller;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Jelly;
import gameLogic.resource.Train;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**Controller for using routing, with GUI*/
public class JellyRouteController {

    /**The context of the Game.*/
    private Context context;

    /**The group of buttons used in the GUI for routing.*/
    private Group routingButtons = new Group();

    /**The positions selected in routing.*/
    private List<IPositionable> positions;

    /**The connections selected in routing.*/
    private List<Connection> connections;

    /**Whether or not the player is currently using routing*/
    private boolean isRouting = false;

    /**The jelly currently having a route selected*/
    private Jelly jelly;

    /**Whether or not the currently selected route is at a point where the routing can be completed*/
    private boolean canEndRouting = true;

    //changeRoute
    private boolean editingRoute = false;
    private double distance = 0;

    /**Instantiation method. Sets up a listener for when a jelly is selected. If the RouteController is routing, that station is then added to the route,
     * @param context The context of the game.
     */
    public JellyRouteController(Context context) {
        this.context = context;
        StationController.subscribeStationClick(new StationClickListener() {
            @Override
            public void clicked(Station station) {
                if (isRouting) {
                    addStationToRoute(station);
                }
            }
        });
    }

    /**This method is called when a jelly is selected for routing,
     * @param jelly The jelly to produce a route for.
     */
    public void begin(Jelly jelly) {
        //This method is called when the user wants to create a route
        this.jelly = jelly;
        this.distance =0;
        connections = new ArrayList<Connection>();

        //sets the relevant flags to show that a route is being created
        isRouting = true;
        context.getGameLogic().setState(GameState.ROUTING);

        //Creates a new list and adds the station that the jelly is currently on as the first node.
        positions = new ArrayList<IPositionable>();

        //When a jelly has been placed at a station its position is equal to that of the station that it is located.
        //When a jelly already has a route and is moving, the position of jelly is (-1,-1).
        //This is checked here as we do not wish to route the jelly from its position to (-1,-1), hence this is only done when the jelly is at a station
        if (jelly.getPosition().getX() != -1) {
            positions.add(jelly.getPosition());
        }else{
            editingRoute = true;
        }

        //Generates all the buttons necessary to complete routing
        addRoutingButtons();

        //This makes all trains except the currently routed jelly to be invisible.
        //This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
        JellyController jellyController = new JellyController(context);
        jellyController.setJelliesVisible(jelly, false);
        jelly.getActor().setVisible(true);
    }

    /**This method adds a station to the route. It's location is added, and the appropriate connection is stored.
     * @param station The station to be added.
     */
    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        if (positions.size() == 0) {
            if (editingRoute) {
                //Checks whether the train's actor is paused due to a bug with blocked trains
                if (jelly.getActor().isPaused()){
                    Station lastStation = jelly.getLastStation();
                    //Checks if a connection exists between the station the jelly is paused at and the clicked station
                    if (context.getGameLogic().getMap().doesConnectionExist(lastStation.getName(),station.getName())){
                        positions.add(station.getLocation());

                        //Sets the relevant boolean checking if the last node on the route is a junction or not
                        canEndRouting = !(station instanceof CollisionStation);
                    }else {
                        context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
                    }
                }else {
                    Station lastStation = jelly.getLastStation();
                    Station nextStation = jelly.getNextStation();
                    if (station.getName() == lastStation.getName() || nextStation.getName() == station.getName()) {
                        //If the connection exists then the station passed to the method is added to the route
                        positions.add(station.getLocation());
                        //Sets the relevant boolean checking if the last node on the route is a junction or not
                        canEndRouting = !(station instanceof CollisionStation);
                    } else {
                        context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
                    }
                }
            }else{
                positions.add(station.getLocation());
            }
        }
        else {
            //Finds the last station in the current route
            IPositionable lastPosition = positions.get(positions.size() - 1);
            Station lastStation = context.getGameLogic().getMap().getStationFromPosition(lastPosition);

            //Check whether a connection exists using the function in Map
            boolean hasConnection = context.getGameLogic().getMap().doesConnectionExist(station.getName(), lastStation.getName());

            if (!hasConnection) {
                //If the connection doesn't exist then this informs the user
                context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);

            } else {
                distance+= context.getGameLogic().getMap().getDistance(lastStation, station);
                DecimalFormat integer = new DecimalFormat("0");

                context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / jelly.getSpeed() / 2)) + " turns.", Color.BLACK);
                //If the connection exists then the station passed to the method is added to the route
                positions.add(station.getLocation());
                connections.add(context.getGameLogic().getMap().getConnection(lastStation.getName(), station.getName()));
                //Sets the relevant boolean checking if the last node on the route is a junction or not
                canEndRouting = !(station instanceof CollisionStation);
            }
        }
    }

    /**This method is called when Routing commences for a Train. It sets up buttons for cancelling and finishing the routing,*/
    private void addRoutingButtons() {
        TextButton doneRouting = new TextButton("Route Complete", context.getSkin());
        TextButton cancel = new TextButton("Cancel", context.getSkin());

        doneRouting.setPosition(TaxeGame.WIDTH - 250, TaxeGame.HEIGHT - 33);
        cancel.setPosition(TaxeGame.WIDTH - 100, TaxeGame.HEIGHT - 33);

        cancel.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                endRouting();
            }
        });

        doneRouting.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(!canEndRouting) {
                    context.getTopBarController().displayFlashMessage("Your route must end at a station", Color.RED);
                    return;
                }

                confirmed();
                endRouting();
            }
        });

        routingButtons.addActor(doneRouting);
        routingButtons.addActor(cancel);

        context.getStage().addActor(routingButtons);
    }

    /**This method is called when a Route has been finalised by the player. The route is created from the positions, and the Jelly is set along this route
     * using a TrainController.*/
    private void confirmed() {
        jelly.setRoute(context.getGameLogic().getMap().createRoute(positions));

        @SuppressWarnings("unused")
        JellyMoveController move = new JellyMoveController(context, jelly);
    }

    /**This method is called when the routing is finalised by the player or cancelled. The existing route is dropped and the RouteController is set up for the next Routing.*/
    private void endRouting() {
        context.getGameLogic().setState(GameState.NORMAL);
        routingButtons.remove();
        isRouting = false;

        JellyController jellyController = new JellyController(context);
        jellyController.setJelliesVisible(jelly, true);
        if (jelly.getPosition().getX()!=-1){
            jelly.getActor().setVisible(false);
        }

        drawRoute(Color.GRAY);
    }

    /**This method draws the currently selected Route for the player to view, using a different Color.
     * @param color The Color of the Route.
     */
    public void drawRoute(Color color) {
        if (editingRoute && (positions.size()==1)){
            Rectangle bounds = jelly.getActor().getBounds();
            TaxeGame game = context.getTaxeGame();
            game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            game.shapeRenderer.setColor(color);
            game.shapeRenderer.rectLine(bounds.getX(), bounds.getY(),
                    positions.get(0).getX(), positions.get(0).getY(),
                    StationController.CONNECTION_LINE_WIDTH);
            game.shapeRenderer.end();
        }

        for (Connection connection : connections) {
            if ((connection.isBlocked()) && (!(Game.getInstance().getState() == GameState.PLACING)) && (!(Game.getInstance().getState() == GameState.ROUTING))){
                connection.getActor().setConnectionColor(Color.RED);
            } else{
                connection.getActor().setConnectionColor(color);
            }
        }
    }
}
