package gameLogic.goal;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GoalManagerTest extends TestCase {


    PlayerManager pm = new PlayerManager();
    ResourceManager rm = new ResourceManager();
    GoalManager gm = new GoalManager(rm);


    @Test
    public void getRandomGoalTest() throws Exception {
         for ( int i=0; i<22; i++) //check if goals are randomly generate and if their structure is according to the turn number
            {
                System.out.println(gm.generateRandomGoal(i).toString());

                }

    }

    @Test
    public void generateDestDifToOriginTest() throws Exception{
        Station station = Game.getInstance().getMap().getRandomStation();

        for (int i=0; i<10; i++) // check that the generated destination stations are different to the origin one
            assertNotSame(gm.generateDestDifToOrigin(station, station.getType()), station);
    }


    @Test
    public void goalManagerTest() throws Exception {  //test from the previous teams
        pm = new PlayerManager();
        pm.createPlayers(2);
        Player player1 = pm.getCurrentPlayer();

        Train train = new Train("Green", "", "", 100, 1, 1);

        Station station1 = new Station("station1", new Position(5, 5));
        Station station2 = new Station("station2", new Position(2, 2));
        List<Station> iR = new ArrayList<Station>();
        iR.add(station1);
        iR.add(station2);
        List<List<Station>> idealRoute = new ArrayList<List<Station>>(10);
        idealRoute.add(iR);
        Goal goal = new Goal(station1, station2, null,0, idealRoute);
        player1.addGoal(goal);
        player1.addResource(train);

        ArrayList<Station> route = new ArrayList<Station>();
        route.add(station1);
        route.add(station2);
        train.setRoute(route);

        train.addHistory(station1, 0);

        //pm.turnOver();
        //pm.turnOver();
        train.addHistory(station2, 1);

        ArrayList<String> completedStrings = gm.trainArrived(train, player1);
        assertTrue("Goal wasn't completed", goal.isComplete(train));
        assertTrue("Completed goal string not right", completedStrings.size() > 0);

    }

}



