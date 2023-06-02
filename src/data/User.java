package data;

import lib.SQL;

import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private int state;
    private String code;

    public User(int id, String username, String password, String email, int state, String code){
        this.setId(id);
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setState(state);
        this.setCode(code);
    }

    public User(String username, String password, String email, int state, String code){
        this.setId(0);
        this.setUsername(username);
        this.setPassword(password);
        this.setEmail(email);
        this.setState(state);
        this.setCode(code);
    }

    public int getId() {
        return this.id;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public String getEmail() {
        return this.email;
    }
    public int getState() {
        return this.state;
    }
    public String getCode() {
        return this.code;
    }
    public static User getUserByName(String username, String password){
        List<User> userList = SQL.getUserTable(0);
        User user = null;

        for (User item: userList)
            if(item.getUsername().equals(username) && item.getPassword().equals(password))user = item;

        return user;
    }
    public static User getUserByID(int ID){
        List<User> userList = SQL.getUserTable(ID);
        User user = null;

        user = userList.get(0);

        return user;
    }
    public static int getIdByUserName(String username){
        List<User> userList = SQL.getUserTable(0);
        int ID = 0;

        for (User item: userList)
            if(item.getUsername().equals(username))ID = item.getId();

        return ID;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public static boolean UserExist(User user){
        List<User> UserList = SQL.getUserTable(0);

        for (User item: UserList)
            if(item.getUsername().equals(user.getUsername()))return true;

        return false;
    }

    public static int register(User user){
        if(UserExist(user))return -1; //User Exist
        else if(SQL.addUser(user)>0)return 1; //success
        else return 0; //fail
    }
    public static int activation(String code){
        if(SQL.activation(code)>0) return 1; //activation success
        else return 0; //activation fail
    }
    public static int login(String userName, String password){
        User user = getUserByName(userName, password);

        if(user != null) {
            if (user.getState() != 0) {
                return 1; //success
            } else return 0; //no activation
        } else return -1; //username or password incorrect

    }
}
