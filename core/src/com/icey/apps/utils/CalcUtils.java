package com.icey.apps.utils;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.utils.Array;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.SaveData;
import com.icey.apps.data.Supply;
import com.icey.apps.ui.CalcTable;
import com.icey.apps.ui.FlavorTable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Records user input, calculates and saves data relating to recipe
 * - also recieves supply info, updates it & sends it back to supply util class
 * - instead of using EtOH/H2O/etc I used "other" to refer to those liquids
 *      - otherFlavor is a flavor consisting of that
 *
 * Calculation Notes:
 * - remember to use doubleValue() method for Double, or else will not calculate correct values
 * - round the numbers using BigDecimal class (using Rounding.HALF_DOWN)
 * - flavors percentage & base percentage will always use same desired amount
 * - liquid supplies are effected by how much flavor & base is used
 *
 * 2 kinds of assumptions about the data can be made which effect final results:
 *  1) If flavor "other" is also the same as "EtOH/H2O/etc" for regular liquid, then
 *          it will effect the final amount of "EtOH/H2O/etc" that liquid uses
 *  2) Flavor "other" is different from liquid "EtOH/H2O/etc" so has no effect on it
 * TODO: figure out what kind of assumption to make about flavor "other" liquid
 *
 * Created by Allen on 1/14/15.
 *
 */
public class CalcUtils {
    
    private static CalcUtils instance;

    String recipeName; //recipe name
    
    double baseStrength; //base strength
    double amountDesired; //final desired amount
    double strengthDesired; //final strength
    
    Array<Flavor> flavors; //flavors added
    int numFlavors;

    Array<Supply> supplies; //pg, vg or other
    Base base; //nicotine base supply

    Array<Integer> basePercents; //base percents
    Array<Integer> desiredPercents; //desired percents
    Array<Double> finalMills; //all calculated values


    public CalcUtils(){

        //the default values set when calculator screen first seen
        recipeName = "New Recipe";
        amountDesired = Constants.DEFAULT_FINAL_AMT; //default ml desired
        strengthDesired = Constants.DESIRED_STR; //default strength

        desiredPercents = Constants.DEFAULT_DESIRED_PERCENTS;

        flavors = new Array<Flavor>();
        numFlavors = 1;

        if (SupplyUtils.getSupplyUtils().supplied){
            base = SupplyUtils.getSupplyUtils().getBase();
            supplies = SupplyUtils.getSupplyUtils().getSupplies();

            baseStrength = base.getBaseStrength();
            basePercents = base.getBasePercents();
        }
        else{
            base = Constants.DEFAULT_BASE;
            
            baseStrength = base.getBaseStrength(); //strength of nicotine default
            basePercents = base.getBasePercents();
        }

        finalMills = Constants.INITLAL_FINAL_MLS;
    }

    //constructor for testing purposes
    public CalcUtils(double amountDesired, double desiredStrength, Base base, Array<Integer> desiredPercents){
        this.amountDesired = amountDesired;
        this.strengthDesired = desiredStrength;
        this.desiredPercents = desiredPercents;

        this.base = base;
        this.basePercents = base.getBasePercents();
        this.baseStrength = base.getBaseStrength();

        flavors = new Array<Flavor>();
        numFlavors = 0;

        Double[] finalMls = {0.0, 0.0, 0.0, 0.0};
        finalMills = new Array<Double>(finalMls);
    }
    
    public static CalcUtils getCalcUtil(){
        if (instance == null){
            instance = new CalcUtils();
        }
        return instance;
    }
    
    public void setDesiredPercent(String chars, String type){
        int percent = Integer.parseInt(chars);
        int change = 100 - percent;
        
        if (type.equals(Constants.DESIRED_PERC_LABELS[0])){
            desiredPercents.set(0, percent);
            updateVG(change, true);
        }
        else if (type.equals(Constants.DESIRED_PERC_LABELS[1])){
            desiredPercents.set(1, percent);
            updatePG(change, true);
        }
        else if (type.equals(Constants.DESIRED_PERC_LABELS[2])){
            desiredPercents.set(2, percent);
        }
    }


    public void setBasePercent(String chars, String fieldName){
        int percent = Integer.parseInt(chars);
        int change = 100 - percent; //- basePercents.get(2)

        if (fieldName.equals(Constants.BASE_PERC_LABELS[0])){
            basePercents.set(0, percent);
            updateVG(change, false);
        }
        else if (fieldName.equals(Constants.BASE_PERC_LABELS[1])){
            basePercents.set(1, percent);
            updatePG(change, false);
        }
    }

    public void setFlavorPercent(String chars, int index){
        int percent = Integer.parseInt(chars);
        int change = desiredPercents.get(index) - percent;
        
        if (change > 0)
            flavors.get(index).setPercent(change);
    }
    
    public void setFlavorType(CheckBox box, int index){
        if (box.getName().contains("PG")){
            flavors.get(index).setType(0);
        }
        else if (box.getName().contains("VG")){
            flavors.get(index).setType(1);
        }
        else{
            flavors.get(index).setType(2);
        }
        uncheckBoxes(box, index);
        //checkFlavorPercent(index, flavors.get(index).getType());
    }

    private void uncheckBoxes(CheckBox box, int key){
        for (CheckBox b: FlavorTable.instance.checkBoxMap.get(key)){
            if (!box.equals(b) && b.isChecked())
                b.setChecked(false);
        }

    }


    public void updatePG(int change, boolean desired){
        if (desired){
            desiredPercents.set(0, change);
            CalcTable.instance.percentTextFields.get(0).setText(Double.toString(change));
        }
        else{
            basePercents.set(0, change);
            CalcTable.instance.percentTextFields.get(3).setText(Double.toString(change));
        }
    }

    public void updateVG(int change, boolean desired) {
        if (desired){
            desiredPercents.set(1, change);
            CalcTable.instance.percentTextFields.get(1).setText(Double.toString(change));
        }
        else{
            basePercents.set(1, change);
            CalcTable.instance.percentTextFields.get(4).setText(Double.toString(change));
        }
    }



    /** This calc method is the second kind of assupmtion: flavor "other" is unique,
     * - does not effect liquid "other"
     *
     */
    public void calcAmounts(){
        log("desired percents = " + desiredPercents.toString(", DP= "));
        log("base percents = " + basePercents.toString());

        double tempAmt = amountDesired; //so that flavor/base %s not skewed by change if other
        double otherAmt = (desiredPercents.get(2).doubleValue()/100) * amountDesired; //amt of "other" based on %

        //set the base first, since it is requierd to be specific to nicotine amount
        double baseAmt = base.getRecipeAmount(tempAmt, strengthDesired);
        finalMills.set(3, baseAmt);

        //set the flavors next, since need to take into account flavor type of "other"
        for (int i = 0; i < flavors.size; i++){
            Flavor f = flavors.get(i);
            finalMills.set(i + 4, f.calcRecipeAmount(tempAmt));
            amountDesired = f.recalcAmount(amountDesired, finalMills);
        }

        //"other" of liquid also effects amountDesired like "other" of flavor
        if (desiredPercents.get(2).intValue() > 0) {
            finalMills.set(2, otherAmt - finalMills.get(2)); //set other amount, take into account flavor "other" set
            amountDesired -= finalMills.get(2).doubleValue(); //subtract from amount desired
        }


        //now, after taking into acount other liquids, calculate PG & VG amounts
        for (int i = 0; i < 2; i++){
            double liqAmt = amountDesired*(desiredPercents.get(i).doubleValue()/100);
            finalMills.set(i, finalMills.get(i).doubleValue() + liqAmt); //need to add to previous amt as it may be altered
        }

        base.recalcLiquidAmts(finalMills); //recalculate with base percents & amount
        roundAllFinalAmounts();
    }


    /** This calc method is the 1st kind of assumption: flavor "other" is same as liquid "EtOH/H2O/etc"
     *  - so this changes final amount of "other" (EtOH/H2O/etc) of liquid (aka supply)
     *
     */
    public void calcAmounts_Alt(){
        double tempAmt = amountDesired; //so that flavor/base %s not skewed by change if other
        double otherAmt = (desiredPercents.get(2).doubleValue()/100) * amountDesired; //amt of "other" based on %

        //set the base first, since it is requierd to be specific to nicotine amount
        double baseAmt = base.getRecipeAmount(tempAmt, strengthDesired);
        finalMills.set(3, baseAmt);

        //set the flavors next, since need to calculate "other" & take into account flavor type of "other"
        for (int i = 0; i < flavors.size; i++){
            Flavor f = flavors.get(i);
            finalMills.set(i + 4, f.calcRecipeAmount(tempAmt));
            amountDesired = f.recalcAmount_Alt(amountDesired, finalMills, otherAmt);
        }

        //set "other" only if it was not already covered by flavor "other"
        if (otherAmt >= finalMills.get(2).doubleValue()) {
            finalMills.set(2, otherAmt - finalMills.get(2)); //set other amount, take into account flavor "other" set
            amountDesired -= finalMills.get(2).doubleValue(); //subtract from amount desired
        }


        //now, after taking into acount other liquids, calculate PG & VG amounts
        for (int i = 0; i < 2; i++){
            double liqAmt = amountDesired*(desiredPercents.get(i).doubleValue()/100);
            finalMills.set(i, finalMills.get(i).doubleValue() + liqAmt); //need to add to previous amt as it may be altered
        }

        base.recalcLiquidAmts(finalMills); //recalculate with base percents & amount
        roundAllFinalAmounts();
    }

    //rounds all values
    public void roundAllFinalAmounts(){
        //rounds all values to 1 decimal place
        for (int i = 0; i < finalMills.size; i++){
            finalMills.set(i, round(finalMills.get(i)));
            log(" i : " + Integer.toString(i) + ", amt = " + finalMills.get(i).toString());
        }
    }

    /**rounds down all values by half-decimal place, so that values equals desired amount
     *
     * @param value
     * @return returns value that rounds 2nd decimal place to 1st (ie 2.12 ~2.1; 2.55 ~2.5; 2.46 ~2.5
     */
    public double round(Double value){
        //in setScale, first parameter is places to round to
        double d = new BigDecimal(value.doubleValue()).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        return d;
    }


    public boolean areFlavorsSet(){
        for (Flavor f : flavors){
            if (f.isFlavorSet())
                return false;
        }

        return true;
    }
    
    public boolean areDesiredAt100(){
        int totalDesired = 0;
        
        for (Integer i : desiredPercents){
            totalDesired += i.intValue();
        }
        
        if (totalDesired != 100)
            return false;
        
        return true;
    }
    
    public boolean areBaseAt100(){
        int totalBase = 0;
        
        for (Integer i : basePercents){
            totalBase += i;
        }
        
        if (totalBase != 100)
            return false;
        
        return true;
    }
    
    public void addFlavor(Flavor flavor){
        flavors.add(flavor);

        numFlavors = flavors.size;

        finalMills.add(0.0); //add entry into finalMills

        log("Added a new flavor, " + flavor.getName() + "; size of flavors array = " + flavors.size);
    }
    
    public void removeFlavor(int index){
        flavors.removeIndex(index);
        log("removed flavor; size of array = " + flavors.size);
    }

    public String getRecipeName() {
        return recipeName;
    }


    public boolean saved; //whether saved or not
    public void saveData(){
        try {
            SaveData.RecipeData recipeData = new SaveData.RecipeData();

            recipeData.base = new Base(baseStrength, basePercents);

            recipeData.recipeName = this.recipeName;

            recipeData.amountDesired = this.amountDesired;
            recipeData.strengthDesired = this.strengthDesired;
            recipeData.desiredPercents = this.desiredPercents;

            recipeData.flavors = this.flavors;

            recipeData.finalMills = this.finalMills;

            MainApp.saveManager.saveRecipeData(recipeName, recipeData);
            saved = true;
        }
        catch(NullPointerException e){
            saved = false;
        }
    }

    public boolean loaded;
    public void loadData(String name, boolean encoded){
        log("Data loading...");
//        MainApp.saveManager.getRecipeData(name, this);
        try {
            SaveData.RecipeData data = (SaveData.RecipeData) MainApp.saveManager.loadRecipeData(name);
            recipeName = data.recipeName;
            amountDesired = data.amountDesired;
            strengthDesired = data.strengthDesired;
            desiredPercents = data.desiredPercents;

            //Base
            base = data.base;

            //base strengths
            baseStrength = data.strengthNic;
            basePercents = data.basePercents;

            //flavors
            flavors = data.flavors;

            finalMills = data.finalMills; //final amounts
            log("Desired percents = " + desiredPercents.toString(", "));

            loaded = true;
        }
        catch (NullPointerException e){
            log("All values from SaveData not there!" + e.toString());
        }
    }
    
    public void deleteRecipe(String recipeName){
        MainApp.saveManager.deleteRecipe(recipeName);
    }
    
    
    public boolean recipesFound;
    public Array<String> getAllRecipes(){
        Array<String> recipeTitles = new Array<String>();
        
        try{
            if (MainApp.saveManager.getRecipeData().size > 0) {
                for (String title : MainApp.saveManager.getRecipeData().keys())
                    recipeTitles.add(title);
            }
            recipesFound = true;
        }
        catch(NullPointerException e){
            log(e.toString());
            recipesFound = false;
        }

        
        return recipeTitles;
    }

    public void setBaseStrength(double baseStrength) {
        this.baseStrength = baseStrength;
    }

    public void setStrengthDesired(double strengthDesired) {
        this.strengthDesired = strengthDesired;
    }

    public void setBasePercents(Array<Integer> basePercents) {
        this.basePercents = basePercents;
    }

    public void setBase(Base base){
        this.base = base;
    }

    public void setDesiredPercents(Array<Integer> desiredPercents) {
        this.desiredPercents = desiredPercents;
    }

    public void setFlavors(Array<Flavor> flavors){
        this.flavors = flavors;

        for (int i = 0; i < flavors.size; i++) {
//            if (flavors.get(i).getType() == 2)
//                otherFlavors.add(flavors.get(i));
            finalMills.add(0.0);
        }
    }

    public void setFinalMills(Array<Double> finalMills){
        this.finalMills = finalMills;
    }

    public Array<Flavor> getFlavors() {
        return flavors;
    }

    public Array<Double> getFinalMills() {
        return finalMills;
    }

    public double getBaseStrength() {
        return baseStrength;
    }

    public double getAmountDesired() {
        return amountDesired;
    }

    public double getStrengthDesired() {
        return strengthDesired;
    }

    public Array<Integer> getBasePercents() {
        return basePercents;
    }

    public Array<Integer> getDesiredPercents() {
        return desiredPercents;
    }

    public void setRecipeName(String name){
        this.recipeName = name;
    }

    public void setAmountDesired(double amountDesired){
        this.amountDesired = amountDesired;
    }

    public void setFlavorName(String name, int index){
        flavors.get(index).setName(name);
    }


    //logs messages
    private void log(String message){
        System.out.println("CalcUtil LOG: " + message);
    }
}


//TODO: get rid of these if not using:

//    /**the difference the base pg/vg makes in the regular PG/VG values
//     *
//     */
//    private void baseDifference(double baseAmt){
//        for (int i = 0; i < 2; i++){
//            double amt = amountDesired-(baseAmt*(basePercents.get(i).doubleValue()/100));
//            finalMills.set(i, rounded(amt));
//        }
//    }
//
//
//    /**the difference the flavor pg/vg/other makes in the regular PG/VG/other values
//     *
//     * @param type : type of flavor - PG, VG or Other
//     * @param amount : the amount, so subtract from original amount to calculate true PG/VG ratios
//     */
//    private void flavorDifference(int type, double amount){
//        if (type < 2){
//            double prevAmt = finalMills.get(type).doubleValue();
//            finalMills.set(type, rounded(prevAmt - amount));
//        }
//        else{
//            amountDesired -= amount;
//        }
//
//        amountDesired -= amount;
//    }


//    public void updateBasePG(int change){
//        basePercents.set(0, basePercents.get(0));
//        CalcTable.instance.percentTextFields.get(3).setText(Double.toString(change));
//    }
//
//    public void updateBaseVG(int change){
//        basePercents.set(1, change);
//        CalcTable.instance.percentTextFields.get(4).setText(Double.toString(change));
//    }
//
//    public void updateBaseOther(int change){
//        basePercents.set(2, change);
//        CalcTable.instance.percentTextFields.get(5).setText(Double.toString(change));
//    }


//    private void checkFlavorPercent(int index, int type){
//        int percent = flavors.get(index).getPercent();
//        int change = desiredPercents.get(type) - percent;
//
//        if (change > 0) {
//            desiredPercents.set(type, change);
//            //ensure100(desiredPercents, type, change);
//        }
//    }
//
//    private void checkFlavorType(int index, int percent){
//        int type = flavors.get(index).getType();
//        if (type != -1){
//            int change = desiredPercents.get(type) - percent;
//            desiredPercents.set(type, percent);
//        }
//    }
