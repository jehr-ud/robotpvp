// Componente repositorio: es el único que habla con la tabla scores
function createScoreRepository(db) {
  const insertScore = db.prepare(
    'INSERT INTO scores (uid, score, winner, played_at) VALUES (?, ?, ?, ?)'
  );

  const selectByUser = db.prepare(
    'SELECT id, uid, score, winner, played_at FROM scores WHERE uid = ? ORDER BY played_at DESC'
  );

  return {
    save(uid, score, winner) {
      const played_at = new Date().toISOString();
      const result = insertScore.run(uid, score, winner ? 1 : 0, played_at);
      return { id: result.lastInsertRowid, uid, score, winner, played_at };
    },

    findByUser(uid) {
      // Convierte el campo winner de 0/1 a booleano
      return selectByUser.all(uid).map(row => ({
        id: row.id,
        uid: row.uid,
        score: row.score,
        winner: row.winner === 1,
        played_at: row.played_at
      }));
    }
  };
}

module.exports = { createScoreRepository };
