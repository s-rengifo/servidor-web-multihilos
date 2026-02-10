# Servidor Web Multihilo en Java
## Samuel Rengifo A00404150

Este entrega consiste en la implementación de un servidor web básico utilizando Java, Sockets y Hilos. El servidor es capaz de procesar solicitudes HTTP/1.0, servir archivos estáticos (HTML e imágenes) y gestionar múltiples conexiones de manera concurrente.

## Tabla de Contenidos
1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Cumplimiento de Requerimientos](#-cumplimiento-de-requerimientos)
3. [Estructura de Archivos](#-estructura-de-archivos)

---

## Cumplimiento de Requerimientos

Aqui se detalla cómo el código cumple con los puntos solicitados en el enunciado de la tarea:

### 1. Conexión TCP y Operación Continua
* **Requerimiento:** Escuchar en un puerto > 1024 y operar continuamente.
* **Implementación (`ServidorWeb.java`):**
    * Se define `int puerto = 6789;`.
    * Se utiliza un ciclo infinito `while(true)` que envuelve el método `accept()`, permitiendo que el servidor siga escuchando indefinidamente después de recibir una conexión.

### 2. Capacidad Multi-hilos
* **Requerimiento:** Crear un hilo independiente por cada solicitud.
* **Implementación (`ServidorWeb.java`):**
    * La clase `SolicitudHttp` implementa la interfaz `Runnable`.
    * Dentro del bucle principal, se ejecuta:
        ```java
        SolicitudHttp solicitud = new SolicitudHttp(socketCliente);
        Thread hilo = new Thread(solicitud);
        hilo.start();
        ```
    * Esto delega el procesamiento a un nuevo hilo del OS inmediatamente.

### 3. Interpretación HTTP y Extracción de Recursos
* **Requerimiento:** Interpretar GET y extraer el recurso.
* **Implementación (`SolicitudHttp.java`):**
    * Se usa `StringTokenizer` sobre la línea de solicitud (`GET /archivo.html HTTP/1.1`).
    * Se descarta el primer token ("GET") y se captura el segundo token que corresponde al nombre del archivo.
    * Se antepone un punto `.` para convertir la ruta absoluta web en una ruta relativa local (`./archivo.html`).

### 4. Salida por Consola los Headers
* **Requerimiento:** Mostrar la línea de solicitud y los encabezados recibidos.
* **Implementación (`SolicitudHttp.java`):**
    * Se imprime la primera línea con `System.out.println`.
    * Se implementa un ciclo `while` específico para leer el resto del stream:
        ```java
        while ((lineaEncabezado = br.readLine()) != null && !lineaEncabezado.isEmpty()) {
            System.out.println("Header: " + lineaEncabezado);
        }
        ```
    * Esto asegura que todos los metadatos del navegador se muestren en el servidor.

### 5. Estructura de Respuesta HTTP Válida
* **Requerimiento:** Línea de estado, headers y cuerpo usando CRLF.
* **Implementación (`SolicitudHttp.java`):**
    * Se define la constante `CRLF = "\r\n"`.
    * Se envían secuencialmente: Línea de Estado -> Content-Type -> CRLF (Línea en blanco) -> Cuerpo del mensaje.

### 6. Soporte de Tipos MIME
* **Requerimiento:** Servir HTML, JPG y GIF con su tipo correcto.
* **Implementación (`SolicitudHttp.java`):**
    * El método auxiliar `contentType(String nombreArchivo)` analiza la extensión del archivo.
    * Retorna `text/html`, `image/jpeg` o `image/gif` según corresponda, asignándolo al header `Content-type`.
    * Se utiliza un buffer de bytes para enviar las imágenes sin corrupción de datos.

### 7. Manejo de Error 404
* **Requerimiento:** Responder con código 404 si el recurso no existe.
* **Implementación (`SolicitudHttp.java`):**
    * Se intenta abrir el archivo dentro de un bloque `try-catch`.
    * Si se captura `FileNotFoundException`, se cambia la bandera `archivoExiste = false`.
    * En la respuesta, se envía el encabezado `HTTP/1.0 404 Not Found` y se genera un cuerpo HTML indicando el error.

### 8. Gestión de Recursos (Sockets y Streams)
* **Requerimiento:** Cerrar correctamente sockets y streams.
* **Implementación (`SolicitudHttp.java`):**
    * Al finalizar el método `proceseSolicitud()`, se ejecutan explícitamente:
        * `os.flush()`
        * `os.close()`
        * `br.close()`
        * `socket.close()`
    * Esto libera los recursos del sistema operativo asociados al hilo que acaba de terminar.

---

## Estructura de Archivos

* `ServidorWeb.java`: Clase principal que inicia el socket y el bucle de aceptación de clientes.
* `SolicitudHttp.java`: Clase `Runnable` que contiene la lógica de procesamiento de la petición HTTP.
* `facebook.html`: Archivo de prueba para servir.
* `cr7siu.jpg`: Imagen de prueba.

---