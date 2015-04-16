package gameLogic;

import Util.Tuple;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class Recorder {
    //Writes to file at end of turn then resets
    ArrayList<Train> placedTrains=new ArrayList<Train>();
    ArrayList<Train> removedTrains=new ArrayList<Train>();
    ArrayList<Connection> placedConnections = new ArrayList<Connection>();
    ArrayList<Connection> removedConnections = new ArrayList<Connection>();
    ArrayList<Tuple<Integer,List<Station>>> setRoutes = new ArrayList<Tuple<Integer, List<Station>>>();
    ArrayList<Goal> givenGoals = new ArrayList<Goal>();
    ArrayList<Goal> removedGoals = new ArrayList<Goal>();
    ArrayList<Resource> givenResources = new ArrayList<Resource>();
    ArrayList<Resource> removedResources = new ArrayList<Resource>();
    ArrayList<Connection> connectionsBlocked = new ArrayList<Connection>();

    public Recorder(PlayerManager pm){
        pm.subscribeTurnChanged(new TurnListener() {
            // only set back the interrupt so the train can move after the turn has changed (players turn ended)
            @Override
            public void changed() {
                //Write to file
                placedTrains=new ArrayList<Train>();
                removedTrains=new ArrayList<Train>();
                placedConnections = new ArrayList<Connection>();
                removedConnections = new ArrayList<Connection>();
                setRoutes = new ArrayList<Tuple<Integer, List<Station>>>();
                givenGoals = new ArrayList<Goal>();
                removedGoals = new ArrayList<Goal>();
                givenResources = new ArrayList<Resource>();
                removedResources = new ArrayList<Resource>();
                connectionsBlocked = new ArrayList<Connection>();
            }
        });
    }

    public void placeTrain(Train train){
        placedTrains.add(train);
    }

    public void removeTrain(Train train){
        if (placedTrains.contains(train)){
            placedTrains.remove(train);
        } else{
            removedTrains.add(train);
        }
    }

    public void placeConnection(Connection connection){
        if (removedConnections.contains(connection)){
            removedConnections.remove(connection);
        }else{
            placedConnections.add(connection);
        }
    }

    public void removeConnection(Connection connection){
        if (placedConnections.contains(connection)){
            placedConnections.remove(connection);
        } else{
            removedConnections.add(connection);
        }
    }

    public void addRoute(Train train){
        setRoutes.add(new Tuple<Integer,List<Station>>(train.getID(),train.getRoute()));
    }

    public void addGoal(Goal goal){
        givenGoals.add(goal);
    }

    public void removeGoal(Goal goal){
        if (givenGoals.contains(goal)) {
            givenGoals.remove(goal);
        }else{
            removedGoals.add(goal);
        }
    }

    public void addResource(Resource resource){
        givenResources.add(resource);
    }

    public void removeResource(Resource resource){
        removedResources.add(resource);
    }
}
