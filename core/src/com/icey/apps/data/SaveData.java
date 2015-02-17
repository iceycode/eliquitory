package com.icey.apps.data;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;

/** Save class & objects which get saved
 * - Save contains maps of objects which get saved
 * - SupplyData & RecipeData encapsulate saved user parameters
 *      - SupplyData - contains users supply properties
 *          ie, amount of PG, VG; name & amount of flavor, base strength, amount
 *      - RecipeData - contains users' previous recipe parameters
 *          ie, amount desired, strength, etc
 *
 * Created by Allen on 2/3/15.
 */
public class SaveData {

    // Save class which becomes serialized into JSON format
    public static class Save{
        public ObjectMap<String, Object> recipeData = new ObjectMap<String, Object>();
        public IntMap<Object> supplyData = new IntMap<Object>();
        public ObjectMap<String, Object> accountData = new ObjectMap<String, Object>();
    }


    /** Supply Data
     * - data regarding supply - can be 1 of 3 supplies
     */
    public static class SupplyData{
        public Supply supply; //the supply (liquid: PG, VG, other)
        public Base base; //the nicotine base
        public Flavor flavor; //the flavor
    }


    /** Recipe Data
     * - contains data relating to the recipe made
     */
    public static class RecipeData{
        public String recipeName;

        public Base base;
        public double amountDesired;
        public double strengthDesired;
        public Array<Integer> desiredPercents;

        public double strengthNic;
        public Array<Integer> basePercents;

        public Array<Flavor> flavors;

        public Array<Double> finalMills;
    }
}
