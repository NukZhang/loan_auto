import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class LoanProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare(LoanQueuesConstant.LOAN_QUEUE,true,false,false,null);
        //发送标的
        String message="Loan"+new Date().getTime();
        channel.basicPublish("", LoanQueuesConstant.LOAN_QUEUE,
                MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        System.out.println("NewTask send '"+message+"'");
        channel.close();
        connection.close();
    }
    public static HashMap<String,Object> createLoan(int money){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("money",money);
        return null;
    }
}
