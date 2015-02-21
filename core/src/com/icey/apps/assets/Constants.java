package com.icey.apps.assets;

import com.badlogic.gdx.utils.Array;
import com.icey.apps.data.Base;
import com.icey.apps.data.Flavor;

/** Constants class
 * - contains fields used throughout the
 *
 *
 * Created by Allen on 1/11/15.
 */
public class Constants {

    //--------global constants for app (used anywhere/everywhere)--------
    //default screen width & height
    public static final float SCREEN_HEIGHT = 800f;
    public static final float SCREEN_WIDTH = 480f;

    //default values for advertisements
    public static final float AD_HEIGHT = 50;

    public static String SAVE_FILE = "save.json" ;
    public static String SAVE_FILE_2 = "assets/save/save.json";
    public static String SAVE_FILE_ENCODED = "assets/save/save_encoded.json";

    public static String FONT_REG_14 = "fonts/opensans_bold_reg.fnt"; //size 14 font
    public static String FONT_MED_16 = "fonts/opensans_bold_med.fnt"; //size 16 font
    public static String FONT_LIGHT_16 = "fonts/opensans_light_med.fnt"; //light font, size 16

    //Skin JSON file locations
    public static final String MENU_SKIN = "skins/menu/menuSkin.json"; //skin json file for menu
    public static final String SUPPLY_MENU_SKIN = "skins/supplies/supplySkin.json";
    public static final String CALC_SKIN = "skins/calculator/calcSkin.json"; //calc skin
    public static final String SETTING_SKIN = "skins/settings/settingSkin.json";


    //names of settings
    public static final String SETTINGS_Desktop = "settings1";
    public static final String SETTINGS_Android = "settings2";
    public static final String SETTINGS_Web = "settings3";
    public static final String SETTINGS_iOS = "settings4";


    //=====================Menu Constants=====================-
    public static final float[] MENU_TITLE_POS = {Constants.SCREEN_WIDTH/2 - 350/2, 400f};
    public static final float[] MENU_TITLE_SIZE = {375, 150};
    public static final float[] MENU_BTN_SIZE = {225, 100};



    //=====================Supply Constants=====================
    public static final String SUPPLY_TABLE_BACK = "textures/supplies/supplyTableBack.png"; //background of table containing current supplies

    public static final String[] PERC_FIELD_NAMES = {"PG %: ", "VG %: ", "Other %: "}; //for percent textfield for base
    public static final String[] SUPPLY_CHECKBOX_TITLES = {"PG ", "VG ", "Other "};

    public static final String DELETE_CHECK = "Are you sure you want to delete?";

    public static final String ERROR_SUPPLY = "Error saving liquid supply.";
    public static final String ERROR_BASE = "Error saving nicotine base.";
    public static final String ERROR_FLAVOR = "Error saving flavor.";
    
    
    public static final String[] SUPPLY_NAMES = {"Propylene Glycol", "Vegetable Glycerin",
            "Other: ", "Nicotine Base", "Flavor: "};

    public static final String[] OTHER_NAMES = {"Alcohol", "Water", "Other"};


    //=====================Calculator constants =====================
    public static final String GOAL_TITLE = " Desired Amount";
    public static final String BASE_TITLE = "Nicotine Base";
    public static final String FLAVORS_TITLE = "Flavor(s)";
    
    public static final String FINAL_CALCS_TITLE = "mL (drops)";
    public static final String SUPPLY_AMTS_TITLE = "Supply";
    public static final String[] AMOUNTS_TITLES = {"Amt (mL): ", "Str (mg): "}; //0=liq ml; 1 = str nicotine

    public static final String[] TYPE_NAMES = {"PG %: ", "VG %: ", "Other %: "};
    public static final int[] GOAL_PERCENT_TYPES = {0, 1, 2, 5};
    public static final int[] BASE_PERCENT_TYPES = {3, 4, 6};

//    public static final String[] BASE_PERC_LABELS = {"Base PG %:", "Base VG %:", "Base Other %:"};
//    public static final String[] SLIDER_NAMES = {"desired slider", "base slider"};
//    //public static final String[] FLAVOR_PERC_LABELS = {"Flavor %: ", "Flavor PG %:", "Flavor VG %:", "Flavor EtOH/H2O/etc %:"};
//    public static final String FLAV_FIELD_NAME = "flavorField_";
    public static final String NEW_FLAVOR_STRING = "New Flavor: ";
    public static Flavor NEW_FLAVOR = new Flavor("New Flavor");

    public static final String[] CALCLABEL_TEXT = {"PG: ", "VG: ", "Other: ", "Base: "};

    //error messages for when calulating
    public static final String ERROR_MAIN = "Error";
    public static final String[] ERROR_MSGS = {"A flavor type or percent not set", "Desired percents not at 100%", 
            "Base percents not at 100%", "Desired amount not set"};
    
    /**
     * 0-3 desired PG-->base vg; 4 flavor percent; 5-7 desired amount, desired strength, base strength
     */
    public static final String[] ERROR_DETAILS = {"DESIRED PG", "DESIRED VG",
            "BASE PG", "BASE VG %", "FLAVOR : ", "DESIRED AMOUNT"
            , "DESIRED STRENGTH", "BASE STRENGTH "};
    
    public static final int[] AMOUNT_LISTENER_TYPE = {0, 1}; //0=ml; 1 = mg (strength)

    //width & height of the text fields (percents)
    public static final float TEXT_FIELD_WIDTH = 50; //previously 150
    public static final float TEXT_FIELD_HEIGHT = 25f;

    public static final float CHECKBOX_WIDTH = 80;
    public static final float CHECKBOX_HEIGHT = 25;

    public static final float TITLE_HEIGHT = 50f;
    public static final float TITLE_WIDTH = 200f;
    public static final float CALC_BTN_WIDTH = 200f;
    public static final float ADD_FLAVOR_BTN_WITH = 200f;




    //----these are empty initial values---
    //default base strength & percents - user can change in settings
    public static final double ZERO_FINAL_AMOUNT = 0.0;
    public static final Array<Integer> ZERO_DESIRED_PERCENTS = new Array<Integer>(new Integer[]{0, 0, 0});
    public static final double ZERO_STRENGTH = 0.0; //desired strength (medium)
    
    //base defaults - user will be able to change in settings
    public static final double EMPTY_BASE_STR = 100.0;
    public static final Array<Integer> ZERO_BASE_PERCENTS = new Array<Integer>(new Integer[]{0, 0});
    public static final Base EMPTY_BASE = new Base(0, EMPTY_BASE_STR, ZERO_BASE_PERCENTS);

    //flavor default - the go-to flavor for user, can change in settings
    public static final  double DEFAULT_FLAV_AMT = 0; //amount of the flavor supply
    public static final String DEFAULT_FLAV_NAME = "Flavor1"; //name of flavor
    public static final Flavor EMPTY_FLAVOR = new Flavor(0, "Flavor1");

    public static Array<Double> INITLAL_FINAL_MLS = new Array<Double>(new Double[]{0.0, 0.0, 0.0, 0.0});

    //set to 20 as default value, but user can change this
    public static double DROPS_PER_ML = 20;


    /** constants for tests
     *
     */
    public static class Tests {
        //=============Constants for CalcUtilTests (6 expected final amounts)============
        public static double DESIRED_AMT = 100.0;
        public static double DESIRED_STR = 10.0;
        public static Array<Integer> DESIRED_PERCS = new Array<Integer>(new Integer[]{30, 70, 0});
        public static Array<Integer> ALT_DESIRED_PERCS = new Array<Integer>(new Integer[]{30, 70, 10});

        public static Flavor FLAV1 = new Flavor("Flavor1", 10, 0);
        public static Flavor FLAV2 = new Flavor("Flavor2", 5, 1);
        public static Flavor FLAV3 = new Flavor("Flavor3", 5, 2);
        public static Array<Flavor> TEST_FLAVORS = new Array<Flavor>(new Flavor[]{FLAV1, FLAV2, FLAV3});

        public static Array<Integer> BASE_PERCS = new Array<Integer>(new Integer[]{50, 50});
        public static double BASE_STR = 100;
        public static Base TEST_BASE = new Base(BASE_STR, BASE_PERCS);


        /** 6 Total Final Amounts to Tests for ----based on 20 drops/ml
         * expect0 = PG/VG, 1 Flavor(PG); expect1 = PG/VG + 2 Flavors (PG, VG); expect2= PG/VG + 3 Flavors;
         *  expect3= PG/VG/Other + 1 Flavor(PG); expect4 = PG/VG/Other + 2 Flavors (PG, Other)
         *     expect5 = PG/VG/Other + 3 Flavors (PG, VG, Other)
         */
        public final static Double[][] EXPECTED_FAs = {{15.0, 65.0, 0.0, 10.0, 10.0},{15.0, 60.0, 0.0, 10.0, 10.0, 5.0},
                {13.5, 56.5, 0.0, 10.0, 10.0, 5.0, 5.0}, {12.0, 58.0, 10.0, 10.0, 10.0},
                {10.5, 54.5, 10.0, 10.0, 10.0, 5.0},{10.5, 49.5, 10.0, 10.0, 10.0, 5.0, 5.0}};

    }


    /** TODO: figure out how to (or if to) implement this class
     *  User defaults
     *  - will be able to be altered using the options menu
     */
    public static class UserDefaults{



        //default base strength & percents - user can change in settings
        public static double DEFAULT_FINAL_AMT = 30.0;
        public static Array<Integer> DEFAULT_DESIRED_PERCENTS = new Array<Integer>(new Integer[]{70, 30, 0});
        public static double DESIRED_STR = 16.0; //desired strength (medium)

        //base defaults - user will be able to change in settings
        public static double DEFAULT_BASE_STR = 100.0;
        public static Array<Integer> DEFAULT_BASE_PERCENTS = new Array<Integer>(new Integer[]{0, 0});
        public static Base DEFAULT_BASE = new Base(0, DEFAULT_BASE_STR, DEFAULT_BASE_PERCENTS);

        //flavor default - the go-to flavor for user, can change in settings
        public static double DEFAULT_FLAV_AMT = 0; //amount of the flavor supply
        public static String DEFAULT_FLAV_NAME = "Flavor1"; //name of flavor
        public static Flavor FLAVOR_DEFAULT = new Flavor(0, DEFAULT_FLAV_NAME);

        public static Array<Double> INITLAL_FINAL_MLS = new Array<Double>(new Double[]{0.0, 0.0, 0.0, 0.0, 0.0});
        
    }
    
}
