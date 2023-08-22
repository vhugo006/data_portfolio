import time

import boto3

stream_name = "DataPortfolioStream"  # Replace with your stream name


def consume_stream():

    kinesis_client = boto3.client('kinesis')

    shard_iterator = kinesis_client.get_shard_iterator(
        StreamName=stream_name,
        ShardId="shardId-000000000000",
        ShardIteratorType="LATEST"
    )

    while True:
        print(f"ShardIterator: {shard_iterator}")

        response = kinesis_client.get_records(ShardIterator=shard_iterator["ShardIterator"], Limit=10)
        records = response["Records"]
        print(f"Records: {records}")

        if not records:
            print("No more records to consume.")
            # break

        for record in records:
            print(f"Consumed: {record['Data']}")

        shard_iterator = response["NextShardIterator"]
        time.sleep(5)


def main():
    consume_stream()


if __name__ == "__main__":
    main()
