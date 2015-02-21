package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.MainApp;
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

    Skin skin;
    CalcUtils calcUtils = CalcUtils.getCalcUtil();

    boolean supplyEnabled = MainApp.supplyEnabled;

    private final float FIELD_WIDTH = Constants.TEXT_FIELD_WIDTH;
    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;

    //the group & collections of individual flavor widgets
    VerticalGroup vertGroup; //vertical group for insertion/removal of flavor fields
    public Array<Label> calcLabels; //labels for the calculated amounts to add
    public Array<Label> supplyLabels; //flavor supply labels
    public Array<TextField> flvrPercsTFs = new Array<TextField>();
    public Array<TextField> flvrTitleTFs = new Array<TextField>();
    public Array<ButtonGroup<CheckBox>> checkBoxes = new Array<ButtonGroup<CheckBox>>();
    
    Array<Flavor> supplyFlavors; //flavors in the users saved supply
    Array<String> flavorNames = new Array<String>(); //flavor names in supply - go into selectbox;
    ObjectMap<String, Flavor> flavorMap; //to map out flavors in supply to position in flavorTF array
    
    Flavor currFlavor; //current flavor being added
    public int numFlavors = 0; //the number of flavors
    String fieldName = "flavorFieldName_";

//    int cols = CalcTable.cols;


    public FlavorTable(Skin skin){
        this.skin = skin;

        setTableProperties(); //table properties

        initArray_Maps(); //initialize arrays/maps

        if (supplyEnabled)
            setSupplies(); //set the supplies (if any)

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
        calcLabels = new Array<Label>();
        supplyLabels = new Array<Label>();
        flavorMap = new ObjectMap<String, Flavor>(); //flavors set here (when added & from supply)
    }


    //sets the supplies
    protected void setSupplies(){
        supplyLabels = new Array<Label>();

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
        add(vertGroup).center().colspan(5).expandX(); //add this nested table into the root flavor table
    }


    //adds 1st flavor to the table
    protected void initFirstFlavor(){
        flavorNames.add(Constants.NEW_FLAVOR_STRING); //add default value
        currFlavor = Constants.NEW_FLAVOR; //a new flavor initially added

        flavorMap.put(Constants.NEW_FLAVOR_STRING, currFlavor);
    }


    /** adds a new flavor
     *
     * @param flavor : flavor added
     */
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
    protected void addFlavorFields(Table table){
        final int flavorID = numFlavors - 1; //id of flavor added

        //add select box
        addSelectBox(flavorID, table);

        TextField flavorTextField = new TextField("", skin, "nameTextField");
        //flavorTextField.setName(fieldName);
        flavorTextField.setTextFieldListener(UIUtils.flavorNameListener(flavorID));
        flavorTextField.setMessageText("Flavor " + Integer.toString(numFlavors));
        flavorTextField.setColor(Color.RED);
        flvrTitleTFs.add(flavorTextField);

        table.add(flavorTextField).width(100).height(FIELD_HEIGHT).align(Align.left).padLeft(2f);

        if (supplyEnabled){
            setFlavorSupply();
            table.add(supplyLabels.peek()).width(70).left().padLeft(70);
        }
        
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
        table.add(deleteButton).right().padLeft(120);
        
        //add checkboxes
        table.row();
        setCheckBoxes(table, flavorID);
    }


    public void setFlavorSupply(){
        //supply amount. if any
        Label supplyLabel = new Label("x", skin, "supply");
        supplyLabel.setAlignment(Align.center);
        supplyLabel.getStyle().fontColor = Color.RED;
        supplyLabels.add(supplyLabel);

        setSupplyLabel(currFlavor.getTotalAmount(), currFlavor.getKey(), supplyLabels.peek());
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
    
    
    protected void setFlavorSupplyNameFields(){
        flavorNames.add("New Flavor");

        if (calcUtils.flavorSupplied){
            for (int i = 0; i < supplyFlavors.size; i++){
                Flavor f = supplyFlavors.get(i);
                flavorNames.add(f.getName());
                flavorMap.put(f.getName(), f);
            }
        }
    }

    
    //a drop-down list for when typing the flavor, so that user can select a flavor in list
//    @SuppressWarnings("unchecked") //for selectbox - need to specify class
    protected void addSelectBox(final int flavorID, Table table){
        final SelectBox<String> dropDown = new SelectBox<String>(skin); //select box
        dropDown.setItems(flavorNames);
        dropDown.setSelected("New Flavor"); //sets 1st item as selected
        
        dropDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String flavName = dropDown.getSelected().toString();
                log(" switched flavor to " + flavName);
                switchFlavor(flavorID, flavName);
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
    protected void setCheckBoxes(Table table, final int id){
        //checkBox button group - for setting max buttons checked
        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<CheckBox>();
        buttonGroup.setMaxCheckCount(1); //sets max check count to 1

        //add the checkboxes to buttongroup
        for (int i = 0; i < 3; i++){
            CheckBox checkBox = new CheckBox(Constants.TYPE_NAMES[i], skin);
            checkBox.addListener(UIUtils.checkBoxListener(i, id));
            buttonGroup.add(checkBox);

            //add to button group
            buttonGroup.add(checkBox);

            table.add(buttonGroup.getButtons().get(i)).width(80f).height(20f);
        }

        checkBoxes.add(buttonGroup);
    }



    /** removes the flavor form the field
     *
     * @param flavID : the numeric id (order added) of flavor in group
     */
    public void removeFlavorFields(int flavID){

        SnapshotArray<Actor> children = vertGroup.getChildren(); //remove the table from this array

        //children.removeIndex(flavID); //remove from children of group
        calcUtils.removeFlavor(flavID); //remove from calcutil flavors array

        if (children.size > 1){
            for (int i = flavID; i < children.size - 1; i++) {
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
        currFlavor = flavorMap.get(name);

        flvrTitleTFs.get(index).setText(name);

        calcUtils.switchFlavor(index, currFlavor);

        if (supplyEnabled)
            supplyLabels.get(index).setText(Double.toString(currFlavor.getTotalAmount()));

        //only set checkboxes if the flavor type is 0-2
        if (currFlavor.getType() > 0) {
            checkBoxes.get(index).getButtons().get(currFlavor.getType()).setChecked(true);
            calcUtils.setFlavorType(currFlavor.getType(), index);
        }
    }

//    //updates/sets the calcLabels amounts
//    public void updateCalcLabels(){
//        Array<Double> finalMills = calcUtils.getFinalMills();
//
//        for (int i = 0; i < calcLabels.size; i++ ){
//            double amount = finalMills.get(i+4).doubleValue();
//            String text = amount + " (" + (int)(amount*20)+")";
//            calcLabels.get(i).setText(text);
//        }
//    }



//    public void updateSupplyLabels(){
//        if (calcUtils.flavorSupplied){
//            for (int i = 0; i < calcUtils.getSupplyFlavors().size; i++){
//                setSupplyLabel(0, calcUtils.getSupplyFlavors().get(i).getKey(), supplyLabels.get(i));
//            }
//        }
//
//    }

    
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
    

    private void log(String message){
        Gdx.app.log("FlavorTable LOG: ", message);
    }
    
}