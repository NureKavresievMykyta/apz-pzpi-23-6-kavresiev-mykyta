@echo off
title IoT DEVICE - SMART PHOROPTER
color 0B
echo ==========================================
echo       START IOT CLIENT...
echo ==========================================
echo.

set JAVA_PATH="C:\Users\kavres\.jdks\ms-17.0.17\bin\java.exe"


echo Using Java: %JAVA_PATH%

%JAVA_PATH% -cp target/optiview-0.0.1-SNAPSHOT.jar -Dloader.main=ua.nure.kavresiev.optiview.SmartPhoropterEmulator org.springframework.boot.loader.launch.PropertiesLauncher

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [INFO] Trying to start...
    %JAVA_PATH% -cp target/optiview-0.0.1-SNAPSHOT.jar -Dloader.main=ua.nure.kavresiev.optiview.SmartPhoropterEmulator org.springframework.boot.loader.PropertiesLauncher
)

pause