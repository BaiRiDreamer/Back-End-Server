import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.time.chrono.ChronoLocalDate;
import java.util.concurrent.*;

/**
 * 主函数，控制客户端请求的接受与处理
 * 通过线程池来控制并发量
 *
 */
public class Main
{
    private static final int PORT = 7345;               //默认服务器监听地址
    private static final int EXECUTORPOOLSIZE = 10;     //默认服务器线程池大小

    public static void main (String[] args)
    {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server is listening on port " + PORT);
            Database database = new Database();
//            while (true)
//            {
                Socket clientSocket = serverSocket.accept();
                //ClinetHandler是对一个客户端操作的请求处理类,通过线程池来创建一个客户端处理类,从而对一个客户端进行控制
                executor.submit(new ClientHandler(clientSocket, database));
                System.out.println("New client connected, the task has been submitted to the thread pool");
//            }
        }
        catch (IOException e)
        {
            System.err.println("ServerSocket exception: " + e.getMessage());
        }
    }
}