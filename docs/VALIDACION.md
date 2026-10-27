# Validación técnica

## Alcance y estado

Este documento separa las comprobaciones funcionales previamente registradas para el microservicio de la validación del pipeline CI/CD actual.

**Estado del pipeline:** pendiente de confirmar mediante una ejecución real en GitHub Actions. La presencia de los pasos en el archivo de workflow no demuestra que hayan finalizado correctamente.

## Pruebas automatizadas y funcionales previamente documentadas

La documentación previa de este proyecto registra una ejecución de:

```bash
./mvnw clean verify
```

Y los siguientes resultados declarados en esa validación anterior:

- 9 pruebas ejecutadas.
- 0 fallos, 0 errores y 0 pruebas omitidas.
- Verificación de cobertura JaCoCo cumplida.
- Construcción Maven finalizada correctamente.

También se registraron las siguientes pruebas funcionales de la API:

| Prueba | Resultado registrado anteriormente |
| --- | --- |
| Abrir la página de presentación | HTTP 200 |
| Abrir Swagger UI | HTTP 200 |
| Listar pagos inicialmente | Respuesta `[]` |
| Registrar un pago | Pago creado con id `1` |
| Listar después de registrar | Pago presente en la respuesta |
| Eliminar el pago | HTTP 204 |
| Listar después de eliminar | Respuesta `[]` |

Estos resultados se conservan como evidencia histórica del proyecto; no se han vuelto a ejecutar como parte de esta actualización documental.

## Pipeline CI/CD configurado

El workflow `.github/workflows/ci-cd.yml` contempla:

1. **Pruebas y cobertura:** Java 21, `./mvnw --batch-mode --no-transfer-progress clean verify` y publicación del informe JaCoCo.
2. **Seguridad:** análisis de archivos/dependencias con Trivy para vulnerabilidades HIGH y CRITICAL.
3. **Construcción:** creación de la imagen Docker del microservicio.
4. **Seguridad de imagen:** análisis de la imagen construida con Trivy.
5. **Despliegue simulado:** ejecución mediante Docker Compose, comprobación HTTP de `/` y `/api/pagos`, y recopilación de logs.
6. **Limpieza:** detención del entorno temporal de Compose incluso si un paso anterior falla.

El despliegue es una simulación temporal en el runner de GitHub Actions; no representa un despliegue permanente en un proveedor cloud.

## Comprobaciones pendientes

- [ ] Revisar en la pestaña **Actions** que la ejecución disparada por el último cambio termine correctamente.
- [ ] Confirmar que los trabajos de pruebas, seguridad y construcción/despliegue simulado hayan finalizado correctamente.
- [ ] Revisar el artefacto `informe-jacoco`.
- [ ] Revisar el artefacto de logs del despliegue simulado.
- [ ] Si algún trabajo falla, inspeccionar sus logs y corregir el problema antes de presentar la entrega como validada.

## Ramas y trazabilidad

La rama de evaluación para el pipeline es `feature/pipeline-cicd-evaluacion`. Las ramas históricas y los resultados de otros workflows deben comprobarse en GitHub antes de utilizarlos como evidencia de esta validación.
