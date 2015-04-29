package gameLogic.replay;

import gameLogic.map.Position;

public class JsonTrain extends JsonResource{
    private int id;
    private String placedStation;
    public JsonTrain(){
        //Unused but the Json library requires a default constructor for reflection
    }
    public JsonTrain(int id,int index,String placedStation){
        //passes to the constructor of JsonResource
        super(index);
        this.id = id;
        this.placedStation = placedStation;
    }

    public int getId() {
        return id;
    }

    public String getPlacedStation() {
        return placedStation;
    }
}
