adb pair <ip:port>     # enter the pairing code
adb connect <ip:another_port_shown> 
adb devices

# compile, package, install, run on android
mvn -Pandroid -Djava.home="$GRAALVM_HOME" clean gluonfx:compile gluonfx:link gluonfx:package gluonfx:install gluonfx:nativerun

# run as java
mvn -Plinux-x64 clean javafx:run

# run native on desktop
mvn -Pdesktop -Djava.home="$GRAALVM_HOME" clean gluonfx:compile gluonfx:link gluonfx:nativerun

