@echo off
REM Run Java Vehicle Renting System

echo ========================================
echo  Running Vehicle Renting System
echo ========================================
echo.

REM Check if compiled classes exist
if not exist "bin\main\java\com\newsoft\VehicleRenting\Main.class" (
    echo Error: Compiled classes not found!
    echo Please run compile.bat or compile_and_run.bat first.
    echo.
    pause
    exit /b 1
)

REM Run the application
java -cp bin main.java.com.newsoft.VehicleRenting.Main

echo.
echo ========================================
echo  Execution completed
echo ========================================
pause
