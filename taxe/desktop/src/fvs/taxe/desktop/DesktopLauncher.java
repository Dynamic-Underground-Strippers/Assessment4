package fvs.taxe.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import fvs.taxe.TaxeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//Set window size
		config.height = TaxeGame.HEIGHT;
		config.width = TaxeGame.WIDTH;
		config.title = "Trains Across Campus";
		config.resizable = false;
		config.addIcon("dus256.png", FileType.Internal);
		config.addIcon("dus128.png", FileType.Internal);
		config.addIcon("dus64.png", FileType.Internal);
		config.addIcon("dus32.png", FileType.Internal);
		config.addIcon("dus16.png",FileType.Internal);
		//config.fullscreen = true;
		new LwjglApplication(new TaxeGame(), config);
	}
}
