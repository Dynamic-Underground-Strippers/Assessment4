package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import fvs.taxe.TaxeGame;
import fvs.taxe.dialog.GoalClicked;
import gameLogic.Player;
import gameLogic.PlayerChangedListener;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;
import gameLogic.goal.GoalListener;

/**Controller for updating UI with goals.*/
public class GoalController {
	/**The context of the Game.*/
	private Context context;
	
	/**A group of buttons used for controlling the goals,*/
	private Group goalButtons = new Group();

	/**The instantation method sets up listeners for Goal changes and Player changes so that it can update the UI accordingly,
	 * @param context The context of the game.
	 */
	public GoalController(Context context) {
		this.context = context;
		
		context.getGameLogic().getGoalManager().subscribeGoalFinished(new GoalListener() {
			@Override
			public void finished(Goal goal) {
				// if a goal has completed, change the display of goals
				drawCurrentPlayerGoals();
			}
		});
		
		context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
			@Override
			public void changed() {
				drawCurrentPlayerGoals();
			}
		});
	}
	
	/**This method draws the current player's goals in the game UI.*/
	public void drawCurrentPlayerGoals() {
		goalButtons.remove();
		goalButtons.clear();

		drawHeaderText();

		float top = (float) TaxeGame.HEIGHT;
		float x = TaxeGame.WIDTH - NotepadController.WIDTH + 10.0f;
		float y = top -65.0f;

		PlayerManager pm = context.getGameLogic().getPlayerManager();
		Player currentPlayer = pm.getCurrentPlayer();

		for (Goal goal : currentPlayer.getGoals()) {
			if(goal.getComplete()) {
				continue;
			}
			y-=65;
			Label label  = new Label (goal.toString(), context.getSkin());
			label.setPosition(x, y);

			label.setWidth(NotepadController.WIDTH - 20);
			label.setWrap(true);
			label.addListener(new GoalClicked(goal));
			goalButtons.addActor(label);
		}

		context.getStage().addActor(goalButtons);
	}

	/**This method draws the header text (e.g. the current Player) for the goals.*/
	public void drawHeaderText() {
		TaxeGame game = context.getTaxeGame();
		float top = (float) TaxeGame.HEIGHT;
		float x = TaxeGame.WIDTH - NotepadController.WIDTH + 10.0f;
		float y = top -50.0f;

		game.batch.begin();
		game.fontSmall.setColor(Color.BLACK);
		game.fontSmall.draw(game.batch, playerGoalHeader(), x, y);
		game.batch.end();
	}

	/**This method generates a string for the Goal header.
	 * @return A string consisting of "Player " + the player number + " Goals:".
	 */
	private String playerGoalHeader() {
		return "Player " + context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber() + " Goals:";
	}
}
