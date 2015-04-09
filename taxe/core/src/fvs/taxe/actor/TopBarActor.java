package fvs.taxe.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import fvs.taxe.TaxeGame;
import fvs.taxe.controller.TopBarController;

/**Type of Actor specifically for implementing the Top bar in the game GUI*/
public class TopBarActor extends Actor {
	
	/**ShapeRenderer used to render the shape of the TopBar*/
	private ShapeRenderer shapeRenderer;
	
	/**Width of obstacle Top Bar*/
	private float obstacleWidth = 0;
	
	/**Color for the controls, can be get and set*/
	private Color controlsColor = Color.LIGHT_GRAY;
	
	/**Color for obstacles, can be get and set*/
    private Color obstacleColor = Color.LIGHT_GRAY;
    
    /**Height of the TopBar*/
	private int controlsHeight;

	/**Instantiation method setsup variables*/
	public TopBarActor(){
		super();
		this.shapeRenderer = new ShapeRenderer();
		this.controlsHeight = TopBarController.CONTROLS_HEIGHT;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.end();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // main topBar
        shapeRenderer.setColor(controlsColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, controlsHeight);
       
        // obstacle topBar 
        shapeRenderer.setColor(obstacleColor);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, obstacleWidth, controlsHeight);
        
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, TaxeGame.HEIGHT - controlsHeight, TaxeGame.WIDTH, 1);
        
        shapeRenderer.end();
        batch.begin();
	}
	
	/**Sets the color of the Obstacle bar*/
	public void setObstacleColor(Color color) {
		this.obstacleColor = color;
	}

	/**Sets the color of the Control bar*/
	public void setControlsColor(Color color) {
		this.controlsColor = color;
	}

	/**Sets the widthof the Obstacle bar*/
	public void setObstacleWidth(float width) {
		this.obstacleWidth = width;
	}
}
