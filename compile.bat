@echo off
echo ====================================================
echo   Compiling Personal Expense Tracker (Java)
echo ====================================================
echo.
javac *.java
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] All Java source files compiled successfully.
) else (
    echo [ERROR] Compilation failed.
)
pause

