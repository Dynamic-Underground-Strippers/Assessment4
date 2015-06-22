package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;

import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.Player;

import java.util.ArrayList;

/**Controller for updating the game with graphics for Scores.*/
public class ScoreController {
	
	/**The context of the Game.*/
	private Context context;
	
	/**Instantiation method.
	 * @param context The Context of the game.
	 */
	public ScoreController(Context context) {
        this.context = context;
    }
	
	/**This method draws the Score details, using the current Player's score taken from the Context.*/
	public void drawScoreDetails() {
		TaxeGame game = context.getTaxeGame();
		ArrayList<String> playerStrings = new ArrayList<String>();
		for (Player player:  context.getGameLogic().getPlayerManager().getAllPlayers()){
			playerStrings.add("Player " +player.getPlayerNumber() + ": " + player.getScore());
		}

        game.batch.begin();
		if (context.getGameLogic().getPlayerManager().getTurnNumber()>=22) {
			game.fontSmall.setColor(Color.WHITE);
		}else{
			game.fontSmall.setColor(Color.BLACK);
		}

        game.fontSmall.draw(game.batch, "Score:", TaxeGame.WIDTH - NotepadController.WIDTH +50.0f, (float) TaxeGame.HEIGHT - 560.0f);
		int offset = 20;
		for (String playerString: playerStrings){
			game.fontSmall.draw(game.batch, playerString, TaxeGame.WIDTH - NotepadController.WIDTH +50.0f, (float) TaxeGame.HEIGHT - 560.0f - (offset));
			offset +=20;
		}

        game.batch.end();
	}
	
	/**This method draws the final Score details, showing the Target points and Turn number to the player, generated using the Context.*/
	public void drawFinalScoreDetails() {
		TaxeGame game = context.getTaxeGame();
		Game gameLogic = context.getGameLogic();
		game.batch.begin();
		game.fontSmall.draw(game.batch, "Target: " + gameLogic.TOTAL_POINTS + " points, Turn: " + (gameLogic.getPlayerManager().getTurnNumber() + 1), (float) TaxeGame.WIDTH - NotepadController.WIDTH + 10.0f, 20.0f);
		game.batch.end();
	}
}
