# 2018/09/27-23:59:55 — 2018/09/28-00:00:05
http POST 'localhost:4242/api/put' << EOF
[
  {
    "metric": "metric-a",
    "timestamp": 1538092800,
    "value": 10,
    "tags": {
      "t1": "v1"
    }
  },
  {
    "metric": "metric-a",
    "timestamp": 1538092801,
    "value": 20,
    "tags": {
      "t1": "v1"
    }
  },
  {
    "metric": "metric-b",
    "timestamp": 1538092802,
    "value": 30,
    "tags": {
      "t2": "v2"
    }
  },
  {
    "metric": "metric-b",
    "timestamp": 1538092803,
    "value": 40,
    "tags": {
      "t2": "v2"
    }
  }
]
EOF
