# Validación técnica de CI/CD

## Alcance

La evidencia de esta página proviene de GitHub Actions. No se ejecutaron Maven ni Docker localmente en este entorno. El despliegue definido por el workflow es temporal y simulado dentro del runner de GitHub Actions; no publica el servicio en una nube.

## Evidencia más reciente

Workflow [CI/CD #119](https://github.com/gladys3112/pagos-svc/actions/runs/40607576551), asociado al PR [#7](https://github.com/gladys3112/pagos-svc/pull/7), desde `feature/pipeline-cicd-evaluacion` hacia `main`. El PR permanece abierto y sin fusionar; no se modificó `main`.

Después de desactivar Automatic Analysis en SonarCloud, el scanner terminó correctamente y la puerta de análisis IE3 del workflow pasó. Sin embargo, el comentario de SonarCloud en el PR informó que su **Quality Gate falló**: la condición **Security Rating on New Code** obtuvo C y requiere A. El workflow actual comprueba que el scanner envíe el análisis, pero no falla cuando el Quality Gate remoto es negativo. Por eso, IE3 no se considera completamente validado hasta revisar y corregir los hallazgos en SonarCloud.

- **Maven y JaCoCo:** pasó `./mvnw --batch-mode --no-transfer-progress clean verify`; 9 pruebas, 0 fallos, 0 errores y 0 omitidas. Se cumplieron los umbrales configurados de JaCoCo.
- **Trivy del repositorio:** pasó con severidad HIGH/CRITICAL y `--ignore-unfixed`.
- **Dependabot:** pasó la consulta real de alertas. Una comprobación previa registró 0 alertas abiertas.
- **SonarCloud scanner:** pasó con Automatic Analysis desactivado y análisis basado en CI.
- **SonarCloud Quality Gate:** falló por Security Rating C en código nuevo (se requiere A). Los detalles de los hallazgos deben revisarse en el dashboard de SonarCloud.
- **Puerta IE3 del workflow:** pasó porque el job considera éxito el envío del análisis, sin comprobar el Quality Gate remoto.
- **Snyk:** omitido; `ENABLE_SNYK` no está configurada como `true`. Es una alternativa opcional a Sonar en el workflow actual.
- **Construcción/escaneo de imagen Docker y despliegue simulado:** omitidos en los reintentos del workflow #119. GitHub reejecutó trabajos fallidos, pero no toda la cadena dependiente. Esta ejecución no valida esos criterios.

La ejecución [CI/CD #93](https://github.com/gladys3112/pagos-svc/actions/runs/40601508495), en otro commit, validó la construcción y el escaneo de la imagen propia `pagos-svc`. No valida el despliegue Compose del commit actual. En esa ejecución histórica, un escaneo adicional de la imagen oficial PostgreSQL detectó CVE upstream de severidad alta/crítica y el despliegue fue omitido. La imagen PostgreSQL se usa en Compose, pero no forma parte del escaneo de imagen propio definido en el workflow actual.

## Configuración externa

- **SonarCloud:** el proyecto usa análisis desde CI. Automatic Analysis debe permanecer desactivado para evitar conflicto con el scanner de GitHub Actions. El Quality Gate requiere revisar sus hallazgos de seguridad nuevos y corregirlos en el código.
- **GitHub Actions:** `SONAR_TOKEN` está configurado a nivel de repositorio. Las variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY` están siendo leídas por el workflow.
- **Snyk:** opcional. Para activarlo, se requiere `ENABLE_SNYK=true` y el secreto `SNYK_TOKEN`.

## Estado frente a la pauta

- **IE1 — contenedores:** Dockerfile multi-stage, runtime Java 21 Alpine, usuario no root y healthcheck. La imagen propia se construyó y escaneó con éxito en #93; falta repetirlo en una ejecución actual.
- **IE2 — pruebas:** validado en #119; 9 pruebas y cobertura JaCoCo pasaron.
- **IE3 — seguridad:** Trivy del repositorio y Dependabot pasaron. El scanner de SonarCloud y la puerta técnica pasaron, pero el Quality Gate de SonarCloud falló por Security Rating C en código nuevo. IE3 queda incompleto hasta resolver ese hallazgo y volver a comprobar el Quality Gate.
- **IE4 — despliegue trazable:** el workflow define Compose, SHA del commit, healthcheck, smoke test de creación/lectura y artefactos de estado/logs. Aún falta una ejecución completa que demuestre el despliegue del commit actual.
- **IE5 — orquestación:** Compose define la aplicación y PostgreSQL con dependencia por healthcheck, volumen persistente y límites de recursos. La ejecución runtime de esta configuración aún debe comprobarse.

No se afirma una validación completa: falta resolver el Quality Gate de SonarCloud y obtener una ejecución completa con construcción/escaneo de imagen y despliegue simulado exitosos.
