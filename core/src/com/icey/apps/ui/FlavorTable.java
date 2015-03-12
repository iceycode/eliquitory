package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.MainApp;
import com.icey.apps.data.Flavor;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.Constants;
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
//    CalcUtils calcUtils;

    boolean supplyEnabled = MainApp.supplyEnabled;

    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;

    //the group & collections of individual flavor widgets
    VerticalGroup vertGroup; //vertical group for insertion/removal of flavor fields
    public Array<Label> calcLabels = new Array<Label>();; //labels for the calculated amounts to add
//    public Array<Label> supplyLabels = new Array<Label>();; //flavor supply labels
    public Array<TextField> flavorNameTFs = new Array<TextField>(); //flavor names

    public Array<Slider> flavorSliders = new Array<Slider>(); //array for sliders
    public static Array<Label> percentLabels = new Array<Label>();

//    public Array<TextField> flvrTitleTFs = new Array<TextField>();
    public Array<ButtonGroup<CheckBox>> checkBoxes = new Array<ButtonGroup<CheckBox>>();
    
//    Array<Flavor> supplyFlavors; //flavors in the users saved supply
    Array<String> flavorNames = new Array<String>(); //flavor names in supply - go into selectbox;

    public static ObjectMap<String, Flavor> flavorMap= new ObjectMap<String, Flavor>(); //to map out flavors in supply
    
    Flavor currFlavor; //current flavor being added
    public int numFlavors = 0; //the number of flavors


    public FlavorTable(Skin skin){
        this.skin = skin;

        setTableProperties(); //table properties
//        if (supplyEnabled)
//            setSupplies(); //set the supplies (if any)

        //debug();
        initFlavorGroup(); //set the flavor group
    }

    //sets table properties
    protected void setTableProperties(){
        //        defaults().maxWidth(480).maxHeight(200);
        //setFillParent(true); //causes space to occur at ScrollPane
        top();

        //debug(); //debug this table
        //pack();
    }


//    //sets the supplies
//    protected void setSupplies(){
//        supplyLabels = new Array<Label>();
//
//        //the supllied flavors
//        if (CalcUtils.getCalcUtil().flavorSupplied) {
//            supplyFlavors = CalcUtils.getCalcUtil().getSupplyFlavors();
//            setFlavorSupplyNames();
//        }
//    }


    //sets the vertical group which holds the flavors
    protected void initFlavorGroup(){
        initFirstFlavor(); //initialize first flavor

        vertGroup = new VerticalGroup(); //add indiviual flavors to a VerticalGroup
        vertGroup.align(Align.top);

        addNewFlavor(currFlavor); //adds 1 flavor initially
        add(vertGroup).top(); //.expandX() add this nested table into the root flavor table
    }


    //adds 1st flavor to the table
    protected void initFirstFlavor(){
        flavorNames.add(Constants.NEW_FLAVOR_STRING); //add default value
        currFlavor = Constants.NEW_FLAVOR; //a new flavor initially added

        currFlavor.setName("Flavor " + Integer.toString(numFlavors + 1));

//        flavorMap.put(Constants.NEW_FLAVOR_STRING, currFlavor);
    }


    /** adds a new flavor
     *
     * @param flavor : flavor added
     */
    public void addNewFlavor(Flavor flavor){
        this.currFlavor = flavor;
        
//        if (!flavorMap.containsKey(flavor.getName())){
//            currFlavor.setName("Flavor " + Integer.toString(numFlavors + 1));
//            flavorMap.put(currFlavor.getName(), currFlavor);
//        }

        Table table = new Table(); //new table added to vertGroup
        table.top();
//        table.debug();
        table.padBottom(5);
        table.columnDefaults(1).width(125);

        table.align(Align.center);
        //table.defaults().colspan(4); //spans 4 columns across nested FlavorTable within root table

        //add into calc utility if is not already loaded
        CalcUtils.getCalcUtil().addFlavor(currFlavor);

        numFlavors++; //increment num of flavor fields
        addFlavorFields(table); //new flavor input fields

        vertGroup.addActor(table);
    }

    
    //flavor fields get added to a new table, which in turn is added to vertical group
    protected void addFlavorFields(Table table){
        final int flavorID = numFlavors - 1; //id of flavor added

        //add select box storing names of flavors (or new flavor)
        // the name, when selected will be added to the textfield
//        addSelectBox(flavorID, table);
        Label flavorName = new Label("Name: ", skin);
        flavorName.setAlignment(Align.right);
        table.add(flavorName).width(125).height(FIELD_HEIGHT).right();

//        TextField flavorTextField = new TextField("", skin, "nameTextField");
        TextField flavorTextField = new TextField("", skin);
        flavorTextField.setAlignment(Align.center);
//        flavorTextField.setTextFieldListener(UIUtils.flavorNameListener(flavorID));
        flavorTextField.setText(currFlavor.getName());
        UIUtils.setDialogKeyboard(flavorTextField, -2, "Flavor name", "Enter name.", flavorID);
        flavorNameTFs.add(flavorTextField);
        table.add(flavorTextField).width(110).height(FIELD_HEIGHT).left().padLeft(2f);

//        if (supplyEnabled){
//            setFlavorSupply();
//            table.add(supplyLabels.peek()).width(70).left().padLeft(15);  //.padLeft(145);
//        }
        addCalcLabel(table);
        
        table.row();
        addFlavorPercentFields(table, flavorID); //2nd row


        //the delete button will delete flavor from the table
//        final Button deleteButton = new Button(skin, "delete");
        final TextButton deleteButton = new TextButton("DEL", skin, "small");
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (deleteButton.isPressed()) {
                    removeFlavorFields(flavorID);
                    CalcUtils.getCalcUtil().removeFlavor(flavorID); //remove from calcutil flavors array
                }
            }
        });
        table.add(deleteButton).right().padLeft(11);
        
        //add checkboxes
        table.row().padTop(2);
        setCheckBoxes(table, flavorID, currFlavor.getType());
    }


    protected void addCalcLabel(Table table){
        Label label = new Label("0.0", skin, "default-red");
        calcLabels.add(label);

        table.add(label).padLeft(40);
    }


//    public void setFlavorSupply(){
//        //supply amount. if any
//        Label supplyLabel = new Label("0.0", skin);
//        supplyLabel.setAlignment(Align.center);
//
//        setSupplyLabel(currFlavor.getTotalAmount(), currFlavor.getKey(), supplyLabel);
//    }
//
//
//    //sets the supply labels with amounts previously stored (if it was)
//    public void setSupplyLabel(double amount, int flavKey, Label label){
//        if (CalcUtils.getCalcUtil().supplied){
//            if (CalcUtils.getCalcUtil().getSupplyAmounts().containsKey(flavKey)){
//                amount = CalcUtils.getCalcUtil().getSupplyAmounts().get(flavKey);  //get the amount
//            }
//        }
//
//        //different colors for how much user has
//        label = UIUtils.SupplyUI.flavorLabel(label, amount, skin);
//
//        label.setText(Double.toString(amount));
//
//        supplyLabels.add(label);
//    }





//    //this sets up an object map containing flavor and amount
//    //also adds to String Array, flavor names which go in a drop down SelectBox
//    protected void setFlavorSupplyNames(){
//        flavorNames.add("New Flavor");
//
//        if (CalcUtils.getCalcUtil().flavorSupplied){
//            for (int i = 0; i < supplyFlavors.size; i++){
//                Flavor f = supplyFlavors.get(i);
//                flavorNames.add(f.getName());
//                flavorMap.put(f.getName(), f);
//            }
//        }
//    }
//
//
//    //a drop-down list for when typing the flavor, so that user can select a flavor in list
////    @SuppressWarnings("unchecked") //for selectbox - need to specify class
//    protected void addSelectBox(final int flavorID, Table table){
//        final SelectBox<String> dropDown = new SelectBox<String>(skin); //select box
//        dropDown.setItems(flavorNames);
//        dropDown.setSelected("New Flavor"); //sets 1st item as selected
//
//        dropDown.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                String flavName = dropDown.getSelected().toString();
//                log(" switched flavor to " + flavName);
//                switchFlavor(flavorID, flavName);
//            }
//        });
//
//        table.add(dropDown).width(125).height(FIELD_HEIGHT);
//    }
    

    //percent fields for percentage of flavor in recipe
    protected void addFlavorPercentFields(Table table, final int id){
        //adding the flavor percent label & textfield
        Label flavPercLabel = new Label("Flavor % : 0", skin);
        percentLabels.add(flavPercLabel);

//        flavPercLabel.setAlignment(Align.right);
        table.add(flavPercLabel).padRight(2).right();

        // add a percent slider
        Slider slider = percentSlider(table, id);
        slider.setValue((float)currFlavor.getPercent()*1.5f);
        
//        TextField flavorPercentField = new TextField("", skin, "digit");
//        flavorPercentField.setAlignment(Align.center);
//        flavorPercentField.setTextFieldListener(UIUtils.flavorPercentListener(id));
//        flavorPercentField.setMaxLength(2);
//        flavorPercentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
//        flavorPercentField.setColor(Color.GRAY);
//
//        if (currFlavor.getPercent() > 0)
//            flavorPercentField.setMessageText("  " + Integer.toString(currFlavor.getPercent()));
//
//        flvrPercsTFs.add(flavorPercentField);
//
//        table.add(flavorPercentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).left().padLeft(2f);

    }


    protected Slider percentSlider(Table table, final int id){
        final Slider slider = new Slider(0, 150, 1.5f, false, skin, "large-horizontal");
        slider.setName("flavor");

        slider.addListener(UIUtils.sliderListener(slider, id));

        flavorSliders.add(slider);

        table.add(slider).width(150).padLeft(2).colspan(2).align(Align.left);

        return slider;
    }


    //sets up the checkboxes
    protected void setCheckBoxes(Table table, final int id, int checked){
        //checkBox button group - for setting max buttons checked
        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<CheckBox>();
        buttonGroup.setMaxCheckCount(1); //sets max check count to 1
        //buttonGroup.setMinCheckCount(0);
        //buttonGroup.setUncheckLast(false);

        //add the checkboxes to buttongroup
        for (int i = 0; i < 3; i++){
            CheckBox checkBox = new CheckBox(Constants.FLAV_TYPE_NAMES[i], skin);
            checkBox.addListener(UIUtils.checkBoxListener(i, id));

            if (i == checked)
                checkBox.setChecked(true);

            //add to button group
            buttonGroup.add(checkBox);

            table.add(checkBox).width(80f).height(22f);
        }

        checkBoxes.add(buttonGroup);
    }



    /** removes the flavor form the field
     *
     * @param flavID : the numeric id (order added) of flavor in group
     */
    public void removeFlavorFields(int flavID){

        SnapshotArray<Actor> children = vertGroup.getChildren(); //remove the table from this array

        if (children.size > 1){
            for (int i = flavID; i < children.size - 1; i++) {
                //delete from the vertical group
                children.swap(i, i + 1);
                vertGroup.swapActor(i, i + 1);

                //delete and set the labels
//                flavorNameTFs.set(i, flavorNameTFs.removeIndex(i + 1));
//                percentLabels.set(i, percentLabels.removeIndex(i + 1));
//                flavorSliders.set(i, flavorSliders.removeIndex(i + 1));
//                calcLabels.set(i, calcLabels.removeIndex(i+1));
                flavorNameTFs.swap(i, i+1);
                percentLabels.swap(i, i+1);
                flavorSliders.swap(i, i+1);
                calcLabels.swap(i, i+1);

//                supplyLabels.set(i, supplyLabels.removeIndex(i + 1)); //TODO: for supply version uncomment
            }
        }

        calcLabels.removeIndex(calcLabels.size - 1);
        flavorSliders.removeIndex(flavorSliders.size - 1);
        flavorNameTFs.removeIndex(flavorNameTFs.size - 1);
        percentLabels.removeIndex(percentLabels.size - 1);

        numFlavors--; //decrement number of flavors

        log("removed fields, number of flavors = " + numFlavors );

        vertGroup.removeActor(children.removeIndex(children.size - 1));
    }


//    //switches flavor if new flavor chosen from selectbox
//    public void switchFlavor(int index, String name){
//        currFlavor = flavorMap.get(name);
//
//        flvrTitleTFs.get(index).setText(name);
//
//        CalcUtils.getCalcUtil().switchFlavor(index, currFlavor)
//
//        if (supplyEnabled)
//            supplyLabels.get(index).setText(Double.toString(currFlavor.getTotalAmount()));
//
//        //only set checkboxes if the flavor type is 0-2
//        if (currFlavor.getType() > 0) {
//            checkBoxes.get(index).getButtons().get(currFlavor.getType()).setChecked(true);
//            CalcUtils.getCalcUtil().setFlavorType(currFlavor.getType(), index);
//        }
//
// }

    //updates/sets the calcLabels amounts
    public void updateCalcLabels(SnapshotArray<Double> finalAmounts){

        for (int i = 0; i < calcLabels.size; i++ ){
            double amount = finalAmounts.get(i+4).doubleValue();
            int drops = (int)(amount*Constants.DROPS_PER_ML);
            String text = amount + " (" + drops +")";
            calcLabels.get(i).setText(text);
        }
    }

    
    public void setLoadedData(){

        Array<Flavor> flavors = CalcUtils.getCalcUtil().getLoadedFlavors();

        int currNum = numFlavors; //since numFlavors changes
        //remove all current flavors
        for (int i = currNum; i > 0; i--) {
            removeFlavorFields(i);
        }

        CalcUtils.getCalcUtil().removeAllFlavors();

        //set up the flavors
        for (int i = 0; i < flavors.size; i++) {
            addNewFlavor(flavors.get(i));
//            if (i >= numFlavors) {
////                if (flavorMap.containsKey(f.getName())) //FIXME: enable in pro version
////                    addNewFlavor(flavorMap.get(f.getName()));
////                else
//
//            } else {
//                currFlavor = flavors.get(i);
//                flavorNameTFs.get(i).setText(currFlavor.getName());
//                flavorSliders.get(i).setValue((float) currFlavor.getPercent() * 1.5f);
//            }
        }

    }

    

    private void log(String message){
        Gdx.app.log("FlavorTable LOG: ", message);
    }
    
}