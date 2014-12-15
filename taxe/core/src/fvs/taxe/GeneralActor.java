package fvs.taxe;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import gameLogic.map.IPositionable;
import gameLogic.map.Position;

public class GeneralActor extends Actor {
	protected IPositionable location;
    protected Object relation;
    protected Texture texture;
    protected boolean leftclick = false;
    private Size size;
    
    public GeneralActor(int x, int y, Object relation){
    	//Configure variables
        this.relation = relation;
        size = new Size(16,16);
        this.location = new Position(x-(size.getWidth()/2),y-(size.getHeight()/2));
        
        //Set actor bounds (Not size)
        setBounds((float) location.getX(),(float) location.getY(),(float) size.getHeight(),(float) size.getWidth());
        
        //Add click listener
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button){
                ((MapActor)event.getTarget()).leftclick = true;
                return true;
            }
        });
    }
    
    protected Size getSize(){
    	return size;
    }
    
    protected void setSize(int height, int width){
    	Size currentSize = this.size;
    	location = new Position((location.getX() + (currentSize.getWidth()/2))-(size.getWidth()/2),(location.getY() + (currentSize.getHeight()/2))-(size.getHeight()/2));
    	size = new Size(height, width);
        setBounds((float) location.getX(),(float) location.getY(),(float) size.getHeight(),(float) size.getWidth());
    }
    
    //Draw actor on each draw call
    @Override
    public void draw(Batch batch, float alpha){
        batch.draw(texture, location.getX(), location.getY(),0,0,size.getHeight(),size.getWidth(),1,1,this.getRotation(),0,0,texture.getWidth(),texture.getHeight(),false,false);
    }
}
