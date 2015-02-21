package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;

/** A simple menu screen
 * TODO: add ability to connect with FB, google+, email (others maybe)
 *
 * Created by Allen on 1/11/15.
 */
public class MenuScreen implements Screen{


    //dimensions & positions of labels, textbutton
    private final float[] TITLE_POS = Constants.MENU_TITLE_POS;
    private final float[] TITLE_SIZE = Constants.MENU_TITLE_SIZE;

    private final float[] BTN_SIZE = Constants.MENU_BTN_SIZE;

    Skin skin;
    Stage stage;
    Table table;

    public MenuScreen(){
        setStage(); //setup the stage
        setTable();
    }


    protected void setStage(){
        this.skin = Assets.manager.get(Constants.MENU_SKIN, Skin.class);

        //stage = new Stage(new ScalingViewport(Scaling.fill, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
    }

    protected void setTable(){
        table = new Table();

        table.setClip(true); //this may the answer to scaling problem
        table.setBounds(0, 50, Constants.SCREEN_WIDTH-50, Constants.SCREEN_HEIGHT);
        table.setLayoutEnabled(true); //need this for invalidating hierarchy

        //setBackground(); //set the background as Image (actor) on Stage

        //Texture background = Assets.manager.get(Constants.MENU_BACKGROUND_FILE, Texture.class);
        table.setBackground(new TextureRegionDrawable(new TextureRegion(skin.getRegion("background"))));

        table.center();

        setTitleLabel(); //title added to table
        setCalcButton(); //calc button added
        setSupplyButton(); //supply button added
        setSettingsButton();

        stage.addActor(table);

    }

    //sets up the main title label
    protected void setTitleLabel(){
        //title label
        Label title = new Label("E-Liquid CALC", skin);
        title.setAlignment(Align.center);
        title.setSize(TITLE_SIZE[0], TITLE_SIZE[1]);

        table.add(title).width(title.getWidth()).height(title.getHeight()).center().fill();
        table.row().padBottom(75).padTop(150f).center();
    }


    protected void setCalcButton(){


        //button which sends user to calculator screen
        final TextButton calcButton = new TextButton("CALCULATOR", skin, "calc");
        calcButton.setSize(BTN_SIZE[0], BTN_SIZE[1]);
//        calcButton.setScale(scaleX, scaleY);

        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed())
                    MainApp.setState(1);
            }
        });

        table.add(calcButton).width(calcButton.getWidth()).height(calcButton.getHeight()).center();
        table.row().fill().padBottom(25);
    }

    //sets supply button - if supplies not enabled, makes button disabled & changes appearance of it
    protected void setSupplyButton(){
        final TextButton supplyButton;

        if (MainApp.supplyEnabled){
            //button sends user to supply input screen
            supplyButton = new TextButton("SUPPLIES", skin, "supply");
            supplyButton.setSize(BTN_SIZE[0], BTN_SIZE[1]);
            supplyButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                if (supplyButton.isPressed())
                    MainApp.setState(2);
                }
            });
        }
        else{
            supplyButton = new TextButton("SUPPLIES DISABLED\n(Enable Feature in Settings)", skin, "supplyOff");
            supplyButton.setDisabled(true);
        }


        table.add(supplyButton).width(supplyButton.getWidth()).height(supplyButton.getHeight()).center();

    }


    protected void setSettingsButton(){
        table.row();

        final TextButton settingsButton = new TextButton("SETTINGS", skin, "settings");
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (settingsButton.isPressed()){
                    MainApp.setState(3);
                }
            }
        });

        table.add(settingsButton).width(settingsButton.getWidth()).height(settingsButton.getHeight()).center();
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        stage.act();
        stage.draw();

    }


    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        //Set the viewport to the whole screen.
        // this prevents blackbars from being shown
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //restore the stage's viewport; true updates camera to center
        stage.getViewport().update(width, height, true);
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
    
    private void log(String message){
        Gdx.app.log("MenuScreen LOG: ", message);
    }
}
