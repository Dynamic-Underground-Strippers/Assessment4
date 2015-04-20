package gameLogic.replay;

import gameLogic.map.Position;

public class JsonTrain extends JsonResource{
    private int id;
    private String placedStation;
    public JsonTrain(){}
    public JsonTrain(int id,int index,String placedStation){
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
