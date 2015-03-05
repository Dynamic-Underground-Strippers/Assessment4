package fvs.taxe.actor;

import gameLogic.map.IPositionable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**This class is a type of image specifically for creating connections between stations.*/
public class ConnectionActor extends Image{

	/**This variable stores the width of the connection between stations in pixels.*/
	private float connectionWidth;
	
	/**The shapeRenderer variable is used to render a line from the start to the end of the given color and connectionWidth.*/
	private ShapeRenderer shapeRenderer;
	
	/**The color of the connection between stations.*/
	private Color color;
	
	/**The start position of the connection, where the line is drawn from.*/
	private IPositionable start;
	
	/**The end position of the connection, where the line is drawn to.*/
	private IPositionable end;

	public ConnectionActor(Color color, IPositionable start, IPositionable end, float connectionWidth)  {
		shapeRenderer = new ShapeRenderer();
		this.color = color;
		this.start = start;
		this.end = end;
		this.connectionWidth = connectionWidth;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), connectionWidth);
        shapeRenderer.end();
        batch.begin();
	}

	public void setConnectionColor(Color color) {
		this.color = color;
	}
	
	public Color getConnectionColor(){
		return this.color;
	}

	public IPositionable getMidpoint() {
		//This returns the midPoint of the connection, which is useful for drawing the obstacle indicators on to the connection
		return new IPositionable() {
			@Override
			public int getX() {
				return (start.getX() + end.getX()) / 2;
			}

			@Override
			public int getY() {
				return (start.getY() + end.getY()) / 2;
			}

			@Override
			public void setX(int x) {

			}

			@Override
			public void setY(int y) {

			}

			@Override
			public boolean equals(Object o) {
				return false;
			}
		};
	}
}
