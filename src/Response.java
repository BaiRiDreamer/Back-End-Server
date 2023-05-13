import java.io.Serializable;

public class Response implements Serializable
{
    String responseType;
    String responseContent;

    public Response(String responseType, String responseContent)
    {
        this.responseType = responseType;
        this.responseContent = responseContent;
    }

    @Override
    public String toString ()
    {
        String result = "Response: ";
        result += responseType;
        result += "\nContent:";
        result += responseContent;
        return result;
    }
}
