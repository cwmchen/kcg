@echo off
rem 如果不能正常显示中文需要设置编码为 UTF-8
setlocal enabledelayedexpansion
chcp 65001
set JAVA=%JAVA_HOME%
rem set JAVA=D:\tools\Java\jre1.8.0_191
if %JAVA% equ "" echo 请在您的环境中设置JAVA_HOME或者使用(set JAVA=D:\tools\Java\jre1.8.0_191)来指定您的Java，需要JDK8或更高版本! & EXIT /B 1

rem 获取目录
set BASE_DIR=%~dp0
rem 删除了最后5个字符（因为包含\bin\）,以获取根目录
set BASE_DIR=%BASE_DIR:~0,-5%

set RUN=%1

if "%RUN%"=="" (
    call:start
	goto :eof
)

echo %BASE_DIR%

rem 判断是否为启动命令
if /i "%RUN%" == "start" (
	call:start
	goto :eof
)

rem 判断是否为关闭命令
if /i "%RUN%" == "stop" (
   call:stop
   goto :eof
)

rem 判断是否为重启命令
if /i "%RUN%" == "restart" (
   call:restart
   goto :eof
)

if /i "%RUN%" == "help" (
   call:help
   goto :eof
)

rem 没有任何输入时进行相应提示
call:end
goto :eof


rem 执行启动
:start
rem spring boot 默认配置文件的搜索位置
set DEFAULT_SEARCH_LOCATIONS="classpath:/,classpath:/config/,file:./,file:./config/"
rem 自定义搜索地址，需要使用 --spring.config.location 来指定
set CUSTOM_SEARCH_LOCATIONS=%DEFAULT_SEARCH_LOCATIONS%,file:%BASE_DIR%/conf/

set "JAVA_OPT=%JAVA_OPT% -Xms512m -Xmx512m -Xmn256m"
set "JAVA_OPT=%JAVA_OPT% -Dfile.encoding=utf-8
set "JAVA_OPT=%JAVA_OPT% -Dloader.path=%BASE_DIR%/lib/ -jar %BASE_DIR%/lib/kcg-dashboard.jar"
set "JAVA_OPT=%JAVA_OPT% --spring.config.location=%CUSTOM_SEARCH_LOCATIONS%"
set "JAVA_OPT=%JAVA_OPT% --logging.path=%BASE_DIR%/logs"
set "JAVA_OPT=%JAVA_OPT% --logging.config=%BASE_DIR%/conf/logback-spring.xml"
call "%JAVA%\bin\java.exe" %JAVA_OPT% kcg-dashboard %*
goto :eof

rem 执行关闭
:stop
set "PATH=%JAVA%\bin;%PATH%"
echo 结束进程 kcg-dashboard
for /f "tokens=1" %%i in ('jps -m ^| find "kcg-dashboard"') do ( taskkill /F /PID %%i )
goto :eof

rem 执行重启
:restart
call:stop
call:start
goto :eof

:help
echo 1、启动服务使用 start   参数
echo 2、关闭服务使用 stop    参数
echo 3、重启服务使用 restart 参数
echo 4、没有参数默认启动服务
goto :eof

:end
echo 没有进行任何操作,请检查您的输入参数，您可以使用 help 获取帮助！
goto :eof

endlocal