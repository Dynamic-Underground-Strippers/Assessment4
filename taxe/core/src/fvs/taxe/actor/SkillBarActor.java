package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;
import java.util.List;

import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

public class SkillBarActor extends Actor {
	public static final int NUM_ITEMS = 7;
	public static final float HEIGHT = 85f;
	public static final float WIDTH = HEIGHT * NUM_ITEMS;
	private static final float XPOS = TaxeGame.WIDTH / 2f - WIDTH / 2f;
	private static final float YPOS = 0;
	private static Texture bgTexture;
	private List<Resource> resources;
	private static final HashMap<String, Texture> textures = new HashMap<String, Texture>();

	public SkillBarActor() {
		if (bgTexture == null) bgTexture = new Texture(Gdx.files.internal("toolbarsquare.png"));
		setSize(WIDTH, HEIGHT);
		setPosition(XPOS, YPOS);
		resources = Game.getInstance().getPlayerManager().getCurrentPlayer().getResources();
		if (textures.size() == 0) {
			textures
					.put("44 Train", new Texture(Gdx.files.internal("resources/icons/44Train.png")));
			textures.put("Electric Train",
					new Texture(Gdx.files.internal("resources/icons/ElectricTrain.png")));
			textures.put("Taxi Train",
					new Texture(Gdx.files.internal("resources/icons/TaxiTrain.png")));
			textures.put("Bike Train",
					new Texture(Gdx.files.internal("resources/icons/BikeTrain.png")));
			textures.put("New Connection",
					new Texture(Gdx.files.internal("resources/icons/NewTrack.png")));
			textures.put("Delete Connection",
					new Texture(Gdx.files.internal("resources/icons/RemoveTrack.png")));
		}
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		batch.begin();
		for (int i = 0; i < NUM_ITEMS; i++) {
			batch.draw(bgTexture, getX() + ((float) i / (float) NUM_ITEMS) * getWidth(), getY(),
					getHeight(), getHeight());
			if (i < resources.size()) {
				if (resources.get(i) instanceof Train) {
						if (((Train)(resources.get(i))).getPosition()==null) {
						batch.draw(textures.get(((Train) resources.get(i)).getName()),
								getX() + ((float) i / (float) NUM_ITEMS) * getWidth() +
										(getHeight() * 0.1f), getY() + (getHeight() * 0.15f),
								getHeight() * 0.8f, getHeight() * 0.8f);
					}
				} else if (resources.get(i) instanceof NewConnection) {
					batch.draw(textures.get("New Connection"),
							getX() + ((float) i / (float) NUM_ITEMS) * getWidth() +
									(getHeight() * 0.1f), getY() + (getHeight() * 0.15f),
							getHeight() * 0.8f, getHeight() * 0.8f);
				} else if (resources.get(i) instanceof DeleteConnection) {
					batch.draw(textures.get("Delete Connection"),
							getX() + ((float) i / (float) NUM_ITEMS) * getWidth() +
									(getHeight() * 0.1f), getY() + (getHeight() * 0.15f),
							getHeight() * 0.8f, getHeight() * 0.8f);
				}
			}
		}
		batch.end();
		batch.begin();
	}
}
