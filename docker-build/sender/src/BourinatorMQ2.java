import org.apache.activemq.ActiveMQConnectionFactory;
 
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

import java.io.*;
import java.nio.file.*;

/**
    Sender :
    Usage : run BourinatorMQ2 send <nb_message> 
    Exemple : 
        send 0 artemis      => bombarde le broker artemis
     
    Receiver :
        Usage : run BourinatorMQ2 receive 
    Exemple : 
        receive 
      
 */
public class BourinatorMQ2 {
    
  
    public static String queueName = "Bourin.Queue";  
    public static String username = "cli";
    public static String password = "cli";
    public static String brokerPP =  "failover:(tcp://localhost:61616)";



	
    public static void main(String[] args) throws Exception {
        int nb = 0;
        String pwd = password, login = username, broker = brokerPP /*brokers*/, fpath = ".";
        if(args!=null && args.length >=2) {
            
            nb = Integer.parseInt(args[0]);
            fpath = args[1];
        }
        else{
            System.out.println("Usage : run BourinatorMQ2 <nb_message> <fichier>");
        }
 

        if( nb > 0) {
            for(int i=0;i<nb;i++){
                sendFile(fpath,broker,login,pwd,queueName);
                Thread.sleep(1);
            }
        }
        else if( nb == 0) {
                int i = 0;
                while(true){
                    sendFile(fpath,broker,login,pwd,queueName);
                    Thread.sleep(1);
                    i++;
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
            String text = msg/* + " (" + msg.hashCode() +")"*/;
            TextMessage message = session.createTextMessage(text);
            System.out.println("Sent message: "+ message.hashCode() );
            producer.send(message);
           // session.close();
           // connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
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