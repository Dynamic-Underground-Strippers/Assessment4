package fvs.taxe;


import Util.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.TrainController;
import fvs.taxe.controller.TrainMoveController;
import fvs.taxe.dialog.DialogButtonClicked;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.replay.Replay;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class ReplayManager {

    private Context context;

    public ReplayManager(Context context){
        this.context = context;
    }

    public void setUpForReplay(Player currentPlayer, Replay.Turn replayData){

        //Add new connections
        for (Connection c : replayData.getPlacedConnections()){
            NewConnection nc = new NewConnection();
            nc.setStation1(c.getStation1());
            nc.setStation2(c.getStation2());
            nc.use(context);
            System.out.println("Added connection between: " + nc.getStation1().getName() + "," + nc.getStation2().getName());
        }

        //remove old connections
        for (Connection c : replayData.getRemovedConnections()){
            DeleteConnection dc = new DeleteConnection();
            dc.setStation1(c.getStation1());
            dc.setStation2(c.getStation2());
            dc.use(context);
            System.out.println("Deleted connection between: " + dc.getStation1().getName() + "," + dc.getStation2().getName());
        }

        //block connections
        for (Connection c : replayData.getBlockedConnections()){
            c.setBlocked(5);
        }

        //add resources to player
        for (Resource r : replayData.getGivenResources()){
            Game.getInstance().getResourceManager().addResourceToPlayer(currentPlayer,r);
        }

        //remove resources from player
        for (Integer index : replayData.getRemovedResources()){
            Game.getInstance().getResourceManager().removeResourceFromPlayerByID(currentPlayer, index);
        }

        //place trains
        for (Tuple<Integer,Station> placing : replayData.getPlacedTrains()){
            Train train = currentPlayer.getTrainByID(placing.getFirst());
            Station station = placing.getSecond();
            train.setPosition(station.getLocation());
            train.addHistory(station, Game.getInstance().getPlayerManager().getTurnNumber());

            Gdx.input.setCursorImage(null, 0, 0);

            TrainController trainController = new TrainController(context);
            TrainActor trainActor = trainController.renderTrain(train);
            trainController.setTrainsVisible(null, true);
            trainActor.setVisible(true);
            train.setActor(trainActor);
        }

        for (Tuple<Integer, ArrayList<Station>> route : replayData.getSetRoutes()){
            Train t = currentPlayer.getTrainByID(route.getFirst());
            t.setRoute(route.getSecond());
            TrainMoveController move = new TrainMoveController(context, t);
        }

        //remove goals
        for (Goal g : replayData.getRemovedGoals()){
            currentPlayer.removeGoal(g);
        }

        //add goals
        Game.getInstance().getGoalManager().updatePlayerGoals(currentPlayer); //remove completed goals etc
        if (replayData.getGivenGoal() != null) {
            currentPlayer.addGoal(replayData.getGivenGoal());
        }

        //Finished Setup
        Game.getInstance().setState(GameState.ANIMATING); //once finished setup, set into animating state to begin 1 second animation time



    }
}
