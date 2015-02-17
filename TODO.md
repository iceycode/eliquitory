#TODO#

##*Code*##

###*Libgdx*###

Market Services - deployment

- add google ads, google play services
- add a larger database into the mix (possibly in future release)

TABLE RESIZING NOTE:

- make sure table resizes widgets & cells - setting layoutEnabled(true) & invalidateHierarchy() during resize
- do not set actor size & rely on it, set size using tables

MenuScreen

-  fonts need to be bit bigger
-  ~~fix table widget positions/size - listeners not aligned~~


SupplyScreen

- make fonts smaller & table larger
- make the messageFontColor more bold
-  ~~regular supplies checkboxes not checked when opening edit window~~
- ~~make the window more opaque, it is now too transparent~~ 
- ~~create separate textFont style for editing supply window~~
- ~~for some reason, only with supplies, title tab bar is not up at top~~


SupplyScreen & CalculatorScreen

- fix calculatorScreen table layout listeners not at same position as cells
- when going from Calcscreen to SupplyScreen, nicotine base supply is set to settings in calcscreen
- ~~set a custom textfieldfilter~~ 

FlavorTable

- unchecked FlavorTable operations;mfollowing error occurs during compile:

	~~~~
	Note: /Users/Allen/MEGA/Workspaces/app workspace/eliquitory/core/src/com/icey/apps/ui/FlavorTable.java uses unchecked or unsafe operations.
	~~~~



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

HTML:

-  figure out exactly what is causing this error:
	
	~~~~
	Error:Gradle: Execution failed for task ':html:compileGwt'.
> Process 'command '/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home/bin/java'' finished with non-zero exit value 1
	~~~~
	- Base64Coder error occurs

SaveManager

- set platform specific filehandle paths

##*Graphics*##

- add splash when loading
- make graphics look a bit nicer
- ~~Set opaque background for adding/editing supplies in supplywindow~~ 

##FEATURES TO ADD##


- ability to save files to external SD card (for android)
- ability to send recipe/supply data to email, text, etc
- database of flavors
- websites that user can buy refills
    - ability to auto-order supplies when running low
- sync up with other user recipes, similar to <e-liquid-recipes.com>



##Gradle-IntelliJ ISSUES##


- Set system JAVA_HOME path & check:

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
- add to ~./mavenrc

~~~~
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home
~~~~


- IntelliJ forces change in gradle properties, even after changing gradle-wrapper.properties
- Can fix gradle after doing a full rebuild by deleting
- Possible real (non-temporary) fix:
	- Need to make sure to set JVM everytime
	- check using javac -version in IntelliJ terminal

###SOLVED Java/JVM issue! ###
- Added a script to .profile so that setjdk 1.7 command will make use of java 7 & set global JVM to 1.7.0_55

Thanks to this blog post!
<http://www.jayway.com/2014/01/15/how-to-switch-jdk-version-on-mac-os-x-maverick/>