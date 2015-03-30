package gameLogic.event;

import gameLogic.Game;
import gameLogic.PlayerManager;

import java.util.ArrayList;
import java.util.Random;

public class EventsManager {

    /** Random instance for generating random resources*/
    private Random random = new Random();

    private PlayerManager pm;

    private ArrayList<EventType> history = new ArrayList<EventType>();

    //Array storing indices of all types of events that can occur on first event
    private final int[] first = {1,2,3,4,5,6,9};

    //Array storing indices of all types of events that can occur on second event
    private final int[] second = {1,2,3,4,5,6,9};

    //Array storing indices of all types of events that can occur on third event
    private final int[] third = {1,3,6,7,8,9};

    public EventsManager (PlayerManager playerManager){
        pm = playerManager;
    }

    public void eventAct(){
        int turn = pm.getTurnNumber();

        if (turn == 10 || turn == 20 || turn == 30) { //if event occurs at set event times of morning, afternoon, evening
            EventType type = randomEvent(turn);

            switch (type) {

                case FLOOD:
                    System.out.println("EVENT: FLOOD");
                    break;

                case GRADUATION:
                    System.out.println("EVENT: GRADUATION");
                    break;

                case FRESHERS_FLU:
                    System.out.println("EVENT: FRESHERS FLU");
                    break;

                case FAXIVAL:
                    System.out.println("EVENT: FAXIVAL");
                    break;

                case ROSES:
                    System.out.println("EVENT: ROSES");
                    break;

                case EXAMS:
                    System.out.println("EVENT: EXAMS");
                    break;

                case LIVE_AND_LOUD:
                    System.out.println("EVENT: LIVE AND LOUD");
                    break;

                case SUMMER_BALL:
                    System.out.println("EVENT: SUMMER BALL");
                    break;

                case WIND:
                    System.out.println("EVENT: WIND");
                    break;
                    
            }
        } else {
            System.out.println("Event manager incorrectly called in turn " + turn);
        }

    }

    private EventType randomEvent(int turn) {
        EventType type;
        if (turn == 10) {
            while (true) { //loop until an event type is found that hasn't already happened in this game.
                int num = first[new Random().nextInt(EventType.values().length)];
                type = EventType.values()[num];
                if (!(history.contains(type))) {
                    history.add(type);
                    return type;
                }
            }
        } else if (turn == 20) {
            while (true) { //loop until an event type is found that hasn't already happened in this game.
                int num = second[new Random().nextInt(EventType.values().length)];
                type = EventType.values()[num];
                if (!(history.contains(type))) {
                    history.add(type);
                    return type;
                }
            }
        } else if (turn == 30) {
            while (true) { //loop until an event type is found that hasn't already happened in this game.
                int num = third[new Random().nextInt(EventType.values().length)];
                type = EventType.values()[num];
                if (!(history.contains(type))) {
                    history.add(type);
                    return type;
                }
            }
        } else {

            return null;

        }

    }

}
