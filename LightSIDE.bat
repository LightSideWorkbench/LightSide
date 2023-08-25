@echo off
(echo Launching LightSide! Console output and errors are being saved to lightside_log.) >CON

set memory=8G

set other_args=-Dswing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel --add-opens java.xml/com.sun.org.apache.xml.internal.serialize=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED

set classpath="build/classes/java/main;lib/;lib/plugins/*;lib/plugins/genesis.jar;lib/*"

set wekafiles=wekafiles/packages/chiSquaredAttributeEval/chiSquaredAttributeEval.jar;wekafiles/packages/bayesianLogisticRegression/bayesianLogisticRegression.jar;wekafiles/packages/LibLINEAR/lib/liblinear-java-1.96-SNAPSHOT.jar;wekafiles/packages/LibLINEAR/LibLINEAR.jar;wekafiles/packages/LibSVM/lib/libsvm.jar;wekafiles/packages/LibSVM/LibSVM.jar

set splash=toolkits/icons/logo.png

set mainclass=edu.cmu.side.Workbench

( echo %date% %time%
  java.exe -Xmx"%memory%" %other_args% -splash:"%splash%" -classpath "%classpath%";"%wekafiles%" "%mainclass%"
) >>lightside_log.log 2>&1
