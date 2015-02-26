#README#
-

A cross-platform e-cig juice calculator created using libgdx which calculates quantity user needs to create ejuice. The 
ultimate goal is to market this on the google play marketplace at first, and the appstore later. A free version with
adds will be exist that will not keep track of supplies.

Features include:

- User can save recipes and supplies. 
- Supplies are kept track of when entering values for calculation and indicates to user when running low on a supply (within app currently). 
- User will be able to text, email or upload recipe as note to the cloud.
- User will be able to search databases of stores for supplies - flavors, liquids & nicotine base.
- A database of flavors will also be included.
- The option to share recipes will eventually exist as well.

App Market States:
- 3 states that the app can be in depending on what user selects
- Rely on 2 boolean variables in code:
- Costs TBD. Still need to figure out ads, marketing strategy, etc.

| State        |   Description       | $ Cost |
| ------------ | --------------------|--------|
| Ads Enabled, Supply Features Disabled | Ads are on and user cannot store supplies | Free  |
| Ads Disabled, Supply Disabled |  User has ads disabled but no access to supply storage feature. | .99 |
| Ads Disabled, Supply Feature Enabled | User has access to supply storage feature. | 1.99? |

Google Dev:
    Project Number: 1019753713442
    Project ID: handy-digit-860

##Google Play Description##

This is an app designed for calculating liquid ratios to make "ejuice" or "eliquid" for electronic cigarettes. The ejuice
recipes created can be saved and loaded as well. This app also includes the ability to track quantities of individual liquids (VG, PG, etc),
flavors and nicotine base solution.

Features:

- User can save recipes and supplies.
- Supplies are kept track of when entering values for calculation and indicates to user when running low on a supply.

Features to come:

- User will be able to text, email or upload recipe as note to the cloud.
- User will be able to search databases of stores for supplies - flavors, liquids & nicotine base.
- A database of flavors will also be included.
- The option to share recipes will eventually exist as well.



    
###Names###
I still have yet to figure out an official name for this app. A few things to note when naming - keep it simple & descriptive. 

Potential names:

- Eliquitory (current name of the project and repo)
- EJuice Toolkit
- EJuice Lab
- EJuice Mate
- ELiquid Maker
- EJuice Tracker

###Changelog###
Nearly complete, just tweaking a few things. See [TODO](TODO.md).

- 02-24-15
	- completed the skin that will be the main theme

- 02-21-15
    - created a decent icon
    - changed up the fonts a bit

- 02-20-15
    - changed final calculated amounts display to seperate dialog
    - still need get a couple things done before being finished:
        - set up payment interface using gdx-pay
        - create an icon
        - create a splash screen
        - run final tests on ads, play services
- 02-17-15
    - got the screens to resize for anroid
    - table layouts completely finished

- 02-12-15
    - table layouts almost all completely finished
    - supply screen & calculator screen saving respective data correctly
- 02-06-15
    - have moved "extra" features of calculator to a private repository
    - fixed issues occuring with gradle & dev environment (intellij JDK not set to gradle one)
- 02-04-15
    - added tests for save and calc utility classes (passed all)
- 02-02-15
	- updated libgdx dependency from 1.5.0 to 1.5.3
	- added gson as possible serialization method
- 01-30-15
	- got the save/load working for all aspects of calculator
	- began the tweaking the overall look and feel of the app
- 1-18-15
	- finished the main calculator screen
	- added menu screen & began working on supply screen
- 1-06-15
	- began working on the app

##DEVELOPMENT NOTES##
###*Project setup*###

- OSX Yosemite 10.10.2 64-bit on MacBook Pro 2011 with IntelliJ 14 CE (also can work with Eclipse)
- SDKs: JDK 1.8, Android API 20.0 (Java 1.7.055)
- Libraries used (managed with Gradle):
- libgdx 1.5.3, libgdx tools, gson-2.3.1-beta
- Testing: Mockito-2.0.3 & JUnit-4.12
- see end of this section

##Adding Mobile Services##

Details on adding services such as Google Play are found [here](MobileServices.md)

###*Libgdx Implementation Notes*###

- Since this is an app, not a game, main app uses ApplicationListener.
- camera not used, since no moving bodies, only UI
- tables are used to hold elements
	- these need to be updated during resize
- Skins contain widget style- labels, textfields, buttons, etc
	- there configurations set JSON file, uses atlas & packed png
	- main class (MyTexturePacker) contains method to pack skins
- For saving, using Json and saving to an ObjectMap

- App State default values: supply disabled, ads enabled
    `boolean supplyEnabled = false; boolean adsEnabled = true`
- These are stored in libgdx Preferences class (SharedPreferences on Android); which can have multiple names
    Location of Preferences (OSX):

    ~~~~
    ~/.prefs/My Preferences
    ~~~~

###Running###
See <https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline>. For OSX, need to do in root.

####*Desktop:*####

~~~~
./gradlew desktop:run
~~~~

####*Android*####
* make sure that the AVD is running correctly
* need to clean/rebuild project if predex using old classes
* making sure android API set in project.properties, AndroidManifest & build.gradle

~~~~ 
./gradlew android:installDebug android:run
~~~~ 
	
####*HTML*####
dependencies in build.gradle need to be in GdxDefinition.gwt.xml & *SuperDev.gwt.xml

~~~~
./gradlew html:superDev
~~~~

####*iOS (need to provision device via XCode)*####

~~~~ 
./gradlew ios:launchIPhoneSimulator
~~~~
~~~~ 
./gradlew ios:launchIPadSimulator
~~~~
~~~~ 
./gradlew ios:launchIOSDevice
~~~~

###Packaging:###
When finished with final product, need to sign Android APK.

####*Desktop:*####

~~~~
./gradlew desktop:dist
~~~~

####*Android:*####

~~~~
./gradlew android:assembleRelease
~~~~

####*HTML:*####

~~~~
./gradlew html:dist
~~~~

####*iOS:*#### 

~~~~
./gradlew ios:createIPA
~~~~


###Fixes for Project Issues###

####*GradleConnectionError Issue*####
- GradleConnectionException and bootstrap classpath not being set during Gradle build
- Current fix (works!):
- plugged script into .profile so that just need to run following:
	- Set Java to 7 & JVM to 1.7 using terminal

		~~~~
		setjdk 1.7
		~~~~

####*Solutions to Other Possible Issues*####
Quick solutions to potential issues:
- invalidating caches & restarting IntelliJ
- in IntelliJ: Build | Clean Project or Build | Rebuild Project
- rebuild the project by deleting old bin, build directories
- See end of [TODO](TODO.md) for more info on possible solutions - ones I have tried, that did not really succeed but might by useful in future.

###Contact Info###
----

My email: 
<allen.jagoda@gmail.com>