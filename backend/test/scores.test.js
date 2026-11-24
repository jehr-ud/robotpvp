// Pruebas del backend con el runner que trae Node (node:test).
// Usan una base de datos en memoria para no tocar scores.db.
const { test, before, after } = require('node:test');
const assert = require('node:assert');

const { createDatabase } = require('../src/database');
const { createScoreRepository } = require('../src/scoreRepository');
const { createApp } = require('../src/app');

let server;
let url;

before(() => {
  const db = createDatabase(':memory:');
  const app = createApp(createScoreRepository(db));
  server = app.listen(0); // 0 = puerto libre
  url = `http://localhost:${server.address().port}`;
});

after(() => {
  server.close();
});

function saveScore(uid, body) {
  return fetch(`${url}/v1/score/history/user/${uid}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
}

test('guarda una partida válida', async () => {
  const res = await saveScore('jugador1', { score: 50, winner: true });
  const body = await res.json();

  assert.strictEqual(res.status, 201);
  assert.strictEqual(body.data.score, 50);
  assert.strictEqual(body.data.winner, true);
});

test('rechaza un score decimal o negativo', async () => {
  const res1 = await saveScore('jugador1', { score: 10.5, winner: true });
  const res2 = await saveScore('jugador1', { score: -1, winner: true });

  assert.strictEqual(res1.status, 400);
  assert.strictEqual(res2.status, 400);
});

test('rechaza una partida sin winner', async () => {
  const res = await saveScore('jugador1', { score: 10 });
  assert.strictEqual(res.status, 400);
});

test('devuelve el historial del jugador', async () => {
  await saveScore('jugador2', { score: 30, winner: false });

  const res = await fetch(`${url}/v1/score/history/user/jugador2`);
  const body = await res.json();

  assert.strictEqual(body.data.length, 1);
  assert.strictEqual(body.data[0].score, 30);
});

test('calcula las estadísticas del jugador', async () => {
  await saveScore('jugador3', { score: 20, winner: true });
  await saveScore('jugador3', { score: 70, winner: false });

  const res = await fetch(`${url}/v1/score/stats/user/jugador3`);
  const body = await res.json();

  assert.deepStrictEqual(body.data, {
    uid: 'jugador3',
    total_games: 2,
    wins: 1,
    losses: 1,
    best_score: 70
  });
});
