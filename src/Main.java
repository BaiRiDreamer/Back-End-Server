import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.chrono.ChronoLocalDate;
import java.util.concurrent.*;

public class Main
{
    private static final int PORT = 66666;
    private static final int EXECUTORPOOLSIZE = 10;

    public static void main (String[] args)
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server is listening on port " + PORT);
            Database database = new Database();
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket, database));
                System.out.println("New client connected, the task has been submitted to the thread pool");
            }
        }
        catch (Exception e)
        {
            System.err.println("Server exception: " + e.getMessage());
        }
    }
}