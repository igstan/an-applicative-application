http POST 'localhost:4242/api/query' << EOF
{
  "start": "2018/05/01 00:10:00",
  "end": "2018/05/01 00:10:05",
  "timezone": "UTC",
  "queries": [
    {
      "aggregator": "none",
      "metric": "metric-a",
      "tags": {}
    },
    {
      "aggregator": "none",
      "metric": "metric-b",
      "tags": {}
    }
  ]
}
EOF
