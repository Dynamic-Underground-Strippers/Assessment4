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

public class JsonTurn {
    private ArrayList<JsonTrain> placedTrains=new ArrayList<JsonTrain>();
    private ArrayList<JsonConnection> placedConnections = new ArrayList<JsonConnection>();
    private ArrayList<JsonConnection> removedConnections = new ArrayList<JsonConnection>();
    private ArrayList<Object[]> setRoutes = new ArrayList<Object[]>();
    private JsonGoal givenGoal = new JsonGoal();
    private ArrayList<JsonGoal> removedGoals = new ArrayList<JsonGoal>();
    private ArrayList<JsonResource> givenResources = new ArrayList<JsonResource>();
    private ArrayList<JsonResource> removedResources = new ArrayList<JsonResource>();
    private ArrayList<JsonConnection> connectionsBlocked = new ArrayList<JsonConnection>();

    public JsonTurn(){

    }

    public void placeTrain(Train train){

        JsonTrain jsonTrain = new JsonTrain(train.getID(),train.getIndex(),train.getHistory().get(0).getFirst().getName());
        getPlacedTrains().add(jsonTrain);
    }

    public void placeConnection(Connection connection){
        JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
        if (getRemovedConnections().contains(jsonConnection)){
            getRemovedConnections().remove(jsonConnection);
        }else{
            getPlacedConnections().add(jsonConnection);
        }
    }

    public void removeConnection(Connection connection){
        JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
        if (getPlacedConnections().contains(jsonConnection)){
            getPlacedConnections().remove(jsonConnection);
        } else{
            getRemovedConnections().add(jsonConnection);
        }
    }

    public void addRoute(Train train){
        List<String> jsonRoute = new ArrayList<String>();
        for (Station station: train.getRoute()){
            jsonRoute.add(station.getName());
        }
        Object[] jsonRouteArray = new Object[jsonRoute.size()+1];
        jsonRouteArray[0] = train.getID();
        int index=1;

        for (String stationName: jsonRoute){
            jsonRouteArray[index] = stationName;
            index++;
        }

        getSetRoutes().add(jsonRouteArray);
    }

    public void addGoal(Goal goal){
        JsonGoal jsonGoal = new JsonGoal(goal.getOrigin().getName(),goal.getDestination().getName(),goal.getIdealRoute());
        givenGoal = jsonGoal;
    }

    public void removeGoal(Goal goal){
        JsonGoal jsonGoal = new JsonGoal(goal.getOrigin().getName(),goal.getDestination().getName(),goal.getIdealRoute());
       getRemovedGoals().add(jsonGoal);
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
        getGivenResources().add(jsonResource);
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
        getRemovedResources().add(jsonResource);
    }

    public void blockConnection(Connection connection){
        JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
        connectionsBlocked.add(jsonConnection);
    }

    public ArrayList<JsonTrain> getPlacedTrains() {
        return placedTrains;
    }

    public ArrayList<JsonConnection> getPlacedConnections() {
        return placedConnections;
    }

    public ArrayList<JsonConnection> getRemovedConnections() {
        return removedConnections;
    }

    public ArrayList<Object[]> getSetRoutes() {
        return setRoutes;
    }

    public JsonGoal getGivenGoal() {
        return givenGoal;
    }

    public ArrayList<JsonGoal> getRemovedGoals() {
        return removedGoals;
    }

    public ArrayList<JsonResource> getGivenResources() {
        return givenResources;
    }

    public ArrayList<JsonResource> getRemovedResources() {
        return removedResources;
    }

    public ArrayList<JsonConnection> getConnectionsBlocked() {
        return connectionsBlocked;
    }
}
