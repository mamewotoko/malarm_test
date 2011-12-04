# malarm_test - UI test of malarm application
## What is this?

This is a UI test of malarm using Robotium library

## How to run:
1. Download robotium-solo-xx.jar file

http://code.google.com/p/robotium/downloads/list?can=3&amp;q=&amp:colspec=Filename+Summary+Uploaded+ReleaseDate+Size+DownloadCount

2. Put robotium-solo-xx.jar into lib directory
3. Deploy Malarm application

Malarm source repository: git://github.com/mamewotoko/malarm.git

4. Start eclipse and add lib/robotium-solo-xx.jar into build path
5. Build
6. Run as Android JUnit test

## How to start testing from the command line
    adb shell am instrument -w com.mamewo.malarm_test/android.test.InstrumentationTestRunner

## TODO
- capture screen in more useful way

## Reference
- Robotium: http://code.google.com/p/robotium/
- How to use ddmslib to capture screen shot
http://blog.codetastrophe.com/2008/12/using-androiddebugbridge-api-to-get.html
- Screenshots with Android NativeDriver - internals
http://nativedriver.googlecode.com/files/Screenshot_on_Android_Internals.pdf
----
Takashi Masuyama < mamewotoko@gmail.com >  
http://www002.upp.so-net.ne.jp/mamewo/
