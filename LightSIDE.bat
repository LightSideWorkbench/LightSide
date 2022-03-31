@echo off
PATH %PATH%;%JAVA_HOME%\bin\
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do (
set "v0=%%j"
set "v1=%%k"
)

if %v0% == 1 if %v1% lss 8 echo LightSide requires Java 1.8 or greater
if %v0% == 1 if %v1% == 8 call LightSIDE-java8.bat 
if %v0% == 1 if %v1% gtr 8 call LightSIDE-java9plus.bat 
if %v0% gtr 1 call LightSIDE-java9plus.bat 
