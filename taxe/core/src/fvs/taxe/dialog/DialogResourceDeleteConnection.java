package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.dialog.ResourceDialogClickListener;
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;

import java.util.ArrayList;
import java.util.List;

public class DialogResourceDeleteConnection extends Dialog {
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();

    public DialogResourceDeleteConnection(DeleteConnection deleteConnection, Skin skin) {
        super(deleteConnection.toString(), skin);

        //Generates all the buttons that allow the user to interact with the dialog
        text("What do you want to do with this 'Remove Connection'?");
        button("Remove connection", "DELETE");
        button("Drop", "DROP");
        button("Cancel", "CLOSE");
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

    private void clicked(Button button) {
        //Informs all listeners which button has been pressed
        for (ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    public void subscribeClick(ResourceDialogClickListener listener) {
        //Adds listener to the dialog
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        //Tells the clicked routine which button has been pressed
        if (obj == "CLOSE") {
            this.remove();
        } else if (obj == "DROP") {
            clicked(Button.DELETECONNECTION_DROP);
        } else if (obj == "DELETE") {
            clicked(Button.DELETECONNECTION_DELETE);
        }
    }
}
