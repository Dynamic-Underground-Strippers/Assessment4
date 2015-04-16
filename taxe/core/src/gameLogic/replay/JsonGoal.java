package gameLogic.replay;

import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

public class JsonGoal{
    String origin;
    String destination;
    ArrayList<String> idealRoute;
    public JsonGoal(){}
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