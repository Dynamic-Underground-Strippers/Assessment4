package fvs.taxe.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import fvs.taxe.actor.ClockActor;

public class ClockController {
    private final Context context;
    private BitmapFont font;
    ClockActor clock = new ClockActor();
    float[] xPosPercentages = new float[]{0.11f, 0.32f, 0.56f, 0.76f};

    public ClockController(Context context) {
        this.context = context;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("clock_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (ClockActor.HEIGHT * 0.6f);
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public void draw() {
        if (clock != null) clock.remove();
        context.getStage().addActor(clock);
        int hour = 9;
        boolean halfPast = false;
        for (int i = 0; i < context.getGameLogic().getPlayerManager().getTurnNumber(); i++) {
            if (!(halfPast = !halfPast)) hour++;
            if (hour == 24) hour = 0;
        }
        String hourStr = String.valueOf(hour);
        String[] parts = new String[4];
        if (hourStr.length() == 1) {
            parts[0] = "0";
            parts[1] = String.valueOf(hourStr.charAt(0));
        } else {
            parts[0] = String.valueOf(hourStr.charAt(0));
            parts[1] = String.valueOf(hourStr.charAt(1));
        }
        parts[2] = String.valueOf(halfPast ? "3" : "0");
        parts[3] = String.valueOf("0");
        Group group = new Group();
        for (int i = 0; i < xPosPercentages.length; i++) {
            Label label = new Label(parts[i], context.getSkin());
            label.setStyle(new Label.LabelStyle(font, Color.GREEN));
            label.setPosition(ClockActor.XPOS + xPosPercentages[i] * ClockActor.WIDTH, ClockActor.YPOS + 40);
            group.addActor(label);
        }

        context.getStage().addActor(group);
    }
}
