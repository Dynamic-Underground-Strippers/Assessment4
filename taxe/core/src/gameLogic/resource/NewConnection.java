package gameLogic.resource;

import com.badlogic.gdx.graphics.Color;
import fvs.taxe.actor.ConnectionActor;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import com.badlogic.gdx.math.Intersector;

import javax.swing.*;


public class NewConnection extends Resource {

    private Station station1;
    private Station station2;

    public NewConnection() {
        this.name = "New Connection";
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

    public boolean use(Context context){
        Game.getInstance().getMap().addConnection(station1, station2);
        Connection connection = Game.getInstance().getMap().getConnection(station1.getName(),station2.getName());

        for (Connection c : Game.getInstance().getMap().getConnections()){

            //if connection doesn't contain one of the stations involved in new connection
            //as this would register as an intersection
            if (!(c.getStation1().equals(station1)) && (!(c.getStation2().equals(station2))) &&
                    (!(c.getStation1().equals(station2))) && (!(c.getStation2().equals(station1)))) {

                if (Intersector.intersectSegments(
                        c.getStation1().getLocation().getX(),
                        c.getStation1().getLocation().getY(),
                        c.getStation2().getLocation().getX(),
                        c.getStation2().getLocation().getY(),
                        station1.getLocation().getX(),
                        station1.getLocation().getY(),
                        station2.getLocation().getX(),
                        station2.getLocation().getY(),
                        null)){
                    //if lines intersect, remove connection and return false
                    Game.getInstance().getMap().removeConnection(station1, station2);

                    JOptionPane.showMessageDialog(null, "" + c.getStation1().getName() + "," + c.getStation2().getName(), "InfoBox: ", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }
                //else continue with loop

            }

        }

        //if exit loop then no connections overlap, so draw connection and return true
        final IPositionable start = connection.getStation1().getLocation();
        final IPositionable end = connection.getStation2().getLocation();
        ConnectionActor connectionActor = new ConnectionActor(Color.GRAY, start, end, 5, context);
        connection.setActor(connectionActor);
        context.getStage().addActor(connectionActor);
        return true;
    }

    @Override
    public void dispose() {

    }
}