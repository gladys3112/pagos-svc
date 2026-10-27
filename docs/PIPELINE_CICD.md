# Pipeline CI/CD de pagos-svc

## Flujo automatizado

El workflow `.github/workflows/ci-cd.yml` se ejecuta en push a `main`, `develop`, ramas `feature/**` y `hotfix/**`, al abrir pull requests hacia `main` o `develop`, y manualmente desde GitHub Actions.

1. **Pruebas:** Java 21 y Maven ejecutan `./mvnw clean verify`; el build falla si fallan las pruebas o la regla de cobertura configurada en `pom.xml`. El informe JaCoCo se guarda como artefacto.
2. **Seguridad:** Trivy analiza dependencias y sistema de archivos; bloquea el pipeline ante vulnerabilidades HIGH o CRITICAL conocidas que tengan corrección disponible.
3. **Construcción:** Docker Buildx construye una imagen identificada por el SHA completo del commit.
4. **Escaneo de imagen:** Trivy revisa las dependencias y paquetes de la imagen antes del despliegue.
5. **Despliegue simulado:** Docker Compose levanta el contenedor en el runner de GitHub Actions. Se comprueba una respuesta HTTP y la API de pagos. Al terminar, el entorno se detiene.
6. **Trazabilidad:** los logs del despliegue y el informe JaCoCo se conservan como artefactos de la ejecución durante 14 días.

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
