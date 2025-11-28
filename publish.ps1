# publish.ps1 - Inicializa repo Git y hace push a GitHub (PowerShell)
# Uso: ./publish.ps1 <github-repo-url>
# Ejemplo: ./publish.ps1 https://github.com/sgomezmalagon/Juego_Bolas.git

param(
    [string]$remoteUrl = "https://github.com/sgomezmalagon/Juego_Bolas.git"
)

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Write-Error "git no está instalado o no está en el PATH. Instala git antes de continuar."
    exit 1
}

# Inicializar repo si no existe
if (!(Test-Path .git)) {
    git init
    Write-Host "Repositorio git inicializado." -ForegroundColor Green
}

# Añadir remoto si no existe
$remotes = git remote
if (-not ($remotes -match "origin")) {
    git remote add origin $remoteUrl
    Write-Host "Remoto 'origin' añadido: $remoteUrl" -ForegroundColor Green
}

# Añadir, commit y push
git add .
try {
    git commit -m "Initial commit: Juego_Bolas"
} catch {
    Write-Host "No se ha realizado commit (posible falta de cambios o autor no configurado)." -ForegroundColor Yellow
}

Write-Host "Haciendo push a origin main..." -ForegroundColor Cyan
git branch -M main
git push -u origin main

Write-Host "Push completado (si las credenciales son correctas)." -ForegroundColor Green

