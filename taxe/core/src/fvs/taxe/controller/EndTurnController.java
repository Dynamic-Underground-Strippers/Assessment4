package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.actor.EndTurnActor;

public class EndTurnController {
	private Context context;
	private EndTurnActor endTurnActor;

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
		context.getStage().addActor(endTurnActor);
	}
}
