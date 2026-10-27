# Pipeline CI/CD de pagos-svc

## Flujo automatizado

El workflow `.github/workflows/ci-cd.yml` se ejecuta con push a `main`, `develop`, ramas `feature/**` y `hotfix/**`, pull requests hacia `main` o `develop`, y manualmente desde GitHub Actions.

1. **Pruebas y cobertura:** Java 21 ejecuta `./mvnw --batch-mode --no-transfer-progress clean verify`. JaCoCo aplica umbrales y el informe se guarda como artefacto.
2. **Dependabot:** comprueba `.github/dependabot.yml` y consulta las alertas reales de GitHub. Requiere Dependabot Alerts habilitado y permiso `security-events: read`.
3. **Trivy del repositorio:** analiza dependencias/archivos y bloquea vulnerabilidades HIGH/CRITICAL corregibles.
4. **Análisis estático/dependencias:** SonarCloud se ejecuta con `SONAR_ORGANIZATION`, `SONAR_PROJECT_KEY` y el secreto `SONAR_TOKEN`. Snyk es la alternativa al habilitar `ENABLE_SNYK=true` y `SNYK_TOKEN`.
5. **Puerta IE3:** al menos uno entre SonarCloud y Snyk debe finalizar correctamente. La construcción del servicio depende también de la verificación de Dependabot; un trabajo omitido no se considera aprobado.
6. **Construcción y análisis de imagen:** Buildx crea `pagos-svc:<SHA>` y Trivy escanea la imagen con umbral HIGH/CRITICAL.
7. **Despliegue simulado:** Docker Compose inicia PostgreSQL y el microservicio, espera el healthcheck y ejecuta una prueba HTTP y una operación de creación/lectura de pago. Publica el estado y los logs como artefactos por 14 días; luego detiene el entorno y elimina el volumen temporal.

## Trazabilidad

El SHA completo del commit se utiliza como etiqueta de imagen. El workflow publica los resultados Maven/JaCoCo y las evidencias del despliegue dentro de GitHub Actions. Este despliegue solo existe durante la ejecución del runner; no publica el servicio en AWS, Azure u otra nube.

## Seguridad y recursos

- El workflow limita los permisos predeterminados de GitHub Actions a lectura del repositorio; el job Dependabot agrega `security-events: read` para consultar alertas.
- Trivy hace fallar el pipeline ante vulnerabilidades HIGH/CRITICAL corregibles en el repositorio y en la imagen propia `pagos-svc`.
- PostgreSQL utiliza la imagen oficial `postgres:17-alpine` como dependencia de Compose. El workflow no escanea esa imagen de terceros; el escaneo de imagen cubre el artefacto propio de la aplicación.
- El contenedor de la aplicación corre como usuario no privilegiado, con filesystem de solo lectura, sin capabilities, `no-new-privileges`, límites de CPU/memoria/PIDs y un `tmpfs` temporal.
- Compose espera que PostgreSQL esté saludable antes de iniciar la aplicación y conserva sus datos en un volumen nombrado durante ejecuciones locales.

## Ejecución local

Requisitos: Docker Engine y Docker Compose v2.

```bash
docker compose up --build -d
docker compose ps
curl --fail http://localhost:8080/
curl --fail http://localhost:8080/api/pagos
docker compose logs --follow
docker compose down
```

En Compose la aplicación usa PostgreSQL. Al ejecutar el servicio directamente con Maven, se usa H2 en memoria por defecto; la consola H2 solo está disponible en esa ejecución directa y no en Compose.

Para ejecutar las pruebas:

- Linux/macOS: `./mvnw --batch-mode --no-transfer-progress clean verify`
- Windows: `mvnw.cmd --batch-mode --no-transfer-progress clean verify`

## Configuración de GitHub

En **Settings → Code security and analysis**, habilita Dependabot Alerts y el dependency graph. En **Settings → Secrets and variables → Actions**, configura los valores de SonarCloud o Snyk indicados arriba. No guardes credenciales en el repositorio.

## Validación

Los resultados reales, los bloqueos de configuración y la correspondencia con IE1–IE5 se mantienen en [VALIDACION.md](VALIDACION.md). No consideres completa la evaluación hasta que una ejecución del workflow pase Dependabot, una de las herramientas SonarCloud/Snyk, la construcción/escaneo Docker y el despliegue simulado.
