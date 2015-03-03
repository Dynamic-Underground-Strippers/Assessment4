package fvs.taxe.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

/**This is a special type of dialogue used when there are multiple trains at a station.*/
public class DialogStationMultitrain extends Dialog {

	/**The context of the Game.*/
	private Context context;
	
	/**Whether or not the station has a train on it.*/
	private boolean hasTrain = false;

	/**The instantiation method sets up the dialogue.
	 * @param station The station to be used.
	 * @param skin The skin for the GUI.
	 * @param context The context of the game.
	 */
	public DialogStationMultitrain(Station station, Skin skin, Context context) {
		super(station.getName(), skin);
		this.context = context;
		
		text("Choose which train you would like");
		
		List<Resource> activeTrains = context.getGameLogic().getPlayerManager().getCurrentPlayer().getActiveTrains();
		List<Train> localTrains = new ArrayList<Train>(); // player's active trains at that station
		for (Resource resource : activeTrains) {
			if(((Train) resource).getPosition() == station.getLocation()) {
				localTrains.add((Train) resource);
			}
		}
		
		if (localTrains.size() == 0) {
			// if no active trains at station do nothing!
			hide();
			context.getSideBarController().displayFlashMessage("No Player " + context.getGameLogic().getPlayerManager().getCurrentPlayer().getPlayerNumber() + " trains at this station", Color.RED);
		} else if (localTrains.size() == 1) {
			// if one active train, skip the dialog and go straight to train
			hide();
			result(localTrains.get(0));
			context.getGameLogic().setState(GameState.WAITING);
		} else {
			// if multiple trains, show dialog
			for (Resource resource : localTrains){
				String destination = "";
				if(((Train) resource).getFinalDestination() != null) {
					destination = " to " + ((Train) resource).getFinalDestination().getName();
				}
				button(((Train) resource).getName() + destination + " (Player " + ((Train) resource).getPlayer().getPlayerNumber() + ")", ((Train) resource));
				getButtonTable().row();
				hasTrain = true;
			}
			context.getGameLogic().setState(GameState.WAITING);
		}

		button("Cancel","CANCEL");
	}

	@Override
	public Dialog show(Stage stage) {
		show(stage, null);
		setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
		return this;
	}

	@Override
	public void hide() {
		hide(null);
	}

	@Override
	protected void result(Object obj) {
		context.getGameLogic().setState(GameState.NORMAL);
		if(obj == "CANCEL"){
			this.remove();
		} else {
			//Simulate click on train
			TrainClicked clicker = new TrainClicked(context, (Train) obj);
			clicker.clicked(null, 0, 0);
		}
		
	}

	/**@return True if the station has a train, false otherwise.*/
	public boolean getHasTrain() {
		return hasTrain;
	}
}
