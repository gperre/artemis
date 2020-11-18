import org.apache.activemq.ActiveMQConnectionFactory;
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

import java.io.*;
import java.nio.file.*;

/**
    Sender :
    Usage : run ActiveMQClient send<nb_message>
    <brocker: artemis|>
    Exemple : 
        send 0 artemis      => bombarde le broker artemis
        send 10             => envoi 10 messages broker standard
    Receiver :
        Usage : run ActiveMQClient receive <brocker: artemis|>
    Exemple : 
        receive artemis      => écoute le broker artemis
        receive             => écoute le broker standard
 */
public class AMQConsumer  {
    
    
    public static String queueName = "TooTooBig.Queue";
    
    public static String username = "cli";//"admin";
    public static String password = "cli"; //"admin";
    public static String brokers =  "failover:(tcp://172.20.0.100:61616,tcp://172.20.0.101:61617)?randomize=false";

    public static void main(String[] args) throws Exception {
    //  int pause = Integer.parseInt(args[0]);
    int pause = 5000;
      int i=0;
      Connection connection =null;
      Session session = null;
      MessageConsumer consumer = null;
        try {
            
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username,password,brokers);   
            connection = connectionFactory.createConnection();
            connection.start();
           // connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //ActiveMQQueue mq = new ActiveMQQueue(queue+"?exclusive=true");
            Destination destination = session.createQueue(queueName);
            System.out.println("Consumer initialize with selector="+args[0]);
            consumer = session.createConsumer(destination,args[0]);
            while(true){
                Message message = consumer.receive(1000);
            
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received message with selector["+args[0]+"] from "+queueName +" (" + i+")=" + textMessage.getStringProperty("tag") );
                } 
                i++;
               try{ Thread.sleep(pause); }catch(Exception e){}
            }
            //pause between scan
            


         //   consumer.close();
         //   session.close();
         //   connection.close();

            
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        } finally {
            consumer.close();
            session.close();
            connection.close();
        }
      
        
        

    }
    

   

}