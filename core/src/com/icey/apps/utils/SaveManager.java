package com.icey.apps.utils;

import com.badlogic.gdx.Gdx;
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

    private Save save;

    //user preferences store basic info (name, email, etc)
    //also stores the 'app state' - boolean value for ads disabled/supplies enabled
    private Preferences userPrefs;

    //local since internal files are read only & want to write to this jsonEnable file
    private FileHandle saveFile;

    //the supply key String modifier
    private final String SUPPLY_KEY = "Supply_"; //modifier for supply key in object map
    public IntMap<Supply> supplyData; //supply data in map form
    

    public SaveManager(boolean encoded, String saveFileName, String prefsName){
        this.encoded = encoded;
        this.saveFile = Gdx.files.local(saveFileName);

        this.save = getSave();
        this.userPrefs = Gdx.app.getPreferences(prefsName);

        //set supply map, empty if it does not exist
        setSupplyData();
    }


    protected Save getSave() {
        Save save = new Save();

        if (saveFile.exists()) {
            Json json = new Json();

            if (encoded)
                save = json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString()));
            else
                save = json.fromJson(Save.class, saveFile.readString());
        }

        if (save == null)
            return new Save();

        return save;
    }


    //saves to a jsonEnable file
    public void saveToJson(){
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);


        if (encoded)
            saveFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else
            saveFile.writeString(json.prettyPrint(save), false);

        log(json.fromJson(Save.class, Base64Coder.decodeString(saveFile.readString())).toString());
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
     * @param key : the name of the recipe
     */
    public void saveRecipeData(String key){
        SaveManager.RecipeData recipeData = new SaveManager.RecipeData();
        recipeData.recipeName = key;

        recipeData.base = CalcUtils.getCalcUtil().base;

        recipeData.amountDesired = CalcUtils.getCalcUtil().amountDesired;
        recipeData.strengthDesired = CalcUtils.getCalcUtil().strengthDesired;
        recipeData.desiredPercents = CalcUtils.getCalcUtil().desiredPercents;

        recipeData.flavors = CalcUtils.getCalcUtil().flavors;

        recipeData.finalMills = CalcUtils.getCalcUtil().finalMills;


        save.data.put(key, recipeData);

        saveToJson();
    }


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

        save.data.put(SUPPLY_KEY + key, supply);

        saveToJson();
        log("saved supply data");
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


    /** turns ads off (or on)
     *
     * @param value : false if ads disabled
     */
    public void saveAdState(boolean value){
        userPrefs.putBoolean("adsEnabled", value);

        userPrefs.flush();
    }



    /** saves supply feature - turns it on (or off)
     *
     * @param value : false means supply enabled
     */
    public void saveSupplyState(boolean value){
        userPrefs.putBoolean("supplyDisabled", value);

        userPrefs.flush();
    }


    /** save drops per milliter setting
     *
     * @param value :
     */
    public void saveDropsPerML(float value){
        userPrefs.putFloat("dropsPerML", value);

        userPrefs.flush();
    }


    /** initializes app features & settings
     *
     * @param adsDisabled : the default value is true (ads are enabled)
     * @param supplyDisabled : the default value is true (supply feature not enabled)
     * @param dropsPerML : the default drops per ml
     */
    public void initSettings(boolean adsDisabled, boolean supplyDisabled, float dropsPerML){
        //sets default states
        userPrefs.putBoolean("adsEnabled", adsDisabled); //default is true
        userPrefs.putBoolean("supplyDisabled", supplyDisabled); //default is true
        userPrefs.putFloat("dropsPerML", 20);

        userPrefs.flush(); //need to call this after updating Preferences
    }


//    //saves the user defaults
//    public void saveUserDefaults(UserDefaultData defaults){
//        save.data.put("Defaults", defaults);
//
//        saveToJson();
//    }



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


    public ObjectMap<String, Object> getRecipeData(){
        return save.data;
    }


    public IntMap<Supply> getSupplyData(){
        if (supplyData == null)
            supplyData = new IntMap<Supply>();
        return supplyData;
    }


    public boolean getAdState(boolean defValue){

        return userPrefs.getBoolean("adsEnabled", defValue);
    }

    public boolean getSupplyState(boolean defValue){
        return userPrefs.getBoolean("supplyDisabled", defValue);
    }


    public double getDropsPerML(){
        return (double)userPrefs.getFloat("dropsPerML", 20); //default is 20
    }


    public Preferences getPrefs(){
        return userPrefs;
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

    //----User defaults---these can be altered by user in settings
    public static class UserDefaultData {

        //a value between 20 & 40
        // scientific standard is 20 drops per milliter (~.02 ml per drop)
        //  though this value can vary based on measuring device & liquid density
        public static double dropsPerMl; //default is about 20 drops per ml

        //default base strength & percents - user can change in settings
        public static double defaultDesiredAmt;
        public static Array<Integer> defaultDesiredPercents;
        public static double desiredStr; //desired strength (medium)

        //base defaults - user will be able to change in settings
        public static Base defaultBase ;

        //flavor default - the go-to flavor for user, can change in settings
        public static double defaultFlavPercent; //amount of the flavor supply
        public static String defaultFlavorName = "Flavor1"; //name of flavor
        public static Flavor defaultFlavor ;
    }
}