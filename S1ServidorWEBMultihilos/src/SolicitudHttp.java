import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

final public class SolicitudHttp implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public SolicitudHttp(Socket socket) throws Exception{
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            proceseSolicitud();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceseSolicitud() throws Exception {
        // inp/out
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Leer la linea de solicitud
        String lineaDeSolicitud = br.readLine();

        System.out.println("Linea de solicitud: " + lineaDeSolicitud);

        // Leer y mostrar por consola los encabezados HTTP
        String lineaEncabezado = null;
        while ((lineaEncabezado = br.readLine()) != null && !lineaEncabezado.isEmpty()) {
            System.out.println("Header: " + lineaEncabezado);
        }
        System.out.println("-----------------------------\n");

        // Extraer el nombre del archivo usando StringTokenizer
        StringTokenizer partesLinea = new StringTokenizer(lineaDeSolicitud);
        // Salta el GET
        partesLinea.nextToken();
        // Extrae '/file.png'
        String nombreArchivo = partesLinea.nextToken();

        // Anteponemos un punto para que busque en el directorio actual (la raiz)
        nombreArchivo = "." + nombreArchivo;

        // Abrir el archivo solicitado
        FileInputStream fis = null;
        boolean archivoExiste = true;
        try {
            fis = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException e) {
            archivoExiste = false;
        }

        // init respuesta
        String lineaDeEstado = null;
        String lineaHeader = null;
        String cuerpoMensaje = null;

        if (archivoExiste) {
            lineaDeEstado = "HTTP/1.0 200 OK" + CRLF;
            lineaHeader = "Content-type: " + contentType(nombreArchivo) + CRLF;
        } else {
            lineaDeEstado = "HTTP/1.0 404 Not Found" + CRLF;
            lineaHeader = "Content-type: text/html" + CRLF;
            cuerpoMensaje = "<html><head><title>No Encontrado</title></head>" +
                    "<body><h1>404 Not Found</h1><p>El archivo solicitado no existe</p></body></html>";
        }

        // Enviar la linea de estado
        enviarString(lineaDeEstado, os);
        // Enviar el header
        enviarString(lineaHeader, os);
        // Enviar la linea en blanco
        enviarString(CRLF, os);

        // Enviar el cuerpo
        if (archivoExiste) {
            enviarBytes(fis, os);
            fis.close();
        } else {
            enviarString(cuerpoMensaje, os);
        }

        // Limpiar y cerrar
        os.flush();
        os.close();
        br.close();
        socket.close();
    }

    // Metodo para determinar el tipo
    private static String contentType(String nombreArchivo) {
        if (nombreArchivo.endsWith(".htm") || nombreArchivo.endsWith(".html")) {
            return "text/html";
        }
        if (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (nombreArchivo.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    // Metodo auxiliar para enviar texto
    private static void enviarString(String line, OutputStream os) throws Exception {
        os.write(line.getBytes(StandardCharsets.UTF_8));
    }

    // Metodo auxiliar para enviar archivos
    private static void enviarBytes(InputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

}
