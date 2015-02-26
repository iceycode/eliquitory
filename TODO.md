#TODO#

##*Code*##

Market Services - deployment

- get a developer license
- create an application
- add AdMob ads via google play services
    - see https://github.com/libgdx/libgdx/wiki/Google-Mobile-Ads-in-Libgdx-%28replaces-deprecated-AdMob%29
    - more detailed tutorial at https://github.com/libgdx/libgdx/wiki/Admob-in-libgdx

- Get an AdMob unit ID https://support.google.com/admob/v2/answer/3052638
- https://developers.google.com/mobile-ads-sdk/docs/admob/android/quick-start

- In-App Purchases
    - set up gdx-pay

TABLE RESIZING NOTE:

- make sure table resizes widgets & cells during resize method by calling invalidateHierarchy() on root table

MenuScreen

-  fonts need to be bit bigger for titles
-  ~~fix table widget positions/size - listeners not aligned~~

Options Screen
- ~~create it~~
- ~~add drops/ml setting to it~~
- ability to connect with FB/G+
- add to it unlocking features
    - ~~disable Ads~~
    - ~~unlock supply keeping feature~~

SupplyScreen

- ~~make fonts smaller & table larger~~
- make the messageFontColor more bold
-  ~~regular supplies checkboxes not checked when opening edit window~~
- ~~make the window more opaque, it is now too transparent~~ 
- ~~create separate textFont style for editing supply window~~
- ~~for some reason, only with supplies, title tab bar is not up at top~~


SupplyScreen & CalculatorScreen

- fix calculatorScreen table layout listeners not at same position as cells
- when going from Calcscreen to SupplyScreen, nicotine base supply is set to settings in calcscreen
- ~~set a custom textfieldfilter~~ 

 


###*Platform-Dependent*###
Android:

- run on virtual android device
- fix screen resizing - table not scaling children 
	Fix table by enabling clipping, don't rescale anything other than table.
		
	~~~~
	//forresize
	stage.getViewport().update(width, height, false);
	table.invalidateHierarchy();
	table.setSize(width, height);
	...
	//for table settings
	table.setClip(true);
	~~~~
- Error - related to AVD or app?
	
	~~~~
	Warning occurs: WARNING: linker: libdvm.so has text relocations. This is wasting memory and is a security risk. Please fix.
	~~~~
- make area for adds bigger 
- SupplyScreen
	- make buttons for adding bigger
	- adjust window position
- MenuScreen
	- make fonts bigger
	- listeners are not aligned - (worked before) 
- Google Play
	- set up app in Google Play Dev Console
	- Monetize App with AdMob
	- create tests for ads


HTML - NOTE: this is low on list of TODOs since:

-  figure out exactly what is causing this error:
	
	~~~~
	Error:Gradle: Execution failed for task ':html:compileGwt'.
> Process 'command '/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home/bin/java'' finished with non-zero exit value 1
	~~~~
	- Base64Coder error occurs


##*Graphics*##

- add splash when loading
- ~~make UI look a bit nicer~~
- ~~Set opaque background for adding/editing supplies in supplywindow~~

##FEATURES TO ADD##

- ability to save files to external SD card (for android)
- ability to send recipe/supply data to email, text, etc
- database of flavors
- websites that user can buy refills
    - ability to auto-order supplies when running low
- sync up with other user recipes, similar to <e-liquid-recipes.com>
- add ability to add custom names to liquids


##Gradle-IntelliJ Issues##

###SOLVED Gradle-Java version issue! ###
- Added a script to .profile so that setjdk 1.7 command will make use of java 7 & set global JVM to 1.7.0_55
    To change java/jvm enter following into terminal:

    ~~~~
    setjdk 1.7
    ~~~~

Thanks to this blog post!
<http://www.jayway.com/2014/01/15/how-to-switch-jdk-version-on-mac-os-x-maverick/>


Other solutions for future:

- ~~If using Intellij on OSX and getting Gradle error, set JVM version by going into package contents & editing Info.plist of intellij app~~

- ~~Set system JAVA_HOME path & check:~~

	~~~~
	export JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
	~~~~
	or

	~~~~
	export JAVA_HOME=`/usr/libexec/java_home -v '1.7*'`
	~~~~
	To check

	~~~~
	echo $JAVA_HOME
	~~~~
- ~~reset the compiler by updating CurrentJDK symbolic link to 1.7~~

    ~~~~
    cd /System/Library/Frameworks/JavaVM.framework/Versions/
    sudo rm CurrentJDK
    sudo ln -s /Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/ CurrentJDK
    ~~~~
- ~~add to ~./mavenrc~~

    ~~~~
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home
    ~~~~


- IntelliJ forces change in gradle properties, even after changing gradle-wrapper.properties
- Can fix gradle after doing a full rebuild by deleting
- Possible real (non-temporary) fix:
	- Need to make sure to set JVM everytime
	- check using javac -version in IntelliJ terminal

