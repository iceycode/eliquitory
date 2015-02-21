package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Flavor;
import com.icey.apps.ui.CalcTable;
import com.icey.apps.ui.CalcWindow;
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
 *
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
 * * see CalcTable/FlavorTable for details
 *  TODO: center messageText labels - figure out how to do this
 * TODO: expand recipe title name
 *
 * TODO: create a popup window for final amounts instead of keeping in table
 *            
 *  TODO: menu buttons need to be offset a little
 *     TODO: load needs to be cenetered, back button goes to left side
 *
 * TODO: increase size of percent fields a bit
 * TODO; set a selectbox for selecting "Other"
 * TODO: specify error (which flavor or percent) in popup
 *
 */
public class CalculatorScreen implements Screen {

    Skin skin; //skin
    Stage stage; //main stage

    //Tables which hold UI widgets, get added to stage
    CalcTable table;  //root table
    //FlavorTable flavorTable; //flavorTable - holds flavors
    ScrollPane scroll; //ScrollPane for flavorTable

    String errorMsg = Constants.ERROR_MAIN;
    String[] errorMsgs = Constants.ERROR_MSGS; //0=flavor, 1=desired percents, 2 = base percents

    LoadWindow loadWindow;
    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //tool used for calculations, loading, saving

    public Button backButton; //back button


    public CalculatorScreen(){
        skin = Assets.manager.get(Constants.CALC_SKIN, Skin.class);

        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        setCalcTable();

        setAmountsWindow();
    }


    /** sets up the main table
     * - main table should take care of all scaling
     */
    public void setCalcTable(){
        table = new CalcTable(skin);

//        setFlavorScrollTable();

        setButtons();
        setLoadWindow();

        stage.addActor(table);
    }

//    //a nested table with a scrollpane in it
//    protected void setFlavorScrollTable(){
//
//        flavorTable = new FlavorTable(skin);
//
//        ScrollPane scroll = new ScrollPane(flavorTable, skin); //create scrollabel flavor table
//
//        table.add(scroll).width(480).height(200).colspan(6); //add to the outer table
//        table.row();
//    }


    boolean loadWindowSetup = false;
    protected void setLoadWindow(){

        if (!loadWindowSetup){
            loadWindow = new LoadWindow("Saved Recipes", skin, "load");
            stage.addActor(loadWindow);
        }
        else{
            loadWindow.updateRecipes(calcUtils.getAllRecipes());
        }

    }


    //the of of each liquid &
    protected void setAmountsWindow(){

    }


    //shows the amounts window
    protected void showFinalAmounts(){

    }


    protected void setButtons(){
        //the flavor button
        final TextButton flavorButton = new TextButton("Add flavor", skin, "flavor");
        flavorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flavorButton.isPressed()) 
                    table.flavorTable.addNewFlavor(new Flavor("Flavor " + table.flavorTable.numFlavors));//add new flavor
            }
        });

        table.add(flavorButton).width(200).height(50).colspan(6).align(Align.center);
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

        table.add(calcButton).width(200).height(50).colspan(6).align(Align.center);
        

        setMenuButtons();

    }


    protected void setMenuButtons(){
        table.row().pad(5);
        
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
        table.add(saveButton).width(100).align(Align.center);


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
        table.add(loadButton).width(100).align(Align.center);


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
        table.add(backButton).width(100).align(Align.left);
    }


    /** returns an error dialog which gets added to stage
     *
     * @param message
     * @param detail
     */
    protected void setErrorDialog(String message, String detail){
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

//
//    //info about widgets
//    protected void getTableWidgetInfo() throws NullPointerException{
//        for (Actor a : table.getChildren()){
////            a.setScale(MainApp.scaleX, MainApp.scaleY);
//            log ("Actor name: " + a.getName());
//            log ("Actor class: " + a.getClass().getSimpleName());
//            log("Table Actor position: " + "(" + a.getX() + ", " + a.getY() + ")" + "\n" +
//                    "Actor Actor size: " + a.getWidth() + " x " + a.getHeight());
//        }
//    }


    public void calculate(){
        //calculate amounts if touchdown
        if (!calcUtils.areFlavorsSet()){
            log("flavors not set!");
            setErrorDialog(errorMsgs[0], calcUtils.getError());
        }
        else if (!calcUtils.areDesiredAt100()){
            log("desired percents do not add up to 100");
            setErrorDialog(errorMsgs[1], calcUtils.getError());
        }
        else if (!calcUtils.areBaseAt100()){
            log("base percents do not add up to 100");
            setErrorDialog(errorMsgs[2], calcUtils.getError());
        }
        else{
            log("calculating now...");
            calcUtils.calcAmounts();
            displayResults();
        }
    }
    
    
    //display the calculated results
    protected void displayResults(){
        //table.updateCalcLabels();
        //flavorTable.updateCalcLabels();
        
//        if (calcUtils.updatedSupply){
//            table.updateSupplyLabels();
//            flavorTable.updateSupplyLabels();
//        }
        new CalcWindow(skin, calcUtils.getFinalMills(), calcUtils.getFlavors()).show(stage);
    }
    
    
    //display data that is loaded
    private void displayLoadedData(){
        
        table.setLoadedRecipe();
        
        //set up the flavors
        for (Flavor f : calcUtils.getFlavors()){
            if (calcUtils.getFlavors().size > table.flavorTable.numFlavors)
                table.flavorTable.addNewFlavor(new Flavor("Flavor " + table.flavorTable.numFlavors));
            
            for (TextField tf: table.flavorTable.flvrTitleTFs)
                tf.setMessageText(f.getName());
            
            for (TextField tf : table.flavorTable.flvrPercsTFs)
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
        //restore the stage's viewport; true updates camera to 0,0 - do not need this
        stage.getViewport().update(width, height, false);

        table.invalidateHierarchy();
        table.setSize(width, height);

        log("Resized screen");
    }


//    protected void getTableWidgetInfo(){
//        for (Actor a : table.getChildren()){
//            log("Positions before scaling:  (" + a.getX()+ ", " + a.getY() + ")");
//            a.setScale(table.getScaleX(), table.getScaleY());
//
//            log("Positions AFTER scaling:  (" + a.getX()+ ", " + a.getY() + ")");
//        }
//    }

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
