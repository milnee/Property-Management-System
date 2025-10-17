@echo on
echo Starting Property Management Application Setup...
echo.

REM Check Java installation
echo Checking Java installation...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found!
    echo Please install Java JDK 17 or later.
    pause
    exit /b 1
)
echo Java check passed.
echo.

REM Set JAVA_HOME correctly
echo Setting JAVA_HOME...
set "JAVA_HOME=C:\Program Files\Java\jdk-24"
echo JAVA_HOME set to: %JAVA_HOME%
echo.

REM Check Maven installation
echo Checking Maven installation...
set "MVN_PATH=C:\Program Files\Apache\Maven\bin"
if exist "%MVN_PATH%\mvn.cmd" (
    echo Found Maven at %MVN_PATH%
    set "PATH=%PATH%;%MVN_PATH%"
) else (
    echo Error: Maven not found at %MVN_PATH%
    echo Please install Maven.
    pause
    exit /b 1
)
echo Maven check passed.
echo.

REM Show current directory
echo Current working directory: %CD%
echo.

REM Clean any previous builds
echo Cleaning previous builds...
rmdir /s /q target 2>nul
echo.

REM Try to run the application
echo Building and running the application...
echo.
call "%MVN_PATH%\mvn.cmd" clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed! Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Build successful! Starting the application...
echo.
call "%MVN_PATH%\mvn.cmd" javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error running the application!
    echo Please check:
    echo 1. Java JDK 17 or later is installed
    echo 2. Maven is installed in C:\Program Files\Apache\Maven
    echo 3. All dependencies are available
    echo.
    echo You can try running: mvn clean compile javafx:run -X
    echo for more detailed error information.
    echo.
    pause
    exit /b 1
)

echo.
echo Application closed.
pause