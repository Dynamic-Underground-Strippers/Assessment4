package fvs.taxe.actor;

import com.badlogic.gdx.math.Vector2;
import fvs.taxe.TaxeGame;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.IPositionable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import gameLogic.map.Position;

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

	private TaxeGame game;

	// By "partially draw", it means to draw the whole connection in COlor, then draw the partial section in black

	/** Boolean saying whether the connection should be partly drawn */
	private boolean partialDraw;

	/** If the connection is being partially drawn, where the partial connection starts from */
	private IPositionable partialStart;

	/** If the connection is being partially drawn, where the partial connection ends */
	private IPositionable partialNext;

	public ConnectionActor(Color color, IPositionable start, IPositionable end, float connectionWidth, Context context)  {
		shapeRenderer = new ShapeRenderer();
		this.color = color;
		this.start = start;
		this.end = end;
		this.connectionWidth = connectionWidth;
		game = context.getTaxeGame();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(start.getX(), start.getY(), end.getX(), end.getY(), connectionWidth);


		if (partialDraw) {
			shapeRenderer.setColor(Color.BLACK);
			shapeRenderer.rectLine(partialStart.getX(), partialStart.getY(), partialNext.getX(), partialNext.getY(), connectionWidth);
		}

		shapeRenderer.end();
		if (Game.getInstance().getState() == GameState.ROUTING) {
			IPositionable midpoint = this.getMidpoint();
			batch.begin();
			game.fontTiny.setColor(Color.BLACK);
			String text = String.valueOf(Math.round(getDistance()));
			game.fontTiny.draw(batch, text,
					midpoint.getX() - game.fontTiny.getBounds(text).width / 2f,
					midpoint.getY() + game.fontTiny.getBounds(text).height / 2f);
			batch.end();
		}

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

	public float getDistance() {
		//Uses vector maths to find the absolute distance of the connection
		return Vector2.dst(start.getX(), start.getY(), end.getX(), end.getY());
	}

	/** Clear the previous partial drawing values, and disable the drawing of partial routes until reenabled with setPartialPosition() */
	public void clearPartialPosition(){
		partialDraw = false;
		partialStart = null;
		partialNext = null;
	}

	/** Set the connection to be partially drawn
	 * @param x The starting location's x value
	 * @param y The starting location's y value
	 * @param position The end position to draw the partial route to
	 */
	public void setPartialPosition(float x, float y, IPositionable position) {
		partialDraw = true;
		partialStart = new Position((int)x, (int)y);
		partialNext = position;
	}

}
