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
import gameLogic.obstacle.Obstacle;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

public class Recorder {
    //Writes to file at end of turn then resets
    private List<JsonTurn> jsonTurns;
    private List<String> jellyHistory=new ArrayList<String>();
    public Recorder(){
        //Unused but the json library requires a default constructor for reflection
    }
    public Recorder(PlayerManager pm){
      jsonTurns = new ArrayList<JsonTurn>();
        jsonTurns.add(new JsonTurn());
        pm.subscribeTurnChanged(new TurnListener() {
            @Override
            public void changed() {
                //Write to file every time turn changes
                saveReplay();

                //Adds a new element to the list every turn
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
        //Serialises the current class to a jsonString which can be deserialised automatically
        if (!Game.getInstance().getReplay()) {
            if (jsonTurns.size() > 0) {
                Json json = new Json();
                String jsonText = json.prettyPrint(this);
                //If you wish to make the save file system more complex this is where you would change it
                FileHandle file = Gdx.files.local("replay.json");

                //Writes the string to a file
                file.writeString(jsonText, false);
            }
        }
    }

    public void updateJelly(Station destination){
        //Adds a new station to the Jelly's history to be saved
        jellyHistory.add(destination.getName());
    }


    public Replay loadReplay(){
        //Creates a new recorder which is a deserialised version of the Json
        //Has to create a new recorder as you cannot use "this=" to assign this instance to equal json.fromJson
        FileHandle file = Gdx.files.local("replay.json");
        String text = file.readString();
        Json json = new Json();
        Recorder loadRecorder = json.fromJson(Recorder.class, text);

        //Passes the loadedRecorder to the Replay class
        return new Replay(loadRecorder);
    }

    public List<String> getJellyHistory() {
        return jellyHistory;
    }
}
