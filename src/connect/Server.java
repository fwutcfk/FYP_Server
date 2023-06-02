package connect;

import data.User;
import data.UserIP;
import lib.Face;

import java.io.*;
import java.net.Socket;

public class Server {
    private final int CODE_SIZE = 4;
    private Socket s;

    public Server(Socket s){
        this.s = s;
    }

    public void start(){
        try {
            String command, IP;

            DataInputStream dis = new DataInputStream(s.getInputStream());

            command = dis.readUTF();

            System.out.printf("Received command: %s%n", command);

            switch(command){
                case "Register":
                    register();
                    break;
                case "Login":
                    login();
                    break;
                case "Activation":
                    activation();
                    break;
                case "EmailAuthentication":
                    emailAuthentication();
                    break;
                case "SignIn":
                    signIn();
                    break;
                case "SignOut":
                    signOut();
                    break;
                case "FaceRegister":
                    faceRegister();
                    break;
                case "FaceLogin":
                    faceLogin();
                    break;
                default:
                    System.out.printf("Unknow command: %s%n", command);
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(){
        try{
            String userName, password, email, code;

            DataInputStream dis = new DataInputStream(s.getInputStream());

            userName = dis.readUTF();
            password = dis.readUTF();
            email = dis.readUTF();
            code = Email.Code(4);
            System.out.printf("Received data from client, UserName: %s, Password: %s, Email: %s, Generate Code: %s.%n",userName,password,email,code);

            User user = new User(userName,password,email,0,code);

            int registerCheck = User.register(user);

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            switch (registerCheck){
                case 1: //success
                    dos.writeUTF("Register Success.");
                    dos.writeBoolean(true);
                    int txtID = User.getIdByUserName(userName);
                    dos.writeInt(txtID);
                    System.out.println("Return id: " + txtID);
                    System.out.printf("The Account: %s register success.%n", userName);
                    break;
                case 0: //user exist
                    dos.writeUTF("User Exist.");
                    dos.writeBoolean(false);
                    System.out.printf("The Account: %s was exist.%n", userName);
                    break;
                case -1: //sql fail
                    dos.writeUTF("Register Fail.");
                    dos.writeBoolean(false);
                    System.out.printf("Account: %s register Fail.%n", userName);
                    break;
                default: //unknown
                    dos.writeUTF("Unknown Error.");
                    dos.writeBoolean(false);
                    System.out.printf("Account: %s register error. Unknown Error.%n", userName);
                    break;
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void activation(){
        try{
            String code;

            DataInputStream dis = new DataInputStream(s.getInputStream());

            code = dis.readUTF();

            int activationCheck = User.activation(code);

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            switch (activationCheck){
                case 1: //success
                    dos.writeUTF("Activation Success.");
                    dos.writeBoolean(true);
                    System.out.println("Activation Success.");
                    break;
                case 0: //code error
                    dos.writeUTF("Activation Fail. Check your code.");
                    dos.writeBoolean(false);
                    System.out.println("Activation Fail.");
                    break;
                default: //unknown
                    dos.writeUTF("Unknown Error.");
                    dos.writeBoolean(false);
                    System.out.println("Unknown activation error.");
                    break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void login(){
        try{
            String userName, password, ip;

            DataInputStream dis = new DataInputStream(s.getInputStream());

            userName = dis.readUTF();
            password = dis.readUTF();
            ip = dis.readUTF();

            int loginCheck = User.login(userName, password);

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            switch (loginCheck){
                case 1:
                    int id = User.getIdByUserName(userName);
                    UserIP userIP = new UserIP(id, ip, 0);
                    int ipCheck = UserIP.login(userIP);
                    dos.writeBoolean(true);
                    dos.writeInt(ipCheck);
                    if(ipCheck == 1){
                        dos.writeUTF("Login Success.");
                        dos.writeInt(id);
                        System.out.printf("The Account: %s login success.%n", userName);
                    }
                    else if(ipCheck == 0){
                        dos.writeUTF("Login Success.");
                        dos.writeInt(id);
                        System.out.printf("The Account: %s login success.%n", userName);
                    }
                    else if(ipCheck == -1){
                        dos.writeUTF("This account is currently online.");
                        System.out.printf("The Account: %s is currently online.%n", userName);
                    }
                    break;
                case 0:
                    dos.writeBoolean(false);
                    dos.writeUTF("The account hasn't activation\nPlease activation the account");
                    System.out.printf("The account: %s hasn't activation.%n", userName);
                    break;
                case -1:
                    dos.writeBoolean(false);
                    dos.writeUTF("Login fail");
                    System.out.printf("The Account: %s login fail.%n", userName);
                    break;
                default:
                    dos.writeBoolean(false);
                    dos.writeUTF("Unknown Error.");
                    System.out.println("Unknown login error.");
                    break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void emailAuthentication(){
        try{
            int id = 0;
            String ip;
            DataInputStream dis = new DataInputStream(s.getInputStream());

            id = dis.readInt();
            ip = dis.readUTF();
            User user = User.getUserByID(id);
            System.out.printf("Id: %d, UserName: %s request email authentication from %s.%n", id, user.getUsername(), ip);

            String code = Email.Code(4);
            System.out.printf("Generate the code: %s%n", code);

            String emailText = String.format("Your account has been logged in from a other IP (%s). This is your login code: %s%n", ip, code);
            Email.Send(user.getEmail(), emailText);

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            dos.writeUTF(code);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void signIn(){
        try{
            int id;
            String ip;

            DataInputStream dis = new DataInputStream(s.getInputStream());
            id = dis.readInt();
            ip = dis.readUTF();

            UserIP userIP = new UserIP(id, ip, 1);
            UserIP.signIn(userIP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void signOut(){
        try{
            int id;
            String ip;

            DataInputStream dis = new DataInputStream(s.getInputStream());
            id = dis.readInt();
            ip = dis.readUTF();

            UserIP userIP = new UserIP(id, ip, 0);
            UserIP.signOut(userIP);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void faceRegister(){
        String filePath = "src/face/";
        int id = 0;
        try {
            DataInputStream dis = new DataInputStream(s.getInputStream());
            id = dis.readInt();
            System.out.printf("Received the id: %d%n", id);

            int length = dis.readInt();
            byte[] data = new byte[length];
            dis.readFully(data);

            String fileName = String.format("Face_%d.jpg",id);
            FileOutputStream fos = new FileOutputStream(filePath+fileName);
            fos.write(data);
            System.out.printf("Update %s success%n", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void faceLogin(){
        try{
            int id = 0;

            DataInputStream dis = new DataInputStream(s.getInputStream());
            id = dis.readInt();

            String facePath = "src/face/";
            String face = String.format("Face_%d.jpg",id);

            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            if(new File(facePath, face).exists()){
                dos.writeBoolean(true);

                int length = dis.readInt();
                byte[] data = new byte[length];
                dis.readFully(data);

                String faceLocal = facePath + face;

                double faceLoginCheck = Face.compareFace(faceLocal, data);

                System.out.println("Similarity is " + faceLoginCheck);

                if(faceLoginCheck > 0.5)dos.writeBoolean(true);
                else dos.writeBoolean(false);
            }
            else {
                dos.writeBoolean(false);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
