@echo off
if not exist bin (
    echo "bin folder not found. Please compile first (run compile.bat)."
    pause
    exit /b 1
)
REM Adjust the Main class package path if different
java -cp bin main.java.com.newsoft.VehicleRenting.Main
pause
