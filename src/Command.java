import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author Li Weihao
 */
public class Command implements Serializable
{
    public String command;
    public String[] args;
    public Database database;
    public byte[] file;

    public Command (String command, String[] args)
    {
        this.command = command;
        this.args = args;
    }

    public Command (String command, String[] args, byte[] file)
    {
        this.command = command;
        this.args = args;
        this.file = file;
    }

    @Override
    public String toString ()
    {
        String result = "Command: ";
        result += command;
        result += "\nArgs:";
        for(String arg : args)
        {
            result += " " + arg;
        }
        return result;
    }

    public Response execute ()
    {
        if (command.equals("isUserExist"))
        {
            try
            {
                if (database.isUserExist(args[0], args[1]))
                {
                    return new Response("true", "The user exists");
                }
                else
                {
                    return new Response("false", "The user does not exist");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("isUserValid"))
        {
            try
            {
                database.isUserValid(args[0], args[1]);
                return new Response("true", "The user is valid");
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                return new Response("false", e.toString());
            }
        }
        else if (command.equals("registerNewUser"))
        {
            try
            {
                if (database.registerNewUser(args[0], args[1], args[2], args[3]))
                {
                    return new Response("true", "Register successfully");
                }
                else
                {
                    return new Response("false", "Register failed");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("PublishPost"))
        {
            try
            {
                boolean tmp = args[6].equals("true") ? true : false;
                if (database.PublishPost(args[0], args[1], args[2], args[3], args[4], args[5], file, tmp))
                {
                    return new Response("true", "Publish post successfully");
                }
                else
                {
                    return new Response("false", "Publish post failed");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("getAllPost"))
        {
            try
            {
                ResultSet resultSet = database.getAllPost();
                JSONArray jsonArray = new JSONArray();

                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("post_id", resultSet.getInt("post_id"));
                    jsonObject.put("title", resultSet.getString("title"));
                    jsonObject.put("content", resultSet.getString("content"));
                    jsonObject.put("posting_time", resultSet.getTimestamp("posting_time"));
                    jsonObject.put("posting_city_id", resultSet.getInt("posting_city_id"));
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonObject.put("filename", resultSet.getString("filename"));
                    jsonObject.put("file", resultSet.getBytes("file"));
                    jsonObject.put("isunKnown", resultSet.getBoolean("isunKnown"));

                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "没有帖子被发布 ( •̀ ω •́ )✧");
                }

                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("getIthIdPost"))
        {
            try
            {
                ResultSet resultSet = database.getIthIdPost(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("post_id", resultSet.getInt("post_id"));
                    jsonObject.put("title", resultSet.getString("title"));
                    jsonObject.put("content", resultSet.getString("content"));
                    jsonObject.put("posting_time", resultSet.getTimestamp("posting_time"));
                    jsonObject.put("posting_city_id", resultSet.getInt("posting_city_id"));
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonObject.put("filename", resultSet.getString("filename"));
                    jsonObject.put("file", resultSet.getBytes("file"));
                    jsonObject.put("isunKnown", resultSet.getBoolean("isunKnown"));

                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "没有帖子被发布 ( •̀ ω •́ )✧");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("getPublishedPost"))
        {
            try
            {
                ResultSet resultSet = database.getPublishedPost(args[0]);
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("post_id", resultSet.getInt("post_id"));
                    jsonObject.put("title", resultSet.getString("title"));
                    jsonObject.put("content", resultSet.getString("content"));
                    jsonObject.put("posting_time", resultSet.getTimestamp("posting_time"));
                    jsonObject.put("posting_city_id", resultSet.getInt("posting_city_id"));
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonObject.put("filename", resultSet.getString("filename"));
                    jsonObject.put("file", resultSet.getBytes("file"));
                    jsonObject.put("isunKnown", resultSet.getBoolean("isunKnown"));

                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "没有帖子被发布 ( •̀ ω •́ )✧");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }
        }
        else if (command.equals("getPostCnt"))
        {
            try
            {
                ResultSet resultSet = database.getAllPost();
                resultSet.last();
                int cnt = resultSet.getRow();
                return new Response("true", Integer.toString(cnt));
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }

        }
        else if (command.equals("getUserPostCnt"))
        {
            try
            {
                ResultSet resultSet = database.getPublishedPost(args[0]);
                if (resultSet == null)
                {
                    return new Response("true", "该用户还没发表过帖子捏");
                }
                resultSet.last();
                int mypostCnt = resultSet.getRow();
                return new Response("true", Integer.toString(mypostCnt));
            }
            catch (SQLException e)
            {
                System.err.println(e.toString());
                return new Response("DatabaseErr", e.toString());
            }


        }
//        else if (command == "")
//        {
//
//        }
        else
        {
            return new Response("false", "Unknown command");
        }
    }
}

