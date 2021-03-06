package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import fvs.taxe.controller.Context;
import gameLogic.Game;

public class DialogTurnSkipped extends Dialog {

    Context context;
    public DialogTurnSkipped(Skin skin, Context context) {
        super("Miss a turn", skin);
        this.context = context;
        //Informs player that they have missed their turn.
        text("Due to a YUSU candidate pestering your train\n You're going to miss this turn.");
        button("OK", "EXIT");
        align(Align.center);
    }

    @Override
    public Dialog show(Stage stage) {
        //Shows the dialog
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }

    @Override
    public void hide() {
        //Hides the dialog
        hide(null);
    }

    @Override
    protected void result(Object obj) {
        //When the button is clicked
        Game.getInstance().getPlayerManager().turnOver(this.context);
        this.remove();
    }

}