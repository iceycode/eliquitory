package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Supply;
import com.icey.apps.screens.SupplyScreen;
import com.icey.apps.utils.SupplyUtils;
import com.icey.apps.utils.UIUtils;

/** Popup window that contains UI for setting supplies
 * - varies based on what supply is being added
 *
 * NOTE: fix for supply table tab - had to remove padding from the rows - this did not effect other tables
 *
 * TODO: add a selectbox to table if "Other" is chosen to give it a name
 *
 * Created by Allen on 1/28/15.
 */
public class SupplyWindow extends Dialog{

    public Supply supply; //the current supply being set up
    
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    Skin skin;
    Table table;

    int flavorCount = supplyUtils.lastFlavorKey; //number of flavors - used to set the key
    
    boolean edit = false;
    public static Array<TextField> percentTextFields;
    ButtonGroup<CheckBox> buttonGroup; //group for textboxes


    /** constructor for supplywindow - editing or adding
     *
     * @param skin : skin from supplyScreen
     * @param style : String, style name
     * @param supply : the supply being manipulated
     * @param edit : whether edit supply or not
     */
    public SupplyWindow(Skin skin, String style, Supply supply, boolean edit) {
        super("", skin, style); //no title, label added instead
        this.skin = skin;
        this.edit = edit; //for editing

        //setSize(400, 300); //sets the size of window

        //center(); //center the supplyWindow
        setModal(true);
        setKeepWithinStage(true);

        table = getContentTable(); //the main table
        table.setClip(true);
//        table.debug();
        table.top(); //set to top
        
        this.supply = supply;

        if (style.equals("default")) setSupplyTable();
        else if (style.equals("base")) setBaseTable();
        else setFlavorTable();
    }



    //the widgets set if user is adding/editing a supply
    public void setSupplyTable(){
        int checkBox = -1; //checkbox to check

        log(supply.toString());
        
        Label titleLabel = new Label("", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        Label amtLabel = new Label("Amount (ml): ", skin);
        
        TextField amountTF = new TextField("", skin);
        amountTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        amountTF.setTextFieldListener(UIUtils.SupplyUI.amountListener(0, supply));

        if (edit){
            titleLabel.setText("Edit Liquid Supply");
            amountTF.setStyle(skin.get("editField", TextField.TextFieldStyle.class));
            
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            
            checkBox = supply.getSupplyType();
        }
        else{
            titleLabel.setText("Add Liquid Supply");
            amountTF.setMessageText("0");
        }
        
        //ROW 1 - title 
        table.add(titleLabel).expandX().fillX().colspan(6).top().padBottom(5);

        // ROW 2 - amount of supply
        table.row(); //.padTop(5)
        table.add(amtLabel).align(Align.right).padRight(2);
        table.add(amountTF);

        // ROW 3 - checkboxes
        table.row(); //.padTop(10)
        setCheckBoxes(0, checkBox);
        
        // ROW 4
        table.row(); //.padTop(10);
        setSaveButton();
        setCancelButton();
    }


    //the widgets created if user adding/editing a base
    public void setBaseTable(){
        supply.setSupplyType(3);
        percentTextFields = new Array<TextField>();
        
        log(supply.toString());

        //2 columns, 7 rows total
        Label titleLabel = new Label("", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        Label amtLabel = new Label("Amount (ml): ", skin);

        TextField amountTF = new TextField("", skin);
        amountTF.setName("amount");
        amountTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        amountTF.setTextFieldListener(UIUtils.SupplyUI.amountListener(0, supply));

        Label strengthLabel = new Label("Strength (mg): ", skin);
        TextField strengthTF = new TextField("", skin);
        strengthTF.setName("strength");
        strengthTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        strengthTF.setTextFieldListener(UIUtils.SupplyUI.amountListener(1, supply));

        //set the fields if editable or not
        if (edit){
            titleLabel.setText("Edit Nicotine Base");
            
            //widget font colors are blue if editable
            amountTF.setStyle(skin.get("editField", TextField.TextFieldStyle.class));
            strengthTF.setStyle(skin.get("editField", TextField.TextFieldStyle.class));
            
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            strengthTF.setMessageText(Double.toString(supply.getBaseStrength()));
        }
        else{
            titleLabel.setText("Add Nicotine Base");
            amountTF.setMessageText("0.0");
            strengthTF.setMessageText("0.0");
            supply.setBasePercents(Constants.ZERO_BASE_PERCENTS); //set empty percents
        }
        
        //ROW 1 - title
        table.add(titleLabel).fillX().expand().colspan(2).top();

        //ROW 2 - Amounts
        table.row().padTop(5);
        table.add(amtLabel).align(Align.right).padRight(2);
        table.add(amountTF);

        //ROW 3 - Strength of nicotine
        table.row().padBottom(5);
        table.add(strengthLabel).align(Align.right).padRight(2).padBottom(5);
        table.add(strengthTF);

        //ROW 4 - 6 PG:VG ratio slider, PG, VG textfields respectively
        addPercentFields(supply.getBasePercents()); //add empty percentfields

        //ROW 7 - buttons
        table.row().padTop(5);
        setSaveButton();
        setCancelButton();
    }
    
    public void setFlavorTable(){ 
        supply.setSupplyType(4);
        log(supply.toString());
        
        int checkBox = -1; //checkbox to check
        
        Label titleLabel = new Label("", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);

        Label flavLabel = new Label("Flavor Name: ", skin);
        TextField nameField = new TextField("", skin);
        nameField.setTextFieldListener(UIUtils.SupplyUI.flavorNameListener(supply));

        Label amtLabel = new Label("Amount (ml): ", skin); //label for amount
        TextField amountTF = new TextField("", skin); //textfield for amount
        amountTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        amountTF.setTextFieldListener(UIUtils.SupplyUI.amountListener(0, supply));
        
        //if flavor exists, set field texts & also checkBoxes
        if (edit) {
            titleLabel.setText("Edit Flavor");
            nameField.setStyle(skin.get("editField", TextField.TextFieldStyle.class));
            amountTF.setStyle(skin.get("editField", TextField.TextFieldStyle.class));
            
            nameField.setMessageText(supply.getName());
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            checkBox = supply.getFlavorType();
        }
        else {
            titleLabel.setText("Add Flavor");
            nameField.setMessageText("Flavor name here");
        }

        //ROW 1 - title
        table.add(titleLabel).expandX().fillX().colspan(6).top();

        //ROW 2 - flavor name
        table.row().padTop(5); 
        table.add(flavLabel).align(Align.right).padRight(2);
        table.add(nameField);

        //ROW 3 - Amount
        table.row();
        table.add(amtLabel).align(Align.right).padRight(2);
        table.add(amountTF);

        //ROW 4 - checkboxes for type
        table.row();
        setCheckBoxes(2, checkBox); //the checkboxes

        //ROW 5 - buttons
        table.row().padTop(10);
        setSaveButton();
        setCancelButton();

        flavorCount++; //increment number of flavors
    }
    

    /** adds percent fields for base
     *
     * @param percents : the base percents
     */
    protected void addPercentFields(Array<Integer> percents){


        Array<Label> percentLabels = new Array<Label>();
        //set up the percent text fields & labels
        for (int i = 0; i < 2; i++){
            Label pgLabel = new Label(Constants.PERC_FIELD_NAMES[i], skin);
            pgLabel.setAlignment(Align.right);

            TextField percentField = new TextField("", skin);
            percentField.setColor(Color.RED);
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            percentField.setMaxLength(2);
            percentField.setName(Constants.PERC_FIELD_NAMES[i]);
            percentField.setTextFieldListener(UIUtils.SupplyUI.percentListener(supply));

            if (edit) {
                percentField.setStyle(skin.get("editField", TextField.TextFieldStyle.class));

            }

            percentField.setMessageText(percents.get(i).toString());

            percentTextFields.add(percentField);
            percentLabels.add(pgLabel);
        }

        //set up the slider & percent text fields
        final Slider slider = new Slider(0, 100, 1, false, skin);
        slider.setValue(percents.get(0)); //set slider to PG value
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                supply.setPgPercent((int)slider.getValue());
                percentTextFields.get(0).setText(Integer.toString((int)(slider.getValue())));
            }
        });

        Label ratioLabel = new Label("PG/VG ratio", skin);
        ratioLabel.setAlignment(Align.right);

        //row 4 - slider
        table.row();
        table.add(ratioLabel);
        table.add(slider).center().padBottom(5);

        //rows 5-6 percent textfield labels
        for (int i = 0; i < 2; i++){
            table.row();
            table.add(percentLabels.get(i)).width(50).height(25).align(Align.right).padRight(2);
            table.add(percentTextFields.get(i)).width(40).height(25).padRight(5).left();
        }
    }


    /** adds the checkboxes 
     * - also sets a checkbox as checked based on the 2nd parameter
     * 
     * @param category : pg, vg or other
     * @param checkedBox : the checkbox which will be checked
     */
    private void setCheckBoxes(int category, int checkedBox){
        //checkBox button group - for setting max buttons checked
        buttonGroup = new ButtonGroup<CheckBox>();
        buttonGroup.setMaxCheckCount(1); //sets max check count to 1
        
        for (int i = 0; i < 3; i++){
            String name = Constants.SUPPLY_CHECKBOX_TITLES[i];
            CheckBox typeCheckBox = new CheckBox(name, skin);
            typeCheckBox.setName(name);
            //whether this is checked or not
            if (i == checkedBox)
                typeCheckBox.setChecked(true);
            
            typeCheckBox.addListener(UIUtils.SupplyUI.checkBoxListener(category, this));

            buttonGroup.add(typeCheckBox); //add to button group
            
            table.add(buttonGroup.getButtons().get(i)).width(100).height(20f).padTop(10);
        }
    }


    //adds a select field if "Other" is checked when adding Supply (not flavor)
    //allows user to select which kind of "Other" it is; even giving it a name
    protected void setOtherSelectBox(){

        final SelectBox<String> selectBox = new SelectBox<String>(skin);
        selectBox.setItems(Constants.OTHER_NAMES);
        selectBox.setSelected(selectBox.getItems().get(selectBox.getItems().size -1));

        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                supply.setName(selectBox.getSelected().toString());

                if (selectBox.getSelected().equals("Other")){
                    setOtherNameField();
                }
            }
        });

        table.row();
        table.add(selectBox).height(25).width(75); //adds to index 6; right after checkBox "Other"

    }


    //adds yet another field - a textfield for user to specify "Other" name
    protected void setOtherNameField(){
        Label label = new Label("Name: ", skin);
        table.add(label).height(25).width(50);

        TextField nameField = new TextField("", skin);
        //TODO: add a listener to this
        nameField.setMessageText("Name of supply");
        nameField.setTextFieldListener(UIUtils.SupplyUI.supplyNameListener(supply));

        table.add(nameField).width(100).height(25).right().padLeft(2);
    }

    
    public void setLiquidType(String name){
        if (name.contains("PG")){
            supply.setSupplyType(0);
        }
        else if (name.contains("VG")){
            supply.setSupplyType(1);
        }
        else{
            supply.setSupplyType(2);
            setOtherSelectBox();
        }
    }


    /** the type of flavor based on name
     * 
     * @param name : PG, VG or Other
     */
    public void setFlavorType(String name){
        if (name.contains("PG")){
            supply.setFlavorType(0);
        }
        else if (name.contains("VG")){
            supply.setFlavorType(1);
        }
        else{
            supply.setFlavorType(2);
            setOtherSelectBox();
        }
    }


    //save button
    protected void setSaveButton(){
        final TextButton saveButton = new TextButton("Save", skin, "save");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (saveButton.isPressed()){
                    if (supply.isSupplySet()) {
                        supplyUtils.saveSupply(supply.getSupplyType(), supply);
                        remove(); //removes it from stage
                    }
                    else{
                        errorDialog(supply.getErrorMessage());
                    }

                }
            }
        });

        table.add(saveButton).width(100).align(Align.center);

    }


    //cancels user's entering of supply value
    protected void setCancelButton(){
        final TextButton cancelButton = new TextButton("Cancel", skin, "save"); //same as save style
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (cancelButton.isPressed()) {
                    remove();
                }
            }
        });

        table.add(cancelButton).width(100).align(Align.center);
    }


    //error dialog if something is not entered correctly
    protected Dialog errorDialog(String errorMsg){
        return new Dialog("", skin)
                .text(errorMsg).button("Fix").show(SupplyScreen.instance.getStage());
    }

    
//    @Override
//    public void act(float delta) {
//        super.act(delta);
//
////        //dismisses if click anywhere on outside of dialog box
////        if (Gdx.input.isTouched()){
////            if (Gdx.input.getX() > (getX() + getWidth()) || Gdx.input.getX() < getX()) {
////                hide();
////                remove();
////            }
////            if (Gdx.input.getY() < getY() || Gdx.input.getY() > (getY() + getHeight())) {
////                hide();
////                remove();
////            }
////        }
//    }
 
    
    
    private void log(String message){
        Gdx.app.log("SupplyWindow LOG: ", message);
        
    }

}
