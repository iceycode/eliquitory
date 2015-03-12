package com.icey.apps.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.icey.apps.utils.Constants;

/** contains skins for creations of screens
 * TODO: add splash screen for when loading assets
 *
 * Created by Allen on 1/10/15.
 */
public class Assets {

    public static AssetManager manager;

    private static int skinID; //id of skin (default is 0)

    //loads the assets as Textures
    public static void loadAssets(){
        manager = new AssetManager();

        loadSkins();
        setCurrentSkin(0);
    }

    //loads the skins of screens
    private static void loadSkins(){

        //dark theme - implement this in every screen
        manager.load(Constants.DARK_SKIN, Skin.class);
    }


    //FIXME: add more skins to make id param useful
    public static void setCurrentSkin(int id){
        skinID = id;
    }

    public static Skin getSkin(){
        return manager.get(Constants.DARK_SKIN, Skin.class);
    }


    //class containing assets for running tests
    public static class TestAssets{

        public static AssetManager testAM;


        public static AssetManager loadAssets(){
            AssetManager testAM = new AssetManager();

            return testAM;
        }


        public static Skin getTestSkin(){
            TextureRegion background = AssetHelper.createTextureRegion((int)Constants.SCREEN_WIDTH, (int)Constants.SCREEN_HEIGHT, Color.WHITE, .5f);
            TextureRegion region1 = AssetHelper.createTextureRegion(100, 100, Color.RED, 1f);
            TextureRegion region2 = AssetHelper.createTextureRegion(100, 100, Color.RED, .8f);
            TextureRegion region3 = AssetHelper.createTextureRegion(200, 200, Color.YELLOW, 1f);

            Skin skin = new Skin(Gdx.files.internal(Constants.CALC_SKIN));
            skin.add("background", background);
            skin.add("region1", region1);
            skin.add("region2", region2);
            skin.add("region3", region3);

            return skin;
        }
    }

}
