# EP3 — Observabilidad y entornos reales en DevOps

## Alcance

EP3 extiende EP2 sin reemplazarlo. Se conserva el workflow CI/CD anterior y se agregan Actuator, métricas Prometheus, un dashboard Grafana y manifiestos Kubernetes.

## Arquitectura

- **Aplicación:** Spring Boot en Java 21, dos réplicas Kubernetes.
- **Salud:** Spring Boot Actuator expone health, readiness y liveness.
- **Métricas:** Micrometer publica métricas Prometheus en `/actuator/prometheus`.
- **Recolección:** Prometheus consulta el servicio `pagos-svc:8080` cada 15 segundos.
- **Visualización:** Grafana carga un dashboard provisionado con cuatro paneles.
- **Orquestación:** Kubernetes administra réplicas, servicios, probes y límites de recursos.

## Las cuatro métricas del dashboard

1. **Latencia:** percentil 95 de duración de solicitudes HTTP (segundos).
2. **Solicitudes:** tasa de solicitudes HTTP por segundo.
3. **Errores:** porcentaje de respuestas HTTP 5xx.
4. **Memoria:** porcentaje de heap JVM utilizado.

Las métricas se obtienen de Micrometer/Spring Boot; las rutas Actuator se excluyen de los cálculos HTTP del negocio.

## Ejecución local

Requisitos: Docker, kubectl y kind.

```bash
./mvnw clean verify
docker build -t pagos-svc:ep3 .
kind create cluster --name pagos-ep3
kind load docker-image pagos-svc:ep3 --name pagos-ep3
kubectl apply -f k8s/observability.yaml
kubectl rollout status deployment/pagos-svc -n pagos-observability
kubectl rollout status deployment/prometheus -n pagos-observability
kubectl rollout status deployment/grafana -n pagos-observability
```

Acceso local mediante port-forward:

```bash
kubectl port-forward -n pagos-observability service/grafana 3000:3000
kubectl port-forward -n pagos-observability service/pagos-svc 8080:8080
```

- Grafana: http://localhost:3000 (usuario `admin`, contraseña de laboratorio `admin-ep3-local`).
- Aplicación: http://localhost:8080
- Salud: http://localhost:8080/actuator/health
- Métricas: http://localhost:8080/actuator/prometheus

La contraseña incluida es únicamente para el clúster local desechable de evaluación. En un entorno real debe reemplazarse por un Secret y no reutilizarse.

## Pipeline y evidencia

El workflow `.github/workflows/ep3-observability.yml` ejecuta pruebas, construye y escanea la imagen, crea un clúster kind temporal, despliega los manifiestos y verifica salud y métricas. Guarda recursos, eventos y descripción de pods como artefactos de GitHub Actions.

### Demostración de falla crítica (IE6)

En GitHub Actions selecciona **EP3 - Observabilidad y Kubernetes → Run workflow** y marca `simulate_critical_failure=true`. El job **Demostración de falla crítica IE6** emite un error controlado y termina con código 1. El job Kubernetes exige que esa demostración esté omitida (ejecución normal) o aprobada; si la falla crítica se activa, Kubernetes queda omitido. No se introduce una vulnerabilidad en la aplicación ni se afecta producción.

Para la ejecución normal deja `simulate_critical_failure=false`; entonces el despliegue Kubernetes se ejecuta después de las pruebas y del escaneo.

## Auditoría y protección de ramas

La configuración de protección es una política del repositorio y debe habilitarse desde GitHub:

1. Settings → Rules → Rulesets → New ruleset → New branch ruleset.
2. Nombre: `Protección main y develop EP3`; estado Active.
3. Target branches: incluir `main` y `develop`.
4. Activar **Require a pull request before merging** y exigir al menos 1 aprobación si existe un segundo revisor disponible.
5. Activar **Require status checks to pass** y seleccionar los checks CI/CD aplicables después de que GitHub los detecte.
6. Activar **Block force pushes** y **Restrict deletions**.
7. Guardar el ruleset.

Si el plan/cuenta no permite exigir aprobación a otra persona en un repositorio individual, documentar la limitación y realizar auto-revisión sin declarar una aprobación externa inexistente.

### Evidencias de auditoría que se deben guardar

- Historial de commits con autor, fecha y mensaje.
- Pull request EP3 con descripción, archivos modificados y revisión.
- Historial de ejecuciones Actions (una ejecución normal exitosa y otra con falla crítica).
- Reglas de protección activas en Settings → Rules → Rulesets.
- Artefacto `evidencias-kubernetes` y logs de la ejecución.

No se debe afirmar que una regla está activa hasta comprobarla en la configuración del repositorio.

## Registro de validación

Completar con enlaces/fechas de ejecuciones reales:
- Ejecución normal: pendiente de validación.
- Ejecución de falla crítica IE6: pendiente de validación.
- Reglaset main/develop: pendiente de activar y verificar.
- Captura de dashboard con datos: pendiente de generar con solicitudes reales.

## Declaración de uso de IA

Se utilizó ChatGPT como apoyo para revisar la pauta, proponer la instrumentación de observabilidad, estructurar manifiestos Kubernetes y preparar documentación y pruebas del pipeline. La estudiante debe comprender, revisar y validar cada componente, registrar qué herramientas utilizó y citar su uso conforme a las instrucciones de Duoc UC: https://bibliotecas.duoc.cl/ia.

La reflexión individual debe ser escrita por cada integrante con sus propias palabras, a partir de lo que efectivamente aprendió.
