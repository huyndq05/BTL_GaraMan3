@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ========================================================
echo   KHOI DONG HE THONG WEB APP GARAMAN3 (LOCALHOST:8080)
echo ========================================================
echo.

set MAVEN_CMD="C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
if not exist %MAVEN_CMD% (
    set MAVEN_CMD=mvn
)

echo Dang khoi chay may chu Apache Tomcat tai http://localhost:8080/GaraMan3 ...
echo Nhan Ctrl + C neu muon dung server.
echo.

start "" "http://localhost:8080/GaraMan3"

%MAVEN_CMD% tomcat7:run
pause
