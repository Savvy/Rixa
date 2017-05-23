@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Rixa-MC startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and RIXA_MC_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Rixa-MC-1.0.jar;%APP_HOME%\lib\JDA-3.0.0_165.jar;%APP_HOME%\lib\mysql-connector-java-5.1.38.jar;%APP_HOME%\lib\lavaplayer-1.2.34.jar;%APP_HOME%\lib\spigot-api-1.8.8-R0.1-SNAPSHOT.jar;%APP_HOME%\lib\bukkit-1.8.8-R0.1-SNAPSHOT.jar;%APP_HOME%\lib\commons-lang3-3.5.jar;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\json-20160810.jar;%APP_HOME%\lib\jna-4.4.0.jar;%APP_HOME%\lib\nv-websocket-client-1.31.jar;%APP_HOME%\lib\unirest-java-1.4.9.jar;%APP_HOME%\lib\lavaplayer-common-1.0.4.jar;%APP_HOME%\lib\slf4j-api-1.7.22.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\commons-io-2.5.jar;%APP_HOME%\lib\jackson-core-2.8.5.jar;%APP_HOME%\lib\jackson-databind-2.8.5.jar;%APP_HOME%\lib\jsoup-1.10.1.jar;%APP_HOME%\lib\base64-2.3.9.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\json-simple-1.1.1.jar;%APP_HOME%\lib\ebean-2.8.1.jar;%APP_HOME%\lib\snakeyaml-1.15.jar;%APP_HOME%\lib\bungeecord-chat-1.8-SNAPSHOT.jar;%APP_HOME%\lib\httpasyncclient-4.1.1.jar;%APP_HOME%\lib\httpmime-4.5.2.jar;%APP_HOME%\lib\httpcore-4.4.4.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.9.jar;%APP_HOME%\lib\jackson-annotations-2.8.0.jar;%APP_HOME%\lib\junit-4.10.jar;%APP_HOME%\lib\persistence-api-1.0.jar;%APP_HOME%\lib\httpcore-nio-4.4.4.jar;%APP_HOME%\lib\hamcrest-core-1.1.jar;%APP_HOME%\lib\gson-2.3.1.jar;%APP_HOME%\lib\guava-18.0.jar

@rem Execute Rixa-MC
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %RIXA_MC_OPTS%  -classpath "%CLASSPATH%" me.savvy.RixaMC %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable RIXA_MC_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%RIXA_MC_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
