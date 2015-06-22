package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class NewsFlashActor extends Actor {
	Texture texture;

	public NewsFlashActor(String imagePath) {
		texture = new Texture(Gdx.files.internal(imagePath));
		setPosition(0, 0);
		setSize(512, 512);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		batch.begin();
		batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		batch.end();
		batch.begin();
	}
}
