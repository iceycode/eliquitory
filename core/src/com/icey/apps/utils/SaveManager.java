package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.*;
import com.google.gson.Gson;
import com.icey.apps.data.SaveData;
import com.icey.apps.data.SaveData.RecipeData;
import com.icey.apps.data.SaveData.Save;
import com.icey.apps.data.SaveData.SupplyData;
import com.icey.apps.data.Supply;


//incorrect String - causes serialization trace
//since CalcData changed to CalcUtils & RecipeData nested class in CalcData
/** Manages save data for recipes & supplies
 * 
 * A lot of this is taken from:
 * http://www.toxsickproductions.com/libgdx/libgdx-intermediate-saving-and-loading-recipeData-from-files/
 *  - tweaked out a bit to fit my own needs
 * - saves into class Save which is encoded into json file
 *  - two ObjectMaps - recipe save & supply save 
 *  TODO: setup FB account, google plus & email account options to store in Save or Preferences
 *
 * Created by Allen on 1/11/15.
 */
public class SaveManager {
    
    private static SaveManager instance;
    
    private boolean encoded = true; //default is true
    private Save save;

    private Preferences userPrefs; //user preferences store basic info (name, email, etc)

    //local since internal files are read only & want to write to this json file
    private FileHandle saveFile;

    //for testing purposes
    public boolean json = false; //the type of serializer user - Json vs Gson


    public SaveManager(FileHandle saveFile){
        this.saveFile = saveFile;

        try{
            this.save = getSave();
        }
        catch(NullPointerException e){
            this.save = new Save();
        }

    }

    public SaveManager(boolean encoded, boolean json, FileHandle saveFile){
        this.encoded = encoded;
        this.json = json;

        this.saveFile = saveFile;

        try{
            this.save = getSave();
        }
        catch(NullPointerException e){
            this.save = new Save();
            log("No savefile! Created new Save class" + e.toString());
        }
    }

    public Save getSave() throws NullPointerException{
        Save save = new Save();

        if(saveFile.exists()){
            Json json = new Json();
            
            try{
                if(encoded) save = json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString()));
                else save = json.fromJson(Save.class, saveFile.readString());
            }
            catch(SerializationException se){
                log("problem with json serialization...class probably changed/moved");
            }

        }

        return save;
    }

    public Save getSave_Gson() throws NullPointerException{
        Save save = new Save();
        saveFile = Gdx.files.local("save.json");

        if(saveFile.exists()){
            Gson gson = new Gson();
            if(encoded) save = gson.fromJson(Base64Coder.decodeString(saveFile.readString()), Save.class);
            else save = gson.fromJson(saveFile.readString(), Save.class);
        }

        if(save == null)
            return new Save();

        return save;
    }


    
    //saves to a json file
    public void saveToJson(){
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);


        if (encoded) saveFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else saveFile.writeString(json.prettyPrint(save), false);

        log("saved RecipeData into json file as ObjectMap value");
    }


    //saves to a Gson file
    private void saveToGson(){
        Gson gson = new Gson();
        if (encoded) saveFile.writeString(Base64Coder.encodeString(gson.toJson(save)), false);
        else saveFile.writeString(Base64Coder.encodeString(gson.toJson(save)), false);
        log("saved using Gson serializer without encoding");
    }

    /** Loads the data from Save class
     *
     * @param key
     * @return
     */
    public Object loadRecipeData(String key) {
        log("Key = " + key);

        if (save.recipeData.containsKey(key))
            return save.recipeData.get(key);
        else
            return null;   //this if() avoids an exception, but check for null on load.
    }

    /** gets the recipe data into the CalcUtil class
     *
     * @param name
     */
    public void getRecipeData(String name, CalcUtils calcUtils){

        try {
            SaveData.RecipeData data = (SaveData.RecipeData) loadRecipeData(name);
            calcUtils.setRecipeName(data.recipeName);
            calcUtils.setAmountDesired(data.amountDesired);
            calcUtils.setStrengthDesired(data.strengthDesired);
            calcUtils.setDesiredPercents(data.desiredPercents);

            //Base
            calcUtils.setBase(data.base);

            //base strengths
            calcUtils.setBaseStrength(data.strengthNic);
            calcUtils.setBasePercents(data.basePercents);

            //flavors
            calcUtils.setFlavors(data.flavors);

            calcUtils.setFinalMills(data.finalMills); //final amounts

            calcUtils.loaded = true; //calcUtils now loaded with values
        }
        catch (NullPointerException e){
            log("All values from SaveData not there!" + e.toString());
        }
    }

    
    @SuppressWarnings("unchecked")
    public Object loadSupplyData(int key){
        log("Key = " + Integer.toString(key));

        if (save.supplyData.containsKey(key)){
            return save.supplyData.get(key);
        }
        else
            return null;
    }


    //------------Methods for saving supplies-------------

    //saves using Json as serializer
    public void saveRecipeData(String key, RecipeData data){
        save.recipeData.put(key, data);
        saveToJson(); //SaveData current save immediatly.
    }


    //saves using Gson as serializer
    public void saveRecipeData_Gson(String key, RecipeData data){
        save.recipeData.put(key, data);
        saveToGson();
    }

    public void renameSavedRecipe(String newName, String oldName){
        Object data = save.recipeData.get(oldName);
        save.recipeData.remove(oldName);
        save.recipeData.put(newName, data);
        
        saveToJson();
    }
    
    public void deleteRecipe(String name){
        save.recipeData.remove(name);
    }
    
    
    
    //------------Methods for saving supplies-------------
    public void saveSupplyData(int key, SupplyData supply){
        save.supplyData.put(key, supply);
        saveToJson();

        log("save supplydata is null? " + save.supplyData);
    }

    public void updateSupplyData(int key, SupplyData supplyData){
        save.supplyData.remove(key);
        save.supplyData.put(key, supplyData);
        saveToJson();

        log("updated supplyData");
    }



    public void saveAllSupplies(IntMap<Object> supplies){
        save.supplyData = supplies;
    }
    
    
    public void deleteSupply(int key){
        save.supplyData.remove(key);
    }
    
    public void updateSupplyAmount(int type, double change){
        Supply supply = (Supply)save.supplyData.get(type);
        supply.updateTotalAmount(change);
        
        save.supplyData.remove(type); //remove old supply recipeData
        save.supplyData.put(type, supply); //

        saveToJson();
    }
    
    public ObjectMap<String, Object> getRecipeData(){
        return save.recipeData;
    }
    
    public IntMap<Object> getSupplyData(){
        return save.supplyData;
    }

    public void setEncoded(boolean encoded) {
        this.encoded = encoded;
    }

    public void setSave(Save save){
        this.save = save;
    }

    public void setSaveFile(FileHandle file){
        this.saveFile = file;
    }


    private static void log(String message){
        System.out.println("SaveManager LOG: " + message);
    }
}
