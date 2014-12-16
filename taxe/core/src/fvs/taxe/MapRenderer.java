package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class MapRenderer {
    private Stage stage;
    private TaxeGame game;
    private Map map;
    private List<StationClickListener> stationClickListeners = new ArrayList<StationClickListener>();

    public MapRenderer(TaxeGame game, Stage stage) {
        this.game = game;
        this.stage = stage;
        map = new Map();
    }

    public void subscribeStationClick(StationClickListener listener) {
        stationClickListeners.add(listener);
    }

    private void stationClicked(Station station) {
        for (StationClickListener listener : stationClickListeners) {
            listener.clicked(station);
        }
    }

    public void renderStations() {
        for (Station station : map.getStations()) {
            renderStation(station);
        }
    }

    private void renderStation(final Station station) {
        IPositionable location = station.getLocation();
        MapActor actor = new MapActor(location.getX(), location.getY(), station);
        actor.setTouchable(Touchable.enabled);
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                /*
                 if a station is clicked, someone could be training to place a train there,
                 or be routing a train, we don't care about this, we should just raise an event
                  */
                stationClicked(station);
            }
        });
        stage.addActor(actor);
    }

    public void renderConnections() {
        int lineWidth = 5;
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(Color.GRAY);
        //game.shapeRenderer.setProjectionMatrix(camera.combined);

        for (Connection connection : map.getConnections()) {
            IPositionable start = connection.getStation1().getLocation();
            IPositionable end = connection.getStation2().getLocation();
            game.shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), lineWidth);
        }
        game.shapeRenderer.end();
    }

    public void renderTrain(Train t) {
        Image train = new Image(new Texture(Gdx.files.internal(t.getImage())));
        IPositionable position = t.getPosition();
        train.setPosition(position.getX(), position.getY());
        train.setSize(100f, 100f);
        //train.addAction(sequence(moveTo(340f, 290f, 5f), moveTo(560, 390, 5f), moveTo(245, 510, 5f)));
        stage.addActor(train);
    }
}