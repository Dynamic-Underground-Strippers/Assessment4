package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import fvs.taxe.actor.NotepadActor;

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
}
