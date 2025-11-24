@echo off
REM Compile Java Vehicle Renting System

echo ========================================
echo  Compiling Java Vehicle Renting System
echo ========================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files
echo Compiling source files...
javac -d bin -sourcepath src src\main\java\com\newsoft\VehicleRenting\model\*.java src\main\java\com\newsoft\VehicleRenting\repository\*.java src\main\java\com\newsoft\VehicleRenting\service\*.java src\main\java\com\newsoft\VehicleRenting\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ========================================
    echo  Compilation FAILED!
    echo ========================================
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Compilation Successful!
echo ========================================
pause
