package com.icey.apps.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.icey.apps.MainApp;
import com.icey.apps.utils.Constants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "E-liquid Calculator";
        config.width = (int)Constants.SCREEN_WIDTH;
        config.height = (int)Constants.SCREEN_HEIGHT;

		new LwjglApplication(new MainApp(), config);
	}
}
