package fvs.taxe.controller;

import fvs.taxe.actor.TrainActor;
import fvs.taxe.dialog.TrainClicked;
import gameLogic.Player;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

/**Controller for managing games graphics*/
public class TrainController {
	/**The game context.*/
    private Context context;

    /**Instantiation method.
     * @param context The game context.
     */
    public TrainController(Context context) {
        this.context = context;
    }

    /**This method renders a train by adding it to the Game as a TrainActor.
     * @param train The train to be rendered.
     * @return The TrainActor produced using the train.
     */
    public TrainActor renderTrain(Train train) {
        TrainActor trainActor = new TrainActor(train,context);
        trainActor.addListener(new TrainClicked(context, train));
        trainActor.setVisible(false);
        context.getStage().addActor(trainActor);

        return trainActor;
    }



    /**This method sets all trains on the map to a visibility except for a specified train.
     * @param train The train to be excluded.
     * @param visible The visibility to set all the other resources to.
     */
    public void setTrainsVisible(Train train, boolean visible) {

        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
            for(Resource resource : player.getResources()) {
                if(resource instanceof Train) {
                	boolean trainAtStation = false;
                	for(Station station : context.getGameLogic().getMap().getStations()) {
                		if(station.getLocation() == ((Train) resource).getPosition()){
                			trainAtStation = true;
                			break;
                		}
                	}
                    if(((Train) resource).getActor() != null && resource != train && !trainAtStation) {
                        ((Train) resource).getActor().setVisible(visible);
                    }
                }
            }
        }
    }
}
