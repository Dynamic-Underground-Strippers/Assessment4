package gameLogic.resource;

import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.map.Connection;
import gameLogic.map.Station;


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

    public boolean use(){
        Game.getInstance().getMap().addConnection(station1, station2);
        return true;
    }

    @Override
    public void dispose() {

    }
}