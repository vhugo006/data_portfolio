import time

import boto3
from botocore.exceptions import NoCredentialsError

stream_name = "DataPortfolioStream"  # Replace with your stream name


def send_data_with_sdk(data):

    try:
        print(f"Initializing kinesis client...")

        kinesis_client = boto3.client('kinesis')
        response = kinesis_client.put_record(
            StreamName=stream_name,
            Data=data.encode('utf-8'),
            PartitionKey="1"
        )
        print(f"Record sent via SDK. Data: {data}. ShardId: {response['ShardId']}")
    except NoCredentialsError:
        print("Could not locate valid credentials. Please check your AWS credentials configuration.")


def main():
    for i in range(1, 11):
        message = f"Message {i}"
        send_data_with_sdk(message)
        time.sleep(1)


if __name__ == "__main__":
    main()
