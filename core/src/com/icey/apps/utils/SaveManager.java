package com.icey.apps.utils;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;
import com.icey.apps.data.Supply;


//incorrect String - causes serialization trace
//since CalcData changed to CalcUtils & RecipeData nested class in CalcData
/** Manages save data for recipes & supplies
 * 
 * A lot of this is taken from:
 * http://www.toxsickproductions.com/libgdx/libgdx-intermediate-saving-and-loading-data-from-files/
 *  - tweaked out a bit to fit my own needs
 * - saves into class Save which is encoded into jsonEnable file
 *  - two ObjectMaps - recipe save & supply save 
 *  - Currently not*
 *  TODO: setup FB account, google plus & email account options to store in Save or Preferences
 *
    - html:compileGWT: something with FileHandle(String) - changing to Gdx.files.local() fixes
 * Created by Allen on 1/11/15.
 */
public class SaveManager {
    
//    private static SaveManager instance;
    
    private boolean encoded = true; //Bas64Encoder enabled by default
    private boolean jsonEnable = false; //the type of serializer used - Json vs Gson

    private Save save;

    private Preferences userPrefs; //user preferences store basic info (name, email, etc)

    //local since internal files are read only & want to write to this jsonEnable file
    private FileHandle saveFile;

    //the supply key String modifier
    private final String SUPPLY_KEY = "Supply_"; //modifier for supply key in object map
    public IntMap<Supply> supplyData; //supply data in map form
    

    public SaveManager(boolean encoded, FileHandle saveFile){
        this.encoded = encoded;
        this.saveFile = saveFile;

        this.save = getSave();

        //set supply map, empty if it does not exist
        if (save != null)
            setSupplyData();
    }


    public Save getSave() {
        Save save = new Save();

        try {
            if (saveFile.exists()) {
                Json json = new Json();

                if (encoded)
                    save = json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString()));
                else
                    save = json.fromJson(Save.class, saveFile.readString());
            }
        }
        catch(Throwable e){
            log("Could not find file. " + e.toString());
            save = new Save();
        }

        return save;
    }



    //saves to a jsonEnable file
    public void saveToJson(){
        if (this.jsonEnable){
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            
            if (encoded) 
                saveFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
            else 
                saveFile.writeString(json.prettyPrint(save), false);
            
        }
        
        log("saved with Json : " + jsonEnable + "/ Gson : " + !jsonEnable);
    }

    
    /** Loads the data from Save class
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object loadRecipeData(String key) {
        log("Key = " + key);

        if (save.data.containsKey(key))
            return save.data.get(key);
        else
            return null;   //this if() avoids an exception, but check for null on load.
    }

    
    @SuppressWarnings("unchecked")
    public Object loadSupplyData(int key){
        log("Key = " + Integer.toString(key));

        if (save.data.containsKey(SUPPLY_KEY + key)){
            return save.data.get(SUPPLY_KEY + key);
        }
 
        return null;
    }


    //==============Methods for saving/deleting/updating recipes==============

    /** saves using Json as serializer
     *
     * @param key
     * @param data
     */
    public void saveRecipeData(String key, RecipeData data){
        save.data.put(key, data);

        saveToJson();
    }


//
//    public void renameSavedRecipe(String newName, String oldName){
//        Object data = save.data.get(oldName);
//        save.data.remove(oldName);
//        save.data.put(newName, data);
//
//        if (jsonEnable) saveToJson();
//        else save_Gson();
//    }

    /** deletes recipe by name
     *
     * @param name : recipe name (aka key)
     */
    public void deleteRecipe(String name){
        save.data.remove(name);
    }


    /** saves the recipe with key
     *
     * @param key : key is int, appended to end of String SUPPLY_KEY
     * @param supply : the supply being saved
     */
    public void saveSupplyData(int key, Supply supply){
        
        if (this.supplyData.containsKey(key)){
            updateSupplyData(key, supply);
        }
        else{
            save.data.put(SUPPLY_KEY + key, supply);
        }

        saveToJson();
        log("saved supply data");
    }


    /** updates supply data based on key
     *
     * @param key : key in map
     * @param supply : supply being updated
     */
    public void updateSupplyData(int key, Supply supply){
        //remove from current supplydata map
        supplyData.remove(key);
        supplyData.put(key, supply);
        
        //remove from the save file
        save.data.remove(SUPPLY_KEY + key);
        save.data.put(SUPPLY_KEY + key, supply);

        saveToJson();
        
        log("updated supplyData");
    }

    /** deletes the supply
     *
     * @param key
     */
    public void deleteSupply(int key){
        this.supplyData.remove(key);
        
        save.data.remove(SUPPLY_KEY + key);

        saveToJson();
    }
    
    
    public ObjectMap<String, Object> getRecipeData(){
        return save.data;
    }
    
    
    public IntMap<Supply> getSupplyData(){
        return supplyData;
    }


    //sets the supply data
    public void setSupplyData(){
        this.supplyData = new IntMap<Supply>();

        for (String key : save.data.keys()){
            if (key.contains(SUPPLY_KEY)){
                int in = Integer.parseInt(key.substring(key.indexOf('_')+1));
                Supply supply = ((Supply)save.data.get(key));
                supplyData.put(in, supply);
            }
        }
        
    }


    //sets the save data - for testing purposes
    public void setSave(Save save){
        this.save = save;
    }


    private void log(String message){
        System.out.println("SaveManager LOG: " + message);
    }


    //==================Nested save classes for data==================
    // Save class which becomes serialized into JSON format
    public static class Save {
        public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
    }


    /** Recipe Data
     * - contains data relating to the recipe made
     */
    public static class RecipeData{
        public String recipeName;

        public double amountDesired;
        public double strengthDesired;
        public Array<Integer> desiredPercents;

        public Base base;
        public double strengthNic;
        public Array<Integer> basePercents;

        public Array<Flavor> flavors;

        public Array<Double> finalMills;
    }
}