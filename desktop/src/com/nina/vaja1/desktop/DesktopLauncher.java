package com.nina.vaja1.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nina.vaja1.vaja1Game;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.x=0;
		config.y=0;
		config.width = 1024;
		config.height = 768;
		new LwjglApplication(new vaja1Game(), config);
	}
}
