package com.icey.apps.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.icey.apps.utils.Constants;

/**
* Created by Allen on 2/23/15.
*/
public class AssetHelper {

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


    public static BitmapFont createBMFont(String path, int size){
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        return font;
    }


    /** gets fonts in skins based on screen
     *  - rescales the fonts
     * @param type
     * @return
     */
    public static Array<BitmapFont> getScreenFonts(int type){
        Skin skin;

        if (type == 0)
            skin = Assets.manager.get(Constants.MENU_SKIN, Skin.class);
        else if (type == 1)
            skin = Assets.manager.get(Constants.CALC_SKIN, Skin.class);
        else
            skin = Assets.manager.get(Constants.SUPPLY_MENU_SKIN, Skin.class);

        return skin.getAll(BitmapFont.class).values().toArray();
    }


    //returns an array of fonts
    public static Array<BitmapFont> getAllFonts(){
        Array<BitmapFont> fonts = new Array<BitmapFont>();

        Array<Skin> skinArray = new Array<Skin>();
        Assets.manager.getAll(Skin.class, skinArray);

        for (Skin skin : skinArray){
            ObjectMap<String, BitmapFont> fontMap = skin.getAll(BitmapFont.class);
            fonts.addAll(fontMap.values().toArray());
        }

        return fonts;
    }

    //smoothes the fonts in the skin
    //NOTE: this seems to help a lot with fonts
    public static void smoothFonts() {

        Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class);

        for (BitmapFont font : skin.getAll(BitmapFont.class).values()){
            font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }
}
