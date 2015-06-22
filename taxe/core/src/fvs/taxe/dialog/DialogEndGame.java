package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.MainMenuScreen;
import fvs.taxe.TaxeGame;
import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.goal.Goal;

/**This is a type of Dialogue specifically for the end of the game.*/
public class DialogEndGame extends Dialog{
	/**The Game instance.*/
	private TaxeGame game;
	
	/**The instantiation sets up the Dialogue for the end of the game.
	 * @param game The game type.
	 * @param pm The player manager containing the players.
	 * @param skin The skin used for the GUI.
	 */
	public DialogEndGame(TaxeGame game, PlayerManager pm, Skin skin) {
		super("GAME OVER", skin);
		this.game = game;
		
		int highscore = 0;
		int playernum = 0;

		for(Player player : pm.getAllPlayers()) {
			text("Player " + player.getPlayerNumber() + " scored " + player.getScore() + " points!");
			getContentTable().row();
			
			if(player.getScore() > highscore) {
				highscore = player.getScore();
				playernum = player.getPlayerNumber();
			}
		}
		if(playernum != 0) {
			text("PLAYER " + playernum + " WINS!");
		} else {
			text("It's a tie!");
		}
		

		button(" Exit ","EXIT");
	}
	
	@Override
	public Dialog show(Stage stage) {
		show(stage, null);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}
	
	@Override
	public void hide() {
		hide(null);
	}
	
	@Override
	protected void result(Object obj) {
		if(obj == "EXIT"){
			Gdx.app.exit();
		} else {
			game.setScreen(new MainMenuScreen(game));
		}
	}
}
