const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
    ...options,
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => ({ message: "Error inesperado" }));
    throw new Error(detail.message ?? "No se pudo completar la operacion");
  }

  return response.json();
}

export function fetchDashboard() {
  return request("/dashboard/resumen");
}

export function fetchAlertas() {
  return request("/alertas");
}

export function fetchCasos(filters = {}) {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.set(key, value);
    }
  });
  const query = params.toString();
  return request(`/casos${query ? `?${query}` : ""}`);
}

export function fetchCaso(casoId) {
  return request(`/casos/${casoId}`);
}

export function emitirAlertas(casoId, payload) {
  return request(`/casos/${casoId}/alertas`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function registrarReporte(casoId, payload) {
  return request(`/casos/${casoId}/reportes`, {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function crearCaso(payload) {
  return request("/casos", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function actualizarEstado(casoId, estado, resultado = null) {
  return request(`/casos/${casoId}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ estado, resultado, operador: "OP_FINDRA" }),
  });
}
