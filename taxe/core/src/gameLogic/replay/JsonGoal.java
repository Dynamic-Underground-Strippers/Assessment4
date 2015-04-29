package gameLogic.replay;

import gameLogic.map.NodeType;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

public class JsonGoal{
    private String origin;
    private String destination;
    private String nodeType;

    public JsonGoal(){}
    public JsonGoal(String origin, String destination,NodeType nodeType){
        this.origin=origin;
       if (nodeType!=null){
           //If the nodeType is not null then the destination must be null
            this.nodeType = nodeType.toString();
           //The Json library does not handle null values, instead it does not write anything to the json, therefore the string "null" must be written instead which can be interpreted later
           this.destination = "null";
        }else{
           //If the nodeType is  null then the destination must not be null
           //The Json library does not handle null values, instead it does not write anything to the json, therefore the string "null" must be written instead which can be interpreted later
            this.nodeType = "null";
           this.destination = destination;
        }

    }

    public boolean equals(Object obj) {
        //Custom equals method as there was an issue comparing two JsonGoals and in some cases was not returning true when it should have been
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

    public String getNodeType() {
        return this.nodeType;
    }
}