package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Flavor;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.UIUtils;

/** Table that holds widgets related to flavors
 * - added to scrolltable in calculator screen
 *  
 * Created by Allen on 1/21/15.
 * TODO: for flavor table, supply amount label is off (a bit
 * 
 */
public class FlavorTable extends Table{

    public static FlavorTable instance;
    
    //width & height of the text fields (percents)
    private final float FIELD_WIDTH = Constants.TEXT_FIELD_WIDTH;
    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;
    private final float TITLE_WIDTH = Constants.TITLE_WIDTH;
    private final float TITLE_HEIGHT = Constants.TITLE_HEIGHT;
    
    Skin skin;

    //CalcTable rootTable;
    CalcUtils calcUtils = CalcUtils.getCalcUtil();
    
    //---the widgets that are a part of this table
    VerticalGroup vertGroup; //vertical group for insertion/removal of flavor fields
    public Array<Label> calcLabels; //labels for the calculated amounts to add
    public Array<Label> supplyLabels; //flavor supply labels
    public Array<TextField> flvrPercsTFs = new Array<TextField>();
    public Array<TextField> flvrTitleTFs = new Array<TextField>();

    //CheckBox map for flavors added (by order); for Array:0=PG, 1=VG, 2 =EtOH/H2O/etc
    public OrderedMap<Integer, Array<CheckBox>> checkBoxMap  = new OrderedMap<Integer, Array<CheckBox>>();
    
    //these arrays/maps are to help manipulate fields of flavor if it switched to supply one
    SelectBox<TextField> dropDown; //current selectbox
    Array<Flavor> supplyFlavors; //flavors in the users saved supply
    Array<String> flavorNames = new Array<String>(); //flavor names in supply - go into selectbox;
    ObjectMap<String, Flavor> flavorMap; //to map out flavors in supply to position in flavorTF array
    
    Flavor currFlavor; //current flavor being added
    public int numFlavors = 0; //the number of flavors
    String fieldName = "flavorFieldName_";


    public FlavorTable(Skin skin){
        instance = FlavorTable.this;
        this.skin = skin;

        setTableProperties(); //table properties

        initArray_Maps(); //initialize arrays/maps

        setSupplies(); //set the supplies (if any)

        //setTitle(); //set the title of the table
        initFlavorGroup(); //set the flavor group
    }

    //sets table properties
    protected void setTableProperties(){
        //        defaults().maxWidth(480).maxHeight(200);
        setFillParent(true);
        top();
        setLayoutEnabled(true); //note: default is true
        setClip(true);

        //debug(); //debug this table
        //pack();
    }

    //initializes the arrays & maps used
    protected void initArray_Maps(){
        flavorNames = new Array<String>();
        flvrPercsTFs = new Array<TextField>();
        flvrTitleTFs = new Array<TextField>();
        checkBoxMap = new OrderedMap<Integer, Array<CheckBox>>();
        calcLabels = new Array<Label>();
        supplyLabels = new Array<Label>();
        flavorMap = new ObjectMap<String, Flavor>(); //flavors set here (when added & from supply)
    }




    //sets the supplies
    protected void setSupplies(){
        //the supllied flavors
        if (calcUtils.flavorSupplied) {
            supplyFlavors = calcUtils.getSupplyFlavors();
            setFlavorSupplyNameFields();
        }
    }


    //sets the vertical group which holds the flavors
    protected void initFlavorGroup(){
        initFirstFlavor(); //initialize first flavor


        vertGroup = new VerticalGroup(); //add indiviual flavors to a VerticalGroup

        addNewFlavor(currFlavor); //adds 1 flavor initially
        add(vertGroup).align(Align.center).colspan(6).expandX(); //add this nested table into the root flavor table
    }


    //adds 1st flavor to the table
    @SuppressWarnings("unchecked")
    protected void initFirstFlavor(){
        flavorNames.add(Constants.NEW_FLAVOR_STRING); //add default value
        currFlavor = Constants.NEW_FLAVOR; //a new flavor initially added

        flavorMap.put(Constants.NEW_FLAVOR_STRING, currFlavor);
    }


    public void addNewFlavor(Flavor flavor){
        this.currFlavor = flavor;
        
        if (!flavorMap.containsKey(flavor.getName())){
            currFlavor.setName("Flavor " + Integer.toString(numFlavors + 1));
            flavorMap.put(currFlavor.getName(), currFlavor);
        }
        

        Table table = new Table(); //new table added to vertGroup
        table.debug();
        table.center();
        //table.defaults().colspan(4); //spans 4 columns across nested FlavorTable within root table
        numFlavors++; //increment num of flavors

        //add into calc utility
        calcUtils.addFlavor(currFlavor);
        
        addFlavorFields(table); //new flavor input fields

        vertGroup.addActor(table);
    }

    
    //flavor fields get added to a new table, which in turn is added to vertical group
    @SuppressWarnings("unchecked")
    protected void addFlavorFields(Table table){
        final int flavorID = numFlavors - 1; //id of flavor added

        //adding the flavor name label & text field
        Label flavNameLabel = new Label("", skin);
        table.add(flavNameLabel);
        
        TextField flavorTextField = new TextField("", skin, "nameTextField");
        //flavorTextField.setName(fieldName);
        flavorTextField.setTextFieldListener(UIUtils.flavorNameListener(flavorID));
        flavorTextField.setMessageText("Flavor " + Integer.toString(numFlavors));
        flavorTextField.setColor(Color.RED);
//        flavorTextField.setCursorPosition(2);
        flvrTitleTFs.add(flavorTextField);

        setCheckBoxes(flavorID);

        //supply amount. if any
        Label supplyLabel = new Label("x", skin, "supplyLabel");
        supplyLabel.setAlignment(Align.center);
        supplyLabel.getStyle().fontColor = Color.RED;
        supplyLabels.add(supplyLabel);
        setSupplyLabel(currFlavor.getTotalAmount(), currFlavor.getKey(), supplyLabels.peek());

        addSelectBox(flavorID, table); //add the select box followed by textfield
        table.add(flavorTextField).width(100).height(FIELD_HEIGHT).align(Align.left).padLeft(2f);
        
        //where calculated amount of flavor needed goes
        Label calcLabel = new Label("---", skin, "calcsLabel");
        calcLabel.setAlignment(Align.center);
        calcLabels.add(calcLabel);
        table.add(calcLabels.peek()).width(50).align(Align.center); //where the calculated value goes
        
        //add the supply labels here
        table.add(supplyLabels.peek()).width(50).align(Align.center);
        
        
        table.row();
        addFlavorPercentFields(table, flavorID); //2nd row
        
        //the delete button will delete flavor from the table
        final Button deleteButton = new Button(skin, "delete");
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (deleteButton.isPressed()) {
                    removeFlavorFields(flavorID);
                }
            }
        });
        add();
        table.add(deleteButton).align(Align.right);
        
        
        table.row();
        addCheckBoxes(table, flavorID); //3rd row - add checkboxes
    }

    //sets the supply labels with amounts previously stored (if it was)
    public void setSupplyLabel(double amount, int flavKey, Label label){
        if (calcUtils.supplied){
            if (calcUtils.getSupplyAmounts().containsKey(flavKey)){
                amount = calcUtils.getSupplyAmounts().get(flavKey);  //get the amount
            }
        }

        //different colors for how much user has
        if (amount > 30) label.getStyle().fontColor = Color.BLACK;
        else if (amount > 15) label.getStyle().fontColor = Color.BLUE;
        else  label.getStyle().fontColor = Color.RED;

        label.setText(Double.toString(amount));
    }
    
    
    public void setFlavorSupplyNameFields(){
        if (calcUtils.flavorSupplied){
            for (int i = 0; i < supplyFlavors.size; i++){
                Flavor f = supplyFlavors.get(i);
                flavorNames.add(f.getName());
                flavorMap.put(f.getName(), f);
            }
        }
    }

    
    //a drop-down list for when typing the flavor, so that user can select a flavor in list
    protected void addSelectBox(final int flavorID, Table table){
        final SelectBox dropDown = new SelectBox(skin); //select box
        dropDown.setItems(flavorNames);
        dropDown.setSelectedIndex(0); //sets 1st item as selected
        
        dropDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                log(" switched flavor to " + dropDown.getSelected().toString());
                switchFlavor(flavorID, dropDown.getSelected().toString());
            }
        });

        table.add(dropDown).width(125).height(FIELD_HEIGHT);
    }
    

    //percent fields for percentage of flavor in recipe
    protected void addFlavorPercentFields(Table table, final int id){
        //adding the flavor percent label & textfield
        Label flavPercLabel = new Label("Flavor %: ", skin);
//        flavPercLabel.setAlignment(Align.right);
        table.add(flavPercLabel).align(Align.right).colspan(2).padRight(2);
        
        TextField flavorPercentField = new TextField("", skin, "numTextField");
        flavorPercentField.setName(fieldName);
        flavorPercentField.setTextFieldListener(UIUtils.flavorPercentListener(id));
        flavorPercentField.setMaxLength(2);
        flavorPercentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        flavorPercentField.setColor(Color.GRAY);
        
        if (currFlavor.getPercent() > 0)
            flavorPercentField.setText(Integer.toString(currFlavor.getPercent()));
        
        flvrPercsTFs.add(flavorPercentField);
        
        table.add(flavorPercentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left).padLeft(2f);
        
    }

    //sets up the checkboxes
    protected void setCheckBoxes(final int id){
        //adding the checkboxes
        CheckBox pgCheck = new CheckBox("PG ", skin);
        pgCheck.setName("flavorCheckBoxPG_" + Integer.toString(id + 1));
        pgCheck.addListener(flavorBoxListener(id));
        

        CheckBox vgCheck = new CheckBox("VG ", skin);
        vgCheck.setName("flavorCheckBoxVG_" + Integer.toString(id + 1));
        vgCheck.addListener(flavorBoxListener(id));
        

        CheckBox otherCheck = new CheckBox("EtOH/H2O/etc ", skin);
//        otherCheck.setScale(scaleX, scaleY);
        otherCheck.setName("flavorCheckBoxOther_"+Integer.toString(id + 1));
        otherCheck.addListener(flavorBoxListener(id));


        //add to flavor checkBox array and into map
        Array<CheckBox> boxes = new Array<CheckBox>();
        boxes.add(pgCheck);
        boxes.add(vgCheck);
        boxes.add(otherCheck);
        checkBoxMap.put(id, boxes);
    }

    //actually adds the checkboxes
    protected void addCheckBoxes(Table table, int id){
        table.add(checkBoxMap.get(id).get(0)).width(80f).height(20f);
        table.add(checkBoxMap.get(id).get(1)).width(80f).height(20f);
        table.add(checkBoxMap.get(id).get(2)).width(180f).height(20f);
        table.row();
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

                //delete the set the labels
                calcLabels.set(i, calcLabels.removeIndex(i + 1));
                supplyLabels.set(i, supplyLabels.removeIndex(i + 1));
            }
        }
        numFlavors--; //decrement number of flavors

        vertGroup.removeActor(children.removeIndex(children.size - 1));
    }
    
    
    //switches flavor if new flavor chosen from selectbox
    public void switchFlavor(int index, String name){
        Flavor f = flavorMap.get(name);

        flvrTitleTFs.get(index).setText(name);
        calcUtils.switchFlavor(index, f);
        supplyLabels.get(index).setText(Double.toString(f.getTotalAmount()));

        //only set checkboxes if the flavor type is 0-2
        if (f.getType() > 0) {
            checkBoxMap.get(index).get(f.getType()).setChecked(true);
            calcUtils.setFlavorType(checkBoxMap.get(index).get(f.getType()), index);
        }
            
        currFlavor = f;
    }

    //updates/sets the calcLabels amounts
    public void updateCalcLabels(){
        Array<Double> finalMills = calcUtils.getFinalMills();
        
        for (int i = 0; i < calcLabels.size; i++ ){
            double amount = finalMills.get(i+4).doubleValue();
            String text = amount + " (" + (int)(amount*20)+")";
            calcLabels.get(i).setText(text);
        }
    }



    public void updateSupplyLabels(){
        if (calcUtils.flavorSupplied){
            for (int i = 0; i < calcUtils.getSupplyFlavors().size; i++){
                setSupplyLabel(0, calcUtils.getSupplyFlavors().get(i).getKey(), supplyLabels.get(i));
            }
        }

    }

    
    public void setLoadedData(){
        //set up the flavors
        for (Flavor f : calcUtils.getFlavors()){
            if (calcUtils.getFlavors().size > numFlavors){
                if (flavorMap.containsKey(f.getName()))
                    addNewFlavor(flavorMap.get(f.getName()));
                else
                    addNewFlavor(new Flavor("Flavor " + numFlavors));
            }


            for (TextField tf: flvrTitleTFs)
                tf.setMessageText(f.getName());

            for (TextField tf : flvrPercsTFs)
                tf.setMessageText(Double.toString(f.getPercent()));
        }
        
    }
    
 
    private ChangeListener flavorBoxListener(final int id){
        ChangeListener flavorBoxListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox box = ((CheckBox)actor);
                if (box.isChecked()){
                    //int i = Integer.parseInt(box.getName().substring(box.getName().length()-1))-1;
                    log( "Flavor whose type changed, index = " + id);
                    
                    calcUtils.setFlavorType(box, id);
                }
            }
        };

        return flavorBoxListener;
    }

    private void log(String message){
        Gdx.app.log("FlavorTable LOG: ", message);
    }
    
}