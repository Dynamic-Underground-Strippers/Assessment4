package gameLogic.resource;

import Util.Node;
import com.badlogic.gdx.graphics.Color;

import fvs.taxe.actor.ConnectionActor;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.Map;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.map.CollisionStation;

import com.badlogic.gdx.math.Intersector;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class DeleteConnection extends Resource {

    private Station station1;
    private Station station2;

    public DeleteConnection() {
        this.name = "Delete Connection";
        //By default forTurns set to 5, possible to make this random instead
        this.station1 = null;
        this.station2 = null;
    }


    public Station getStation1() {
        return station1;
    }

    public void setStation1(Station station1) {
        this.station1 = station1;
    }

    public Station getStation2() {
        return station2;
    }

    public void setStation2(Station station2) {
        this.station2 = station2;
    }

    public boolean use(Context context) {
        Map map = Game.getInstance().getMap();

        Connection tempDeleted = map.getConnection(station1.getName(), station2.getName());
        map.removeConnection(station1, station2);

        //Find the ideal solution to solving this objective
        Node<Station> originNode = new Node<Station>();
        originNode.setData(station1);
        ArrayList<Node<Station>> searchFringe = new ArrayList<Node<Station>>();
        searchFringe.add(originNode);
        List<Station> route = map.getIdealRoute(station2, searchFringe, map.getStationsList());

        if (route == null) {
            //if no route exists between two stations
            //stations not allowed to be isolated
            //add back in connection and return false
            map.addConnection(tempDeleted);
            return false;
        } else {
            tempDeleted.getActor().remove();
            return true;
        }

    }

    @Override
    public void dispose() {

    }
}
