package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
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

        protected TextField.TextFieldListener flavorNameListener(final Supply supply){
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

        protected TextField.TextFieldListener percentListener(final Supply supply){
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


        protected TextField.TextFieldListener amountListener(final int type, final Supply supply){
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
    
    

    private static void log(String message){
        Gdx.app.log("CalcTable LOG: ", message);
    }


}
