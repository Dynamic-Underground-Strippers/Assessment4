package gameLogic.replay;


import Util.Tuple;
import gameLogic.Game;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.NodeType;
import gameLogic.map.Station;
import gameLogic.resource.*;

import java.util.ArrayList;
import java.util.List;

public class Replay {
    //This class stores a list of turns, each which store what occurs on each turn
    private ArrayList<Turn> turns;
    private ArrayList<Station> jellyHistory = new ArrayList<Station>();
    public Replay(Recorder loadedRecorder) {
        turns = new ArrayList<Turn>();
        Map map = Game.getInstance().getMap();
        ResourceManager rm = Game.getInstance().getResourceManager();
        for (String station : loadedRecorder.getJellyHistory()) {
            jellyHistory.add(map.getStationByName(station));
        }

        for (JsonTurn turn : loadedRecorder.getJsonTurns()) {
            //Declared to allow the game to convert the station names into actual objects

            //Declared to allow the game to load in the correct resources


            //Turns the information loaded from the Json into the given goal
            Station origin = map.getStationByName(turn.getGivenGoal().getOrigin());
            Station destination = map.getStationByName(turn.getGivenGoal().getDestination());
            NodeType nodeType = null;
            Goal givenGoal;
            List<List<Station>> idealRoute = new ArrayList<List<Station>>();
            if (origin != null) {
                if (destination == null) {
                    nodeType = NodeType.valueOf(turn.getGivenGoal().getNodeType());
                    idealRoute = Game.getInstance().getGoalManager().getIdealRouteForType(origin, nodeType);
                } else {
                    idealRoute.add(Game.getInstance().getGoalManager().getIdealRoute(origin, destination));
                }
                givenGoal = new Goal(map.getStationByName(turn.getGivenGoal().getOrigin()), map.getStationByName(turn.getGivenGoal().getDestination()), nodeType, loadedRecorder.getJsonTurns().indexOf(turn), idealRoute);
            } else {
                givenGoal = null;
            }

            ArrayList<Goal> removedGoals = new ArrayList<Goal>();
            //Turns the information loaded from the Json into a list of removed goals
            for (JsonGoal removedJsonGoal : turn.getRemovedGoals()) {
                origin = map.getStationByName(removedJsonGoal.getOrigin());
                destination = map.getStationByName(removedJsonGoal.getDestination());

                //Check whether the nodeType is equal to "null"
                if (removedJsonGoal.getNodeType().equals("null")) {
                   nodeType = null;
                }else{
                    nodeType = NodeType.valueOf(removedJsonGoal.getNodeType());
                }

                idealRoute = new ArrayList<List<Station>>();
                //Checks whether or not it needs to retrieve multiple ideal routes or just one
                if (destination == null) {
                    idealRoute = Game.getInstance().getGoalManager().getIdealRouteForType(origin, nodeType);
                } else {
                    idealRoute.add(Game.getInstance().getGoalManager().getIdealRoute(origin, destination));
                }

                Goal removedGoal = new Goal(origin, destination, nodeType, 0, idealRoute);
                removedGoals.add(removedGoal);
            }

            //Loads in placed trains from Json
            ArrayList<Tuple<Integer, Station>> placedTrains = new ArrayList<Tuple<Integer, Station>>();
            for (JsonTrain jsonTrain : turn.getPlacedTrains()) {
                placedTrains.add(new Tuple<Integer, Station>(jsonTrain.getId(), map.getStationByName(jsonTrain.getPlacedStation())));
            }

            //Reads in all of the placed connections from the Json
            ArrayList<Connection> placedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection : turn.getPlacedConnections()) {
                Connection placedConnection = new Connection(map.getStationByName(jsonConnection.getStart()), map.getStationByName(jsonConnection.getEnd()));
                placedConnections.add(placedConnection);
            }

            //Reads in all of the removed connections from the Json
            ArrayList<Connection> removedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection : turn.getRemovedConnections()) {
                Connection removedConnection = new Connection(map.getStationByName(jsonConnection.getStart()), map.getStationByName(jsonConnection.getEnd()));
                removedConnections.add(removedConnection);
            }

            //Reads in all of the routes from the Json
            ArrayList<Tuple<Integer, ArrayList<Station>>> routes = new ArrayList<Tuple<Integer, ArrayList<Station>>>();
            for (Object[] jsonRoute : turn.getSetRoutes()) {
                ArrayList<Station> route = new ArrayList<Station>();
                for (int i = 1; i < jsonRoute.length; i++) {
                    route.add(map.getStationByName((String) jsonRoute[i]));
                }
                //Converts the object array to the Tuple<Integer,ArrayList<Station>>
                routes.add(new Tuple<Integer, ArrayList<Station>>((Integer) jsonRoute[0], route));
            }

            //Reads in all of the blocked connections from the Json
            ArrayList<Connection> blockedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection : turn.getConnectionsBlocked()) {
                Connection blockedConnection = map.getConnection(jsonConnection.getStart(), jsonConnection.getEnd());
                blockedConnections.add(blockedConnection);
            }

            //Reads in all of the given resources from the Json
            ArrayList<Resource> givenResources = new ArrayList<Resource>();
            for (JsonResource jsonResource : turn.getGivenResources()) {
                if (jsonResource.getIndex() < 0) {
                    if (jsonResource.getIndex() == -1) {
                        givenResources.add(new NewConnection());
                    } else {
                        givenResources.add(new DeleteConnection());
                    }
                } else {
                    givenResources.add(rm.getTrainByIndex(jsonResource.getIndex()));
                }
            }

            //Reads in all of the removed resources from the Json
            ArrayList<Integer> removedResources = new ArrayList<Integer>();
            for (JsonResource jsonResource : turn.getRemovedResources()) {
                if (jsonResource.getIndex() < 0) {
                    removedResources.add(jsonResource.getIndex());
                } else {
                    removedResources.add(((JsonTrain) jsonResource).getId());
                }
            }
            //Creates a new turn data structure based on the data read in from the json
            turns.add(new Turn(givenGoal, removedGoals, placedConnections, removedConnections, blockedConnections, givenResources, removedResources, placedTrains, routes));
        }
    }

    public ArrayList<Station> getJellyRoute() {
        return this.jellyHistory;

    }


    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public class Turn{
        //This class stores everything has happened in this turn, each can be accessed individually by using the getters
        private Goal givenGoal;
        private ArrayList<Goal> removedGoals;
        private ArrayList<Tuple<Integer,Station>> placedTrains;
        private ArrayList<Connection> placedConnections;
        private ArrayList<Connection> removedConnections;
        private ArrayList<Connection> blockedConnections;
        private ArrayList<Tuple<Integer,ArrayList<Station>>> setRoutes;
        private ArrayList<Resource> givenResources;
        private ArrayList<Integer> removedResources;

        public Turn(Goal givenGoal,ArrayList<Goal> removedGoals,ArrayList<Connection> placedConnections,ArrayList<Connection> removedConnections, ArrayList<Connection> blockedConnections,ArrayList<Resource> givenResources,ArrayList<Integer> removedResources,ArrayList<Tuple<Integer,Station>> placedTrains,ArrayList<Tuple<Integer,ArrayList<Station>>> setRoutes){
            this.givenGoal = givenGoal;
            this.removedGoals = removedGoals;
            this.placedConnections = placedConnections;
            this.removedConnections = removedConnections;
            this.blockedConnections = blockedConnections;
            this.setRoutes = setRoutes;
            this.givenResources = givenResources;
            this.removedResources = removedResources;
            this.placedTrains = placedTrains;
        }

        public ArrayList<Integer> getRemovedResources() {
            return removedResources;
        }

        public ArrayList<Resource> getGivenResources() {
            return givenResources;
        }

        public Goal getGivenGoal() {
            return givenGoal;
        }

        public ArrayList<Goal> getRemovedGoals() {
            return removedGoals;
        }

        public ArrayList<Tuple<Integer,Station>> getPlacedTrains() {
            return placedTrains;
        }

        public ArrayList<Connection> getPlacedConnections() {
            return placedConnections;
        }

        public ArrayList<Connection> getRemovedConnections() {
            return removedConnections;
        }

        public ArrayList<Connection> getBlockedConnections() {
            return blockedConnections;
        }

        public ArrayList<Tuple<Integer, ArrayList<Station>>> getSetRoutes() {
            return setRoutes;
        }

    }
}
