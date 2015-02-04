package com.icey.apps.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.SaveData;
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
 *
 * Created by Allen on 1/19/15.
 */
public class SupplyScreen implements Screen{
    
    public static SupplyScreen instance; //in order to access this screen from supplyWindow
    
    Skin skin = Assets.manager.get(Constants.SUPPLY_MENU_SKIN, Skin.class);
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    //background textures
    Texture supplyTableBack = Assets.manager.get(Constants.SUPPLY_TABLE_BACK, Texture.class);
    Texture mainBackground = Assets.manager.get(Constants.SUPPLY_MENU_BACKGROUND, Texture.class);

    Stage stage; //the stage, holds ui - table & supply Table
    Table table; //the root table
    Table supplyTable; //the supply table (goes under add buttons)


    //constructor for supply screen
    public SupplyScreen(){
        instance = SupplyScreen.this;
        
        stage = new Stage();
        setRootTable();
    }

    
    //the main table, encapsulates all UI elements
    private void setRootTable(){
        table = new Table();
        table.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        table.top().center();
        table.setClip(true);
        
        table.setBackground(new TextureRegionDrawable(new TextureRegion(mainBackground))); //set background
        
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
                    new SupplyWindow(skin, new Supply()).show(stage);
                }
            }
        });

        final TextButton addBaseBtn = new TextButton("Add Nicotine Base", skin, "base");
        addBaseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addBaseBtn.isPressed()){
                    new SupplyWindow(skin, new Base()).show(stage);
                }
            }
        });
        
        final TextButton addFlavorBtn = new TextButton("Add Flavor", skin, "flavor");
        addFlavorBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (addFlavorBtn.isPressed()){
                    new SupplyWindow(skin, new Flavor()).show(stage);
                }
            }
        });
        
        table.add(addSupplyBtn);
        table.add(addBaseBtn);
        table.add(addFlavorBtn);

    }
    
    
    //the supply table, showing which supplies user has
    private void setSupplyTable(){
        supplyTable = new Table(); //current supply table
        supplyTable.top().left().pad(10); //set properties of table
        supplyTable.setBackground(new TextureRegionDrawable(new TextureRegion(supplyTableBack)));
        supplyTable.defaults().pad(5); //set all cells to pad 5 all ways

        //if user has previously entered supplies, they will be added to to table on creation
        if (supplyUtils.supplied) {
            for (IntMap.Entry e : supplyUtils.getSupplyMap())
                addToSupplyTable((SaveData.SupplyData)e.value);
        }
        
        //create scrollpane & add supplyTable to it
        ScrollPane scrollPane = new ScrollPane(supplyTable, skin);

        //adds scroll with supplyTable to root table
        table.add(scrollPane).colspan(3).width(350).height(200);
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
    
    
    //adds supplies to supply table (row keeps track of hwere they are in table)
    int row = 0;
    public void addToSupplyTable(SaveData.SupplyData data){

        Label supplyLabel; //supply label name
        Label amountLabel; //the amount of the supply
        int key; //the supplies key (location) in the IntMap
        
        if (data.supply != null){
            Supply s = data.supply;
            key = s.getSupplyType();
            supplyLabel = new Label(s.getName(), skin, "supply");
            amountLabel = new Label(Double.toString(s.getTotalAmount()), skin, "supply");
        }
        else if (data.base != null){
            Base base = data.base;
            key = 4;
            supplyLabel = new Label("Nicotine Base", skin, "base");
            amountLabel = new Label(Double.toString(base.getTotalAmount()), skin, "base");
        }
        else {
            Flavor flavor = data.flavor;
            key = flavor.key;
            supplyLabel = new Label(flavor.getName(), skin, "flavor");

            amountLabel = new Label(Double.toString(flavor.getTotalAmount()), skin, "flavor");
        }

        //set name as row for identification purposes (to edit later)
        supplyLabel.setName("Name" + Integer.toString(row));
        amountLabel.setName("Amount" + Integer.toString(row));


        supplyTable.add(supplyLabel).width(120);
        supplyTable.add(amountLabel).width(60);
        
        supplyTable.add(editButton(key)).padLeft(20).width(80);
        supplyTable.add(deleteButton(row, key)).padLeft(10).width(80);

        supplyTable.row().pad(5); //next row contains next values

        row++;
    }



    public void updateSupplyTable(int row, SaveData.SupplyData data){
        SnapshotArray<Actor> children = supplyTable.getChildren();
        children.ordered = false;

        //get 1st two children of row, since they are labels name & amount respectively
        Label nameLabel = (Label)children.get(row*4);
        Label amountLabel = (Label)children.get(row*4+1);

        if (data.supply != null){
            nameLabel.setText(data.supply.getName());
            amountLabel.setText(Double.toString(data.supply.getTotalAmount()));
        }
        else if (data.base != null){
            nameLabel.setText("Nicotine Base");
            amountLabel.setText(Double.toString(data.base.getTotalAmount()));
        }
        else{
            nameLabel.setText(data.flavor.getName());
            nameLabel.setText(Double.toString(data.flavor.getTotalAmount()));
        }
    }


    //the button which allows user to edit the supply
    private TextButton editButton(final int key){
        final TextButton editButton = new TextButton("Edit", skin, "edit");
        editButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (editButton.isPressed()){
                    SupplyWindow editWindow = new SupplyWindow(skin, supplyUtils.getSupplyByType(key));
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
                            log("DELETE FINAL");
                            deleteSupply(row, key);
                        }
                    }.text(Constants.DELETE_CHECK).button(new TextButton("Delete", skin)).show(stage);
                }

            }
        });
        
        return deleteButton;
    }
    
    private void deleteSupply(int row, int key){
        SnapshotArray<Actor> children = supplyTable.getChildren();
        children.ordered = false;
        
        if (children.size > 1) {
            for (int i = row*4; i < children.size - 4; i++) {
                children.swap(i, i + 1);
            }
        }
        
        for (int i = 0; i < 4; i++){
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
