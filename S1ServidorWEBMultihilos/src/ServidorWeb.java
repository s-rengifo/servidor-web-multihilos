import java.io.* ;
import java.net.* ;
import java.util.* ;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ServidorWeb {
    public static void main(String argv[]) throws Exception{
        // Para establecer el # de puerto
        int puerto = 6789;

        // ServerSocket reserva el puerto en el OS
        ServerSocket servidor = new ServerSocket(puerto);

        // Pool de 10 hilos. Esto significa que el servidor
        // procesara hasta 10 clientes simultaneamente de forma eficiente
        ExecutorService pool = Executors.newFixedThreadPool(10);

        System.out.println("Server web escuchando en: " + puerto);

        // Bucle infinito para procesar solicitudes
        while(true){
            // Escucha y acepta conexion
            Socket socketCliente = servidor.accept();

            // Construye un objeto para procesar el mensaje de solicitud HTTP
            SolicitudHttp solicitud = new SolicitudHttp(socketCliente);

            // Crea un nuevo hilo para procesar la solicitud
            // Thread hilo = new Thread(solicitud);
            // Inicia el hilo
            //hilo.start();

            // Se ejecuta desde el pool, y el pool decide que
            // hilo libre toma la tarea
            pool.execute(solicitud);
        }

    }
}
