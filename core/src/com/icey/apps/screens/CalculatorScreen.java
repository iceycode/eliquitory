package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.icey.apps.assets.Assets;
import com.icey.apps.data.Flavor;
import com.icey.apps.ui.CalcTable;
import com.icey.apps.ui.LoadWindow;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.UIUtils;

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

    //Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class); //skin

    Skin skin = Assets.getSkin();

    Stage stage; //main stage

    //Tables which hold UI widgets, get added to stage
    CalcTable table;  //root table
    //FlavorTable flavorTable; //flavorTable - holds flavors
    ScrollPane scroll; //ScrollPane for flavorTable

    Label errorTitle;
    String[] errorMsgs = Constants.ERROR_MSGS; //0=flavor, 1=desired percents, 2 = base percents

    LoadWindow loadWindow;
    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //tool used for calculations, loading, saving

    public Button backButton; //back button
    SnapshotArray<Double> finalMills;


    public CalculatorScreen(){
        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        setCalcTable();

        errorTitle = new Label(Constants.ERROR_MAIN, skin, "tab");
        errorTitle.setAlignment(Align.center);
    }


    /** sets up the main table
     * - main table should take care of all scaling
     */
    public void setCalcTable(){
        table = new CalcTable(skin);

        setButtons();
        setLoadWindow();

        stage.addActor(table);
    }


    boolean loadWindowSetup = false;
    protected void setLoadWindow(){

        if (!loadWindowSetup){
            loadWindow = new LoadWindow(skin, "window-large");
        }
        else{
            loadWindow.addRecipes(calcUtils.getAllRecipes());
        }

    }


    protected void setButtons(){
        //the flavor button
        final TextButton flavorButton = new TextButton("Add Flavor", skin, "rounded");
        flavorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flavorButton.isPressed()) 
                    table.flavorTable.addNewFlavor(new Flavor("Flavor " + table.flavorTable.numFlavors));//add new flavor
            }
        });

        table.add(flavorButton).width(200).height(50).colspan(table.cols).center().padBottom(5);
        table.row();

        //the calculator button
//        final TextButton calcButton = new TextButton("Calculate!", skin);
        final TextButton calcButton = new TextButton("Calculate!", skin, "medium");
        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed())
                    calculate();
            }
        });

        table.add(calcButton).width(200).height(50).colspan(table.cols).center().padBottom(5);
        

        setMenuButtons();

    }


    protected void setMenuButtons(){
        table.row().pad(5);
        
        //the save button
        final TextButton saveButton = new TextButton("Save", skin, "medium");
        saveButton.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               saveRecipe();
           }
        });

        table.add(saveButton).width(100).align(Align.center);


        //the load button
//        final TextButton loadButton = new TextButton("Load", skin, "menu");
        final TextButton loadButton = new TextButton("Load", skin, "medium");
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (loadButton.isPressed()){
                    loadWindow.addRecipes(calcUtils.getAllRecipes());
                    loadWindow.show(stage);
                }
            }
        });
        table.add(loadButton).width(100).colspan(2).align(Align.center);


        //the back button
//        final Button backButton = new Button(skin);
//        final TextButton backButton = UIUtils.Buttons.textButton("BACK", skin, "menu");
        final TextButton backButton = UIUtils.Buttons.textButton("Back", skin, "medium");
        backButton.addListener(UIUtils.backTextButtonListener(backButton));
        table.add(backButton).width(100).align(Align.left);
    }



    /** returns an error dialog which gets added to stage
     *
     * @param message
     * @param detail
     */
    protected void showErrorDialog(String message, String detail){
        Dialog errorDialog = new Dialog("", skin);
        errorDialog.getContentTable().top();
        errorDialog.getContentTable().add(errorTitle).fillX().expandX().center();

        errorDialog.getContentTable().row();
        errorDialog.getContentTable().add(new Label(message, skin)).center();

        errorDialog.getContentTable().row();
        errorDialog.getContentTable().add(new Label(detail, skin)).center();

        errorDialog.setMovable(true);

        errorDialog.getButtonTable().setHeight(40);
        errorDialog.button("Fix it");

        errorDialog.show(stage); //, Actions.fadeOut(2f)
    }



    public void calculate(){
        //calculate amounts if touchdown
        if (!calcUtils.areFlavorsSet()){
            log("flavors not set!");
            showErrorDialog(errorMsgs[0], calcUtils.getError());
        }
        else if (!calcUtils.isGoalSet()){
            showErrorDialog(errorMsgs[3], "");
        }
        else{
            log("calculating now...");
            finalMills = calcUtils.calcAmounts();
            table.updateCalcLabels(calcUtils.calcAmounts());
        }
    }



    protected void saveRecipe(){
        //calculate amounts
        if (calcUtils.getRecipeName()==""){
            showErrorDialog("Forgot to add recipe name.", "");
        }
        else{
            log("saving now...");
            calculate();
            calcUtils.saveData(finalMills);
            setLoadWindow(); //add new saved data to load window
        }
    }

    //display data that is loaded
    protected void displayLoadedData(){
        
        table.setLoadedRecipe();

        table.updateCalcLabels(calcUtils.loadedAmounts);

        //displayResults(); //display the loaded recipe results
    }


//    //if clicked outside of dialog
//    public void outsideDialog(Dialog dialog){
//        //dismisses if click anywhere on outside of dialog box
//        if (Gdx.input.isTouched()){
//            if (Gdx.input.getX() > (dialog.getX() + dialog.getWidth()) || Gdx.input.getX() < dialog.getX()) {
//                dialog.hide();
//                dialog.remove();
//            }
//            if (Gdx.input.getY() < dialog.getY() || Gdx.input.getY() > (dialog.getY() + dialog.getHeight())) {
//                dialog.hide();
//                dialog.remove();
//            }
//        }
//    }


    @Override
    public void show() {
        Gdx.gl.glClearColor(.2f, .4f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        //if a recipe is chosen in loadwindow, hide it, display results
        if (loadWindow.recipeChosen) {
//            loadWindow.hide();
            displayLoadedData();

            loadWindow.recipeChosen = false;
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
