package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import fvs.taxe.TaxeGame;
import fvs.taxe.controller.NotepadController;

public class NotepadActor extends Actor {

	private Texture notepadTexture;

	public NotepadActor() {
		super();
		notepadTexture = new Texture(Gdx.files.internal("notepad.png"));
		setSize(NotepadController.WIDTH, NotepadController.HEIGHT);
		setPosition(TaxeGame.WIDTH - NotepadController.WIDTH - 15,
				TaxeGame.HEIGHT - NotepadController.HEIGHT - 5);
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		batch.begin();
		batch.draw(notepadTexture, getX(), getY(), getWidth(), getHeight());
		batch.end();
		batch.begin();
	}
}
