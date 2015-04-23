package gameLogic.replay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import gameLogic.Game;
import gameLogic.PlayerManager;
import gameLogic.TurnListener;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class Recorder {
    //Writes to file at end of turn then resets
    private List<JsonTurn> jsonTurns;
    public Recorder(){

    }
    public Recorder(PlayerManager pm){
      jsonTurns = new ArrayList<JsonTurn>();
        jsonTurns.add(new JsonTurn());
        pm.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                //Write to file
                saveReplay();
                jsonTurns.add(new JsonTurn());
            }
        });
    }

    public void placeTrain(Train train){
        jsonTurns.get(jsonTurns.size()-1).placeTrain(train);
    }

    public void placeConnection(Connection connection){
        jsonTurns.get(jsonTurns.size()-1).placeConnection(connection);
    }

    public void removeConnection(Connection connection){
        jsonTurns.get(jsonTurns.size()-1).removeConnection(connection);
    }

    public void blockConnection(Connection connection){
        jsonTurns.get(jsonTurns.size()-1).blockConnection(connection);
    }
    public void addRoute(Train train){
        jsonTurns.get(jsonTurns.size()-1).addRoute(train);
    }

    public void addGoal(Goal goal){
        jsonTurns.get(jsonTurns.size()-1).addGoal(goal);
    }

    public void removeGoal(Goal goal){
        jsonTurns.get(jsonTurns.size()-1).removeGoal(goal);
    }

    public void addResource(Resource resource){
        jsonTurns.get(jsonTurns.size()-1).addResource(resource);
    }

    public void removeResource(Resource resource){
        jsonTurns.get(jsonTurns.size()-1).removeResource(resource);
    }

    public List<JsonTurn> getJsonTurns(){
        return this.jsonTurns;
    }

    public void saveReplay(){
       //Saves replay
        if (!Game.getInstance().getReplay()) {
            if (jsonTurns.size() > 0) {
                Json json = new Json();
                String jsonText = json.prettyPrint(this);
                FileHandle file = Gdx.files.local("replay.json");
                file.writeString(jsonText, false);
            }
        }
    }

    private void printContents(){
        Json json = new Json();
        System.out.println(json.prettyPrint(this));
    }

    public Replay loadReplay(){
        FileHandle file = Gdx.files.local("replay.json");
        String text = file.readString();
        Json json = new Json();
        Recorder loadRecorder = json.fromJson(Recorder.class, text);
        return new Replay(loadRecorder);
    }



}
