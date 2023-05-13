import java.io.Serializable;
import java.sql.ResultSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class Command implements Serializable
{
    public String command;
    public String[] args;
    public Database database;

    public Command (String command, String[] args)
    {
        this.command = command;
        this.args = args;
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
        if (command == "isUserValid")
        {
            try
            {
                database.isUserValid(args[0], args[1]);
                return new Response("true", "The user is valid");
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                return  new Response("false", e.toString());
            }
        }
        else if (command == "getAllPost")
        {
            try
            {
                ResultSet resultSet = database.getAllPost();
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("post_id", resultSet.getString("post_id"));
                    jsonObject.put("title", resultSet.getString("title"));
                    jsonObject.put("content", resultSet.getString("content"));
                    jsonObject.put("posting_time", resultSet.getString("posting_time"));
                    jsonObject.put("posting_city_id", resultSet.getString("posting_city_id"));
                    jsonObject.put("author_name", resultSet.getString("author_name"));

                    jsonArray.add(jsonObject);
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
                return new Response("false", e.toString());
            }
        }
        else {
            return new Response("false", "Unknown command");
        }
    }
}

