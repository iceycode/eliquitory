package com.icey.apps.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Constants;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration((int) Constants.SCREEN_WIDTH, (int)Constants.SCREEN_HEIGHT);
        }

        @Override
        public ApplicationListener getApplicationListener () {
            
            return new MainApp();
        }
}