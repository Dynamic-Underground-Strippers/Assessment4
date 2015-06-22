package fvs.taxe.controller;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.actor.NewsFlashActor;
import javafx.application.Application;

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
		final InputListener keyListener = new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				newsFlashActor.remove();
				context.getStage().removeListener(this);
				return false;
			}
		};
		context.getStage().addListener(keyListener);
		context.getStage().addActor(newsFlashActor);
	}

	public static NewsFlashController getInstance() {
		return instance;
	}
}
