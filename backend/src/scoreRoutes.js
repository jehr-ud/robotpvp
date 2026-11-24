const express = require('express');

// Componente de rutas: recibe el repositorio, no sabe nada de SQL
function createScoreRoutes(scoreRepository) {
  const router = express.Router();

  // GET /v1/score/history/user/:uid - historial de un usuario
  router.get('/history/user/:uid', (req, res) => {
    const { uid } = req.params;
    const data = scoreRepository.findByUser(uid);

    res.send({
      status: 'ok',
      message: `fetch ok - data for: ${uid}`,
      data
    });
  });

  // POST /v1/score/history/user/:uid - guarda una partida
  // Body esperado: { score: number, winner: boolean }
  router.post('/history/user/:uid', (req, res) => {
    const { uid } = req.params;
    const { score, winner } = req.body;

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

    const data = scoreRepository.save(uid, score, winner);

    res.status(201).send({
      status: 'ok',
      message: 'save ok',
      data
    });
  });

  return router;
}

module.exports = { createScoreRoutes };
