package gameLogic.replay;

public class JsonTrain extends JsonResource{
    int id;
    public JsonTrain(){}
    public JsonTrain(int id,int index){
        super(index);
        this.id = id;
    }
}
