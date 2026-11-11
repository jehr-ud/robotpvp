const express = require('express');
const Database = require('better-sqlite3');

const app = express();
const port = 3000;

// Middleware para parsear JSON en el body
app.use(express.json());

// Inicializa la base de datos (crea el archivo si no existe)
const db = new Database('scores.db');

// Crea la tabla si no existe
db.exec(`
  CREATE TABLE IF NOT EXISTS scores (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    uid       TEXT    NOT NULL,
    score     INTEGER NOT NULL,
    winner    INTEGER NOT NULL DEFAULT 0,
    played_at TEXT    NOT NULL
  )
`);

// Sentencias preparadas
const insertScore = db.prepare(
  'INSERT INTO scores (uid, score, winner, played_at) VALUES (?, ?, ?, ?)'
);

const selectByUser = db.prepare(
  'SELECT id, uid, score, winner, played_at FROM scores WHERE uid = ? ORDER BY played_at DESC'
);

// GET / - health check
app.get('/', (req, res) => {
  res.send({
    status: 'ok',
    message: 'running'
  });
});

// GET /v1/score/history/user/:uid - historial de un usuario
app.get('/v1/score/history/user/:uid', (req, res) => {
  const { uid } = req.params;
  console.log('Fetching scores for uid:', uid);

  const rows = selectByUser.all(uid);

  // Convierte el campo winner de 0/1 a booleano para la respuesta
  const data = rows.map(row => ({
    id: row.id,
    uid: row.uid,
    score: row.score,
    winner: row.winner === 1,
    played_at: row.played_at
  }));

  res.send({
    status: 'ok',
    message: `fetch ok - data for: ${uid}`,
    data
  });
});

// POST /v1/score/history/user/:uid - guarda una partida
// Body esperado: { score: number, winner: boolean }
app.post('/v1/score/history/user/:uid', (req, res) => {
  const { uid } = req.params;
  const { score, winner } = req.body;

  // Validación básica
  if (score === undefined || typeof score !== 'number') {
    return res.status(400).send({
      status: 'error',
      message: '"score" es requerido y debe ser un número'
    });
  }

  if (winner === undefined || typeof winner !== 'boolean') {
    return res.status(400).send({
      status: 'error',
      message: '"winner" es requerido y debe ser un booleano'
    });
  }

  const played_at = new Date().toISOString();

  const result = insertScore.run(uid, score, winner ? 1 : 0, played_at);

  res.status(201).send({
    status: 'ok',
    message: 'save ok',
    data: {
      id: result.lastInsertRowid,
      uid,
      score,
      winner,
      played_at
    }
  });
});

app.listen(port, () => {
  console.log(`App score listening on port ${port}`);
});
