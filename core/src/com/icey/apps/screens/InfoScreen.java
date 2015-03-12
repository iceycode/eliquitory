package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.icey.apps.assets.Assets;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.UIUtils;

/** A screen dedicated to info about the app
 *
 * Created by Allen on 3/4/15.
 */
public class InfoScreen implements Screen{

    Skin skin = Assets.getSkin();
    Stage stage; //stage
    Table table; //main table


    public InfoScreen(){
        stage = new Stage(new ScalingViewport(Scaling.fill, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        setTable();

        stage.addActor(table);
    }

    //setup the table
    protected void setTable(){
        table = new Table();

        table.setClip(true); //this may the answer to scaling problem
        table.setBounds(0, 50, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT-50);
        table.center();

        table.setBackground(new TextureRegionDrawable(new TextureRegion(skin.getRegion("background"))));

        Label title = new Label("ABOUT", skin, "title");
        title.setAlignment(Align.center);
        table.add(title).width(300).height(100).padBottom(50).colspan(3).padTop(25);

        table.row().padTop(50);

        Label label = new Label(Constants.InfoText.ABOUT_LITE_V1, skin);
        label.setWrap(true); //wraps text

        table.add(label).left();

        table.row().padTop(10);

        Label featureLabel = new Label(Constants.InfoText.FEATURES_LITE_V1, skin);
        featureLabel.setWrap(true);

        table.add(featureLabel).left();


        table.row().padTop(150);
        setButtons();
    }


    //sets the buttons on table
    public void setButtons(){


//        final TextButton calcButton = new TextButton("Calculator", skin);
//        calcButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (calcButton.isPressed())
//                    MainApp.setState(1);
//            }
//        });
//
//        table.add(calcButton).width(100).center();

        final TextButton backButton = UIUtils.Buttons.textButton("Back", skin, "medium");
        backButton.addListener(UIUtils.backTextButtonListener(backButton));

        table.add(backButton).width(100).right().bottom();
    }

    @Override
    public void show() {
        Gdx.gl.glClearColor(37/255f, 37/255f, 37/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        stage.act();
        stage.draw();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        //restore the stage's viewport; true updates camera to center
        stage.getViewport().update(width, height, false);
        //stage.getCamera().update();//updates camera

        table.invalidateHierarchy();
        table.setSize(width, height);
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
        stage.dispose();
    }
}
