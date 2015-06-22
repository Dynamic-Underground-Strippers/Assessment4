package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
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
		//Checks whether the actor exists and if it does then it is removed
		//This is due to issues with garbage collection and memory leaks that we experienced
		if (skillBarActor != null) skillBarActor.remove();
		skillBarActor = new SkillBarActor();
		resources = Game.getInstance().getPlayerManager().getCurrentPlayer().getResources();
		final ArrayList<Resource> unplacedResources = new ArrayList<Resource>();
		for (Resource resource:resources){
			if (resource instanceof Train){
				if (((Train) resource).getPosition()==null){
					unplacedResources.add(resource);
				}
			}else
			unplacedResources.add(resource);
		}
		skillBarActor.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				//Passes to the relevant click listener based on which resource was clicked by the player
				for (int i = 0; i < unplacedResources.size(); i++) {
					if (x < ((float) (i + 1) / (float) SkillBarActor
							.NUM_ITEMS) *
							SkillBarActor.WIDTH) {

						if (unplacedResources.get(i) instanceof Train) {
								new TrainClicked(context, (Train) unplacedResources.get(i))
										.clicked(event, x, y);

						} else if (unplacedResources.get(i) instanceof NewConnection) {
							new NewConnectionClicked(context, (NewConnection) unplacedResources.get(i))
									.clicked(event, x, y);

						} else if (unplacedResources.get(i) instanceof DeleteConnection) {
							new DeleteConnectionClicked(context,
									(DeleteConnection) unplacedResources.get(i)).clicked(event, x, y);
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