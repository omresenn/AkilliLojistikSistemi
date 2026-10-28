@echo off
chcp 65001 > nul
echo ===================================================
echo   Akilli Lojistik Sistemi - Proje Derleme Araci
echo ===================================================

set "JAVAC_CMD="

if exist "C:\Users\omere\AppData\Local\Programs\IntelliJ IDEA\jbr\bin\javac.exe" (
    set "JAVAC_CMD=C:\Users\omere\AppData\Local\Programs\IntelliJ IDEA\jbr\bin\javac.exe"
) else if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\javac.exe" (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
    )
)

if not defined JAVAC_CMD (
    where javac >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        set "JAVAC_CMD=javac"
    ) else (
        echo [HATA] javac derleyicisi bulunamadi! Lutfen Java JDK kurulu oldugundan emin olun.
        pause
        exit /b 1
    )
)

if not exist "bin" mkdir bin

echo Kaynak kodlar derleniyor (Java 8+ uyumlu)...
"%JAVAC_CMD%" --release 8 -Xlint:-options -encoding UTF-8 -d bin src\com\lojistik\model\*.java src\com\lojistik\algorithm\*.java src\com\lojistik\factory\*.java src\com\lojistik\observer\*.java src\com\lojistik\repository\*.java src\com\lojistik\service\*.java src\com\lojistik\ui\*.java src\com\lojistik\*.java

if %ERRORLEVEL% equ 0 (
    echo [BASARILI] Derleme tamamlandi! Sinif dosyalari 'bin' klasorune kaydedildi.
) else (
    echo [HATA] Derleme sirasinda hata olustu.
    pause
    exit /b 1
)
