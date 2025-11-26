# run.ps1 - Compila y ejecuta el juego desde PowerShell (Windows)
# Uso: ./run.ps1
# Requisitos: JDK instalado y `javac`/`java` en el PATH.

$src = "src"
$out = "out"

if (!(Test-Path $out)) {
    New-Item -ItemType Directory -Path $out | Out-Null
}

Write-Host "Compilando fuentes Java..." -ForegroundColor Cyan
# Obtenemos todos los archivos .java del src recursively
$files = Get-ChildItem -Recurse -Filter *.java -Path $src | ForEach-Object { $_.FullName }

if ($files.Count -eq 0) {
    Write-Error "No se encontraron archivos .java en la carpeta 'src'. Asegúrate de ejecutar desde la raíz del repo."
    exit 1
}

# Compilar
& javac -d $out @files
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilación fallida. Revisa los errores anteriores."
    exit $LASTEXITCODE
}

Write-Host "Compilación OK. Ejecutando el juego..." -ForegroundColor Green
# Ejecutar la clase Main. Para activar debug usa: java -Dgame.debug=true -cp out Main
& java -cp $out Main

# Fin

