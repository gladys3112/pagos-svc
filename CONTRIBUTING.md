# Guía de contribución

## Antes de comenzar

1. Actualizar `main` y `develop`.
2. Confirmar que las pruebas pasen con `./mvnw clean verify`.
3. Crear la rama desde el origen correcto.

## Ramas

- Funcionalidad: `feature/descripcion-corta`, creada desde `develop`.
- Corrección urgente: `hotfix/descripcion-corta`, creada desde `main`.
- Usar nombres en minúsculas, sin tildes y separados por guiones.

## Commits

Utilizar el formato `tipo(alcance): descripcion-corta`.

Tipos permitidos: `feat`, `fix`, `docs`, `test`, `refactor` y `chore`.

## Pull requests

- Escribir un título coherente con la convención de commits.
- Explicar qué cambió, por qué se hizo y cómo se comprobó.
- Revisar la diferencia completa antes de fusionar.
- Confirmar que GitHub Actions termine correctamente.
- Fusionar mediante merge commit para conservar la trazabilidad.
- Sincronizar `develop` después de un hotfix integrado en `main`.
