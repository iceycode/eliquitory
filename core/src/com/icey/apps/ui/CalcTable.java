package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.icey.apps.assets.Constants;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.SupplyUtils;

/** Table Widget containg Liquid (PG, VG, Other) & Base labels/textfields
 *
 * Created by Allen on 1/21/15.
 */
public class CalcTable extends Table{
    
    
    public static CalcTable instance;
    
    Skin skin;
    Stage stage;
    
    //width & height of the text fields (percents)
    final float fieldsWidth = Constants.TEXT_FIELD_WIDTH;
    final float fieldsHeight = Constants.TEXT_FIELD_HEIGHT;

    //the text fields within this table
    public TextField titleTextField;
    public TextField strTextField;
    public TextField amtDesTextField;
    public TextField baseStrTF;
    public Array<TextField> percentTextFields; //0-2 Desired; 3-5 base; 6 flavor

    public Array<Label> calcLabels; //index: 0=PG, 1=VG, 2=other, 3=Base, 4=Flavor1, 5=Flavor2...N=FlavorN
    public Array<Label> supplyLabels; //index: same as above

    //default values set initially
    String recipeName = "Recipe Name Here";

    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //stores amounts to be calculated
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();
    
    public CalcTable(Skin skin, Stage stage){
        instance = CalcTable.this;
        
        this.skin = skin;
        this.stage = stage;


        //setBackground(new TextureRegionDrawable(new TextureRegion(Assets.manager.get(Constants.CALC_BACKGROUND, Texture.class))));
        
        percentTextFields = new Array<TextField>();

        //table properties
        setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        setLayoutEnabled(true);
        setFillParent(true);
        right().top();
        defaults().padRight(2f);
        //columnDefaults(3).width(50f).align(Align.left);
        //setBackground(skin.getDrawable("calcBackground"));
        setTable();
        
        setSupplyLabels();
    }
    
    private void setTable(){
        //create final calculations labels supply labels (start with 4 each)
        calcLabels = new Array<Label>();
        supplyLabels = new Array<Label>();
        for (int labelCount = 0; labelCount < 4; labelCount++){
            calcLabels.add(new Label("-", skin)); //initially set with "-", indicates not calculated
            
            Label supplyLabel = new Label("0", skin); //0 indicates not supplied
            supplyLabel.getStyle().fontColor = Color.RED; //if 0, set as red
            supplyLabels.add(supplyLabel);
        }

        //label for recipe name
        Label titleTFLabel = new Label("Recipe Name:", skin);
        add(titleTFLabel).align(Align.right);
        
        //title text field (recipe name)
        titleTextField = new TextField("", skin, "titleTextField"); //title of recipe at top
        titleTextField.setMessageText(recipeName);
        titleTextField.setName("recipeTextField");
        titleTextField.setTextFieldListener(nameTextFieldListener());
        titleTextField.setColor(Color.RED);
        add(titleTextField).height(50f).colspan(3).fillX(); //.width(Constants.SCREEN_WIDTH - titleTFLabel.getWidth())
        row();

        Label goalLabel = new Label(" Desired Amount, Strength & Ratios", skin, "goalLabel");
        add(goalLabel).width(300).height(50).colspan(2).align(Align.right);

        Label calcsLabelTitle = new Label(" mL (drops)", skin, "calcsLabel");
        add(calcsLabelTitle).width(100).height(calcsLabelTitle.getHeight()).align(Align.left) ;
        
        Label supplyLabelTitle = new Label(" Supply ", skin, "supplyLabel");
        supplyLabelTitle.setColor(Color.YELLOW);
        add(supplyLabelTitle).width(80);
        row();

        //amount desired label
        Label amtLabel = new Label("Amount (mL): ", skin);
        add(amtLabel).align(Align.right);
        
        //amount desired textfield 
        amtDesTextField = new TextField("", skin, "numTextField");
        amtDesTextField.setName("amountTextField");
        amtDesTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        amtDesTextField.setTextFieldListener(numTextFieldListener());
        amtDesTextField.setMessageText(Double.toString(calcUtils.getAmountDesired()));
        amtDesTextField.setColor(Color.RED);
        add(amtDesTextField).width(fieldsWidth).height(fieldsHeight).align(Align.left);
        row();
        
        Label strLabel = new Label("Strength (mg): ", skin);
        add(strLabel).align(Align.right);
        
        strTextField = new TextField("", skin, "numTextField");
        strTextField.setName("strengthTextField");
        strTextField.setTextFieldListener(numTextFieldListener());
        strTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        strTextField.setMessageText(Double.toString(calcUtils.getStrengthDesired()));
        strTextField.setColor(Color.RED);
        add(strTextField).width(fieldsWidth).height(fieldsHeight).align(Align.left);
        row();
//        add(dropmlTextField).width(fieldsWidth).height(fieldsHeight);
//        row();
        addPercentFields(true);

        //---NICOTINE BASE---second category
        Label baseLabel = new Label(" Nicotine Base", skin, "baseLabel");
        add(baseLabel).height(50).align(Align.left).colspan(2);
        add(calcLabels.get(3)); //.width(50f).height(25f).align(Align.right)
        add(supplyLabels.get(3));
        row();
        baseStrTF = new TextField("", skin, "numTextField");
        baseStrTF.setName("basestrengthTextField");
        baseStrTF.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        baseStrTF.setMessageText(Double.toString(calcUtils.getBaseStrength()));
        baseStrTF.setColor(Color.RED);
        add(new Label("Strength (mg): ", skin)).align(Align.right); //width(120).height(25f).
        add(baseStrTF).width(fieldsWidth).height(fieldsHeight).align(Align.left);
        row();
        addPercentFields(false);
    }

    /** adds percentage fields to the
     *
     * //@param percentFieldLabels //String[] percentFieldLabels,
     * @param desired
     */
    private void addPercentFields(boolean desired){
        int numFields = 2;
        if (desired)
            numFields = 3;

        for (int i = 0; i < numFields; i++){
            Label pgLabel = new Label(Constants.DESIRED_PERC_LABELS[i], skin);
            TextField percentField = new TextField("", skin, "numTextField");
            percentField.setColor(Color.RED);
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            
            percentField.setMaxLength(2);
            percentField.setTextFieldListener(percentListener());

            add(pgLabel).align(Align.right);
            add(percentField).width(fieldsWidth).height(fieldsHeight).align(Align.left);

            
            if (desired){
                add(calcLabels.get(i));
                add(supplyLabels.get(i));
                percentField.setName(Constants.DESIRED_PERC_LABELS[i]);
                percentField.setMessageText(Integer.toString(calcUtils.getDesiredPercents().get(i)));
            }
            else{
                percentField.setName("Base " + Constants.DESIRED_PERC_LABELS[i]);
                percentField.setMessageText(Double.toString(calcUtils.getBasePercents().get(i)));
            }
            
            row();
            
            percentTextFields.add(percentField);
        }

    }
    
    //sets the supply labels
    public void setSupplyLabels(){
        //in case supplies have not been filled, create empty supply objects
        if (!supplyUtils.supplied)
            supplyUtils.setEmptySupplyMap();
        
        OrderedMap<Integer, Double> amounts = supplyUtils.getAllSupplyAmounts();

        for (Integer i : amounts.keys()){
            double amt = amounts.get(i);
            Label l = supplyLabels.get(i);
            
            //different colors for how much user has
            if (amt > 30)
                l.getStyle().fontColor = Color.BLACK;
            else if (amt > 15)
                l.getStyle().fontColor = Color.BLUE;
            else if (amt < 5)
                l.getStyle().fontColor = Color.RED;
            
            l.setText(Double.toString(amt));
            
        }
    }


    private TextField.TextFieldListener nameTextFieldListener(){
        TextField.TextFieldListener nameTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                log("Name typed: " + c);
                if ((c == '\r' || c == '\n')){
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (textField.getName().contains("flavor")){
                    int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                    calcUtils.setFlavorName(String.valueOf(textField.getText()), i);
                }
                else if (textField.getName().contains("recipe")){
                    calcUtils.setRecipeName(textField.getText());
                    log("recipe name: " + textField.getText());
                }

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
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }

                if (c >= '0' && c <= '9'){
                    if (textField.getName().contains("flavor")){
                        int i = Integer.parseInt(textField.getName().substring(textField.getName().length()-1))-1;
                        calcUtils.setFlavorPercent(textField.getText(), i);
                    }
                    else if (textField.getName().contains("Base")){
                        calcUtils.setBasePercent(textField.getText(), textField.getName());
                    }
                    else{
                        calcUtils.setDesiredPercent(textField.getText(), textField.getName());
                    }
                }
            }
        };

        return percentFieldListener;
    }

    private TextField.TextFieldListener numTextFieldListener(){
        TextField.TextFieldListener numTextFieldListener = new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c){
                log( "Amount typed: " + c);

//                if (c == '\n') textField.getOnscreenKeyboard().show(false);

                if ((c == '\r' || c == '\n')) {
                    textField.next(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                            Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
                }
                else if (textField.getName().contains("amount") && textField.getText()!=null){
                    calcUtils.setAmountDesired(Integer.parseInt(textField.getText()));
                }
                else if (textField.getName().contains("strength") && textField.getText()!=null){
                    calcUtils.setAmountDesired(Integer.parseInt(textField.getText()));
                }
            }
        };

        return numTextFieldListener;
    }
    
    private void log(String message){
        Gdx.app.log("CalcTable LOG: ", message);
    }
    
}
