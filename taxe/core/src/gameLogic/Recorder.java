package gameLogic;

import Util.Tuple;
import com.badlogic.gdx.utils.Json;
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
    ArrayList<Turn> turns;
    public Recorder(PlayerManager pm){
        turns = new ArrayList<Turn>();
        turns.add(new Turn());
        pm.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                //Write to file
                turns.add(new Turn());
            }
        });
    }

    public void placeTrain(Train train){
        turns.get(turns.size()-1).placeTrain(train);
    }

    public void placeConnection(Connection connection){
        turns.get(turns.size()-1).placeConnection(connection);
    }

    public void removeConnection(Connection connection){
        turns.get(turns.size()-1).removeConnection(connection);
    }

    public void addRoute(Train train){
        turns.get(turns.size()-1).addRoute(train);
    }

    public void addGoal(Goal goal){
        turns.get(turns.size()-1).addGoal(goal);
    }

    public void removeGoal(Goal goal){
        turns.get(turns.size()-1).removeGoal(goal);
    }

    public void addResource(Resource resource){
        turns.get(turns.size()-1).addResource(resource);
    }

    public void removeResource(Resource resource){
        turns.get(turns.size()-1).removeResource(resource);
    }

    public void saveReplay(){
        Json json = new Json();
        System.out.println(json.prettyPrint(turns));
    }

    private class Turn{
        ArrayList<Train> placedTrains=new ArrayList<Train>();
        ArrayList<Connection> placedConnections = new ArrayList<Connection>();
        ArrayList<Connection> removedConnections = new ArrayList<Connection>();
        ArrayList<Tuple<Integer,List<Station>>> setRoutes = new ArrayList<Tuple<Integer, List<Station>>>();
        ArrayList<Goal> givenGoals = new ArrayList<Goal>();
        ArrayList<Goal> removedGoals = new ArrayList<Goal>();
        ArrayList<Resource> givenResources = new ArrayList<Resource>();
        ArrayList<Resource> removedResources = new ArrayList<Resource>();
        ArrayList<Connection> connectionsBlocked = new ArrayList<Connection>();

        public Turn(){

        }

        public void placeTrain(Train train){
            placedTrains.add(train);
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
}
