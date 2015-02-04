package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.SaveData;
import com.icey.apps.data.Supply;
import com.icey.apps.screens.SupplyScreen;

/** Manages the supplies user has
 * - Works with SupplyScreen & CalculatorScreen
 *  On supply screen, stores the data
 *  On CalculatorScreen, it gets updates for how much is being used
 *    
 *  NOTE: only can hold 1 kind of Base & 1 of each: PG, VG & Other
 *    can always add onto these later
 *  Can hold multiple flavors, starts at 4
 *
 * TODO: create error checking methods
 *
 * Created by Allen on 1/19/15.
 */
public class SupplyUtils {
    
    public static SupplyUtils instance;
    
    SaveManager saveManager = MainApp.saveManager; //for saving recipes

    IntMap<Object> supplyMap; //supplies loaded/saved into supplyMap
    public boolean supplied; //whether user is supplied or not

    public int lastFlavorKey = 4;

    public SupplyUtils(){
        supplyMap = saveManager.getSupplyData();

        if (supplyMap.size > 0){
            supplied = true;
            lastFlavorKey = getLastFlavorKey();
        }
        else{
            log("no supplies are stored...going to need to add values OR defaul is set");
            //TODO: figure out whether a default map should be set (method created already)
            supplied = false;//need to enter in the supplies first
        }
    }

    public static SupplyUtils getSupplyUtils(){
        if (instance == null){
            instance = new SupplyUtils();
        }
        return instance;
    }
    
    //returns a map of the supplies to amounts
    public OrderedMap<Integer, Double> getAllSupplyAmounts(){
        OrderedMap<Integer, Double> supplyAmounts = new OrderedMap<Integer, Double>();

        for (int i = 0; i < supplyMap.size; i++) {
            SaveData.SupplyData data = (SaveData.SupplyData)supplyMap.entries().next().value;
            double total = getSupplyTotal(data);
            
            //finds key of vlaue, -1 if not found
            int key = supplyMap.findKey(supplyMap.entries().next().value, false, -1);

            supplyAmounts.put(key, total);
        }

        return supplyAmounts;
    }

    public double getSupplyTotal(SaveData.SupplyData data) {
        double total;
        //check for supply class & then assign total
        if (data.supply != null) total = data.supply.getTotalAmount();
        else if (data.base != null) total = data.base.getTotalAmount();
        else total = data.flavor.getTotalAmount();

        log("total = " + Double.toString(total));

        return total;
    }
    
    public Array<Supply> getSupplies(){
        Array<Supply> supplies = new Array<Supply>();
        
        for (int i = 0; i < 3; i++){
            supplies.add((Supply)getSupplyByType(i));
        }
        
        return supplies;
    }
    
    public Base getBase(){
        return (Base)getSupplyByType(3);
    }

    //returns an array of all the flavors
    public Array<Flavor> getAllFlavors(){
        Array<Flavor> flavorSupply = new Array<Flavor>();
        
        if (supplyMap.size >= 4) {
            for (int i = 4; i < supplyMap.size; i++) {
                flavorSupply.add((Flavor)getSupplyByType(i));
            }
        }
        
        return flavorSupply;
    }
    
    /** returns a supply, base or flavor
     *
     * @param key : the key of supply
     * @return null (should never return this)
     */
    public Object getSupplyByType(int key){
        SaveData.SupplyData supplyData = (SaveData.SupplyData)supplyMap.get(key);
        
        if (supplyData.supply != null) return supplyData.supply;
        else if (supplyData.base != null) return supplyData.base;
        else if (supplyData.flavor != null) return supplyData.flavor;
        
        return null;
    }

    //need to obtain any keys >= 4
    public int getLastFlavorKey(){
        int largestKey = 4;

        //iterates through keys & compares to 4 (smallest key for flavors)
        for (int i = 0; i < supplyMap.size; i++){
            int key = supplyMap.keys().next(); //get next key
            if (key > largestKey)
                largestKey = key;
        }

        return largestKey;
    }
    
    /** adds a supply & saves it
     * 
     * @param key
     * @param supply
     */
    public void saveSupply(int key, Object supply){

        if (supplyMap.containsKey(key)){
            SaveData.SupplyData data = updateSupply(key, supply);
            SupplyScreen.instance.updateSupplyTable(key, data);
        }
        else{
            SaveData.SupplyData data = saveNewSupply(key, supply);
            SupplyScreen.instance.addToSupplyTable(data);
        }

    }

    /** saves & returns a new supply as supplyData object
     *
     * @param key
     * @param supply
     * @return
     */
    public SaveData.SupplyData saveNewSupply(int key, Object supply){
        SaveData.SupplyData supplyData = new SaveData.SupplyData();

        if (key < 3)
            supplyData.supply = (Supply)supply;
        else if (key == 3)
            supplyData.base= (Base)supply;
        else
            supplyData.flavor = (Flavor)supply;

        supplyMap.put(key, supplyData);

        saveManager.saveSupplyData(key, supplyData);

        return supplyData;
    }


    /** updates an already saved supply
     *
     * @param key
     * @param supply
     * @return
     */
    public SaveData.SupplyData updateSupply(int key, Object supply){
        SaveData.SupplyData supplyData = (SaveData.SupplyData)supplyMap.get(key);

        if (key < 3) {
            supplyData.supply = (Supply) supply;

        }
        else if (key == 3) {
            supplyData.base = (Base) supply;

        }
        else{
            supplyData.flavor = (Flavor)supply;
        }


        //save into supply utils map
        supplyMap.remove(key);
        supplyMap.put(key, supplyData);

        //save into saved map
        saveManager.updateSupplyData(key, supplyData);

        return supplyData;
    }


    
    public void removeSupply(int key){
        saveManager.deleteSupply(key);
    }
    
    
    public IntMap<Object> setEmptySupplyMap(){
        IntMap<Object>  data = new IntMap<Object> ();
        
        for (int i = 0; i < 3; i++){
            SaveData.SupplyData supplyData = new SaveData.SupplyData();
            supplyData.supply = new Supply(0, i);
            data.put(i, supplyData);
        }
        
        SaveData.SupplyData baseData = new SaveData.SupplyData();
        baseData.base = Constants.DEFAULT_BASE;
        data.put(3, baseData);
        
        SaveData.SupplyData flavorData = new SaveData.SupplyData();
        flavorData.flavor = Constants.FLAVOR_DEFAULT;
        data.put(4, flavorData);
        
        return data;
    }
    
    public IntMap<Object> getSupplyMap(){
        return supplyMap;
    }


    private void log(String message){
        Gdx.app.log("SupplyUtils LOG: ", message);
    }
}
