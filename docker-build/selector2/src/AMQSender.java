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
    
    
    private static final long MB_FILE_SIZE_1 = 1L * 1024; // 2Mo * 1024 * 1024; // 2 GiB message
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
      
    public static String queueName = "TooTooBig.Queue";
    
    public static String username = "cli";//"admin";
    public static String password = "cli"; //"admin";
    public static String brokers =  "failover:(tcp://172.20.0.100:61616,tcp://172.20.0.101:61617)?randomize=false";

    public static void main(String[] args) throws Exception {
      
      
      
      
        int i=0;
      while(i<Integer.parseInt(args[0])){
        AMQSender amq = new AMQSender();
        amq.run();
        i++;
        System.out.println("i="+i);
      }
        
        

    }
    

    public void run()  {
        int nb = 0;
        String pwd = password, login = username, broker = brokers /*brokers*/, fpath = ".";
       
            nb = 1;
            fpath="./send.me.xml";
         
 
        if( true) {
            for(int i=0;i<nb;i++){
                sendFile(fpath,broker,login,pwd,queueName);
               //try{ Thread.sleep(1); }catch(Exception e){}
            }
            if( nb == 0) {
                int i = 0;
                while(true){
                    sendFile(fpath,broker,login,pwd,queueName);
                    try{ Thread.sleep(1); }catch(Exception e){}
                    i++;
                }
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
            
          //  msg = "<ns:IntegrateAnnouncement xmlns:ns=\"http://services.ssw.soget.fr/201511/\" xmlns:edi=\"http://schemas.ssw.soget.fr/edi/\" xmlns:ns1=\"http://schemas.ssw.soget.fr/201308/\" xmlns:ann=\"http://schemas.ssw.soget.fr/201511/announcement\" xmlns:ns2=\"http://schemas.ssw.soget.fr/201504/\"><ns:header><edi:Application>S-ONE_PSW</edi:Application><edi:Owner>EDIGREG</edi:Owner><edi:MessageType>http://services.ssw.soget.fr/201511/#IntegrateAnnouncement</edi:MessageType><edi:InterchangeId>2006232323230000</edi:InterchangeId>    <edi:Date>2017-02-27T08:45:45Z</edi:Date>    <edi:InterfaceCode>INTDOC</edi:InterfaceCode></ns:header><ns:announcement><ns1:Sender>CGREG</ns1:Sender><ns1:Receiver>SOGET</ns1:Receiver><ns1:Date>2017-02-27T08:45:45Z</ns1:Date><ann:AnnouncementType>85</ann:AnnouncementType><ann:Reference>VAG0000005831</ann:Reference><ann:Function>Create</ann:Function><ann:State>Valid</ann:State><ann:FreightAgent>CGREG</ann:FreightAgent><ann:Place>FRLEH</ann:Place><ann:HandlingPlace>TDF</ann:HandlingPlace><ann:Transports><ns1:Transport><ns1:PSW.VoyageAgentID>VAG0000005831</ns1:PSW.VoyageAgentID></ns1:Transport></ann:Transports><ann:HandlingUnits/><ann:AnnouncementDocuments><ann:AnnouncementDocument><ann:Reference>GSIDVMLXD</ann:Reference><ann:Type>705</ann:Type><ann:ProcessingIndicator>24</ann:ProcessingIndicator><ann:Locations><ns1:Location><ns1:Type>9</ns1:Type><ns1:Code>ESBIO</ns1:Code></ns1:Location><ns1:Location><ns1:Type>11</ns1:Type><ns1:Code>FRLEH</ns1:Code></ns1:Location><ns1:Location><ns1:Type>76</ns1:Type><ns1:Code>ESBIO</ns1:Code></ns1:Location><ns1:Location><ns1:Type>20</ns1:Type><ns1:Code>AUSYD</ns1:Code></ns1:Location></ann:Locations><ann:Goods><ann:Good><ann:ItemNumber>1</ann:ItemNumber><ann:Reference>MFQNGQXXG</ann:Reference>                    <ann:PackagingCode>CT</ann:PackagingCode>                    <ann:Quantity>395</ann:Quantity>                    <ann:Description>TYRES - REF 4220220889 FREIGHT COLLECT ALSO NOTIFY MAINFREIGHT INTERNATIONAL 25 NAWEENA ROAD</ann:Description><ann:Weight><ns1:MeasureGrossWeight><ns1:Value>13042</ns1:Value><ns1:Unit>KGM</ns1:Unit></ns1:MeasureGrossWeight>                    </ann:Weight>                </ann:Good>            </ann:Goods>        </ann:AnnouncementDocument>    </ann:AnnouncementDocuments></ns:announcement></ns:IntegrateAnnouncement>";
           
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
            session.close();
            connection.close();
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }

    }

    public static void receive(String broker,String u, String p, String queue) {
        try {
            
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(u,p,broker);   
            Connection connection = connectionFactory.createConnection();
            connection.start();
           // connection.setExceptionListener(this);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //ActiveMQQueue mq = new ActiveMQQueue(queue+"?exclusive=true");
            Destination destination = session.createQueue(queue);
            MessageConsumer consumer = session.createConsumer(destination);
            Message message = consumer.receive(1000);
           
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                if(text !=null)
                System.out.println("Received: " + text);
            } else {
               if(message!=null) System.out.println("Received: " + message);
            }
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
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