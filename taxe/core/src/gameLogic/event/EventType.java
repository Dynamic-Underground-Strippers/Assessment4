package gameLogic.event;

import java.util.Random;

/** Enumerable type that represents all of the possible types that events can be */
public enum EventType {
    FLOOD, //ALL DAY - NODES AROUND LAKE BLOCKED
    GRADUATION, // MORNING/AFTERNOON - LOTS OF GOALS TO CENTRAL HALL
    FRESHERS_FLU, //ALL DAY - SWARM OF BLOCKAGES BETWEEN NODES
    FAXIVAL, //MORNING/AFTERNOON - LOTS OF GOALS TO HALIFAX
    ROSES, //MORNING/AFTERNOON - GOALS TO SPORTS PLACES
    EXAMS, //ALL DAY - LOTS OF GOALS TO LIBRARY
    LIVE_AND_LOUD, //EVENING, GOALS TO HES EAST
    SUMMER_BALL, //EVENING - GOALS TO TAXIS/BUS
    WIND, // ALL DAY - closes exposed nodes across campus

    /**
     * NUMBERS:
     * MORNING: 7
     * AFTERNOON: 7
     * EVENING: 6
     */



}
