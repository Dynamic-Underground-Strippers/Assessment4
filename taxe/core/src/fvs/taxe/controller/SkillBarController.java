package fvs.taxe.controller;

import fvs.taxe.actor.SkillBarActor;
import gameLogic.PlayerChangedListener;

public class SkillBarController {
	private final Context context;
	private SkillBarActor skillBarActor;

	public SkillBarController(Context context) {
		this.context = context;
		context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
			@Override
			public void changed() {
				draw();
			}
		});
	}

	public void draw() {
		if (skillBarActor != null) skillBarActor.remove();
		skillBarActor = new SkillBarActor(context);
		context.getStage().addActor(skillBarActor);
	}
}