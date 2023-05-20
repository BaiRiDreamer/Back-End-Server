import com.alibaba.druid.pool.DruidDataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private static final String host = "localhost";
    private static final String port = "5432";
    private static final String dbname = "Project2";
    private static final String user = "checker";
    private static final String pwd = "123456";


    public static void main (String[] args)
    {
        /**
         * 创建线程池
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        /**
         * 创建数据库连接池
         */
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + dbname);
        dataSource.setUsername(user);
        dataSource.setPassword(pwd);
        dataSource.setDriverClassName("org.postgresql.Driver");
        /**
         * 设置数据库连接池的参数
         */
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(90000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);


        /**
         * 创建服务器套接字，持续监听端口并进行相关操作
         */
        try (ServerSocket serverSocket = new ServerSocket(PORT))
        {
            System.out.println("Server is listening on port " + PORT + " ("+LocalDateTime.now()+")");
            Database database = new Database();
            database.con = dataSource.getConnection();
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                //ClinetHandler是对一个客户端操作的请求处理类,通过线程池来创建一个客户端处理类,从而对一个客户端进行控制
                executor.submit(new ClientHandler(clientSocket, database));
                System.out.println("New client connected, the task has been submitted to the thread pool" + " ("+LocalDateTime.now()+")");
            }
        }
        catch (IOException e)
        {
            System.err.println("ServerSocket exception: " + e.getMessage());
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
}