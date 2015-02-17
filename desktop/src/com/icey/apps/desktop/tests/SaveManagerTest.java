package com.icey.apps.desktop.tests;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.icey.apps.data.Supply;
import com.icey.apps.utils.SaveManager;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/** Tests class for SaveManager, for JSON writing/reading
 * - tests methods that involved writing ObjectMap & IntMap to JSON format
 * - tests both encoded & non-encoded methods of saving with Json & Gson serializers*
 *
 * - Created by Allen 02/03/15
 *
 * NOTES:
 * - All Tests pass! (02/03/15)
 *
 * - After updating save data structure, will NOT be using Gson (02/10/15)
 *   -  using Json serializer that comes with libgdx since it is sufficient for my needs
 */
public class SaveManagerTest {

    //save manager objects to test
    private SaveManager saveManager; //the saveManager object which will be tested

    //data objects which will be tested with mock values (null)
    ObjectMap<String, Object> recipeMap;
    IntMap<Supply> supplyMap;

    
    private final String[] SAVES = {"assets/saves/tests/save1.json", "assets/saves/tests/save_encoded1.json",
                    "assets/saves/tests/save2.json", "assets/saves/tests/save_encoded2.json"};

    //sets up a temporary folder to set file to, automatically gets deleted after
//    @Rule
//    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void setData() throws IOException{

        //mock data for ObjectMap & IntMap
        recipeMap = mock(ObjectMap.class);
//        supplyMap = mock(IntMap.class);
        for (int i = 0; i < 3; i++){
            SaveManager.RecipeData recipeData = mock(SaveManager.RecipeData.class);
            Supply supply = mock(Supply.class);

            recipeMap.put("Recipe" + Integer.toString(i), recipeData);
            supplyMap.put(i, supply);
        }
        
        
    }

    @Test
    public void testJsonSave_Encoded() throws Exception{
        //File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(true, new FileHandle(SAVES[2])); //the save manager being testd
        saveManager.setSave(new SaveManager.Save());

        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveManager.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (Supply)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }

    }

    @Test
    public void testJsonSave_NotEncoded() throws Exception{
        //File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(false, new FileHandle(SAVES[0])); //the save manager being testd
        saveManager.setSave(new SaveManager.Save());


        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveManager.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (Supply)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }
    }
//
//    @Test
//    public void testSaveGson_Encoded() throws Exception {
//        //File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
//        this.saveManager = new SaveManager(true, false, new FileHandle(SAVES[3])); //the save manager being testd
//
//        saveManager.setSave(new SaveManager.Save());
//
//        for (int i = 0; i < 3; i++){
//            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveManager.RecipeData) recipeMap.get("Recipe1"));
//            saveManager.saveSupplyData(i, (Supply)supplyMap.get(i));
//
//            assertNotNull(saveManager.getRecipeData());
//            assertNotNull(saveManager.getSupplyData());
//
//            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
//            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
//        }
//    }

//
//    @Test
//    public void testGson_NotEncoded() throws Exception {
//        //File saveFile = tempFolder.newFile("testSave3.json"); //create temp file in temp folder
//        this.saveManager = new SaveManager(false, true, new FileHandle(SAVES[1])); //the save manager being testd
//
//        saveManager.setSave(new SaveManager.Save()); //create new save file
//
//        for (int i = 0; i < 3; i++){
//            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveManager.RecipeData) recipeMap.get("Recipe1"));
//            saveManager.saveSupplyData(i, (Supply)supplyMap.get(i));
//
//            assertNotNull(saveManager.getRecipeData());
//            assertNotNull(saveManager.getSupplyData());
//
//            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
//            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
//        }
//    }
//
}