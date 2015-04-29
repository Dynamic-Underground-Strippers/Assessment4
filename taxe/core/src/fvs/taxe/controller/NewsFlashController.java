package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.actor.NewsFlashActor;

public class NewsFlashController {
	private static NewsFlashController instance;
	private NewsFlashActor newsFlashActor;
	private Context context;

	public NewsFlashController(Context context) {
		this.context = context;
		instance = this;
	}

	public void showNewsFlash(String imagePath) {
		newsFlashActor = new NewsFlashActor(imagePath);
		newsFlashActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				if (x > 226 && x < 242 && y > newsFlashActor.getHeight() - 80 &&
						y < newsFlashActor.getHeight() - 64) {
					newsFlashActor.remove();
				}
			}
		});
		context.getStage().addActor(newsFlashActor);
	}

	public static NewsFlashController getInstance() {
		return instance;
	}
}
