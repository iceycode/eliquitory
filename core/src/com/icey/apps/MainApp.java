package com.icey.apps;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.icey.apps.assets.AssetHelper;
import com.icey.apps.assets.Assets;
import com.icey.apps.screens.CalculatorScreen;
import com.icey.apps.screens.MenuScreen;
import com.icey.apps.utils.Constants;
import com.icey.apps.utils.SaveManager;

/** Main application class
 * - in render method, shows & renders the screens
 * - also responsible to taking "back" events (keyboard back, android back button)
 * - since everything is on a stage, will be using stage viewport/camera
 *
 * - App states = {0: menu screen, 1: calc screen, 2: supplies screen, 3: settings screen}
 *
 * Notes on Device Type & settings:
 *  - Android: 
 *     * android:screenOrientation= "sensorLandscape" <-- in MainActivity
 *     * android:screenOrientation="portrait" <--- might use this instead
 *
 * NOTE: DO NOT initalize anything, other then primitives, before create() method
 *
 * App States:
 *  - App can have a 3 kinds of states dependent on following:
 *          adsEnabled; supplyEnabled
 *          [true; false]
 *          [false; false]
 *          [false; true]
 *
 * * * * * * * * TODOS * * * * * * * * *
 * TODO: add recipe screen (maybe?)
 *
 * Created by Allen on 01/06.
 */
public class MainApp implements ApplicationListener{

    Screen screen; //the current screen being shown
    Array<Screen> screens; //all the screens (0=menu, 1 =supply, 2 = calc)
    boolean screensLoaded = false; //whether screens are loaded or not
    
    //(boolean - encoded, boolean json
    public static SaveManager saveManager; //handles user save data & preferences
    private String settingsName;
    private String saveFileName; //save file location

    public static boolean supplyEnabled = false; //whether supply feature enabled
    public static boolean adsEnabled = true;

    //the apps state & previous state
    // -1: causes exit/pause; 0: menu, 1: calc, 2: supplies, 3: settings
    public static int state;
    public static int prevState; //the previous state

    private static boolean screenSet = false; //value determines whether screen is set
    ApplicationType appType;
    
    //main fonts used throughout all screens
    Array<BitmapFont> bitmapFonts;

    public static float appWidth;
    public static float appHeight;



	@Override
	public void create () {
        Assets.loadAssets(); //assets are loaded
        setState(-1); //initial state set

        initAppSettings(); //settings (dimensions, save files, etc) based on device

        initSaveManager(true); //initializes save manager (True = encoded)

    }


    /** get device & user app settings
     * - gets type of app by device to set platform-dependent graphics settings
     *
     *
     */
    protected void initAppSettings(){
        appType = Gdx.app.getType();

        if (appType == ApplicationType.Android || appType == ApplicationType.iOS){
            //sets display to device width, height & fullscreen (T/F)
            Gdx.graphics.setDisplayMode(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), true);

            appWidth = Gdx.graphics.getWidth();
            appHeight = Gdx.graphics.getHeight();

            scaleFonts();
        }

        log("App Size: " + Gdx.graphics.getWidth() + " x " + Gdx.graphics.getHeight());


    }

    /** initializes the SaveManager
     * - contains user save data for recipes and/or supplies
     * - sets app state (3 types): Ads, No Ads, Supplies/No supplies
     *
     * @param encoded : whether savedata is encoded or not
     */
    protected void initSaveManager(boolean encoded){
        //sets the preferences name based on ApplicationType enum
        if (appType == ApplicationType.Android)
            settingsName = Constants.SETTINGS_Android;
        else if (appType == ApplicationType.WebGL)
            settingsName = Constants.SETTINGS_Web;
        else if (appType == ApplicationType.iOS)
            settingsName = Constants.SETTINGS_iOS;
        else
            settingsName = Constants.SETTINGS_Desktop;


        if (encoded)
            saveFileName = Constants.SAVE_FILE_ENCODED;
        else
            saveFileName = Constants.SAVE_FILE;

        saveManager = new SaveManager(encoded, saveFileName, settingsName);

        //setAppFeatures();
    }


//    //returns values related to features enabled/disabled
//    protected void setAppFeatures(){
//        //adsEnabled only works on Android currently as of 02/19
//        adsEnabled = saveManager.getAdState(true);
//        supplyEnabled = !saveManager.getSupplyState(true);
//    }


    protected void initScreens(){
        screens = new Array<Screen>(3);
        
        //add all the screens to an Array for switching
        screens.add(new MenuScreen());
        screens.add(new CalculatorScreen());
//        screens.add(new SupplyScreen());
//        screens.add(new SettingScreen());

        scaleFonts();
        setState(0);
//        screensLoaded = true;
    }


    //scaling factor for the Density Independent Pixel unit
    // scales fonts to appropriate proportions (see the javadoc)
    protected void scaleFonts(){
        bitmapFonts = AssetHelper.getAllFonts();

        for (BitmapFont font : bitmapFonts){
            if (appType!=ApplicationType.Desktop){
                font.setScale(Gdx.graphics.getDensity()*.9f);
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

        if (Assets.manager.update()) {
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
    protected void exitApp(){

        for (Screen s : screens){
            s.dispose();
        }

        if (appType == ApplicationType.Android){
            pause();
        }
        else{
            //on android, will cause dispose/pause in near future
            Gdx.app.exit();
        }

        //dispose();
    }


    private void log(String message){
        Gdx.app.log("MainApp log: ", message);
    }
}
