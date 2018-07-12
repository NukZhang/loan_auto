import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AutoIvsProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare(LoanQueuesConstant.AUTO_INVESTOR_QUEUE,true,false,false,null);
        //分发信息
        for (int i=0;i<20;i++){
            String message="user"+i;
            channel.basicPublish("", LoanQueuesConstant.AUTO_INVESTOR_QUEUE,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("new user added '"+message+"'");
        }
        channel.close();
        connection.close();
    }
}
