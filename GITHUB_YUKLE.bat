@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo ===================================================
echo         GitHub Otomatik Yükleme Aracı
echo ===================================================
echo.

set "REPO_URL="
if exist "BURAYA_GITHUB_LINKINI_YAZ.txt" (
    set /p REPO_URL=<"BURAYA_GITHUB_LINKINI_YAZ.txt"
)

if "%REPO_URL%"=="" (
    echo [UYARI] 'BURAYA_GITHUB_LINKINI_YAZ.txt' dosyası bulunamadı veya boş.
    echo.
    set /p REPO_URL="GitHub Repo Linkinizi Yapıştırın: "
)

echo.
echo [1/3] Hedef Repo: %REPO_URL%
echo.
echo [2/3] Dosyalar hazırlanıyor...
git branch -M main
git remote remove origin >nul 2>&1
git remote add origin %REPO_URL%

echo.
echo [3/3] Kodlar GitHub'a yükleniyor (Lütfen bekleyin)...
git push -u origin main

if %ERRORLEVEL% equ 0 (
    echo.
    echo ===================================================
    echo   [BAŞARILI] Kodlar GitHub'a başarıyla yüklendi!
    echo ===================================================
) else (
    echo.
    echo ===================================================
    echo   [BİLGİ] Eğer giriş penceresi açıldıysa tarayıcıdan GitHub ile giriş yapın.
    echo ===================================================
)

echo.
echo Pencereyi kapatmak için herhangi bir tuşa basınız...
pause > nul
