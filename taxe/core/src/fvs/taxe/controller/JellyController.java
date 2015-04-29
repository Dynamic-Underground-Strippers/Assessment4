package fvs.taxe.controller;

import fvs.taxe.actor.JellyActor;
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

    /**This method renders a jelly by adding it to the Game as a JellyActor.
     * @param jelly The jelly to be rendered.
     * @return The TrainActor produced using the jelly.
     */
    public JellyActor renderJelly(Jelly jelly) {
        JellyActor jellyActor = new JellyActor(jelly,context);
        jellyActor.setVisible(false);
        context.getStage().addActor(jellyActor);
        return jellyActor;
    }
}
