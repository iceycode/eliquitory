package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.SaveManager;
import com.icey.apps.utils.UIUtils;

/** Settings Screen
 * - contains ability to set Drops/ML
 * - also has ability to disable ads & unlock supply feature
 *
 * TODO: implement ability to unlock features
 *
 * TODO: implement ability to set drops per ml
 * TODO (maybe): in future add ability to connect to FB, Google Plus, add vendors for supplies
 *
 * Created by Allen on 2/19/15.
 */
public class SettingScreen implements Screen {

    Stage stage;
    Table table; //main table for buttons/settings

    Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class);

    SaveManager saveManager = MainApp.saveManager;
    Preferences settings;


    public SettingScreen(){

        stage = new Stage(new ExtendViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        settings = saveManager.getPrefs();

        setTable();
    }


    public void setTable(){
        table = new Table();
        table.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        table.top();
        table.setClip(true);
        table.setFillParent(true);

        table.setBackground(skin.getDrawable("background"));

        Label title = new Label("SETTINGS", skin, "title");
        title.setAlignment(Align.center);
        table.add(title).width(300).height(100).padBottom(50).colspan(3).padTop(25);

        setDrops();
        setButtons();

        stage.addActor(table);
    }

    protected void setDrops(){

        //ROW 2 : drops per ml
        table.row().padBottom(100);

        Label label = new Label("Drops per ml: ", skin);
        table.add(label).right();

        TextField textField = new TextField("", skin, "digit");
        textField.setMaxLength(2);
        textField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        textField.setTextFieldListener(UIUtils.SettingsUI.dropsListener());
        textField.setAlignment(Align.center);

        table.add(textField).left().width(50).height(25).padLeft(2);


        final Slider slider = new Slider(20, 40, 1, false, skin);
        slider.setValue((float)Constants.DROPS_PER_ML);
        slider.addListener(UIUtils.SettingsUI.dropsSliderListener(slider, textField));

        table.add(slider).width(110).height(25);
    }


    //sets the buttons on table
    public void setButtons(){
        //ROW 3 disable ads button
        table.row().padTop(100);

        setFeatureButton(!MainApp.adsEnabled, 0);

        //ROW 4 : enable supply button
        table.row().pad(10);

        setFeatureButton(MainApp.supplyEnabled, 1);


        //ROW 5 - back button (last rows)
        table.row().padTop(100);
//        final Button backButton = new Button(skin);
        final TextButton backButton = UIUtils.Buttons.textButton("Back", skin, "default");
        backButton.addListener(UIUtils.backTextButtonListener(backButton));


        table.add(backButton).width(100).right().colspan(3);
    }

    /** sets up the feature button - enabled or disabled
     *
     * @param feature : if true, button is disabled (ads off or supply enabled)
     * @param type : the type of feature (ad or supply)
     */
    protected void setFeatureButton(boolean feature, final int type){
        final TextButton featureButton;

        if (feature){
            featureButton = new TextButton("Disable Ads", skin, "disabled-med");
            featureButton.setDisabled(true);
            table.add(featureButton).width(150).height(75).colspan(3).center();
        }
        else{
            featureButton = new TextButton("Disable Ads", skin, "medium");
            featureButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (featureButton.isPressed()){
                        if (type == 0)
                            saveManager.saveAdState(false);
                        else
                            saveManager.saveSupplyState(false);
                    }
                }
            });
        }

        table.add(featureButton).width(150).height(75).colspan(3).center();
    }


    //updates features buttons to disabled
    public void updateFeatureButtons(){


    }



    //TODO: set up the in-app payment method/API
    protected void purhcaseFeature(int type){
        if (type == 0){

        }
        else{

        }
    }


    @Override
    public void show() {
        Gdx.gl.glClearColor(.2f, .4f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }



    @Override
    public void render(float delta) {

        show();
    }



    @Override
    public void resize(int width, int height) {
        //restore the stage's viewport; true updates camera to 0,0 - do not need this
        stage.getViewport().update(width, height, false);

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

    }


    private void log(String message){
        Gdx.app.log("SupplyScreen log: ", message);
    }
}
