import jmcomic
import pika
import json

# 创建配置对象
# 使用option对象来下载本子
# 等价写法: option.download_album(422866)

# 连接到RabbitMQ服务器
credentials = pika.PlainCredentials('local', '123456')
connection = pika.BlockingConnection(pika.ConnectionParameters(host='local', credentials=credentials))
channel = connection.channel()

channel.exchange_declare(exchange='exchange_topics_inform', exchange_type='topic', durable=True)
channel.queue_declare(queue="queue_inform_jm_request", durable=True)
channel.queue_bind(exchange='exchange_topics_inform', queue='queue_inform_jm_request')

option = None


# 发送消息
def send_message(message):
    channel.basic_publish(
        exchange='exchange_topics_inform',
        routing_key='bot.jm.response',
        body=message,
        properties=pika.BasicProperties(
            delivery_mode=2,  # 确保消息持久化
        )
    )
    print(f" [x] Sent {message}")


def get_jm_info(album):
    if album is None or album[0] is None:
        return "{}"
    info = album[0]
    return {
        'name': info.name,
        'related_list': info.related_list,
        'title': info.title,
        'tags': info.tags
    }


def callback(ch, method, properties, body):
    print(" [x] Received %r" % body.decode())
    jm_code = json.loads(body.decode())
    resp = jmcomic.download_album(jm_code['jmCode'], option)
    send_message(json.dumps({'data': get_jm_info(resp), 'type': jm_code['type'], 'number': jm_code['number']})
                 .encode('utf-8'))
    ch.basic_ack(delivery_tag=method.delivery_tag)


channel.basic_consume(queue='queue_inform_jm_request',  # 指定队列
                      auto_ack=False,  # 手动应答方式
                      on_message_callback=callback)

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    option = jmcomic.create_option_by_file('./option.yml')
    channel.start_consuming()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
