package com.icey.apps;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.icey.apps.assets.Assets;
import com.icey.apps.assets.Constants;
import com.icey.apps.screens.CalculatorScreen;
import com.icey.apps.screens.MenuScreen;
import com.icey.apps.screens.SupplyScreen;
import com.icey.apps.utils.SaveManager;

/** Main application class
 * - in render method, shows & renders the screens
 * - also responsible to taking "back" events (keyboard back, android back button)
 * - since everything is on a stage, will be using stage viewport/camera
 *
 * Notes on Device Type & settings:
 *  - Android: 
 *     * android:screenOrientation= "sensorLandscape" <-- in MainActivity
 *     * android:screenOrientation="portrait" <--- might use this instead
 *
 * NOTE: DO NOT initalize anything, other then primitives, before create() method
 *
 * * * * * * * * TODOS * * * * * * * * *
 * TODO: add recipe screen (maybe?)
 *
 *
 * Created by Allen on 01/06.
 */
public class MainApp implements ApplicationListener{

    Screen screen; //the current screen being shown
    Array<Screen> screens; //all the screens (0=menu, 1 =supply, 2 = calc)
    boolean screensLoaded = false; //whether screens are loaded or not
    
    //(boolean - encoded, boolean json
    public static SaveManager saveManager;

    //the apps state & previous state
    // -1: causes exit; 0: menu, 1: calc, 2: supplies (more to come)
    public static int state;
    public static int prevState; //the previous state

    private static boolean screenSet = false; //value determines whether screen is set
    //this is for mobile devices - screen size is set based on device & does not change
    boolean screenSizeSet = false;
    
    ApplicationType appType;
    
    //main fonts used throughout all screens
    Array<BitmapFont> bitmapFonts;

    //scale for X/Y in order to adjust size/position of stage actors
    public static float scaleX = 1;
    public static float scaleY = 1;

    public static float appWidth;
    public static float appHeight;

    private FileHandle saveFile; //save file location


	@Override
	public void create () {
        Assets.loadAssets(); //assets are loaded
        setState(-1); //initial state set

        initAppSettings(); //settings (dimensions, save files, etc) based on device
        initSaveData(false); //initializes save data (True = encoded)
    }


    //get device type to set platform-dependent app settings
    protected void initAppSettings(){
        appType = Gdx.app.getType();

        if (appType == ApplicationType.Android || appType == ApplicationType.iOS){
            //sets display to device width, height & fullscreen (T/F)
            Gdx.graphics.setDisplayMode(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), true);

            appWidth = Gdx.graphics.getWidth();
            appHeight = Gdx.graphics.getHeight();

            scaleX = Gdx.graphics.getWidth()/ Constants.SCREEN_WIDTH;
            scaleY = Gdx.graphics.getHeight()/Constants.SCREEN_HEIGHT;
        }

        log("App Size: " + Gdx.graphics.getWidth() + " x " + Gdx.graphics.getHeight());
    }

    protected void initSaveData(boolean encoded){
        if (encoded)
            saveFile = Gdx.files.local(Constants.SAVE_FILE_ENCODED);
        else
            saveFile = Gdx.files.local(Constants.SAVE_FILE);

        saveManager = new SaveManager(false, saveFile);
    }


    protected void initScreens(){
        screens = new Array<Screen>(3);
        
        //add all the screens to an Array for switching
        screens.add(new MenuScreen());
        screens.add(new CalculatorScreen());
        screens.add(new SupplyScreen());

        scaleFonts();
        setState(0);
        screensLoaded = true;
    }


    //scaling factor for the Density Independent Pixel unit
    // scales fonts to appropriate proportions (see the javadoc)
    protected void scaleFonts(){
        bitmapFonts = Assets.AssetHelper.getAllFonts();

        for (BitmapFont font : bitmapFonts){
            font.setScale(.9f, .9f); //make font a bit smaller for all

            if (appType!=ApplicationType.Desktop){
                font.setScale(Gdx.graphics.getDensity());
            }

        }
    }


	@Override
	public void render () {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND); //enables color blending

        //for catching the back button/escape button
        Gdx.input.setCatchBackKey(true); //enables android back key usage
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            updateState(); //updates state if back hit
        }

        if(Assets.manager.update()) {
            if (!screensLoaded){
                initScreens();
            }
            else{
                showScreen();
            }
        }
	}


    //updates state if back button or escape hit
    protected void updateState(){
        if ((state == 1 && prevState == 2) || (state == 2 && prevState == 1))
            setState(0); //to prevent user being stuck on calc & supply screens
        else if (state > 0)
            setState(prevState);
        else
            exitApp();
    }


    //shows the screens
    protected void showScreen(){
        //set the screen if it has not been set recently or at all
        if (!screenSet && state >= 0){
//            if (state == 0) setScreen(menuScreen);
//            else if (state == 1) setScreen(new CalculatorScreen());
//            else setScreen(new SupplyScreen());
            
            setScreen(screens.get(state));
            screenSet = true;
        }

        //renders the current screen
        if (this.screen != null) {
            this.screen.show();
        }
    }


    //TODO: set up a splash screen
    protected void showSplashScreen(){

    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void resize(int width, int height) {
        //Set the viewport to the whole screen.
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (this.screen!=null) screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        log("Resize - new dimensions: " + width +  " x " + height);
    }

    /** sets the screen that will be rendered & hides previous ones
     * 
     * @param screen
     */
    protected void setScreen(Screen screen){

        if (this.screen != null) this.screen.hide(); //previous screen hidden
        
        this.screen = screen; //set the screen

        screenSet = true;
    }
    
    //the new state
    public static void setState(int newState){
        prevState = state; //previous state set to current one

        state = newState; //set to the new state
        screenSet = false; //set to false, so switch screen can occur
    }


    //exit app methods of disposal
    //TODO: Set so that Home button disposes completely, back button pauses or something like that
    protected void exitApp(){

        for (Screen s : screens){
            s.dispose();
        }

        //on android, will cause dispose/pause in near future (see doc)
        Gdx.app.exit();
        //dispose();
    }


    private void log(String message){
        Gdx.app.log("MainApp log: ", message);
    }
}
