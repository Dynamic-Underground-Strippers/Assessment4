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
		if (endTurnActor != null) endTurnActor.remove();
		endTurnActor = new EndTurnActor();
		endTurnActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				context.getGameLogic().getPlayerManager().turnOver(context);
			}
		});
		context.getGameLogic().subscribeStateChanged(new GameStateListener() {
			@Override
			public void changed(GameState state) {
				if (state == GameState.ANIMATING && !removed) {
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
