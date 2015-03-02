package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.data.Supply;
import com.icey.apps.ui.CalcTable;
import com.icey.apps.ui.FlavorTable;
import com.icey.apps.ui.SupplyWindow;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.SupplyUtils;

/** SupplyScreen class
 *
 * Contains the supply fields user can add
 * - only for base, user fills out percents
 * - for supply & flavor, checks box 
 * - only for flavor name needs to be added
 * - all require an amount to be added
 *
 * TODO: set back button to right (padRight(10)
 *
 * Created by Allen on 1/19/15.
 */
public class SupplyScreen implements Screen{
    
    public static SupplyScreen instance; //in order to access this screen from supplyWindow
    
//    Skin skin = Assets.manager.get(Constants.DARK_SKIN, Skin.class);
    Skin skin = Assets.getSkin();
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    Stage stage; //the stage, holds ui - table & supply Table
    Table table; //the root table
    Table supplyTable; //the supply table (goes under add buttons)

    SupplyWindow supplyWindow; //current supply window
    Supply supply; //current supply being added or edited

    IntMap<Integer> tableMap; //maps out supply keys to table row


    //constructor for supply screen
    public SupplyScreen(){
        instance = SupplyScreen.this;

        tableMap  = new IntMap<Integer>();

        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        
        setRootTable();
    }


    //the main table, encapsulates all UI elements
    private void setRootTable(){
        table = new Table();
        //table.setScale(scaleX, scaleY); //TODO: figure out if scaling individual cells is needed if this is here

        table.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        table.center();

        table.setClip(true);
        table.setLayoutEnabled(true); //need this for invalidating hierarchy
        
        table.setBackground(new TextureRegionDrawable(skin.getRegion("background"))); //set background
        
        table.debug();

        //1st rows - title
        Label supplyTitle = new Label("Supplies", skin, "title");
        supplyTitle.setAlignment(Align.center);
        table.add(supplyTitle).width(230).height(75).colspan(3).padBottom(100);
        
        table.row().padBottom(50f);//rows 2 - add buttons, pad bottom by 50
        setAddButtons(); //addbuttons

        table.row(); //rows 3 - contains inner table with users supplies (1 per rows)
        setSupplyTable();

        //butto to calculator & back to main menu button
        table.row().padTop(100);
        setMenuButtons();
        
        stage.addActor(table);
    }


    //buttons which add supplies to supply table & save data
    private void setAddButtons(){
        final TextButton addSupplyBtn = new TextButton("Add Supply", skin, "rounded");
        addSupplyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addSupplyBtn.isPressed()){
                    supply = new Supply();
                    supplyWindow = new SupplyWindow(skin, "default", supply, false, 0);
                    supplyWindow.show(stage);
                }
            }
        });

        String baseText = "Add Base";
        final TextButton addBaseBtn = new TextButton(baseText, skin, "rounded");
//        addBaseBtn.setWidth(addBaseBtn.getStyle().font.getBounds(baseText).width);
        addBaseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addBaseBtn.isPressed()){
                    supply = new Supply();
                    supplyWindow = new SupplyWindow(skin, "default", supply, false, 1);
                    supplyWindow.show(stage);
                }
            }
        });
        
        final TextButton addFlavorBtn = new TextButton("Add Flavor", skin, "rounded");
        addFlavorBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addFlavorBtn.isPressed()){
                    supply = new Supply();
                    supplyWindow = new SupplyWindow(skin, "default", supply, false, 2);
                    supplyWindow.show(stage);
                }
            }
        });
        
        table.add(addSupplyBtn).fill().pad(10);

        table.add(addFlavorBtn).fill().pad(10);

        table.add(addBaseBtn).fill().pad(10);
    }

    //the supply table, showing which supplies user has
    //layout of table--> [title : amount : type/percent : edit : remove]
    private void setSupplyTable(){
        supplyTable = new Table(); //current supply table

        supplyTableProperties(); //set the properties

        supplyTable.add(new Label("Supply Name", skin, "bold-cyan"));
        supplyTable.add(new Label("Amount (ml)", skin, "bold-cyan"));
        supplyTable.add(new Label("Type/Percents", skin, "bold-cyan"));

        supplyTable.row().padBottom(5);

        supplyTable.debug();

        addUserSupplies();
        //create scrollpane & add supplyTable to it
        ScrollPane scrollPane = new ScrollPane(supplyTable, skin);

        //adds scroll with supplyTable to root table
        table.add(scrollPane).colspan(3).width(480).height(300);

    }

    protected void supplyTableProperties(){
        //table settings, background & defaults
        supplyTable.top().left().pad(10); //set properties of table
        supplyTable.setFillParent(true);
        supplyTable.setBackground(new TextureRegionDrawable(new TextureRegion(skin.getRegion("table-back"))));
        supplyTable.defaults().pad(2); //set all cells to pad 5 all ways

        //column defaults
        supplyTable.columnDefaults(0).width(120).height(20);
        supplyTable.columnDefaults(1).width(70).height(20);
        supplyTable.columnDefaults(2).width(100).height(20);
        supplyTable.columnDefaults(3).width(50);
        supplyTable.columnDefaults(4).width(50);
    }

    protected void addUserSupplies(){
        //if user has previously entered supplies, they will be added to to table on creation
        if (supplyUtils.supplied) {
            for (IntMap.Entry e : supplyUtils.getSupplyMap())
                addToSupplyTable((Supply)e.value);
        }
    }


    
    //the bottom menu buttons - lead to calculator or back to main menu
    private void setMenuButtons(){
        final TextButton calcButton = new TextButton("Calculator", skin, "medium");
        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed()) {
                    MainApp.setState(1);
                }
            }
        });

        table.add(calcButton);

        final TextButton backButton = new TextButton("Back", skin, "medium");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (backButton.isPressed()) {
                    MainApp.setState(0);
                }
            }
        });

        table.add(backButton).colspan(2).right().padRight(2);
    }
    
    

    int rows = 1; //for keeping track, for deletion (1st row is table field names)
    final int COLS = 5; //as of now, 5 columns
    /** Adds supplies to supply table
     *  
     * @param data
     */
    public void addToSupplyTable(Supply data){
        int key = data.getSupplyType(); //the supplies key (location) in the IntMap

        double amount = data.getTotalAmount();

        String typeName = data.getTypeName(key); //the typeName of liquid (either Flavor or Supply)
        String name = data.getName();

        Label nameLabel = new Label(name, skin); //supply label name
        Label amountLabel = new Label(Double.toString(amount), skin); //the amount of the supply

        //add the labels to the table
        supplyTable.add(nameLabel).width(120);
        supplyTable.add(amountLabel).width(70);

        //add type & percent labels
        Label typeLabel = new Label(typeName, skin);
        if (key == 3){
            String percentDisplay = "PG:VG " + data.getBasePercents().get(0) + ":" + data.getBasePercents().get(1);
            Label percentLabel = new Label(percentDisplay, skin );
            supplyTable.add(percentLabel).width(100);
        }
        else{
            supplyTable.add(typeLabel).width(100);
        }
            
        
        supplyTable.add(editButton(key)).padLeft(15).width(50);
        supplyTable.add(deleteButton(rows, key)).padLeft(5).width(50);

        supplyTable.row().pad(5); //next rows contains next values
        rows++;
        tableMap.put(data.getSupplyType(), rows);

        updateCalcTable(key, amount, name);
    }

    protected void updateCalcTable(int key, double amount, String name){
        if (key < 4)
            CalcTable.updateSupplyAmount(key, amount);
        else if (FlavorTable.flavorMap.containsKey(name))
            FlavorTable.flavorMap.get(name).setTotalAmount(amount);
    }


    public void updateSupplyTable(int key, Supply data){
        SnapshotArray<Actor> children = supplyTable.getChildren();
        children.ordered = false;

        int row = tableMap.get(key);

        //get 1st two children of rows, since they are labels name & amount respectively
        Label nameLabel = (Label)children.get(row+ 1);
        Label amountLabel = (Label)children.get(row + 2);
        Label tpLabel = (Label)children.get(row+3);

        nameLabel.setText(data.getName());
        amountLabel.setText(Double.toString(data.getTotalAmount()));

        if (data.getSupplyType()==3){
            String percentDisplay = "PG:VG " + data.getBasePercents().get(0) + ":" + data.getBasePercents().get(1);
            tpLabel.setText(percentDisplay);
        }
        else{
            tpLabel.setText(data.getTypeName(key));
        }

        updateCalcTable(key, data.getTotalAmount(), data.getName());
    }


    //the button which allows user to edit the supply
    private TextButton editButton(final int key){
        final TextButton editButton = new TextButton("Edit", skin, "small");
        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (editButton.isPressed()){
                    SupplyWindow editWindow;
                    if (key < 3)
                        editWindow = new SupplyWindow(skin, "default", supplyUtils.getSupplyByType(key), true, 0);
                    else if (key == 3)
                        editWindow = new SupplyWindow(skin, "default", supplyUtils.getSupplyByType(key), true, 1);
                    else
                        editWindow = new SupplyWindow(skin, "default", supplyUtils.getSupplyByType(key), true, 2);
                    editWindow.show(stage);
                }
            }
        });
        
        return editButton;
    }


    //button that if pressed, deletes supply from table & from Save file as well
    private Button deleteButton(final int row, final int key){
        final TextButton deleteButton = new TextButton("Delete", skin, "small");
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (deleteButton.isPressed()){
                    //confirmation pop-up dialog that makes sure user wants to delete supply
                    new Dialog("", skin){
                        protected void result (Object object) {
                            log("DELETE");
                            deleteSupply(row, key);
                        }
                    }.text(Constants.DELETE_CHECK).button(new TextButton("Delete", skin)).show(stage);
                }

            }
        });
        
        return deleteButton;
    }
    

    /**Deletes the wdigets in the supplyTable based on rows#
     *
     * @param row : rows supply is in
     * @param key : the supply key in the supplyUtils map
     */
    protected void deleteSupply(int row, int key){
        SnapshotArray<Actor> children = supplyTable.getChildren();
        children.ordered = false;
        
        if (children.size > 1) {
            for (int i = row*COLS; i < children.size - COLS; i++) {
                children.swap(i, i + 1);
            }
        }
        
        for (int i = 0; i < COLS; i++){
            supplyTable.removeActor(children.get(children.size - 1));
        }
        
        supplyUtils.removeSupply(key);
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
        stage.dispose();
    }


    public Stage getStage() {
        return stage;
    }

    private void log(String message){
        Gdx.app.log("SupplyScreen LOG: ", message);
    }
}
