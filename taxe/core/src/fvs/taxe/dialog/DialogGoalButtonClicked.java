package fvs.taxe.dialog;

import fvs.taxe.Button;
import gameLogic.Player;
import gameLogic.goal.Goal;

public class DialogGoalButtonClicked implements ResourceDialogClickListener {
    private Player currentPlayer;
    private Goal goal;

    public DialogGoalButtonClicked(Player player, Goal goal) {
        this.currentPlayer = player;
        this.goal = goal;
    }

    @Override
    public void clicked(Button button) {
        switch (button) {
            case GOAL_DELETE:
                currentPlayer.removeGoal(goal);
                //simulate mouse exiting goal button to remove tooltips

                break;
        }
    }
}
