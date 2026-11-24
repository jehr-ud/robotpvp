web service to save scores for players

### requeriments

- NodeJs 26v

### run the project
In terminal, execute:

```
npm install
```

then

```
npm start
```

### run the tests

```
npm test
```

### endpoints

- `GET /` health check
- `POST /v1/score/history/user/:uid` guarda una partida. Body: `{ "score": 50, "winner": true }`
- `GET /v1/score/history/user/:uid` historial del jugador
- `GET /v1/score/stats/user/:uid` partidas, victorias, derrotas y mejor puntaje

### using HTTP Client

-   Test the web services