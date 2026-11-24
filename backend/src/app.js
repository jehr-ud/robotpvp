const express = require('express');
const { createScoreRoutes } = require('./scoreRoutes');

// Arma la aplicación con el repositorio que se le pase
function createApp(scoreRepository) {
  const app = express();

  // Middleware para parsear JSON en el body
  app.use(express.json());

  // GET / - health check
  app.get('/', (req, res) => {
    res.send({
      status: 'ok',
      message: 'running'
    });
  });

  app.use('/v1/score', createScoreRoutes(scoreRepository));

  return app;
}

module.exports = { createApp };
