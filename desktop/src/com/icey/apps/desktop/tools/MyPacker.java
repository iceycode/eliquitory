package com.icey.apps.desktop.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.util.Scanner;

/** for packing textures to be into one image in order to use with json for skins
 *
 * Created by Allen on 1/10/15.
 */
public class MyPacker {
    private static String choiceMessage = "Choose which textures to pack: \n 1: Calculator textures \n 2: Main Menu textures " +
            "\n 3: Supply Menu textures \n 4: Setting Menu textures \n 5: Recipe Menu textures " +
            "\n 6: Dark Theme textures \nENTER NUMBER HERE: ";
    private static String errorMsg = "Wrong value entered, try again";

    //input & output directories and packfile names
    private static String inputDir_calc = "textures/calculator";
    private static String outputDir_calc = "skins/calculator";
    private static String packFileName_calc = "skins/calculator/calcSkin";
    
    private static String inputDir = "textures/main_menu"; //input directory
    private static String outputDir = "skins/menu";     //output directory of atlas & packed directories
    private static String packFileName = "skins/menu/menuSkin"; //atlas file which will coincide with skin
    
    private static String inputDir_supplies = "textures/supplies";
    private static String outputDir_supplies = "skins/supplies";
    private static String packFileName_supplies = "skins/supplies/supplySkin";

    private static String inputDir_settings = "textures/settings";
    private static String outputDir_settings = "skins/settings";
    private static String packFileName_settings = "skins/settings/settingSkin";

    private static String inputDir_recipes = "textures/recipes";
    private static String outputDir_recipes = "skins/recipes";
    private static String packFileName_recipes = "skins/recipes/recipeSkin";

    private static String inputDir_darkTheme = "textures/theme-dark";
    private static String outputDir_darkTheme = "skins/dark";
    private static String packFileName_darkTheme = "skins/dark/darkSkin";


    //texture packer
    private static TexturePacker.Settings settings;


    public static void main(String[] args) throws Exception{
        settings = new TexturePacker.Settings();
        settings.filterMin = Texture.TextureFilter.Linear;

        getUserInput(choiceMessage);
    }
    
    //get user input
    protected static void getUserInput(String message){
        System.out.print(message);
        Scanner reader = new Scanner(System.in);
        int num = reader.nextInt();
        
        packTextures(num);
    }

    //packs the textures in input directory to designated file in output directory
    private static void packTextures(int choice){

        switch (choice){
            case 1:
                TexturePacker.process(settings, inputDir_calc, outputDir_calc, packFileName_calc);
                break;
            case 2:
                TexturePacker.process(settings, inputDir, outputDir, packFileName);
                break;
            case 3:
                TexturePacker.process(settings, inputDir_supplies, outputDir_supplies, packFileName_supplies);
                break;
            case 4:
                TexturePacker.process(settings, inputDir_settings, outputDir_settings, packFileName_settings);
                break;
            case 5:
                TexturePacker.process(settings, inputDir_recipes, outputDir_recipes, packFileName_recipes);
                break;
            case 6:
                TexturePacker.process(settings, inputDir_darkTheme, outputDir_darkTheme, packFileName_darkTheme);
                break;
            default:
                getUserInput(errorMsg); //error, try again
                break;
        }

        packAgain();
    }

    protected static void packAgain(){
        System.out.println("Press Q to quit or another key, then enter, to pack more textures: ");
        Scanner reader = new Scanner(System.in);

        if (reader.next().equals("q") || reader.next().equals("Q"))
            System.exit(0);
        else
            getUserInput(choiceMessage);
    }
}
