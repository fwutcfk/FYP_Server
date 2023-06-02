package connect;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class Email {
    private final static String EmailSMTPHost = "smtp.gmail.com";
    private final static String EmailUser = "*********";
    private final static String EmailAccount = "*********@gmail.com";
    private final static String EmailPassword = "*********";
    private final static String AppPassword = "****************";
    private final static int PORT = 587;

    public static void Send(String toEmail, String emailText){
        String to = toEmail;

        String from = EmailAccount;
        final String username = EmailUser;
        final String password = AppPassword;

        String host = EmailSMTPHost;
        int port = PORT;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject("Testing Subject");

            message.setText(emailText);

            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    public static String Code(int length){
        String code = "";
        Random random = new Random();

        for (int i = 0;i < length;i++){
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";

            if(charOrNum.equals("char")){
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                code += (char) (random.nextInt(26) + temp);
            }
            else if(charOrNum.equals("num")){
                code += String.valueOf(random.nextInt(10));
            }

        }

        return code;
    }
}

