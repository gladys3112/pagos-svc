# Publicación y creación de evidencias en GitHub

El repositorio local ya contiene las ramas y los commits preparados. Para conservar la posibilidad de crear pull requests reales, no se deben fusionar las ramas antes de publicarlas.

## 1. Configurar la identidad

Usar el mismo nombre y correo asociados a la cuenta de GitHub:

```bash
git config user.name "TU NOMBRE"
git config user.email "TU CORREO DE GITHUB"
```

## 2. Crear el repositorio remoto

En GitHub, crear un repositorio vacío llamado `pagos-svc`. No agregar README, licencia ni `.gitignore` desde la página.

## 3. Conectar el repositorio

```bash
git remote add origin https://github.com/USUARIO/pagos-svc.git
```

Si ya existe un remoto incorrecto:

```bash
git remote set-url origin https://github.com/USUARIO/pagos-svc.git
```

## 4. Subir todas las ramas preparadas

```bash
git push -u origin main
git push -u origin develop
git push -u origin feature/pagina-presentacion
git push -u origin feature/changelog
git push -u origin hotfix/titulo-pagina
```

## 5. Crear los pull requests

Abrirlos en este orden:

1. `feature/pagina-presentacion` hacia `develop`.
2. `feature/changelog` hacia `develop`.
3. `hotfix/titulo-pagina` hacia `main`.

Los títulos y descripciones están en `docs/TEXTOS_PULL_REQUEST.md`.

Como el trabajo es individual, GitHub no permite aprobar un PR propio. Antes del merge, dejar constancia de la auto-revisión mediante el checklist, revisar los cambios y esperar que las comprobaciones automáticas finalicen.

## 6. Sincronizar el hotfix

Después de fusionar el hotfix en `main`:

```bash
git checkout main
git pull origin main
git checkout develop
git pull origin develop
git merge main
git push origin develop
```

## 7. Conservar las ramas para la evidencia

No borrar las ramas `feature/*` ni `hotfix/*` hasta obtener la captura de ramas y completar la revisión del docente.

## 8. Guardar las evidencias

Seguir la lista de `docs/evidencias/README.md` y subir las capturas mediante un commit de documentación en `develop`.

## 9. Entregar

Copiar la dirección `https://github.com/USUARIO/pagos-svc` y enviarla mediante AVA y al correo del docente.
