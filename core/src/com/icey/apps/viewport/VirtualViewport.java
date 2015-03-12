package com.icey.apps.viewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.icey.apps.utils.Constants;

/**
 * Created by Allen on 3/8/15.
 */
public class VirtualViewport extends ScalingViewport{

    float virtualWidth = Constants.SCREEN_WIDTH;
    float virtualHeight = Constants.SCREEN_HEIGHT;

    float virtualAspectRatio = Constants.SCREEN_WIDTH/Constants.SCREEN_HEIGHT;
    float aspectRatio; //the real aspect ratio

    float realWidth;
    float realHeight;

    public VirtualViewport(Scaling scaling, float worldWidth, float worldHeight) {
        super(scaling, worldWidth, worldHeight);

        //set real height & width
        this.realWidth = worldWidth;
        this.realHeight = (float) Gdx.graphics.getHeight();

        setViewport();
    }


    //sets the viewport for the stage based on real size & virtual size aspect ratios
    protected void setViewport(){

        this.aspectRatio = realWidth/realHeight;

        //if aspectRatio is greater, this means relative to virtual screen width to height,
        //  the device width is larger relative to the height
        if (aspectRatio >= virtualAspectRatio) {
            float diffX = Math.abs((virtualWidth - (virtualWidth * aspectRatio)));
            realWidth -= diffX;
        }
        else{
            float diffY = Math.abs((virtualHeight - (virtualHeight * aspectRatio)));
            realHeight -= diffY;
        }

        setWorldSize(realWidth, realHeight);
    }

    @Override
    public void setScaling(Scaling scaling) {
        super.setScaling(scaling);
    }


    @Override
    public void update(int screenWidth, int screenHeight, boolean centerCamera) {
        float aspectRatio = screenWidth/screenHeight;
        float virtualAspectRatio = Constants.SCREEN_WIDTH/Constants.SCREEN_HEIGHT;

        //if aspectRatio is greater, this means relative to virtual screen width to height,
        //  the device width is larger relative to the height
        if (aspectRatio >= virtualAspectRatio) {
            int diffX = (int) Math.abs((Constants.SCREEN_WIDTH - (Constants.SCREEN_WIDTH * aspectRatio)) / 2f);
            screenWidth -= diffX;
        }
        else{
            int diffY = (int) Math.abs(Constants.SCREEN_HEIGHT - (Constants.SCREEN_HEIGHT * aspectRatio));
            screenHeight -= diffY;
        }

        super.update(screenWidth, screenHeight, centerCamera);
    }

    @Override
    public void setWorldSize(float worldWidth, float worldHeight) {
        super.setWorldSize(worldWidth, worldHeight);
    }
}
