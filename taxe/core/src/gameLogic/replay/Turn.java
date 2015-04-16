package gameLogic.replay;

import Util.Tuple;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class Turn{
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
