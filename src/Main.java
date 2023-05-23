import com.alibaba.druid.pool.DruidDataSource;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * 主函数，控制客户端请求的接受与处理
 * 通过线程池来控制并发量
 *
 */
public class Main
{
    private static  int PORT = 7345;               //默认服务器监听地址


    private static  String host = "192.168.62.128";
    private static  String port = "7654";
    private static  String dbname = "postgres";
    private static  String user = "test";
    private static  String pwd = "Test@123";

//    private static  String host = "localhost";
//    private static  String port = "5432";
//    private static  String dbname = "Project2";
//    private static  String user = "checker";
//    private static  String pwd = "123456";

    public static void main (String[] args)
    {
        try
        {   String desktopPath = System.getProperty("user.home") + "\\Desktop\\back.properties";
            Scanner scanner = new Scanner(new File(desktopPath));
            String  hostRegex = "Host = \".+\"";
            String  portRegex = "Port = \"[0-9]*\"";
            String  dbnameRegex = "DBName = \"[a-zA-Z0-9]*\"";
            String  userRegex = "User = \".+\"";
            String  pwdRegex = "Pwd = \".+\"";


            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                line = line.trim();
                if (Pattern.matches(hostRegex, line))
                {
                    host = line.substring(8, line.length() - 1);
                }
                else if (line.matches(portRegex))
                {
                    port = line.substring(8, line.length() - 1);
                }
                else if (line.matches(dbnameRegex))
                {
                    dbname = line.substring(10, line.length() - 1);
                }
                else if (line.matches(userRegex))
                {
                    user = line.substring(8, line.length() - 1);
                }
                else if (line.matches(pwdRegex))
                {
                    pwd = line.substring(7, line.length() - 1);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("back.properties not found");
        }

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