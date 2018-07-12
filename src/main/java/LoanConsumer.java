import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class LoanConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(LoanQueuesConstant.LOAN_QUEUE, true, false, false, null);
        System.out.println("LoanConsumer  Waiting for messages");

        //每次从队列获取的数量
        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Loan  Received '" + message + "'");
                try {
//                    throw  new Exception();
                    doWork(message);
                }catch (Exception e){
                    channel.abort();
                }finally {
                    System.out.println("Consumer Done");
                    channel.basicAck(envelope.getDeliveryTag(),false);
                }
            }
        };
        boolean autoAck=false;
        //消息消费完成确认
        channel.basicConsume(LoanQueuesConstant.LOAN_QUEUE, autoAck, consumer);
    }
    private static void doWork(String task) throws Exception{
        try {
            Thread.sleep(1000); // 暂停1秒钟模拟服务消费时间
            AutoIvsConsumer.autoInvest(10);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
