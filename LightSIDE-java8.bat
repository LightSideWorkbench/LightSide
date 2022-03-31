@echo off
(echo Launching LightSide! Console output and errors are being saved to lightside_log.) >CON

set MAXHEAP=4G

set OTHER_ARGS=-Dswing.defaultlaf=com.sun.java.swing.plaf.windows.WindowsLookAndFeel

set SPLASH=toolkits/icons/logo.png

set MAIN_CLASS=edu.cmu.side.Workbench

set JAR="LightSide.jar"

( echo %date% %time%
  java -cp %JAR% -Xmx"%MAXHEAP%" -splash:"%SPLASH%" %OTHER_ARGS% %MAIN_CLASS%
) >>lightside_log.log 2>&1
