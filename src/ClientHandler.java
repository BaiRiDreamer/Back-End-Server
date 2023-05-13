import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Socket clientSocket;
    private Database database;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ClientHandler (Socket socket, Database database)
    {
        this.clientSocket = socket;
        this.database = database;
    }

    @Override
    public void run ()
    {
        try
        {
            ois = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("New client connected");

            //将获取的内容反序列化
            Command command = (Command) ois.readObject();
            command.database = database;
            oos.writeObject(command.execute());
            oos.flush();
        }
        catch (IOException e)
        {
            System.out.println("IOException: " + e.getMessage());
            try
            {
                oos.writeObject(new Response("false", e.toString()));
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
        catch (Exception e)
        {
            try
            {
                oos.writeObject(new Response("false", e.toString()));
            }
            catch (IOException ex)
            {
                throw new RuntimeException(ex);
            }
        }
    }
}





