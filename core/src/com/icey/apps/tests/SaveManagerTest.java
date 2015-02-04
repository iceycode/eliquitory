package com.icey.apps.tests;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.icey.apps.data.SaveData;
import com.icey.apps.utils.SaveManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
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
 * - All Tests pass! (02/03/15)
 *
 */
public class SaveManagerTest {

    //save manager objects to test
    private SaveManager saveManager; //the saveManager object which will be tested

    //data objects which will be tested
    ObjectMap<String, Object> recipeMap;
    IntMap<Object> supplyMap;


    //sets up a temporary folder to set file to, automatically gets deleted after
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Before
    public void setData1() throws IOException{

        //mock data for ObjectMap & IntMap
        recipeMap = mock(ObjectMap.class);
        supplyMap = mock(IntMap.class);
        for (int i = 0; i < 3; i++){
            SaveData.RecipeData recipeData = mock(SaveData.RecipeData.class);
            SaveData.SupplyData supplyData = mock(SaveData.SupplyData.class);

            recipeMap.put("Recipe" + Integer.toString(i), recipeData);
            supplyMap.put(i, supplyData);
        }
    }

    @Test
    public void testJsonSave_Encoded() throws Exception{
        File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(new FileHandle(saveFile)); //the save manager being testd
        saveManager.setSave(new SaveData.Save());

        saveManager.setEncoded(true); //encoded = true

        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveData.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (SaveData.SupplyData)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }

    }

    @Test
    public void testJsonSave_NotEncoded() throws Exception{
        File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(new FileHandle(saveFile)); //the save manager being testd

        saveManager.setEncoded(false); //encoded = true
        saveManager.setSave(new SaveData.Save());


        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveData.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (SaveData.SupplyData)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }
    }

    @Test
    public void testSaveGson_Encoded() throws Exception {
        File saveFile = tempFolder.newFile("testSave.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(new FileHandle(saveFile)); //the save manager being testd

        saveManager.setSave(new SaveData.Save());

        saveManager.setEncoded(true); //encoded = true
        saveManager.json = false;

        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveData.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (SaveData.SupplyData)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }
    }


    @Test
    public void testGson_NotEncoded() throws Exception {
        File saveFile = tempFolder.newFile("testSave3.json"); //create temp file in temp folder
        this.saveManager = new SaveManager(new FileHandle(saveFile)); //the save manager being testd

        saveManager.setEncoded(false); //encoded = true
        saveManager.json = false;

        saveManager.setSave(new SaveData.Save()); //create new save file

        for (int i = 0; i < 3; i++){
            saveManager.saveRecipeData("Recipe" + Integer.toString(i), (SaveData.RecipeData) recipeMap.get("Recipe1"));
            saveManager.saveSupplyData(i, (SaveData.SupplyData)supplyMap.get(i));

            assertNotNull(saveManager.getRecipeData());
            assertNotNull(saveManager.getSupplyData());

            assertSame(recipeMap.get("Recipe"+Integer.toString(i)), saveManager.loadRecipeData("Recipe"+ Integer.toString(i)));
            assertSame(supplyMap.get(i), saveManager.loadSupplyData(i));
        }
    }

}