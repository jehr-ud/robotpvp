const Database = require('better-sqlite3');

// Componente de base de datos: abre el archivo y crea la tabla si no existe
function createDatabase(dbPath) {
  const db = new Database(dbPath);

  db.exec(`
    CREATE TABLE IF NOT EXISTS scores (
      id        INTEGER PRIMARY KEY AUTOINCREMENT,
      uid       TEXT    NOT NULL,
      score     INTEGER NOT NULL,
      winner    INTEGER NOT NULL DEFAULT 0,
      played_at TEXT    NOT NULL
    )
  `);

  return db;
}

module.exports = { createDatabase };
