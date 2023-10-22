- Cosas pendientes: por qué en clientHandler se le pasa un Funko al método update y un String al insert
- Cambiar los nombres de los métodos
- Borrar la clase routes, o buscar utilidad
- Cambiar el método verifyAdmin y no incluir el rol en el token

- [x] Estructura funkos csv

  * COD: En formato UUID v4
  * NOMBRE: Cadena de caracteres
  * MODELO: Solo tiene estos valores: MARVEL, DISNEY, ANIME u OTROS
  * PRECIO: Moneda con dos decimales.
  * FECHA\_LANZAMIENTO: Fecha en formato YYYY-MM-DD siguiendo ISO-8601

Antes de procesarlos ten en cuenta que puede haber errores en los campos.

**Vamos a trabajar con un cliente servidor**

- [ ]  El servidor, que escucha en el puerto 3000, nada mas arrancar lee el fichero de funkos.csv del directorio data y lo carga en una base de datos.

**El Servidor debe procesar las siguientes llamadas:**

- [ ]  Obtener todos los funkos.
- [ ]  Obtener funkos por código.
- [ ]  Obtener funkos por modelo.
- [ ]  Obtener funkos por año de lanzamiento.
- [ ]  Insertar un funko.
- [ ]  Actualizar un funko.
- [ ]  Eliminar un funko.



- [ ]  El servidor debe ser capaz de procesar las peticiones y saber responder ante errores inesperados.
- [ ]  Además, tendrá un sistema de autenticación y autorización basado en JWT, sabiendo que solo los adminstradores pueden borrar funkos.
- [ ]  Las conexiones deben ser seguras.
- [ ]   Se debe desplegar el servidor en Docker y mostrar como el cliente interactúa con él.

Se recomienda usar un Logger en todo el proceso y testear los elementos más relevantes.

**Entrega**

Para entregar se debe crear un [repositorio](https://aulavirtual33.educa.madrid.org/ies.luisvives.leganes/mod/url/view.php?id=35215 "Repositorio") con el código siguiendo GitFlow. En el README.md de tu proyecto debes explicar cómo has realizado el proceso y mostrar capturas del proceso y analizar los distintos elementos y cómo se han desarrollado. Posteriormente se incluirá el enlace del [repositorio](https://aulavirtual33.educa.madrid.org/ies.luisvives.leganes/mod/url/view.php?id=35215 "Repositorio") en el aula virtual.

La práctica puede hacerse en parejas o individualmente. En caso de hacerse en parejas, se debe indicar en el README.md del proyecto el nombre del compañero o compañera.

Se valorará:

- Solución aportada
- Uso de Arquitecturas Limpias y código limpio
- Principios SOLID
- Test unitarios y con dobles.
- Despliegue en Docker

Agregar entrega
