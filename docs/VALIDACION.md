# Validación técnica

## Pruebas automatizadas

Comando ejecutado:

```bash
./mvnw clean verify
```

Resultado:

- 9 pruebas ejecutadas.
- 0 fallos.
- 0 errores.
- 0 pruebas omitidas.
- Verificación de cobertura JaCoCo cumplida.
- Construcción Maven finalizada correctamente.

## Prueba funcional de la API

Se inició el archivo JAR y se comprobaron las siguientes operaciones:

| Prueba | Resultado |
| --- | --- |
| Abrir la página de presentación | HTTP 200 |
| Abrir Swagger UI | HTTP 200 |
| Listar pagos inicialmente | Respuesta `[]` |
| Registrar un pago | Pago creado con id `1` |
| Listar después de registrar | Pago presente en la respuesta |
| Eliminar el pago | HTTP 204 |
| Listar después de eliminar | Respuesta `[]` |

## Verificación del repositorio

- Las ramas `main`, `develop`, `feature/pagina-presentacion`, `feature/changelog` y `hotfix/titulo-pagina` están creadas.
- Cada rama de trabajo contiene únicamente el cambio correspondiente.
- El workflow se encuentra configurado para push a `develop` y pull request a `main`.
- El repositorio Git no presenta objetos dañados.
