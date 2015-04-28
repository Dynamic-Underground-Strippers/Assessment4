package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.HashMap;
import java.util.List;

import fvs.taxe.TaxeGame;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DeleteConnectionClicked;
import fvs.taxe.dialog.NewConnectionClicked;
import fvs.taxe.dialog.TrainClicked;
import gameLogic.Game;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

public class SkillBarActor extends Actor {
	private static final int NUM_ITEMS = 6;
	private static final float HEIGHT = 85f;
	private static final float WIDTH = HEIGHT * NUM_ITEMS;
	private static final float XPOS = TaxeGame.WIDTH / 2f - WIDTH / 2f;
	private static final float YPOS = 0;
	private static Texture bgTexture;
	private List<Resource> resources;
	private static final HashMap<String, Texture> trainTextures = new HashMap<String, Texture>();

	public SkillBarActor(final Context context) {
		if (bgTexture == null) bgTexture = new Texture(Gdx.files.internal("toolbarsquare.png"));
		setSize(WIDTH, HEIGHT);
		setPosition(XPOS, YPOS);
		resources = Game.getInstance().getPlayerManager().getCurrentPlayer().getResources();
		addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for (int i = 1; i < NUM_ITEMS + 1; i++) {
					if (x < ((float) i / (float) NUM_ITEMS) * getWidth()) {
						if (resources.get(i - 1) instanceof Train) {
							new TrainClicked(context, (Train) resources.get(i - 1))
									.clicked(event, x, y);
						} else if (resources.get(i - 1) instanceof NewConnection) {
							new NewConnectionClicked(context, (NewConnection) resources.get(i))
									.clicked(event, x, y);
						} else if (resources.get(i - 1) instanceof DeleteConnection) {
							new DeleteConnectionClicked(context,
									(DeleteConnection) resources.get(i)).clicked(event, x, y);
						}
						return;
					}
				}
			}
		});
		if (trainTextures.size() == 0) {
			trainTextures
					.put("44 Train", new Texture(Gdx.files.internal("trains/icons/44Train.png")));
			trainTextures.put("Electric Train",
					new Texture(Gdx.files.internal("trains/icons/ElectricTrain.png")));
			trainTextures.put("Taxi Train",
					new Texture(Gdx.files.internal("trains/icons/TaxiTrain" + ".png")));
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
					batch.draw(trainTextures.get(((Train) resources.get(i)).getName()),
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
