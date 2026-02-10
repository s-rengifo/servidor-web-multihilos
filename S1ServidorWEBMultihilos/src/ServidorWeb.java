import java.io.* ;
import java.net.* ;
import java.util.* ;

public final class ServidorWeb {
    public static void main(String argv[]) throws Exception{
        // Para establecer el # de puerto
        int puerto = 6789;

        // ServerSocket reserva el puerto en el OS
        ServerSocket servidor = new ServerSocket(puerto);
        System.out.println("Server web escuchando en: " + puerto);

        // Bucle infinito para procesar solicitudes
        while(true){
            // Escucha y acepta conexion
            Socket socketCliente = servidor.accept();

            // Construye un objeto para procesar el mensaje de solicitud HTTP
            SolicitudHttp solicitud = new SolicitudHttp(socketCliente);

            // Crea un nuevo hilo para procesar la solicitud
            Thread hilo = new Thread(solicitud);

            // Inicia el hilo
            hilo.start();
        }

    }
}
