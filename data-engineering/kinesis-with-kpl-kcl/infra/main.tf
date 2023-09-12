provider "aws" {
  region = "us-east-1"
}

resource "aws_kinesis_stream" "data_portfolio_stream" {
  name        = "DataPortfolioStream"
  shard_count = 1
}
