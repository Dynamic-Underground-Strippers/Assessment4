package fvs.taxe;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**This is the main class of the game, created by the Desktop initiation class. It sets up the rest of the game.*/
public class TaxeGame extends Game {
	
	/**These variables hold the width and height of the window we will be using in the game.*/
	public static final int WIDTH=1272,HEIGHT=678;

	/**The batch is used to draw the game. Each frame it is cleared and new items are drawn into it.*/
	public SpriteBatch batch;
	
	/**The font used for text throughout the game.*/
	public BitmapFont font;
	
	/**A smaller version of the game font.*/
	public BitmapFont fontSmall;
	
	/**ShapeRenderer instance used to render shapes without immediately using textures.*/
	public ShapeRenderer shapeRenderer;

	/**Instantiation method. Sets up the batch, fonts and shapeRenderer, and then sets the Screen to the mainMenu.*/
	@Override
	public void create() {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		//create font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		// font size 50 pixels
		parameter.size = 50;
		font = generator.generateFont(parameter);
		parameter.size = 20;
		fontSmall = generator.generateFont(parameter);
		generator.dispose(); // don't forget to dispose to avoid memory leaks!

		setScreen(new MainMenuScreen(this));
	}

	/**This method renders the game, using super.render().*/
	public void render() {
		super.render(); //important!
	}

	/**Drop our game resources.*/
	public void dispose() {
		batch.dispose();
		font.dispose();
		shapeRenderer.dispose();
	}
}