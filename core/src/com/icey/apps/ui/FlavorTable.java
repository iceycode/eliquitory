package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Flavor;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.SupplyUtils;

/** Table that holds widgets related to flavors
 * - added to scrolltable in calculator screen
 *  
 * Created by Allen on 1/21/15.
 */
public class FlavorTable extends Table{

    public static FlavorTable instance;
    
    //width & height of the text fields (percents)
    final float fieldsWidth = Constants.TEXT_FIELD_WIDTH;
    final float fieldsHeight = Constants.TEXT_FIELD_HEIGHT;
    Skin skin;

    //CalcTable rootTable;
    CalcUtils calcUtils = CalcUtils.getCalcUtil();
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();
    
    //---the widgets that are a part of this table
    VerticalGroup vertGroup; //vertical group for insertion/removal of flavor fields
    public Array<Label> calcLabels; //labels for the calculated amounts to add
    public Array<Label> supplyLabels; //flavor supply labels
    public Array<TextField> flvrPercsTFs;
    public Array<TextField> flvrTitleTFs;
    public OrderedMap<Integer, Array<CheckBox>> checkBoxMap; //0=PG, 1=VG, 2 =EtOH/H2O/etc
    
    OrderedMap<Integer, List<Label>> ddFlavLists;
    
    int numFlavors = 0; //the number of flavors
    
    public FlavorTable(Skin skin){
        instance = FlavorTable.this;
        
        this.skin = skin;

        defaults().colspan(4).maxWidth(480).maxHeight(200);
        top();
        //debug(); //debug this table
        
        flvrPercsTFs = new Array<TextField>();
        flvrTitleTFs = new Array<TextField>();
        checkBoxMap = new OrderedMap<Integer, Array<CheckBox>>();
        
        calcLabels = new Array<Label>();
        supplyLabels = new Array<Label>();
        ddFlavLists = new OrderedMap<Integer, List<Label>>();

        vertGroup = new VerticalGroup(); //add tables here

        addNewFlavor(); //adds 1 flavor initially

        add(vertGroup).colspan(4); //add this nested table into the root flavor table
    }


    public void addNewFlavor(){
        Table table = new Table();
        numFlavors++;
        calcUtils.addFlavor(new Flavor("New Flavor"));
        calcLabels.add(new Label("", skin));
        supplyLabels.add(new Label("", skin));

        addFlavorFields(table); //new flavor entry
        vertGroup.addActor(table);
    }
    
    private void addFlavorFields(Table table){
        //adding the flavor name label & text field
        Label flavNameLabel = new Label("Flavor Name: ", skin);
        table.add(flavNameLabel);
        
        TextField flavorTextField = new TextField("", skin, "nameTextField");
        flavorTextField.setTextFieldListener(nameTextFieldListener());
        flavorTextField.setName("flavorFieldName_" + Integer.toString(numFlavors));
        flavorTextField.setMessageText("Flavor " + Integer.toString(numFlavors));
        flavorTextField.setColor(Color.RED);
        flavorTextField.setCursorPosition(2);
        flvrTitleTFs.add(flavorTextField);
        table.add(flavorTextField).width(fieldsWidth).height(fieldsHeight);
        table.add(calcLabels.peek()); //where the calculated value goes
        table.add(supplyLabels.peek());
        
        //the delete button will delete flavor from the table
        final Button deleteButton = new Button(skin, "delete");
        final int flavNum = numFlavors - 1; //the index of flavor in vertical group
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (deleteButton.isPressed()) {
                    String flavName = ((Button)actor).getName();
                    int flavNumber = Integer.parseInt(flavName.substring(flavName.indexOf('_')+1, flavName.length()));
                    removeFlavorFields(flavNumber);
                }
            }
        });
        table.add(deleteButton).align(Align.left);

        table.row();
        addFlavorPercentFields(table); //2nd row
        addCheckBoxes(table); //3rd row
    }
    
    //a drop-down list for when typing the flavor, so that user can select a flavor in list
    private void setFlavorList(){
        List<Label> flavList = new List<Label>(skin);
        
        Array<Flavor> flavors = supplyUtils.getAllFlavors();
        Array<Label> flavLabels = new Array<Label>();
        
        
        for (Flavor f : flavors){
            flavLabels.add(new Label(f.getName(), skin));
        }
        
        flavList.setItems(flavLabels);
        ddFlavLists.put(numFlavors, flavList);
    }


    //percent fields for percentage of flavor in recipe
    private void addFlavorPercentFields(Table table){
        //adding the flavor percent label & textfield
        Label flavPercLabel = new Label("Flavor %: ", skin);
        table.add(flavPercLabel).align(Align.right);
        
        TextField flavorPercentField = new TextField("", skin, "numTextField");
        flavorPercentField.setName("flavorPercentField_"+ Integer.toString(numFlavors));
        flavorPercentField.setTextFieldListener(percentListener());
        flavorPercentField.setMaxLength(2);
        flavorPercentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        flavorPercentField.setColor(Color.RED);
        flvrPercsTFs.add(flavorPercentField);
        table.add(flavorPercentField).width(fieldsWidth).height(fieldsHeight);
        table.row();
    }

    //add pg, vg & other checkboxes
    private void addCheckBoxes(Table table){
        //adding the checkboxes
        CheckBox pgCheck = new CheckBox("PG ", skin);
        pgCheck.setName("flavorCheckBoxPG_" + Integer.toString(numFlavors));
        pgCheck.addListener(flavorBoxListener());
        table.add(pgCheck).width(80f).height(20f);

        CheckBox vgCheck = new CheckBox("VG ", skin);
        vgCheck.setName("flavorCheckBoxVG_" + Integer.toString(numFlavors));
        vgCheck.addListener(flavorBoxListener());
        table.add(vgCheck).width(80f).height(20f);

        CheckBox otherCheck = new CheckBox("EtOH/H2O/etc ", skin);
        otherCheck.setName("flavorCheckBoxOther_"+Integer.toString(numFlavors));
        otherCheck.addListener(flavorBoxListener());
        table.add(otherCheck).width(180f).height(20f);
        table.row();

        //add to flavor checkBox array and into map
        Array<CheckBox> boxes = new Array<CheckBox>();
        boxes.add(pgCheck);
        boxes.add(vgCheck);
        boxes.add(otherCheck);
        checkBoxMap.put(numFlavors-1, boxes);
        
    }

    /** removes the flavor form the field
     *
     * @param flavNum
     */
    public void removeFlavorFields(int flavNum){

        SnapshotArray<Actor> children = vertGroup.getChildren(); //remove the table from this array

        //children.removeIndex(flavNum); //remove from children of group
        calcUtils.removeFlavor(flavNum); //remove from calcutil flavors array

        if (children.size > 1){
            for (int i = flavNum; i < children.size - 1; i++) {
                //delete from the vertical group
                children.swap(i, i + 1);
                vertGroup.swapActor(i, i + 1);

                //delete the other widgets
                calcLabels.set(i, calcLabels.removeIndex(i + 1));
                supplyLabels.set(i, supplyLabels.removeIndex(i + 1));
            }
        }
        numFlavors--; //decrement number of flavors

        vertGroup.removeActor(children.removeIndex(children.size - 1));
    }
    
    //sets the calcLabels amounts
    public void addFlavorAmounts(){
        
    }
    
    public void addSupplyAmounts(){
        
    }

    private TextField.TextFieldListener percentListener(){
        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log( "Percentage typed: " + c + ", for " + textField.getName());
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= '0' && c <= '9'){
                    if (textField.getName().contains("flavor")){
                        int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                        calcUtils.setFlavorPercent(textField.getText(), i);
                    }
//                    else if (textField.getName().contains("Base")){
//                        calcUtils.setBasePercent(textField.getText(), textField.getName());
//                    }
//                    else{
//                        calcUtils.setDesiredPercent(textField.getText(), textField.getName());
//                    }
                }
            }
        };

        return percentFieldListener;
    }

    private TextField.TextFieldListener nameTextFieldListener(){
        final int flavNum = numFlavors;
    TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
        @Override
        public void keyTyped(TextField textField, char c) {
            log( "Name typed: " + c);
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) 
                            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (textField.getName().contains("flavor")){
                    int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                    calcUtils.setFlavorName(String.valueOf(textField.getText()), i);
                }

//                else if (textField.getName().contains("recipe")){
//                    calcUtils.setRecipeName(textField.getText());
//                    log("recipe name: " + textField.getText());
//                }

            }
        };

        return nameTextFieldListener;
    }

    private ChangeListener flavorBoxListener(){
        ChangeListener flavorBoxListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox box = ((CheckBox)actor);
                if (box.isChecked()){
                    int i = Integer.parseInt(box.getName().substring(box.getName().length()-1))-1;
                    log( " the index of flavors = " + i);
                    
                    calcUtils.setFlavorType(box, i);
                }
            }
        };

        return flavorBoxListener;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void log(String message){
        Gdx.app.log("FlavorTable LOG: ", message);
    }
    
}
