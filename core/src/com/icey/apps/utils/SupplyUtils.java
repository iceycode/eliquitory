package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.icey.apps.MainApp;
import com.icey.apps.assets.Constants;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
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
 *  TODO: figure out whether a default map should be set (method created already)
 *  TODO: show a small popup it supplies are not set, urging user to enter them
 *
 * Created by Allen on 1/19/15.
 */
public class SupplyUtils {
    
    public static SupplyUtils instance;
    
    SaveManager saveManager = MainApp.saveManager; //for saving recipes

    IntMap<Supply> supplyMap; //supplies loaded/saved into supplyMap
    IntMap<Supply> emptyMap; //an empty map of supplies, if none is present
    
    IntMap<Double> supplyAmounts; //amount of supplies
    IntMap<Double> flavorAmounts; //amount of flavors
    public boolean supplied = false; //whether user is supplied or not
    public boolean supplyChange = false; //whether supply changed
    
    public Supply supply; //current supply being saved in window
    public Supply supplyPrev; //previous supply
    
    public int lastFlavorKey = 4;

    public SupplyUtils(){
        supplyMap = saveManager.getSupplyData();
        
        if (supplyMap.size > 0){
            //fillMissingValues(); //fills missing supplies
            setSupplyAmounts(); //sets the amounts
            
            supplied = true;
        }
        else{
            log ("no supply map is stored in save file");
            emptyMap = emptySupplyMap();
            setSupplyAmounts();
        }
    }

    public static SupplyUtils getSupplyUtils(){
        if (instance == null){
            instance = new SupplyUtils();
        }
        return instance;
    }


    public Array<Supply> getSupplies(){
        Array<Supply> supplies = new Array<Supply>();
        
        for (int i = 0; i < 3; i++){
            Supply s = getSupplyByType(i);
            if (s != null)
                supplies.add(s);
            else
                supplies.add(new Supply(0, i));
        }
        
        return supplies;
    }
    
    public Base getBase(){
        if (supplyMap.containsKey(3))
            return new Base(supplyMap.get(3));
        
        return new Base(0, 0, Constants.ZERO_BASE_PERCENTS);
    }

    //returns an array of all the flavors
    public Array<Flavor> getAllFlavors(){
        Array<Flavor> flavorSupply = new Array<Flavor>();

        if (supplyMap.containsKey(4)) {
            for (IntMap.Entry e : supplyMap){
                Supply s = (Supply)e.value;
                if (s.getSupplyType() >= 4){
                    flavorSupply.add(new Flavor(s));
                }
            }
        }
        
        return flavorSupply;
    }
    
    /** returns a supply, base or flavor
     *
     * @param key : the key of supply
     * @return null (should never return this)
     */
    public Supply getSupplyByType(int key){
        if (supplyMap.containsKey(key)){
            return supplyMap.get(key);
        }
        
        return new Supply(0, key); //returns an empty supply
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
    
    /** adds a supply & saves it if not in map
     *  updates supply if in the map
     *
     * @param key : current supply key
     * @param supply : current supply
     */
    public void saveSupply(int key, Supply supply){

        if (saveManager.supplyData.containsKey(key)){
            updateSupply(key, supply);

        }
        else{
            saveNewSupply(key, supply);
        }
        
        //update the supply label in table on the screen
//        if (key < 4)
//            CalcTable.instance.updateSupply(key, supply.getTotalAmount());
    }

    /** saves & returns a new supply as supplyData object
     * - also updates supplyscreen window
     * @param key
     * @param supply
     */
    public void saveNewSupply(int key, Supply supply){
        
        supplyMap.put(key, supply);
        saveManager.saveSupplyData(key, supply);
        SupplyScreen.instance.addToSupplyTable(supply);
    }


    /** updates an already saved supply
     * - also updates the supplyscreen table
     * @param key
     * @param supply
     * @return
     */
    public Object updateSupply(int key, Supply supply){
        //save into supply utils map
        supplyMap.remove(key);
        supplyMap.put(key, supply);

        //save into save data
        saveManager.updateSupplyData(key, supply);
        
        SupplyScreen.instance.updateSupplyTable(key, supply);
        return supply;
    }

    
    public void removeSupply(int key){
        saveManager.deleteSupply(key);
    }
    
    
    public void setSupplyAmounts(){
        supplyAmounts = new IntMap<Double>();

        for (IntMap.Entry entry: supplyMap.entries()){
            Supply s = (Supply)entry.value;
            supplyAmounts.put(s.getSupplyType(), s.getTotalAmount());
        }
    }
    

    public IntMap<Supply> emptySupplyMap(){
        IntMap<Supply>  data = new IntMap<Supply> ();
        
        for (int i = 0; i < 3; i++){
            Supply supplyData = new Supply(0, i);
            data.put(i, supplyData);
        }
        
        data.put(3, new Supply(Constants.EMPTY_BASE));
        
        return data;
    }
    
    
    //TODO: fix this so that empty map not returned, but requested
    public IntMap<Supply> getSupplyMap(){
        this.supplyMap = saveManager.getSupplyData();
        
        if (supplyMap.size == 0)
            return emptyMap;
        
        return supplyMap;
    }
    
    public IntMap<Double> getSupplyAmounts(){
        return supplyAmounts;
    }


    private void log(String message){
        Gdx.app.log("SupplyUtils LOG: ", message);
    }
}
