package test;

import java.util.ArrayList;
import java.util.List;

import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;
import gameLogic.goal.GoalManager;
import gameLogic.map.NodeType;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.NewConnection;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest extends LibGdxTest {

    @Test
    public void returnAllTrains() throws Error{
        PlayerManager pm = new PlayerManager();
        Player p = new Player(pm, 1);
        ArrayList<Train> listOfTrains = new ArrayList<Train>();

        for (int n = 0; n < 5; n++){
            Train t = new Train("","","",n,n,n);
            p.addResource(t);
            listOfTrains.add(t);
        }
        for (int n = 0; n < 5; n++){
            assertSame(listOfTrains.get(n), p.getTrains().get(n));
        }
    }

    @Test
    public void returnAllTrainsWithNonTrains() throws Error{
        PlayerManager pm = new PlayerManager();
        Player p = new Player(pm, 1);
        ArrayList<Train> listOfTrains = new ArrayList<Train>();

        for (int n = 0; n < 5; n++){
            Train t = new Train("","","",n,n,n);
            p.addResource(t);
            listOfTrains.add(t);
            p.addResource(new NewConnection());
        }

        assertTrue(p.getTrains().size() == 5);

        for (int n = 0; n < 5; n++){
            assertSame(listOfTrains.get(n), p.getTrains().get(n));
        }
    }

    @Test
    public void removeGoal() throws Error{
        PlayerManager pm = new PlayerManager();
        Player p = new Player(pm, 1);

        Station s1 = new Station("1", new Position(1,2));
        Station s2 = new Station("1", new Position(1,2));

        List<List<Station>> idealRoute = new ArrayList<List<Station>>();
        List<Station> route = new ArrayList<Station>();
        route.add(s1);
        route.add(s2);
        idealRoute.add(route);

        Goal g = new Goal(s1, s2, NodeType.COLLEGE, 0, idealRoute);

        assertTrue(p.getActiveGoals().size() == 0);

        p.addGoal(g);

        assertSame(g, p.getActiveGoals().get(0));

        p.removeGoal(g);

        assertTrue(p.getActiveGoals().size() == 0);

        p.removeGoal(g); //System will crash if test fails

    }
}
