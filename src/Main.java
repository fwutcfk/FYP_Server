import connect.Email;
import connect.Server;
import lib.SQL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class Main {
    public static final int DEFAULT_PORT = 1027;
    public static void main(String[] args) {
        //SQL.createUserTable();
        //SQL.createUserIPTable();

        //SQL.truncateAllTable();

        //text(DEFAULT_PORT);
        //System.out.println(getLocalIpAddress());
        start(DEFAULT_PORT);
        //Email.Send("lky110105@gmail.com","hello");
    }

    public static void start(int port){
        try{
            ServerSocket ss = new ServerSocket(port);
            System.out.printf("The server is running at port number %d%n", port);

            while (true){
                Socket s = ss.accept();

                Server server = new Server(s);
                if(s.isConnected())
                    server.start();

                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void text(int port){
        String filePath = "src/face/";
        int id = 0;
        String fileName = String.format("Face_%d.jpg",id);
        File file = new File(filePath+fileName);
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.printf("The server is running at port number %d%n", port);

            while (true){
                Socket s = ss.accept();

                // 获取Socket的输入流，用于接收数据
                InputStream inputStream = s.getInputStream();

                // 创建一个ByteArrayOutputStream对象，用于将接收到的数据写入字节数组
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                // 创建一个缓冲区字节数组，用于从输入流中读取数据
                byte[] buffer = new byte[1024];

                // 从输入流中读取数据，并将其写入ByteArrayOutputStream对象中
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Get the picture");

                // 将ByteArrayOutputStream对象中的字节数组转换为BufferedImage对象
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));

                // 关闭输入流和Socket对象
                inputStream.close();
                s.close();

                ImageIO.write(bufferedImage, "jpg", file);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address= addresses.nextElement();
                    if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && address instanceof java.net.Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}