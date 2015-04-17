package gameLogic.replay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import gameLogic.PlayerManager;
import gameLogic.TurnListener;
import gameLogic.goal.Goal;
import gameLogic.map.Connection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;

public class Recorder {
    //Writes to file at end of turn then resets
    ArrayList<Turn> turns;
    public Recorder(){

    }
    public Recorder(PlayerManager pm){
        turns = new ArrayList<Turn>();
        turns.add(new Turn());
        pm.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                //Write to file
                turns.add(new Turn());
            }
        });
        loadReplay();
    }

    public void placeTrain(Train train){
        turns.get(turns.size()-1).placeTrain(train);
    }

    public void placeConnection(Connection connection){
        turns.get(turns.size()-1).placeConnection(connection);
    }

    public void removeConnection(Connection connection){
        turns.get(turns.size()-1).removeConnection(connection);
    }

    public void addRoute(Train train){
        turns.get(turns.size()-1).addRoute(train);
    }

    public void addGoal(Goal goal){
        turns.get(turns.size()-1).addGoal(goal);
    }

    public void removeGoal(Goal goal){
        turns.get(turns.size()-1).removeGoal(goal);
    }

    public void addResource(Resource resource){
        turns.get(turns.size()-1).addResource(resource);
    }

    public void removeResource(Resource resource){
        turns.get(turns.size()-1).removeResource(resource);
    }

    public void saveReplay(){
       //Saves replay
      if (turns.size()>0) {
            Json json = new Json();
           String jsonText = json.prettyPrint(this);
            FileHandle file = Gdx.files.local("replay.json");
            file.writeString(jsonText, false);
        }
    }

    private void printContents(){
        Json json = new Json();
        System.out.println(json.prettyPrint(this));
    }

    public void loadReplay(){
        FileHandle file = Gdx.files.local("replay.json");
        String text = file.readString();
        Json json = new Json();
        Recorder loadRecorder = json.fromJson(Recorder.class, text);
        loadRecorder.printContents();
    }



}
