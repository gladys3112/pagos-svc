# Registro de trazabilidad

## Flujo planificado

| N.° | Origen | Rama de trabajo | Destino | Resultado esperado |
| --- | --- | --- | --- | --- |
| 1 | `develop` | `feature/pagina-presentacion` | `develop` | Pie de página integrado mediante PR |
| 2 | `develop` | `feature/changelog` | `develop` | Changelog integrado mediante PR |
| 3 | `main` | `hotfix/titulo-pagina` | `main` | Título corregido mediante PR |
| 4 | `main` | — | `develop` | Hotfix sincronizado en la rama de integración |

## Comandos utilizados

```bash
git clone <URL_DEL_REPOSITORIO>
git checkout develop
git pull origin develop
git checkout -b feature/pagina-presentacion
git add src/main/resources/static/index.html
git commit -m "feat(ui): agregar pie de pagina con version del servicio"
git push -u origin feature/pagina-presentacion
```

El mismo flujo se repite para `feature/changelog`. El hotfix se crea desde `main`, se integra a `main` mediante pull request y después se sincroniza hacia `develop`.

## Evidencia que debe conservarse

- Vista de las ramas publicadas.
- Historial de commits.
- PR de cada feature con su destino `develop`.
- PR del hotfix con su destino `main`.
- Resultado verde de GitHub Actions.
- Registro de `mvn test` y `mvn verify`.
