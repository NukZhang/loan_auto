import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class AutoIvsConsumer {
    /**
     *
     * @param loanMoney 标的金额
     * @throws IOException
     * @throws TimeoutException
     */
    public static void autoInvest(int loanMoney) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //设置登录账号
        factory.setHost("localhost");
        //链接服务器
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        boolean autoAck = true; //自动应答
        String message ="";
        GetResponse resp ;
        do{
            resp = channel.basicGet(LoanQueuesConstant.AUTO_INVESTOR_QUEUE,autoAck);
            if(resp==null){
                System.out.println(LoanQueuesConstant.AUTO_INVESTOR_QUEUE+" 队列无消息");
                message="x";
                continue;
            }
            message = new String(resp.getBody(), "UTF-8");
            int investMoney = 1;//假定每个人投资1，真实则money不同
            System.out.println(String.format(" [x] Recv Count %s , msg = %s;"
                    ,resp.getMessageCount()
                    ,message));
            //投资完毕，该投资人自动排列到自动投标队尾
            channel.basicPublish("", LoanQueuesConstant.AUTO_NEXT_QUEUE,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("user added '"+message+"'");
            loanMoney-=investMoney;
            if(loanMoney==0){
                message="x";//投资完毕，退出消费
            }
            System.out.println("剩余金额："+loanMoney);
            doWork(message);
        }while(!"x".equals(message));
        channel.close();
        connection.close();
    }
    private static void doWork(String task) {
        try {
            Thread.sleep(1000); // 暂停1秒钟模拟服务消费时间
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
