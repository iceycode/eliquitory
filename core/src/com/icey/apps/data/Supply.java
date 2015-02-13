package com.icey.apps.data;

import com.badlogic.gdx.utils.Array;
import com.icey.apps.assets.Constants;
import com.icey.apps.ui.SupplyWindow;

/** the types of supply (type of liquid)
 * 0 = PG
 * 1 = VG
 * 2 = Other
 * 3 = Baes
 * 4 = Flavor
 * 
 * TODO: maybe just make Base & Flavor subclasses of Supply
 * Created by Allen on 1/19/15.
 */
public class Supply {
    
    String name = ""; //the name of the supply
    double totalAmount = -1; //amount total
    int supplyType = -1; //the type of supply (pg, vg, other)
    int flavorType = -1; //for flavors

    Array<Integer> basePercents = new Array<Integer>(); //if this is a base type, then percents are set
    double baseStrength;
    
    double percent;

    double amountNeeded = 0; //the amount needed for recipe

    String errorMsg;

    public Supply(){}

    
    public Supply(double amount, int type){
        this.supplyType = type;
        this.totalAmount = amount;
        this.name = Constants.SUPPLY_NAMES[type];
    }
    
    
    //saves base as a supply
    public Supply(Base base){
        this.supplyType = 3;
        this.totalAmount = base.getTotalAmount();
        this.basePercents = base.getBasePercents();
        this.baseStrength = base.getBaseStrength();
        this.name = Constants.SUPPLY_NAMES[3];
    }
    
    
    //saves flavor as a supply
    public Supply(Flavor flavor){
        this.supplyType = 4;
        this.totalAmount = flavor.getTotalAmount();
        this.name = flavor.getName();
        this.flavorType = flavor.getType();
    }


    /** updates total amount
     *  
     * @param change the change
     */
    public void updateTotalAmount(double change){
        this.totalAmount += change;
    }


    /**checks to see if supply is set
     *
     * @return
     */
    public boolean isSupplySet(){
        if (supplyType==-1 || totalAmount==-1) {
            this.errorMsg = "Error saving liquid supply.";
            return false;
        }
        
        
        if (supplyType == 3){
            if (baseStrength == -1){
                this.errorMsg = "Error saving nicotine base.";
                return false;
            }
                
            
            for (Integer i : basePercents){
                if (i == null) {
                    this.errorMsg = "Error saving flavor.";
                    return false;
                }
            }
        }
        
        if (supplyType >= 4){
            if (name == null){
                this.errorMsg = "Error saving flavor.";
                return false;
            }
            if (flavorType == -1){
                this.errorMsg = "Error saving flavor.";
                return false;
            }
        }

        return true;
    }


    public String getName() {
        //sets a temporary name, if this supply not named
        if (name == ""){
            setTempName();
        }

        return name;
    }
    
    
    public void setTempName(){
        if (supplyType == 0)
            name = "Propylene Glycol";
        else if (supplyType == 1)
            name = "Vegetable Glycerine";
        else if (supplyType == 2)
            name = "EtOH/H2O/other";
        else if (supplyType == 3)
            name = "Nicotine Base";
        else
            name = "Flavor: ";
    }

    public void setPgPercent(int pgPercent) {
        int change = 100 - pgPercent;
        basePercents.set(0, pgPercent);
        basePercents.set(1, change);

        SupplyWindow.percentTextFields.get(1).setText(Integer.toString(change));
    }

    public void setVgPercent(int vgPercent) {
        int change = 100 - vgPercent;
        basePercents.set(1, vgPercent);
        basePercents.set(0, change);

        SupplyWindow.percentTextFields.get(0).setText(Integer.toString(change));
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAmountNeeded() {
        return amountNeeded;
    }

    public void setAmountNeeded(double amountNeeded) {
        this.amountNeeded = amountNeeded;
    }

    public int getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(int supplyType) {
        this.supplyType = supplyType;
    }

    public void setBasePercents(Array<Integer> percents){
        this.basePercents = percents;
    }
    
    public Array<Integer> getBasePercents() {
        return basePercents;
    }

    public double getBaseStrength() {
        return baseStrength;
    }

    public void setBaseStrength(double baseStrength) {
        this.baseStrength = baseStrength;
    }

    public int getFlavorType() {
        return flavorType;
    }

    public void setFlavorType(int flavorType) {
        this.flavorType = flavorType;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }
    
    

    public void setErrorMessage(String message){this.errorMsg = message;}
    
    public String getErrorMessage(){
        return errorMsg;
    }

}
