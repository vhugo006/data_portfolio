package data.portfolio;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.*;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;
import software.amazon.kinesis.retrieval.KinesisClientRecord;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

import java.nio.charset.StandardCharsets;

import static software.amazon.awssdk.regions.Region.US_EAST_1;

public class KinesisConsumerApp {

    private static final Logger log = LoggerFactory.getLogger(KinesisConsumerApp.class);
    private static final String STREAM_NAME = "DataPortfolioStream";
    private static final String WORKER_ID = "DataPortfolioWorker";
    private static final String APPLICATION_NAME = "DataPortfolioApplication";

    private static final KinesisAsyncClient kinesisClient = KinesisAsyncClient.create();
    private static final DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.create();
    private static final CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.create();


    public static void main(String... args) {

        KinesisAsyncClient kinesisClient = KinesisAsyncClient.builder()
                .region(US_EAST_1).build();

        ConfigsBuilder configsBuilder = getConfigsBuilder();

        Scheduler scheduler = new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig()
                        .retrievalSpecificConfig(new PollingConfig(STREAM_NAME, kinesisClient)));

        int exitCode = 0;

        System.out.println("Starting " + APPLICATION_NAME);
        log.info("Starting Kinesis Consumer. . .");

        try {
            scheduler.run();
        } catch (Exception e) {
            log.error("Caught exception while processing data.", e);
            exitCode = 1;
        }

        log.info("Shutting down {} ", APPLICATION_NAME);
        System.exit(exitCode);
    }

    @NotNull
    private static ConfigsBuilder getConfigsBuilder() {
        final ShardRecordProcessorFactory shardRecordProcessorFactory = () -> new ShardRecordProcessor() {
            @Override
            public void initialize(InitializationInput initializationInput) {
                log.info("Initializing record processor for shard: {}", initializationInput.shardId());
            }

            @Override
            public void processRecords(ProcessRecordsInput processRecordsInput) {
                for (KinesisClientRecord record : processRecordsInput.records()) {
                    String data = StandardCharsets.UTF_8.decode(record.data()).toString();
                    log.info("Data received: {}", data);
                }
            }

            @Override
            public void leaseLost(LeaseLostInput leaseLostInput) {
                log.info("Lost lease, so terminating...");
            }

            @Override
            public void shardEnded(ShardEndedInput shardEndedInput) {
                log.info("Reached shard end checkpointing.");
                try {
                    shardEndedInput.checkpointer().checkpoint();
                } catch (InvalidStateException | ShutdownException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
                log.info("Shutdown requested.");
            }
        };

        return new ConfigsBuilder(
                STREAM_NAME,
                APPLICATION_NAME,
                kinesisClient,
                dynamoClient,
                cloudWatchClient, WORKER_ID, shardRecordProcessorFactory);
    }
}
