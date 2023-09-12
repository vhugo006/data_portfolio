
package data.portfolio;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.producer.KinesisProducer;
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;

import static com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration.ThreadingModel.PER_REQUEST;

public class KinesisProducerApp {

    private static final Logger log = LoggerFactory.getLogger(KinesisProducerApp.class);
    private static final String STREAM_NAME = "DataPortfolioStream";
    private static final String REGION = "us-east-1";
    private static final String TIMESTAMP = Long.toString(System.currentTimeMillis());

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        ProducerConfig producerConfig = new ProducerConfig(REGION);
        KinesisProducer kinesisProducer = new KinesisProducer(producerConfig.getProducerConfiguration());

        log.info("Adding message . . .");
        ByteBuffer data = ByteBuffer.wrap(("Hello Kinesis").getBytes());
        kinesisProducer.addUserRecord(STREAM_NAME, TIMESTAMP, randomExplicitHashKey(), data);

        kinesisProducer.flushSync();
        kinesisProducer.destroy();
    }

    static class ProducerConfig {

        private final KinesisProducerConfiguration producerConfiguration;

        public ProducerConfig(String region) {
            producerConfiguration = new KinesisProducerConfiguration();
            producerConfiguration.setRegion(region);
            producerConfiguration.setCredentialsProvider(new DefaultAWSCredentialsProviderChain());
            producerConfiguration.setMaxConnections(10);
            producerConfiguration.setRequestTimeout(60000);
            producerConfiguration.setRecordMaxBufferedTime(5000);
            producerConfiguration.setThreadingModel(PER_REQUEST);
            producerConfiguration.setThreadPoolSize(10);
            producerConfiguration.setAggregationEnabled(false);
        }

        public KinesisProducerConfiguration getProducerConfiguration() {
            return producerConfiguration;
        }
    }

    public static String randomExplicitHashKey() {
        return new BigInteger(128, RANDOM).toString(10);
    }
}