# 2018/05/01 00:10:00
http POST 'localhost:4242/api/put' << EOF
[
  {
    "metric": "metric-a",
    "timestamp": 1525133400,
    "value": 10,
    "tags": {
      "t1": "v1",
      "t2": "v2"
    }
  },
  {
    "metric": "metric-b",
    "timestamp": 1525133401,
    "value": 20,
    "tags": {
      "t1": "v1",
      "t2": "v2"
    }
  }
]
EOF
