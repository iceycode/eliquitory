package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Supply;
import com.icey.apps.ui.SupplyWindow;

/** Class holds listeners & common elements for UI
 *  
 * Created by Allen on 1/28/15.
 */
public class UIUtils {


    public static TextField.TextFieldListener nameTextFieldListener(){
        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log("Name typed: " + c);
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (textField.getName().contains("flavor")){
                    int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                    CalcUtils.getCalcUtil().setFlavorName(String.valueOf(textField.getText()), i);
                }
                else if (textField.getName().contains("recipe")){
                    CalcUtils.getCalcUtil().setRecipeName(textField.getText());
                    log("recipe name: " + textField.getText());
                }

            }
        };

        return nameTextFieldListener;
    }

    public static TextField.TextFieldListener numTextFieldListener(final int type){
        TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c){
                log( "Amount typed: " + c);

//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                
                if ((c >= '0' && c <= '9') || c == '.'){
                    if (type == 0){
                        CalcUtils.getCalcUtil().setAmountDesired(Double.parseDouble(textField.getText()));
                    }
                    else if (type == 1){
                        CalcUtils.getCalcUtil().setStrengthDesired(Double.parseDouble(textField.getText()));
                    }
                }

            }
        };

        return numTextFieldListener;
    }

    
    //percent listener for desired or base
    public static TextField.TextFieldListener percentListener(){
        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log("Percentage typed: " + c + ", for " + textField.getName());
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= '0' && c <= '9'){
                    CalcUtils.getCalcUtil().setPercent(Integer.parseInt(textField.getText()), textField.getName());
                }
            }
        };

        return percentFieldListener;
    }

    //flavor name listener
    public static TextField.TextFieldListener flavorNameListener(final int id){

        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log( "Name typed: " + c);
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

//                int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                
                CalcUtils.getCalcUtil().setFlavorName(String.valueOf(textField.getText()), id);
            }
        };

        return nameTextFieldListener;
    }

    
    
    //flavor percent listener
    public static TextField.TextFieldListener flavorPercentListener(final int id){

        TextField.TextFieldListener percentFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log( "Percentage typed: " + c + ", for " + textField.getName());
                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= '0' && c <= '9'){
                    //int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                    CalcUtils.getCalcUtil().setFlavorPercent(textField.getText(), id);
                }
            }
        };

        return percentFieldListener;
    }
    
    
    
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

        public static TextField.TextFieldListener percentListener(final Supply supply){
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


        public static TextField.TextFieldListener amountListener(final int type, final Supply supply){
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
        
        public static ChangeListener checkBoxListener(final int category, final SupplyWindow window){
            ChangeListener flavorBoxListener = new ChangeListener(){
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    CheckBox box = ((CheckBox)actor);
                    if (box.isChecked()){
                        log("category checkbox belongs to is ");

                        if (category == 0){
                            window.setLiquidType(box.getName());
                        }
                        else {
                            window.setFlavorType(box.getName());
                        }
                    }
                }
            };

            return flavorBoxListener;
        }

    }

    /** a customized inputlistener for keyboard
     *
     * @param supply
     * @param textField
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
                else if (Character.isDigit(character) || Character.getType(character)==Character.DECIMAL_DIGIT_NUMBER) {
                    if (textField.getText().matches("\\\\d+(\\\\.\\\\d+)*")){
                        if (type == 0) {
                            supply.setTotalAmount(Double.parseDouble(textField.getText()));
                        }
                        else {
                            supply.setBaseStrength(Double.parseDouble(textField.getText()));
                        }
                    }
                }

                return true;
            }
        };


        return listener;
    }



    //this is a customized TextFieldFilter for double values (not just numbers)
    public static class MyTextFieldFilter extends TextField.TextFieldFilter.DigitsOnlyFilter {
        
        @Override
        public boolean acceptChar(TextField textField, char c) {
            //((c >= '0' && c <= '9') || c == '.')  && isDecimalDigit(c)
            if (textField.getText().contains(".") && c == '.')
                return false;
            
            return isDecimalDigit(c);
        }
        
        public static boolean isDecimalDigit(char c){
            return c == '.' || (c >= '0' && c <= '9');
        }
    }
    
    
    //custom textfield class
    public static class DecimalTextField extends TextField{
        
        //for default
        public DecimalTextField(String text, Skin skin) {
            super(text, skin);
        }

        //for other types of textfield listeners
        public DecimalTextField(String text, Skin skin, String styleName) {
            super(text, skin, styleName);
        }

        @Override
        public void setFocusTraversal(boolean focusTraversal) {
            
            
            
            super.setFocusTraversal(focusTraversal);
        }
        
        
        
        
    }
    
    
    
    
    
    
    

    private static void log(String message){
        Gdx.app.log("CalcTable LOG: ", message);
    }


}
