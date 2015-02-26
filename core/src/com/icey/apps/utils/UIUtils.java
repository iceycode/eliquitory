package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Clipboard;
import com.icey.apps.MainApp;
import com.icey.apps.data.Supply;

/** Class holds listeners & common elements for UI
 *  
 * Created by Allen on 1/28/15.
 */
public class UIUtils {


    //listener for textfields for names - flavor or recipe

    /** Returns a TextField listener for RecipeName field or Flavor name field
     *
     * @return : a listener for name TextField
     */
    public static TextField.TextFieldListener nameTextFieldListener(){
        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {

                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (textField.getName().contains("flavor")){
                    int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                    CalcUtils.getCalcUtil().setFlavorName(String.valueOf(textField.getText()), i);
                }
                else if (textField.getName().contains("recipe")){
                    CalcUtils.getCalcUtil().setRecipeName(textField.getText());
                }

            }
        };

        return nameTextFieldListener;
    }


    /** returns listener for amounts - mg nicotine or ml juice
     * 
     * @param type : the listener either for volume of liquid in ml (0) or strength nic in mg (1)
     * @return : a listener for amount TextField
     */
    public static TextField.TextFieldListener numTextFieldListener(final int type){
        TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c){

//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (isDecimalDigit(c)){
                    setAmountValue(type, textField.getText());
                }
            }
        };

        return numTextFieldListener;
    }


    /** sets the number value based on type of textfield & its text
     * 
     * @param type : type of textfield
     * @param text : String value from textfield
     */
    protected static void setAmountValue(int type, String text){
        if (type == 0){
            CalcUtils.getCalcUtil().setAmountDesired(Double.parseDouble(text));
        }
        else if (type == 1){
            CalcUtils.getCalcUtil().setStrengthDesired(Double.parseDouble(text));
        }
        
    }
    


    /** a percent field listener method
     *
     * @param type : the type of percent (0-PG, 1=VG, 2=other, 3=base pg, 4=base vg)
     * @return textfield listener
     */
    public static TextField.TextFieldListener percentListener(final int type){
        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (isInteger(c)){
                    CalcUtils.getCalcUtil().setPercent(Integer.parseInt(textField.getText()), type);
                }
            }
        };

        return percentFieldListener;
    }


    /** listener for flavor name TextField on calculator screen
     * 
     * @param id : flavor ID (also position in arrays & table row)
     * @return : listener for flavor name
     */
    public static TextField.TextFieldListener flavorNameListener(final int id){

        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else{
                    CalcUtils.getCalcUtil().setFlavorName(String.valueOf(textField.getText()), id);
                }
            }
        };

        return nameTextFieldListener;
    }


    /** Flavor percent TextField listener for calculator screen
     *
     * @param id : flavor ID - position in arrays & table row
     * @return : listener for flavor percent TextField
     */
    public static TextField.TextFieldListener flavorPercentListener(final int id){

        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (isInteger(c)){
                    CalcUtils.getCalcUtil().setFlavorPercent(textField.getText(), id);
                }
            }
        };

        return percentFieldListener;
    }


    /** return listener for flavor type checkboxes
     *
     * @param type : listener for specific type that the flavor is
     * @param id : flavor ID - position in arrays & table row
     * @return : listener for flavor type CheckBox
     */
    public static ChangeListener checkBoxListener(final int type, final int id){
        ChangeListener flavorBoxListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CheckBox box = ((CheckBox)actor);
                if (box.isChecked()){
                    log( "Flavor whose type changed, index = " + id);

                    CalcUtils.getCalcUtil().setFlavorType(type, id);
                }
            }
        };

        return flavorBoxListener;
    }


    public static ChangeListener backTextButtonListener(final TextButton button){
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (button.isPressed()){
                    MainApp.setState(MainApp.prevState);
                }
            }
        };
    }


    public static ChangeListener backButtonListener(final Button button){
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (button.isPressed()){
                    MainApp.setState(MainApp.prevState);
                }
            }
        };
    }


    /** a customized inputlistener - not currently in use
     *
     * @param supply : supply being added
     * @param textField :
     * @param type
     * @return
     */
    public static InputListener amountValueListener(final Supply supply, final TextField textField, final int type){
        InputListener listener = new InputListener(){
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if ((character == '\r' || character == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (isDecimalDigit(character)) {
                    if (textField.getText().matches("\\\\d+(\\\\.\\\\d+)*")){
                        if (type == 0) {
                            supply.setTotalAmount(Double.parseDouble(textField.getText()));
                        }
                        else {
                            supply.setBaseStrength(Double.parseDouble(textField.getText()));
                        }
                    }
                }

                return false;
            }
        };


        return listener;
    }



    protected static boolean isDecimalDigit(char c){
        return c == '.' || (c >= '0' && c <= '9');
    }


    protected static boolean isInteger(char c){
        return c >='0' && c <= '9';
    }



    //this is a customized TextFieldFilter for double values (not just numbers)
    public static class MyTextFieldFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {

        @Override
        public boolean acceptChar(TextField textField, char c) {
            //checks to see whetehr textField already contains a decimal point
            if (textField.getText().contains(".") && c == '.')
                return false;

            return isDecimalDigit(c);
        }

    }


    public static class ClipboardText implements Clipboard{

        @Override
        public String getContents() {
            return null;
        }

        @Override
        public void setContents(String content) {

        }
    }


    //methods for creating common buttons
    public static class Buttons{

        public static TextButton textButton(String title, Skin skin, String style){
            return new TextButton(title, skin, style);
        }
    }


    //contains the listeners for SupplyScreen UI
    public static class SupplyUI{

        public static TextField.TextFieldListener flavorNameListener(final Supply supply){
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


        /** a supply name listener
         *
         * @param supply : the supply being added
         * @return : a listener for the supply
         */
        public static TextField.TextFieldListener supplyNameListener(final Supply supply){
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


        /** percent of the base
         *
         * @param supply: supply being added (will be base)
         * @return : the listener
         */
        public static TextField.TextFieldListener percentListener(final Supply supply){
            TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
                @Override
                public void keyTyped(TextField textField, char c) {
                    if ((c == '\r' || c == '\n')) {
                        textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                    }
                    else if (isInteger(c)){
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


        /** returns the amount of the supply or strength of base
         *
         * @param type : 0 = amount, 1 = strength
         * @param supply : the supply
         * @return a listener
         */
        public static TextField.TextFieldListener amountListener(final int type, final Supply supply){
            TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
                @Override
                public void keyTyped(TextField textField, char c){
//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                    if ((c == '\r' || c == '\n')) {
                        textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                    }
                    else if (isDecimalDigit(c)){
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


        /** returns a CheckBox listener
         *
         * @param supply : window from SupplyScreen
         * @param category : whether it is liquid supply or flavor supply
         * @param type : specific type of liquid - PG, VG or Other
         * @return a listener
         */
        public static ChangeListener checkBoxListener(final Supply supply, final int category, final int type){
            ChangeListener flavorBoxListener = new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    CheckBox box = ((CheckBox)actor);
                    if (box.isChecked()){
                        if (category == 0){
                            supply.setSupplyType(type);
                        }
                        else {
                            supply.setFlavorType(type);
                        }
                    }
                }
            };

            return flavorBoxListener;
        }


        /** Changes label (mainly color) depending on amount user has left
         *
         * @param label : label being altered
         * @param amount : amount use has in supply
         * @param skin : the skin
         * @return : the altered label or unaltered label
         */
        public static Label flavorLabel(Label label, double amount, Skin skin){
            if (amount > 15) label = new Label("", skin, "default-green");
            else if (amount < 5)  label = new Label("", skin, "default-red");

            return label;
        }


    }




    //UI listeners for settings UI
    public static class SettingsUI{

        /** listener for textfield in settings which sets drops per ml
         *
         * @return : TexFieldListener for drops/ml TextField in Settings
         */
        public static TextField.TextFieldListener dropsListener(){
            return new TextField.TextFieldListener() {
                @Override
                public void keyTyped(TextField textField, char c) {
                    if (isDecimalDigit(c)){
                        Constants.DROPS_PER_ML = Double.parseDouble(textField.getText());
                        MainApp.saveManager.saveDropsPerML(Float.parseFloat(textField.getText()));
                    }
                }
            };
        }


        /** Listener for slider which sets drops/ml
         *
         * @param slider : slider which sets drops/ml
         * @return : ChangeListener for slider which sets drops/ml
         */
        public static ChangeListener dropsSliderListener(final Slider slider, final TextField textField){
            return new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Constants.DROPS_PER_ML = (double)slider.getValue();
                    textField.setText(Double.toString(Constants.DROPS_PER_ML));
                    MainApp.saveManager.saveDropsPerML(slider.getValue());
                }
            };
        }


    }




    private static void log(String message){
        Gdx.app.log("UIUtils LOG: ", message);
    }



}
