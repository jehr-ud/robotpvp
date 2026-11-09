const express = require('express');
const app = express()
const port = 3000

app.get('/', (req, res) => {
  res.send({
    "status": "ok",
    "message": "running"
  })
})

app.get('/v1/score/history/user/:uid', (req, res) => {
  console.log("user uuid" + req.params.uid)
  res.send({
    "status": "ok",
    "message": "fetch ok - data for: " + req.params.uid,
    "data": []
  })
})

app.post('/v1/score/history/user/:uid', (req, res) => {
  res.send({
    "status": "ok",
    "message": "save ok",
    "data": {}
  })
})

app.listen(port, () => {
  console.log(`App score listening on port ${port}`)
})