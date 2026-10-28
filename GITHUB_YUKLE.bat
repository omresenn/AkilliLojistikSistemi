@echo off
chcp 65001 > nul
echo ===================================================
echo     GitHub Otomatik Yükleme Aracı
echo ===================================================
echo.
echo 1) https://github.com/new adresine git.
echo 2) Repository name kısmına 'AkilliLojistikSistemi' yaz ve 'Create repository' butonuna bas.
echo.
set /p REPO_URL="GitHub Repo Linkinizi Yapıştırın (Örn: https://github.com/omresenn/AkilliLojistikSistemi.git): "

if "%REPO_URL%"=="" (
    echo [HATA] Link girmediniz.
    pause
    exit /b
)

git branch -M main
git remote remove origin >nul 2>&1
git remote add origin %REPO_URL%
git push -u origin main

echo.
echo ===================================================
echo GitHub'a yükleme tamamlandı!
echo ===================================================
pause
