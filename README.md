#README#

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

Nearly complete, just tweaking a few things. See [TODO](TODO.md).

----

#DEVELOPMENT NOTES#

##Changelog##
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

----

##Environment setup##

- OSX 10.10.2 64 bit with IntelliJ 14 CE (also can work with Eclipse)
- SDKs: JDK 1.8, Android API 20.0 (Java 1.7.055)
- Libraries used (managed with Gradle):
- libgdx 1.5.3, libgdx tools, gson-2.3.1-beta
- Testing: Mockito-2.0.3 & JUnit-4.12



GradleConnectionException issue: 
Potential fixes found online:

- If using intellij on OSX and getting Gradle error, set JVM version by going into package contents & editing Info.plist of intellij app
- Other things I have tried:
    - Set gradle JDK directory to 1.7, in terminal:
<code>./gradlew -D JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home"<code>
    - set in gradle.properties: <code>org.gradle.java.home="/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home"<code>

STILL NO FIX FOR GradleConnectionException issue!!!

----

##Deployment##
###Running###
See <https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline>. For OSX, need to do in root.

Desktop: <code>gradlew desktop:run<code>

Android:
<code> gradlew android:installDebug android:run<code>

HTML: <code> gradlew html:superDev <code>

iOS (need to provision device via XCode)

<code> gradlew ios:launchIPhoneSimulator <code>

<code>gradlew ios:launchIPadSimulator <code>

<code>gradlew ios:launchIOSDevice<code>


###Packaging:###
When finished with final product, need to sign Android APK.

Desktop: 
<code>gradlew desktop:dist<code>

Android: 
<code>gradlew android:assembleRelease<code>

HTML:
<code> gradlew html:dist <code>

iOS: <code>gradlew ios:createIPA<code>



##Contact Info##
----

My email: 
<allen.jagoda@gmail.com>