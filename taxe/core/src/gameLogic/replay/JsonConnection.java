package gameLogic.replay;

public class JsonConnection{
    private String start;
    private String end;
    public JsonConnection(){}
    public JsonConnection(String start,String end){
        this.start = start;
        this.end = end;
    }
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonConnection))
            return false;
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