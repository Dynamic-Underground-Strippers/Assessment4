package gameLogic.resource;

import gameLogic.map.Connection;
import gameLogic.map.Station;


public class NewConnection extends Resource {
    
    private Station station1;
    private Station station2;

    public NewConnection() {
        this.name = "New Connection";
        //By default forTurns set to 5, possible to make this random instead
        this.forTurns = 5;
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

    @Override
    public void dispose() {

    }
}