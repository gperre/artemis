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
public class ButinatorMQ  {
    
    
    public static String queueName = "PREPROD.FRA.Logistics.Air.Queue";
    
    public static String username = "cli";//"admin";
    public static String password = "cli"; //"admin";
    public static String brokers =  "failover:(tcp://localhost:61616)?randomize=false";
    public static String[] queues = new String[]{"PREPROD.FRA.Logistics.Air.Queue","PREPROD.FRA.Logistics.Movements.Queue","PREPROD.FRA.Logistics.IntegrateAnnouncement.Queue"};
    public static void main(String[] args) throws Exception {
      int pause = Integer.parseInt(args[0]);
	  
      int ind=0;
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
           // Destination destination = session.createQueue(queueName);
            //consumer = session.createConsumer(destination);
            MessageConsumer[] consumers = new MessageConsumer[queues.length];
            for(int i=0;i<queues.length;i++){
                Destination destination = session.createQueue(queues[i]);
                consumers[i] = session.createConsumer(destination);
            }

            while(true){

                for(int i=0;i<queues.length;i++) {
                    Message message = consumers[i].receive(10);
                    
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();
                        System.out.println("Received message from "+queues[i] +" (" + ind+")" );
                    } 
                    ind++;
                     //pause between scan
                    try{ Thread.sleep(pause); }catch(Exception e){}

                }
                
            }
           
    

            
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