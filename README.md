#README#

A simple eliquid calculator created using libgdx. This will eventually store all recipes and also be able to calculate how much of which liquid is running low (flavors, base, etc).

###About environment setup###

- OSX 10.10.2 64 bit with IntelliJ 14 CE (also can work with Eclipse)
- SDKs: JDK 1.8, Android API 21.0 (Lollipop)
- Libraries used (managed with Gradle):
- libgdx 1.5.3, libgdx tools, gson-2.3.1-beta
- Testing: Mockito-2.0.3 & JUnit-4.12
- NOTE: If using intellij on OSX and getting Gradle error, set JVM version by going into package contents & editing Info.plist of intellij app



##*LOG*##
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
	- finished the main calclator screen
	- added menu screen & began working on supply screen
- 1-06-15
	- began working on the app
