#Adding Mobile Services#
----

This contains info on adding mobile services such as Google Play services or AppStore services.

Current state of services:

02-17:
Currently, google_play_services_lib is module outside of root. Keep this in mind if cloning from git or recreating project.

##Google Play##
----

Project ID: handy-digit-860
Project Name: E-Juice Maker

AdMob Publisher ID: pub-8567699201008640
Testing AdMob id: ca-app-pub-3940256099942544/6300978111

Get an AdMob unit ID https://support.google.com/admob/v2/answer/3052638

This app will mainly be using Google Play services for pushing updates. Also, possibly to sync users up with Google Plus
and maybe for various features to be added in the future.

I have now had to import 3 times, each time after closing and reopening project. This most likely has to do with Gradle
not having the module in the build. Might have to add a build stub to the google-play.. module to make gradle recognize
that it is being used.

Solutions to importing google play services library into project:
Links:
[Bug fix for IntelliJ 12](https://youtrack.jetbrains.com/issue/IDEA-96525)
Another solution, but does not involve creation of Dummy class: [link](http://devsupport.crystalsdk.com/default.asp?W217)
Yet another method:
It is the second response to the question: [link](https://stackoverflow.com/questions/14372391/java-lang-noclassdeffounderror-com-google-android-gms-rstyleable/15826818#15826818)
    - this solution is similar to 1 & 2, but does not require the libary to be in root project.

    1. Select your project from project panel
    2. Hit F4 in order to open the project structure window
    3. Select Modules from left panel, then hit + button then select "import module" and navigate to "ANDROID-SDK"/extras/google/google_play_services/libproject/google-play-services_lib. By doing this this project will be added to Modules alongside with your project
    4 Select your project, then select "Dependencies" (it's a Tab) from right panel. Click + button then "3 Module dependencies..." and select "google-play-services_lib".
    5. Click + button again then "1 jars or directories..." and navigate to "/libs" folder in the above path and select "google-play-services.jar".
    6. Click ok and close project structure window.

    Note: Make sure you have not added "google-play-services_lib" project as library in "Libraries" (left panel under Modules).
    If you have, you can go to "Libraries and simply remove it. Google Play Library Should Not Appear In Libraries.

    Note 2: You can copy the whole google-play-services_lib directory to your own project if you wish to have it in version control or similar.

Basic Steps For Importing:
    1. Imported directory above into project root directory - File | Import Module
    (1a. Create a file Dummy.java in google-play-.../src folder and add the text "class Dummy {}" there - fix for bug from IntelliJ 12)
    2. Imported module, selected googl-play-services & created library from sources
        2-alt. "Create module from existing sources"
    3. Added the dependency to Android Module using via File | Project Structure | Modules...

*Libgdx tutorials:*
- need to add a sort of wrapper for banner ads
    - libgdx wiki [link](https://github.com/libgdx/libgdx/wiki/Google-Mobile-Ads-in-Libgdx-%28replaces-deprecated-AdMob%29)
    - http://fortheloss.org/tutorial-set-up-google-services-with-libgdx/

###My Solution###
- Location of directory:
    path = [Android SDK Location]/sdk/extras/google/google_play_services/libproject/google-play-services_lib/

- What I did:
- the above worked, however, upon reopening my project gradle had warning and error related to the library
    - seems like I have to import google-play.. everytime I reopen project


###Info on Google Play Services###

See [google developer page](https://developer.android.com/google/play-services/index.html) for detailed information.

- To build.gradle, under android, add following dependencies:
    - NOTE: first dependency causes errors
    - also, need to add these so that they properly compile the play services module in root

~~~~
dependencies{
    compile 'com.android.support:appcompat-v7:21.0.3'
    compile 'com.google.android.gms:play-services:6.5.87'
}
~~~~

- In the manifest file, add following tag as child of <application> element:

~~~~
<application ...>

        ...
        <meta-data android:name="com.google.android.gms.version"
                           android:value="@integer/google_play_services_version" />
        <activity

        </activity>
       <activity android:name="com.google.android.gms.ads.AdActivity"
       android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"/>
</application>

~~~~

- Add these two permissions as children of the 'manifest' element above:

~~~~
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
~~~~

Create a proguard exception - add to android/proguard-project.txt:

~~~~
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
~~~~

Create a new xml in android/res/values:
- do not user actual publisher ID for tests

~~~~
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!--<string name="app_id">YOUR APP ID HERE</string>-->
    <string name="banner_ad_unit_id">ca-app-pub-3940256099942544/6300978111</string>
</resources>
~~~~

For testing:

    ~~~~
    addTestDevice (String deviceId)
    ~~~~

- device ID can be obtained by viewing the logcat output after creating new ad
- For emulator:

    ~~~~
    DEVICE_ID_EMULATOR
    ~~~~


##Setting up In-app Purchases##
User the libgdx tool gdx-pay.
In core libs, need to put gdx-pay.jar

In android libs:
    - gdx-pay-android.jar
    - gdx-pay-android-openiab.jar (supports GooglePlay, Amazon, etc)
    - gdx-pay-android-ouya.jar (support OUYA)

In AndroidManifext.xml

    <!--all-->
    <uses-permission android:name="android.permission.INTERNET"/>
    <!--Google Play-->
    <uses-permission android:name="com.android.vending.BILLING"/>
    <!--Open Store-->
    <uses-permission android:name="org.onepf.openiab.permission.BILLING"/>
    <!--Samsung Apps-->
    <uses-permission android:name="com.sec.android.iap.permission.BILLING"/>
    <!--Nokia-->
    <uses-permission android:name="com.nokia.payment.BILLING"/>
    <!--SlideME-->
    <uses-permission android:name="com.slideme.sam.manager.inapp.permission.BILLING"/>

In proguard:
    
    -keep class com.android.vending.billing.**
    -keep class com.amazon.** {*;}
    -keep class com.sec.android.iap.**
    -keep class com.nokia.payment.iap.aidl.**
    -dontwarn org.onepf.oms.appstore.FortumoBillingService

