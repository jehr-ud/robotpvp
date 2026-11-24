const config = require('./src/config');
const { createDatabase } = require('./src/database');
const { createScoreRepository } = require('./src/scoreRepository');
const { createApp } = require('./src/app');

// Se conectan los componentes: base de datos -> repositorio -> app
const db = createDatabase(config.dbPath);
const scoreRepository = createScoreRepository(db);
const app = createApp(scoreRepository);

app.listen(config.port, () => {
  console.log(`App score listening on port ${config.port}`);
});
