package com.icey.apps.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Flavor;
import com.icey.apps.utils.CalcUtils;
import com.icey.apps.utils.SupplyUtils;

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
    private final float FIELD_HEIGHT = Constants.TEXT_FIELD_HEIGHT;

    public CalcWindow(Skin skin, Array<Double> amounts, Array<Flavor> flavors){
        super("", skin);
        this.skin = skin;

        this.amounts = amounts;
        this.flavors = flavors;
        this.supplies = SupplyUtils.getSupplyUtils().getSupplyAmounts();

        setTable();

        setCalcLabels();

        setButtons();
    }

    //sets up the table
    protected void setTable(){
        table = getContentTable();

        table.columnDefaults(0).prefWidth(125).prefHeight(40);
        table.columnDefaults(1).prefWidth(75).prefHeight(25);
        table.columnDefaults(2).prefWidth(75).prefHeight(25);

        //the main title
        Label title = new Label("Calculated Amounts", skin);
        table.add(title).colspan(3).center();


        //---ROW 2---
        table.row();
        add(new Label("", skin)); //add empty label as spacing element

        Label calcsLabelTitle = new Label(Constants.FINAL_CALCS_TITLE, skin);
        table.add(calcsLabelTitle).width(FIELD_WIDTH*2); //.align(Align.left) //calcsLabelTitle.getWidth() - prev width

        Label supplyLabelTitle = new Label(Constants.SUPPLY_AMTS_TITLE, skin);
        supplyLabelTitle.setColor(Color.YELLOW);
        table.add(supplyLabelTitle).width(FIELD_WIDTH*1.25f);

    }


    //sets labels for calculated amounts & before/after supply amounts
    protected void setCalcLabels(){

        //first set the non-flavor amounts
        for (int i = 0; i < 4; i++){
            addLabels(i, amounts.get(i), Constants.CALCLABEL_TEXT[i]);
            table.row();
        }

        //then set the flavor amounts
        for (int i = 0; i < flavors.size; i++){
            addLabels(i, amounts.get(i+4), flavors.get(i).getName());
            table.row();
        }
    }


    protected void addLabels(int i, double amount, String title){
        //the title label
        table.add(new Label(title, skin));

        //the amount calculated
        Label calcLabel = new Label(Double.toString(amount), skin, "calcsLabel");
        table.add(calcLabel);

        Label supplyLabel = new Label("0", skin, "supply"); //0 indicates not supplied
        if (calcUtils.supplied)
            checkSupply(i, supplyLabel);

        table.add(supplyLabel);
    }


    protected void checkSupply(int index, Label label){

        double amt = 0;

        if (supplies.containsKey(index)) {
            amt = supplies.get(index);
        }

        //different colors for how much user has
        if (amt > 30)
            label.getStyle().fontColor = Color.BLACK;
        else if (amt > 15)
            label.getStyle().fontColor = Color.BLUE;
        else if (amt < 5)
            label.getStyle().fontColor = Color.RED;

        label.setText(Double.toString(amt));
    }


    protected void setButtons(){
        btnTable = getButtonTable();

        final TextButton copyButton = new TextButton("Copy", skin, "dialog");
        copyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (copyButton.isPressed()){
                    //copy to text
                }
            }
        });

        btnTable.add(copyButton).pad(10);

        final TextButton closeButton = new TextButton("Close", skin, "dialog");
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
    protected void toText(){
        String text = "";

        for (int i = 0; i < amounts.size; i++){
            if (i < 4){
                text+= texts[i];
            }
            else{
                text+="Flavor: " + flavors.get(i-4).getName();
            }

            text += "\n\t Amount: "+amounts.get(i).toString() + " ml ("+
                    (amounts.get(i).intValue()*20) + " drops)" + "\tSupply: " + supplies.get(i);
        }
    }


}
