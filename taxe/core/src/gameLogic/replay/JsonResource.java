package gameLogic.replay;

public class JsonResource{
    private int index;
    public JsonResource(){
        //Unused but the Json library requires a default constructor for reflection
    }
    public JsonResource(int index){
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
