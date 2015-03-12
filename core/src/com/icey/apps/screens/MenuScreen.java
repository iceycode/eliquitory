package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.UIUtils;

/** A simple menu screen
 * NOTE: in the lite version, menu does not exist
 * TODO: add ability to connect with FB, google+, email (others maybe)
 *
 * Created by Allen on 1/11/15.
 */
public class MenuScreen implements Screen{

//    float SCREEN_WIDTH = Constants.SCREEN_WIDTH*MainApp.scaleX;
//    float SCREEN_HEIGHT = Constants.SCREEN_HEIGHT* MainApp.scaleY;

    //dimensions & positions of labels, textbutton
    private final float[] TITLE_SIZE = Constants.MENU_TITLE_SIZE;

    //Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class);
    Skin skin = Assets.getSkin();

    OrthographicCamera camera;
    ScalingViewport viewport;
    Stage stage;
    Table table;

    boolean supplyEnabled; //this is set here to see if main app is enabled


    public MenuScreen(){

        //set the viewport
        viewport = new ScalingViewport(Scaling.fill, Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);

        //set the stage
        stage = new Stage(viewport);

//        stage = new Stage(new ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
//        this.supplyEnabled = MainApp.supplyEnabled;

        //set the table which contains menu UI (just buttons)
        setTable();
    }


    protected void setTable(){
        table = new Table();

        //table.setFillParent(true);
        table.setClip(true);
        table.setBounds(0, 50, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT - 50);
        table.center();

        table.setBackground(new TextureRegionDrawable(new TextureRegion(skin.getRegion("background"))));

        setTitleLabel(); //title added to table
        setCalcButton(); //calc button added

        setDropFields();
        //setSupplyButton(); //supply button added
        //setSettingsButton();

        setInfoButton();

        stage.addActor(table);
    }

    //sets up the main title label
    protected void setTitleLabel(){
        //title label
        Label title = new Label("EJuice Toolkit", skin, "title");
        title.setAlignment(Align.center);
        title.setSize(TITLE_SIZE[0], TITLE_SIZE[1]);

        table.add(title).width(title.getWidth()).height(title.getHeight()).center().fill().colspan(3);
        table.row().padBottom(25).padTop(25).center();
    }


    protected void setCalcButton(){

        //button which sends user to calculator screen
        final TextButton calcButton = new TextButton("CALCULATOR", skin, "large");

        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed())
                    MainApp.setState(1);
            }
        });

        table.add(calcButton).width(calcButton.getWidth()).height(calcButton.getHeight()).center().colspan(3);

    }

//    //button which sends user to RecipeScreen - to see recipes made
//    protected void setRecipeButton(){
//        table.row().padBottom(25);
//
//        final TextButton settingsButton = new TextButton("SETTINGS", skin, "large");
//        settingsButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (settingsButton.isPressed()){
//                    MainApp.setState(3);
//                }
//            }
//        });
//
//        table.add(settingsButton).width(settingsButton.getWidth()).height(settingsButton.getHeight()).center();
//    }


//    //sets supply button - if supplies not enabled, makes button disabled & changes appearance of it
//    protected void setSupplyButton(){
//        table.row().fill().padBottom(25);
//
//        final TextButton supplyButton;
//
//        if (MainApp.supplyEnabled){
//            //button sends user to supply input screen
//            supplyButton = new TextButton("SUPPLIES", skin, "large");
////            supplyButton.setSize(BTN_SIZE[0], BTN_SIZE[1]);
//            supplyButton.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    if (supplyButton.isPressed())
//                        MainApp.setState(2);
//                }
//            });
//        }
//        else{
//            supplyButton = new TextButton("SUPPLIES DISABLED\n(See Settings)", skin, "disabled-med");
//            supplyButton.setDisabled(true);
//        }
//
//
//        table.add(supplyButton).width(supplyButton.getWidth()).height(supplyButton.getHeight()).center();
//
//    }

//
//    protected void setSettingsButton(){
//        table.row().padBottom(25);
//
//        final TextButton settingsButton = new TextButton("SETTINGS", skin, "large");
//        settingsButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (settingsButton.isPressed()){
//                    MainApp.setState(3);
//                }
//            }
//        });
//
//        table.add(settingsButton).width(settingsButton.getWidth()).height(settingsButton.getHeight()).center();
//    }


    protected void setDropFields(){
        //ROW 2 : drops per ml
        table.row().padBottom(100).padTop(25);

        Label label = new Label("Drops per ml: ", skin);
        table.add(label).right().height(25);

        TextField textField = new TextField("", skin, "digit");
        textField.setMaxLength(2);
        textField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
//        textField.setTextFieldListener(UIUtils.SettingsUI.dropsListener());
        textField.setAlignment(Align.center);
        textField.setText(Double.toString(MainApp.saveManager.getDropsPerML()));

        UIUtils.setDialogKeyboard(textField, 3, "Drops per ml", "Enter drops per ml", 0);

        table.add(textField).left().width(50).height(25).padLeft(2);

        final Slider slider = new Slider(10, 60, 1, false, skin, "large-horizontal");
        slider.setValue((float)MainApp.saveManager.getDropsPerML());
        slider.addListener(UIUtils.SettingsUI.dropsSliderListener(slider, textField));

        table.add(slider).width(110).height(25);
    }


//    protected void setDefaultFields(){
//        table.row().padBottom(100).padTop(25);
//
//        Label label = new Label("Drops per ml: ", skin);
//        table.add(label).right();
//
//        TextField textField = new TextField("", skin, "digit");
//        textField.setMaxLength(2);
//        textField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
////        textField.setTextFieldListener(UIUtils.SettingsUI.dropsListener());
//        textField.setAlignment(Align.center);
//        textField.setText(Double.toString(MainApp.saveManager.getDropsPerML()));
//        UIUtils.setDialogKeyboard(textField, 3, "Drops per ml", "Enter drops per ml", 0);
//
//        table.add(textField).left().width(50).height(25).padLeft(2);
//
//        final Slider slider = new Slider(10, 60, 1, false, skin);
//        slider.setValue((float)MainApp.saveManager.getDropsPerML());
//        slider.addListener(UIUtils.SettingsUI.dropsSliderListener(slider, textField));
//
//        table.add(slider).width(110).height(25);
//    }


    protected void setInfoButton(){
        table.row().padTop(100);

        //button which sends user to calculator screen
        final TextButton aboutButton = new TextButton("Info", skin, "large");

        aboutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (aboutButton.isPressed())
                    MainApp.setState(2); //FIXME: in pro version, this is 4
            }
        });

        table.add(aboutButton).width(aboutButton.getWidth()).height(aboutButton.getHeight()).center().colspan(3);
    }


//    public void updateAppFeatures(){
//        Array<Actor> children = table.getChildren();
//
//        final TextButton supplyButton = new TextButton("SUPPLIES", skin, "large");
////            supplyButton.setSize(BTN_SIZE[0], BTN_SIZE[1]);
//        supplyButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if (supplyButton.isPressed())
//                    MainApp.setState(2);
//            }
//        });
//
//        children.set(2, supplyButton);
//    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

//        if (supplyEnabled!= MainApp.supplyEnabled){ //FIXME: in pro version enable
//            updateAppFeatures();
//        }

        stage.act();
        stage.draw();

//        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
//            new Dialog("", skin){
//                @Override
//                protected void result(Object object) {
//                    if ((Boolean)object){
//                        MainApp.exitApp = true;
//                    }
//                }
//            }.text("Exit Application?").button("Yes", true).button("No", false).show(stage);
//        }
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
        stage.getViewport().update(width, height, false);

        table.invalidateHierarchy();
        table.setSize(width, height);

        log("Resized Screen; new size : " + width + "x" + height);
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
