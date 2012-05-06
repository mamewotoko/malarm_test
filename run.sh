#! /bin/sh
# start Android emulator, then install and test

PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
AVD=XperiaEmu
PORT=5554
TARGET=installd
echo arg $1
if [ "$1" != "notest" ]; then
  TARGET="$TARGET test"
fi

adb devices | grep emulator-$PORT > /dev/null
if [ $? -ne 0 ]; then
 echo start emulator
 emulator -avd $AVD -port $PORT &
 echo waiting emulator 2 min
 sleep 120
fi

echo start testing
# unlock screen by pressing menu key
adb connect localhost:$PORT
adb shell input keyevent 3 
adb shell input keyevent 82
ant uninstall
ant debug
ant $TARGET

