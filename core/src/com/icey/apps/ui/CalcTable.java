package com.icey.apps.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.UIUtils;

/** Table Widget containg Liquid (PG, VG, Other) & Base labels/textfields
 *
 * NOTE: paid app methods are commented out
 *
 * - holds percent fields
 * - sends updates to CalcUtils on what is going on with data
 *
 * Table Layout
 *  Root Table <---Scroll(FlavorTable <--- VertGroup <--- Individual Flavor tables)
 *
 * Created by Allen on 1/21/15.
 */
public class CalcTable extends Table{

    Skin skin;
    
    //width & height of the text fields (percents)
    private final float FIELD_WIDTH = Constants.TEXT_FIELD_WIDTH;
    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;

    //dimensions of title fields
    private final float TITLE_HEIGHT = Constants.TITLE_HEIGHT;
    private final float TITLE_WIDTH = Constants.TITLE_WIDTH;

//    public boolean supplyEnabled = MainApp.supplyEnabled; //whether enabled supply or not

    //the text fields within this table
    public TextField titleTextField;
    public TextField strTextField;
    public TextField amtDesTextField;
    public TextField baseStrTF;

    //percent text fields : 0-2 Desired; 3-5 base
    //static since need to be manipulated externally
    public static Array<TextField> percentTextFields = new Array<TextField>();
    public static Array<Slider> ratioSliders = new Array<Slider>();

    public FlavorTable flavorTable;

    //public Array<Label> calcLabels; //index: 0=PG, 1=VG, 2=other, 3=Base, 4=Flavor1, 5=Flavor2...N=FlavorN
    public static Array<Label> supplyLabels; //index: same as above
    public Array<Label> calcLabels; //calculated amount labels

    CalcUtils calcUtils = CalcUtils.getCalcUtil(); //stores, calculates amounts & supply changes

    public static int cols = 4; //columns table contains with supplies not enabled
//    public static int cols = 5; //

    public CalcTable(Skin skin){
        this.skin = skin;

//        if (supplyEnabled)
//            cols++;

        //the background - previously skin held calcBackground (should just be background)
        setBackground(new TextureRegionDrawable(skin.getRegion("background")));

        setTableProperties(); //table properties

        setTableWidgets(); //table widgets
        //debug();

    }


    //table properties
    protected void setTableProperties(){
        setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);

        //setClip(true); //clips any widget on outside of table bounds
        setLayoutEnabled(true); //default is true
        setFillParent(true); //fills parent (stage in this case)

        top().left(); //align the table to top left
//        defaults().padRight(2f);

        columnDefaults(0).prefWidth(100).right();
        columnDefaults(1).prefWidth(FIELD_WIDTH).left();
        columnDefaults(2).prefWidth(110).center();

        if (cols == 4)
            columnDefaults(3).width(60).left();
    }

    protected void setTableWidgets(){
//        if (supplyEnabled)
//            setSupplyLabels(); //initialize labels for supplies only
        setCalcLabels();
        setRecipeTitle(); //row 1

        //set user desired value fields
        setGoalTitle(); //row 2
        setGoalAmount(); //row 3
        setGoalStrength(); //row 4
        setGoalPercents(); //rows 5-7

        //set the nicotine base fields
        setBaseTitle(); //row 8
        setBaseStrength(); //rows 9
        setBaseRatios(); //rows 10-11

        //set the flavor fields
        setFlavorTitle();
        setFlavorScrollTable();
    }

    //======ROW 1====Title of Recipe
    protected void setRecipeTitle(){
        //label for recipe name
        Label titleTFLabel = new Label("Name: ", skin);
        add(titleTFLabel).width(titleTFLabel.getTextBounds().width).padRight(2).center();

        //title text field (recipe name)
        titleTextField = new TextField("", skin, "title"); //title of recipe at top

//        titleTextField = new TextField("", skin, "recipe"); //title of recipe at top
        titleTextField.setMessageText("Recipe Name Here");
        titleTextField.setName("recipeTextField");
        titleTextField.setFocusTraversal(true);
        titleTextField.setTextFieldListener(UIUtils.nameTextFieldListener()); //.width(titleTextField.getWidth())
        add(titleTextField).height(TITLE_HEIGHT).colspan(cols-1).fill().expand(); //.width(Constants.SCREEN_WIDTH - 150).height(TITLE_HEIGHT)
    }

    //====ROW 2==== GOAL AMOUNTS (DESIRED)
    protected void setGoalTitle(){
        row();

        Label goalLabel = new Label(Constants.GOAL_TITLE, skin, "title");
        goalLabel.setSize(TITLE_WIDTH, TITLE_HEIGHT);
        goalLabel.setAlignment(Align.center);

        add(goalLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).center();

        Label calcLabel = new Label("Final Amounts", skin);
        calcLabel.setAlignment(Align.center);

        add(calcLabel);
//        if (supplyEnabled){
//            add(goalLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).center();
//
//            Label supplyLabel = new Label("Supply", skin);
//            supplyLabel.setAlignment(Align.center);
//            add(supplyLabel); //, "calcsLabel"
//        }
//        else{
//            add(goalLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols).center();
//        }

    }


    //====ROW 3==== Goal Amounts - volume of juice (ml)
    protected void setGoalAmount(){

        row();

        //amount desired label
        Label amtLabel = new Label(Constants.AMOUNTS_TITLES[0], skin);
        amtLabel.setAlignment(Align.right);

        add(amtLabel).align(Align.right);
        //amount desired textfield
        amtDesTextField = new TextField("", skin, "digit");
        amtDesTextField.setName("amountTextField");
        amtDesTextField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        amtDesTextField.setTextFieldListener(UIUtils.numTextFieldListener(Constants.AMOUNT_LISTENER_TYPE[0]));
        amtDesTextField.setFocusTraversal(true);
        amtDesTextField.setAlignment(Align.center); //offets text alignment by 2
        amtDesTextField.setMessageText("  " + Double.toString(calcUtils.getAmountDesired()));
        add(amtDesTextField).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left);

    }


    //====ROW 4==== Goal strength - strength of nicotine (mg)
    protected void setGoalStrength(){
        row();

        Label strLabel = new Label(Constants.AMOUNTS_TITLES[1], skin);
        strLabel.setAlignment(Align.right);
        add(strLabel).align(Align.right);

//        strTextField = new TextField("", skin, "numTextField");
        strTextField = new TextField("", skin, "digit");
        strTextField.setHeight(5);
        strTextField.setName("strengthTextField");
        strTextField.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        strTextField.setTextFieldListener(UIUtils.numTextFieldListener(Constants.AMOUNT_LISTENER_TYPE[1]));
        strTextField.setFocusTraversal(true);
        strTextField.setAlignment(Align.center);
        strTextField.setMessageText("  " + Double.toString(calcUtils.getStrengthDesired()));

        add(strTextField).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left);
    }

    //=====ROWS 5-7===== Goal Percents - PG, VG & Other percents
    protected void setGoalPercents(){
        row();

        int[] types = Constants.GOAL_PERCENT_TYPES;
        addPercentFields(calcUtils.getDesiredPercents(), types); //add percent fields for desired amounts
    }

    //====ROW 8==== Nicotine Base title
    protected void setBaseTitle(){

        Label baseLabel = new Label(Constants.BASE_TITLE, skin, "title");
        baseLabel.setSize(TITLE_WIDTH, TITLE_HEIGHT);
        baseLabel.setAlignment(Align.center);
//        baseLabel.getStyle().fontColor = Color.BLACK; //NOTE: changing style here changes everywhere for font
        add(baseLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).align(Align.center); //.align(Align.right)


        //add calcLabel
        add(calcLabels.get(3));

//        if (supplyEnabled){
//            add(baseLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).center();
//
//            add(supplyLabels.get(3)).padLeft(15);
//        }
//        else{
//            add(baseLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols).center();
//        }


    }

    //====ROW 9==== Nicotine Base Strength
    protected void setBaseStrength(){
        row();

        Label baseStrLabel = new Label("Strength (mg): ", skin);
        baseStrLabel.setAlignment(Align.right);
        add(baseStrLabel).align(Align.right).fill(); //width(120).height(25f).

        baseStrTF = new TextField("", skin, "digit");
        baseStrTF.setName("basestrengthTextField");
        baseStrTF.setTextFieldFilter(new UIUtils.MyTextFieldFilter());
        baseStrTF.setFocusTraversal(true);
        baseStrTF.setAlignment(Align.center);
        baseStrTF.setMessageText("  " + Double.toString(calcUtils.getBaseStrength()));
        add(baseStrTF).width(FIELD_WIDTH).height(FIELD_HEIGHT).align(Align.left); //.height(FIELD_HEIGHT)
    }

    //=====ROW 10-11===== base PG/VG percents (ratio)
    protected void setBaseRatios(){
        row();

        Array<Integer> percents = calcUtils.getBasePercents();
        log(calcUtils.getBasePercents().toString());

        if (percents.size == 0)
            percents = Constants.ZERO_BASE_PERCENTS;

        int[] types = Constants.BASE_PERCENT_TYPES;

        addPercentFields(percents, types);
    }

    //=====ROW 12====Flavor title
    protected void setFlavorTitle(){
        Label flavorLabel = new Label(Constants.FLAVORS_TITLE, skin, "title");
        flavorLabel.setSize(TITLE_WIDTH, TITLE_HEIGHT);
        flavorLabel.setAlignment(Align.center);
        add(flavorLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).center();

        Label calcLabel = new Label("Final Amounts", skin);
        calcLabel.setAlignment(Align.center);
        add(calcLabel); //, "calcsLabel"

//        if (supplyEnabled){
//            add(flavorLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols-1).center();
//
//            Label supplyLabel = new Label("Supply", skin);
//            supplyLabel.setAlignment(Align.center);
//            add(supplyLabel); //, "calcsLabel"
//        }
//        else{
//            add(flavorLabel).width(TITLE_WIDTH).height(TITLE_HEIGHT).colspan(cols).center();
//        }

        //====11th Row===(flavorTable goes here)
        row();
    }


    //a nested table with a scrollpane in it
    protected void setFlavorScrollTable(){

        flavorTable = new FlavorTable(skin);

        ScrollPane scroll = new ScrollPane(flavorTable, skin); //create scrollabel flavor table

        add(scroll).width(480).height(200).colspan(cols); //add to the outer table
        row();
    }


    /** adds percentage fields to the
     *
     * @param percents : percents of base or goal
     * @param types: types of percent values
     */
    protected void addPercentFields(Array<Integer> percents, int[] types){

        int numFields = types.length - 1;
        Slider slider = null; //ratio slider (need to set the PG/VG ratio to 50:50 after setup)

        for (int i = 0; i < numFields; i++){
            Label percLabel = new Label(Constants.TYPE_NAMES[i], skin);
            percLabel.setAlignment(Align.right);
            add(percLabel).right(); //.width(width+10).align(Align.right)

            TextField percentField = new TextField("", skin, "digit");
            percentField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
            percentField.setAlignment(Align.center);
//            percentField.setName(Constants.TYPE_NAMES[i]);
            percentField.setMessageText("  " + Integer.toString(percents.get(i)));
            percentField.setMaxLength(2);
            percentField.setTextFieldListener(UIUtils.percentListener(types[i]));
            
//            float width = percLabel.getTextBounds().width;
            if (i == 0){
                add(percentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();
                slider = ratioSlider(types[types.length - 1]);
                ratioSliders.add(slider);
            }
            else {
                add(percentField).width(FIELD_WIDTH).height(FIELD_HEIGHT).left().colspan(2);
            }
            
            //add labels if goal percents
            if (numFields == 3 ){ //&& supplyEnabled
                add(calcLabels.get(i));
                //add(supplyLabels.get(i)).padLeft(15);
            }

            row();
            
            percentTextFields.add(percentField);
        }

        slider.setValue(50);

        log("SIze of percentTextField array = " + percentTextFields.size);
    }
    
    
    //sets slider with max min 0, max 100, step size of 1
    //TODO: fix slider at 50 percent - rounding down from 0 to 101
    protected Slider ratioSlider(final int type){
        final Slider slider = new Slider(0, 100, 1, false, skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int percent = (int)slider.getValue(); //returns a percent value of slider
                calcUtils.setRatio(percent, type);
            }
        });

//        add(new Label("PG:VG Ratio: ", skin)).width(FIELD_WIDTH).right();
        add(slider).width(110);

        return slider;
    }
    
    
//    //sets the supply labels with amounts previously stored
//    public void setSupplyLabels(){
//        supplyLabels = new Array<Label>();
//
//        for (int i = 0; i < 4; i++){
//            double amt = 0;
//
//            if (calcUtils.supplied){
//                if (calcUtils.getSupplyAmounts().containsKey(i))
//                    amt = calcUtils.getSupplyAmounts().get(i);
//            }
//
//
//            Label l = new Label("-", skin);
//
//            //different colors for how much user has
//            //different colors for how much user has
//            l = UIUtils.SupplyUI.flavorLabel(l, amt, skin);
//
//            l.setText(Double.toString(amt));
//
//            supplyLabels.add(l);
//        }
//    }

    //final amounts lables
    protected void setCalcLabels(){
        calcLabels = new Array<Label>();

        for (int i = 0; i < 4; i++){
            Label label = new Label("0.0 (0)", skin, "default-red");
            calcLabels.add(label);
        }
    }


    public void updateCalcLabels(SnapshotArray<Double> amounts){
        for (int i = 0; i < calcLabels.size; i++){
            String calc = amounts.get(i)  + " (" + amounts.get(i)*Constants.DROPS_PER_ML + ")";

            log(calc);
            calcLabels.get(i).setText(calc);
            log("Labels show: " + calcLabels.get(i).getText());
        }

        flavorTable.updateCalcLabels(amounts);
    }




    public static void updateSupplyAmount(int type, double amount){
        supplyLabels.get(type).setText(Double.toString(amount));
    }
    
    public void setLoadedRecipe(){
        titleTextField.setText(calcUtils.getRecipeName());
        amtDesTextField.setText(Double.toString(calcUtils.getAmountDesired()));
        strTextField.setText(Double.toString(calcUtils.getStrengthDesired()));
        baseStrTF.setText(Double.toString(calcUtils.getBaseStrength()));

        //set hte ratios (this will set the percent fields)
        ratioSliders.get(0).setValue(calcUtils.getDesiredPercents().get(0));
        ratioSliders.get(1).setValue(calcUtils.getBasePercents().get(0));

        //set other percent
        percentTextFields.get(2).setText(Double.toString(calcUtils.getDesiredPercents().get(2)));

        //set up desired percents
//        for (int i = 0; i < 3; i ++){
//            percentTextFields.get(i).setText(Double.toString(calcUtils.getDesiredPercents().get(i)));
//        }
//
//        for (int i = 0; i < 2; i++){
//            percentTextFields.get(i+3).setText(Double.toString(calcUtils.getBasePercents().get(i)));
//        }
        
        flavorTable.setLoadedData();
    }


    
    private void log(String message){
        Gdx.app.log("CalcTable LOG: ", message);
    }
    
    
}
