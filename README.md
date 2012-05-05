# malarm_test - UI test of malarm application
## What is this?

This is a UI test of malarm using Robotium library

## How to run:
1. Download robotium-solo-xx.jar file
 e.g. execute libs/setup.sh in lib directory

2. Put robotium-solo-xx.jar into libs directory
3. Deploy Malarm application

Malarm source repository: git://github.com/mamewotoko/malarm.git

4. Start eclipse and add libs/robotium-solo-xx.jar into build path
5. Build
6. Run as Android JUnit test

## How to start testing from the command line
    ant test

or
    adb shell am instrument -w com.mamewo.malarm_test/android.test.InstrumentationTestRunner

## TODO
- capture screen in more useful way
- check version of malarm before testing
- fix setAlarm test

## Reference
- Robotium:
http://code.google.com/p/robotium/
- How to use ddmslib to capture screen shot
http://blog.codetastrophe.com/2008/12/using-androiddebugbridge-api-to-get.html
- Screenshots with Android NativeDriver - internals
http://nativedriver.googlecode.com/files/Screenshot_on_Android_Internals.pdf
----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/

