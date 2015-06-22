package fvs.taxe.controller;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import fvs.taxe.actor.SkillBarActor;
import fvs.taxe.actor.TrainActor;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Train;

/**Controller for using routing, with GUI*/
public class RouteController {
	
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
    
    /**The train currently having a route selected*/
    private Train train;
    
    /**Whether or not the currently selected route is at a point where the routing can be completed*/
    private boolean canEndRouting = true;

    //changeRoute
    private boolean editingRoute = false;
    private double distance = 0;
    /** If view route has been selected, the index into the connections array of which connection the train is partly across */
    private int indexPartial = -1;

    /**Instantiation method. Sets up a listener for when a train is selected. If the RouteController is routing, that station is then added to the route,
     * @param context The context of the game.
     */
    public RouteController(Context context) {
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

    /**This method is called when a train is selected for routing,
     * @param train The train to produce a route for.
     */
    public void begin(Train train) {
        //This method is called when the user wants to create a route
        this.train = train;
        this.distance =0;
        connections = new ArrayList<Connection>();

        //sets the relevant flags to show that a route is being created
        isRouting = true;
        context.getGameLogic().setState(GameState.ROUTING);

        //Creates a new list and adds the station that the train is currently on as the first node.
        positions = new ArrayList<IPositionable>();

        //When a train has been placed at a station its position is equal to that of the station that it is located.
        //When a train already has a route and is moving, the position of train is (-1,-1).
        //This is checked here as we do not wish to route the train from its position to (-1,-1), hence this is only done when the train is at a station
        if (train.getPosition().getX() != -1) {
            positions.add(train.getPosition());
        }else{
            editingRoute = true;
        }

        //Generates all the buttons necessary to complete routing
        addRoutingButtons();

        //This makes all trains except the currently routed train to be invisible.
        //This makes the screen less cluttered while routing and prevents overlapping trainActors from stopping the user being able to click stations.
        try {
            Game.getInstance().getJelly().getActor().setVisible(false);
        }catch(Exception e){

        }
        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, false);
        train.getActor().setVisible(true);
    }

    /**This method adds a station to the route. It's location is added, and the appropriate connection is stored.
     * @param station The station to be added.
     */
    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        if (positions.size() == 0) {
            if (editingRoute) {
                //Checks whether the train's actor is paused due to a bug with blocked trains
                if (train.getActor().isPaused()){
                    Station lastStation = train.getLastStation();
                    //Checks if a connection exists between the station the train is paused at and the clicked station
                    if (context.getGameLogic().getMap().doesConnectionExist(lastStation.getName(),station.getName())){
                        positions.add(station.getLocation());

                        //Sets the relevant boolean checking if the last node on the route is a junction or not
                        canEndRouting = !(station instanceof CollisionStation);
                    }else {
//                        context.getNotepadController().displayFlashMessage("This connection doesn't exist", Color.RED);
                    }
                }else {
                    Station lastStation = train.getLastStation();
                    Station nextStation = train.getNextStation();
                    if (station.getName() == lastStation.getName() || nextStation.getName() == station.getName()) {
                        //If the connection exists then the station passed to the method is added to the route
                        positions.add(station.getLocation());
                        //Sets the relevant boolean checking if the last node on the route is a junction or not
                        canEndRouting = !(station instanceof CollisionStation);
                    } else {
//                        context.getNotepadController().displayFlashMessage("This connection doesn't exist", Color.RED);
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
//                context.getNotepadController().displayFlashMessage("This connection doesn't exist", Color.RED);

            } else {
                distance+= context.getGameLogic().getMap().getDistance(lastStation, station);
                DecimalFormat integer = new DecimalFormat("0");

                //context.getTopBarController().displayMessage("Total Distance: " + integer.format(distance) + ". Will take " + integer.format(Math.ceil(distance / train.getSpeed() / 2)) + " turns.", Color.BLACK);
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
        CancelActor cancelActor = new CancelActor();
        cancelActor.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                endRouting();
            }
        });
        CompleteActor completeActor = new CompleteActor();
        completeActor.addListener(new ClickListener() {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                if(!canEndRouting) {
//                    context.getNotepadController().displayFlashMessage("Your route must end at a station", Color.RED);
                    return;
                }

                confirmed();
                endRouting();
            }
        });

        routingButtons.addActor(cancelActor);
        routingButtons.addActor(completeActor);

        context.getStage().addActor(routingButtons);
    }

    /**This method is called when a Route has been finalised by the player. The route is created from the positions, and the Train is set along this route
     * using a TrainController.*/
    private void confirmed() {
        train.setRoute(context.getGameLogic().getMap().createRoute(positions));

        @SuppressWarnings("unused")
		TrainMoveController move = new TrainMoveController(context, train);
    }

    /**This method is called when the routing is finalised by the player or cancelled. The existing route is dropped and the RouteController is set up for the next Routing.*/
    private void endRouting() {
        context.getGameLogic().setState(GameState.NORMAL);
        routingButtons.remove();
        isRouting = false;
editingRoute = false;
        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, true);
        try {
            Game.getInstance().getJelly().getActor().setVisible(true);
        }catch(Exception e){

        }
        if (train.getPosition().getX()!=-1){
            train.getActor().setVisible(false);
        }

        //Resets the partial routing so that the connectionActor returns to its normal state
        if (indexPartial != -1){
            context.getGameLogic().getMap().getConnections().get(indexPartial).getActor().clearPartialPosition();
        }
        indexPartial = -1;
        drawRoute(Color.OLIVE);

    }

    /**This method draws the currently selected Route for the player to view, using a different Color.
     * @param color The Color of the Route.
     */
    public void drawRoute(Color color) {
        if (editingRoute){
            drawPartialRoute();
        }else {
            //If the route is not being edited then there is no need to draw a partial route, each connectionActor is coloured appropriately instead
            for (Connection connection : connections) {
                if ((connection.isBlocked()) && (!(Game.getInstance().getState() == GameState.PLACING)) && (!(Game.getInstance().getState() == GameState.ROUTING))) {
                    connection.getActor().setConnectionColor(Color.RED);
                } else {
                    connection.getActor().setConnectionColor(color);
                }
            }
        }
    }

    /** draws a route with a train partially on it */
    private void drawPartialRoute() {
        //This method draws a route which has a train part-way along the track
        // calculate where train is
        Station next = train.getNextStation();
        Connection partialConnection = context.getGameLogic().getMap().getConnection(next.getName(), train.getLastStation().getName());
        indexPartial  = context.getGameLogic().getMap().getConnections().indexOf(partialConnection);
        // set the connection actor to render a portion of it (from train to station)
        partialConnection.getActor().setPartialPosition(train.getActor().getX()+ TrainActor.width/2, train.getActor().getY() + TrainActor.height/2, positions.get(0));

        for (int i = connections.indexOf(partialConnection)+1; i<connections.size(); i++){
            connections.get(i).getActor().setConnectionColor(Color.BLACK);
        }
    }
    private class CompleteActor extends Actor {
        private final Texture image;

        public CompleteActor() {
            image = new Texture(Gdx.files.internal("complete.png"));
            setSize(120, 60);
            setPosition(TaxeGame.WIDTH / 2f - 140, SkillBarActor.HEIGHT);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();
            batch.begin();
            batch.draw(image, getX(), getY(), getWidth(), getHeight());
            batch.end();
            batch.begin();
        }
    }
    private class CancelActor extends Actor {
        private final Texture image;

        public CancelActor() {
            image = new Texture(Gdx.files.internal("cancel.png"));
            setSize(120, 60);
            setPosition(TaxeGame.WIDTH / 2f +20, SkillBarActor.HEIGHT);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();
            batch.begin();
            batch.draw(image, getX(), getY(), getWidth(), getHeight());
            batch.end();
            batch.begin();
        }
    }
}
