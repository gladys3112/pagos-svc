#!/usr/bin/env bash
set -euo pipefail

script_dir=$(CDPATH= cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
cd "$script_dir/.."

required_branches=(
  main
  develop
  feature/pagina-presentacion
  feature/changelog
  hotfix/titulo-pagina
)

required_files=(
  README.md
  CHANGELOG.md
  .github/workflows/hola-mundo.yml
  src/main/resources/static/index.html
)

echo "Ramas requeridas"
for branch in "${required_branches[@]}"; do
  git show-ref --verify --quiet "refs/heads/$branch"
  echo "OK  $branch"
done

echo "Archivos requeridos en alguna rama"
for file in "${required_files[@]}"; do
  found=false
  for branch in "${required_branches[@]}"; do
    if git cat-file -e "$branch:$file" 2>/dev/null; then
      found=true
      break
    fi
  done

  if [ "$found" = true ]; then
    echo "OK  $file"
  else
    echo "FALTA  $file" >&2
    exit 1
  fi
done

echo "Verificacion del repositorio completada."
