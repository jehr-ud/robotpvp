const express = require('express');
const { validateUid, validateScore } = require('./validators');

// Componente de rutas: recibe el repositorio, no sabe nada de SQL
function createScoreRoutes(scoreRepository) {
  const router = express.Router();

  function badRequest(res, message) {
    return res.status(400).send({ status: 'error', message });
  }

  // GET /v1/score/history/user/:uid - historial de un usuario
  router.get('/history/user/:uid', (req, res) => {
    const { uid } = req.params;

    const uidError = validateUid(uid);
    if (uidError) return badRequest(res, uidError);

    const data = scoreRepository.findByUser(uid);

    res.send({
      status: 'ok',
      message: `fetch ok - data for: ${uid}`,
      data
    });
  });

  // POST /v1/score/history/user/:uid - guarda una partida
  // Body esperado: { score: entero >= 0, winner: boolean }
  router.post('/history/user/:uid', (req, res) => {
    const { uid } = req.params;

    const error = validateUid(uid) || validateScore(req.body);
    if (error) return badRequest(res, error);

    const { score, winner } = req.body;
    const data = scoreRepository.save(uid, score, winner);

    res.status(201).send({
      status: 'ok',
      message: 'save ok',
      data
    });
  });

  // GET /v1/score/stats/user/:uid - victorias, derrotas y mejor puntaje
  router.get('/stats/user/:uid', (req, res) => {
    const { uid } = req.params;

    const uidError = validateUid(uid);
    if (uidError) return badRequest(res, uidError);

    res.send({
      status: 'ok',
      message: `stats ok - data for: ${uid}`,
      data: scoreRepository.getStats(uid)
    });
  });

  return router;
}

module.exports = { createScoreRoutes };
