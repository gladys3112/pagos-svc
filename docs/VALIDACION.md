# Validación técnica de CI/CD

## Alcance

La evidencia de esta página proviene de GitHub Actions. No se ejecutaron Maven ni Docker localmente en este entorno. El despliegue definido por el workflow es temporal y simulado dentro del runner de GitHub Actions; no publica el servicio en una nube.

## Evidencia más reciente

Workflow [CI/CD #119](https://github.com/gladys3112/pagos-svc/actions/runs/40607576551), asociado al PR [#7](https://github.com/gladys3112/pagos-svc/pull/7), desde `feature/pipeline-cicd-evaluacion` hacia `main`. El PR permanece abierto y sin fusionar; no se modificó `main`.

En el reintento realizado después de cambiar la configuración de análisis en SonarCloud:

- **Maven y JaCoCo:** pasó `./mvnw --batch-mode --no-transfer-progress clean verify`; 9 pruebas, 0 fallos, 0 errores y 0 omitidas. Se cumplieron los umbrales configurados de JaCoCo.
- **Trivy del repositorio:** pasó con severidad HIGH/CRITICAL y `--ignore-unfixed`.
- **Dependabot:** pasó la consulta real de alertas. La comprobación anterior registró 0 alertas abiertas.
- **SonarCloud:** pasó después de desactivar Automatic Analysis en el proyecto SonarCloud y ejecutar el análisis desde GitHub Actions.
- **Puerta IE3:** pasó al completarse SonarCloud con éxito.
- **Snyk:** omitido; la variable `ENABLE_SNYK` no está configurada como `true`. No es necesario para satisfacer la puerta actual porque SonarCloud pasó.
- **Construcción/escaneo de imagen Docker y despliegue simulado:** quedaron omitidos en este reintento. GitHub reintentó los trabajos que habían fallado y no reejecutó automáticamente toda la cadena dependiente. Por tanto, esta ejecución no valida esos criterios.

La ejecución [CI/CD #93](https://github.com/gladys3112/pagos-svc/actions/runs/40601508495), en otro commit, validó la construcción y el escaneo de la imagen propia `pagos-svc`. No valida el despliegue Compose del commit actual. En esa ejecución histórica, un escaneo adicional de la imagen oficial PostgreSQL detectó 26 CVE upstream de severidad alta/crítica y el despliegue fue omitido. La imagen PostgreSQL se usa en Compose, pero no forma parte del escaneo de imagen propio definido en el workflow actual.

## Configuración externa

- **SonarCloud:** el proyecto usa análisis desde CI. Automatic Analysis debe permanecer desactivado para evitar el conflicto con el scanner de GitHub Actions.
- **GitHub Actions:** el secreto `SONAR_TOKEN` está configurado a nivel de repositorio. Las variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY` están siendo leídas correctamente por el workflow.
- **Snyk:** opcional en la configuración actual. Para activarlo, se requiere `ENABLE_SNYK=true` y el secreto `SNYK_TOKEN`.

Se requiere una ejecución nueva completa para validar la construcción/escaneo de la imagen y los smoke tests de Compose después de que la puerta IE3 pase.

## Estado frente a la pauta

- **IE1 — contenedores:** Dockerfile multi-stage, runtime Java 21 Alpine, usuario no root y healthcheck. La imagen propia se construyó y escaneó con éxito en #93; falta repetirlo en una ejecución actual posterior a la puerta IE3.
- **IE2 — pruebas:** validado en #119; 9 pruebas y cobertura JaCoCo pasaron.
- **IE3 — seguridad:** SonarCloud, Trivy del repositorio, Dependabot y la puerta de análisis pasaron en #119. Snyk se omitió.
- **IE4 — despliegue trazable:** el workflow define Compose, SHA del commit, healthcheck, smoke test de creación/lectura y artefactos de estado/logs. Aún falta una ejecución completa que demuestre el despliegue en el commit actual.
- **IE5 — orquestación:** Compose define la aplicación y PostgreSQL con dependencia por healthcheck, volumen persistente y límites de recursos. La ejecución runtime de esta configuración aún debe comprobarse.

No se afirma una validación completa hasta que una ejecución completa pase la construcción/escaneo de imagen y el despliegue simulado.
