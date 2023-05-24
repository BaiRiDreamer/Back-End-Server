import java.sql.*;

import static java.sql.ResultSet.TYPE_SCROLL_SENSITIVE;

/**
 * 该类是用来操作数据库所需要的方法和函数等
 *
 * @author Li Weihao
 */
public class Database {
    static Connection con = null;

    public void closeDatasource() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    public boolean isUserExist(String username, String userId) throws SQLException {
        String sqlUsername = "select * from author where author_name = ?";
        PreparedStatement prepStUsername = con.prepareStatement(sqlUsername);
        prepStUsername.setString(1, username);

        String sqlUserID = "select * from author_and_id where author_id = ?";
        PreparedStatement prepStUserID = con.prepareStatement(sqlUserID);
        prepStUserID.setString(1, userId);

        ResultSet resultSetName = prepStUsername.executeQuery();
        ResultSet resultSetID = prepStUserID.executeQuery();

        if (resultSetName.next() || resultSetID.next()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断该用户是否存在或者是否密码正确
     * 若存在且密码正确，返回true
     * 若存在问题，则直接抛出Exception，Exception.toString中包含错误原因
     *
     * @return Exception
     */
    public boolean isUserValid(String username, String password) throws Exception {
        if (username == null || password == null || username.equals("") || password.equals("")) {
            throw new Exception("用户名或密码为空");
        }
        String sqlUsername = "select * from author where author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sqlUsername);
        preparedStatement.setString(1, username);

        ResultSet resultSetName = preparedStatement.executeQuery();
        if (resultSetName.next()) {
            if (resultSetName.getString("password").equals(password)) {
                return true;
            } else {
                throw new Exception("密码错误");
            }
        } else {
            throw new Exception("用户不存在");
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    public boolean registerNewUser(String username, String userId, String phone, String password) throws SQLException {
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

        if (prepStUsername.executeUpdate() != 0 && prepStUserID.executeUpdate() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean PublishPost(String username, String title, String content, String country, String city, String fileName, byte[] file, boolean isUnknown) throws SQLException {
        String cityInsert = "insert into city (city, country) select ?, ? where not exists (select * from city where city = ? and country = ?)";
        PreparedStatement preparedStatementCity = con.prepareStatement(cityInsert);
        preparedStatementCity.setString(1, city);
        preparedStatementCity.setString(2, country);
        preparedStatementCity.setString(3, city);
        preparedStatementCity.setString(4, country);

        preparedStatementCity.execute();


        String citySearch = "select * from city where city = ? and country = ?";
        PreparedStatement preparedStatementCitySearch = con.prepareStatement(citySearch);
        preparedStatementCitySearch.setString(1, city);
        preparedStatementCitySearch.setString(2, country);
        ResultSet resultSetCity = preparedStatementCitySearch.executeQuery();

        ResultSet resultSet = getAllPost("admin");
        resultSet.last();
        int postcnt = resultSet.getRow();
        resultSet.beforeFirst();

        String sql = "insert into post (post_id,title, content, posting_time, posting_city_id, author_name, filename, file, isunknown) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postcnt);
        preparedStatement.setString(2, title);
        preparedStatement.setString(3, content);
        preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        if (resultSetCity.next()) {
            preparedStatement.setInt(5, resultSetCity.getInt("city_id"));
        }
        preparedStatement.setString(6, username);
        preparedStatement.setString(7, fileName);
        preparedStatement.setBytes(8, file);
        preparedStatement.setBoolean(9, isUnknown);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public ResultSet getAllPost(String username) throws SQLException {
        String sql = "select * from post except select p.* from shield s join post p on s.author_shielded = p.author_name where s.author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql, TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();

        return rs;
    }

    public ResultSet getIthIdPost(int postId) throws SQLException {
        String sql = "select * from post where post_id= ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);

        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getPublishedPost(String username) throws SQLException {
        String sql = "select * from post where author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql, TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();

        return rs;
    }

    public ResultSet getUserFollowBy(String username) throws SQLException {
        String sql = "select follow_author_name from author_followed_by where author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getPostLiked(int postId) throws SQLException {
        String sql = "select author_name from post_liked where post_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getPostShared(int postId) throws SQLException {
        String sql = "select author_name from post_shared where post_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getPostfavorited(int postId) throws SQLException {
        String sql = "select author_name from post_favorited where post_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public boolean likePost(String username, int postId) throws SQLException {
        String sql = "insert into post_liked (post_id, author_name) values (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        preparedStatement.setString(2, username);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean sharePost(String username, int postId) throws SQLException {
        String sql = "insert into post_shared (post_id, author_name) values (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        preparedStatement.setString(2, username);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
//            System.out.println(e);
            return false;
        }
    }

    public boolean favoritePost(String username, int postId) throws SQLException {
        String sql = "insert into post_favorited (post_id, author_name) values (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        preparedStatement.setString(2, username);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
//            System.out.println(e);
            return false;
        }
    }

    public boolean followUser(String username, String followUsername) throws SQLException {
        String sql = "insert into author_followed_by (author_name, follow_author_name) values (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(2, followUsername);
        preparedStatement.setString(1, username);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean unFollowUser(String username, String followUsername) throws SQLException {
        String sql = "delete from author_followed_by where author_name = ? and follow_author_name = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(2, followUsername);
        preparedStatement.setString(1, username);
        try {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public ResultSet getPostReply(int postId) throws SQLException {
        String sql = "select * from reply where post_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, postId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getReplySecreply(int replyId) throws SQLException {
        String sql = "select * from sec_reply where reply_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, replyId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet searchPostOr(String author_name, String title, String content, int postId) throws SQLException {
        if (postId != -1) {
            String sql = "select * from post where post.author_name like ? or post.title like ? or post.content like ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, "%" + author_name + "%");
            preparedStatement.setString(2, "%" + title + "%");
            preparedStatement.setString(3, "%" + content + "%");
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        } else {
            String sql = "select * from post where post_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        }
    }

    public ResultSet searchPostAnd(String author_name, String title, String content, int postId) throws SQLException {
        if (postId != -1) {
            String sql = "select * from post where post.author_name like ? and post.title like ? and post.content like ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, "%" + author_name + "%");
            preparedStatement.setString(2, "%" + title + "%");
            preparedStatement.setString(3, "%" + content + "%");
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        } else {
            String sql = "select * from post where post_id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, postId);
            ResultSet rs = preparedStatement.executeQuery();
            return rs;
        }
    }

    public ResultSet getUserHadReply(String username) throws SQLException {
        String sql = "select * from reply r join post p on p.post_id = r.post_id where r.reply_author = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getUserHadLiked(String username) throws SQLException {
        String sql = "with tableTmp as ( select * from post_liked where author_name = ? ) select p.*  from post p join tableTmp t on p.post_id = t.post_id";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getUserHadShared(String username) throws SQLException {
        String sql = "with tableTmp as ( select * from post_shared where author_name = ?) select * from post p join tableTmp t on p.post_id = t.post_id";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public ResultSet getUserHadFavorited(String username) throws SQLException {
        String sql = "with tableTmp as ( select * from post_favorited where author_name = ?) select * from post p join tableTmp t on p.post_id = t.post_id";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public boolean blockUser(String username, String beBlockedUserName) throws SQLException {
        String sql = "insert into shield (author_name, author_shielded) values (?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, beBlockedUserName);
        if (preparedStatement.executeUpdate() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public ResultSet getIthreply(int replyId) throws SQLException {
        String sql = "select reply_content, reply_author, reply_stars from reply where reply_id = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, replyId);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }

    public boolean replyPost(int postId, String replyContent, String replyAuthorName) throws SQLException {
        String sqlGetReplyCnt = "select * from reply";
        PreparedStatement preparedStatementGetReplyCnt = con.prepareStatement(sqlGetReplyCnt, TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = preparedStatementGetReplyCnt.executeQuery();
        rs.last();
        int replyCnt = rs.getRow();


        String sql = "insert into reply (post_id, reply_content, reply_author, reply_stars, reply_id) values (?, ?, ?,0,?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql, TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        preparedStatement.setInt(1, postId);
        preparedStatement.setString(2, replyContent);
        preparedStatement.setString(3, replyAuthorName);
        preparedStatement.setInt(4, replyCnt + 1);
        if (preparedStatement.executeUpdate() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean replySecReply(int replyId, String secReplyContent, String replyAuthorName) throws SQLException {
        String sql = "insert into sec_reply (reply_id, sec_reply_content, sec_reply_author, sec_reply_stars) values (?, ?, ?, 0)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, replyId);
        preparedStatement.setString(2, secReplyContent);
        preparedStatement.setString(3, replyAuthorName);
        if (preparedStatement.executeUpdate() == 0) {
            return false;
        } else {
            return true;
        }
    }                                                                                                               //s 用时间差倒数          参数              限制取的条数

    public ResultSet getHotSearches(int likedWeight, int sharedWeight, int favoritedWeight, int replyWeight, int timeDifferenceWeight, int timeDivParameter, int limit) throws SQLException {
        String sql = "with LikedCnt_SharedCnt_FavoritedCnt_ReplyCnt_TimeDifference as (with LikedCnt_SharedCnt_FavoritedCnt_ReplyCnt\n" +
                "                                                                          as (with LikedCnt_SharedCnt_FavoritedCnt\n" +
                "                                                                                       as (with LikedCnt_SharedCnt\n" +
                "                                                                                                    as (with LikedCnt\n" +
                "                                                                                                                 as (select p.*, count(*) as likedCnt\n" +
                "                                                                                                                     from post p\n" +
                "                                                                                                                              left join post_liked pl on p.post_id = pl.post_id\n" +
                "                                                                                                                     group by p.post_id,\n" +
                "                                                                                                                              p.title,\n" +
                "                                                                                                                              p.content,\n" +
                "                                                                                                                              p.posting_time,\n" +
                "                                                                                                                              p.posting_city_id,\n" +
                "                                                                                                                              p.posting_city_id,\n" +
                "                                                                                                                              p.author_name,\n" +
                "                                                                                                                              p.filename,\n" +
                "                                                                                                                              p.file,\n" +
                "                                                                                                                              p.isunknown)\n" +
                "                                                                                                        select p.*, count(*) as sharedCnt\n" +
                "                                                                                                        from LikedCnt p\n" +
                "                                                                                                                 left join post_shared ps on p.post_id = ps.post_id\n" +
                "                                                                                                        group by p.post_id,\n" +
                "                                                                                                                 p.title,\n" +
                "                                                                                                                 p.content,\n" +
                "                                                                                                                 p.posting_time,\n" +
                "                                                                                                                 p.posting_city_id,\n" +
                "                                                                                                                 p.posting_city_id,\n" +
                "                                                                                                                 p.author_name,\n" +
                "                                                                                                                 p.filename,\n" +
                "                                                                                                                 p.file,\n" +
                "                                                                                                                 p.isunknown,\n" +
                "                                                                                                                 p.likedCnt)\n" +
                "                                                                                           select p.*, count(*) as favoritedCnt\n" +
                "                                                                                           from LikedCnt_SharedCnt p\n" +
                "                                                                                                    left join post_favorited pf on p.post_id = pf.post_id\n" +
                "                                                                                           group by p.post_id, p.title,\n" +
                "                                                                                                    p.content,\n" +
                "                                                                                                    p.posting_time,\n" +
                "                                                                                                    p.posting_city_id,\n" +
                "                                                                                                    p.posting_city_id,\n" +
                "                                                                                                    p.author_name,\n" +
                "                                                                                                    p.filename, p.file,\n" +
                "                                                                                                    p.isunknown,\n" +
                "                                                                                                    p.likedCnt,\n" +
                "                                                                                                    p.sharedCnt)\n" +
                "                                                                              select p.*, count(*) as replyCnt\n" +
                "                                                                              from LikedCnt_SharedCnt_FavoritedCnt p\n" +
                "                                                                                       left join reply r on p.post_id = r.post_id\n" +
                "                                                                              group by p.post_id, p.title, p.content,\n" +
                "                                                                                       p.posting_time,\n" +
                "                                                                                       p.posting_city_id,\n" +
                "                                                                                       p.posting_city_id, p.author_name,\n" +
                "                                                                                       p.filename, p.file, p.isunknown,\n" +
                "                                                                                       p.likedCnt, p.sharedCnt,\n" +
                "                                                                                       p.favoritedCnt)\n" +
                "\n" +
                "                                                                 select *,\n" +
                "                                                                        div("+timeDivParameter+",\n" +
                "                                                                            (extract(seconds from now() - posting_time) *\n" +
                "                                                                             1 +\n" +
                "                                                                             extract(minutes from now() - posting_time) *\n" +
                "                                                                             60 +\n" +
                "                                                                             extract(hours from now() - posting_time) *\n" +
                "                                                                             60 * 60 +\n" +
                "                                                                             extract(days from now() - posting_time) *\n" +
                "                                                                             24 * 60 * 60)::integer) as timeDifference\n" +
                "                                                                 from LikedCnt_SharedCnt_FavoritedCnt_ReplyCnt)\n" +
                "select *, (+likedCnt * "+likedWeight+" + sharedCnt * "+sharedWeight+" + favoritedCnt * "+favoritedWeight+" + replyCnt * "+replyWeight+" + timeDifference * "+timeDifferenceWeight+") as totalWeight\n" +
                "from LikedCnt_SharedCnt_FavoritedCnt_ReplyCnt_TimeDifference order by totalWeight desc limit "+limit+";\n";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        return rs;
    }
}