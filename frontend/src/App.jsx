import React, { useEffect, useMemo, useState } from "react";
import {
  AlertTriangle,
  Bell,
  CircleDot,
  Clock3,
  FileText,
  Filter,
  Home,
  MapPinned,
  Search,
  Shield,
  Users,
  X,
} from "lucide-react";
import {
  actualizarEstado,
  crearCaso,
  emitirAlertas,
  fetchAlertas,
  fetchCaso,
  fetchCasos,
  fetchDashboard,
  fetchReportes,
  registrarReporte,
} from "./api.js";

const emptyFilters = {
  texto: "",
  estado: "",
  zona: "",
  edadMin: "0",
  edadMax: "18",
};

const navItems = [
  ["Inicio", Home],
  ["Casos", CircleDot],
  ["Alertas", AlertTriangle],
  ["Mapa", MapPinned],
  ["Reportes", FileText],
  ["Usuarios", Users],
];

export function App() {
  const [view, setView] = useState("dashboard");
  const [casos, setCasos] = useState([]);
  const [dashboard, setDashboard] = useState(null);
  const [selectedCasoId, setSelectedCasoId] = useState(null);
  const [selectedCaso, setSelectedCaso] = useState(null);
  const [filters, setFilters] = useState(emptyFilters);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [alertasData, setAlertasData] = useState([]);
  const [selectedAlertaCasoId, setSelectedAlertaCasoId] = useState(null);
  const [alertasLoading, setAlertasLoading] = useState(false);
  const [reportesData, setReportesData] = useState([]);
  const [selectedReporteCasoId, setSelectedReporteCasoId] = useState(null);
  const [reportesLoading, setReportesLoading] = useState(false);

  useEffect(() => {
    refreshDashboard();
    refreshCasos();
    // Initial load only; later refreshes are triggered explicitly by user actions.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!selectedCasoId) {
      return;
    }

    if (selectedCaso?.casoId === selectedCasoId) {
      return;
    }

    let cancelled = false;
    setSelectedCaso(null);
    fetchCaso(selectedCasoId)
      .then((data) => { if (!cancelled) setSelectedCaso(data); })
      .catch((exception) => { if (!cancelled) setError(exception.message); });

    return () => { cancelled = true; };
  }, [selectedCasoId]); // eslint-disable-line react-hooks/exhaustive-deps

  async function loadReportes() {
    setReportesLoading(true);
    try {
      const data = await fetchReportes();
      setReportesData(data);
      if (data.length > 0) setSelectedReporteCasoId(data[0].casoId);
    } catch (exception) {
      setError(exception.message);
    } finally {
      setReportesLoading(false);
    }
  }

  async function loadAlertas() {
    setAlertasLoading(true);
    try {
      const data = await fetchAlertas();
      setAlertasData(data);
      if (data.length > 0) setSelectedAlertaCasoId(data[0].casoId);
    } catch (exception) {
      setError(exception.message);
    } finally {
      setAlertasLoading(false);
    }
  }

  async function refreshDashboard() {
    try {
      const data = await fetchDashboard();
      setDashboard(data);
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function refreshCasos(nextFilters = filters) {
    setLoading(true);
    setError("");
    try {
      const data = await fetchCasos({ ...nextFilters, size: 20 });
      setCasos(data);
      if (!selectedCasoId && data.length > 0) {
        setSelectedCasoId(data[0].casoId);
      }
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  function openDetail(casoId) {
    setSelectedCasoId(casoId);
    setView("detail");
  }

  async function handleAlertSubmit(payload) {
    if (!selectedCaso) {
      return;
    }

    const updated = await emitirAlertas(selectedCaso.casoId, payload);
    setSelectedCaso(updated);
    await Promise.all([refreshDashboard(), refreshCasos()]);
    setModalOpen(false);
  }

  async function handleCrearCaso(payload) {
    try {
      const created = await crearCaso(payload);
      setSelectedCaso(created);
      setSelectedCasoId(created.casoId);
      setView("detail");
      await Promise.all([refreshDashboard(), refreshCasos()]);
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleCambiarEstado(estado) {
    if (!selectedCaso) return;
    try {
      const updated = await actualizarEstado(selectedCaso.casoId, estado);
      setSelectedCaso(updated);
      await Promise.all([refreshDashboard(), refreshCasos()]);
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function handleQuickReport() {
    if (!selectedCaso) {
      return;
    }

    const coordinates = selectedCaso.menor.ultimaUbicacion.coordinates;
    const updated = await registrarReporte(selectedCaso.casoId, {
      longitude: coordinates[0] + 0.004,
      latitude: coordinates[1] - 0.003,
      descripcion: "Reporte ciudadano cargado desde panel FINDRA",
      contacto: "linea 134",
      operador: "OP_FINDRA",
    });
    setSelectedCaso(updated);
  }

  const activeCases = useMemo(
    () => casos.filter((caso) => caso.estado === "ACTIVO"),
    [casos],
  );

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <Shield size={30} aria-hidden="true" />
          <div>
            <strong>FINDRA</strong>
            <span>Alerta Sofia · SIFEBU</span>
          </div>
        </div>

        <nav className="nav">
          {navItems.map(([label, Icon]) => (
            <button
              className={navIsActive(label, view) ? "active" : ""}
              key={label}
              type="button"
              onClick={() => {
                if (label === "Inicio") { setView("dashboard"); }
                else if (label === "Alertas") { setView("alertas"); loadAlertas(); }
                else if (label === "Casos") { setView("search"); }
                else if (label === "Reportes") { setView("reportes"); loadReportes(); }
                else { setView("search"); }
              }}
              title={label}
            >
              <Icon size={18} aria-hidden="true" />
              <span>{label}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <span>v2.4.1</span>
          <strong>Turno: Noche</strong>
        </div>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <label className="global-search">
            <Search size={18} aria-hidden="true" />
            <input
              placeholder="Buscar caso o menor..."
              value={filters.texto}
              onChange={(event) => setFilters({ ...filters, texto: event.target.value })}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  refreshCasos();
                  setView("search");
                }
              }}
            />
          </label>
          <div className="status-pill">
            <span className="pulse" />
            {dashboard?.alertasActivas ?? 0} alertas activas
          </div>
        </header>

        {error && (
          <div className="alert-banner">
            <AlertTriangle size={18} aria-hidden="true" />
            {error}
          </div>
        )}

        {view === "dashboard" && (
          <Dashboard
            activeCases={activeCases}
            cases={casos}
            dashboard={dashboard}
            loading={loading}
            onCreateCase={() => setView("new-case")}
            onOpenCase={openDetail}
          />
        )}

        {view === "new-case" && (
          <NewCaseView
            onBack={() => setView("dashboard")}
            onSubmit={handleCrearCaso}
          />
        )}

        {view === "search" && (
          <SearchView
            cases={casos}
            filters={filters}
            loading={loading}
            onApply={() => refreshCasos()}
            onChangeFilters={setFilters}
            onClear={() => {
              setFilters(emptyFilters);
              refreshCasos(emptyFilters);
            }}
            onOpenCase={openDetail}
          />
        )}

        {view === "detail" && (
          selectedCaso
            ? <CaseDetail
                caso={selectedCaso}
                onBack={() => setView("search")}
                onCambiarEstado={handleCambiarEstado}
                onEmitAlert={() => setModalOpen(true)}
                onQuickReport={handleQuickReport}
              />
            : <div className="loading-detail">Cargando caso...</div>
        )}

        {view === "alertas" && (
          <AlertasView
            alertas={alertasData}
            loading={alertasLoading}
            selectedCasoId={selectedAlertaCasoId}
            onSelect={setSelectedAlertaCasoId}
            onOpenCase={openDetail}
          />
        )}

        {view === "reportes" && (
          <ReportesView
            reportes={reportesData}
            loading={reportesLoading}
            selectedCasoId={selectedReporteCasoId}
            onSelect={setSelectedReporteCasoId}
            onOpenCase={openDetail}
          />
        )}
      </main>

      {modalOpen && selectedCaso && (
        <AlertModal
          caso={selectedCaso}
          onClose={() => setModalOpen(false)}
          onSubmit={handleAlertSubmit}
        />
      )}
    </div>
  );
}

function Dashboard({ activeCases, cases, dashboard, loading, onCreateCase, onOpenCase }) {
  return (
    <section className="view-grid">
      <div className="section-heading">
        <div>
          <span>Panel Principal</span>
          <h1>Casos activos</h1>
        </div>
        <button className="primary-button" type="button" onClick={onCreateCase}>
          <FileText size={17} aria-hidden="true" />
          Nuevo caso
        </button>
      </div>

      <div className="metrics">
        <Metric label="Casos activos" value={dashboard?.casosActivos ?? "-"} note="2 desde ayer" />
        <Metric
          label="Alertas emitidas hoy"
          value={dashboard?.alertasEmitidasHoy ?? "-"}
          note="Ultima: hace 34 min"
        />
        <Metric label="Casos resueltos" value={dashboard?.casosResueltosMes ?? "-"} note="Este mes" />
        <Metric
          label="Tiempo prom. activacion"
          value={`${dashboard?.tiempoPromedioActivacionMinutos ?? 18} min`}
          note="4 min menos vs. semana ant."
        />
      </div>

      <div className="dashboard-layout">
        <div className="panel">
          <div className="panel-title">
            <h2>Casos activos</h2>
            {loading && <span>Cargando...</span>}
          </div>
          <CaseTable cases={activeCases} onOpenCase={onOpenCase} />
        </div>
        <GeoPanel cases={cases} />
      </div>
    </section>
  );
}

function SearchView({
  cases,
  filters,
  loading,
  onApply,
  onChangeFilters,
  onClear,
  onOpenCase,
}) {
  return (
    <section className="search-layout">
      <aside className="filters">
        <div className="panel-title">
          <h2>Filtros avanzados</h2>
          <Filter size={18} aria-hidden="true" />
        </div>

        <label>
          Estado del caso
          <select
            value={filters.estado}
            onChange={(event) => onChangeFilters({ ...filters, estado: event.target.value })}
          >
            <option value="">Todos</option>
            <option value="ACTIVO">Activo</option>
            <option value="RESUELTO">Resuelto</option>
            <option value="ARCHIVADO">Archivado</option>
          </select>
        </label>

        <label>
          Zona geografica
          <input
            value={filters.zona}
            onChange={(event) => onChangeFilters({ ...filters, zona: event.target.value })}
            placeholder="Provincia o localidad"
          />
        </label>

        <div className="filter-row">
          <label>
            Edad min.
            <input
              min="0"
              max="18"
              type="number"
              value={filters.edadMin}
              onChange={(event) => onChangeFilters({ ...filters, edadMin: event.target.value })}
            />
          </label>
          <label>
            Edad max.
            <input
              min="0"
              max="18"
              type="number"
              value={filters.edadMax}
              onChange={(event) => onChangeFilters({ ...filters, edadMax: event.target.value })}
            />
          </label>
        </div>

        <button className="primary-button full" type="button" onClick={onApply}>
          Buscar
        </button>
        <button className="ghost-button full" type="button" onClick={onClear}>
          Limpiar filtros
        </button>
      </aside>

      <div className="panel results-panel">
        <div className="panel-title">
          <div>
            <h1>Buscador de casos</h1>
            <span>{cases.length} resultados · Ordenar por tiempo transcurrido</span>
          </div>
          {loading && <span>Cargando...</span>}
        </div>
        <div className="case-list">
          {cases.map((caso) => (
            <button className="case-row" key={caso.casoId} type="button" onClick={() => onOpenCase(caso.casoId)}>
              <Avatar name={caso.menor.nombre} />
              <div>
                <strong>{caso.menor.nombre}</strong>
                <span>
                  {caso.casoId} · {caso.menor.edad} anos · {caso.zona}
                </span>
              </div>
              <ElapsedClock date={caso.fechaActivacion} />
              {list(caso.alertasEmitidas).length > 0 && <span className="alert-chip">Alerta</span>}
              <span className="link-label">Ver caso</span>
            </button>
          ))}
        </div>
      </div>
    </section>
  );
}

function CaseDetail({ caso, onBack, onCambiarEstado, onEmitAlert, onQuickReport }) {
  return (
    <section className="detail-view">
      <div className="detail-header">
        <button className="ghost-button" type="button" onClick={onBack}>
          Volver
        </button>
        <div>
          <span>{caso.casoId} · {caso.menor.nombre}</span>
          <h1>{caso.estado}</h1>
        </div>
        <ElapsedClock date={caso.fechaActivacion} large />
        {caso.estado === "ACTIVO" && (
          <button className="ghost-button" type="button" onClick={() => onCambiarEstado("RESUELTO")}>
            Cerrar caso
          </button>
        )}
        {caso.estado === "ACTIVO" && (
          <button className="ghost-button" type="button" onClick={() => onCambiarEstado("ARCHIVADO")}>
            Archivar
          </button>
        )}
      </div>

      <div className="detail-grid">
        <div className="identity-panel">
          <Avatar name={caso.menor.nombre} large />
          <h2>{caso.menor.nombre}</h2>
          <p>{caso.menor.edad} anos · {caso.menor.sexo === "F" ? "Femenino" : "Masculino"}</p>
          <dl>
            <dt>Cabello</dt>
            <dd>{caso.menor.cabello}</dd>
            <dt>Ojos</dt>
            <dd>{caso.menor.ojos}</dd>
            <dt>Estatura</dt>
            <dd>{caso.menor.estatura}</dd>
            <dt>Ropa</dt>
            <dd>{caso.menor.ropa}</dd>
            <dt>Senas</dt>
            <dd>{caso.menor.senas}</dd>
          </dl>
        </div>

        <div className="panel legal-panel">
          <h2>Datos legales</h2>
          <p>Expediente: {caso.autoridadJudicial.nroExpediente}</p>
          <p>Fiscal: {caso.autoridadJudicial.fiscal}</p>
          <p>Juez: {caso.autoridadJudicial.juez}</p>
          <p>Denunciante: {caso.denunciante.nombre} ({caso.denunciante.vinculo})</p>
        </div>

        <GeoPanel cases={[caso]} detail />

        <div className="panel alert-manager">
          <div className="panel-title">
            <h2>Gestor de alertas</h2>
            <button className="primary-button" type="button" onClick={onEmitAlert} disabled={caso.estado !== "ACTIVO"}>
              <Bell size={17} aria-hidden="true" />
              Emitir Alerta Sofia
            </button>
          </div>
          <div className="mini-table">
            {list(caso.alertasEmitidas).map((alerta, index) => (
              <div key={`${alerta.canal}-${index}`}>
                <span>{alerta.canal}</span>
                <span>{alerta.operador}</span>
                <span>{formatDate(alerta.timestamp)}</span>
                <strong>{alerta.estado}</strong>
              </div>
            ))}
            {list(caso.alertasEmitidas).length === 0 && <p>No hay alertas emitidas.</p>}
          </div>
        </div>

        <div className="panel attachments">
          <h2>Adjuntos / Evidencia</h2>
          {list(caso.documentosAdjuntos).map((doc) => (
            <span key={doc.url}>{doc.tipo} {doc.url}</span>
          ))}
        </div>

        <div className="panel timeline">
          <div className="panel-title">
            <h2>Linea de tiempo</h2>
            <button className="ghost-button" type="button" onClick={onQuickReport}>
              Reporte rapido
            </button>
          </div>
          {list(caso.historialAcciones)
            .slice()
            .reverse()
            .map((accion, index) => (
              <div className="timeline-item" key={`${accion.accion}-${index}`}>
                <time>{formatTime(accion.timestamp)}</time>
                <div>
                  <strong>{accion.usuario}</strong>
                  <span>{accion.detalle}</span>
                </div>
              </div>
            ))}
        </div>
      </div>
    </section>
  );
}

function NewCaseView({ onBack, onSubmit }) {
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    menorNombre: "",
    menorEdad: "",
    menorSexo: "F",
    menorCabello: "",
    menorOjos: "",
    menorEstatura: "",
    menorRopa: "",
    menorSenas: "",
    menorLat: "-34.6037",
    menorLng: "-58.3816",
    denuncianteNombre: "",
    denuncianteVinculo: "",
    denuncianteTel: "",
    juez: "",
    fiscal: "",
    nroExpediente: "",
    zona: "",
  });

  function set(key, value) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    const payload = {
      menor: {
        nombre: form.menorNombre,
        edad: Number(form.menorEdad),
        sexo: form.menorSexo,
        cabello: form.menorCabello,
        ojos: form.menorOjos,
        estatura: form.menorEstatura,
        ropa: form.menorRopa,
        senas: form.menorSenas,
        ultimaUbicacion: {
          type: "Point",
          coordinates: [Number(form.menorLng), Number(form.menorLat)],
          descripcion: form.zona,
        },
      },
      denunciante: {
        nombre: form.denuncianteNombre,
        vinculo: form.denuncianteVinculo,
        tel: form.denuncianteTel,
      },
      autoridadJudicial: {
        juez: form.juez,
        fiscal: form.fiscal,
        nroExpediente: form.nroExpediente,
      },
      zona: form.zona,
    };
    setSubmitting(true);
    try {
      await onSubmit(payload);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="detail-view">
      <div className="detail-header">
        <button className="ghost-button" type="button" onClick={onBack}>
          Volver
        </button>
        <div>
          <span>Nuevo caso</span>
          <h1>Registrar caso Alerta Sofía</h1>
        </div>
      </div>

      <form className="new-case-form" onSubmit={handleSubmit}>
        <div className="panel">
          <h2>Datos del menor</h2>
          <div className="form-grid">
            <label>Nombre completo *<input required value={form.menorNombre} onChange={(e) => set("menorNombre", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} /></label>
            <label>Edad *<input required type="number" min="0" max="18" value={form.menorEdad} onChange={(e) => set("menorEdad", e.target.value)} onKeyDown={(e) => ["e", "E", "+", "-", "."].includes(e.key) && e.preventDefault()} /></label>
            <label>Sexo
              <select value={form.menorSexo} onChange={(e) => set("menorSexo", e.target.value)}>
                <option value="F">Femenino</option>
                <option value="M">Masculino</option>
              </select>
            </label>
            <label>Cabello<input value={form.menorCabello} onChange={(e) => set("menorCabello", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} placeholder="Ej: castaño oscuro" /></label>
            <label>Ojos<input value={form.menorOjos} onChange={(e) => set("menorOjos", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} placeholder="Ej: marrones" /></label>
            <label>Estatura (m)<input type="number" step="0.01" min="0.30" max="2.10" placeholder="Ej: 1.52" value={form.menorEstatura} onChange={(e) => set("menorEstatura", e.target.value)} /></label>
            <label>Ropa<input value={form.menorRopa} onChange={(e) => set("menorRopa", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ ,.-]/g, ""))} placeholder="Ej: camisa azul, jeans" /></label>
            <label>Señas particulares<input value={form.menorSenas} onChange={(e) => set("menorSenas", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ ,.-]/g, ""))} placeholder="Ej: lunar en mejilla izquierda" /></label>
            <label>Zona / última ubicación *<input required value={form.zona} onChange={(e) => set("zona", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ ,.-]/g, ""))} placeholder="Ej: CABA" /></label>
            <label>Latitud<input type="number" step="any" value={form.menorLat} onChange={(e) => set("menorLat", e.target.value)} /></label>
            <label>Longitud<input type="number" step="any" value={form.menorLng} onChange={(e) => set("menorLng", e.target.value)} /></label>
          </div>
        </div>

        <div className="panel">
          <h2>Datos del denunciante</h2>
          <div className="form-grid">
            <label>Nombre *<input required value={form.denuncianteNombre} onChange={(e) => set("denuncianteNombre", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} /></label>
            <label>Vínculo<input value={form.denuncianteVinculo} onChange={(e) => set("denuncianteVinculo", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} placeholder="Ej: madre" /></label>
            <label>Teléfono<input value={form.denuncianteTel} onChange={(e) => set("denuncianteTel", e.target.value.replace(/[^0-9 +()-]/g, ""))} placeholder="Ej: +54 11 1234-5678" /></label>
          </div>
        </div>

        <div className="panel">
          <h2>Autoridad judicial</h2>
          <div className="form-grid">
            <label>N° expediente<input value={form.nroExpediente} onChange={(e) => set("nroExpediente", e.target.value.replace(/[^A-Za-z0-9/.-]/g, ""))} placeholder="Ej: 2024-12345" /></label>
            <label>Juez<input value={form.juez} onChange={(e) => set("juez", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} /></label>
            <label>Fiscal<input value={form.fiscal} onChange={(e) => set("fiscal", e.target.value.replace(/[^A-Za-záéíóúÁÉÍÓÚñÑüÜ .-]/g, ""))} /></label>
          </div>
        </div>

        <div className="modal-actions">
          <button className="ghost-button" type="button" onClick={onBack}>Cancelar</button>
          <button className="primary-button" type="submit" disabled={submitting}>
            <FileText size={17} aria-hidden="true" />
            {submitting ? "Registrando..." : "Registrar caso"}
          </button>
        </div>
      </form>
    </section>
  );
}

function AlertModal({ caso, onClose, onSubmit }) {
  const [channels, setChannels] = useState(["SMS masivo", "Redes sociales", "Aplicacion ciudadana FINDRA"]);
  const [observaciones, setObservaciones] = useState("");
  const [requiresAuthorization, setRequiresAuthorization] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  function toggleChannel(channel) {
    setChannels((current) =>
      current.includes(channel)
        ? current.filter((item) => item !== channel)
        : [...current, channel],
    );
  }

  async function submit() {
    setSubmitting(true);
    await onSubmit({
      canales: channels,
      observaciones,
      requiereAutorizacion: requiresAuthorization,
      operador: "OP_FINDRA",
    });
    setSubmitting(false);
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <button className="icon-button close" type="button" onClick={onClose} title="Cerrar">
          <X size={20} aria-hidden="true" />
        </button>
        <h2>Confirmar Alerta Sofia</h2>
        <p>{caso.casoId} · {caso.menor.nombre}</p>

        <fieldset>
          <legend>Canales a activar</legend>
          {["SMS masivo", "Redes sociales", "Cadena nacional TV + Radio", "Aplicacion ciudadana FINDRA"].map(
            (channel) => (
              <label className="check-line" key={channel}>
                <input
                  checked={channels.includes(channel)}
                  type="checkbox"
                  onChange={() => toggleChannel(channel)}
                />
                {channel}
              </label>
            ),
          )}
        </fieldset>

        <label>
          Observaciones
          <textarea
            placeholder="Motivo / notas adicionales..."
            value={observaciones}
            onChange={(event) => setObservaciones(event.target.value)}
          />
        </label>

        <label className="check-line warning">
          <input
            checked={requiresAuthorization}
            type="checkbox"
            onChange={(event) => setRequiresAuthorization(event.target.checked)}
          />
          Requiere autorizacion del fiscal a cargo.
        </label>

        <div className="modal-actions">
          <button className="ghost-button" type="button" onClick={onClose}>
            Cancelar
          </button>
          <button className="primary-button" type="button" disabled={submitting || channels.length === 0} onClick={submit}>
            <Bell size={17} aria-hidden="true" />
            Confirmar y emitir
          </button>
        </div>
      </div>
    </div>
  );
}

function CaseTable({ cases, onOpenCase }) {
  return (
    <div className="case-table">
      <div className="case-table-head">
        <span>Menor</span>
        <span>Edad</span>
        <span>Zona</span>
        <span>Tiempo</span>
      </div>
      {cases.map((caso) => (
        <button key={caso.casoId} type="button" onClick={() => onOpenCase(caso.casoId)}>
          <span className="table-person">
            <Avatar name={caso.menor.nombre} />
            <span>
              <strong>{caso.menor.nombre}</strong>
              <small>{caso.casoId}</small>
            </span>
          </span>
          <span>{caso.menor.edad} anos</span>
          <span>{caso.zona}</span>
          <ElapsedClock date={caso.fechaActivacion} />
        </button>
      ))}
    </div>
  );
}

// Bounding box Argentina: lng [-73, -53], lat [-55, -22]
const ARG_LNG_MIN = -73, ARG_LNG_MAX = -53;
const ARG_LAT_MIN = -55, ARG_LAT_MAX = -22;

function geoToPercent(coordinates) {
  if (!Array.isArray(coordinates) || coordinates.length < 2) return null;
  const [lng, lat] = coordinates;
  const left = ((lng - ARG_LNG_MIN) / (ARG_LNG_MAX - ARG_LNG_MIN)) * 100;
  // lat is inverted: higher lat = lower top value
  const top = ((ARG_LAT_MAX - lat) / (ARG_LAT_MAX - ARG_LAT_MIN)) * 100;
  return { left: Math.min(Math.max(left, 2), 96), top: Math.min(Math.max(top, 2), 96) };
}

function GeoPanel({ cases, detail = false }) {
  return (
    <div className={`panel map-panel ${detail ? "map-detail" : ""}`}>
      <div className="panel-title">
        <h2>{detail ? "Ultima ubicacion conocida" : "Mapa de casos"}</h2>
        <span>Vista nacional</span>
      </div>
      <div className="map-canvas">
        {cases.slice(0, 7).map((caso) => {
          const coords = caso.menor?.ultimaUbicacion?.coordinates;
          const pos = geoToPercent(coords);
          if (!pos) return null;
          return (
            <button
              className={list(caso.alertasEmitidas).length > 0 ? "map-pin alert" : "map-pin"}
              key={caso.casoId}
              style={{ left: `${pos.left}%`, top: `${pos.top}%` }}
              title={`${caso.casoId} · ${caso.zona}`}
              type="button"
            >
              <MapPinned size={16} aria-hidden="true" />
            </button>
          );
        })}
      </div>
      <div className="legend">
        <span><i className="dot alert-dot" />Con alerta emitida</span>
        <span><i className="dot" />Sin alerta</span>
      </div>
    </div>
  );
}

function Metric({ label, value, note }) {
  return (
    <div className="metric">
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{note}</small>
    </div>
  );
}

function Avatar({ name, large = false }) {
  const initials = name
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return <span className={large ? "avatar large" : "avatar"}>{initials}</span>;
}

function ElapsedClock({ date, large = false }) {
  const elapsed = useMemo(() => {
    const minutes = Math.max(0, Math.floor((Date.now() - new Date(date).getTime()) / 60000));
    const hours = Math.floor(minutes / 60);
    const remainder = minutes % 60;
    return `${String(hours).padStart(2, "0")}:${String(remainder).padStart(2, "0")}:00`;
  }, [date]);

  return (
    <span className={large ? "elapsed large" : "elapsed"}>
      <Clock3 size={large ? 20 : 15} aria-hidden="true" />
      {elapsed}
    </span>
  );
}

function navIsActive(label, view) {
  if (label === "Inicio") return view === "dashboard";
  if (label === "Alertas") return view === "alertas";
  if (label === "Reportes") return view === "reportes";
  if (label === "Casos") return view === "search" || view === "detail" || view === "new-case";
  return false;
}

function formatRelative(value) {
  const minutes = Math.max(0, Math.floor((Date.now() - new Date(value).getTime()) / 60000));
  if (minutes < 60) return `hace ${minutes} min`;
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return `hace ${hours}h`;
  return `hace ${Math.floor(hours / 24)} d`;
}

function AlertasView({ alertas, loading, selectedCasoId, onSelect, onOpenCase }) {
  const selected = alertas.find((a) => a.casoId === selectedCasoId);

  return (
    <section className="alertas-layout">
      <div className="panel alertas-lista">
        <div className="panel-title">
          <div>
            <span>Módulo de alertas</span>
            <h2>Casos con alertas</h2>
          </div>
          {loading && <span>Cargando...</span>}
          {!loading && <span>{alertas.length} casos</span>}
        </div>

        <div className="alertas-personas">
          {!loading && alertas.length === 0 && (
            <p style={{ color: "#64717a", padding: "12px 0" }}>No hay alertas registradas.</p>
          )}
          {alertas.map((item) => (
            <button
              key={item.casoId}
              className={`alerta-persona-row${selectedCasoId === item.casoId ? " selected" : ""}`}
              type="button"
              onClick={() => onSelect(item.casoId)}
            >
              <Avatar name={item.menorNombre} />
              <div className="alerta-persona-info">
                <strong>{item.menorNombre}</strong>
                <span>{item.casoId} · {item.zona}</span>
              </div>
              <div className="alerta-meta">
                <span className="alerta-count">{item.totalAlertas}</span>
                <span className="alerta-tiempo">{formatRelative(item.ultimaAlerta)}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      <div className="panel alertas-detalle">
        {selected ? (
          <>
            <div className="alertas-detalle-header">
              <Avatar name={selected.menorNombre} />
              <div className="alertas-detalle-header-info">
                <h2>{selected.menorNombre}</h2>
                <span>{selected.casoId} · {selected.zona} · {selected.menorEdad} años</span>
              </div>
              <div className="alertas-detalle-header-actions">
                <span className={`estado-badge estado-${selected.estadoCaso}`}>
                  {selected.estadoCaso}
                </span>
                <button
                  className="ghost-button"
                  type="button"
                  onClick={() => onOpenCase(selected.casoId)}
                >
                  Ver caso completo →
                </button>
              </div>
            </div>

            <div className="alertas-timeline">
              {selected.alertas.map((alerta, index) => (
                <div key={`${alerta.canal}-${index}`} className="alerta-item">
                  <time>{formatDate(alerta.timestamp)}</time>
                  <div className="alerta-item-info">
                    <strong>{alerta.canal}</strong>
                    <span>{alerta.operador}</span>
                    {alerta.observaciones && (
                      <span className="alerta-obs">{alerta.observaciones}</span>
                    )}
                  </div>
                  <span className={`estado-badge estado-${alerta.estado}`}>
                    {alerta.estado?.replace(/_/g, " ")}
                  </span>
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="alertas-placeholder">
            <AlertTriangle size={40} aria-hidden="true" />
            <p>Seleccioná un caso para ver sus alertas</p>
          </div>
        )}
      </div>
    </section>
  );
}

const ESTADO_REPORTE_LABEL = {
  RECIBIDO: "Recibido",
  VERIFICADO: "Verificado",
  DESCARTADO: "Descartado",
};

function ReportesView({ reportes, loading, selectedCasoId, onSelect, onOpenCase }) {
  const selected = reportes.find((r) => r.casoId === selectedCasoId);

  return (
    <section className="alertas-layout">
      <div className="panel alertas-lista">
        <div className="panel-title">
          <div>
            <span>Módulo de reportes</span>
            <h2>Casos con reportes</h2>
          </div>
          {loading && <span>Cargando...</span>}
          {!loading && <span>{reportes.length} casos</span>}
        </div>

        <div className="alertas-personas">
          {!loading && reportes.length === 0 && (
            <p style={{ color: "#64717a", padding: "12px 0" }}>No hay reportes ciudadanos registrados.</p>
          )}
          {reportes.map((item) => (
            <button
              key={item.casoId}
              className={`alerta-persona-row${selectedCasoId === item.casoId ? " selected" : ""}`}
              type="button"
              onClick={() => onSelect(item.casoId)}
            >
              <Avatar name={item.menorNombre} />
              <div className="alerta-persona-info">
                <strong>{item.menorNombre}</strong>
                <span>{item.casoId} · {item.zona}</span>
              </div>
              <div className="alerta-meta">
                <span className="alerta-count">{item.totalReportes}</span>
                <span className="alerta-tiempo">{formatRelative(item.ultimoReporte)}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      <div className="panel alertas-detalle">
        {selected ? (
          <>
            <div className="alertas-detalle-header">
              <Avatar name={selected.menorNombre} />
              <div className="alertas-detalle-header-info">
                <h2>{selected.menorNombre}</h2>
                <span>{selected.casoId} · {selected.zona} · {selected.menorEdad} años</span>
              </div>
              <div className="alertas-detalle-header-actions">
                <span className={`estado-badge estado-${selected.estadoCaso}`}>
                  {selected.estadoCaso}
                </span>
                <button
                  className="ghost-button"
                  type="button"
                  onClick={() => onOpenCase(selected.casoId)}
                >
                  Ver caso completo →
                </button>
              </div>
            </div>

            <div className="alertas-timeline">
              {selected.reportes.map((reporte, index) => (
                <div key={`${reporte.timestamp}-${index}`} className="alerta-item">
                  <time>{formatDate(reporte.timestamp)}</time>
                  <div className="alerta-item-info">
                    <strong>{reporte.descripcion}</strong>
                    {reporte.contacto && <span>Contacto: {reporte.contacto}</span>}
                    {reporte.ubicacion?.descripcion && (
                      <span className="alerta-obs">{reporte.ubicacion.descripcion}</span>
                    )}
                  </div>
                  <span className={`estado-badge estado-${reporte.estado}`}>
                    {ESTADO_REPORTE_LABEL[reporte.estado] ?? reporte.estado}
                  </span>
                </div>
              ))}
            </div>
          </>
        ) : (
          <div className="alertas-placeholder">
            <FileText size={40} aria-hidden="true" />
            <p>Seleccioná un caso para ver sus reportes</p>
          </div>
        )}
      </div>
    </section>
  );
}

function formatDate(value) {
  return new Intl.DateTimeFormat("es-AR", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

function formatTime(value) {
  return new Intl.DateTimeFormat("es-AR", {
    hour: "2-digit",
    minute: "2-digit",
  }).format(new Date(value));
}

function list(value) {
  return Array.isArray(value) ? value : [];
}
