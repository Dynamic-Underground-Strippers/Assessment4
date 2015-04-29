package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import fvs.taxe.TaxeGame;

public class EndTurnActor extends Actor {
	private final Texture image;

	public EndTurnActor() {
		image = new Texture(Gdx.files.internal("end_turn.png"));
		setSize(SkillBarActor.HEIGHT * 2, SkillBarActor.HEIGHT);
		setPosition(TaxeGame.WIDTH / 2f - SkillBarActor.HEIGHT, SkillBarActor.HEIGHT);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		batch.begin();
		batch.draw(image, getX(), getY(), getWidth(), getHeight());
		batch.end();
		batch.begin();
	}
}
