package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.TaxeGame;
import gameLogic.Game;

/**This class stores the Context of the game, such as the game itself, the stage, etc.*/
public class Context {
	
	/**The main Instance of the game is storedhere.*/
    private TaxeGame taxeGame;
    
    /**The stage of the game is stored here.*/
    private Stage stage;
    
    /**The skin used for UI in the game is stored here.*/
    private Skin skin;
    
    /**The Game logic itself is stored here.*/
    private Game gameLogic;
    
    /**A RouteController for the context that can be get or set.*/
    private RouteController routeController;
    
    /**A TopBarController for the context that can be get or set.*/
    private SideBarController sideBarController;

    /**Instantiation method sets up private variables.
     * @param stage The stage to be used in the context
     * @param skin The skin to be used in the context
     * @param taxeGame The main Game instance to be used in the context
     * @param gameLogic The Game's logic instance to be used in the context
     */
    public Context(Stage stage, Skin skin, TaxeGame taxeGame, Game gameLogic) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
        this.gameLogic = gameLogic;
    }

    /**@returns the Context's stage.*/
    public Stage getStage() {
        return stage;
    }

    /**@returns the Context's skin.*/
    public Skin getSkin() {
        return skin;
    }

    /**@returns the Context's main Game Instance.*/
    public TaxeGame getTaxeGame() {
        return taxeGame;
    }

    /**@returns the Context's Game Logic.*/
    public Game getGameLogic() {
        return gameLogic;
    }

    /**@returns the Context's RouteController.*/
    public RouteController getRouteController() {
        return routeController;
    }

    /**Sets the routeController.
     * @param routeController The new RouteController to be used in the Context.
     */
    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
    }

    /**@returns the Context's SideBarController.*/
    public SideBarController getSideBarController() {
        return sideBarController;
    }
    /**Sets the SideBarController.
     * @param sideBarController The new SideBarController to be used in the Context.
     */
    public void setSideBarController(SideBarController sideBarController) {
        this.sideBarController = sideBarController;
    }

}
