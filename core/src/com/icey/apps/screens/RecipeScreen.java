package com.icey.apps.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.SaveManager;

/** Recipe Screen
 * - shows users recipes
 * - allows user to see particular amounts for each one
 * - allows user to rate the recipe as well
 *
 * Created by Allen on 2/23/15.
 */
public class RecipeScreen implements Screen {

//    Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class);
    Skin skin = Assets.getSkin();

    Stage stage;
    Table table;

    SaveManager saveManager = MainApp.saveManager;
    Array<SaveManager.RecipeData> recipes;


    public RecipeScreen(){
        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
    }

    protected void setTable(){
        table = new Table();

        table.setClip(true);



    }

    protected void setTitle(){
        Label title = new Label("Saved Recipes", skin, "title");
        title.setSize(225, 100);

        table.add(title).colspan(3);
    }



    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
