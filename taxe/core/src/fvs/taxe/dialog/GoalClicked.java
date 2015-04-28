package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import fvs.taxe.Tooltip;
import fvs.taxe.actor.StationActor;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.goal.Goal;
import gameLogic.map.Station;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**Specialised dialogue which is activated when a goal is clicked.*/
public class GoalClicked extends ClickListener {

	/**The goal the ClickListener represents.*/

	/**Instantiation method.
	 * @param goal The goal that the ClickListener corresponds to.
	 */
	public GoalClicked(Goal goal,Context context) {
		this.goal = goal;
		this.context = context;
	}

	private Context context;
	private Goal goal;
	private Tooltip tooltip1;
	private Tooltip tooltip2;
	private Tooltip tooltip3;

	//This boolean was necessary to check whether tooltips are currently being displayed or not. Otherwise tooltips got constantly re-rendered
	private boolean showingTooltips;

	/**When the GoalClicked is clicked, then the origin and destination of the goal are highlighted on the map.*/
	@Override
	public void clicked(InputEvent event, float x, float y) {
		//A check was necessary as to whether tooltips were currently being shown
		//This is due to the odd way that the events work
		//When clicking on a goal, it simultaneously performs the enter and exit methods
		//This led to some unintended behaviour where the tooltips were permanently rendered
		//Therefore they are only hidden if they are being shown
		if (showingTooltips) {

			//This hides the currently shown tooltips as otherwise they get stuck
			tooltip1.hide();
			tooltip2.hide();

			try {
				tooltip3.hide();
			} catch (Exception e){
			}

			//Resets the tooltip flag to false
			showingTooltips = false;
		}


		if (Game.getInstance().getState() == GameState.NORMAL) {
			//If the current game state is normal then a dialog is displayed allowing the user to interact with their goal
			Player currentPlayer = context.getGameLogic().getPlayerManager().getCurrentPlayer();
			DialogGoalButtonClicked listener = new DialogGoalButtonClicked(currentPlayer,
					goal);
			DialogGoal dia = new DialogGoal(goal, context.getSkin());
			dia.show(context.getStage());
			dia.subscribeClick(listener);
		}
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		if (!showingTooltips) {
			//Need to check whether tooltips are currently being shown as otherwise it redraws them instantly after the clicked routine has ended
			tooltip1 = new Tooltip(context.getSkin());
			Station origin = goal.getOrigin();
			StationActor originActor = origin.getActor();

			//Sets the tooltip to have the origin's name and to be shown to the top right of the station
			tooltip1.setPosition(originActor.getX() + 20, originActor.getY() + 20);
			tooltip1.show(origin.getName());
			context.getStage().addActor(tooltip1);

			//Sets the tooltip to have the destination's name and to be shown to the top right of the station
			tooltip2 = new Tooltip(context.getSkin());
			Station destination = goal.getDestination();
			if (destination!=null) {
				StationActor destinationActor = destination.getActor();
				context.getStage().addActor(tooltip2);
				tooltip2.setPosition(destinationActor.getX() + 20, destinationActor.getY() + 20);
				tooltip2.show(destination.getName());
			}

			//If there is an intermediary station then a tooltip is also drawn for this station in the same way as the others
			Station intermediary = goal.getExclusionStation();
			if (!intermediary.getName().equals(origin.getName())) {
				tooltip3 = new Tooltip(context.getSkin());
				StationActor intermediaryActor = intermediary.getActor();
				context.getStage().addActor(tooltip3);
				tooltip3.setPosition(intermediaryActor.getX() + 20, intermediaryActor.getY() + 20);
				tooltip3.show(intermediary.getName());
			}

			//Indicates that toolTips are currently being displayed
			showingTooltips = true;
		}
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		//If tooltips are currently being displayed then it hides them all
		if (showingTooltips) {
			tooltip1.hide();
			tooltip2.hide();
			try {
				tooltip3.hide();
			} catch (Exception e){
			}
			//Indicates that tooltips are currently not being displayed
			showingTooltips = false;
		}
	}

}
