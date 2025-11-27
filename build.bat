@echo off

if not exist "run" mkdir "run"
if not exist "build\palette" mkdir "build\palette"

if not exist "run" (
    echo Error: Failed to create required directories 1>&2
    exit /b 1
)

if not exist "build\palette" (
    echo Error: Failed to create required directories 1>&2
    exit /b 1
)

echo eula=true> "run\eula.txt"
if %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to accept EULA 1>&2
    exit /b 1
)

echo Starting Minecraft server
call gradlew.bat --console plain --no-daemon runServer --info --stacktrace

if %ERRORLEVEL% NEQ 0 (
    echo Error: Could not start server 1>&2
    exit /b 1
)

echo Copying generated files

copy "run\palette\colors.csv" "build\palette\" >nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to copy generated files 1>&2
    exit /b 1
)

copy "run\palette\blocks.csv" "build\palette\" >nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to copy generated files 1>&2
    exit /b 1
)

echo Done
