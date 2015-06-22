package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.actor.EndTurnActor;
import gameLogic.GameState;
import gameLogic.GameStateListener;

public class EndTurnController {
	private Context context;
	private EndTurnActor endTurnActor;
	private boolean removed = false;

	public EndTurnController(Context context) {
		this.context = context;
	}

	public void draw() {
		//This draws the end turn button

		//If the actor exists then remove it and redraw it.
		//This was necessary as the garbage collection was failing for this
		if (endTurnActor != null) endTurnActor.remove();
		endTurnActor = new EndTurnActor();
		endTurnActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//When the button is clicked, it triggers the turn over event
				context.getGameLogic().getPlayerManager().turnOver(context);
			}
		});
		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				//Hides the end turn button while animating to prevent unintended behaviour
				if ((state != GameState.NORMAL) && !removed) {
					endTurnActor.remove();
					removed = true;
				} else if (removed) {
					context.getStage().addActor(endTurnActor);
					removed = false;
				}
			}
		});
		context.getStage().addActor(endTurnActor);
	}
}
