package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Flavor;
import com.icey.apps.ui.CalcTable;
import com.icey.apps.ui.FlavorTable;
import com.icey.apps.ui.LoadWindow;
import com.icey.apps.utils.CalcUtils;

/** the main calculator app
 *
 * 3 main categories of info to fill out:
 * 1) Amount desired: target amount, strength, percents
 * 2) Base nicotine amount/strength, percents
 * 3) Flavors: names, percents
 * 
 * The Calculations for the amount of nicotine needed
 * (Desired Strength / Concentrated Nicotine Strength) x Bottle size = Amount needed (in milliliters)
 * 
 * For flavor calculations:
 *  flavorPercent/100 * AmountTotal
 *
 * For amount of PG, VG & Other:
 *  AmountTotal*(desired PG%/100) - BaseNeeded*(Base PG/100)
 *  AmountTotal * (desired VG/100) - BaseNeeded*(Base VG/100)
 *  ...
 *
 * STYLES used from skin
 * textbutton styles: flavor, menu;
 * scrollpane style: default
 * labelstyles: default, titleLabel, goalLabel, baseLabel, calcsLabel, flavorLabel
 * textfieldstyle: numTextField, nameTextField
 * font: default-font
 * scrollPane: default
 * 
 * Two Tables: FlavorTable & CalcTable classes
 *   - CalcTable is the main table, FlavorTable is a scrollable Table in CalcTable
 * 
 *
 * Created by Allen on 1/10/15.
 *
 * --------TODOs for Calculator-------
 * TODO: fix table properties/alignments
 *  TODO: fix subtitle sizing (it is off screen)
 *  TODO: fix menu buttons alignments
 *
 * TODO: fix Widgets
 * TODO: specify error (which flavor or percent) in popup
 *
 */
public class CalculatorScreen implements Screen {

    Skin skin;
    Stage stage; //main stage
    CalcTable table;
    FlavorTable flavorTable;

    String errorMsg = Constants.ERROR_MAIN;
    String[] errorMsgs = Constants.ERROR_MSGS; //1=flavor, 2=desired percents, 3 = base percents
    
    LoadWindow loadWindow;
    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //tool used for calculations, loading, saving

    public Button backButton; //back button

    public CalculatorScreen(){
        skin = Assets.manager.get(Constants.CALC_SKIN, Skin.class);

        stage = new Stage();

        stage.addActor(new Image(Assets.manager.get(Constants.CALC_BACKGROUND, Texture.class))); //background as Image
        setCalcTable();
    }

    
    public void setCalcTable(){
        table = new CalcTable(skin, stage);
        
        setFlavorScrollTable();

        setButtons();
        setLoadWindow();

        stage.addActor(table);
    }

    //a nested table with a scrollpane in it
    private void setFlavorScrollTable(){
        //---FLAVORS--- third category: initially adds 1 FLavor by name of name
        Label flavorLabel = new Label(" Flavor(s)", skin, "flavorLabel");
        table.add(flavorLabel).width(300).height(50).align(Align.center).colspan(4);
        table.row();
        
        flavorTable = new FlavorTable(skin);

        ScrollPane scroll = new ScrollPane(flavorTable, skin); //create scrollabel flavor table
        table.add(scroll).width(480).height(200).colspan(4); //add to the outer table
        table.row();

    }


    boolean loadWindowSetup = false;
    private void setLoadWindow(){

        if (!loadWindowSetup){
            loadWindow = new LoadWindow("Saved Recipes", skin, "load");
            stage.addActor(loadWindow);
        }
        else{
            loadWindow.updateRecipes(calcUtils.getAllRecipes());
        }

    }

    private void setButtons(){
        //the flavor button
        final TextButton flavorButton = new TextButton("Add flavor", skin, "flavor");
        flavorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flavorButton.isPressed())
                    flavorTable.addNewFlavor();//add new flavor
            }
        });

        table.add(flavorButton).width(200).height(50).colspan(4).align(Align.center);
        table.row();

        //the calculator button
        final TextButton calcButton = new TextButton("Calculate!", skin);
        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed())
                    calculate();
            }
        });

        table.add(calcButton).width(200).height(50).colspan(3).align(Align.center);
        table.row();

        setMenuButtons();

    }

    private void setMenuButtons(){
        //the save button
        TextButton saveButton = new TextButton("Save", skin, "menu");
        saveButton.addListener(new InputListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                calcUtils.saveData();
                if (calcUtils.saved = true){
                    log("wrong values!");
                    new Dialog("", skin){
                        protected void result(Object object) {
                            outsideDialog(this);
                        }
                    }.text("ERROR SAVING:\n").text("Wrong Values Entered or lacking values!")
                            .button("Fix it!").key(Input.Keys.ESCAPE, false).key(Input.Keys.ENTER, true).
                            show(stage); //, Actions.fadeOut(2f)
                }

                return true;
            }
        });
        table.add(saveButton);


        //the load button
        final TextButton loadButton = new TextButton("Load", skin, "menu");
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (loadButton.isPressed()){
                    loadWindow.updateRecipes(calcUtils.getAllRecipes());
                    loadWindow.showWindow();
                }
            }
        });
        table.add(loadButton);


        //the back button
        final Button backButton = new Button(skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (backButton.isPressed()){
                    MainApp.setState(MainApp.prevState);
                }
            }
        });
        table.add(backButton).width(30);
    }

    public void setErrorDialog(String message){
        Dialog errorDialog = new Dialog("", skin){
            protected void result(Object object) {
                outsideDialog(this);
            }
        };
        errorDialog.getContentTable().add(new Label(errorMsg, skin)).align(Align.center);
        errorDialog.getContentTable().row();
        errorDialog.setMovable(true);

        errorDialog.button("Fix it");
        errorDialog.getButtonTable().setHeight(30f);

        errorDialog.text(message).show(stage); //, Actions.fadeOut(2f)
    }


    public void calculate(){
        //calculate amounts if touchdown
        if (!calcUtils.areFlavorsSet()){
            log("flavors not set!");
            setErrorDialog(errorMsgs[0]);
        }
        else if (!calcUtils.areDesiredAt100()){
            log("desired percents do not add up to 100");
            setErrorDialog(errorMsgs[1]);
        }
        else if (!calcUtils.areBaseAt100()){
            log("base percents do not add up to 100");
            setErrorDialog(errorMsgs[2]);
        }
        else{
            log("calculating now...");
            calcUtils.calcAmounts();
            displayResults();
        }
    }
    
    
    //display the calculated results
    private void displayResults(){
        for (int i = 0; i < 4; i++){
            String text = String.format("%.1f", calcUtils.getFinalMills().get(i))+" (" +
                    (int)(calcUtils.getFinalMills().get(i).doubleValue()*20)+")";
            table.calcLabels.get(i).setText(text);
            
            if (i > 4)
                flavorTable.calcLabels.get(i).setText(text);
        }
    }
    
    
    //display data that is loaded
    private void displayLoadedData(){
        
        table.titleTextField.setMessageText(calcUtils.getRecipeName());
        table.amtDesTextField.setMessageText(Double.toString(calcUtils.getAmountDesired()));
        table.strTextField.setMessageText(Double.toString(calcUtils.getStrengthDesired()));
        table.baseStrTF.setMessageText(Double.toString(calcUtils.getBaseStrength()));
        
        //set up the percent text fields
        Array<Integer> percents = new Array<Integer>();
        for (Integer percent: calcUtils.getDesiredPercents())
            percents.add(percent);
        for (Integer percent : calcUtils.getBasePercents())
            percents.add(percent);
        
        for (int i = 0; i < percents.size; i ++){
            table.percentTextFields.get(i).setMessageText(Double.toString(percents.get(i)));
        }
        
        //set up the flavors
        for (Flavor f : calcUtils.getFlavors()){
            if (calcUtils.getFlavors().size > flavorTable.flvrTitleTFs.size)
                flavorTable.addNewFlavor();
            
            for (TextField tf: flavorTable.flvrTitleTFs)
                tf.setMessageText(f.getName());
            
            for (TextField tf : flavorTable.flvrPercsTFs)
                tf.setMessageText(Double.toString(f.getPercent()));
        }

        displayResults(); //display the loaded recipe results
    }


    //if clicked outside of dialog
    public void outsideDialog(Dialog dialog){
        //dismisses if click anywhere on outside of dialog box
        if (Gdx.input.isTouched()){
            if (Gdx.input.getX() > (dialog.getX() + dialog.getWidth()) || Gdx.input.getX() < dialog.getX()) {
                dialog.hide();
                dialog.remove();
            }
            if (Gdx.input.getY() < dialog.getY() || Gdx.input.getY() > (dialog.getY() + dialog.getHeight())) {
                dialog.hide();
                dialog.remove();
            }
        }
    }



    @Override
    public void show() {
        Gdx.gl.glClearColor(.2f, .4f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);


        //if a recipe is chosen in loadwindow, hide it, display results
        if (loadWindow.recipeChosen) {
            loadWindow.hideWindow();
            displayLoadedData();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
        stage.dispose();
    }
    
    private void log(String message){
        Gdx.app.log("CalcScreen LOG", message);
    }



}
