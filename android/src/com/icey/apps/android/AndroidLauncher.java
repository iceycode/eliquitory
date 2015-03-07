package com.icey.apps.android;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.icey.apps.MainApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Android Launcher (aka Main Activity)
 *
 * A lot of this code was taken from tutorial-libgdx-google-ads-android found via libgdx wiki
 * wiki: https://github.com/libgdx/libgdx/wiki/Google-Mobile-Ads-in-Libgdx-%28replaces-deprecated-AdMob%29
 * repo: https://github.com/TheInvader360/tutorial-libgdx-google-ads
 *
 * Also see https://github.com/libgdx/libgdx/wiki/Admob-in-libgdx for more detailed tutorial & info about controlling ads
 * com.google.android.gms:play-services-ads:6.5.87 <----these are ad services
 * TODO: setup for my own app
 *
 * TODO: figure out advertisment setup
 * TODO: get google dev id
 *
 * TODO: get an AdMob Ad Unit ID
 * https://support.google.com/admob/v2/answer/3052638
 *
 * To cause a device to recieve text ads use:
 *  addTestDevice (String deviceId)
 *
 *
 */

public class AndroidLauncher extends AndroidApplication {

    //NOTE: this is currently a test ad unit
    private static final String AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/6300978111"; //google test ad
    private static final String AD_UNIT_ID = "ca-app-pub-8567699201008640/7700422013";
    //private static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/developer?id=";

    //for getting actual device ID
    private MainApp mainApp; //main app instance
    private AdView adView;
    private View appView;

    //for showing/hiding ads
    public static boolean showAds;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mainApp = new MainApp(); //create main app

        //showAds = MainApp.adsEnabled;
        showAds = true;

        if (showAds){
            createWithAds();
        }
        else{
            createWithoutAds();
        }
	}

    //no ads
    protected void createWithoutAds(){
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        initialize(mainApp, config); //FOR WITHOUT ADS
    }


    //creates instance of MainApp() with AdView
    protected void createWithAds(){
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        config.useGLSurfaceView20API18 = false;
        config.useAccelerometer = false;
        config.useCompass = false;

        //creates ad layout
        RelativeLayout layout = setAdLayout();

        //creates the AdMob view
        adView = createAdView();
        layout.addView(adView);

        //creates the MainApp view - add first, or else will cover ads
        appView = createAppView(config);
        layout.addView(appView);

        setContentView(layout);
        startAdvertising(adView);

    }


    protected RelativeLayout setAdLayout(){

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        RelativeLayout layout = new RelativeLayout(this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);

        return layout;
    }


    //The ad view - contains a seperate thread containing AdMob view
    protected AdView createAdView() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AD_UNIT_ID_TEST);

        //adView.setAdUnitId(AD_UNIT_ID); //my ad unit

        //NOTE: had to disable inspection on this since it was showing incorrect error (type mismatch)
        adView.setId(12345); // this is an arbitrary id, allows for relative positioning in createGameView()
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        adView.setLayoutParams(params);
        adView.setBackgroundColor(Color.BLACK);
        return adView;
    }


    //The MainApp view - libgdx view
    protected View createAppView(AndroidApplicationConfiguration cfg) {
        View appView = initializeForView(mainApp, cfg);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ABOVE, adView.getId());
        appView.setLayoutParams(params);

        return appView;
    }

    //get a request for ad
    protected void startAdvertising(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();

        //NOTE: for testing on external device if using own test unit
//        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        String deviceId = md5(android_id).toUpperCase();
//        boolean isTestDevice = adRequest.isTestDevice(this);
//
//        log("is Admob Test Device ? "+deviceId+" "+isTestDevice); //to confirm it worked

//        AdRequest.Builder builder = new AdRequest.Builder();
//        builder.addTestDevice(deviceId); //for external DEVICE
////        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build(); //for EMULATOR
//        builder.build();


        adView.loadAd(adRequest);
    }

    //for obtaining device ID for testing AdMob ads
    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log(e.getMessage());
        }
        return "";
    }

    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        Button b1 = new Button(this);
        b1.setText("Quit");
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(this);
        b2.setText("t");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URL)));
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        dialog.setContentView(ll);
        dialog.show();
    }


    private static void log(String message){
        Gdx.app.log("AndroidLauncher LOG: ", message);
    }

}
