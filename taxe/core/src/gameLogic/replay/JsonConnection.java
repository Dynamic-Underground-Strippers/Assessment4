package gameLogic.replay;

public class JsonConnection{
    private String start;
    private String end;
    public JsonConnection(){
        //This is not used but a default constructor is required for reflection
    }
    public JsonConnection(String start,String end){
        this.start = start;
        this.end = end;
    }
    public boolean equals(Object obj) {
        //Custom equals method as there was an issue comparing two JsonConnections and in some cases was not returning true when it should have been
        if (!(obj instanceof JsonConnection))
            return false;
        //Compares the start and end nodes of the two connections
        if ((((JsonConnection) obj).getStart() == this.getStart())&&(((JsonConnection) obj).getEnd() == this.getEnd()))
            return true;
        return false;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}