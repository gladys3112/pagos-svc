# pagos-svc

Microservicio REST de pagos desarrollado con Spring Boot 3.3, Java 21, Maven, H2 y Docker. Este repositorio corresponde a la Evaluación Parcial N.° 1 de Ingeniería DevOps e incluye ramificación GitFlow, documentación técnica y automatización con GitHub Actions.

> Trabajo realizado de forma individual. Los pull requests se mantienen para simular el flujo colaborativo y dejar trazabilidad de cada cambio.

## Funcionalidades

- Registrar un pago.
- Listar los pagos registrados.
- Eliminar un pago por su identificador.
- Consultar y probar la API mediante Swagger UI.
- Revisar la base de datos temporal mediante la consola H2.
- Ejecutar pruebas automáticas y control de cobertura con JaCoCo.
- Construir y ejecutar el servicio mediante Docker Compose.

## Tecnologías

| Tecnología | Uso |
| --- | --- |
| Java 21 | Lenguaje del microservicio |
| Spring Boot 3.3 | API REST y configuración de la aplicación |
| Spring Data JPA | Acceso a los datos |
| H2 | Base de datos en memoria |
| Springdoc OpenAPI | Documentación Swagger de la API |
| Maven | Dependencias, pruebas y construcción |
| JaCoCo | Verificación de cobertura |
| Docker | Empaquetado y ejecución del servicio |
| GitHub Actions | Automatización del flujo CI |

## Ejecución local

Requisitos: JDK 21 y Docker Desktop o Docker Engine.

### Con Docker

```bash
docker compose up --build -d
docker compose logs -f
```

Para detener el servicio:

```bash
docker compose down
```

### Con Maven

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux o macOS:

```bash
./mvnw spring-boot:run
```

## Direcciones del servicio

| Recurso | Dirección |
| --- | --- |
| Página de presentación | <http://localhost:8080> |
| Swagger UI | <http://localhost:8080/swagger-ui/index.html> |
| Consola H2 | <http://localhost:8080/h2-console> |
| API de pagos | <http://localhost:8080/api/pagos> |

Datos para la consola H2:

- JDBC URL: `jdbc:h2:mem:pagosdb`
- Usuario: `sa`
- Contraseña: dejar vacía.

## Endpoints

| Método | Ruta | Función |
| --- | --- | --- |
| `GET` | `/api/pagos` | Lista todos los pagos |
| `POST` | `/api/pagos` | Registra un pago |
| `DELETE` | `/api/pagos/{id}` | Elimina un pago |

Ejemplo para registrar un pago:

```json
{
  "nombre": "Entrada VIP",
  "orden": "ORD-1001",
  "monto": 45000,
  "estado": "APROBADO"
}
```

## Pruebas y cobertura

```bash
./mvnw clean verify
```

El comando ejecuta las pruebas y verifica la cobertura mediante JaCoCo. El informe queda en `target/site/jacoco/index.html`.

Los resultados de la comprobación final están documentados en [docs/VALIDACION.md](docs/VALIDACION.md).

## Modelos de ramificación considerados

| Modelo | Funcionamiento | Aplicabilidad |
| --- | --- | --- |
| GitFlow | Mantiene `main`, `develop`, ramas `feature` y `hotfix` | Proyectos con entregas planificadas que necesitan separar integración y producción |
| GitHub Flow | Utiliza `main` y ramas breves que se integran mediante pull requests | Equipos pequeños y aplicaciones con despliegue continuo |
| Trunk-based development | Trabaja sobre un tronco principal y ramas de vida muy corta | Equipos con integración continua madura y cambios pequeños y frecuentes |

## Modelo seleccionado: GitFlow

### Justificación del uso de GitFlow

Aunque realicé el trabajo sola, decidí utilizar GitFlow porque me permitió mantener el repositorio más ordenado. Usé la rama `main` para conservar la versión estable del proyecto y `develop` para integrar y revisar los cambios antes de pasarlos a la versión final. También utilicé una rama `feature` para desarrollar una función nueva y una rama `hotfix` para realizar una corrección. De esta forma pude trabajar sin arriesgar el código que ya funcionaba y mantener un mejor control de los cambios realizados.

## Estructura de ramas

| Rama | Origen | Propósito | Destino del PR |
| --- | --- | --- | --- |
| `main` | Inicial | Versión estable y lista para producción | — |
| `develop` | `main` | Integración de funcionalidades terminadas | `main` al preparar una versión |
| `feature/pagina-presentacion` | `develop` | Agregar el pie de página con la versión | `develop` |
| `feature/changelog` | `develop` | Incorporar el registro de cambios | `develop` |
| `hotfix/titulo-pagina` | `main` | Corregir el título de la página principal | `main` |

## Trazabilidad de cambios

| Tipo | Rama | Cambio | Commit esperado |
| --- | --- | --- | --- |
| Feature 1 | `feature/pagina-presentacion` | Pie de página con la versión del servicio | `feat(ui): agregar pie de pagina con version del servicio` |
| Feature 2 | `feature/changelog` | Archivo `CHANGELOG.md` | `docs: agregar changelog del microservicio pagos` |
| Hotfix | `hotfix/titulo-pagina` | Corrección del elemento `<title>` | `fix(ui): corregir titulo de la pagina principal` |

## Convenciones y buenas prácticas

### Mensajes de commit

Formato: `tipo(alcance): descripcion-corta`, escrito en minúsculas y sin tildes.

| Tipo | Uso | Ejemplo |
| --- | --- | --- |
| `feat` | Nueva funcionalidad | `feat(ui): agregar pie de pagina` |
| `fix` | Corrección de un error | `fix(ui): corregir titulo` |
| `docs` | Documentación | `docs: agregar changelog` |
| `test` | Pruebas | `test(api): cubrir eliminacion de pagos` |
| `chore` | Configuración, herramientas o CI | `chore(ci): agregar workflow hola mundo` |

### Naming de ramas

- Usar `feature/descripcion-corta` para funcionalidades.
- Usar `hotfix/descripcion-corta` para correcciones urgentes.
- Escribir los nombres en minúsculas, sin tildes y con palabras separadas por guiones.
- Crear las ramas `feature` desde `develop` y las ramas `hotfix` desde `main`.

### Flujo de merge

1. Actualizar la rama de origen antes de comenzar.
2. Crear una rama específica para el cambio.
3. Realizar commits pequeños y descriptivos.
4. Subir la rama y abrir un pull request al destino correcto.
5. Revisar la diferencia y confirmar que el workflow termine en verde.
6. Fusionar mediante merge commit para mantener visible la trazabilidad.
7. Sincronizar `develop` después de integrar un hotfix en `main`.

No se realizan cambios directos sobre `main` o `develop` cuando corresponden a una feature o un hotfix.

### Estrategia de revisión individual

Como el trabajo fue realizado de forma individual, antes de fusionar cada pull request realicé una auto-revisión de los archivos modificados, comprobé el resultado de las pruebas y revisé que el destino del PR fuera correcto. Cada pull request incluye una descripción, un checklist y el resultado de GitHub Actions para dejar evidencia del control efectuado.

### Estructura de carpetas

```text
pagos-svc/
├── .github/
│   ├── workflows/hola-mundo.yml
│   └── pull_request_template.md
├── docs/
│   ├── TRAZABILIDAD.md
│   └── VALIDACION.md
├── src/
│   ├── main/java/cl/duoc/pagos/
│   ├── main/resources/
│   └── test/java/cl/duoc/pagos/
├── CHANGELOG.md
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

No se versionan archivos generados, credenciales, variables privadas ni el directorio `target/`.

## Automatización con GitHub Actions

El workflow `Hola Mundo CI` se ejecuta automáticamente en los siguientes casos:

- Cada `push` realizado a `develop`.
- Cada pull request cuyo destino sea `main`.

El workflow descarga el código, muestra información de la ejecución y verifica el proyecto con Maven. Su función dentro de CI/CD es entregar una validación automática y repetible antes de integrar cambios, disminuyendo la posibilidad de incorporar una versión con errores.

## Declaración de uso de inteligencia artificial

Se utilizó ChatGPT como apoyo en la revisión de redacción no evaluativa y en la orientación técnica del repositorio. La justificación y la reflexión personal fueron redactadas por la estudiante.

Referencia para la declaración y citación de IA: <https://bibliotecas.duoc.cl/ia>.

## Reflexión personal

Al principio me costó entender por qué era necesario utilizar varias ramas si estaba trabajando sola. Después comprendí que cada una cumple una función diferente y que sirven para organizar mejor el desarrollo. Lo que más aprendí fue que no conviene hacer todos los cambios directamente en `main`, porque un error podría afectar la versión estable del proyecto. Me ayudó a comprender mejor el funcionamiento de Git y GitHub. También aprendí que es importante realizar commits con mensajes claros, ya que permiten identificar fácilmente cada cambio y volver a una versión anterior si ocurre algún problema. Considero que lo aprendido me servirá en futuros proyectos, especialmente cuando tenga que trabajar con más personas.

## Estado de la entrega

- [x] Repositorio publicado en GitHub.
- [x] Ramas `main`, `develop`, `feature/pagina-presentacion`, `feature/changelog` y `hotfix/titulo-pagina` visibles.
- [x] Dos pull requests de tipo feature fusionados hacia `develop`.
- [x] Un pull request de tipo hotfix fusionado hacia `main`.
- [x] Hotfix sincronizado desde `main` hacia `develop`.
- [x] Workflow visible y en verde en la pestaña Actions.
- [x] `mvn test` y `mvn verify` ejecutados sin errores.
