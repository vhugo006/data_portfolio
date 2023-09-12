import time

import boto3

stream_name = "DataPortfolioStream"  # Replace with your stream name
kinesis_client = boto3.client('kinesis')


def consume_stream():
    shard_iterator_response = kinesis_client.get_shard_iterator(
        StreamName=stream_name,
        ShardId="shardId-000000000000",
        ShardIteratorType="LATEST"
    )

    shard_iterator = shard_iterator_response["ShardIterator"]

    while True:

        records_response = kinesis_client.get_records(ShardIterator=shard_iterator, Limit=10)

        records = records_response["Records"]
        print(f"Records size: {len(records)}")

        if not records:
            print("No records to consume.")
            # break

        for record in records:
            print(f"Consumed: {record['Data']}")

        shard_iterator = records_response["NextShardIterator"]
        time.sleep(5)


def main():
    consume_stream()


if __name__ == "__main__":
    main()
