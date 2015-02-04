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
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.Supply;
import com.icey.apps.screens.SupplyScreen;
import com.icey.apps.utils.SupplyUtils;

/** Popup window that contains UI for setting supplies
 * - varies based on what supply is being added
 *
 * TODO: make background opaque (need to create new texture for this)
 * TODO: make sure if editing, all values are set from previous supply - open another dialog if not
 *
 * Created by Allen on 1/28/15.
 */
public class SupplyWindow extends Dialog{
    
    //3 kinds of classes window widgets setup
    public Supply supply;
    public Base base;
    public Flavor flavor;
    
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();

    Skin skin;
    Table table;

    int category = -1; //supply category (0=Supply, 1=Base, 2=Flavor)
    int flavorCount = supplyUtils.lastFlavorKey; //number of flavors - used to set the key
    

    public SupplyWindow(Skin skin, Object miscSupply) {
        super("", skin); //no title, label added instead
        this.skin = skin;
        
        setModal(true); //set so that it disappears if clicked outside of OR widgets on inside?

        table = getContentTable(); //the main table
        table.debug();
        table.top().center(); //set to top left
        table.setWidth(400); //max width of table
        
        if (miscSupply instanceof Supply)
            setSupplyTable((Supply)miscSupply);
        else if (miscSupply instanceof Base)
            setBaseTable((Base)miscSupply);
        else
            setFlavorTable((Flavor)miscSupply);
    }


    //the widgets set if user is adding/editing a supply
    public void setSupplyTable(Supply supply){
        this.supply = supply;
        this.category = 0;

        Label titleLabel = new Label("Add Liquid Supply", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).expandX().fillX().colspan(6);

        table.row().padTop(5); //row 2
        Label amtLabel = new Label("Amount (ml): ", skin);
        table.add(amtLabel).align(Align.right).padRight(2);
        TextField amountTF = new TextField("", skin);
        amountTF.setTextFieldListener(amountListener());
        amountTF.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        table.add(amountTF);

        table.row(); //row for checkboxes
        if (supply.getTotalAmount() != 0){
            amountTF.setText(Double.toString(supply.getTotalAmount()));
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
    public void setBaseTable(Base base){
        this.base = base;
        this.category = 1;

        //2 columns, 7 rows total
        Label titleLabel = new Label("Add Nicotine Base", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).fillX().expand().colspan(2);

        table.row().padTop(5); //amount fields - row 2
        Label amtLabel = new Label("Amount (ml): ", skin);
        table.add(amtLabel).align(Align.right).padRight(2);
        
        TextField amountTF = new TextField("", skin);
        amountTF.setName("amount");
        amountTF.setTextFieldListener(amountListener());
        amountTF.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        amountTF.setMessageText(Double.toString(base.getTotalAmount()));
        table.add(amountTF);

        table.row().padBottom(5); //strength fields - row 3
        Label strengthLabel = new Label("Strength (mg): ", skin);
        table.add(strengthLabel).align(Align.right).padRight(2).padBottom(10);
        
        TextField strengthTF = new TextField("", skin);
        strengthTF.setName("strength");
        strengthTF.setTextFieldListener(amountListener());
        strengthTF.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        strengthTF.setMessageText(Double.toString(base.getBaseStrength()));
        table.add(strengthTF);

        //set the fields if base total amount is not 0 - signifies it was set before
        if (base.getTotalAmount()!= 0){
            amountTF.setText(Double.toString(base.getTotalAmount()));
            strengthTF.setText(Double.toString(base.getBaseStrength()));
        }
        else{
            amountTF.setMessageText("0");
            strengthTF.setMessageText("0");
        }

        //percent fields - rows 4 - 6
        addPercentFields(base.getBasePercents()); //add percentfields

        table.row().padTop(10); //save button - row 7
        setSaveButton();
        setCancelButton();
    }
    
    public void setFlavorTable(Flavor flavor){
        this.flavor = flavor;
//        table.top().left();

        Label titleLabel = new Label("Add Flavor", skin, "supplyTab");
        titleLabel.setAlignment(Align.center);
        table.add(titleLabel).expandX().fillX().colspan(6);

        table.row().padTop(5); //name fields - row 2
        Label flavLabel = new Label("Flavor Name:", skin);
        table.add(flavLabel).align(Align.right).padRight(2);
        
        TextField nameField = new TextField("", skin);
        nameField.setTextFieldListener(flavorNameListener());
        table.add(nameField);


        table.row(); //amount fields - row 3
        Label amtLabel = new Label("Amount (ml): ", skin); //label for amount
        table.add(amtLabel).align(Align.right).padRight(2);

        TextField amountTF = new TextField("", skin); //textfield for amount
        amountTF.setTextFieldListener(amountListener());
        amountTF.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        table.add(amountTF);


        table.row().padTop(10); //checkboxes - row 3

        //if flavor exists, set field texts & also checkBoxes
        if (flavor.getName()!= null) {
            nameField.setText(flavor.getName());
            amountTF.setText(Double.toString(flavor.getTotalAmount()));
            setCheckBoxes(2, flavor.getType()); //the checkboxes
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
    

    private void setSaveButton(){
        final TextButton saveButton = new TextButton("Save", skin, "save");
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (saveButton.isPressed()){
                    //adds the supply to the supply Utils
                    if (category == 0) {
                        if (supply.isSupplySet()) supplyUtils.saveSupply(supply.getSupplyType(), supply);
                        else errorDialog(Constants.ERROR_SUPPLY);

                    }
                    else if (category ==1) {
                        if (base.isBaseSet()) supplyUtils.saveSupply(3, base);
                        else errorDialog(Constants.ERROR_BASE);
                    }
                    else {
                        if (flavor.isFlavorSet()) supplyUtils.saveSupply(flavorCount, flavor);
                        else errorDialog(Constants.ERROR_FLAVOR);
                    }

                    hide(); //hides the window
                    remove(); //removes it from stage
                }
            }
        });

        table.add(saveButton).width(100).align(Align.center);
        
    }


    //cancels user's entering of supply value
    private void setCancelButton(){
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
    
    public void setCategory(String name){
        if (name.contains("PG"))
            supply.setSupplyType(0);
        else if (name.contains("VG"))
            supply.setSupplyType(1);
        else
            supply.setSupplyType(2);
    }

    public void setFlavorType(String name){
        if (name.equals("PG"))
            flavor.setType(0);
        else if (name.equals("VG"))
            flavor.setType(1);
        else
            flavor.setType(2);
    }


    private void addPercentFields(Array<Integer> percents){
        for (int i = 0; i < 3; i++){
            table.row();
            Label pgLabel = new Label(Constants.PERC_FIELD_NAMES[i], skin);
            TextField percentField = new TextField("", skin);
            percentField.setColor(Color.RED);
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            percentField.setMaxLength(2);

            percentField.setMaxLength(2);
            percentField.setTextFieldListener(percentListener());
            
            percentField.setText(percents.get(i).toString());

            table.add(pgLabel).width(50).height(25).align(Align.right).padRight(2);
            table.add(percentField).width(40).height(25).padRight(5);
        }
    }

    
    
    //--------Window Listeners------//
    private TextField.TextFieldListener flavorNameListener(){
        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= 'a' && c <='9')
                    flavor.setName(textField.getText());
            }
        };

        return nameTextFieldListener;
    }

    private TextField.TextFieldListener percentListener(){
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
                        base.setPgPercent(Integer.parseInt(textField.getText()));
                    else if (name.equals(Constants.PERC_FIELD_NAMES[1]))
                        base.setVgPercent(Integer.parseInt(textField.getText()));
                    else
                        base.setOtherPercent(Integer.parseInt(textField.getText()));
                }
            }
        };

        return percentFieldListener;
    }

    private TextField.TextFieldListener amountListener(){
        TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c){
                log( "Amount typed: " + c);

//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                
                
                if (c <= '9' && c >= '0') {
                    if (supply != null) {
                        supply.setTotalAmount(Double.parseDouble(textField.getText()));
                    }
                    else if (flavor != null) {
                        flavor.setTotalAmount(Double.parseDouble(textField.getText()));
                    }
                    else if (base != null) {
                        if (textField.getName().equals("strength"))
                            base.setBaseStrength(Double.parseDouble(textField.getText()));
                        else
                            base.setTotalAmount(Double.parseDouble(textField.getText()));
                    }
                }
            }
        };

        return numTextFieldListener;
    }

    private ChangeListener checkBoxListener(final int category){
        ChangeListener flavorBoxListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox box = ((CheckBox)actor);
                if (box.isChecked()){
                    log("category checkbox belongs to is ");

                    if (category == 0){
                        setCategory(box.getName());
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
    private Dialog errorDialog(String errorMsg){
        return new Dialog("", skin)
                .text(errorMsg).button(errorMsg).show(SupplyScreen.instance.getStage());
    }

//    @Override
//    public void act(float delta) {
//        super.act(delta);
//
//        //dismisses if click anywhere on outside of dialog box
//        if (Gdx.input.isTouched()){
//            if (Gdx.input.getX() > (getX() + getWidth()) || Gdx.input.getX() < getX()) {
//                hide();
//                remove();
//            }
//            if (Gdx.input.getY() < getY() || Gdx.input.getY() > (getY() + getHeight())) {
//                hide();
//                remove();
//            }
//        }
//
//    }

    private void log(String message){
        Gdx.app.log("SupplyWindow LOG: ", message);
        
    }

}
