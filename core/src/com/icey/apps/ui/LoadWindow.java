package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.screens.CalculatorScreen;
import com.icey.apps.utils.CalcUtils;

/** Popup Window for loading saved recipes
 * - similar setup to FlavorTable
 * DialogTable <--| ScrollPane <-- Table |<--| VerticalGroup <--- {Tables (individual recipe widgets)} |
 *
 * Each recipe table contains max 3 columns in lite version:
 *      [Label: recipeName | TextButton : Load | TextButton : delete]
 *
 * In Pro version, max 6 columns in individual recipe table:
 *      [Label: recipeName | TextButton : Load | TextButton : delete]
 *      [Label: rating | Checkboxes: { 1 | 2 | 3 | 4 | 5 } ]
 *
 * Created by Allen on 1/19/15.
 */
public class LoadWindow extends Dialog{

    Skin skin; //window skin (as well as other widgets)
    
    ScrollPane scrollPane;
    Table table;
//    ObjectMap<String, ButtonGroup<CheckBox>> ratingMap = new ObjectMap<String, ButtonGroup<CheckBox>>(); //future recipe stars

    public CalcUtils calcUtils = CalcUtils.getCalcUtil();
    public boolean recipeChosen = false; //if a recipe chosen, then true

    Array<String> currRecipes; //current recipes in load window

    VerticalGroup verticalGroup;
    int numRecipes = 0; //number of recipes: used for id in vertical group - index


    public LoadWindow(Skin skin, String styleName) {
        super("", skin, styleName);
        this.skin = skin;

        getContentTable().top(); //make sure table is on top
        getContentTable().setClip(true);

        setTitle(); //set title tab

        initRecipes(); //initialize recipes in new table in ScrollPane

        addInputListener(); //add dialog input listener (for escape or back input)

        setBackButton();
    }


    protected void setTitle(){
        Label title = new Label("Saved Recipes", skin, "tab");
        title.setAlignment(Align.center);

        getContentTable().add(title).fillX().expandX().top();
    }


    protected void setBackButton(){

        final TextButton backButton = new TextButton("Back to Calculator", skin, "medium");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (backButton.isPressed()){
                    closeWindow();
                }
            }
        });

        getButtonTable().setHeight(40);
        getButtonTable().add(backButton).center().padBottom(15);
    }


    protected void initRecipes(){
        table = new Table(); //table containing the recipes

        verticalGroup = new VerticalGroup();
        currRecipes = new Array<String>();

        table.add(verticalGroup);

        if (calcUtils.getAllRecipes().size > 0)
            addRecipes(calcUtils.getAllRecipes());

        //make nested table fit into ScrollPane
        scrollPane = new ScrollPane(table, skin);

        getContentTable().row();
        getContentTable().add(scrollPane).fill().expand().top();
    }

    /** Adds recipe fields to load window
     *
     * @param recipeNames : name of recipes
     */
    public void addRecipes(Array<String> recipeNames){
        
        for (String recipe: recipeNames){
            if (!currRecipes.contains(recipe, false)){
                addRecipeFields(recipe, new Table(), numRecipes);

                currRecipes.add(recipe);
                numRecipes++;
            }
        }
    }

    /** adds Recipe fields
     *
     * @param recipeName : name of recipe
     * @param table : new table in which recipe fields will be added
     * @param recipeID : recipe id = index of table in vertical group
     */
    protected void addRecipeFields(final String recipeName, Table table, final int recipeID){
        Label label = new Label(recipeName, skin, "default-green");
        table.add(label).width(120);

        final TextButton loadButton = new TextButton("Load", skin, "small");
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (loadButton.isPressed()){
                    calcUtils.loadRecipeData(recipeName);
                    CalculatorScreen.loadedRecipe = true;
                    closeWindow();
                }
            }
        });
        table.add(loadButton).pad(3);

        //add a delete button
        TextButton deleteButton = new TextButton("Delete", skin, "small");
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                log("load window set to invisible");
                String msg = "Are you sure you want to delete recipe?";
                confirmDialog(msg, recipeID, recipeName);
            }
        });

        table.add(deleteButton).pad(3);
        table.row();

        //setCheckBoxes(table, recipeName); //FIXME: uncomment in pro version

        verticalGroup.addActor(table);
    }

    
    protected void addInputListener(){
        InputListener inputListener = new InputListener(){

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Buttons.BACK)
                    closeWindow();
                    log("hid window after escape detected");

                return false;
            }
        };
        
        addListener(inputListener);
        
    }


//    /** Sets up stars to rate recipe //FIXME: uncomment in pro version
//     *
//     * @param table : table recipe is in
//     * @param recipeName : name of recipe to rate
//     */
//    protected void setCheckBoxes(Table table, final String recipeName){
//
//        Label label = new Label("Rating: ", skin, "default-green");
//        table.add(label);
//
//        //checkBox button group - for setting max buttons checked
//        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<CheckBox>();
//        buttonGroup.setMaxCheckCount(5); //sets max check count to 5
//        buttonGroup.setUncheckLast(false); //so that buttons are not checked initially
//
//        int numChecked = calcUtils.getRecipeRating(recipeName, true);
//
//        //add the checkboxes to buttongroup
//        for (int i = 0; i < 5; i++){
//            final CheckBox checkBox = new CheckBox("", skin, "star");
//            checkBox.setName(Integer.toString(i)); //set the name as index in group
//            checkBox.addListener(new ChangeListener() {
//                @Override
//                public void changed(ChangeEvent event, Actor actor) {
//                    if (checkBox.isPressed()){
//                        checkBox.setChecked(true);
//                        updateRecipeRating(recipeName, Integer.parseInt(checkBox.getName()));
//                    }
//                }
//            });
//
//            //check star CheckBox if meets conditions
//            if (numChecked > 0 && i+1<=numChecked){
//                checkBox.setChecked(true);
//            }
//
//            //add to button group
//            buttonGroup.add(checkBox);
//
//            table.add(checkBox).width(checkBox.getWidth()).pad(1).height(20);
//        }
//
//        ratingMap.put(recipeName, buttonGroup);
//    }


//    /** Updates the recipe rating & display of checked stars //FIXME: in pro version
//     *
//     * @param recipeName : recipe name
//     * @param index : index of CheckBox checked in ButtonGroup - in order for others to be checked
//     */
//    protected void updateRecipeRating(String recipeName, int index){
//        ButtonGroup<CheckBox> checkBoxes = ratingMap.get(recipeName);
//        checkBoxes.getCheckedIndex(); //get first index of checked box
//
//        for (int i = 0; i <= index; i++){
//            checkBoxes.getButtons().get(i).setChecked(true);
//        }
//
//        calcUtils.setRecipeRating(recipeName, checkBoxes.getAllChecked().size, true);
//    }


    /** removes the recipe fields from dialog
     *
     * @param recipeID : the numeric id (order added) of recipe in group
     */
    public void removeRecipeFields(int recipeID){

        SnapshotArray<Actor> children = verticalGroup.getChildren(); //children are tables in vertical group

        if (children.size > 1){
            for (int i = recipeID; i < children.size - 1; i++) {
                children.swap(i, i + 1); //swap widgets
                verticalGroup.swapActor(i, i + 1);//swap tables in verticalGroup

                currRecipes.swap(i, i + 1); //swap recipes

            }
        }
        numRecipes--; //decrement number of flavors

        currRecipes.removeIndex(currRecipes.size - 1); //delete last recipe
        verticalGroup.removeActor(children.removeIndex(children.size - 1));
    }


    protected void confirmDialog(String message, final int recipeID, final String recipeName){
        new Dialog("", skin){
            @Override
            protected void result(Object object) {
                if ((Boolean)object){
                    removeRecipeFields(recipeID);
                    calcUtils.deleteRecipe(recipeName);
                }
            }
        }.text(message).button("Yes", true).button("No", false).show(this.getStage());
    }


    //hides the window
    public void closeWindow(){
        //recipeChosen = false;
        this.hide();
    }

    @Override
    public void act(float delta) {
        super.act(delta);

//        //dismisses if click anywhere on outside of dialog box
//        if (Gdx.input.justTouched()){
//            if (Gdx.input.getX() > (getX() + getWidth()) || Gdx.input.getX() < getX()) {
//                closeWindow();
//            }
//            if (Gdx.input.getY() < getY() || Gdx.input.getY() > (getY() + getHeight())) {
//                closeWindow();
//            }
//        }
    }

    private void log(String message){
        Gdx.app.log("LoadWindow LOG: ", message);
    }
}
