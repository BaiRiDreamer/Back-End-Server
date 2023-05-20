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

    public Response execute () throws Exception
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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                return new Response("false", e.getMessage());
            }
        }
        else if (command.equals("registerNewUser"))
        {
            if (database.isUserExist(args[0], args[1]))
            {
                throw new Exception("用户名或者ID已经存在");
            }
            else
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
                    System.err.println(e);
                    return new Response("DatabaseErr", e.getMessage());
                }
            }
        }
        else if (command.equals("PublishPost"))
        {
            try
            {
                boolean tmp = args[6].equals("true");
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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getAllPost"))
        {
            try
            {
                ResultSet resultSet = database.getAllPost(args[0]);
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
                    jsonObject.put("isUnKnown", resultSet.getBoolean("isunKnown"));

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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
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
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getPostCnt"))
        {
            try
            {
                ResultSet resultSet = database.getAllPost(args[0]);
                resultSet.last();
                int cnt = resultSet.getRow();
                resultSet.beforeFirst();
                return new Response("true", Integer.toString(cnt));
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
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
                resultSet.beforeFirst();
                return new Response("true", Integer.toString(mypostCnt));
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getUserFollowBy"))
        {
            try
            {
                ResultSet resultSet = database.getUserFollowBy(args[0]);
                JSONArray jsonArray = new JSONArray();
//                List<String> authorFollowedBy = new ArrayList<>();

                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
//                    authorFollowedBy.add(resultSet.getString("follow_author_name"));
                    jsonObject.put("followedAuthorName", resultSet.getString("follow_author_name"));
                    jsonArray.add(jsonObject);
                }
//                jsonObject.put("authorFollowedBy", authorFollowedBy);
//                jsonArray.add(jsonObject);

                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该用户还没有关注任何人捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getPostLiked"))
        {
            try
            {
                ResultSet resultSet = database.getPostLiked(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该帖子还没有被任何人喜欢捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getPostShared"))
        {
            try
            {
                ResultSet resultSet = database.getPostShared(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该帖子还没有被任何人分享捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getPostfavorited"))
        {
            try
            {
                ResultSet resultSet = database.getPostfavorited(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("author_name", resultSet.getString("author_name"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该帖子还没有被任何人收藏捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("likePost"))
        {
            try
            {
                if (database.likePost(args[0], Integer.parseInt(args[1])))
                {
                    return new Response("true", "点赞成功");
                }
                else
                {
                    return new Response("false", "点赞失败");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("sharePost"))
        {
            try
            {
                if (database.sharePost((args[0]), Integer.parseInt(args[1])))
                {
                    return new Response("true", "分享成功");
                }
                else
                {
                    return new Response("false", "分享失败");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("favoritePost"))
        {
            try
            {
                if (database.favoritePost(args[0], Integer.parseInt(args[1])))
                {
                    return new Response("true", "收藏成功");
                }
                else
                {
                    return new Response("false", "收藏失败");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("followUser"))
        {
            try
            {
                if (database.followUser(args[0], args[1]))
                {
                    return new Response("true", "关注成功");
                }
                else
                {
                    return new Response("false", "关注失败");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("unFollowUser"))
        {
            try
            {
                if (database.unFollowUser(args[0], args[1]))
                {
                    return new Response("true", "取消关注成功");
                }
                else
                {
                    return new Response("false", "取消关注失败");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getPostReply"))
        {
            try
            {
                ResultSet resultSet = database.getPostReply(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reply_id", resultSet.getInt("reply_id"));
                    jsonObject.put("post_id", resultSet.getInt("post_id"));
                    jsonObject.put("reply_content", resultSet.getString("reply_content"));
                    jsonObject.put("reply_stars", resultSet.getInt("reply_stars"));
                    jsonObject.put("reply_author", resultSet.getString("reply_author"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该帖子还没有回复捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }

        }
        else if (command.equals("getReplySecreply"))
        {
            try
            {
                ResultSet resultSet = database.getReplySecreply(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sec_reply_id", resultSet.getInt("sec_reply_id"));
                    jsonObject.put("reply_id", resultSet.getInt("reply_id"));
                    jsonObject.put("sec_reply_content", resultSet.getString("sec_reply_content"));
                    jsonObject.put("sec_reply_stars", resultSet.getInt("sec_reply_stars"));
                    jsonObject.put("sec_reply_author", resultSet.getString("sec_reply_author"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该回复还没有二级回复捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        /**
         * 注意注意，该处是近似搜索，即搜索的是包含关键字的帖子
         */
        else if (command.equals("searchPostOr"))
        {
            try
            {
                ResultSet resultSet = database.searchPostOr(args[0], args[1], args[2], Integer.parseInt(args[3]));
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
                    return new Response("false", "没有找到相关帖子捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        /**
         * 注意注意，该处是近似搜索，即搜索的是包含关键字的帖子
         */
        else if (command.equals("searchPostAnd"))
        {
            try
            {
                ResultSet resultSet = database.searchPostAnd(args[0], args[1], args[2], Integer.parseInt(args[3]));
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
                    return new Response("false", "没有找到相关帖子捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getUserHadReply"))
        {
            try
            {
                ResultSet resultSet = database.getUserHadReply(args[0]);
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reply_id", resultSet.getInt("reply_id"));
                    jsonObject.put("post_id", resultSet.getInt("post_id"));
                    jsonObject.put("reply_content", resultSet.getString("reply_content"));
                    jsonObject.put("reply_stars", resultSet.getInt("reply_stars"));
                    jsonObject.put("content", resultSet.getString("content"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该用户还没有回复捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getUserHadLiked"))
        {
            try
            {
                ResultSet resultSet = database.getUserHadLiked(args[0]);
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
                    return new Response("false", "该用户还没有点赞过任何帖子捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getUserHadShared"))
        {
            try
            {
                ResultSet resultSet = database.getUserHadShared(args[0]);
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
                    return new Response("false", "该用户还没有分享过任何帖子捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getUserHadFavorited"))
        {
            try
            {
                ResultSet resultSet = database.getUserHadFavorited(args[0]);
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
                    return new Response("false", "该用户还没有收藏过任何帖子捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("blockUser"))
        {
            try
            {
                if (database.blockUser(args[0], args[1]))
                {
                    return new Response("true", "已屏蔽该用户");
                }
                else
                {
                    return new Response("false", "该用户不存在,你在逗我玩么");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getIthreply"))
        {
            try
            {
                ResultSet resultSet = database.getIthreply(Integer.parseInt(args[0]));
                JSONArray jsonArray = new JSONArray();
                while (resultSet.next())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reply_content", resultSet.getString("reply_content"));
                    jsonObject.put("reply_author", resultSet.getString("reply_author"));
                    jsonObject.put("reply_stars", resultSet.getInt("reply_stars"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "该回复不存在捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("replyPost"))
        {
            try
            {
                if (database.replyPost(Integer.parseInt(args[0]), args[1], args[2]))
                {
                    return new Response("true", "回复成功");
                }
                else
                {
                    return new Response("false", "该帖子不存在捏");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("replySecReply"))
        {
            try
            {
                if (database.replySecReply(Integer.parseInt(args[0]), args[1], args[2]))
                {
                    return new Response("true", "回复成功");
                }
                else
                {
                    return new Response("false", "该回复不存在捏");
                }
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else if (command.equals("getHotSearches"))
        {
            try
            {
                ResultSet resultSet = database.getHotSearches(Integer.parseInt(args[0]),Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
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
                    jsonObject.put("totalWeight", resultSet.getInt("totalWeight"));
                    jsonObject.put("likeCnt", resultSet.getInt("likeCnt"));
                    jsonObject.put("sharedCnt", resultSet.getInt("sharedCnt"));
                    jsonObject.put("favoritedCnt", resultSet.getInt("favoritedCnt"));
                    jsonObject.put("replyCnt", resultSet.getInt("replyCnt"));
                    jsonObject.put("timeDifference", resultSet.getInt("timeDifference"));
                    jsonArray.add(jsonObject);
                }
                if (jsonArray.isEmpty())
                {
                    return new Response("false", "暂时还没有热搜可以呈现捏");
                }
                String jsonString = jsonArray.toString();
                return new Response("true", jsonString);
            }
            catch (SQLException e)
            {
                System.err.println(e.getMessage());
                return new Response("DatabaseErr", e.getMessage());
            }
        }
        else
        {
            return new Response("false", "未知命令");
        }
    }
}

