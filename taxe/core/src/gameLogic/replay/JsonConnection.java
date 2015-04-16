package gameLogic.replay;

public class JsonConnection{
    String start;
    String end;
    public JsonConnection(){}
    public JsonConnection(String start,String end){
        this.start = start;
        this.end = end;
    }
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonConnection))
            return false;
        if ((((JsonConnection) obj).start == this.start)&&(((JsonConnection) obj).end== this.end))
            return true;
        return false;
    }
}