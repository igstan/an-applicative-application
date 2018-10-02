http POST 'localhost:4242/api/query' << EOF
{
  "start": "2018/05/01 00:10:00",
  "end": "2018/05/01 00:10:02",
  "timezone": "UTC",
  "queries": [
    {
      "metric": "metric-a",
      "aggregator": "none",
      "tags": {}
    },
    {
      "metric": "metric-b",
      "aggregator": "none",
      "tags": {}
    }
  ]
}
EOF
