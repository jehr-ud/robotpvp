// Configuración del servicio. Se puede cambiar con variables de entorno.
module.exports = {
  port: process.env.PORT || 3000,
  dbPath: process.env.DB_PATH || 'scores.db'
};
