package fvs.taxe.controller;

import fvs.taxe.actor.SkillBarActor;

public class SkillBarController {
	private final Context context;
	private SkillBarActor skillBarActor;

	public SkillBarController(Context context) {
		this.context = context;
	}

	public void draw() {
		if (skillBarActor != null) skillBarActor.remove();
		skillBarActor = new SkillBarActor(context);
		context.getStage().addActor(skillBarActor);
	}
}