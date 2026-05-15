const API = "";

let credentials      = null;
let currentUser      = null;
let categories       = [];
let currentView      = "all";  // all | upcoming | overdue | starred | completed | calendar | cat-{id}
let currentCategoryId = null;
let selectedTaskIds  = new Set();
let calYear          = new Date().getFullYear();
let calMonth         = new Date().getMonth();

async function peticion(metodo, ruta, cuerpo) {
  console.log("→ Petición:", metodo, ruta, cuerpo ?? "");
  const opciones = {
    method: metodo,
    headers: {
      "Authorization": "Basic " + btoa(credentials.username + ":" + credentials.password),
      "Content-Type": "application/json"
    }
  };
  if (cuerpo !== undefined) {
    opciones.body = JSON.stringify(cuerpo);
  }
  const respuesta = await fetch(API + ruta, opciones);
  console.log("← Respuesta:", respuesta.status, respuesta.statusText);
  if (respuesta.status === 401) {
    logout();
    return null;
  }
  return respuesta;
}

async function peticionGet(ruta) {
  const respuesta = await peticion("GET", ruta);
  if (!respuesta || !respuesta.ok) return null;
  const datos = await respuesta.json();
  console.log("   JSON recibido:", datos);
  return datos;
}

async function tryLogin(username, password) {
  const respuesta = await fetch(API + "/auth/me", {
    method: "GET",
    headers: { "Authorization": "Basic " + btoa(username + ":" + password) }
  });
  if (respuesta.ok) {
    currentUser  = await respuesta.json();
    credentials  = { username, password };
    sessionStorage.setItem("creds", JSON.stringify({ username, password }));
    return true;
  }
  return false;
}

function logout() {
  credentials  = null;
  currentUser  = null;
  sessionStorage.removeItem("creds");
  document.getElementById("admin-nav-section").classList.add("hidden");
  document.getElementById("nav-admin").classList.add("hidden");
  showLogin();
}

function showLogin() {
  document.getElementById("app").classList.add("hidden");
  document.getElementById("login-screen").classList.remove("hidden");
  document.getElementById("inp-username").value = "";
  document.getElementById("inp-password").value = "";
  document.getElementById("login-error").classList.add("hidden");
  showLoginPanel();
}

function showLoginPanel() {
  document.getElementById("panel-login").classList.remove("hidden");
  document.getElementById("panel-register").classList.add("hidden");
}

function showRegisterPanel() {
  document.getElementById("panel-login").classList.add("hidden");
  document.getElementById("panel-register").classList.remove("hidden");
  document.getElementById("register-error").classList.add("hidden");
  document.getElementById("register-success").classList.add("hidden");
  document.getElementById("register-form").reset();
}

async function tryRegister(username, email, password, fullname) {
  const body = { username, email, password };
  if (fullname) body.fullname = fullname;
  const respuesta = await fetch(API + "/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
  if (respuesta.status === 201) return { ok: true };
  if (respuesta.status === 409) return { ok: false, msg: "El usuario o email ya existe." };
  let msg = "Error al registrar. Comprueba los datos.";
  try {
    const datos = await respuesta.json();
    if (datos.message) msg = datos.message;
  } catch (_) {}
  return { ok: false, msg };
}

function showApp() {
  document.getElementById("login-screen").classList.add("hidden");
  document.getElementById("app").classList.remove("hidden");
  renderUserInfo();
  const esAdmin  = currentUser && currentUser.role === "ADMIN";
  const esGestor = currentUser && currentUser.role === "GESTOR";
  document.getElementById("admin-nav-section").classList.toggle("hidden", !esAdmin && !esGestor);
  document.getElementById("nav-admin").classList.toggle("hidden", !esAdmin && !esGestor);
  if (esGestor) {
    document.getElementById("nav-admin").innerHTML = '<span class="nav-icon">&#9881;</span> Panel Gestor';
  }
  loadCategories();
  loadStats();
  loadTasks();
}

function renderUserInfo() {
  const nombre = currentUser.fullname || currentUser.username || "?";
  document.getElementById("user-name").textContent   = nombre;
  document.getElementById("user-email").textContent  = currentUser.email || "";
  document.getElementById("user-avatar").textContent = nombre.charAt(0).toUpperCase();
}


async function loadStats() {
  const datos = await peticionGet("/tasks/stats");
  if (!datos) return;
  document.getElementById("stat-total").textContent   = datos.total     || 0;
  document.getElementById("stat-pending").textContent = datos.pending   || 0;
  document.getElementById("stat-done").textContent    = datos.completed || 0;
}


async function loadCategories() {
  const datos = await peticionGet("/categories");
  if (!datos) return;
  categories = datos;
  renderCategoryNav();
  renderCategorySelect();
}

function renderCategoryNav() {
  const contenedor = document.getElementById("category-list");
  contenedor.innerHTML = "";
  categories.forEach(function(cat) {
    const a = document.createElement("a");
    a.className    = "nav-link";
    a.href         = "#";
    a.dataset.view = "cat-" + cat.id;
    a.innerHTML    = '<span class="nav-icon">&#128193;</span> ' + escHtml(cat.title);
    a.addEventListener("click", function(e) {
      e.preventDefault();
      closeSidebar();
      setView("cat-" + cat.id, cat.id, cat.title);
    });
    contenedor.appendChild(a);
  });
}

function renderCategorySelect() {
  const sel = document.getElementById("field-category");
  sel.innerHTML = '<option value="">Sin categoría</option>';
  categories.forEach(function(cat) {
    const opt       = document.createElement("option");
    opt.value       = cat.id;
    opt.textContent = cat.title;
    sel.appendChild(opt);
  });
}


const VIEW_TITLES = {
  all:       "Todas las tareas",
  upcoming:  "Próximas (7 días)",
  overdue:   "Vencidas",
  starred:   "Destacadas",
  completed: "Completadas",
  calendar:  "Calendario",
  admin:     "Panel de administración"
};

function setView(viewKey, catId, catTitle) {
  currentView       = viewKey;
  currentCategoryId = catId || null;
  clearSelection();
  document.getElementById("filter-priority").value = "";
  document.getElementById("inp-search").value      = "";

  document.querySelectorAll(".nav-link").forEach(function(a) {
    a.classList.remove("active");
  });
  const activo = document.querySelector('[data-view="' + viewKey + '"]');
  if (activo) activo.classList.add("active");

  const titulo = catTitle ? "Categoría: " + catTitle : (VIEW_TITLES[viewKey] || "Tareas");
  document.getElementById("view-title").textContent = titulo;

  const esAdmin      = viewKey === "admin";
  const esCalendario = viewKey === "calendar";
  document.getElementById("calendar-area").classList.toggle("hidden", !esCalendario);
  document.getElementById("task-list-area").classList.toggle("hidden",  esCalendario || esAdmin);
  document.getElementById("admin-area").classList.toggle("hidden",     !esAdmin);
  document.getElementById("topbar-right-controls").classList.toggle("hidden", esAdmin);

  if (esAdmin) {
    loadAdminPanel();
  } else if (esCalendario) {
    loadCalendar();
  } else {
    loadTasks();
  }
}


const MESES = ["Enero","Febrero","Marzo","Abril","Mayo","Junio",
               "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"];

async function loadCalendar() {
  const tareas = await peticionGet("/tasks");
  if (!tareas) return;
  renderCalendar(tareas);
}

function renderCalendar(tareas) {
  const tareasPorFecha = {};
  tareas.forEach(function(tarea) {
    if (tarea.deadline) {
      if (!tareasPorFecha[tarea.deadline]) tareasPorFecha[tarea.deadline] = [];
      tareasPorFecha[tarea.deadline].push(tarea);
    }
  });

  document.getElementById("cal-month-title").textContent = MESES[calMonth] + " " + calYear;

  const grid = document.getElementById("calendar-grid");
  grid.innerHTML = "";

  const primerDia  = new Date(calYear, calMonth, 1).getDay();
  const offset     = primerDia === 0 ? 6 : primerDia - 1;
  const diasDelMes = new Date(calYear, calMonth + 1, 0).getDate();
  const hoyStr     = today();

  for (let i = 0; i < offset; i++) {
    const vacia = document.createElement("div");
    vacia.className = "cal-cell cal-cell-empty";
    grid.appendChild(vacia);
  }

  for (let d = 1; d <= diasDelMes; d++) {
    const fechaStr = calYear + "-"
      + String(calMonth + 1).padStart(2, "0") + "-"
      + String(d).padStart(2, "0");

    const celda = document.createElement("div");
    celda.className = "cal-cell" + (fechaStr === hoyStr ? " cal-hoy" : "");

    const num = document.createElement("span");
    num.className   = "cal-num";
    num.textContent = d;
    celda.appendChild(num);

    (tareasPorFecha[fechaStr] || []).forEach(function(tarea) {
      const badge = document.createElement("div");
      badge.className = "cal-tarea"
        + (tarea.completed          ? " cal-tarea-hecha" : "")
        + (tarea.priority === "HIGH" ? " cal-tarea-alta"  : "")
        + (tarea.priority === "LOW"  ? " cal-tarea-baja"  : "");
      badge.textContent = tarea.title;
      badge.title = tarea.title + (tarea.description ? " — " + tarea.description : "");
      badge.addEventListener("click", function() { openEditModal(tarea); });
      celda.appendChild(badge);
    });

    grid.appendChild(celda);
  }
}


async function loadTasks() {
  document.getElementById("loading").classList.remove("hidden");
  document.getElementById("task-list").innerHTML = "";
  document.getElementById("empty-state").classList.add("hidden");

  let ruta;
  const busqueda  = document.getElementById("inp-search").value.trim();
  const prioridad = document.getElementById("filter-priority").value;

  if (currentView === "upcoming") {
    ruta = "/tasks/upcoming";
  } else if (currentView === "overdue") {
    ruta = "/tasks/overdue";
  } else {
    const params = new URLSearchParams();
    if (currentView === "starred")   params.set("starred",    "true");
    if (currentView === "completed") params.set("completed",  "true");
    if (currentCategoryId)           params.set("categoryId", currentCategoryId);
    if (busqueda)                    params.set("search",     busqueda);
    if (prioridad)                   params.set("priority",   prioridad);
    ruta = "/tasks?" + params.toString();
  }

  const tareas = await peticionGet(ruta);
  document.getElementById("loading").classList.add("hidden");

  if (!tareas) return;

  if (tareas.length === 0) {
    document.getElementById("empty-state").classList.remove("hidden");
    return;
  }

  const lista = document.getElementById("task-list");
  tareas.forEach(function(tarea) {
    lista.appendChild(buildTaskCard(tarea));
  });

  loadStats();
}


function buildTaskCard(tarea) {
  const li = document.createElement("li");
  li.className = "task-card"
    + (tarea.completed ? " task-done"    : "")
    + (tarea.starred   ? " task-starred" : "");

  const prioridadClase = { HIGH: "priority-high", MEDIUM: "priority-med", LOW: "priority-low" }[tarea.priority] || "";
  const prioridadTexto = { HIGH: "Alta",           MEDIUM: "Media",        LOW: "Baja"         }[tarea.priority] || "";

  const fechaHtml = tarea.deadline
    ? '<span class="task-deadline ' + (isOverdue(tarea) ? "overdue" : "") + '">' + formatDate(tarea.deadline) + "</span>"
    : "";

  const categoriaHtml = tarea.category
    ? '<span class="cat-badge">' + escHtml(tarea.category.title) + "</span>"
    : "";

  const etiquetasHtml = (tarea.tags || []).map(function(t) {
    return '<span class="tag">' + escHtml(t.name) + "</span>";
  }).join("");

  li.innerHTML =
    '<div class="task-check">' +
      '<input type="checkbox" class="task-select" />' +
      '<button class="btn-toggle ' + (tarea.completed ? "done" : "") + '">' +
        (tarea.completed ? "&#10003;" : "&#9675;") +
      "</button>" +
    "</div>" +
    '<div class="task-body">' +
      '<div class="task-top">' +
        '<span class="task-title">' + escHtml(tarea.title) + "</span>" +
        '<span class="priority-badge ' + prioridadClase + '">' + prioridadTexto + "</span>" +
      "</div>" +
      (tarea.description ? '<p class="task-desc">' + escHtml(tarea.description) + "</p>" : "") +
      '<div class="task-meta">' + categoriaHtml + fechaHtml + etiquetasHtml + "</div>" +
    "</div>" +
    '<div class="task-actions">' +
      '<button class="btn-star ' + (tarea.starred ? "starred" : "") + '">&#9733;</button>' +
      '<button class="btn-edit">&#9998;</button>' +
      '<button class="btn-delete">&#10005;</button>' +
    "</div>";

  li.querySelector(".btn-toggle").addEventListener("click", function() { toggleTarea(tarea.id); });
  li.querySelector(".btn-star").addEventListener("click",   function() { starTarea(tarea.id);   });
  li.querySelector(".btn-edit").addEventListener("click",   function() { openEditModal(tarea);  });
  li.querySelector(".btn-delete").addEventListener("click", function() { eliminarTarea(tarea.id); });

  li.querySelector(".task-select").addEventListener("change", function() {
    if (this.checked) selectedTaskIds.add(tarea.id);
    else              selectedTaskIds.delete(tarea.id);
    updateBulkBar();
  });

  return li;
}

function isOverdue(tarea) {
  if (!tarea.deadline || tarea.completed) return false;
  return tarea.deadline < today();
}

function today() {
  return new Date().toISOString().slice(0, 10);
}

function formatDate(fechaStr) {
  if (!fechaStr) return "";
  const p = fechaStr.split("-");
  return p[2] + "/" + p[1] + "/" + p[0];
}

function escHtml(str) {
  if (!str) return "";
  return String(str)
    .replace(/&/g,  "&amp;")
    .replace(/</g,  "&lt;")
    .replace(/>/g,  "&gt;")
    .replace(/"/g,  "&quot;");
}


async function toggleTarea(id) {
  const res = await peticion("PATCH", "/tasks/" + id + "/toggle");
  if (res && res.ok) await loadTasks();
}

async function starTarea(id) {
  const res = await peticion("PATCH", "/tasks/" + id + "/star");
  if (res && res.ok) await loadTasks();
}

async function eliminarTarea(id) {
  if (!confirm("¿Seguro que quieres eliminar esta tarea?")) return;
  const res = await peticion("DELETE", "/tasks/" + id);
  if (res && (res.ok || res.status === 204)) await loadTasks();
}


function openCreateModal() {
  resetTaskForm();
  document.getElementById("modal-title").textContent   = "Nueva tarea";
  document.getElementById("btn-save-task").textContent = "Crear tarea";
  document.getElementById("task-modal").classList.remove("hidden");
  document.getElementById("field-title").focus();
}

function openEditModal(tarea) {
  resetTaskForm();
  document.getElementById("modal-title").textContent   = "Editar tarea";
  document.getElementById("btn-save-task").textContent = "Guardar cambios";

  document.getElementById("task-id").value          = tarea.id;
  document.getElementById("field-title").value      = tarea.title       || "";
  document.getElementById("field-desc").value       = tarea.description || "";
  document.getElementById("field-deadline").value   = tarea.deadline    || "";
  document.getElementById("field-priority").value   = tarea.priority    || "MEDIUM";
  document.getElementById("field-starred").checked  = tarea.starred;
  if (tarea.category) {
    document.getElementById("field-category").value = tarea.category.id;
  }
  document.getElementById("field-tags").value = (tarea.tags || []).map(function(t) {
    return t.name;
  }).join(", ");

  document.getElementById("task-modal").classList.remove("hidden");
  document.getElementById("field-title").focus();
}

function closeTaskModal() {
  document.getElementById("task-modal").classList.add("hidden");
}

function resetTaskForm() {
  document.getElementById("task-form").reset();
  document.getElementById("task-id").value = "";
  document.getElementById("form-error").classList.add("hidden");
}

async function saveTask(e) {
  e.preventDefault();

  const id     = document.getElementById("task-id").value;
  const titulo = document.getElementById("field-title").value.trim();

  if (!titulo) {
    const errEl       = document.getElementById("form-error");
    errEl.textContent = "El título es obligatorio.";
    errEl.classList.remove("hidden");
    return;
  }

  const cuerpo = {
    title:       titulo,
    description: document.getElementById("field-desc").value.trim()     || null,
    deadline:    document.getElementById("field-deadline").value         || null,
    priority:    document.getElementById("field-priority").value,
    categoryId:  document.getElementById("field-category").value
                   ? Number(document.getElementById("field-category").value) : null,
    starred:     document.getElementById("field-starred").checked,
    tags:        document.getElementById("field-tags").value.trim()      || null
  };

  document.getElementById("btn-save-task").disabled = true;

  const metodo = id ? "PUT"        : "POST";
  const ruta   = id ? "/tasks/" + id : "/tasks";
  const res    = await peticion(metodo, ruta, cuerpo);

  document.getElementById("btn-save-task").disabled = false;

  if (!res) return;

  if (res.ok || res.status === 201) {
    closeTaskModal();
    await loadTasks();
  } else {
    let mensaje = "Error al guardar (HTTP " + res.status + ").";
    try {
      const errData = await res.json();
      console.error("Error del servidor:", errData);
      if (errData.detail) mensaje = errData.detail;
      else if (errData.message) mensaje = errData.message;
    } catch (e) {}
    const errEl       = document.getElementById("form-error");
    errEl.textContent = mensaje;
    errEl.classList.remove("hidden");
  }
}


function updateBulkBar() {
  const bar = document.getElementById("bulk-bar");
  if (selectedTaskIds.size > 0) {
    bar.classList.remove("hidden");
    const n = selectedTaskIds.size;
    document.getElementById("bulk-count").textContent = n + " seleccionada" + (n !== 1 ? "s" : "");
  } else {
    bar.classList.add("hidden");
  }
}

function clearSelection() {
  selectedTaskIds.clear();
  document.querySelectorAll(".task-select").forEach(function(cb) { cb.checked = false; });
  updateBulkBar();
}

async function bulkComplete() {
  if (selectedTaskIds.size === 0) return;
  const res = await peticion("PATCH", "/tasks/bulk/complete", { ids: [...selectedTaskIds] });
  if (res && res.ok) { clearSelection(); await loadTasks(); }
}

async function bulkDelete() {
  if (selectedTaskIds.size === 0) return;
  const n = selectedTaskIds.size;
  if (!confirm("¿Eliminar " + n + " tarea" + (n !== 1 ? "s" : "") + "?")) return;
  const res = await peticion("DELETE", "/tasks/bulk", { ids: [...selectedTaskIds] });
  if (res && (res.ok || res.status === 204)) { clearSelection(); await loadTasks(); }
}


function loadAdminPanel() {
  const esGestor = currentUser && currentUser.role === "GESTOR";
  document.getElementById("tab-users").classList.toggle("hidden", esGestor);
  document.getElementById("tab-tasks").classList.toggle("hidden", esGestor);
  showAdminTab(esGestor ? "categories" : "users");
}

function showAdminTab(tab) {
  document.getElementById("tab-users").classList.toggle("active", tab === "users");
  document.getElementById("tab-tasks").classList.toggle("active", tab === "tasks");
  document.getElementById("tab-categories").classList.toggle("active", tab === "categories");
  document.getElementById("admin-users-section").classList.toggle("hidden", tab !== "users");
  document.getElementById("admin-tasks-section").classList.toggle("hidden", tab !== "tasks");
  document.getElementById("admin-categories-section").classList.toggle("hidden", tab !== "categories");
  if (tab === "users") loadAdminUsers();
  else if (tab === "tasks") loadAdminTasks();
  else loadAdminCategories();
}

async function loadAdminUsers() {
  const loadingEl = document.getElementById("admin-users-loading");
  const tbody     = document.getElementById("admin-users-body");
  loadingEl.classList.remove("hidden");
  tbody.innerHTML = "";

  const usuarios = await peticionGet("/admin/users");
  loadingEl.classList.add("hidden");
  if (!usuarios) return;

  usuarios.forEach(function(user) {
    const esAdmin  = user.role === "ADMIN";
    const esGestor = user.role === "GESTOR";

    let rolClase, nuevoRol, btnTexto;
    if (esAdmin) {
      rolClase = "role-admin";
      nuevoRol = null;
      btnTexto = null;
    } else if (esGestor) {
      rolClase = "role-gestor";
      nuevoRol = "USER";
      btnTexto = "Quitar gestor";
    } else {
      rolClase = "role-user";
      nuevoRol = "GESTOR";
      btnTexto = "Hacer gestor";
    }

    const btnRolHtml = btnTexto
      ? '<button class="btn btn-sm btn-ghost btn-change-role">' + btnTexto + "</button>"
      : "";

    const tr = document.createElement("tr");
    tr.innerHTML =
      "<td>" + user.id + "</td>" +
      "<td>" + escHtml(user.username) + "</td>" +
      "<td>" + escHtml(user.email || "") + "</td>" +
      "<td>" + escHtml(user.fullname || "—") + "</td>" +
      '<td><span class="role-badge ' + rolClase + '">' + user.role + "</span></td>" +
      '<td><div class="admin-actions">' +
        btnRolHtml +
        '<button class="btn btn-sm btn-danger btn-del-user">Eliminar</button>' +
      "</div></td>";

    if (btnTexto) {
      tr.querySelector(".btn-change-role").addEventListener("click", function() {
        adminChangeRole(user.id, nuevoRol);
      });
    }
    tr.querySelector(".btn-del-user").addEventListener("click", function() {
      adminDeleteUser(user.id, user.username);
    });

    tbody.appendChild(tr);
  });
}

async function adminChangeRole(id, newRole) {
  const res = await peticion("PATCH", "/admin/users/" + id + "/role", { role: newRole });
  if (res && res.ok) loadAdminUsers();
}

async function adminDeleteUser(id, username) {
  if (!confirm("¿Eliminar al usuario «" + username + "»? Esta acción no se puede deshacer.")) return;
  const res = await peticion("DELETE", "/admin/users/" + id);
  if (res && (res.ok || res.status === 204)) loadAdminUsers();
}

async function loadAdminTasks() {
  const loadingEl = document.getElementById("admin-tasks-loading");
  const tbody     = document.getElementById("admin-tasks-body");
  loadingEl.classList.remove("hidden");
  tbody.innerHTML = "";

  const tareas = await peticionGet("/admin/tasks");
  loadingEl.classList.add("hidden");
  if (!tareas) return;

  tareas.forEach(function(tarea) {
    const prioridad      = { HIGH: "Alta", MEDIUM: "Media", LOW: "Baja" }[tarea.priority] || tarea.priority;
    const prioridadClase = { HIGH: "priority-high", MEDIUM: "priority-med", LOW: "priority-low" }[tarea.priority] || "";
    const autorNombre    = tarea.author ? escHtml(tarea.author.username) : "—";

    const tr = document.createElement("tr");
    tr.innerHTML =
      "<td>" + tarea.id + "</td>" +
      "<td>" + escHtml(tarea.title) + "</td>" +
      "<td>" + autorNombre + "</td>" +
      '<td><span class="status-badge ' + (tarea.completed ? "status-done" : "status-pending") + '">' +
        (tarea.completed ? "Completada" : "Pendiente") +
      "</span></td>" +
      '<td><span class="priority-badge ' + prioridadClase + '">' + prioridad + "</span></td>" +
      "<td>" + (tarea.deadline ? formatDate(tarea.deadline) : "—") + "</td>" +
      '<td><div class="admin-actions">' +
        '<button class="btn btn-sm btn-danger btn-del-task">Eliminar</button>' +
      "</div></td>";

    tr.querySelector(".btn-del-task").addEventListener("click", function() {
      adminDeleteTask(tarea.id, tarea.title);
    });

    tbody.appendChild(tr);
  });
}

async function adminDeleteTask(id, titulo) {
  if (!confirm("¿Eliminar la tarea «" + titulo + "»?")) return;
  const res = await peticion("DELETE", "/admin/tasks/" + id);
  if (res && (res.ok || res.status === 204)) loadAdminTasks();
}


function categoryApiBase() {
  return currentUser.role === "ADMIN" ? "/admin/categories" : "/gestor/categories";
}

async function loadAdminCategories() {
  const loadingEl = document.getElementById("admin-categories-loading");
  const tbody     = document.getElementById("admin-categories-body");
  loadingEl.classList.remove("hidden");
  tbody.innerHTML = "";

  const cats = await peticionGet(categoryApiBase());
  loadingEl.classList.add("hidden");
  if (!cats) return;

  cats.forEach(function(cat) {
    const tr = document.createElement("tr");
    tr.innerHTML =
      "<td>" + cat.id + "</td>" +
      "<td>" + escHtml(cat.title) + "</td>" +
      '<td><div class="admin-actions">' +
        '<button class="btn btn-sm btn-ghost btn-edit-cat">Editar</button>' +
        '<button class="btn btn-sm btn-danger btn-del-cat">Eliminar</button>' +
      "</div></td>";

    tr.querySelector(".btn-edit-cat").addEventListener("click", function() {
      openCategoryModal(cat);
    });
    tr.querySelector(".btn-del-cat").addEventListener("click", function() {
      adminDeleteCategory(cat.id, cat.title);
    });

    tbody.appendChild(tr);
  });
}

function openCategoryModal(cat) {
  document.getElementById("category-id").value = cat ? cat.id : "";
  document.getElementById("category-name-field").value = cat ? cat.title : "";
  document.getElementById("category-modal-title").textContent = cat ? "Editar categoría" : "Nueva categoría";
  document.getElementById("category-form-error").classList.add("hidden");
  document.getElementById("category-modal").classList.remove("hidden");
  document.getElementById("category-name-field").focus();
}

function closeCategoryModal() {
  document.getElementById("category-modal").classList.add("hidden");
}

async function saveCategoryModal(e) {
  e.preventDefault();
  const id      = document.getElementById("category-id").value;
  const title   = document.getElementById("category-name-field").value.trim();
  const errorEl = document.getElementById("category-form-error");

  if (!title) {
    errorEl.textContent = "El nombre es obligatorio.";
    errorEl.classList.remove("hidden");
    return;
  }

  const base = categoryApiBase();
  const res = id
    ? await peticion("PUT", base + "/" + id, { title })
    : await peticion("POST", base, { title });

  if (res && (res.ok || res.status === 201)) {
    closeCategoryModal();
    loadAdminCategories();
    loadCategories();
  } else {
    errorEl.textContent = "Error al guardar la categoría.";
    errorEl.classList.remove("hidden");
  }
}

async function adminDeleteCategory(id, title) {
  if (!confirm("¿Eliminar la categoría «" + title + "»? Las tareas se moverán a Main.")) return;
  const res = await peticion("DELETE", categoryApiBase() + "/" + id);
  if (res && (res.ok || res.status === 204)) {
    loadAdminCategories();
    loadCategories();
  }
}


function toggleSidebar() {
  document.querySelector(".sidebar").classList.toggle("sidebar-open");
  document.getElementById("sidebar-backdrop").classList.toggle("hidden");
}

function closeSidebar() {
  document.querySelector(".sidebar").classList.remove("sidebar-open");
  document.getElementById("sidebar-backdrop").classList.add("hidden");
}


async function init() {
  const guardado = sessionStorage.getItem("creds");
  if (guardado) {
    const { username, password } = JSON.parse(guardado);
    const ok = await tryLogin(username, password);
    if (ok) { showApp(); return; }
  }
  showLogin();
}


document.getElementById("login-form").addEventListener("submit", async function(e) {
  e.preventDefault();
  const username = document.getElementById("inp-username").value.trim();
  const password = document.getElementById("inp-password").value;
  const errEl    = document.getElementById("login-error");
  const btn      = document.getElementById("login-btn");

  btn.disabled    = true;
  btn.textContent = "Entrando...";
  errEl.classList.add("hidden");

  const ok = await tryLogin(username, password);

  btn.disabled    = false;
  btn.textContent = "Entrar";

  if (ok) {
    showApp();
  } else {
    errEl.textContent = "Usuario o contraseña incorrectos.";
    errEl.classList.remove("hidden");
  }
});

document.getElementById("link-go-register").addEventListener("click", function(e) {
  e.preventDefault();
  showRegisterPanel();
});

document.getElementById("link-go-login").addEventListener("click", function(e) {
  e.preventDefault();
  showLoginPanel();
});

document.getElementById("register-form").addEventListener("submit", async function(e) {
  e.preventDefault();
  const username = document.getElementById("reg-username").value.trim();
  const email    = document.getElementById("reg-email").value.trim();
  const fullname = document.getElementById("reg-fullname").value.trim();
  const password = document.getElementById("reg-password").value;
  const errEl    = document.getElementById("register-error");
  const okEl     = document.getElementById("register-success");
  const btn      = document.getElementById("register-btn");

  errEl.classList.add("hidden");
  okEl.classList.add("hidden");
  btn.disabled    = true;
  btn.textContent = "Creando cuenta...";

  const resultado = await tryRegister(username, email, password, fullname);

  btn.disabled    = false;
  btn.textContent = "Crear cuenta";

  if (resultado.ok) {
    okEl.textContent = "Cuenta creada. Iniciando sesión...";
    okEl.classList.remove("hidden");
    const loginOk = await tryLogin(username, password);
    if (loginOk) showApp();
  } else {
    errEl.textContent = resultado.msg;
    errEl.classList.remove("hidden");
  }
});

document.getElementById("btn-logout").addEventListener("click", logout);
document.getElementById("btn-sidebar-toggle").addEventListener("click", toggleSidebar);
document.getElementById("sidebar-backdrop").addEventListener("click", closeSidebar);

document.getElementById("btn-new-task").addEventListener("click", openCreateModal);
document.getElementById("task-form").addEventListener("submit", saveTask);
document.getElementById("modal-close").addEventListener("click", closeTaskModal);
document.getElementById("btn-cancel-modal").addEventListener("click", closeTaskModal);

document.getElementById("inp-search").addEventListener("keydown", function(e) {
  if (e.key === "Enter") loadTasks();
});
document.getElementById("filter-priority").addEventListener("change", function() { loadTasks(); });

document.querySelectorAll(".nav-link[data-view]").forEach(function(a) {
  a.addEventListener("click", function(e) {
    e.preventDefault();
    closeSidebar();
    const vista = a.dataset.view;
    if (!vista.startsWith("cat-")) {
      currentCategoryId = null;
      setView(vista);
    }
  });
});

document.getElementById("cal-prev").addEventListener("click", function() {
  calMonth--;
  if (calMonth < 0) { calMonth = 11; calYear--; }
  loadCalendar();
});

document.getElementById("cal-next").addEventListener("click", function() {
  calMonth++;
  if (calMonth > 11) { calMonth = 0; calYear++; }
  loadCalendar();
});

document.getElementById("tab-users").addEventListener("click", function() { showAdminTab("users"); });
document.getElementById("tab-tasks").addEventListener("click", function() { showAdminTab("tasks"); });
document.getElementById("tab-categories").addEventListener("click", function() { showAdminTab("categories"); });

document.getElementById("btn-new-category").addEventListener("click", function() { openCategoryModal(null); });
document.getElementById("category-form").addEventListener("submit", saveCategoryModal);
document.getElementById("category-modal-close").addEventListener("click", closeCategoryModal);
document.getElementById("btn-cancel-category").addEventListener("click", closeCategoryModal);
document.getElementById("category-modal").addEventListener("click", function(e) {
  if (e.target === this) closeCategoryModal();
});

document.getElementById("btn-bulk-complete").addEventListener("click", bulkComplete);
document.getElementById("btn-bulk-delete").addEventListener("click", bulkDelete);
document.getElementById("btn-bulk-cancel").addEventListener("click", clearSelection);

document.getElementById("task-modal").addEventListener("click", function(e) {
  if (e.target === this) closeTaskModal();
});
document.addEventListener("keydown", function(e) {
  if (e.key === "Escape") closeTaskModal();
});

init();
