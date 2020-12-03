import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.command.ActiveMQTextMessage;

import java.io.*;
import java.nio.file.*;

/**
    Sender :
    Usage : run ActiveMQClient send <nb_message> <brocker: artemis|>
    Exemple : 
        send 0 artemis      => bombarde le broker artemis
        send 10             => envoi 10 messages broker standard
    Receiver :
        Usage : run ActiveMQClient receive <brocker: artemis|>
    Exemple : 
        receive artemis      => écoute le broker artemis
        receive             => écoute le broker standard
 */
public class AMQSender extends Thread {
    
    
  
    private static final long MB_FILE_SIZE_2 = 2L * 1024; // 2Mo * 1024 * 1024; // 2 GiB message
    private void createBigFile(final File file,final long size) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        try (BufferedOutputStream buffOut = new BufferedOutputStream(fileOut)) {
            byte[] outBuffer = new byte[1024 * 1024];
            for (long i = 0; i < size; i += outBuffer.length) {
                buffOut.write(outBuffer);
            }
        }
    }
      
    public static String DEFAULT_QUEUE = "TooTooBig.Queue";
    
    public static String  username = "cli";//"admin";
    public static String  password = "cli"; //"admin";
    public static String  brokers =  "failover:(tcp://172.20.0.100:61616,tcp://172.20.0.101:61617)?randomize=false";

    public static void main(String[] args) throws Exception {
        //params
        int totalMessage = Integer.parseInt(args[0]);
        String queueName = DEFAULT_QUEUE;
        if(args.length > 1) {
            queueName = args[1];
        }
        //send messages
        
        if(totalMessage > 0) {
          
            for(int i=0;i<totalMessage;i++){
                System.out.println("sending message "+i);
                sendFile("./send.me.xml",brokers,username,password,queueName);
                
                //quick pause
                //try{ Thread.sleep(1); }catch(Exception e){}
            }
        } else {
            int i=0;
            while(true) {
                System.out.println("sending message "+i +" to " + queueName);
                sendFile("./send.me.xml",brokers,username,password,queueName);
                
                i++;
                //quick pause
                //try{ Thread.sleep(1); }catch(Exception e){}
            }
        }
    }
    
    public static void sendFile(String filepath,String broker,String u, String p, String queue) {
        String msg = null;
        try {
            msg = readFileAsString(filepath);
        } catch(Exception e){
            e.printStackTrace();
        }
        send(msg,broker,u,p,queue);
    }

    public static void send(String msg,String broker,String u, String p, String queue) {
        try {
               
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(u,p,broker);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queue);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            String text = msg;
			
			ActiveMQTextMessage message = new ActiveMQTextMessage();
            //TextMessage message = session.createTextMessage(text);
			
            message.setStringProperty("tag","selector"+(new java.util.Random().ints(1, (2 + 1)).limit(1).findFirst().getAsInt()));
            message.setStringProperty("https://Soget.EM.Schemas.PropertySchema#Owner","MSET");
            message.setJMSType("xml");
            message.setText(text);
			
            producer.send(message);
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Error while trying to send message: " + e);
            e.printStackTrace();
        }

    }

    public static String readFileAsString(String fileName)throws Exception 
    { 
      String data = ""; 
      data = new String(Files.readAllBytes(Paths.get(fileName))); 
      return data; 
    } 

}