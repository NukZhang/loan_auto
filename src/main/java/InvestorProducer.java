import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class InvestorProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("x-max-priority", 10);
        channel.queueDeclare(LoanQueuesConstant.AUTO_INVESTOR_QUEUE,true,false,false,param);
        //分发信息
        String message="investor_1";
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.priority(5);//默认权重0，手动投资权重5，则优先处理手动投资
        AMQP.BasicProperties properties = builder.build();
        channel.basicPublish("", LoanQueuesConstant.AUTO_INVESTOR_QUEUE,
                properties,message.getBytes());
        System.out.println("new user added '"+message+"'");
        channel.close();
        connection.close();
    }
}
