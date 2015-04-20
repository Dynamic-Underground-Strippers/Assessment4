package gameLogic.replay;

import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

public class JsonGoal{
    private String origin;
    private String destination;
    private ArrayList<String> idealRoute;
    public JsonGoal(){}
    public JsonGoal(String origin, String destination,List<Station> idealRoute){
        this.origin=origin;
        this.destination = destination;
        this.idealRoute = new ArrayList<String>();
        for (Station station: idealRoute){
            this.getIdealRoute().add(station.getName());
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof JsonGoal))
            return false;
        if ((((JsonGoal) obj).getOrigin() == this.getDestination())&&(((JsonGoal) obj).getDestination() == this.getDestination()))
            return true;
        return false;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public ArrayList<String> getIdealRoute() {
        return idealRoute;
    }
}