package fvs.taxe.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import fvs.taxe.TaxeGame;
import fvs.taxe.dialog.TrainClicked;
import gameLogic.Player;
import gameLogic.PlayerChangedListener;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

/**Controller for updating UI with resources.*/
public class ResourceController {
	
	/**The context of the Game.*/
    private Context context;
    
    /**The group of resource buttons used in the Game GUI.*/
    private Group resourceButtons = new Group();

    /**The instantiation method sets up a listener that updates the player Resources graphics when the player changes.
     * @param context The context of the game
     */
    public ResourceController(final Context context) {
        this.context = context;

        context.getGameLogic().getPlayerManager().subscribePlayerChanged(new PlayerChangedListener() {
            @Override
            public void changed() {
                drawPlayerResources(context.getGameLogic().getPlayerManager().getCurrentPlayer());
            }
        });
    }

    /**This method draws a header above the player resources.*/
    public void drawHeaderText() {
        TaxeGame game = context.getTaxeGame();

        game.batch.begin();
        game.fontSmall.setColor(Color.BLACK);
        game.fontSmall.draw(game.batch, "Unplaced Resources:", (float) TaxeGame.WIDTH - SideBarController.WIDTH + 10.0f, (float) TaxeGame.HEIGHT - 325.0f);
        game.batch.end();
    }

    /**This method draws a specific player's resources.
     * @param player The active player whose resources should be drawn.
     */
    public void drawPlayerResources(Player player) {

        float top = (float) TaxeGame.HEIGHT;
        float x = TaxeGame.WIDTH - SideBarController.WIDTH + 10.0f;
        float y = top - 340.0f;
        y -= 50;

        resourceButtons.remove();
        resourceButtons.clear();

        for (final Resource resource : player.getResources()) {
            if (resource instanceof Train) {
                Train train = (Train) resource;

                // don't show a button for trains that have been placed
                if (train.getPosition() != null) {
                    continue;
                }

                TrainClicked listener = new TrainClicked(context, train);

                TextButton button = new TextButton(resource.toString(), context.getSkin());
                button.setPosition(x, y);
                button.addListener(listener);

                resourceButtons.addActor(button);

                y -= 30;
            }
        }

        context.getStage().addActor(resourceButtons);
    }
}
