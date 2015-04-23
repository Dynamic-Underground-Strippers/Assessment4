package fvs.taxe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**This class is used to set up the graphical interface of the main menu for the player. It is first used when the TaxeGame.java is instantiated.*/
public class MainMenuScreen extends ScreenAdapter {
	
	/**Stores the main instance of TaxeGame.java.*/
    final private TaxeGame game;
    
    /**Stores an orthographic camera used in the menu to project clicks.*/
    private OrthographicCamera camera;
    
    /**This rectange stores the bounds of the play button, and is used to detect whether a click has clicked the play button.*/
    private Rectangle playBounds;
    
    /**This rectange stores the bounds of the exit button, and is used to detect whether a click has clicked the exit button.*/
    private Rectangle exitBounds;
    
    /**This vector is set to the location of the most recent click on the screen.*/
    private Vector3 touchPoint;
    
    /**Used to store the map texture which is placed in the background.*/
    private Texture mapTexture;

    private Rectangle replayBounds;
    /**Instantiation method. sets up bounds and camera.
	 *@param game The main TaxeGame instance is assigned to the local variable game.
    */
    public MainMenuScreen(TaxeGame game) {
        this.game = game;
        camera = new OrthographicCamera(TaxeGame.WIDTH, TaxeGame.HEIGHT);
        camera.setToOrtho(false);

        playBounds = new Rectangle(TaxeGame.WIDTH/2 - 200, 350, 400, 100);
        replayBounds = new Rectangle(TaxeGame.WIDTH/2-200,200,400,100);
        exitBounds = new Rectangle(TaxeGame.WIDTH/2 - 200, 50, 400, 100);
        touchPoint = new Vector3();
        mapTexture = new Texture(Gdx.files.internal("Map4.png"));
    }

    /**This method is called once every frame using the render method. It checks whether there has been a touch, and if so, checks whether this touch is within one of the buttons bounds.*/
    public void update() {
        if (Gdx.input.justTouched()) {
            camera.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (playBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new GameScreen(game));
                return;
            }
            if (exitBounds.contains(touchPoint.x, touchPoint.y)) {
                Gdx.app.exit();
            }
            if (replayBounds.contains(touchPoint.x, touchPoint.y)){
                //Add replay start
            }
        }
    }

    /**This method is called once every frame using the render method. It draws the main menu.*/
    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(1, 1, 1, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Draw transparent map in the background
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        Color c = game.batch.getColor();
        game.batch.setColor(c.r, c.g, c.b, (float) 0.3);
        game.batch.draw(mapTexture, 0, 0);
        game.batch.setColor(c);
        game.batch.end();

        //Draw rectangles, did not use TextButtons because it was easier not to
        game.shapeRenderer.setProjectionMatrix(camera.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        game.shapeRenderer.setColor(Color.GREEN);
        game.shapeRenderer.rect(playBounds.getX(), playBounds.getY(), playBounds.getWidth(), playBounds.getHeight());
        game.shapeRenderer.setColor(Color.BLUE);
        game.shapeRenderer.rect(replayBounds.getX(), replayBounds.getY(), replayBounds.getWidth(), replayBounds.getHeight());
        game.shapeRenderer.setColor(Color.RED);
        game.shapeRenderer.rect(exitBounds.getX(), exitBounds.getY(), exitBounds.getWidth(), exitBounds.getHeight());
        game.shapeRenderer.end();

        //Draw text into rectangles
        game.batch.begin();
        String startGameString = "Start Game";
        game.font.draw(game.batch, startGameString, playBounds.getX() + playBounds.getWidth()/2 - game.font.getBounds(startGameString).width/2,
                playBounds.getY() + playBounds.getHeight()/2 + game.font.getBounds(startGameString).height/2); // center the text
        String replayString = "Watch Replay";
        game.font.draw(game.batch, replayString, replayBounds.getX() + replayBounds.getWidth()/2 - game.font.getBounds(replayString).width/2,
                replayBounds.getY() + replayBounds.getHeight()/2 + game.font.getBounds(replayString).height/2); // center the text
        String exitGameString = "Exit";
        game.font.draw(game.batch, exitGameString, exitBounds.getX() + exitBounds.getWidth()/2 - game.font.getBounds(exitGameString).width/2,
                exitBounds.getY() + exitBounds.getHeight()/2 + game.font.getBounds(exitGameString).height/2); // center the text

        game.batch.end();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }
}