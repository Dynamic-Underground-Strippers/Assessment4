package test;


import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TrainTest {
    Train train;

    @Before
    public void trainSetup() throws Exception {
        train = new Train("RedTrain", "RedTrain.png", "RedTrainRight.png", 250, 1, 1);
    }

    @Test
    public void finalDestinationTest() throws Error {
        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(6, 6));
        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);

        train.setRoute(route);
        assertTrue("Setting a train route was not succesful", train.getRoute().size() == 2);
        assertTrue("Final destination wasn't set", train.getFinalDestination() == station2);
    }

    @Test
    public void getNextTest() throws Error {
        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(6, 6));
        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);
        train.setRoute(route);

        train.setRoute(route);
        assertSame(station2, train.getNextStation());
    }

    @Test
    public void getLastTest() throws Error {
        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(6, 6));
        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);
        train.setRoute(route);

        train.setRoute(route);
        assertSame(station1, train.getLastStation());
    }


}
