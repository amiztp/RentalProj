@echo off
REM Compile and Run Java Vehicle Renting System GUI

echo ========================================
echo  Compiling Vehicle Renting System GUI
echo ========================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile all Java files including GUI
echo Compiling source files...
javac -d bin -sourcepath src src\main\java\com\newsoft\VehicleRenting\model\*.java src\main\java\com\newsoft\VehicleRenting\repository\*.java src\main\java\com\newsoft\VehicleRenting\service\*.java src\main\java\com\newsoft\VehicleRenting\ui\*.java

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
echo.
echo ========================================
echo  Launching GUI Application...
echo ========================================
echo.

REM Run the GUI application
start javaw -cp bin main.java.com.newsoft.VehicleRenting.ui.VehicleRentalUI

echo GUI application launched!
echo You can close this window.
timeout /t 3
