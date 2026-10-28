# Validación técnica de CI/CD

## Alcance y estado

Los resultados indicados aquí proceden de GitHub Actions, no de una ejecución local. El despliegue Compose es temporal dentro del runner: no publica el servicio en una nube.

La rama validada es `feature/pipeline-cicd-evaluacion`. El PR [#7](https://github.com/gladys3112/pagos-svc/pull/7) sigue abierto y apunta a `main`; no se fusionó ni se modificó `main`.

## Ejecución completa más reciente

[CI/CD #146](https://github.com/gladys3112/pagos-svc/actions/runs/40616790131), commit `44f072be78eb2e7f8a721b442afa301551a20f38`.

| Comprobación | Resultado observado |
| --- | --- |
| Maven y JaCoCo | Éxito con `./mvnw --batch-mode --no-transfer-progress clean verify`; 9 pruebas, 0 fallos, 0 errores, 0 omitidas y umbrales de cobertura cumplidos. |
| Trivy del repositorio | Éxito; escaneo de vulnerabilidades HIGH/CRITICAL con `--ignore-unfixed`. |
| Dependabot | Éxito; workflow confirmó su configuración y consultó alertas abiertas. |
| SonarCloud | Éxito; el job esperó y comprobó el Quality Gate remoto. |
| Puerta IE3 | Éxito; SonarCloud pasó. |
| Snyk | Omitido: `ENABLE_SNYK` no está habilitada. Es opcional en esta configuración y no bloquea cuando SonarCloud pasa. |
| Construcción Docker y Trivy de imagen | Éxito; imagen construida y escaneo HIGH/CRITICAL completado. |
| Despliegue simulado | Éxito; Compose desplegó en el runner, guardó estado/logs, publicó artefactos de evidencia y detuvo el entorno temporal. Los smoke tests completaron correctamente. |

El comentario de SonarCloud en el [PR #7](https://github.com/gladys3112/pagos-svc/pull/7) confirma Quality Gate aprobado, 0 incidencias nuevas y 0 hotspots nuevos. El mismo comentario reporta **3 riesgos de dependencias nuevas**. Intenté consultar el informe de detalle SCA desde CI y la API devolvió HTTP 403; esto no afectó el análisis ni el Quality Gate. No cambié versiones a ciegas: hay que revisar nombre, versión, severidad y recomendación de corrección en el [panel de riesgos de SonarCloud](https://sonarcloud.io/dependency-risks?id=gladys3112_pagos-svc&pullRequest=7), y después volver a ejecutar el análisis para confirmar que se resolvieron.

## Correcciones verificadas en el workflow

- Se fijaron las acciones Snyk y Docker a SHA completos de versiones publicadas, en respuesta a los hallazgos de Sonar sobre referencias mutables.
- SonarCloud usa `sonar.qualitygate.wait=true`; el job falla si el Quality Gate remoto no pasa.
- Construcción y despliegue comprueban explícitamente el resultado de sus dependencias. Un Snyk omitido no bloquea la ruta si SonarCloud pasa; el despliegue sí requiere que construcción y escaneo de imagen tengan éxito.

## Configuración externa

- `SONAR_TOKEN` está configurado como secreto de repositorio y las variables `SONAR_ORGANIZATION` y `SONAR_PROJECT_KEY` permiten ejecutar el análisis. No se necesita cambiar esas credenciales a raíz del error HTTP 403 anterior: la ejecución #137 autenticó y pasó SonarCloud.
- Mantener desactivado **Automatic Analysis** en SonarCloud porque el análisis se ejecuta desde CI.
- Snyk no se ejecutó. Para habilitarlo, configura la variable de Actions `ENABLE_SNYK=true` y el secreto `SNYK_TOKEN`; no es requisito de la ruta actual si SonarCloud pasa.

## Correspondencia con la pauta

- **IE1 — contenedores:** construcción y análisis de vulnerabilidades de la imagen pasaron en #146.
- **IE2 — pruebas:** Maven y cobertura JaCoCo pasaron en #146.
- **IE3 — seguridad:** Trivy de repositorio, consulta Dependabot y Quality Gate SonarCloud pasaron en #146. Snyk se omitió por estar deshabilitado; SonarCloud reporta 3 riesgos de dependencias nuevas que requieren revisión.
- **IE4 — despliegue trazable:** despliegue Compose temporal, smoke tests, artefactos de estado/logs y limpieza pasaron en #146.
- **IE5 — orquestación:** el despliegue Compose y sus comprobaciones de salud/dependencias pasaron en #146.

No se afirma que Snyk haya pasado ni que el despliegue sea permanente.