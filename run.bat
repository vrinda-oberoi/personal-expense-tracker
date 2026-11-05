@echo off
title Personal Expense Tracker
echo ====================================================
echo   Personal Expense Tracker - Java Semester Project
echo ====================================================
echo.
if not exist Main.class (
    echo Compiling Java source files...
    javac *.java
    if errorlevel 1 (
        echo [ERROR] Compilation failed. Please ensure JDK 8+ is installed.
        pause
        exit /b 1
    )
    echo Compilation successful!
)
echo Launching Expense Tracker GUI...
start "Personal Expense Tracker" java -cp . Main
exit
