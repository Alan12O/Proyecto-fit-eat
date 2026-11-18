
let catalogoIngredientes = [];
let lotesDespensa = [];
let listaRecetas = [];
let estrategiaActual = 'antidesperdicio';
let ingredienteSeleccionadoTemporal = null;
let loteEdicionActual = null;
let recetaSeleccionadaParaCocinar = null;

const API = {
    ingredientes: 'api/ingredientes',
    despensa: 'api/despensa',
    recetas: 'api/recetas'
};

const IMAGENES_INGREDIENTES = {
    1: 'images/leche.jpg',
    2: 'images/huevos.jpg',
    3: 'images/harina.jpg',
    4: 'images/azucar.jpg?v=2',
    5: 'images/tomates.jpg?v=2',
    6: 'images/queso.jpg',
    7: 'images/arroz.jpg',
    8: 'images/pollo.jpg',
    9: 'images/lentejas.jpg',
    10: 'images/pasta.jpg',
    11: 'images/cebolla.jpg',
    12: 'images/zanahorias.jpg',
    13: 'images/aceite.jpg',
    14: 'images/ajo.jpg',
    15: 'images/avena.jpg',
    16: 'images/platano.jpg?v=2',
    17: 'images/espinacas.jpg',
    18: 'images/carne.jpg'
};

const IMAGENES_RECETAS = {
    1: 'images/hotcakes.jpg',
    2: 'images/tortilla_tomate.jpg',
    3: 'images/pollo_arroz.jpg',
    4: 'images/arroz_con_leche.jpg?v=2',
    5: 'images/sopa_lentejas.jpg',
    6: 'images/pasta_verduras.jpg',
    7: 'images/omelette_espinaca.jpg',
    8: 'images/batido_avena.jpg',
    9: 'images/bolonesa.jpg'
};

const PLACEHOLDER_IMG = 'images/splash_hero.jpg';

const SVG_ICONS = {
    edit: `<svg viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>`,
    delete: `<svg viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/><line x1="10" y1="11" x2="10" y2="17"/><line x1="14" y1="11" x2="14" y2="17"/></svg>`,
    check: `<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="#27ae60" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`,
    alertCircle: `<svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" style="vertical-align:-1px;margin-right:4px;"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`,
    clock: `<svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" style="vertical-align:-1px;margin-right:4px;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`,
    checkSm: `<svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="#27ae60" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" style="vertical-align:middle;margin-right:4px;"><polyline points="20 6 9 17 4 12"/></svg>`,
    crossSm: `<svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="#e74c3c" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" style="vertical-align:middle;margin-right:4px;"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>`,
    leaf: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="#9a7d0a" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/><path d="M2 21c0-3 1.85-5.36 5.08-6"/></svg>`,
    bolt: `<svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="#0284c7" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>`
};

document.addEventListener('DOMContentLoaded', () => {
    cargarIngredientes();
    cargarDespensa();
    cargarRecetas();

    if (window.location.hash === '#caducidades') {
        document.getElementById('splashView').style.display = 'none';
        document.getElementById('appContainer').style.display = 'block';
        mostrarVistaCaducidades();
    } else if (window.location.hash === '#recetas') {
        document.getElementById('splashView').style.display = 'none';
        document.getElementById('appContainer').style.display = 'block';
        alternarVistaRecetas(true);
    } else if (window.location.hash === '#dashboard') {
        document.getElementById('splashView').style.display = 'none';
        document.getElementById('appContainer').style.display = 'block';
    }
});

function entrarAlDashboard() {
    const splash = document.getElementById('splashView');
    const app = document.getElementById('appContainer');

    splash.style.transition = 'opacity 0.35s ease, transform 0.35s ease';
    splash.style.opacity = '0';
    splash.style.transform = 'scale(0.97)';

    setTimeout(() => {
        splash.style.display = 'none';
        app.style.display = 'block';
    }, 350);
}

function irAlDashboard() {
    document.getElementById('mainDashboardView').style.display = 'grid';
    document.getElementById('recetasFullView').style.display = 'none';
    document.getElementById('caducidadesDetailView').style.display = 'none';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function alternarVistaRecetas(mostrarFull) {
    const mainView = document.getElementById('mainDashboardView');
    const fullView = document.getElementById('recetasFullView');
    const cadView = document.getElementById('caducidadesDetailView');

    cadView.style.display = 'none';

    if (mostrarFull) {
        mainView.style.display = 'none';
        fullView.style.display = 'block';
        renderizarRecetasGridFull();
    } else {
        fullView.style.display = 'none';
        mainView.style.display = 'grid';
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function mostrarVistaCaducidades() {
    document.getElementById('mainDashboardView').style.display = 'none';
    document.getElementById('recetasFullView').style.display = 'none';
    document.getElementById('caducidadesDetailView').style.display = 'block';
    renderizarDetalleCaducidades();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function cargarIngredientes() {
    try {
        const resp = await fetch(API.ingredientes);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        catalogoIngredientes = await resp.json();
        renderizarModalIngredientes();
    } catch (err) {
        console.error('Error cargando catálogo:', err);
    }
}

function renderizarModalIngredientes() {
    const grid = document.getElementById('ingredientsImageGrid');
    grid.innerHTML = '';

    catalogoIngredientes.forEach(ing => {
        const card = document.createElement('div');
        card.className = 'ingredient-select-card';
        card.dataset.id = ing.id;

        const imgUrl = IMAGENES_INGREDIENTES[ing.id] || PLACEHOLDER_IMG;

        card.innerHTML = `
            <img src="${imgUrl}" alt="${escapeHtml(ing.nombre)}" class="ing-card-img" onerror="this.src='${PLACEHOLDER_IMG}'">
            <span class="ing-card-name">${escapeHtml(ing.nombre)}</span>
            <small style="font-size: 0.72rem; color: #7f8c8d;">${ing.categoria === 'PERECEDERO' ? 'Perecedero' : 'No Perecedero'}</small>
        `;

        card.onclick = () => seleccionarTarjetaIngrediente(ing, card);
        grid.appendChild(card);
    });
}

function abrirModalSeleccionIngrediente() {
    document.getElementById('modalSeleccionIngrediente').style.display = 'flex';
}

function cerrarModalSeleccionIngrediente() {
    document.getElementById('modalSeleccionIngrediente').style.display = 'none';
}

function seleccionarTarjetaIngrediente(ing, cardElement) {
    document.querySelectorAll('.ingredient-select-card').forEach(c => c.classList.remove('active'));
    cardElement.classList.add('active');
    ingredienteSeleccionadoTemporal = ing;
}

function confirmarSeleccionIngrediente() {
    if (!ingredienteSeleccionadoTemporal) {
        alert('Por favor haz clic en una de las imágenes para seleccionar un ingrediente.');
        return;
    }

    const ing = ingredienteSeleccionadoTemporal;
    document.getElementById('selectedIngredienteId').value = ing.id;
    document.getElementById('pickerLabel').textContent = ing.nombre;
    document.getElementById('labelUnidadForm').textContent = ing.unidadMedida;

    const inputFecha = document.getElementById('inputFechaCaducidad');
    const groupFecha = document.getElementById('groupFechaCaducidad');

    if (ing.categoria === 'NO_PERECEDERO') {
        inputFecha.value = '';
        inputFecha.disabled = true;
        groupFecha.title = 'Producto no perecedero: no requiere fecha de caducidad.';
    } else {
        inputFecha.disabled = false;
        groupFecha.title = '';
    }

    cerrarModalSeleccionIngrediente();
}

async function cargarDespensa() {
    try {
        const resp = await fetch(API.despensa);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        const data = await resp.json();

        lotesDespensa = data.despensa || [];
        const alertas = data.alertas || [];

        actualizarKPIs(alertas);

        renderizarTablaInventario(lotesDespensa);

        if (document.getElementById('caducidadesDetailView').style.display === 'block') {
            renderizarDetalleCaducidades();
        }

    } catch (err) {
        console.error('Error cargando despensa:', err);
        document.getElementById('inventarioTbody').innerHTML = `
            <tr><td colspan="6" class="text-center py-4" style="color:#e74c3c;">Error al conectar con la base de datos MySQL.</td></tr>
        `;
    }
}

function actualizarKPIs(alertas) {
    document.getElementById('kpiTotalLotes').textContent = lotesDespensa.length;

    const totalAlertas = alertas.length;
    document.getElementById('kpiAlertas').textContent = totalAlertas;

    const redBadge = document.getElementById('redLeafBadge');
    if (totalAlertas > 0) {
        redBadge.style.display = 'block';
    } else {
        redBadge.style.display = 'none';
    }
}

function renderizarTablaInventario(lotes) {
    const tbody = document.getElementById('inventarioTbody');
    tbody.innerHTML = '';

    if (!lotes || lotes.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4" style="color: #95a5a6;">Tu despensa está vacía. ¡Agrega tus primeros ingredientes!</td></tr>`;
        return;
    }

    lotes.forEach(lote => {
        const tr = document.createElement('tr');
        const ing = lote.ingrediente || {};
        const imgUrl = IMAGENES_INGREDIENTES[ing.id] || PLACEHOLDER_IMG;

        const catClass = obtenerClaseCategoria(ing.nombre, ing.categoria);
        const catNombre = obtenerNombreCategoria(ing.nombre, ing.categoria);
        const catPill = `<span class="category-pill ${catClass}">${escapeHtml(catNombre)}</span>`;

        let estadoPill = '';
        const dias = lote.diasParaCaducar;
        const fechaFormateada = lote.fechaCaducidad ? formatearFecha(lote.fechaCaducidad) : '<span style="color:#bdc3c7;">N/A</span>';

        if (ing.categoria === 'NO_PERECEDERO' || dias === null || dias === undefined || dias > 9999) {
            estadoPill = `<span class="status-pill status-ok">En buen estado</span>`;
        } else if (dias < 0) {
            estadoPill = `<span class="status-pill status-vencido">Vencido</span>`;
        } else if (dias <= 3) {
            estadoPill = `<span class="status-pill status-proximo">Próximo a vencer</span>`;
        } else {
            estadoPill = `<span class="status-pill status-ok">En buen estado</span>`;
        }

        tr.innerHTML = `
            <td>
                <div class="table-food-item">
                    <img src="${imgUrl}" alt="${escapeHtml(ing.nombre)}" class="food-thumb" onerror="this.src='${PLACEHOLDER_IMG}'">
                    <strong>${escapeHtml(ing.nombre || 'Desconocido')}</strong>
                </div>
            </td>
            <td>${catPill}</td>
            <td><strong>${lote.cantidad}</strong> <small>${escapeHtml(ing.unidadMedida || '')}</small></td>
            <td>${fechaFormateada}</td>
            <td>${estadoPill}</td>
            <td class="text-center">
                <div class="table-action-btns">
                    <button class="btn-icon-edit" title="Modificar cantidad" onclick="abrirModalEditarLote(${lote.id}, '${escapeHtml(ing.nombre)}', ${lote.cantidad}, '${escapeHtml(ing.unidadMedida)}')">
                        ${SVG_ICONS.edit}
                    </button>
                    <button class="btn-icon-delete" title="Eliminar lote" onclick="eliminarLote(${lote.id}, '${escapeHtml(ing.nombre)}')">
                        ${SVG_ICONS.delete}
                    </button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function obtenerClaseCategoria(nombre, categoria) {
    if (!nombre) return 'cat-default';
    const n = nombre.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    if (n.includes('arroz') || n.includes('harina') || n.includes('pasta') || n.includes('avena')) return 'cat-granos';
    if (n.includes('leche') || n.includes('queso')) return 'cat-lacteos';
    if (n.includes('tomate') || n.includes('zanahoria') || n.includes('cebolla') || n.includes('espinaca') || n.includes('ajo')) return 'cat-verduras';
    if (n.includes('pollo') || n.includes('carne') || n.includes('huevo')) return 'cat-carnes';
    if (n.includes('lenteja') || n.includes('frijol')) return 'cat-legumbres';
    if (n.includes('platano') || n.includes('fruta')) return 'cat-frutas';
    if (n.includes('aceite')) return 'cat-default';
    return categoria === 'PERECEDERO' ? 'cat-verduras' : 'cat-granos';
}

function obtenerNombreCategoria(nombre, categoria) {
    if (!nombre) return categoria === 'PERECEDERO' ? 'Perecedero' : 'No Perecedero';
    const n = nombre.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    if (n.includes('leche') || n.includes('queso')) return 'Lácteos';
    if (n.includes('tomate') || n.includes('zanahoria') || n.includes('cebolla') || n.includes('espinaca') || n.includes('ajo')) return 'Verduras';
    if (n.includes('pollo') || n.includes('carne') || n.includes('huevo')) return 'Proteínas';
    if (n.includes('arroz') || n.includes('harina') || n.includes('pasta') || n.includes('avena')) return 'Granos';
    if (n.includes('lenteja') || n.includes('frijol')) return 'Legumbres';
    if (n.includes('platano') || n.includes('fruta')) return 'Frutas';
    if (n.includes('aceite')) return 'Aceites';
    if (n.includes('azucar')) return 'Endulzantes';
    return categoria === 'PERECEDERO' ? 'Perecedero' : 'No Perecedero';
}

function formatearCantidad(cantidad, unidad) {
    if (unidad === 'ml' && cantidad >= 1000) {
        const l = cantidad / 1000;
        return (l % 1 === 0 ? l.toFixed(0) : l.toFixed(1)) + ' L';
    }
    if (unidad === 'g' && cantidad >= 1000) {
        const kg = cantidad / 1000;
        return (kg % 1 === 0 ? kg.toFixed(0) : kg.toFixed(1)) + ' kg';
    }
    return `${cantidad} ${unidad || ''}`.trim();
}

function formatearFecha(fechaStr) {
    if (!fechaStr) return '';
    const partes = fechaStr.split('-');
    if (partes.length === 3) {
        return `${partes[2]}/${partes[1]}/${partes[0]}`;
    }
    return fechaStr;
}

async function guardarNuevoLote() {
    const idIngrediente = parseInt(document.getElementById('selectedIngredienteId').value, 10);
    const inputCant = document.getElementById('inputCantidad');
    const inputFecha = document.getElementById('inputFechaCaducidad');
    const cantidad = parseFloat(inputCant.value);

    if (!idIngrediente) {
        alert('Por favor selecciona un ingrediente haciendo clic en "Selecciona ingrediente".');
        abrirModalSeleccionIngrediente();
        return;
    }
    if (isNaN(cantidad) || cantidad <= 0) {
        alert('Ingresa una cantidad válida mayor a 0.');
        inputCant.focus();
        return;
    }

    const ing = catalogoIngredientes.find(i => i.id === idIngrediente);
    let fecha = null;
    if (ing && ing.categoria === 'PERECEDERO') {
        if (!inputFecha.value) {
            alert('Los productos perecederos requieren fecha de caducidad.');
            inputFecha.focus();
            return;
        }
        fecha = inputFecha.value;
    }

    try {
        const resp = await fetch(API.despensa, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json;charset=UTF-8' },
            body: JSON.stringify({
                idIngrediente: idIngrediente,
                cantidad: cantidad,
                fechaCaducidad: fecha
            })
        });
        const data = await resp.json();

        if (resp.ok && data.exito) {
            mostrarToastExito('Lote agregado exitosamente', 'Se actualizó tu inventario de despensa.');

            document.getElementById('selectedIngredienteId').value = '';
            document.getElementById('pickerLabel').textContent = 'Selecciona ingrediente';
            inputCant.value = '';
            inputFecha.value = '';

            cargarDespensa();
            cargarRecetas();
        } else {
            mostrarToastError('No se pudo agregar el lote', data.mensaje || data.error);
        }
    } catch (err) {
        console.error('Error agregando lote:', err);
        mostrarToastError('Error de conexión', 'No se pudo contactar al servidor.');
    }
}

function abrirModalEditarLote(id, nombre, cantActual, unidad) {
    loteEdicionActual = { id, nombre, unidad };
    document.getElementById('labelLoteEdicion').textContent = `Lote #${id}: ${nombre}`;
    document.getElementById('inputEditarCantidad').value = cantActual;
    document.getElementById('labelUnidadEdicion').textContent = unidad;
    document.getElementById('modalEditarLote').style.display = 'flex';
}

function cerrarModalEditarLote() {
    loteEdicionActual = null;
    document.getElementById('modalEditarLote').style.display = 'none';
}

async function guardarEdicionLote() {
    if (!loteEdicionActual) return;
    const nuevaCant = parseFloat(document.getElementById('inputEditarCantidad').value);
    if (isNaN(nuevaCant)) {
        alert('Ingresa una cantidad válida.');
        return;
    }

    try {
        const resp = await fetch(API.despensa, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json;charset=UTF-8' },
            body: JSON.stringify({
                id: loteEdicionActual.id,
                cantidad: nuevaCant
            })
        });
        const data = await resp.json();

        if (resp.ok && data.exito) {
            mostrarToastExito('Cantidad actualizada', 'Se guardaron los cambios del lote.');
            cerrarModalEditarLote();
            cargarDespensa();
            cargarRecetas();
        } else {
            mostrarToastError('Error al actualizar', data.mensaje || data.error);
        }
    } catch (err) {
        console.error('Error actualizando lote:', err);
        mostrarToastError('Error de red', 'No se pudo actualizar el lote.');
    }
}

async function eliminarLote(id, nombre) {
    if (!confirm(`¿Eliminar este lote de "${nombre}" de la despensa?`)) return;

    try {
        const resp = await fetch(`${API.despensa}?id=${id}`, { method: 'DELETE' });
        const data = await resp.json();

        if (resp.ok && data.exito) {
            mostrarToastExito('Lote eliminado', `Se eliminó "${nombre}" del inventario.`);
            cargarDespensa();
            cargarRecetas();
        } else {
            mostrarToastError('Error al eliminar', data.mensaje || data.error);
        }
    } catch (err) {
        console.error('Error eliminando lote:', err);
        mostrarToastError('Error de red', 'No se pudo eliminar el lote.');
    }
}

function renderizarDetalleCaducidades() {
    const listVencidos = document.getElementById('listaLotesVencidos');
    const listPorCaducar = document.getElementById('listaLotesPorCaducar');

    listVencidos.innerHTML = '';
    listPorCaducar.innerHTML = '';

    const vencidos = lotesDespensa.filter(p => p.fechaCaducidad && p.diasParaCaducar < 0);
    const porCaducar = lotesDespensa.filter(p => p.fechaCaducidad && p.diasParaCaducar >= 0 && p.diasParaCaducar <= 3);

    document.getElementById('badgeTotalVencidos').textContent = vencidos.length;
    document.getElementById('badgeTotalPorCaducar').textContent = porCaducar.length;

    if (vencidos.length === 0) {
        listVencidos.innerHTML = `<div class="empty-state-alert">${SVG_ICONS.check}<span>¡Excelente! No tienes productos vencidos en este momento.</span></div>`;
    } else {
        vencidos.forEach(p => {
            listVencidos.appendChild(crearFilaAlertaCaducidad(p, 'vencido'));
        });
    }

    if (porCaducar.length === 0) {
        listPorCaducar.innerHTML = `<div class="empty-state-alert">${SVG_ICONS.check}<span>No hay productos próximos a vencer en los siguientes 3 días.</span></div>`;
    } else {
        porCaducar.forEach(p => {
            listPorCaducar.appendChild(crearFilaAlertaCaducidad(p, 'proximo'));
        });
    }
}

function crearFilaAlertaCaducidad(lote, tipo) {
    const div = document.createElement('div');
    div.className = 'alert-lote-row';
    const ing = lote.ingrediente || {};
    const imgUrl = IMAGENES_INGREDIENTES[ing.id] || PLACEHOLDER_IMG;
    const catClass = obtenerClaseCategoria(ing.nombre, ing.categoria);
    const catNombre = obtenerNombreCategoria(ing.nombre, ing.categoria);
    const cantFormateada = formatearCantidad(lote.cantidad, ing.unidadMedida);

    const badgeEstado = tipo === 'vencido'
        ? `<span class="status-pill status-vencido">${SVG_ICONS.alertCircle}Vencido</span>`
        : `<span class="status-pill status-proximo">${SVG_ICONS.clock}Por Caducar</span>`;

    div.innerHTML = `
        <div class="pill-food-item">
            <img src="${imgUrl}" alt="${escapeHtml(ing.nombre)}" class="pill-food-thumb" onerror="this.src='${PLACEHOLDER_IMG}'">
            <span class="pill-food-name">${escapeHtml(ing.nombre)}</span>
        </div>
        <span class="category-pill ${catClass}">${escapeHtml(catNombre)}</span>
        <span class="pill-qty"><strong>${escapeHtml(cantFormateada)}</strong></span>
        <span class="pill-date">${formatearFecha(lote.fechaCaducidad)}</span>
        ${badgeEstado}
        <div class="table-action-btns">
            <button class="btn-icon-edit" title="Modificar cantidad" onclick="abrirModalEditarLote(${lote.id}, '${escapeHtml(ing.nombre)}', ${lote.cantidad}, '${escapeHtml(ing.unidadMedida)}')">${SVG_ICONS.edit}</button>
            <button class="btn-icon-delete" title="Eliminar lote" onclick="eliminarLote(${lote.id}, '${escapeHtml(ing.nombre)}')">${SVG_ICONS.delete}</button>
        </div>
    `;
    return div;
}

function cambiarEstrategia(estrategia) {
    if (estrategiaActual === estrategia) return;
    estrategiaActual = estrategia;

    document.getElementById('btnEstrategiaAntiDesp').classList.toggle('active', estrategia === 'antidesperdicio');
    document.getElementById('btnEstrategiaDispTotal').classList.toggle('active', estrategia === 'disponibilidad');

    document.getElementById('btnFullAntiDesp').classList.toggle('active', estrategia === 'antidesperdicio');
    document.getElementById('btnFullDispTotal').classList.toggle('active', estrategia === 'disponibilidad');

    cargarRecetas();
}

async function cargarRecetas() {
    try {
        const resp = await fetch(`${API.recetas}?estrategia=${encodeURIComponent(estrategiaActual)}`);
        if (!resp.ok) throw new Error(`HTTP ${resp.status}`);
        listaRecetas = await resp.json();

        const listas = listaRecetas.filter(r => r.tieneIngredientesSuficientes).length;
        document.getElementById('kpiRecetasListas').textContent = listas;

        renderizarRecetasCompact();

        if (document.getElementById('recetasFullView').style.display === 'block') {
            renderizarRecetasGridFull();
        }

        actualizarSelectModalCocinar();

    } catch (err) {
        console.error('Error cargando recetas:', err);
    }
}

function renderizarRecetasCompact() {
    const bannerCompact = document.getElementById('strategyCompactBanner');
    if (bannerCompact) {
        if (estrategiaActual === 'antidesperdicio') {
            bannerCompact.className = 'strategy-compact-banner banner-antidesp';
            bannerCompact.innerHTML = `
                <div class="scb-badge">Anti-Desperdicio Activo</div>
                <div class="scb-text">Prioriza salvar insumos proximos a caducar en tu despensa.</div>
            `;
        } else {
            bannerCompact.className = 'strategy-compact-banner banner-disptotal';
            bannerCompact.innerHTML = `
                <div class="scb-badge">Disponibilidad Total Activa</div>
                <div class="scb-text">100% de ingredientes en despensa, ordenadas por tiempo mas rapido.</div>
            `;
        }
    }

    const container = document.getElementById('recetasCompactList');
    container.innerHTML = '';

    if (!listaRecetas || listaRecetas.length === 0) {
        container.innerHTML = `<p style="padding:1rem; color:#7f8c8d;">No hay recetas disponibles.</p>`;
        return;
    }

    const recetasParaMostrar = listaRecetas.slice(0, 3);

    recetasParaMostrar.forEach((receta, idx) => {
        const card = document.createElement('div');
        card.className = 'receta-compact-card';
        const imgUrl = IMAGENES_RECETAS[receta.id] || PLACEHOLDER_IMG;
        const suf = receta.tieneIngredientesSuficientes;

        let checklistHtml = '';
        if (suf) {
            checklistHtml += `<li>${SVG_ICONS.checkSm} Tienes todos los ingredientes</li>`;
            checklistHtml += `<li>${SVG_ICONS.checkSm} Lista en ${receta.tiempoPreparacion} min</li>`;
        } else {
            const faltantesNombres = (receta.ingredientesFaltantes || []).map(f => f.split(':')[0].toLowerCase());
            receta.ingredientes.forEach(ri => {
                const nom = ri.ingrediente ? ri.ingrediente.nombre : '';
                const faltaEste = faltantesNombres.some(fn => nom.toLowerCase().includes(fn));
                if (faltaEste) {
                    checklistHtml += `<li>${SVG_ICONS.crossSm} Falta ${escapeHtml(nom)}</li>`;
                } else {
                    checklistHtml += `<li>${SVG_ICONS.checkSm} Tienes ${escapeHtml(nom)}</li>`;
                }
            });
        }

        let strategyTagHtml = '';
        if (estrategiaActual === 'antidesperdicio') {
            if (receta.ingredientesRescatados && receta.ingredientesRescatados.length > 0) {
                strategyTagHtml = `<div class="rescata-highlight-pill">Salva: <strong>${escapeHtml(receta.ingredientesRescatados.join(', '))}</strong></div>`;
            } else {
                strategyTagHtml = `<div class="rescata-neutral-pill">Sin insumos en riesgo</div>`;
            }
        } else {
            if (suf) {
                strategyTagHtml = `<div class="disp-highlight-pill">#${idx + 1} Mas rapida · <strong>${receta.tiempoPreparacion} min</strong></div>`;
            } else {
                strategyTagHtml = `<div class="disp-missing-pill">Faltan insumos · ${receta.tiempoPreparacion} min</div>`;
            }
        }

        const badgeHtml = suf
            ? `<span class="receta-ready-badge">Listo para cocinar</span>`
            : `<span class="receta-missing-badge">Faltan algunos insumos</span>`;

        const btnText = suf ? 'Ver Receta' : 'Ver Detalles';
        const btnClass = suf ? 'btn-receta-action' : 'btn-receta-action outline';

        card.innerHTML = `
            <img src="${imgUrl}" alt="${escapeHtml(receta.nombre)}" class="receta-compact-thumb" onerror="this.src='${PLACEHOLDER_IMG}'">
            <div class="receta-compact-info">
                <div>
                    <h3 class="receta-compact-title">${escapeHtml(receta.nombre)}</h3>
                    ${badgeHtml}
                    ${strategyTagHtml}
                    <ul class="receta-checklist">
                        ${checklistHtml}
                    </ul>
                </div>
                <button type="button" class="${btnClass}" onclick="abrirModalCocinar(${receta.id})">
                    ${btnText}
                </button>
            </div>
        `;
        container.appendChild(card);
    });
}

function renderizarRecetasGridFull() {
    const fullBanner = document.getElementById('strategyFullBanner');
    if (fullBanner) {
        if (estrategiaActual === 'antidesperdicio') {
            fullBanner.className = 'strategy-full-banner banner-antidesp';
            fullBanner.innerHTML = `
                <div class="sfb-icon">${SVG_ICONS.leaf}</div>
                <div class="sfb-content">
                    <div class="sfb-title">Estrategia Activa: Anti-Desperdicio de Alimentos</div>
                    <div class="sfb-desc">Prioriza y clasifica en los primeros lugares las recetas que rescatan insumos que estan por vencer (≤ 3 dias) o que ya caducaron en tu despensa (ej. Leche entera o Tomates), para evitar que se echen a perder.</div>
                </div>
            `;
        } else {
            fullBanner.className = 'strategy-full-banner banner-disptotal';
            fullBanner.innerHTML = `
                <div class="sfb-icon">${SVG_ICONS.bolt}</div>
                <div class="sfb-content">
                    <div class="sfb-title">Estrategia Activa: Disponibilidad Total y Rapidez</div>
                    <div class="sfb-desc">Muestra en primer lugar las recetas donde cuentas con el 100% de los ingredientes en tu despensa, ordenadas de menor a mayor tiempo de preparacion (desde la mas rapida en 5 minutos hasta recetas mas elaboradas).</div>
                </div>
            `;
        }
    }

    const grid = document.getElementById('recetasGridExpanded');
    grid.innerHTML = '';

    listaRecetas.forEach((receta, idx) => {
        const card = document.createElement('div');
        card.className = 'receta-full-card';
        const imgUrl = IMAGENES_RECETAS[receta.id] || PLACEHOLDER_IMG;
        const suf = receta.tieneIngredientesSuficientes;

        let topBadgeHtml = '';
        if (suf) {
            topBadgeHtml = `<span class="floating-pill-left" style="background:#eafaf1; color:#27ae60;">Lista para cocinar</span>`;
        } else {
            topBadgeHtml = `<span class="floating-pill-left" style="background:#fdedec; color:#c0392b;">Faltan algunos insumos</span>`;
        }

        let strategyBadgeHtml = '';
        if (estrategiaActual === 'antidesperdicio') {
            if (receta.ingredientesRescatados && receta.ingredientesRescatados.length > 0) {
                strategyBadgeHtml = `<div class="rescata-banner active-rescate">Salva insumo por vencer: <strong>${escapeHtml(receta.ingredientesRescatados.join(', '))}</strong></div>`;
            } else {
                strategyBadgeHtml = `<div class="rescata-banner inactive-rescate">Insumos frescos o no perecederos</div>`;
            }
        } else {
            strategyBadgeHtml = `<div class="disp-banner">#${idx + 1} en rapidez · Listo en <strong>${receta.tiempoPreparacion} minutos</strong></div>`;
        }

        let checklistHtml = '';
        if (suf) {
            checklistHtml += `<li>${SVG_ICONS.checkSm} Tienes todos los ingredientes</li>`;
            checklistHtml += `<li>${SVG_ICONS.checkSm} Tiempo de coccion: ${receta.tiempoPreparacion} min</li>`;
        } else {
            (receta.ingredientesFaltantes || []).slice(0, 3).forEach(f => {
                checklistHtml += `<li>${SVG_ICONS.crossSm} ${escapeHtml(f)}</li>`;
            });
        }

        card.innerHTML = `
            <div class="receta-card-hero">
                <img src="${imgUrl}" alt="${escapeHtml(receta.nombre)}" onerror="this.src='${PLACEHOLDER_IMG}'">
                ${topBadgeHtml}
                <span class="floating-pill-right">${SVG_ICONS.clock} ${receta.tiempoPreparacion} min</span>
            </div>
            <div class="receta-card-body">
                <div>
                    <h3 class="receta-full-title">${escapeHtml(receta.nombre)}</h3>
                    ${strategyBadgeHtml}
                    <ul class="receta-checklist" style="margin-bottom:1rem;">
                        ${checklistHtml}
                    </ul>
                </div>
                <button type="button" class="btn-receta-action" style="width:100%;" onclick="abrirModalCocinar(${receta.id})">
                    Ver receta
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function actualizarSelectModalCocinar() {
    const select = document.getElementById('selectRecetaParaCocinar');
    select.innerHTML = '';
    listaRecetas.forEach(r => {
        const opt = document.createElement('option');
        opt.value = r.id;
        opt.textContent = `${r.nombre} (${r.tiempoPreparacion} min)`;
        select.appendChild(opt);
    });
}

function abrirModalCocinar(idReceta) {
    actualizarSelectModalCocinar();
    const select = document.getElementById('selectRecetaParaCocinar');
    if (idReceta) {
        select.value = idReceta;
    }
    document.getElementById('inputPorciones').value = 2;
    actualizarPreviewPorciones();
    document.getElementById('modalCocinarReceta').style.display = 'flex';
}

function cerrarModalCocinar() {
    document.getElementById('modalCocinarReceta').style.display = 'none';
}

function actualizarPreviewPorciones() {
    const select = document.getElementById('selectRecetaParaCocinar');
    const inputPorc = document.getElementById('inputPorciones');
    const box = document.getElementById('boxPreviewIngredientes');

    const id = parseInt(select.value, 10);
    const porciones = parseInt(inputPorc.value, 10) || 1;
    const receta = listaRecetas.find(r => r.id === id);

    if (!receta || !receta.ingredientes) {
        box.innerHTML = '';
        return;
    }

    let html = `<strong>Ingredientes a descontar para ${porciones} porción(es):</strong><ul style="margin-top:0.35rem; padding-left:1.2rem;">`;
    receta.ingredientes.forEach(ri => {
        const cant = (ri.cantidadNecesaria * porciones).toFixed(1);
        const nom = ri.ingrediente ? ri.ingrediente.nombre : 'Ingrediente';
        const unidad = ri.ingrediente ? ri.ingrediente.unidadMedida : '';
        html += `<li>${nom}: <strong>${cant} ${unidad}</strong></li>`;
    });
    html += `</ul>`;
    box.innerHTML = html;
}

async function ejecutarCocinarReceta() {
    const select = document.getElementById('selectRecetaParaCocinar');
    const inputPorc = document.getElementById('inputPorciones');

    const idReceta = parseInt(select.value, 10);
    const porciones = parseInt(inputPorc.value, 10) || 1;
    const receta = listaRecetas.find(r => r.id === idReceta);

    try {
        const resp = await fetch(API.recetas, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json;charset=UTF-8' },
            body: JSON.stringify({
                idReceta: idReceta,
                porciones: porciones
            })
        });

        const data = await resp.json();

        if (resp.ok && data.exito) {
            cerrarModalCocinar();

            mostrarToastExito('Estado guardado con exito', 'Se descontaron los lotes de despensa.');
            cargarDespensa();
            cargarRecetas();
        } else {

            mostrarToastError('No se pudo Guardar el estado', 'Probablemente no tengas ingredientes suficientes o hubo una falla tecnica');
        }

    } catch (err) {
        console.error('Error al cocinar receta:', err);
        mostrarToastError('No se pudo Guardar el estado', 'Probablemente no tengas ingredientes suficientes o hubo una falla tecnica');
    }
}

function mostrarToastExito(titulo, mensaje) {
    const box = document.getElementById('toastNotificationBox');
    box.innerHTML = `
        <div class="toast-fit-card toast-success-card">
            <div class="toast-icon-circle success-circle">${SVG_ICONS.check}</div>
            <div>
                <h4 class="toast-title success-text">${escapeHtml(titulo)}</h4>
                <p class="toast-msg">${escapeHtml(mensaje)}</p>
            </div>
        </div>
    `;
    box.style.display = 'block';

    setTimeout(() => {
        box.style.display = 'none';
    }, 4500);
}

function mostrarToastError(titulo, mensaje) {
    const box = document.getElementById('toastNotificationBox');
    box.innerHTML = `
        <div class="toast-fit-card toast-error-card">
            <div class="toast-icon-circle error-circle">${SVG_ICONS.crossSm}</div>
            <div>
                <h4 class="toast-title error-text">${escapeHtml(titulo)}</h4>
                <p class="toast-msg">${escapeHtml(mensaje)}</p>
            </div>
        </div>
    `;
    box.style.display = 'block';

    setTimeout(() => {
        box.style.display = 'none';
    }, 4500);
}

function escapeHtml(texto) {
    if (!texto) return '';
    const div = document.createElement('div');
    div.textContent = texto;
    return div.innerHTML;
}
