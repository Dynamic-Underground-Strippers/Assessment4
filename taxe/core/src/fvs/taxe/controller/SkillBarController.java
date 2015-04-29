package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.List;

import fvs.taxe.actor.SkillBarActor;
import fvs.taxe.dialog.DeleteConnectionClicked;
import fvs.taxe.dialog.NewConnectionClicked;
import fvs.taxe.dialog.TrainClicked;
import gameLogic.Game;
import gameLogic.PlayerChangedListener;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

public class SkillBarController {
	private final Context context;
	private SkillBarActor skillBarActor;
	private List<Resource> resources;
	private static SkillBarController instance;

	public SkillBarController(Context context) {
		this.context = context;
		instance = this;
		context.getGameLogic().getPlayerManager()
			   .subscribePlayerChanged(new PlayerChangedListener() {
				   @Override
				   public void changed() {
					   draw();
				   }
			   });
	}

	public void draw() {
		if (skillBarActor != null) skillBarActor.remove();
		skillBarActor = new SkillBarActor();
		resources = Game.getInstance().getPlayerManager().getCurrentPlayer().getResources();
		skillBarActor.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				for (int i = 0; i < SkillBarActor.NUM_ITEMS; i++) {
					if (x < ((float) (i + 1) / (float) SkillBarActor
							.NUM_ITEMS) *
							SkillBarActor.WIDTH) {
						System.out.println(resources);
						if (resources.get(i) instanceof Train) {
							new TrainClicked(context, (Train) resources.get(i))
									.clicked(event, x, y);
						} else if (resources.get(i) instanceof NewConnection) {
							new NewConnectionClicked(context, (NewConnection) resources.get(i))
									.clicked(event, x, y);
						} else if (resources.get(i) instanceof DeleteConnection) {
							new DeleteConnectionClicked(context,
									(DeleteConnection) resources.get(i)).clicked(event, x, y);
						}
						return;
					}
				}
			}
		});
		context.getStage().addActor(skillBarActor);
	}

	public static SkillBarController getInstance() {
		return instance;
	}
}