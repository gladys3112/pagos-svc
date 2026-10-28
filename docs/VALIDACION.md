# Validación técnica de CI/CD

## Alcance

Los resultados provienen de GitHub Actions. No ejecuté Maven ni Docker localmente en este entorno. Compose se despliega temporalmente dentro del runner; no publica el servicio en una nube.

El PR [#7](https://github.com/gladys3112/pagos-svc/pull/7) continúa abierto, con destino a `main`. No se fusionó ni se modificó `main`.

## Ejecuciones recientes

### CI/CD #129

[Ver ejecución #129](https://github.com/gladys3112/pagos-svc/actions/runs/40612542234), en el commit `02a0defc4470388ec1e2b73742e1cb73ab3f5db4`.

- **Maven/JaCoCo:** pasó `./mvnw --batch-mode --no-transfer-progress clean verify`; 9 pruebas, 0 fallos, 0 errores, 0 omitidas, umbrales de cobertura cumplidos.
- **Dependabot:** consulta real de alertas pasó.
- **Trivy del repositorio:** pasó con severidad HIGH/CRITICAL y `--ignore-unfixed`.
- **SonarCloud:** el scanner autenticó y subió el análisis. Al añadir `sonar.qualitygate.wait=true`, el job comprobó el resultado remoto y falló porque SonarCloud devolvió `QUALITY GATE STATUS: FAILED`.
- **Motivo del Quality Gate:** comentario de SonarCloud en el PR indica Security Rating C en código nuevo; se requiere A. Los hallazgos específicos están enlazados en el [dashboard de SonarCloud](https://sonarcloud.io/dashboard?id=gladys3112_pagos-svc&pullRequest=7).
- **Puerta IE3:** falló al no pasar SonarCloud; Snyk se omitió porque `ENABLE_SNYK` no está habilitada.
- **Imagen Docker y despliegue:** omitidos en #129 por el Quality Gate fallido. Esto es intencional; no se debe desplegar si el análisis de seguridad requerido falla.

### CI/CD #127

[Ver ejecución #127](https://github.com/gladys3112/pagos-svc/actions/runs/40612086758), commit `ef65a10975b5f6ac47353e99806c55aa6e2c8e47), anterior a hacer que Sonar espere el Quality Gate.

Maven/JaCoCo, Dependabot, Trivy del repositorio, scanner de SonarCloud, puerta IE3 del workflow, construcción y escaneo de la imagen propia, y despliegue Compose con smoke tests terminaron con éxito. **Esto no significa que el Quality Gate de SonarCloud haya pasado**: el job solo verificaba que el scanner subiera el análisis. Por ello, #127 acredita la construcción y el despliegue, pero no seguridad completa en SonarCloud.

Snyk quedó omitido; es una alternativa a Sonar en este workflow y no se activó.

## Cambios de CI/CD hechos para corregir el flujo

- El job de construcción ahora exige explícitamente que pruebas, Trivy, Dependabot y la puerta IE3 tengan éxito. Así, un Snyk omitido no cancela la cadena cuando SonarCloud sí pasa.
- El despliegue requiere explícitamente que construcción y escaneo de imagen terminen correctamente.
- SonarCloud usa `sonar.qualitygate.wait=true` para que el job espere el Quality Gate y falle si este no pasa. La opción está descrita por la [documentación de SonarQube Cloud](https://docs.sonarsource.com/sonarqube-cloud/analyzing-source-code/analysis-parameters/parameters-not-settable-in-ui).

## Configuración externa pendiente

- En SonarCloud, mantener **Automatic Analysis desactivado**; el proyecto usa análisis CI.
- `SONAR_TOKEN` está configurado como secreto de repositorio; las variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY` se leen correctamente.
- Revisar y corregir en SonarCloud los hallazgos de código nuevo que causan Security Rating C. No se debe relajar el Quality Gate para ocultar el resultado.
- Snyk es opcional. Para usarlo se necesita `ENABLE_SNYK=true` y `SNYK_TOKEN`.

## Estado frente a la pauta

- **IE1 — contenedores:** imagen construida y escaneada en #127. Falta que una ejecución completa posterior al cambio de Quality Gate repita esas comprobaciones.
- **IE2 — pruebas:** Maven/JaCoCo pasó en #129.
- **IE3 — seguridad:** Trivy y Dependabot pasaron en #129; SonarCloud Quality Gate falló por Security Rating C. Incompleto.
- **IE4 — despliegue trazable:** Compose, healthcheck, smoke tests de creación/lectura y artefactos de logs/estado pasaron en #127. En #129 el despliegue se bloqueó correctamente por el Quality Gate.
- **IE5 — orquestación:** la configuración runtime de Compose pasó en #127, con PostgreSQL saludable antes del servicio y smoke tests de la API.

No se declara validación completa: falta corregir el Quality Gate de SonarCloud y volver a obtener una ejecución completa en el commit resultante.
