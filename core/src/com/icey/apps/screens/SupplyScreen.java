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
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.Supply;
import com.icey.apps.ui.SupplyWindow;
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
    
    Skin skin = Assets.manager.get(Constants.SUPPLY_MENU_SKIN, Skin.class);
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    Stage stage; //the stage, holds ui - table & supply Table
    Table table; //the root table
    Table supplyTable; //the supply table (goes under add buttons)


    //constructor for supply screen
    public SupplyScreen(){
        instance = SupplyScreen.this;

        stage = new Stage(new FitViewport(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT));
        
        setRootTable();

    }


    //the main table, encapsulates all UI elements
    private void setRootTable(){
        table = new Table();
        //table.setScale(scaleX, scaleY); //TODO: figure out if scaling individual cells is needed if this is here

        table.setBounds(0, 50, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        table.center();

        table.setClip(true);
        table.setLayoutEnabled(true); //need this for invalidating hierarchy
        
        table.setBackground(new TextureRegionDrawable(skin.getRegion("background"))); //set background
        
        table.debug();

        //1st row - title
        Label supplyTitle = new Label("SUPPLIES", skin, "title");
        supplyTitle.setAlignment(Align.center);
        table.add(supplyTitle).width(230).height(75).colspan(3).padBottom(100);
        
        table.row().padBottom(50f);//row 2 - add buttons, pad bottom by 50
        setAddButtons(); //addbuttons

        table.row(); //row 3 - contains inner table with users supplies (1 per row)
        setSupplyTable();

        //butto to calculator & back to main menu button
        setMenuButtons();
        
        stage.addActor(table);
    }


    //buttons which add supplies to supply table & save data
    private void setAddButtons(){
        final TextButton addSupplyBtn = new TextButton("Add Supply", skin, "supply");
        addSupplyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addSupplyBtn.isPressed()){
                    new SupplyWindow(skin, "default", new Supply(), false).show(stage);
                }
            }
        });

        String baseText = "Add Base";
        final TextButton addBaseBtn = new TextButton(baseText, skin, "base");
//        addBaseBtn.setWidth(addBaseBtn.getStyle().font.getBounds(baseText).width);
        addBaseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addBaseBtn.isPressed()){
                    new SupplyWindow(skin, "base", new Supply(), false).show(stage);
                }
            }
        });
        
        final TextButton addFlavorBtn = new TextButton("Add Flavor", skin, "flavor");
        addFlavorBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addFlavorBtn.isPressed()){
                    new SupplyWindow(skin, "flavor", new Supply(), false).show(stage);
                }
            }
        });
        
        table.add(addSupplyBtn).fill().pad(10);

        table.add(addFlavorBtn).fill().pad(10);

        table.add(addBaseBtn).fill().pad(10);
    }
    
    
    //the supply table, showing which supplies user has
    //layout of table--> [title : amount : type/percent/empty : edit : save]
    private void setSupplyTable(){
        supplyTable = new Table(); //current supply table
        supplyTable.top().left().pad(10); //set properties of table

        supplyTable.setBackground(new TextureRegionDrawable(new TextureRegion(skin.getRegion("supplyTableBack"))));
        supplyTable.defaults().pad(2); //set all cells to pad 5 all ways
        
        supplyTable.debug();

        //if user has previously entered supplies, they will be added to to table on creation
        if (supplyUtils.supplied) {
            for (IntMap.Entry e : supplyUtils.getSupplyMap())
                addToSupplyTable((Supply)e.value);
        }
        
        //create scrollpane & add supplyTable to it
        ScrollPane scrollPane = new ScrollPane(supplyTable, skin);

        //adds scroll with supplyTable to root table
        table.add(scrollPane).colspan(3).width(480).height(200);
        table.row();
    }

    
    //the bottom menu buttons - lead to calculator or back to main menu
    private void setMenuButtons(){
        final TextButton calcButton = new TextButton("Calculator", skin);
        calcButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (calcButton.isPressed()) {
                    MainApp.setState(1);
                }
            }
        });

        table.add(calcButton);

        final Button backButton = new Button(skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (backButton.isPressed()) {
                    MainApp.setState(0);
                }
            }
        });

        table.add(backButton);
    }
    
    

    int row = 0; //for keeping track, for deletion
    final int COLS = 5; //as of now, 5 columns
    /** Adds supplies to supply table
     *  
     * @param data
     */
    public void addToSupplyTable(Supply data){
        String type = ""; //the type of liquid (either Flavor or Supply)

        Label nameLabel; //supply label name
        Label amountLabel; //the amount of the supply
        Label typeLabel = new Label("", skin);
        Label percentLabel = new Label("", skin);
        int key = data.getSupplyType(); //the supplies key (location) in the IntMap

        if (key < 3){
            nameLabel = new Label(data.getName(), skin, "supply");
            amountLabel = new Label(Double.toString(data.getTotalAmount()), skin, "supply");
        }
        else if (key == 3) {
            Base base = new Base(data);
            nameLabel = new Label("Nicotine Base", skin, "base");
            
            amountLabel = new Label(Double.toString(data.getTotalAmount()), skin, "base");
        }
        else{
            Flavor flavor = new Flavor(data);
            nameLabel = new Label("" + flavor.getName(), skin, "flavor");
            type = flavor.getTypeName();
            amountLabel = new Label(Double.toString(flavor.getTotalAmount()), skin, "flavor");
        }

        //set name as row for identification purposes (to edit later)
        nameLabel.setName("Name: " + Integer.toString(row));
        amountLabel.setName("Amount (ml): " + Integer.toString(row));
        
        //add the labels to the table
        supplyTable.add(nameLabel).width(120);
        supplyTable.add(amountLabel).width(40);

        if (key == 3){
            String percentDisplay = "PG:VG " + data.getBasePercents().get(0) + ":" + data.getBasePercents().get(1);
            percentLabel = new Label(percentDisplay, skin, "base");
            supplyTable.add(percentLabel).width(100);
        }
        else{
            typeLabel = new Label(type, skin, "flavor");
            supplyTable.add(typeLabel).width(100);
        }
            
        
        supplyTable.add(editButton(key)).padLeft(15).width(50);
        supplyTable.add(deleteButton(row, key)).padLeft(5).width(50);

        supplyTable.row().pad(5); //next row contains next values

        row++;
    }



    public void updateSupplyTable(int row, Supply data){
        SnapshotArray<Actor> children = supplyTable.getChildren();
        children.ordered = false;

        //get 1st two children of row, since they are labels name & amount respectively
        Label nameLabel = (Label)children.get(row*4);
        Label amountLabel = (Label)children.get(row*4+1);

        nameLabel.setText(data.getName());
        amountLabel.setText(Double.toString(data.getTotalAmount()));

    }


    //the button which allows user to edit the supply
    private TextButton editButton(final int key){
        final TextButton editButton = new TextButton("Edit", skin, "edit");
        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (editButton.isPressed()){
                    SupplyWindow editWindow;
                    if (key < 3)
                        editWindow = new SupplyWindow(skin, "default", supplyUtils.getSupplyByType(key), true);
                    else if (key == 3)
                        editWindow = new SupplyWindow(skin, "base", supplyUtils.getSupplyByType(key), true);
                    else
                        editWindow = new SupplyWindow(skin, "flavor", supplyUtils.getSupplyByType(key), true);
                    editWindow.show(stage);
                }
            }
        });
        
        return editButton;
    }


    //button that if pressed, deletes supply from table & from Save file as well
    private Button deleteButton(final int row, final int key){
        final Button deleteButton = new Button(skin, "delete");
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
    

    /**Deletes the wdigets in the supplyTable based on row#
     *
     * @param row : row supply is in
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
