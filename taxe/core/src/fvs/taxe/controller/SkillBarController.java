package fvs.taxe.controller;

import fvs.taxe.actor.SkillBarActor;

public class SkillBarController {
	private final Context context;

	public SkillBarController(Context context) {
		this.context = context;
	}

	public void draw() {
		context.getStage().addActor(new SkillBarActor(context));
	}
}