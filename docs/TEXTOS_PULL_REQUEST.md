# Textos para los pull requests

Los siguientes textos están preparados para copiar en GitHub. Antes de fusionar, se debe comprobar que el PR muestre únicamente los cambios esperados.

## PR 1 — Feature de página de presentación

**Base:** `develop`  
**Compare:** `feature/pagina-presentacion`

**Título**

```text
feat(ui): agregar pie de pagina con version del servicio
```

**Descripción**

```text
## Descripción
Se agrega un pie de página a la presentación del microservicio para mostrar la versión 1.0.0 y dejar visible que el proyecto utiliza GitFlow.

## Verificación
- [x] Revisé el cambio en index.html.
- [x] Comprobé que la página mantiene sus enlaces.
- [x] Confirmé que el PR apunta a develop.
- [x] Revisé el resultado del workflow.

## Uso de IA
Se utilizó ChatGPT como apoyo para preparar la estructura inicial del cambio. El contenido fue revisado antes de publicarlo.
```

## PR 2 — Feature de changelog

**Base:** `develop`  
**Compare:** `feature/changelog`

**Título**

```text
docs: agregar changelog del microservicio pagos
```

**Descripción**

```text
## Descripción
Se incorpora CHANGELOG.md para documentar la versión inicial del microservicio y los cambios agregados durante la EP01.

## Verificación
- [x] Revisé el formato Markdown.
- [x] Confirmé que la versión registrada es la 1.0.0.
- [x] Confirmé que el PR apunta a develop.
- [x] Revisé el resultado del workflow.

## Uso de IA
Se utilizó ChatGPT como apoyo para organizar el archivo base. El contenido fue revisado antes de publicarlo.
```

## PR 3 — Hotfix del título

**Base:** `main`  
**Compare:** `hotfix/titulo-pagina`

**Título**

```text
fix(ui): corregir titulo de la pagina principal
```

**Descripción**

```text
## Descripción
Se corrige el título mostrado por el navegador para identificar claramente la página como el microservicio de pagos.

## Verificación
- [x] Revisé el elemento title de index.html.
- [x] Confirmé que el PR apunta a main porque corresponde a un hotfix.
- [x] Revisé el resultado del workflow.

## Uso de IA
Se utilizó ChatGPT como apoyo para revisar la estructura del cambio. La corrección fue validada antes de publicarla.
```

## Sincronización del hotfix

Después de fusionar el hotfix en `main`, llevar la corrección a `develop`:

```bash
git checkout main
git pull origin main
git checkout develop
git pull origin develop
git merge main
git push origin develop
```
