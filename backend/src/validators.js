// Componente de validación: devuelve un mensaje de error o null si todo está bien

// Solo letras, números, guion y guion bajo (como los uid de Firebase)
const UID_REGEX = /^[A-Za-z0-9_-]+$/;

function validateUid(uid) {
  if (!UID_REGEX.test(uid)) {
    return '"uid" solo puede tener letras, números, "-" o "_"';
  }
  return null;
}

function validateScore(body) {
  if (!body) {
    return 'el body debe ser un JSON';
  }

  const { score, winner } = body;

  if (!Number.isInteger(score) || score < 0) {
    return '"score" es requerido y debe ser un número entero mayor o igual a 0';
  }

  if (typeof winner !== 'boolean') {
    return '"winner" es requerido y debe ser un booleano';
  }

  return null;
}

module.exports = { validateUid, validateScore };
