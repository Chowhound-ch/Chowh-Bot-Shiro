//package per.chowhound.bot.config;
//
//import org.springframework.amqp.core.*;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//
///**
// *
// * @author : Chowhound
// * @since : 2025/07/29 - 16:13
// */
//@Configuration
//public class RabbitmqTopicConfig {
//    public static final String QUEUE_INFORM_JM_REQ = "queue_inform_jm_request";
//    public static final String QUEUE_INFORM_JM_RESP = "queue_inform_jm_response";
//    public static final String EXCHANGE_TOPICS_INFORM = "exchange_topics_inform";
//    public static final String ROUTING_KEY_JM_REQ = "bot.jm.request.#";
//    public static final String ROUTING_KEY_JM_RESP = "bot.jm.response.#";
//    //队列 起名：TestDirectQueue
//    @Bean(QUEUE_INFORM_JM_REQ)
//    public Queue jmcomicQueueRequest() {
//        return new Queue(QUEUE_INFORM_JM_REQ,true);
//    }
//
//    @Bean(QUEUE_INFORM_JM_RESP)
//    public Queue jmcomicQueueResponse() {
//        return new Queue(QUEUE_INFORM_JM_RESP,true);
//    }
//
//    //Direct交换机 起名：TestDirectExchange
//    @Bean(EXCHANGE_TOPICS_INFORM)
//    Exchange topicExchange() {
//      //  return new DirectExchange("TestDirectExchange",true,true);
//        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
//
//    }
//
//    @Bean
//    public Binding bindingQueueRequest(@Qualifier(QUEUE_INFORM_JM_REQ) Queue queue,
//                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_JM_REQ).noargs();
//    }
//
//    @Bean
//    public Binding bindingQueueResponse(@Qualifier(QUEUE_INFORM_JM_RESP) Queue queue,
//                                              @Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
//        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_JM_RESP).noargs();
//    }
//}