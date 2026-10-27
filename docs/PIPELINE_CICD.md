# Pipeline CI/CD de pagos-svc

## Flujo automatizado

El workflow `.github/workflows/ci-cd.yml` se ejecuta en push a `main`, `develop`, ramas `feature/**` y `hotfix/**`, al abrir pull requests hacia `main` o `develop`, y manualmente desde GitHub Actions.

1. **Pruebas y cobertura:** Java 21 ejecuta `./mvnw --batch-mode --no-transfer-progress clean verify`; JaCoCo verifica la cobertura y publica el informe como artefacto.
2. **Dependabot:** el workflow confirma que existe `.github/dependabot.yml`; las alertas y actualizaciones de Dependabot se administran en GitHub.
3. **Trivy para dependencias:** analiza el repositorio y bloquea vulnerabilidades HIGH/CRITICAL corregibles según `--ignore-unfixed`.
4. **SonarCloud:** ejecuta el análisis si están configuradas las variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY`; requiere el secreto `SONAR_TOKEN`. Usa Java 21 del runner sin aprovisionar otro JRE.
5. **Snyk:** ejecuta el análisis Maven solo si `ENABLE_SNYK=true`; requiere el secreto `SNYK_TOKEN`.
6. **Construcción y seguridad de imagen:** Buildx construye la imagen con el SHA del commit y Trivy escanea sus vulnerabilidades antes del despliegue.
7. **Despliegue simulado:** Docker Compose levanta el contenedor en el runner, espera el estado `healthy` y comprueba las rutas HTTP `/` y `/api/pagos`. Al terminar, el entorno temporal se detiene.
8. **Trazabilidad:** se publican el informe JaCoCo y los logs/estado del despliegue como artefactos durante 14 días.

## Seguridad y recursos

- El workflow declara permisos mínimos de lectura del repositorio por defecto.
- No necesita credenciales cloud ni secretos para el despliegue simulado.
- Trivy funciona como control de seguridad que bloquea la entrega si detecta vulnerabilidades HIGH/CRITICAL corregibles.
- Docker Compose establece límites de memoria y CPU para el servicio.
- El contenedor se ejecuta con un usuario no privilegiado.
- La etiqueta de imagen usa `${{ github.sha }}` para identificar el código probado y desplegado.

## Ejecutar localmente

Requisitos: Docker Engine/Desktop y Docker Compose v2.

```bash
docker compose up --build -d
docker compose ps
curl --fail http://localhost:8080/
curl --fail http://localhost:8080/api/pagos
docker compose logs --follow
docker compose down
```

Para ejecutar pruebas localmente:

- Linux/macOS: `./mvnw --batch-mode clean verify`
- Windows: `mvnw.cmd --batch-mode clean verify`

## Ejecutar y revisar en GitHub

1. Abre la pestaña **Actions** del repositorio.
2. Selecciona **CI/CD - pagos-svc**.
3. Ejecuta el workflow manualmente con **Run workflow**, o crea un push/pull request.
4. Revisa los jobs **Pruebas y cobertura**, **Análisis de seguridad** y **Construcción y despliegue simulado**.
5. Descarga los artefactos `informe-jacoco` y `evidencia-despliegue-<commit>`.

## Alcance del entorno simulado

El despliegue se ejecuta en un runner temporal de GitHub Actions y no en una cuenta cloud pública. Esto demuestra automatización de despliegue en un entorno contenerizado simulado. Si la asignatura exige un proveedor específico (AWS/Azure), se debe adaptar el último job al servicio solicitado y configurar secretos protegidos en GitHub Environments.

## Escalabilidad y orquestación

Docker Compose coordina el servicio y aplica límites de recursos al contenedor. Para múltiples réplicas en un entorno real, se debe usar un orquestador compatible (por ejemplo Kubernetes) o Docker Swarm; la sección `deploy.replicas` de Compose no se considera escalado efectivo en el despliegue local normal.
