package com.icey.apps.data;

import com.badlogic.gdx.utils.Array;

/** the types of supplies & amounts
 * 0 = PG
 * 1 = VG
 * 2 = Other
 * 3 = Base
 * 4 = flavor
 * NOTE: if a supply of same type is added, add 10 to current supply type if exists
 * in the supplyData map
 *   
 * Created by Allen on 1/19/15.
 */
public class Supply {
    
    String name; //the name of the supply
    double totalAmount = -1; //amount total
    int supplyType = -1; //the type of supply (nic base, pg, vg, etc)

    Array<Double> basePercents; //if this is a base type, then percents are set

    public Supply(){}

    
    public Supply(double amount, int type){
        this.supplyType = type;
        this.totalAmount = amount;
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
        if (supplyType==-1 || totalAmount==-1)
            return false;

        return true;
    }


    public String getName() {
        if (supplyType == 0)
            name = "Propylene Glycol";
        else if (supplyType == 1)
            name = "Vegetable Glycerine";
        else
            name = "EtOH/H2O/other";
        return name;
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

    public int getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(int supplyType) {
        this.supplyType = supplyType;
    }

    public Array<Double> getBasePercents() {
        return basePercents;
    }

}
