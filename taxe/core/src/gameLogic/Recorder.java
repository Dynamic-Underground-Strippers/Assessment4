package gameLogic;

import Util.Tuple;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.resource.DeleteConnection;
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
       //Saves replay
        if (turns.size()>0) {
            Json json = new Json();
           String jsonText = json.prettyPrint(turns);
            FileHandle file = Gdx.files.local("scores.json");
            file.writeString(jsonText, false);
        }
    }

    private class JsonTrain extends JsonResource{
        int id;
        public JsonTrain(int id,int index){
            super(index);
            this.id = id;
        }
    }

    private class JsonGoal{
        String origin;
        String destination;
        ArrayList<String> idealRoute;

        public JsonGoal(String origin, String destination,List<Station> idealRoute){
            this.origin=origin;
            this.destination = destination;
            for (Station station: idealRoute){
                this.idealRoute.add(station.getName());
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof JsonGoal))
                return false;
            if ((((JsonGoal) obj).origin == this.destination)&&(((JsonGoal) obj).destination== this.destination))
                return true;
            return false;
        }
    }

    private class JsonResource{
        int index;
        public JsonResource(int index){
            this.index = index;
        }
    }

    private class JsonConnection{
        String start;
        String end;

        public JsonConnection(String start,String end){
            this.start = start;
            this.end = end;
        }
        public boolean equals(Object obj) {
            if (!(obj instanceof JsonConnection))
                return false;
            if ((((JsonConnection) obj).start == this.start)&&(((JsonConnection) obj).end== this.end))
                return true;
            return false;
        }
    }

    private class Turn{
        public ArrayList<JsonTrain> placedTrains=new ArrayList<JsonTrain>();
        ArrayList<JsonConnection> placedConnections = new ArrayList<JsonConnection>();
        ArrayList<JsonConnection> removedConnections = new ArrayList<JsonConnection>();
        ArrayList<Tuple<Integer,ArrayList<String>>> setRoutes = new ArrayList<Tuple<Integer, ArrayList<String>>>();
        ArrayList<JsonGoal> givenGoals = new ArrayList<JsonGoal>();
        ArrayList<JsonGoal> removedGoals = new ArrayList<JsonGoal>();
        ArrayList<JsonResource> givenResources = new ArrayList<JsonResource>();
        ArrayList<JsonResource> removedResources = new ArrayList<JsonResource>();
        ArrayList<JsonConnection> connectionsBlocked = new ArrayList<JsonConnection>();

        public Turn(){

        }

        public void placeTrain(Train train){
            JsonTrain jsonTrain = new JsonTrain(train.getID(),train.getIndex());
            placedTrains.add(jsonTrain);
        }

        public void placeConnection(Connection connection){
            JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
            if (removedConnections.contains(jsonConnection)){
                removedConnections.remove(jsonConnection);
            }else{
                placedConnections.add(jsonConnection);
            }
        }

        public void removeConnection(Connection connection){
            JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
            if (placedConnections.contains(jsonConnection)){
                placedConnections.remove(jsonConnection);
            } else{
                removedConnections.add(jsonConnection);
            }
        }

        public void addRoute(Train train){
            ArrayList<String> jsonRoute = new ArrayList<String>();
            for (Station station: train.getRoute()){
                jsonRoute.add(station.getName());
            }
            setRoutes.add(new Tuple<Integer,ArrayList<String>>(train.getID(),jsonRoute));
        }

        public void addGoal(Goal goal){
            JsonGoal jsonGoal = new JsonGoal(goal.getOrigin().getName(),goal.getDestination().getName(),goal.getIdealRoute());
            givenGoals.add(jsonGoal);
        }

        public void removeGoal(Goal goal){
            JsonGoal jsonGoal = new JsonGoal(goal.getOrigin().getName(),goal.getDestination().getName(),goal.getIdealRoute());
            if (givenGoals.contains(jsonGoal)) {
                givenGoals.remove(jsonGoal);
            }else{
                removedGoals.add(jsonGoal);
            }
        }

        public void addResource(Resource resource){
            JsonResource jsonResource;
            if (resource instanceof Train){
                jsonResource = new JsonResource(((Train) resource).getIndex());
            }else if (resource instanceof DeleteConnection){
                //-2 is delete connection
                jsonResource = new JsonResource(-2);
            }else{
                //-1 is new connection
                jsonResource = new JsonResource(-1);
            }
            givenResources.add(jsonResource);
        }

        public void removeResource(Resource resource){
            JsonResource jsonResource;
            if (resource instanceof Train){
                jsonResource = new JsonResource(((Train) resource).getIndex());
            }else if (resource instanceof DeleteConnection){
                //-2 is delete connection
                jsonResource = new JsonResource(-2);
            }else{
                //-1 is new connection
                jsonResource = new JsonResource(-1);
            }
            removedResources.add(jsonResource);
        }
    }
}
