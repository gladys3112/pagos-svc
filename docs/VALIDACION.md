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

El job SonarCloud se omite si faltan sus dos variables; si las variables existen pero el token no es válido o no tiene permisos, el job falla. Snyk se omite si `ENABLE_SNYK` no es exactamente `true`; al habilitarlo, también necesita un `SNYK_TOKEN` válido. No se debe declarar IE3 completamente validado hasta que ambos análisis se ejecuten y finalicen correctamente.

## Resultados comprobados

Última ejecución revisada: [GitHub Actions #65](https://github.com/gladys3112/pagos-svc/actions/runs/40596146618), commit de la rama `feature/pipeline-cicd-evaluacion` `5d0a43934ae770ca5a2518900fe6ddb0c6360729` (PR #7 abierto; no fusionado).

- **Pruebas y cobertura:** correcto. Maven ejecutó 9 pruebas; 0 fallos, 0 errores y 0 omitidas. JaCoCo informó que se cumplieron todos los límites.
- **Trivy (repositorio e imagen):** correcto, sin hallazgos bloqueantes según la configuración HIGH/CRITICAL y `--ignore-unfixed`.
- **Dependabot:** correcto únicamente como comprobación de que existe `.github/dependabot.yml`; esto no representa un análisis de alertas de Dependabot.
- **Construcción y escaneo Docker:** correctos en GitHub Actions.
- **Despliegue simulado y smoke tests:** correctos. Compose esperó al estado `healthy` y comprobó HTTP en `/` y `/api/pagos`; el entorno temporal se limpió.
- **SonarCloud:** fallido. Tras desactivar la descarga automática del JRE, el scanner usó Java 21 del runner, pero recibió HTTP 403 al consultar `https://api.sonarcloud.io/analysis/engine`. La variable de organización y el project key se entregaron al job, y el secreto `SONAR_TOKEN` no estaba vacío. Los logs no permiten determinar si el token está vencido/revocado, carece de permiso **Execute Analysis** o si la organización/proyecto no coincide con el proyecto autorizado.
- **Snyk:** omitido; el job está condicionado a `ENABLE_SNYK == 'true'` y esa condición no se cumplió.

Estos resultados corresponden a la ejecución indicada, no a pruebas locales. No se debe declarar la evaluación completamente validada mientras SonarCloud falle y Snyk siga omitido si IE3 exige ambos análisis.

## Pendiente de configuración externa

En GitHub → **Settings → Secrets and variables → Actions**:

1. En SonarCloud, comprueba que el proyecto `gladys3112_pagos-svc` pertenece a la organización `gladys3112`, que el usuario/token tiene permiso **Execute Analysis** sobre ese proyecto y que el token no está revocado ni vencido. Después reemplaza el secreto `SONAR_TOKEN` por un token válido si hace falta. No copies el token a archivos ni a logs.
2. Para ejecutar Snyk, configura el secreto `SNYK_TOKEN` y la variable `ENABLE_SNYK=true`.

Después de ajustar la configuración, vuelve a ejecutar el workflow y verifica que ambos trabajos terminen correctamente. La opción `sonar.scanner.skipJreProvisioning=true` permite usar el Java 21 instalado en el runner; no corrige permisos de SonarCloud.
