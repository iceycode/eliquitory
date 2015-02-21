package com.icey.apps.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/** contains skins for creations of screens
 * TODO: add splash screen for when loading assets
 *
 * Created by Allen on 1/10/15.
 */
public class Assets {

    public static AssetManager manager;
    public static boolean loaded = false;


    //loads the assets as Textures
    public static void loadAssets(){
        manager = new AssetManager();

        loadSkins();
    }

    //loads the skins of screens
    private static void loadSkins(){
        manager.load(Constants.CALC_SKIN, Skin.class);
        manager.load(Constants.MENU_SKIN, Skin.class);
        manager.load(Constants.SUPPLY_MENU_SKIN, Skin.class);
        manager.load(Constants.SETTING_SKIN, Skin.class);
    }


    //returns true if manager is done loading
    public boolean isLoaded(){
        return manager.update();
    }

    
    public static class AssetHelper{
        
        public static TextureRegion createTextureRegion(int width, int height, Color color, float alpha){
            color.a = alpha;

            Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGB888);
            pixmap.setColor(color);
            
            Texture texture = new Texture(pixmap);
            
            return new TextureRegion(texture);
        }


        public static Texture createTexture(float width, float height, Color color, float alpha){
            color.a = alpha;

            Pixmap pixmap = new Pixmap((int)width, (int)height, Pixmap.Format.RGB888);
            pixmap.setColor(color);

            Texture texture = new Texture(pixmap);

            return texture;
        }


        /** gets fonts in skins based on screen
         *  - rescales the fonts
         * @param type
         * @return
         */
        public static Array<BitmapFont> getScreenFonts(int type){
            Skin skin;

            if (type == 0)
                skin = manager.get(Constants.MENU_SKIN, Skin.class);
            else if (type == 1)
                skin = manager.get(Constants.CALC_SKIN, Skin.class);
            else
                skin = manager.get(Constants.SUPPLY_MENU_SKIN, Skin.class);

            return skin.getAll(BitmapFont.class).values().toArray();
        }


        //returns an array of fonts
        public static Array<BitmapFont> getAllFonts(){
            Array<BitmapFont> fonts = new Array<BitmapFont>();

            Array<Skin> skinArray = new Array<Skin>();
            manager.getAll(Skin.class, skinArray);

            for (Skin skin : skinArray){
                ObjectMap<String, BitmapFont> fontMap = skin.getAll(BitmapFont.class);
                fonts.addAll(fontMap.values().toArray());
            }

            return fonts;
        }

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
