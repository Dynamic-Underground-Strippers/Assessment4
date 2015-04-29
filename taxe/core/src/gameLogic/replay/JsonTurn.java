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
    //Stores all the relevant Json variables for that particular turn
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
        //Unused, but json library requires default constructor for reflection
    }

    public void placeTrain(Train train){
        //Converts the train into a JsonTrain by passing the ID, Index and placed station
        JsonTrain jsonTrain = new JsonTrain(train.getID(),train.getIndex(),train.getHistory().get(0).getFirst().getName());
        getPlacedTrains().add(jsonTrain);
    }

    public void placeConnection(Connection connection){
        //Converts the connection into a JsonConnection by pasing the two station's names
        JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
        if (getRemovedConnections().contains(jsonConnection)){
            //if this conneciton is already being removed this turn then remove the connection from removed connections, saves effort upon loading
            getRemovedConnections().remove(jsonConnection);
        }else{
            getPlacedConnections().add(jsonConnection);
        }
    }

    public void removeConnection(Connection connection){
        //Converts the connection into a JsonConnection by pasing the two station's names
        JsonConnection jsonConnection = new JsonConnection(connection.getStation1().getName(),connection.getStation2().getName());
        if (getPlacedConnections().contains(jsonConnection)){
            //if this connection is already being placed this turn then remove the connection from removed connections, saves effort upon loading
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

        //This is a very bad way of handling the jsonRouteArray
        //The ideal would have been a list of Tuple<Int,ArrayList>
        //However the Json library has issues when using two dimensional non-generics. Therefore a compromise had to be made instead
        //In this implementation the first element of the array is the ID of the train, the rest is the route
        Object[] jsonRouteArray = new Object[jsonRoute.size()+1];
        jsonRouteArray[0] = train.getID();
        int index=1;

        for (String stationName: jsonRoute){
            jsonRouteArray[index] = stationName;
            index++;
        }
        //This array is then added to the list of routes
        getSetRoutes().add(jsonRouteArray);
    }

    public void addGoal(Goal goal){
        //The goal is converted into a JsonGoal
        JsonGoal jsonGoal;

        //Checks whether or not the Goal's destination is null and hence whether to set the nodeType as null
        if (goal.getDestination()!=null) {
            jsonGoal = new JsonGoal(goal.getOrigin().getName(), goal.getDestination().getName(), null);
        }else{
            jsonGoal = new JsonGoal(goal.getOrigin().getName(), null, goal.getDestinationType());
        }
        givenGoal = jsonGoal;
    }

    public void removeGoal(Goal goal){
        //The goal is converted into a JsonGoal
        JsonGoal jsonGoal;
        if (goal.getDestination()!=null) {
            jsonGoal = new JsonGoal(goal.getOrigin().getName(), goal.getDestination().getName(), null);
        }else{
            jsonGoal = new JsonGoal(goal.getOrigin().getName(), null, goal.getDestinationType());
        }
       getRemovedGoals().add(jsonGoal);
    }

    public void addResource(Resource resource){
        JsonResource jsonResource;
        if (resource instanceof Train){
            //Index of train is between 0 and number of trains
            //Doesn't need an IDto be passed as this will be assigned upon load
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
            //Index of train is between 0 and number of trains
            //ID must be passed here as the game needs to know which train to remove
            jsonResource = new JsonTrain(((Train) resource).getID(),((Train) resource).getIndex(),"");
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
        //Converts a connection to a jsonConnection by passing the two station's names
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
