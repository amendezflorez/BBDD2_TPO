const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";

async function request(path, options = {}) {
  const isFormData = options.body instanceof FormData;
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      ...(isFormData ? {} : { "Content-Type": "application/json" }),
      ...(options.headers ?? {}),
    },
    ...options,
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => ({ message: "Error inesperado" }));
    throw new Error(detail.message ?? "No se pudo completar la operación");
  }

  return response.json();
}

export function fetchDashboard() {
  return request("/dashboard/resumen");
}

export function fetchAlertas() {
  return request("/alertas");
}

export function fetchReportes() {
  return request("/reportes");
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

export function fetchUsuarios() {
  return request("/usuarios");
}

export function crearUsuario(payload) {
  return request("/usuarios", {
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

export async function procesarAudio(file) {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_BASE_URL}/audio/procesar`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => ({ message: "Error inesperado" }));
    throw new Error(detail.message ?? "No se pudo procesar el archivo de audio");
  }

  return response.json();
}


export function subirDocumento(casoId, file, tipo) {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("tipo", tipo);
  formData.append("operador", "OP_FINDRA");
  return request(`/casos/${casoId}/documentos`, {
    method: "POST",
    body: formData,
  });
}

export function urlDocumento(casoId, gridFsId) {
  return `${API_BASE_URL}/casos/${casoId}/documentos/${gridFsId}`;
}
