package lib;

import connect.Email;
import data.User;
import data.UserIP;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQL {
    private static final String URL = "jdbc:mysql://localhost:3306/fyp";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "********";

    public static int addUser(User user){
        int rs = 0;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            String sql = "insert into user(username,password,email,state,code) values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setInt(4, user.getState());
            ps.setString(5, user.getCode());
            rs = ps.executeUpdate();

            String emailMsg = String.format("This is your activation code: %s%n", user.getCode());
            Email.Send(user.getEmail(), emailMsg);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }
    public static int addUserIP(UserIP userIP){
        int rs = 0;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            String sql = "insert into userip(id,ip,online) values(?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userIP.getID());
            ps.setString(2, userIP.getIP());
            ps.setInt(3, userIP.getOnline());
            rs = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public static List<User> getUserTable(int ID){
        List<User> UserList = new ArrayList<>();

        String sql = "select * from user";
        if(ID > 0)sql = sql + " where id=" + ID;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            ps = conn.prepareStatement(sql);
            ResultSet rs =ps.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                int state = rs.getInt("state");
                String code = rs.getString("code");
                User user = new User(id,username,password,email,state,code);
                UserList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return UserList;
    }
    public static List<UserIP> getUserIPTable(int ID){
        List<UserIP> UserIPList = new ArrayList<>();

        String sql = "select * from userip";
        if(ID > 0)sql = sql + " where id=" + ID;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            ps = conn.prepareStatement(sql);
            ResultSet rs =ps.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String ip = rs.getString("ip");
                int online = rs.getInt("online");
                UserIP userIP = new UserIP(id,ip,online);
                UserIPList.add(userIP);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return UserIPList;
    }

    public static int sqlCommand(String sql){
        int rs = 0;
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            ps = conn.prepareStatement(sql);
            rs = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public static void createUserTable(){
        String sql = "create table if not exists `user`(\n" +
                "    id int(11) primary key auto_increment,\n" +
                "    username varchar(255) not null,\n" +
                "    email varchar(255) not null,\n" +
                "    password varchar(255) not null,\n" +
                "    state int(1) not null default 0,\n" +
                "    code varchar(255) not null\n" +
                ")engine=InnoDB default charset=utf8;";

        int rs = sqlCommand(sql);

        System.out.println("Create the User table success.");
    }
    public static void createUserIPTable(){
        String sql = "create table if not exists `userip`(\n" +
                "    id int(11) not null,\n" +
                "    ip varchar(255) not null,\n" +
                "    online int(11) not null\n" +
                ")engine=InnoDB default charset=utf8;";

        int rs = sqlCommand(sql);

        System.out.println("Create the User IP table success.");
    }
    public static void truncate(String tableName){
        String sql = String.format("truncate %s", tableName);

        int rs = sqlCommand(sql);

        System.out.printf("Clear the %s table success.%n", tableName);
    }
    public static void truncateAllTable(){
        truncate("user");
        truncate("userip");
    }
    public static int activation(String code){
        Connection conn = null;
        PreparedStatement ps = null;
        int rs = 0;

        try{
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            String sql = "update user set state=1 where code=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, code);
            rs = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }
    public static int sign(UserIP userIP, int online){
        Connection conn = null;
        PreparedStatement ps = null;
        int rs = 0;

        try{
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            String sql = "update userip set online=? where id=? and ip=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, online);
            ps.setInt(2, userIP.getID());
            ps.setString(3, userIP.getIP());
            rs = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

}
