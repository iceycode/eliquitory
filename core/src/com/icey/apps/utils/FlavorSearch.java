package com.icey.apps.utils;

import com.badlogic.gdx.utils.Array;
import com.icey.apps.data.Flavor;

/** An algorithm for searching flavors in a large database
 * 
 * NOTE: this is not currently implemented 
 *  TODO: find the best database this can be used on, add
 *
 * Created by Allen on 2/12/15.
 */
public class FlavorSearch {

    //the array, which acts as a
    private static Array<Flavor> flavorStack = SupplyUtils.getSupplyUtils().getAllFlavors();
    
    public static Flavor findFlavors(String name){
        
        
        Flavor flavor = flavorStack.pop();
        
        
        
        return flavor;
    }
    
}
