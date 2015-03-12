package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
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
 *
 */
public class CalculatorScreen implements Screen {

    //Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class); //skin

    Skin skin = Assets.getSkin();

    public static Stage stage; //main stage

    public static boolean loadedRecipe = false; //loaded recipe flag

    //Tables which hold UI widgets, get added to stage
    CalcTable table;  //root table
    //FlavorTable flavorTable; //flavorTable - holds flavors
    ScrollPane scroll; //ScrollPane for flavorTable

    Label errorTitle;
    String[] errorMsgs = Constants.ERROR_MSGS; //0=flavor, 1=desired percents, 2 = base percents

    LoadWindow loadWindow;
    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //tool used for calculations, loading, saving

    SnapshotArray<Double> finalMills; //final amounts

    TextButton copyButton;
    String currRecipe = ""; //currently loaded recipe

    public CalculatorScreen(){
        stage = new Stage(new ScalingViewport(Scaling.fill, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));

        //setCopyDialog();

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


    protected void showCopyDialog(){
        Dialog copyDialog = new Dialog("", skin).text("Copied to clipboard");
        copyDialog.show(stage);
        copyDialog.addAction(Actions.sequence(Actions.alpha(1), Actions.fadeOut(3, Interpolation.fade), Actions.removeActor(copyDialog)));
    }


    protected void setButtons(){
        //the flavor button
        final TextButton flavorButton = new TextButton("Add Flavor", skin, "rounded");
        flavorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (flavorButton.isPressed()) {
                    Flavor newFlavor = new Flavor("Flavor " + Integer.toString(table.flavorTable.numFlavors + 1));
                    table.flavorTable.addNewFlavor(newFlavor);//add new flavor

                }
            }

        });

        table.add(flavorButton).width(150).colspan(2).height(50).center().padBottom(5); //.colspan(table.cols)
//        table.row();

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

        table.add(calcButton).width(175).height(50).padBottom(5).padRight(5).colspan(2);
        

        setMenuButtons();

    }


    protected void setMenuButtons(){
        table.row() ;

        //table for menu buttons
        Table buttonTable = new Table();
        buttonTable.top();

        //the save button
        final TextButton saveButton = new TextButton("Save", skin, "medium");
        saveButton.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {

               new Dialog("", skin){
                   @Override
                   protected void result(Object object) {
                       if ((Boolean)object){
                           saveRecipe();
                       }
                   }
               }.text("Save this ejuice recipe?").button("Yes", true).button("No", true).show(stage);
           }
        });

        buttonTable.add(saveButton).width(100).height(50).pad(2);


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
        buttonTable.add(loadButton).width(100).height(50).pad(2);


        //copy button
        copyButton = new TextButton("Copy", skin, "disabled-med");
        copyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (copyButton.isPressed()){
                    UIUtils.sendToClipBoard_NoSupply(finalMills, new SnapshotArray<Flavor>(calcUtils.getFlavors()));
                    showCopyDialog();
                }
            }
        });
        copyButton.setDisabled(true);
        copyButton.setTouchable(Touchable.disabled);

        buttonTable.add(copyButton).width(100).height(50).pad(2);


        //the back button
//        final Button backButton = new Button(skin);
//        final TextButton backButton = UIUtils.Buttons.textButton("BACK", skin, "menu");
        final TextButton backButton = UIUtils.Buttons.textButton("Back", skin, "medium");
        backButton.addListener(UIUtils.backTextButtonListener(backButton));
        buttonTable.add(backButton).width(100).height(50).pad(2); //.align(Align.right);


        table.add(buttonTable).colspan(table.cols).center();
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


    boolean error = false; //error calculating, means cannot save
    public void calculate(){
        //calculate amounts if touchdown
        if (!calcUtils.areFlavorsSet()){
            log("flavors not set!");
            showErrorDialog(errorMsgs[0], calcUtils.getError());
            error = true;
        }
        else if (!calcUtils.isGoalSet()){
            showErrorDialog(errorMsgs[3], "");
            error = true;
        }
        else{
            log("calculating now...");
            finalMills = calcUtils.calcAmounts();
            table.updateCalcLabels(calcUtils.calcAmounts());

            updateCopyButton();
        }
    }

    protected void updateCopyButton(){
        //update copy button
        copyButton.setStyle(skin.get("medium", TextButton.TextButtonStyle.class));
        copyButton.setDisabled(false); //enable button
        copyButton.setTouchable(Touchable.enabled);
    }

    protected void saveRecipe(){
        //calculate amounts
        if (calcUtils.getRecipeName()==""){
            showErrorDialog("Forgot to add recipe name.", "");
        }
        else{
            log("saving now...");
            calculate();

            if (!error){
                calcUtils.saveData(finalMills);
                setLoadWindow(); //add new saved data to load window
            }
            else{
                error = true; //reset error
            }

        }
    }

    //display data that is loaded
    protected void displayLoadedData(){
        
        table.setLoadedRecipe();

        finalMills = calcUtils.loadedAmounts;
        table.updateCalcLabels(finalMills);

        updateCopyButton(); //so that results can be copied
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
        Gdx.gl.glClearColor(37/255f, 37/255f, 37/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setInputProcessor(stage);

        //if a recipe is chosen in loadwindow, hide it, display results
        if (loadedRecipe) {
//            loadWindow.hide();
            displayLoadedData();

//            currRecipe = calcUtils.getRecipeName();
            loadedRecipe = false;
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
