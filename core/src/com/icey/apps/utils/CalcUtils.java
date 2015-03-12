package com.icey.apps.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.SnapshotArray;
import com.icey.apps.MainApp;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
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
 *
 * Created by Allen on 1/14/15.
 *
 */
public class CalcUtils {
    
    private static CalcUtils instance;

    String recipeName; //recipe name
    
    double amountDesired; //final desired amount
    double strengthDesired; //final strength
    Array<Integer> desiredPercents; //desired percents

    Array<Flavor> supplyFlavors; //flavors that are in supply
    Array<Flavor> flavors; //flavors added in screen
    Array<Flavor> loadedFlavors; //flavors from RecipeData

    Base base; //nicotine base supply
    double baseStrength; //base strength
    Array<Integer> basePercents; //base percents

    Array<Double> goalAmounts; //all calculated values (permament storage)
    Array<Double> finalSupplyAmts; //the supply amount after recipe calculated
    public SnapshotArray<Double> loadedAmounts; //temporary final mills (after calculating)
    
    //fields for the supplies
    SupplyUtils supplyUtils = SupplyUtils.getSupplyUtils();
    IntMap<Double> supplyAmounts;
    
    IntMap<Supply> supplies;

    public boolean flavorSupplied = false;
    public boolean supplied = false;

    String errorDetail = ""; //details about error, if applicable

    public CalcUtils(){

        //the default values set when calculator screen first seen
        recipeName = "New Recipe";
        amountDesired = Constants.ZERO_FINAL_AMOUNT; //default ml desired
        strengthDesired = Constants.ZERO_STRENGTH; //default strength

        //set desired percents at 0
        desiredPercents = Constants.DEFAULT_DESIRED_PERCENTS;

        //set the base
        base = Constants.DEFAULT_BASE;
        basePercents = base.getBasePercents();

        flavors = new Array<Flavor>();

        goalAmounts = Constants.INITLAL_FINAL_MLS;
        
//        if (supplyUtils.supplied)
//            setSupplies(); //set amounts in the supply & if supplied
    }

    
    public static CalcUtils getCalcUtil(){
        if (instance == null){
            instance = new CalcUtils();
        }
        return instance;
    }


    //===========user entries for calculations==============
    /** Sets the percent based on field user changed
     * 0, 1 ,2 : Goal PG, VG, Other TextFields
     *  3,4 : Base PG, VG TextFields
     *
     * @param percent: percent typed in
     * @param type: the type of listener (field being listened to)
     */
    public void setPercent(int percent, int type){

        int change = 100 - percent; //change in PG or VG
        
        //desired percents are set
        if (type == 0){
            desiredPercents.set(0, percent);
            updateVG(change, true);
        }
        else if (type == 1){
            desiredPercents.set(1, percent);
            updatePG(change, true);
        }
        else if (type == 2){
            desiredPercents.set(2, percent);
        }
        //base percents set
        else if (type == 3){
            basePercents.set(0, percent);
            updateVG(change, false);
        }
        else if (type == 4){
            basePercents.set(1, percent);
            updatePG(change, false);
        }
    }


    /** Sets PG/VG Ratio - used by slider
     *
     * @param percent : the percent PG
     * @param type : the type of slider: Base or Desired
     */
    public void setRatio(int percent, int type){
        int change = 100 - percent; //change in PG or VG

        if (type == 0){
            desiredPercents.set(0, percent);
//            CalcTable.percentTextFields.get(0).setText(Double.toString(percent));
            String newText = "PG:VG : " + percent + ":" + change;
            CalcTable.percentLabels.get(0).setText(newText);
            updateVG(change, true);
        }
        else if (type == 2){
            desiredPercents.set(2, percent);
            String newText = "Other % : " + percent ;
            CalcTable.percentLabels.get(1).setText(newText);
        }
        else{
            basePercents.set(0, percent);
//            CalcTable.percentTextFields.get(3).setText(Double.toString(percent));
            String newText = "PG:VG : " + Integer.toString(percent) + ":" + Integer.toString(change);
            CalcTable.percentLabels.get(2).setText(newText);
            updateVG(change, false);
        }
    }

    
    //adds a flavor to the supply
    public void addFlavor(Flavor flavor){
        flavors.add(flavor);

//        numFlavors = flavors.size;
        goalAmounts.add(0.0); //add entry into goalAmounts

        log("Added a new flavor, " + flavor.getName() + "; size of flavors array = " + flavors.size);
    }
    
    
//    public void switchFlavor(int index, Flavor flavor){
//        flavors.set(index, flavor);
//    }
    

    public void removeFlavor(int index){
        flavors.removeIndex(index);
        goalAmounts.removeIndex(index);
        log("removed flavor; size of array = " + flavors.size);
    }


    public void removeAllFlavors(){
        flavors.clear();
        goalAmounts.removeRange(4, goalAmounts.size-1);
    }


    //flavor percent
    public void setFlavorPercent(String chars, int index){
        int percent = Integer.parseInt(chars);

        if (percent > 0)
            flavors.get(index).setPercent(percent);
    }

    //flavor percent from slider
    public void setFlavorPercent(int percent, int index){
        flavors.get(index).setPercent(percent);
        String newText = "Flavor % : " + percent;
        FlavorTable.percentLabels.get(index).setText(newText);
    }
    

    
    //flavor type
    public void setFlavorType(int type, int index){
        flavors.get(index).setType(type);
    }


    public void updatePG(int change, boolean desired){
        if (desired){
            desiredPercents.set(0, change);
//            CalcTable.percentTextFields.get(0).setText(Double.toString(change));
        }
        else{
            basePercents.set(0, change);
//            CalcTable.percentTextFields.get(3).setText(Double.toString(change));
        }
    }

    public void updateVG(int change, boolean desired) {
        if (desired){
            desiredPercents.set(1, change);

//            if (Gdx.app.getType()== Application.ApplicationType.Desktop)
//                CalcTable.percentTextFields.get(1).setText(Double.toString(change));
        }
        else{
            basePercents.set(1, change);
//            CalcTable.percentTextFields.get(4).setText(Double.toString(change));
        }
    }

    
    //=============CALCULATIONS====================//
    /** This calc method is the second kind of assumption: flavor "other" is unique,
     * - does not effect liquid "other"
     *
     * @return : final amounts
     */
    public SnapshotArray<Double> calcAmounts(){

        //need to copy these before so that 2nd calculation is not altered
        Array<Double> finalMills = new Array<Double>(this.goalAmounts);
        Array<Flavor> flavors = new Array<Flavor>(this.flavors);
        double amountDesired = this.amountDesired;

        double tempAmt = amountDesired; //so that flavor/base %s not skewed by change if other
        double otherAmt = (desiredPercents.get(2).doubleValue()/100) * amountDesired; //amt of "other" based on %

        //set the base first, since it is requierd to be specific to nicotine amount
        double baseAmt = calcBaseAmount(tempAmt, strengthDesired, base);
        finalMills.set(3, baseAmt);

        //set the flavors next, since need to take into account flavor type of "other"
        for (int i = 0; i < flavors.size; i++){
            Flavor f = flavors.get(i);
            finalMills.set(i + 4, calcFlavorAmount(tempAmt, f));
            amountDesired = subtractFlavorAmounts(amountDesired, finalMills, f);
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

        subtractBaseAmounts(finalMills);//recalculate with base percents & amount

        //round all amounts
        roundAllFinalAmounts(finalMills);
        
//        if (supplied && MainApp.supplyEnabled)
//            updateSupplyAmounts(goalAmounts); //updates the supply amount //TODO: uncomment this in pro version

        return new SnapshotArray<Double>(finalMills);
    }

    /** returns the amount of this flavor for recipe, or -1 if cannot
     * - based on percent of flavor & end amount desired
     *
     * @param amountDesired : the amount desired
     * @param flavor: the flavor whose amount is set
     */
    public double calcFlavorAmount(double amountDesired, Flavor flavor){
        double amount = -1;

        if (flavor.getPercent() == -1 || flavor.getType() == -1)
            flavor.setAmount(-1);
        else{
            amount = amountDesired*((double)flavor.getPercent()/100);
            flavor.setAmount(amount);
        }

        return amount;
    }


    /** sets the base amount needed
     *
     * @param amountDesired : amount user wants
     * @param strengthDesired : strength user wants
     * @param base : the base object
     * @return : the amount of base needed
     */
    public double calcBaseAmount(double amountDesired, double strengthDesired, Base base){
        double amountNeeded = (strengthDesired/baseStrength)*amountDesired;
        base.setAmountNeeded(amountNeeded);

        return amountNeeded;
    }

    /** sets amount needed for flavor in calculated amount array
     *
     * @param amountDesired : goal amount desired
     * @param finalMills : the final calculations
     * @param flavor : the flavor
     *
     * @return the recalculated amount needed for PG, VG or other
     */
    public double subtractFlavorAmounts(double amountDesired, Array<Double> finalMills, Flavor flavor){
        if (flavor.getType() == 2){
            amountDesired -= flavor.getAmount();
        }
        else{
            finalMills.set(flavor.getType(), finalMills.get(flavor.getType()).doubleValue() - flavor.getAmount());
        }

        return amountDesired;
    }

    
    //subtracts the amount of PG/VG base adds to mix
    protected void subtractBaseAmounts(Array<Double> finalMills){
        for (int i = 0; i < 2; i++){
            double baseAmt = finalMills.get(3) * (basePercents.get(i).doubleValue()/100);
            double amt = finalMills.get(i).doubleValue() - baseAmt;
            finalMills.set(i, amt);
        }
    }
    
    
    //rounds all values
    protected void roundAllFinalAmounts(Array<Double> finalMills){
        //rounds all values to 1 decimal place
        for (int i = 0; i < finalMills.size; i++){
            finalMills.set(i, round(finalMills.get(i).doubleValue()));
            log(" i : " + Integer.toString(i) + ", amt = " + finalMills.get(i).toString());
        }
    }


    /**rounds down all values by half-decimal place, so that values equals desired amount
     *
     * @param value : the amount to be rounded
     * @return value rounded 2nd decimal place to 1st (ie 2.12 ~2.1; 2.55 ~2.5; 2.46 ~2.5
     */
    public double round(double value){
        //in setScale, first parameter is places to round to
        if (value > 0) {
            try {
                return new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            } catch (Exception e) {
                log("Number NaN or Infinity: " + value);//number either 0.0 or Infinite
            }

            return round(Math.round(value)); //try again using Math.round, then BigDecimal
        }

        return 0;
    }

//    /** updates supply amounts (only for screen) FIXME: uncomment for pro version
//     * stores them as temporary amounts, then if saved, updates supply utils with the amounts
//     *
//     * @param finalMills : final amounts calculated
//     */
//    public void newSupplyAmounts(Array<Double> finalMills){
//        finalSupplyAmts = new Array<Double>();
//
//        for (int i = 0; i < finalMills.size; i++){
//            if (supplyAmounts.containsKey(i)){
//                double newAmt = supplyAmounts.get(i) - finalMills.get(i).doubleValue();
//
//                if (newAmt != supplyAmounts.get(i)){
//                    supplyAmounts.remove(i);
//                    supplyAmounts.put(i, newAmt);
//
//                    supplyUtils.updateSupply(i, newAmt);
//                }
//            }
//        }
//    }

    
    
    //===============methods which check to see if user set all values==============
    public boolean isGoalSet(){
        if (amountDesired<= 0){
            return false;
        }

        return true;
    }

    public boolean areFlavorsSet(){
        for (Flavor f : flavors){
            log(f.toString() +"\n Percent= "+ f.getPercent() + "\n Type = " + f.getType());
            errorDetail = "Flavor, " + f.getName();
            if (f.getType() == -1){
                errorDetail += ", type not set";
                return false;
            }

            if (f.getPercent() <= 0){
                errorDetail += ", percent not set";
                return false;
            }
        }

        return true;
    }

    //returns a detailed error about values not being set
    public String getError(){
        return errorDetail;
    }
    
    
    //returns name of recipe
    public String getRecipeName() {
        return recipeName;
    }


    public void saveData(Array<Double> finalMills){

        try {
            MainApp.saveManager.saveRecipeData(recipeName, new Array<Double>(finalMills));
        }
        catch(Exception e){
            log(e.toString());
        }
    }


    /** Loads data from recipe
     *
     * @param name : name of recipe
     */
    public void loadRecipeData(String name){
        log("Data loading...");

        try {
            SaveManager.RecipeData data = (SaveManager.RecipeData) MainApp.saveManager.loadRecipeData(name);
            recipeName = data.recipeName;
            amountDesired = data.amountDesired;
            strengthDesired = data.strengthDesired;
            desiredPercents = data.desiredPercents;

            //Base
            base = data.base;

            //base percents & strength
            baseStrength = base.getBaseStrength();
            basePercents = base.getBasePercents();

            //flavors
            loadedFlavors = data.flavors;

            //set loadedAmounts to finalMills (calculated values from saved Recipe)
            loadedAmounts = new SnapshotArray<Double>(data.finalMills);
        }
        catch (Exception e){
            log("All values from SaveData not there!" + e.toString());
        }
    }

    /** loads the recipe rating (for loading recipes)
     *
     * @param name : name of recipe
     * @param encoded : whether it is encoded or not
     */
    public int getRecipeRating(String name, boolean encoded){
        int rating = 0;

        try{
            SaveManager.RecipeData data = (SaveManager.RecipeData) MainApp.saveManager.loadRecipeData(name);

            rating = data.rating;
        }
        catch(Exception e){
            log("NO RATING");
            return rating;
        }

        return rating;
    }


    /** sets recipe rating
     *
     * @param name : name of recipe being rated
     * @param rating : rating of recipe (number of checkboxes set)
     * @param encoded : encoded or not
     */
    public void setRecipeRating(String name, int rating, boolean encoded){
        SaveManager.RecipeData data = (SaveManager.RecipeData) MainApp.saveManager.loadRecipeData(name);
        data.rating = rating;
    }


    /** deletes recipe
     *
     * @param recipeName : the recipe name
     */
    public void deleteRecipe(String recipeName){
        MainApp.saveManager.deleteRecipe(recipeName);
    }
    

    public Array<String> getAllRecipes(){
        Array<String> recipeTitles = new Array<String>();

        if (MainApp.saveManager.getData().size > 0) {
            for (String title : MainApp.saveManager.getRecipeMap().keys())
                recipeTitles.add(title);
        }

        return recipeTitles;
    }
    
//    protected void setSupplies(){
//        supplies = supplyUtils.getSupplyMap();
//
//        base = supplyUtils.getBase();
//        baseStrength = base.getBaseStrength();
//        basePercents = base.getBasePercents();
//
//
//        supplyFlavors = supplyUtils.getAllFlavors();
//        if (supplyFlavors.size > 0){
//            flavorSupplied = true;
//        }
//
//        supplyAmounts = supplyUtils.getSupplyAmounts();
//
//        supplied = true;
//
////        log("PG amount = " + supplies.get(0).getTotalAmount() + "\nVG amount = " + supplies.get(1).getTotalAmount()
////                + "\nbase percents = " + basePercents.toString() + "\nbase strength = " + baseStrength
////                    + "\nOther amount = " + supplies.get(2).getTotalAmount());
//
//    }

    public IntMap<Double> getSupplyAmounts(){
        return supplyAmounts;
    }


    //==========getters & setters=========

    public void setBaseStrength(double strength){
        this.baseStrength = strength;
    }

    public double getBaseStrength() {
        return baseStrength;
    }

    public void setStrengthDesired(double strengthDesired) {
        this.strengthDesired = strengthDesired;
    }

    public double getStrengthDesired() {
        return strengthDesired;
    }

    public void setBasePercents(Array<Integer> basePercents) {
        this.basePercents = basePercents;
    }

    public void setBase(Base base){
        this.base = base;
    }


    public Array<Flavor> getFlavors() {
        return flavors;
    }

    public Array<Flavor> getLoadedFlavors(){
        return loadedFlavors;
    }

    public double getAmountDesired() {
        return amountDesired;
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


    private void logCalculations(){
        //some logs
        for (Flavor f : flavors){
            log("flavor percent " + f.getPercent());
        }
        log("desired percents = " + desiredPercents.toString() + "\nbase percents = " + basePercents.toString()
                + "\nbase strength = " + baseStrength + "\ndesired strength = " + strengthDesired
                + "\ndesired amount = " + amountDesired );
    }

//    /** This calc method is the 1st kind of assumption: flavor "other" is same as liquid "EtOH/H2O/etc"
//     *  - so this changes final amount of "other" (EtOH/H2O/etc) of liquid (aka supply)
//     *
//     */
//    public void calcAmounts_Alt(){
//        double tempAmt = amountDesired; //so that flavor/base %s not skewed by change if other
//        double otherAmt = (desiredPercents.get(2).doubleValue()/100) * amountDesired; //amt of "other" based on %
//
//        //set the base first, since it is requierd to be specific to nicotine amount
//        double baseAmt = base.getRecipeAmount(tempAmt, strengthDesired);
//        goalAmounts.set(3, baseAmt);
//
//        //set the flavors next, since need to calculate "other" & take into account flavor type of "other"
//        for (int i = 0; i < flavors.size; i++){
//            Flavor f = flavors.get(i);
//            goalAmounts.set(i + 4, f.calcRecipeAmount(tempAmt));
//            amountDesired = f.recalcAmount_Alt(amountDesired, goalAmounts, otherAmt);
//        }
//
//        //set "other" only if it was not already covered by flavor "other"
//        if (otherAmt >= goalAmounts.get(2).doubleValue()) {
//            goalAmounts.set(2, otherAmt - goalAmounts.get(2)); //set other amount, take into account flavor "other" set
//            amountDesired -= goalAmounts.get(2).doubleValue(); //subtract from amount desired
//        }
//
//
//        //now, after taking into acount other liquids, calculate PG & VG amounts
//        for (int i = 0; i < 2; i++){
//            double liqAmt = amountDesired*(desiredPercents.get(i).doubleValue()/100);
//            goalAmounts.set(i, goalAmounts.get(i).doubleValue() + liqAmt); //need to add to previous amt as it may be altered
//        }
//
//        subtractBaseAmounts(); //recalculate with base percents & amount
//        roundAllFinalAmounts(goalAmounts);
//    }

}
