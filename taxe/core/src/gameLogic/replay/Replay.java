package gameLogic.replay;


import Util.Tuple;
import gameLogic.Game;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.*;

import java.util.ArrayList;

public class Replay {
    private ArrayList<Turn> turns;
    public Replay(Recorder loadedRecorder){
        for (JsonTurn turn: loadedRecorder.getJsonTurns()){
            //Declared to allow the game to convert the station names into actual objects
           Map map = Game.getInstance().getMap();

            //Declared to allow the game to load in the correct resources
            ResourceManager rm = Game.getInstance().getResourceManager();

            //Turns the information loaded from the Json into the given goal
            ArrayList<Station> idealRoute = new ArrayList<Station>();
            for (String stationName: turn.getGivenGoal().getIdealRoute()){
                idealRoute.add(map.getStationByName(stationName));
            }
            Goal givenGoal = new Goal(map.getStationByName(turn.getGivenGoal().getOrigin()),map.getStationByName(turn.getGivenGoal().getDestination()),loadedRecorder.getJsonTurns().indexOf(turn),idealRoute);

            ArrayList<Goal> removedGoals = new ArrayList<Goal>();
            //Turns the information loaded from the Json into a list of removed goals
            for (JsonGoal removedJsonGoal:turn.getRemovedGoals()){
                idealRoute = new ArrayList<Station>();
                for (String stationName: removedJsonGoal.getIdealRoute()){
                    idealRoute.add(map.getStationByName(stationName));
                }
                Goal removedGoal = new Goal(map.getStationByName(removedJsonGoal.getOrigin()),map.getStationByName(removedJsonGoal.getDestination()),0,idealRoute);
                removedGoals.add(removedGoal);
            }

            //Loads in placed trains from Json
            ArrayList<Train> placedTrains=  new ArrayList<Train>();
            for (JsonTrain jsonTrain:turn.getPlacedTrains()){
                //THIS WON'T WORK THE WAY IT IS INTENDED
                Train placedTrain = rm.getTrainByIndex(jsonTrain.getIndex());
            }

            //Reads in all of the placed connections from the Json
            ArrayList<Connection> placedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection: turn.getPlacedConnections()){
                Connection placedConnection = map.getConnection(jsonConnection.getStart(),jsonConnection.getEnd());
                placedConnections.add(placedConnection);
            }

            //Reads in all of the removed connections from the Json
            ArrayList<Connection> removedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection: turn.getRemovedConnections()){
                Connection removedConnection = map.getConnection(jsonConnection.getStart(),jsonConnection.getEnd());
                removedConnections.add(removedConnection);
            }

            //Reads in all of the routes from the Json
            ArrayList<Tuple<Integer,ArrayList<Station>>> routes = new ArrayList<Tuple<Integer,ArrayList<Station>>>();
            for (Tuple<Integer,ArrayList<String>> jsonRoute: turn.getSetRoutes()){
                ArrayList<Station> route = new ArrayList<Station>();
                for (String station : jsonRoute.getSecond()){
                    route.add(map.getStationByName(station));
                }
                routes.add(new Tuple<Integer,ArrayList<Station>>(jsonRoute.getFirst(),route));
            }

            //Reads in all of the blocked connections from the Json
            ArrayList<Connection> blockedConnections = new ArrayList<Connection>();
            for (JsonConnection jsonConnection: turn.getConnectionsBlocked()){
                Connection blockedConnection = map.getConnection(jsonConnection.getStart(),jsonConnection.getEnd());
                blockedConnections.add(blockedConnection);
            }

            //Reads in all of the given resources from the Json
            ArrayList<Resource> givenResources = new ArrayList<Resource>();
            for (JsonResource jsonResource: turn.getGivenResources()){
                if (jsonResource.getIndex()<0){
                    if (jsonResource.getIndex()==-1){
                        givenResources.add(new NewConnection());
                    } else {
                        givenResources.add(new DeleteConnection());
                    }
                }
                else{
                    givenResources.add(rm.getTrainByIndex(jsonResource.getIndex()));
                }
            }

            //Reads in all of the removed resources from the Json
            ArrayList<Resource> removedResources = new ArrayList<Resource>();
            for (JsonResource jsonResource: turn.getRemovedResources()){
                if (jsonResource.getIndex()<0){
                    if (jsonResource.getIndex()==-1){
                        removedResources.add(new NewConnection());
                    } else {
                       removedResources.add(new DeleteConnection());
                    }
                }
                else{
                    removedResources.add(rm.getTrainByIndex(jsonResource.getIndex()));
                }
            }
            //Creates a new turn data structure based on the data read in from the json
            turns.add(new Turn(givenGoal,removedGoals,placedConnections,removedConnections,blockedConnections,routes,givenResources,removedResources,placedTrains));
        }
    }

    private class Turn{
        private Goal givenGoal;
        private ArrayList<Goal> removedGoals;
        private ArrayList<Train> placedTrains;
        private ArrayList<Connection> placedConnections;
        private ArrayList<Connection> removedConnections;
        private ArrayList<Connection> blockedConnections;
        private ArrayList<Tuple<Integer,ArrayList<Station>>> setRoutes;
        private ArrayList<Resource> givenResources;
        private ArrayList<Resource> removedResources;

        public Turn(Goal givenGoal,ArrayList<Goal> removedGoals,ArrayList<Connection> placedConnections,ArrayList<Connection> removedConnections, ArrayList<Connection> blockedConnections, ArrayList<Tuple<Integer,ArrayList<Station>>> setRoutes,ArrayList<Resource> givenResources,ArrayList<Resource> removedResources,ArrayList<Train> placedTrains){
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

        public ArrayList<Resource> getRemovedResources() {
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

        public ArrayList<Train> getPlacedTrains() {
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
