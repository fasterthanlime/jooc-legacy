@echo off
REM Add the directory path where this file stay to your Windows enviroment variable Path 




if not exist %OOC_DIST%\bin\ooc.jar (
	echo OOC_DIST is set incorrectly or doesn't exists. Please set OOC_DIST.
) else (
java.exe -jar "%OOC_DIST%\bin\ooc.jar" %*
)

