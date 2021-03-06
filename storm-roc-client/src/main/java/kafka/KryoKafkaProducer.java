package kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.message.Message;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.roc.hbase.KryoColumnValue;
import org.roc.hbase.KryoPut;
import org.roc.utils.KryoUtil;


public class KryoKafkaProducer {
	
	static Random rnd = new Random();
	public static ProducerConfig config = null;
	// 0-31
	public static String[] cnStr = { "眼不见", "心不烦", "听不闻", "意不乱", "名不求", "利不贪", "常交友", "广结缘", "遇分歧", "莫争辩", "和为贵", "忍为先", "常念经", "智慧现", "时观己", "少多言", "窗外事", "淡如烟", "做好人", "常布施", "行好事", "不展现", "说好话", "不妄言", "存好心", "断恶念", "心皈依", "持五戒", "断习气", "正知见", "念弥陀", "到彼岸" };
	// 0-37
	public static String[] cnName = { "刘晋荣", "孙宁", "邢洪锐", "陈云云", "张禄", "王增凤", "沈捷", "汪化言", "杨正机", "戴向军", "王延芳", "沈健", "刘伟峰", "解放军同志", "陆先生", "空军干部", "林之棋", "纪虹羽", "肖艳", "沈国金", "王燕峰", "徐若琳", "徐成业", "山西长治高新区临泰经贸有限公司", "李岸白", "赵嘉嘉", "王吏", "欧阳里", "顾春台", "张纬", "刘光华", "李永智", "杨桦", "李振平", "王学军", "张健",
			"张轶", "张昱" };

	public static String TOPIC = "example_3_kryoPut";

	static {
		Properties props = new Properties();
		props.put("metadata.broker.list", "10.0.31.188:9092,10.0.31.189:9092,10.0.31.190:9092");
		// props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("partitioner.class", "com.sugon.bigdata.pump.kafka.SimplePartitioner");
		props.put("request.required.acks", "1");
		config = new ProducerConfig(props);
	}

	// 生成 String类型的数据
	public static void produceKryoPutData() throws IOException {
		/*
		 * 
		 * create kafka topic shell: /usr/local/share/kafka/bin/kafka-topics.sh --create --replication-factor 1 --partitions 3 --topic example_3_kryoPut
		 * --zookeeper 10.0.31.195:2181,10.0.31.197:2181,10.0.31.199:2181/kafka
		 */

		KryoUtil util = new KryoUtil(KryoPut.class, KryoColumnValue.class, ArrayList.class);
		Put put = null;
		KryoPut kryoPut = null;

		long cnt= 0L;
		Producer<Message, byte[]> producer = new Producer<Message, byte[]>(config);
		try {
			while (true) {
				long timestamp = System.currentTimeMillis();
				put = new Put(Bytes.toBytes(timestamp));
				put.add(Bytes.toBytes("a"), Bytes.toBytes("name"), Bytes.toBytes(cnName[rnd.nextInt(37)]));
				put.add(Bytes.toBytes("a"), Bytes.toBytes("age"), Bytes.toBytes(rnd.nextInt(100)));
				put.add(Bytes.toBytes("b"), Bytes.toBytes("address"), Bytes.toBytes(cnStr[rnd.nextInt(31)]));
				kryoPut = new KryoPut(put);
				byte[] bytes = util.toBytes(kryoPut);
				System.out.println(Bytes.toString(bytes));
				// Message message = new Message(bytes);
				producer.send(new KeyedMessage<Message, byte[]>(TOPIC, bytes));
				System.out.println(cnt++);
			}
		} finally {
			producer.close();
		}
	}

	// 生成 String类型的数据
	public static void produceNormalKryoData() throws IOException {
		
	}

	public static void main(String[] args) throws IOException {
		produceKryoPutData();
	}
}
