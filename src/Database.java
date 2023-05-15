import java.sql.*;

import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * 该类是用来操作数据库所需要的方法和函数等
 */
public class Database
{
    static Connection con = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "Project2";
    private String user = "checker";
    private String pwd = "123456";
    private String port = "5432";

    public Database (String host, String dbname, String user, String pwd, String port)
    {
        this.host = host;
        this.dbname = dbname;
        this.user = user;
        this.pwd = pwd;
        this.port = port;
        openDatasource();
    }

    public Database ()
    {
        openDatasource();
    }

    public void openDatasource ()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (Exception e)
        {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }

        try
        {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            con = DriverManager.getConnection(url, user, pwd);
        }
        catch (SQLException e)
        {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void closeDatasource ()
    {
        if (con != null)
        {
            try
            {
                con.close();
                con = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    public boolean isUserExist (String username, String userId) throws SQLException
    {
        String sqlUsername = "select * from author where author_name = ?";
        PreparedStatement prepStUsername = con.prepareStatement(sqlUsername);
        prepStUsername.setString(1, username);

        String sqlUserID = "select * from author_and_id where author_id = ?";
        PreparedStatement prepStUserID = con.prepareStatement(sqlUserID);
        prepStUserID.setString(1, username);

        ResultSet resultSetName = prepStUsername.executeQuery();
        ResultSet resultSetID = prepStUserID.executeQuery();

        if (resultSetName.next() || resultSetID.next())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 判断该用户是否存在或者是否密码正确
     * 若存在且密码正确，返回true
     * 若存在问题，则直接抛出Exception，Exception.toString中包含错误原因
     *
     * @param username
     * @param password
     * @return Exception
     */
    public boolean isUserValid (String username, String password) throws Exception
    {
        String sqlUsername = "select * from author where author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sqlUsername);
        preparedStatement.setString(1, username);

        ResultSet resultSetName = preparedStatement.executeQuery();
        if (resultSetName.next())
        {
            if (resultSetName.getString("password").equals(password))
            {
                return true;
            }
            else
            {
                throw new Exception("密码错误");
            }
        }
        else
        {
            throw new Exception("用户不存在");
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    public boolean registerNewUser (String username, String userId, String phone, String password) throws SQLException
    {
        String sqlUsername = "insert into author (author_name, author_registration_time, author_phone, password) values (?,?, ?, ?)";
        PreparedStatement prepStUsername = con.prepareStatement(sqlUsername);

        String sqlUserID = "insert into author_and_id (author_id, author_name) values (?, ?)";
        PreparedStatement prepStUserID = con.prepareStatement(sqlUserID);

        prepStUsername.setString(1, username);
        prepStUsername.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        prepStUsername.setString(3, phone);
        prepStUsername.setString(4, password);

        prepStUserID.setString(1, userId);
        prepStUserID.setString(2, username);

        if (prepStUsername.execute() && prepStUserID.execute())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean PublishPost (String username, String title, String content, String country, String city, String fileName, byte[] file) throws SQLException
    {
        String cityInsert = "insert into city (city, country) values (?, ?) on conflict do nothing";
        PreparedStatement preparedStatementCity = con.prepareStatement(cityInsert);
        preparedStatementCity.setString(1, city);
        preparedStatementCity.setString(2, country);
        preparedStatementCity.execute();

        String citySearch = "select * from city where city = ? and country = ?";
        PreparedStatement preparedStatementCitySearch = con.prepareStatement(citySearch);
        preparedStatementCitySearch.setString(1, city);
        preparedStatementCitySearch.setString(2, country);
        ResultSet resultSetCity = preparedStatementCitySearch.executeQuery();

        String sql = "insert into post (title, content, posting_time, posting_city_id, author_name, filename, file) values (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, content);
        preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        preparedStatement.setString(4, resultSetCity.getString("city_id"));
        preparedStatement.setString(5, username);
        preparedStatement.setString(6, fileName);
        preparedStatement.setBytes(7, file);

        if (preparedStatement.execute())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public ResultSet getAllPost () throws SQLException
    {
        String sql = "select * from post";
        PreparedStatement preparedStatement = con.prepareStatement(sql, TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        ResultSet rs = preparedStatement.executeQuery();

        return rs;
    }

    public ResultSet getIthIdPost (int postId) throws SQLException
    {
        String sql = "select * from post where post_id= ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);

        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getPublishedPost (String username) throws Exception
    {
        String sql = "select * from post where author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();

        return rs;
    }
}