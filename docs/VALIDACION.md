# Validación técnica

## Estado y alcance

La validación del pipeline actualizado debe confirmarse en GitHub Actions para el commit más reciente de `feature/pipeline-cicd-evaluacion`. Un resultado verde de una ejecución anterior no demuestra que los nuevos trabajos hayan pasado.

## Criterios incorporados

- **IE1 — Docker:** imagen multi-stage, runtime Alpine, usuario no privilegiado, `HEALTHCHECK`, tests omitidos de la compilación de la imagen final mediante `-Dmaven.test.skip=true`, `.dockerignore` y límites/endurecimiento de Compose.
- **IE3 — análisis estático y dependencias:** trabajos SonarCloud y Snyk incorporados, condicionados a la configuración de secretos/variables del repositorio.
- **IE4/E4 — despliegue:** job explícito `Despliegue simulado y smoke tests`, posterior a la construcción y al escaneo de la imagen. Comprueba healthcheck, HTTP y publica logs/estado como artefactos.

## Configuración requerida para IE3

En GitHub → Settings → Secrets and variables → Actions, configurar:

- Secreto `SONAR_TOKEN`
- Variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY`
- Secreto `SNYK_TOKEN`
- Variable `ENABLE_SNYK=true`

Si faltan estas configuraciones, los jobs de SonarCloud y Snyk se omiten. No se debe declarar IE3 completamente validado hasta que ambos análisis se ejecuten y finalicen correctamente.

## Comprobaciones del pipeline

El workflow debe terminar con éxito en el commit actual y mostrar estos trabajos:

1. Pruebas y cobertura.
2. Análisis de seguridad con Trivy.
3. Análisis estático SonarCloud (si configurado).
4. Análisis de dependencias con Snyk (si habilitado).
5. Construcción y análisis de imagen Docker.
6. Despliegue simulado y smoke tests.

El job de despliegue levanta el servicio con Docker Compose, espera el estado `healthy`, consulta `/` y `/api/pagos`, guarda evidencias y detiene el entorno temporal. Esto acredita despliegue simulado dentro del runner, no una publicación permanente en cloud.
