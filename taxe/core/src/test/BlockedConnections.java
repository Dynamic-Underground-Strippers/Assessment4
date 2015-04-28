package test;

import fvs.taxe.actor.ConnectionActor;
import gameLogic.map.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.goal.GoalManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class BlockedConnections extends LibGdxTest {
    @Before
    public void setup() {/*
        Map map = new Map();

        map.addStation("1", new Position(0, 0),NodeType.COLLEGE,new ArrayList<Pair<String, NodeType>>());
        map.addStation("2", new Position(0, 0),NodeType.COLLEGE,new ArrayList<Pair<String, NodeType>>());

        map.addConnection("1","2");

        Connection c = map.getConnection("1","2");
        ConnectionActor actor = new ConnectionActor();
        c.setActor(actor);

        assertFalse(c.isBlocked());

        c.setBlocked(5);

        assertTrue(c.isBlocked());

        for (int n = 0; n < 5; n++) {
            map.decrementBlockedConnections();
        }

        assertFalse(c.isBlocked());
*/



    }

    @Test
    public void StationsTest() throws Exception {

    }
}
