$ErrorActionPreference = "Stop"
Set-Location (Split-Path -Parent $PSScriptRoot)

$ramas = @(
    "main",
    "develop",
    "feature/pagina-presentacion",
    "feature/changelog",
    "hotfix/titulo-pagina"
)

Write-Host "Ramas requeridas"
foreach ($rama in $ramas) {
    git show-ref --verify --quiet "refs/heads/$rama"
    if ($LASTEXITCODE -ne 0) {
        throw "Falta la rama $rama"
    }
    Write-Host "OK  $rama"
}

Write-Host "Verificacion del repositorio completada."
