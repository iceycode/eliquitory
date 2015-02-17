package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Supply;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.SupplyUtils;
import com.icey.apps.utils.UIUtils;

/** Table Widget containg Liquid (PG, VG, Other) & Base labels/textfields
 *
 * - holds percent fields
 * - sends updates to CalcUtils on what is going on with data
 *
 *
 * Created by Allen on 1/21/15.
 */
public class CalcTable extends Table{
    
    
    public static CalcTable instance;
    
    Skin skin;
    
    //width & height of the text fields (percents)
    private final float FIELD_WIDTH = Constants.TEXT_FIELD_WIDTH;
    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;

    //dimensions of title fields
    private final float TITLE_HEIGHT = Constants.TITLE_HEIGHT;
    private final float TITLE_WIDTH = Constants.TITLE_WIDTH;
    

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

    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //stores, calculates amounts & supply changes

//    //scaleX & Y for scaling table
//    float scaleX = MainApp.scaleX;
//    float scaleY = MainApp.scaleY;

    
    public CalcTable(Skin skin){
        instance = CalcTable.this;
        
        this.skin = skin;


        //the background
        setBackground(new TextureRegionDrawable(new TextureRegion(Assets.manager.get(Constants.CALC_BACKGROUND, Texture.class))));

        setTableProperties(); //table properties

        //setBackground(skin.getDrawable("calcBackground"));
        setTableWidgets(); //table widgets

        debug();

    }


    //table properties
    protected void setTableProperties(){
//        setScale(scaleX, scaleY);
        setBounds(0, 50, Constants.SCREEN_WIDTH, Constants.SCREEN_WIDTH);

        setClip(true);
        setLayoutEnabled(true);
        setFillParent(true);

        //center(); //center the table
//        defaults().padRight(2f);

//        columnDefaults(0).width(150).align(Align.right);
//        columnDefaults(1).width(50).align(Align.left);
//        columnDefaults(2).width(105);
    }

    protected void setTableWidgets(){
        initCalcLabels(); //initialize labels for supplies & calculated values into arrays

        //label for recipe name
        Label titleTFLabel = new Label("Recipe Name:", skin);
//        titleTFLabel.setScale(scaleX, scaleY);
        add(titleTFLabel).width(titleTFLabel.getTextBounds().width + 5).align(Align.right);

        //title text field (recipe name)
        titleTextField = new TextField("", skin, "titleTextField"); //title of recipe at top
//        titleTextField.setSize(Constants.SCREEN_WIDTH - 150, 50);
//        titleTextField.setScale(scaleX, scaleY);
        titleTextField.setMessageText(recipeName);
        titleTextField.setName("recipeTextField");
        titleTextField.setTextFieldListener(UIUtils.nameTextFieldListener()); //.width(titleTextField.getWidth())
        add(titleTextField).width(Constants.SCREEN_WIDTH - 150).height(TITLE_HEIGHT).colspan(5).fillX();
        row();


        //====================DESIRED AMOUNTS & FINAL AMOUNTS================= (1st category)
        Label goalLabel = new Label(Constants.GOAL_TITLE, skin, "goalLabel");
        goalLabel.setAlignment(Align.center);
        add(goalLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(3).align(Align.center);

        Label calcsLabelTitle = new Label(Constants.FINAL_CALCS_TITLE, skin, "calcsLabel");
        add(calcsLabelTitle).width(calcsLabelTitle.getWidth()); //.align(Align.left)
        
        Label supplyLabelTitle = new Label(Constants.SUPPLY_AMTS_TITLE, skin, "supplyLabel");
        supplyLabelTitle.setColor(Color.YELLOW);
        add(supplyLabelTitle).width(supplyLabelTitle.getWidth());
        row();

        //amount desired label
        Label amtLabel = new Label(Constants.AMOUNTS_TITLES[0], skin);
//        amtLabel.setSize(150, FIELD_HEIGHT);
//        amtLabel.setScale(scaleX, scaleY);
        amtLabel.setAlignment(Align.right);
        add(amtLabel).align(Align.right);
        
        //amount desired textfield 
        amtDesTextField = new TextField("", skin, "numTextField");
//        amtDesTextField.setSize(FIELD_WIDTH, FIELD_HEIGHT);
//        amtDesTextField.setScale(scaleX, scaleY);
        amtDesTextField.setName("amountTextField");
        amtDesTextField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        amtDesTextField.setTextFieldListener(UIUtils.numTextFieldListener(Constants.AMOUNT_LISTENER_TYPE[0]));
        amtDesTextField.setFocusTraversal(true);
        amtDesTextField.setAlignment(Align.center); //offets text alignment by 2
        amtDesTextField.setMessageText(Double.toString(calcUtils.getAmountDesired()));
        amtDesTextField.setColor(Color.RED);
        add(amtDesTextField).width(FIELD_WIDTH).height(amtDesTextField.getHeight()).align(Align.left);
        row();
        
        Label strLabel = new Label(Constants.AMOUNTS_TITLES[1], skin);
//        strLabel.setSize(150, 25);
//        strLabel.setScale(scaleX, scaleY);
        strLabel.getStyle().fontColor = Color.BLACK;
        strLabel.setAlignment(Align.right);
        add(strLabel).align(Align.right);
        
        strTextField = new TextField("", skin, "numTextField");
//        strTextField.setSize(FIELD_WIDTH, FIELD_HEIGHT);
//        strTextField.setScale(scaleX, scaleY);
        strTextField.setName("strengthTextField");
        strTextField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        strTextField.setTextFieldListener(UIUtils.numTextFieldListener(Constants.AMOUNT_LISTENER_TYPE[1]));
        strTextField.setFocusTraversal(true);
        strTextField.setMessageText(Double.toString(calcUtils.getStrengthDesired()));
        strTextField.setAlignment(Align.center);
        strTextField.setColor(Color.RED);
        add(strTextField).width(FIELD_WIDTH).height(strTextField.getHeight()).align(Align.left);

        percentTextFields = new Array<TextField>();
        row();
        addPercentFields(true); //add percent fields for desired amounts

        
        
        //====================NICOTINE BASE====================2nd category - Nicotine Base
        Label baseLabel = new Label(Constants.BASE_TITLE, skin, "baseLabel");
//        baseLabel.setSize(TITLE_WIDTH, TITLE_HEIGHT);
//        baseLabel.setScale(scaleX, scaleY);
        baseLabel.setAlignment(Align.center);
        baseLabel.getStyle().fontColor = Color.BLACK;
        add(baseLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(3).align(Align.center); //.align(Align.right)
        
        add(calcLabels.get(3)); //.width(50f).height(25f).align(Align.right)
        add(supplyLabels.get(3));
        
        row();
        
        Label baseStrLabel = new Label("Strength (mg): ", skin);
//        baseStrLabel.setSize(150, FIELD_HEIGHT);
//        baseStrLabel.setScale(scaleX, scaleY);
        baseStrLabel.setAlignment(Align.right);
        add(baseStrLabel).align(Align.right).fill(); //width(120).height(25f).

        baseStrTF = new TextField("", skin, "numTextField");
//        baseStrTF.setSize(FIELD_WIDTH, FIELD_HEIGHT);
//        baseStrTF.setScale(scaleX, scaleY);
        baseStrTF.setName("basestrengthTextField");
        baseStrTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        baseStrTF.setAlignment(Align.center);
        baseStrTF.setMessageText(Double.toString(calcUtils.getBaseStrength()));
        baseStrTF.setColor(Color.RED);
        add(baseStrTF).width(FIELD_WIDTH).fillY().align(Align.left); //.height(FIELD_HEIGHT)
        
        row();
        addPercentFields(false); //add base percent fields

        setSupplyLabels(); //supply labels are set

        //====================FLAVOR TITLE==========================
        Label flavorLabel = new Label(Constants.FLAVORS_TITLE, skin, "flavorLabel");
        flavorLabel.setAlignment(Align.center);
        add(flavorLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).align(Align.center).colspan(6);

        row();

        //add flavor table in CalculatorScreen
    }


    //sets labels for calculated amounts & before/after supply amounts
    protected void initCalcLabels(){
        //create final calculations labels supply labels (start with 4 each)
        calcLabels = new Array<Label>();
        supplyLabels = new Array<Label>();
        for (int labelCount = 0; labelCount < 4; labelCount++){
            Label calcLabel = new Label("-", skin, "calcsLabel");
//            calcLabel.setSize(50, 25);
//            calcLabel.setScale(scaleX, scaleY);

            calcLabels.add(calcLabel); //initially set with "-", indicates not calculated

            Label supplyLabel = new Label("0", skin, "supplyLabel"); //0 indicates not supplied
//            supplyLabel.setSize(50, 25);
//            supplyLabel.setScale(scaleX, scaleY);
            supplyLabel.setColor(Color.YELLOW);
            supplyLabel.getColor().a = .8f;
            supplyLabel.getStyle().fontColor = Color.RED; //if supply 0, set as red
            supplyLabels.add(supplyLabel);
        }
    }
    

    /** adds percentage fields to the
     *
     * //@param percentFieldLabels //String[] percentFieldLabels,
     * @param desired
     */
    protected void addPercentFields(boolean desired){
        String[] fieldNames;
        Array<Integer> percents;
        int numFields = 2;
        if (desired){
            numFields = 3;
            percents = calcUtils.getDesiredPercents();
            fieldNames = Constants.DESIRED_PERC_LABELS;
        }
        else{
            percents = calcUtils.getBasePercents();
            
            log(calcUtils.getBasePercents().toString());
            
            if (percents.size == 0)
                percents = Constants.ZERO_BASE_PERCENTS;
            fieldNames = Constants.BASE_PERC_LABELS;
        }
            

        for (int i = 0; i < numFields; i++){
            Label percLabel = new Label(fieldNames[i], skin);
//            percLabel.setSize(150, 25);
//            percLabel.setScale(scaleX, scaleY);
            percLabel.setAlignment(Align.right);
            add(percLabel); //.width(width+10).align(Align.right)

            TextField percentField = new TextField("", skin, "numTextField");
//            percentField.setSize(FIELD_WIDTH, FIELD_HEIGHT);
            percentField.setColor(Color.DARK_GRAY);
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            percentField.setAlignment(Align.center);
            percentField.setName(fieldNames[i]);
            percentField.setMessageText(Integer.toString(percents.get(i)));
            percentField.setMaxLength(2);
            percentField.setTextFieldListener(UIUtils.percentListener());
            
//            float width = percLabel.getTextBounds().width;
            if (i == 0)
                add(percentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left);
            else
                add(percentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left).colspan(2);

            //add a slider
            if (i == 0) addSlider(desired); 
            
            //add labels if desired
            if (desired){
                add(calcLabels.get(i));
                add(supplyLabels.get(i));
            }

            row();
            
            percentTextFields.add(percentField);
        }

    }
    
    
    //sets slider with max min 0, max 100, step size of 1
    //TODO: fix slider at 50 percent - rounding down from 0 to 101
    protected void addSlider(boolean desired){
        final Slider slider = new Slider(0, 100, 1, false, skin);
//        slider.setSize(150, 25);
//        slider.setScale(scaleX, scaleY);

        if (desired) slider.setName(Constants.SLIDER_NAMES[0]);
        else slider.setName(Constants.SLIDER_NAMES[1]);
        
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int percent = (int)slider.getValue(); //returns a percent value of slider
                calcUtils.setPercent(percent, slider.getName());
            }
        });
        
        add(slider).width(125);
        
    }
    
    
    //sets the supply labels with amounts previously stored
    public void setSupplyLabels(){

        IntMap<Supply> supplyMap = SupplyUtils.getSupplyUtils().getSupplyMap();
        
        for (int i = 0; i < 4; i++){
            double amt = 0;
            if (supplyMap.containsKey(i))
                amt = (supplyMap.get(i)).getTotalAmount();
            
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
    
    
    public void setEmptySupplyLabels(){
        for (int i = 0; i < 4; i++){
            
        }
    }

    
    public void updateCalcLabels(){
        Array<Double> finalMills = calcUtils.getFinalMills();
        
        for (int i = 0; i < 4; i++){
            //put the values into "ml (drops)" String format
            String text = finalMills.get(i) + " (" + (int)(finalMills.get(i).doubleValue()*20)+")";
            calcLabels.get(i).setText(text); //show liquid & base amounts
        }
    }

    //update supply labels
    public void updateSupplyLabels(){
        IntMap<Double> supplyMap = calcUtils.getSupplyAmounts();
        
        for (int i = 0; i < 4; i++){
            supplyLabels.get(i).setText(supplyMap.get(i).toString());
        }
    }
    
    
    public void updateSupply(int type, double amount){
        supplyLabels.get(type).setText(Double.toString(amount));
    }
    
    public void setLoadedRecipe(){
        titleTextField.setMessageText(calcUtils.getRecipeName());
        amtDesTextField.setMessageText(Double.toString(calcUtils.getAmountDesired()));
        strTextField.setMessageText(Double.toString(calcUtils.getStrengthDesired()));
        baseStrTF.setMessageText(Double.toString(calcUtils.getBaseStrength()));

        //set up desired percents
        for (int i = 0; i < 3; i ++){
            percentTextFields.get(i).setMessageText(Double.toString(calcUtils.getDesiredPercents().get(i)));
        }
        
        //set up base percents
        for (int i = 0; i < 2; i++){
            percentTextFields.get(i+3).setMessageText(Double.toString(calcUtils.getBasePercents().get(i)));
        }
    }


    
    private void log(String message){
        Gdx.app.log("CalcTable LOG: ", message);
    }
    
    
}