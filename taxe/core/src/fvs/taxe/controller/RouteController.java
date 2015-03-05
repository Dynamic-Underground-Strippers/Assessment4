package fvs.taxe.controller;

import fvs.taxe.StationClickListener;
import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.CollisionStation;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

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
        this.train = train;
        isRouting = true;
        positions = new ArrayList<IPositionable>();
        connections = new ArrayList<Connection>();
        positions.add(train.getPosition());
        context.getGameLogic().setState(GameState.ROUTING);
        addRoutingButtons();

        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, false);
        train.getActor().setVisible(true);
    }

    /**This method adds a station to the route. It's location is added, and the appropriate connection is stored.
     * @param station The station to be added.
     */
    private void addStationToRoute(Station station) {
        // the latest position chosen in the positions so far
        IPositionable lastPosition =  positions.get(positions.size() - 1);
        Station lastStation = context.getGameLogic().getMap().getStationFromPosition(lastPosition);

        boolean hasConnection = context.getGameLogic().getMap().doesConnectionExist(station.getName(), lastStation.getName());
        if(!hasConnection) {
            context.getTopBarController().displayFlashMessage("This connection doesn't exist", Color.RED);
        } else {
            positions.add(station.getLocation());
            connections.add(context.getGameLogic().getMap().getConnection(station.getName(), lastStation.getName()));
            canEndRouting = !(station instanceof CollisionStation);
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

        TrainController trainController = new TrainController(context);
        trainController.setTrainsVisible(train, true);
        train.getActor().setVisible(false);
        
        drawRoute(Color.GRAY);
    }

    /**This method draws the currently selected Route for the player to view, using a different Color.
     * @param color The Color of the Route.
     */
    public void drawRoute(Color color) {
        for (Connection connection : connections) {
            if ((connection.isBlocked()) && (!(Game.getInstance().getState() == GameState.PLACING)) && (!(Game.getInstance().getState() == GameState.ROUTING))){
                connection.getActor().setConnectionColor(Color.RED);
            } else{
                connection.getActor().setConnectionColor(color);
            }
        }
    }
}
