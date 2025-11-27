@echo off
REM Compile all Java sources under src\main\java into bin
if not exist bin mkdir bin

echo Compiling Java sources...
javac -encoding UTF-8 -d bin -sourcepath src\main\java src\main\java\com\newsoft\VehicleRenting\Main.java src\main\java\com\newsoft\VehicleRenting\model\*.java src\main\java\com\newsoft\VehicleRenting\payments\*.java src\main\java\com\newsoft\VehicleRenting\repository\*.java src\main\java\com\newsoft\VehicleRenting\service\*.java src\main\java\com\newsoft\VehicleRenting\ui\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo.
echo Compilation finished successfully.
pause
