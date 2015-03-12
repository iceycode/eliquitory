package com.icey.apps.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.icey.apps.MainApp;
import com.icey.apps.data.Flavor;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.Constants;

/** Popup window showing the calculations
 *
 *
 * Created by Allen on 2/18/15.
 */
public class CalcWindow extends Dialog{

    Table table; //table containing labels
    Table btnTable;

    Skin skin;

    CalcUtils calcUtils = CalcUtils.getCalcUtil();

    Array<Double> amounts;
    Array<Flavor> flavors;
    IntMap<Double> supplies;

    //width & height of the text fields (percents)
    private final float FIELD_WIDTH = Constants.TEXT_FIELD_WIDTH;
    private final float FIELD_HEIGHT = 20;

    double dropsPerML = Constants.DROPS_PER_ML;

    public CalcWindow(Skin skin, String style, Array<Double> amounts, Array<Flavor> flavors){
        super("", skin, style);
        this.skin = skin;

        this.amounts = amounts;
        this.flavors = flavors;
//        this.supplies = SupplyUtils.getSupplyUtils().getSupplyAmounts();

        setTable();

        setCalcLabels();

        setButtons();
    }

    //sets up the table
    protected void setTable(){
        table = getContentTable();

        table.columnDefaults(0).prefWidth(75).height(FIELD_HEIGHT);
        table.columnDefaults(1).prefWidth(50).height(FIELD_HEIGHT);
        table.columnDefaults(2).prefWidth(50).height(FIELD_HEIGHT);


        //the main title
        Label title = new Label("Calculated Amounts", skin, "tab");
        title.setAlignment(Align.center);
        table.add(title).colspan(3).expandX().fillX();


        //---ROW 2---
        table.row();
        table.add(new Label("Name", skin, "default-red")); //add empty label as spacing element

        Label calcsLabelTitle = new Label(Constants.FINAL_CALCS_TITLE, skin, "default-red");
        table.add(calcsLabelTitle).width(FIELD_WIDTH*2); //.align(Align.left) //calcsLabelTitle.getWidth() - prev width

//
//        if (MainApp.supplyEnabled){
//            Label supplyLabelTitle = new Label("Supply After", skin);
//            supplyLabelTitle.setColor(Color.YELLOW);
//            table.add(supplyLabelTitle).width(FIELD_WIDTH*1.25f);
//        }
//

        //---ROWS 3-? ---- contain amounts
        table.row();
    }


    //sets labels for calculated amounts & before/after supply amounts
    protected void setCalcLabels(){


        //first set the non-flavor amounts
        for (int i = 0; i < 4; i++){
            double amount = amounts.get(i);

            addLabels(i, amount, Constants.CALCLABEL_TEXT[i]);
            table.row();
        }

        //then set the flavor amounts
        for (int i = 0; i < flavors.size; i++){
            addLabels(i, amounts.get(i+4), flavors.get(i).getName());
            table.row();
        }
    }


    protected void addLabels(int i, double amount, String title){
        double drops = amount*dropsPerML;

        //the title label
        table.add(new Label(title, skin));

        //the amount calculated
        Label calcLabel = new Label(Double.toString(amount) + "("+Double.toString(drops)+")", skin);
        table.add(calcLabel);


        if (MainApp.supplyEnabled){
            Label supplyLabel = new Label("0", skin); //0 indicates not supplied
            if (calcUtils.supplied)
                checkSupply(i, supplyLabel);

            table.add(supplyLabel).height(FIELD_HEIGHT);
        }

    }


    protected void checkSupply(int index, Label label){

        double amt = 0;

        if (supplies.containsKey(index)) {
            amt = supplies.get(index);
        }

        //different colors for how much user has
        if (amt > 15) label = new Label("", skin, "default-blue");
        else if (amt < 5)  label = new Label("", skin, "default-red");

        label.setText(Double.toString(amt));
    }


    protected void setButtons(){
        btnTable = getButtonTable();

//        final TextButton copyButton = new TextButton("Copy", skin, "dialog");
        final TextButton copyButton = new TextButton("Copy", skin, "small");
        copyButton.setSize(50, 30);
        copyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (copyButton.isPressed()){
                    toClipboard();
                }
            }
        });

        btnTable.add(copyButton).pad(10);

//        final TextButton closeButton = new TextButton("Close", skin, "dialog");
        final TextButton closeButton = new TextButton("Close", skin, "small");
        closeButton.setSize(50, 30);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (closeButton.isChecked()){
                    remove();
                }
            }
        });

        btnTable.add(closeButton).pad(10);
    }

    String[] texts = {"PG: ", "VG: ", "Other: ", "Nic. Base: "};
    //formats amount values to text for copying/pasting
    protected void toClipboard(){
        String text = "";

        for (int i = 0; i < amounts.size; i++){
            if (i < 4){
                text+= texts[i];
            }
            else{
                text+="Flavor: " + flavors.get(i-4).getName();
            }

            text += "\n\t Amount: "+amounts.get(i).toString() + " ml ("+
                    (amounts.get(i).intValue()*Constants.DROPS_PER_ML) + " drops)" + "\tSupply: " + supplies.get(i);
        }

        Gdx.app.getClipboard().setContents(text);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        //closes window if escape or back button pressed
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            this.remove();
    }
}
