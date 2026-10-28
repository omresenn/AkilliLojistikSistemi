@echo off
chcp 65001 > nul
echo ===================================================
echo   Akilli Lojistik Sistemi - Birim Testleri (Unit Tests)
echo ===================================================

set "JAVA_CMD="

if exist "C:\Users\omere\AppData\Local\Programs\IntelliJ IDEA\jbr\bin\java.exe" (
    set "JAVA_CMD=C:\Users\omere\AppData\Local\Programs\IntelliJ IDEA\jbr\bin\java.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    )
)

if not defined JAVA_CMD (
    set "JAVA_CMD=java"
)

if not exist "bin\com\lojistik\LogisticsUnitTestSuite.class" (
    echo [BILGI] Once derleme yapiliyor...
    call compile.bat
)

"%JAVA_CMD%" -Dfile.encoding=UTF-8 -cp bin com.lojistik.LogisticsUnitTestSuite
pause
