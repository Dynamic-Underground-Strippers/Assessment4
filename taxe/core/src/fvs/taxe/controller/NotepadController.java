package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.TaxeGame;
import fvs.taxe.actor.NotepadActor;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;

public class NotepadController {

	/**
	 * The height of the Top Bar.
	 */
	public final static float WIDTH = 300 * 0.8f;
	public final static float HEIGHT = 364 * 0.8f;

	/**
	 * The Game Context.
	 */
	private Context context;

	/**
	 * The end Turn Button used for the player to End the Turn.
	 */
	private TextButton endTurnButton;

	/**
	 * Actor for the background to the Top Bar
	 */
	private NotepadActor notepadActor;

	/**
	 * Instantiation method
	 *
	 * @param context The game Context.
	 */
	public NotepadController(final Context context) {
		this.context = context;
	}

	public void draw() {
		if (notepadActor != null) notepadActor.remove();
		notepadActor = new NotepadActor();
		context.getStage().addActor(notepadActor);
	}

	/**
	 * This method adds an End Turn button to the game that captures an on click event and notifies the game when the turn is over.
	 */
	public void drawEndTurnButton() {
		if (Game.getInstance().getReplay()) {
			endTurnButton = new TextButton("Start", context.getSkin());
		} else {
			endTurnButton = new TextButton("End Turn", context.getSkin());
		}
		endTurnButton.setPosition(TaxeGame.WIDTH - 100.0f, TaxeGame.HEIGHT - 33.0f);
		endTurnButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				context.getGameLogic().getPlayerManager().turnOver(context);
			}
		});

		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.NORMAL || state == GameState.REPLAY_SETUP) {
					endTurnButton.setText("Next Turn");
					endTurnButton.setSize(80, 25);
					endTurnButton.setVisible(true);
				} else {
					endTurnButton.setVisible(false);
				}
			}
		});

		context.getStage().addActor(endTurnButton);
	}


}
