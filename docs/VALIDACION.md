# Validación técnica de CI/CD

## Alcance

Esta validación usa resultados de GitHub Actions. No se ejecutaron Maven ni Docker localmente en este entorno. El despliegue descrito es temporal y simulado dentro del runner de GitHub Actions; no publica el servicio en una nube.

## Evidencia más reciente

Workflow [CI/CD #109](https://github.com/gladys3112/pagos-svc/actions/runs/40603818745), ejecutado sobre el commit `ee9e5fc394626dbd2363ca1e8dad17e7b891ecbc` de `feature/pipeline-cicd-evaluacion`. PR [#7](https://github.com/gladys3112/pagos-svc/pull/7) permanece abierto y sin fusionar; `main` no fue modificado.

- **Maven y JaCoCo:** pasó `./mvnw --batch-mode --no-transfer-progress clean verify`; 9 pruebas, 0 fallos, 0 errores y 0 omitidas. Los umbrales configurados de JaCoCo se cumplieron.
- **Trivy del repositorio:** pasó con severidad HIGH/CRITICAL y `--ignore-unfixed`.
- **Dependabot:** falló la consulta real de alertas con HTTP 403: GitHub respondió `Dependabot alerts are disabled for this repository`. La existencia de `.github/dependabot.yml` por sí sola no acredita que las alertas estén habilitadas.
- **SonarCloud:** falló con HTTP 403 al solicitar `https://api.sonarcloud.io/analysis/engine`. El secreto `SONAR_TOKEN` está presente, pero eso no demuestra que el token tenga permiso **Execute Analysis** ni que corresponda a la organización/proyecto configurados.
- **Snyk:** omitido porque `ENABLE_SNYK` no es exactamente `true`.
- **Puerta IE3:** falló, correctamente, porque ningún análisis configurado terminó con éxito.
- **Construcción/escaneo de imagen Docker y despliegue simulado:** omitidos por la puerta IE3; esta ejecución no los valida.

Ejecución anterior [CI/CD #93](https://github.com/gladys3112/pagos-svc/actions/runs/40601508495), commit `b348fd832ad0327dbc13c125de4bfd773d85f27b`): pruebas, cobertura, Trivy del repositorio y construcción/escaneo de la imagen propia `pagos-svc` pasaron. El escaneo adicional de la imagen oficial PostgreSQL falló al detectar 26 CVE de severidad alta/crítica en el binario upstream `gosu`; por dependencia de ese paso, el despliegue se omitió. La imagen oficial PostgreSQL se usa en Compose, pero no forma parte del escaneo de imagen propio que exige este workflow. No se deben interpretar los resultados de #93 como validación del despliegue actual.

## Configuración externa pendiente

En GitHub → **Settings → Code security and analysis**:

1. Habilitar **Dependabot alerts** y, si está desactivado, el **dependency graph**. La verificación del workflow debe poder consultar `GET /repos/{owner}/{repo}/dependabot/alerts`.
2. En **Settings → Secrets and variables → Actions**, corregir la autorización de SonarCloud: revisar `SONAR_ORGANIZATION`, `SONAR_PROJECT_KEY` y reemplazar `SONAR_TOKEN` por un token vigente con permiso **Execute Analysis** para ese proyecto. No copiar el token a archivos ni logs.
3. Como alternativa al análisis de SonarCloud, configurar el secreto `SNYK_TOKEN` y la variable `ENABLE_SNYK=true`. La pauta pide SonarQube o Snyk; el workflow exige que al menos uno pase.

Tras habilitar las alertas y resolver uno de los análisis, volver a ejecutar el workflow. Solo una nueva ejecución que pase la puerta IE3, construya y escanee la imagen del servicio y complete los smoke tests de Compose permitirá validar los criterios IE3 e IE4 para el commit actual.

## Estado frente a la pauta

- **IE1 — contenedores:** Dockerfile multi-stage, runtime Java 21 Alpine, usuario no root y healthcheck. La imagen propia se construyó y escaneó con éxito en #93; falta repetirlo en una ejecución posterior a la puerta IE3.
- **IE2 — pruebas:** evidencia completa en #109; las pruebas y cobertura JaCoCo pasaron.
- **IE3 — seguridad:** incompleto. Trivy del repositorio pasó, pero Dependabot Alerts está desactivado y SonarCloud falla; Snyk se omitió.
- **IE4 — despliegue trazable:** workflow definido con Compose, SHA del commit, healthcheck, smoke test de creación/lectura y artefactos de estado/logs. El despliegue actual no se ejecutó porque la puerta IE3 falló.
- **IE5 — orquestación:** Compose define la aplicación y PostgreSQL con dependencia por healthcheck, volumen persistente y límites de recursos. La ejecución runtime de esta configuración aún no quedó comprobada en una ejecución válida del workflow.

No se puede afirmar una validación completa ni asegurar el puntaje máximo mientras las tareas externas estén pendientes y no exista evidencia de imagen y despliegue en la ejecución actual.
