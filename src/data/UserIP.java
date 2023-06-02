package data;

import lib.SQL;

import java.util.ArrayList;
import java.util.List;

public class UserIP {
    private int id;
    private String ip;
    private int online; //1 is online, 0 is offline

    public UserIP(int id, String ip, int online){
        setID(id);
        setIP(ip);
        setOnline(online);
    }

    public int getID() {
        return id;
    }
    public String getIP() {
        return ip;
    }
    public int getOnline() {
        return online;
    }

    public static List<UserIP> getIpListById(int id){
        return SQL.getUserIPTable(id);
    }

    public void setID(int id) {
        this.id = id;
    }
    public void setIP(String ip) {
        this.ip = ip;
    }
    public void setOnline(int online) {
        this.online = online;
    }

    public static boolean ipExist(UserIP userIP){
        for(UserIP item : getIpListById(userIP.getID()))if(userIP.getIP().equals(item.getIP()))return true;
        return false;
    }
     public static boolean userOnline(UserIP userIP){
         for(UserIP item : getIpListById(userIP.getID()))if(item.getOnline() == 1)return true;
         return false;
     }

    public static int login(UserIP userIP){
        if(ipExist(userIP))return 1; //success
        else {
            if (userOnline(userIP))return -1; //fail
            else return 0;// success but new IP
        }
    }
    public static void signIn(UserIP userIP){
        if(!ipExist(userIP))SQL.addUserIP(userIP);
        else SQL.sign(userIP, 1);
    }
    public static void signOut(UserIP userIP){
        SQL.sign(userIP, 0);
    }

}
