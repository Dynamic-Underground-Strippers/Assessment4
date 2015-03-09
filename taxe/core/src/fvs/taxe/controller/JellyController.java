package fvs.taxe.controller;

import fvs.taxe.actor.JellyActor;
import fvs.taxe.dialog.JellyClicked;
import gameLogic.Player;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Jelly;

/**Controller for managing games graphics*/
public class JellyController {
    /**The game context.*/
    private Context context;

    /**Instantiation method.
     * @param context The game context.
     */
    public JellyController(Context context) {
        this.context = context;
    }

    /**This method renders a jelly by adding it to the Game as a TrainActor.
     * @param jelly The jelly to be rendered.
     * @return The TrainActor produced using the jelly.
     */
    public JellyActor renderJelly(Jelly jelly) {
        JellyActor jellyActor = new JellyActor(jelly,context);
        jellyActor.addListener(new JellyClicked(context, jelly));
        jellyActor.setVisible(false);
        context.getStage().addActor(jellyActor);

        return jellyActor;
    }



    /**This method sets all jellys on the map to a visibility except for a specified jelly.
     * @param jelly The jelly to be excluded.
     * @param visible The visibility to set all the other resources to.
     */
    public void setJelliesVisible(Jelly jelly, boolean visible) {

        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
            for(Resource resource : player.getResources()) {
                if(resource instanceof Jelly) {
                    boolean jellyAtStation = false;
                    for(Station station : context.getGameLogic().getMap().getStations()) {
                        if(station.getLocation() == ((Jelly) resource).getPosition()){
                            jellyAtStation = true;
                            break;
                        }
                    }
                    if(((Jelly) resource).getActor() != null && resource != jelly && !jellyAtStation) {
                        ((Jelly) resource).getActor().setVisible(visible);
                    }
                }
            }
        }
    }
}
