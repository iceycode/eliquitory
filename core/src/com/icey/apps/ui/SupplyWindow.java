package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

/** Popup window that contains UI for setting supplies
 * - varies based on what supply is being added
 *
 * TODO: make background opaque (need to create new texture for this)
 * TODO: make sure if editing, all values are set from previous supply - open another dialog if not
*      TODO: fix edit window! Use a new constructor & create boolean edit
 *
 * Created by Allen on 1/28/15.
 */
public class SupplyWindow extends Dialog{

    public Supply supply; //the current supply being set up
    
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    Skin skin;
    Table table;

    int category = -1; //supply category (0=Supply, 1=Base, 2=Flavor)
    int flavorCount = supplyUtils.lastFlavorKey; //number of flavors - used to set the key
    
    boolean edit = false;
    
    public static Array<TextField> percentTextFields;

    public SupplyWindow(Skin skin, int type, Supply supply, boolean edit) {
        super("", skin); //no title, label added instead
        this.skin = skin;

//        setModal(true);
        setMovable(true);

        table = getContentTable(); //the main table
        table.debug();
        table.top().center(); //set to top left
        table.setWidth(400); //max width of table

        this.supply = supply;
        this.category = type;

        if (type == 0) setSupplyTable();
        else if (type == 1) setBaseTable();
        else setFlavorTable();
    }



    //the widgets set if user is adding/editing a supply
    public void setSupplyTable(){
//        this.supply = supply;

        Label titleLabel = new Label("Add Liquid Supply", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).fillX().expandX().colspan(6);

        table.row().padTop(5); //row 2
        Label amtLabel = new Label("Amount (ml): ", skin);
        table.add(amtLabel).align(Align.right).padRight(2);
        TextField amountTF = new TextField("", skin);
        amountTF.setTextFieldListener(amountListener(0));
        table.add(amountTF);

        table.row(); //row for checkboxes
        if (edit){
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            setCheckBoxes(0, supply.getSupplyType());
        }
        else{
            amountTF.setMessageText("0");
            setCheckBoxes(0, -1);
        }


        table.row().padTop(10);
        setSaveButton();
        setCancelButton();


    }


    //the widgets created if user adding/editing a base
    public void setBaseTable(){
        supply.setSupplyType(3);
        
        
        percentTextFields = new Array<TextField>();


        //2 columns, 7 rows total
        Label titleLabel = new Label("Add Nicotine Base", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).fillX().expand().colspan(2);

        table.row().padTop(5); //amount fields - row 2
        Label amtLabel = new Label("Amount (ml): ", skin);
        table.add(amtLabel).align(Align.right).padRight(2);
        
        TextField amountTF = new TextField("", skin);
        amountTF.setName("amount");
        amountTF.setTextFieldListener(amountListener(0));
        
        table.add(amountTF);

        table.row().padBottom(5); //strength fields - row 3
        Label strengthLabel = new Label("Strength (mg): ", skin);
        table.add(strengthLabel).align(Align.right).padRight(2).padBottom(10);
        
        TextField strengthTF = new TextField("", skin);
        strengthTF.setName("strength");
        strengthTF.setTextFieldListener(amountListener(1));

        table.add(strengthTF);

        //set the fields if base total amount is not 0 - signifies it was set before
        if (edit){
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            strengthTF.setMessageText(Double.toString(supply.getBaseStrength()));
        }
        else{
            amountTF.setMessageText("0.0");
            strengthTF.setMessageText("0.0");
            supply.setBasePercents(Constants.ZERO_BASE_PERCENTS); //set empty percents
            
        }
        
        
        addPercentSlider(supply.getBasePercents().first());
        //percent fields - rows 4 - 6
        addPercentFields(supply.getBasePercents()); //add empty percentfields

        table.row().padTop(10); //save button - row 7
        setSaveButton();
        setCancelButton();
    }
    
    public void setFlavorTable(){
//        this.flavor = flavor;
//        table.top().left();
        supply.setSupplyType(4);

        Label titleLabel = new Label("Add Flavor", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).expandX().fillX().colspan(6);

        table.row().padTop(5); //name fields - row 2
        Label flavLabel = new Label("Flavor Name: ", skin);
        table.add(flavLabel).align(Align.right).padRight(2);
        
        TextField nameField = new TextField("", skin);
        nameField.setTextFieldListener(flavorNameListener());
        table.add(nameField);


        table.row(); //amount fields - row 3
        Label amtLabel = new Label("Amount (ml): ", skin); //label for amount
        table.add(amtLabel).align(Align.right).padRight(2);

        TextField amountTF = new TextField("", skin); //textfield for amount
        amountTF.setTextFieldListener(amountListener(0));
        table.add(amountTF);


        table.row().padTop(10); //checkboxes - row 3

        //if flavor exists, set field texts & also checkBoxes
        if (edit) {
            nameField.setMessageText(supply.getName());
            amountTF.setMessageText(Double.toString(supply.getTotalAmount()));
            setCheckBoxes(2, supply.getFlavorType()); //the checkboxes
        }
        else {
            nameField.setMessageText("Flavor name here");
            setCheckBoxes(2, -1); //no checkbox is hit
        }


        table.row().padTop(10); //save button - row 4
        setSaveButton();
        setCancelButton();

        flavorCount++; //increment number of flavors
    }


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

    /** adds the checkboxes 
     * - also sets a checkbox as checked based on the 2nd parameter
     * 
     * @param category : pg, vg or other
     * @param checkedBox : the checkbox which will be checked
     */
    private void setCheckBoxes(int category, int checkedBox){
        for (int i = 0; i < 3; i++){
            String name = Constants.SUPPLY_CHECKBOX_TITLES[i];
            CheckBox typeCheckBox = new CheckBox(name, skin);
            typeCheckBox.setName(name);
            typeCheckBox.addListener(checkBoxListener(category));
            
            //whether this is checked or not
            if (i == checkedBox)
                typeCheckBox.isChecked();
            
            table.add(typeCheckBox).width(100).height(20f);
        }

    }
    
    public void setLiquidType(String name){
        if (name.contains("PG"))
            supply.setSupplyType(0);
        else if (name.contains("VG"))
            supply.setSupplyType(1);
        else
            supply.setSupplyType(2);
    }


    /** the type of flavor based on name
     * 
     * @param name : PG, VG or Other
     */
    public void setFlavorType(String name){
        if (name.equals("PG"))
            supply.setFlavorType(0);
        else if (name.equals("VG"))
            supply.setFlavorType(1);
        else
            supply.setFlavorType(2);
    }
    

    protected void addPercentFields(Array<Integer> percents){

        for (int i = 0; i < 2; i++){
            table.row();
            Label pgLabel = new Label(Constants.PERC_FIELD_NAMES[i], skin);
            pgLabel.setAlignment(Align.right);
            
            TextField percentField = new TextField("", skin);
            percentField.setColor(Color.RED);
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            percentField.setMaxLength(2);
            percentField.setName(Constants.PERC_FIELD_NAMES[i]);
            percentField.setTextFieldListener(percentListener());
            
            percentField.setText(percents.get(i).toString());
            
            percentTextFields.add(percentField);

            table.add(pgLabel).width(50).height(25).align(Align.right).padRight(2);
            table.add(percentField).width(40).height(25).padRight(5).align(Align.left);
        }
    }
    
    
    //add a slider for setting base percents
    protected void addPercentSlider(int pgPercent){
        table.row();
        //add(); //empty cell so that slider over percent text fields
        
        final Slider slider = new Slider(0, 100, 1, false, skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                supply.setPgPercent((int)slider.getValue());
                percentTextFields.get(0).setText(Integer.toString((int)(slider.getValue())));
            }
        });
        
        slider.setValue(pgPercent); //set slider to PG value
        
        Label ratioLabel = new Label("PG/VG ratio", skin);
        ratioLabel.setAlignment(Align.right);
        
        table.add(ratioLabel);
        table.add(slider).align(Align.center);
    }
    
    
    //--------Window Listeners------//
    protected TextField.TextFieldListener flavorNameListener(){
        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else{
//                    if (c >= 'a' && c <='9')
                    supply.setName(textField.getText());
                }
                    
            }
        };

        return nameTextFieldListener;
    }

    protected TextField.TextFieldListener percentListener(){
        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log("Percentage typed: " + c + ", for " + textField.getName());
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= '0' && c <= '9'){
                    String name = textField.getName();
                    if (name.equals(Constants.PERC_FIELD_NAMES[0]))
                        supply.setPgPercent(Integer.parseInt(textField.getText()));
                    else
                        supply.setVgPercent(Integer.parseInt(textField.getText()));
                }
            }
        };

        return percentFieldListener;
    }

    
    protected TextField.TextFieldListener amountListener(final int type){
        TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c){
                log( "Amount typed: " + c);

//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }


                if ((c <= '9' && c >= '0')|| c == '.') {
                    if (type == 0) {
                        supply.setTotalAmount(Double.parseDouble(textField.getText()));
                    }
                    else {
                        supply.setBaseStrength(Double.parseDouble(textField.getText()));
                    }
                }
            }
        };

        return numTextFieldListener;
    }

    protected ChangeListener checkBoxListener(final int category){
        ChangeListener flavorBoxListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox box = ((CheckBox)actor);
                if (box.isChecked()){
                    log("category checkbox belongs to is ");

                    if (category == 0){
                        setLiquidType(box.getName());
                    }
                    else {
                        setFlavorType(box.getName());
                    }
                }
            }
        };

        return flavorBoxListener;
    }

    //error dialog if something is not entered correctly
    protected Dialog errorDialog(String errorMsg){
        return new Dialog("", skin)
                .text(errorMsg).button("Fix").show(SupplyScreen.instance.getStage());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        //dismisses if click anywhere on outside of dialog box
        if (Gdx.input.isTouched()){
            if (Gdx.input.getX() > (getX() + getWidth()) || Gdx.input.getX() < getX()) {
                hide();
                remove();
            }
            if (Gdx.input.getY() < getY() || Gdx.input.getY() > (getY() + getHeight())) {
                hide();
                remove();
            }
        }

    }

    private void log(String message){
        Gdx.app.log("SupplyWindow LOG: ", message);
        
    }

}
