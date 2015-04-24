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
            this.nodeType = nodeType.toString();
           this.destination = "null";
        }else{
            this.nodeType = "null";
           this.destination = destination;
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

    public String getNodeType() {
        return this.nodeType;
    }
}