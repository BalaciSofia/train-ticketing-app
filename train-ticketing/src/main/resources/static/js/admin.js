const user = JSON.parse(sessionStorage.getItem('user') || 'null');
if (!user || user.role !== 'ADMIN') window.location.href = '/login.html';

document.getElementById('adminName').textContent = user.username;

let allStations = [];
let allRouteStops = [];
let selectedRouteId = null;
let allSchedules = [];
let allScheduleStops = [];
let selectedScheduleId = null;

async function loadStations() {
    const res = await fetch('/api/stations');
    const stations = await res.json();
    allStations = stations;

    const tbody = document.querySelector('#stationsTable tbody');
    tbody.innerHTML = '';
    stations.forEach(s => {
        const tr = document.createElement('tr');
        tr.dataset.id = s.id;
        tr.innerHTML = `
            <td class="city-cell">${s.city}</td>
            <td style="width:100px">
                <button class="btn btn-sm btn-edit" onclick="editStation(this)">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteStation(${s.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);
    });

    populateStopSelect(document.getElementById('newStopStation'));
}

async function addStation() {
    const input = document.getElementById('newCity');
    const city = input.value.trim();
    if (!city) return;
    await fetch('/api/stations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ city })
    });
    input.value = '';
    loadStations();
}

async function deleteStation(id) {
    await fetch(`/api/stations/${id}`, { method: 'DELETE' });
    loadStations();
}

function editStation(btn) {
    const tr = btn.closest('tr');
    const id = tr.dataset.id;
    const cityCell = tr.querySelector('.city-cell');

    if (btn.textContent === 'Edit') {
        cityCell.innerHTML = `<input type="text" value="${cityCell.textContent}">`;
        btn.textContent = 'Save';
    } else {
        const newCity = cityCell.querySelector('input').value.trim();
        if (!newCity) return;
        fetch(`/api/stations/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, city: newCity })
        }).then(() => loadStations());
    }
}

async function loadTrains() {
    const res = await fetch('/api/trains');
    const trains = await res.json();
    const tbody = document.querySelector('#trainsTable tbody');
    tbody.innerHTML = '';

    const trainFilter = document.getElementById('trainFilter');
    trainFilter.innerHTML = '<option value="">All trains</option>';

    const scheduleTrain = document.getElementById('newScheduleTrain');
    scheduleTrain.innerHTML = '<option value="">Select train</option>';

    trains.forEach(t => {
        const tr = document.createElement('tr');
        tr.dataset.id = t.id;
        tr.innerHTML = `
            <td class="number-cell">${t.trainNumber}</td>
            <td class="seats-cell">${t.numberOfSeats}</td>
            <td style="width:100px">
                <button class="btn btn-sm btn-edit" onclick="editTrain(this)">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteTrain(${t.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);

        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = t.trainNumber;
        trainFilter.appendChild(opt);

        const opt2 = document.createElement('option');
        opt2.value = t.id;
        opt2.textContent = `${t.trainNumber} (${t.numberOfSeats} seats)`;
        scheduleTrain.appendChild(opt2);
    });
}

async function addTrain() {
    const number = document.getElementById('newTrainNumber').value.trim();
    const seats = document.getElementById('newSeats').value.trim();
    if (!number || !seats) return;
    await fetch('/api/trains', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trainNumber: number, numberOfSeats: parseInt(seats) })
    });
    document.getElementById('newTrainNumber').value = '';
    document.getElementById('newSeats').value = '';
    loadTrains();
}

async function deleteTrain(id) {
    await fetch(`/api/trains/${id}`, { method: 'DELETE' });
    loadTrains();
}

function editTrain(btn) {
    const tr = btn.closest('tr');
    const id = tr.dataset.id;
    const numberCell = tr.querySelector('.number-cell');
    const seatsCell = tr.querySelector('.seats-cell');

    if (btn.textContent === 'Edit') {
        numberCell.innerHTML = `<input type="text" value="${numberCell.textContent}">`;
        seatsCell.innerHTML = `<input type="number" value="${seatsCell.textContent}">`;
        btn.textContent = 'Save';
    } else {
        const newNumber = numberCell.querySelector('input').value.trim();
        const newSeats = seatsCell.querySelector('input').value.trim();
        if (!newNumber || !newSeats) return;
        fetch(`/api/trains/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, trainNumber: newNumber, numberOfSeats: parseInt(newSeats) })
        }).then(() => loadTrains());
    }
}

async function loadRoutes() {
    const [routesRes, stopsRes] = await Promise.all([
        fetch('/api/routes'),
        fetch('/api/route-stops')
    ]);
    const routes = await routesRes.json();
    allRouteStops = await stopsRes.json();

    const scheduleRoute = document.getElementById('newScheduleRoute');
    scheduleRoute.innerHTML = '<option value="">Select route</option>';
    routes.forEach(r => {
        const opt = document.createElement('option');
        opt.value = r.id;
        opt.textContent = r.name;
        scheduleRoute.appendChild(opt);
    });

    const tbody = document.querySelector('#routesTable tbody');
    tbody.innerHTML = '';
    routes.forEach(r => {
        const stops = stopsForRoute(r.id);
        const stopsStr = stops.map(s => s.station.city).join(' → ') || '—';

        const tr = document.createElement('tr');
        tr.dataset.id = r.id;
        tr.innerHTML = `
            <td class="route-name-cell">${r.name}</td>
            <td class="route-stops-str">${stopsStr}</td>
            <td style="width:180px">
                <button class="btn btn-sm btn-edit" onclick="openStopsSection(${r.id}, this)">Stops</button>
                <button class="btn btn-sm btn-edit" onclick="editRoute(this)">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteRoute(${r.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);
    });

    if (selectedRouteId !== null) {
        renderStopsList();
    }
}

function stopsForRoute(routeId) {
    return allRouteStops
        .filter(s => s.route.id === routeId)
        .sort((a, b) => a.stopNumber - b.stopNumber);
}

function openStopsSection(routeId, btn) {
    selectedRouteId = routeId;
    const routeName = btn.closest('tr').querySelector('.route-name-cell').textContent;
    document.getElementById('stopsTitle').textContent = routeName;
    renderStopsList();
    document.getElementById('stopsSection').style.display = '';
}

function closeStopsSection() {
    selectedRouteId = null;
    document.getElementById('stopsSection').style.display = 'none';
}

function renderStopsList() {
    const stops = stopsForRoute(selectedRouteId);
    const container = document.getElementById('stopsList');
    container.innerHTML = '';

    if (stops.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'stops-list-item';
        empty.style.color = '#a08060';
        empty.textContent = 'No stops yet.';
        container.appendChild(empty);
        return;
    }

    stops.forEach((s, i) => {
        const div = document.createElement('div');
        div.className = 'stops-list-item';
        div.innerHTML = `
            <span>${i + 1}. ${s.station.city}</span>
            <button class="btn btn-sm btn-danger" onclick="deleteRouteStop(${s.id})">✕</button>`;
        container.appendChild(div);
    });
}

async function deleteRouteStop(id) {
    await fetch(`/api/route-stops/${id}`, { method: 'DELETE' });
    await refreshRouteStops();
    renderStopsList();
    refreshRoutesTableStops();
}

async function addStopToRoute() {
    if (selectedRouteId === null) return;
    const stationId = document.getElementById('newStopStation').value;
    if (!stationId) return;

    const currentStops = stopsForRoute(selectedRouteId);
    const nextNumber = currentStops.length > 0
        ? Math.max(...currentStops.map(s => s.stopNumber)) + 1
        : 1;

    await fetch('/api/route-stops', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            route: { id: selectedRouteId },
            station: { id: parseInt(stationId) },
            stopNumber: nextNumber
        })
    });

    document.getElementById('newStopStation').value = '';
    await refreshRouteStops();
    renderStopsList();
    refreshRoutesTableStops();
}

async function refreshRouteStops() {
    const res = await fetch('/api/route-stops');
    allRouteStops = await res.json();
}

function refreshRoutesTableStops() {
    document.querySelectorAll('#routesTable tbody tr').forEach(tr => {
        const routeId = parseInt(tr.dataset.id);
        const stops = stopsForRoute(routeId);
        tr.querySelector('.route-stops-str').textContent =
            stops.map(s => s.station.city).join(' → ') || '—';
    });
}

function addStopRow() {
    const builder = document.getElementById('stopBuilder');
    const row = document.createElement('div');
    row.className = 'stop-builder-row';
    row.innerHTML = `
        <span class="stop-number"></span>
        <select><option value="">Select station</option></select>
        <button class="btn btn-sm btn-danger" onclick="removeStopRow(this)">✕</button>`;
    populateStopSelect(row.querySelector('select'));
    builder.appendChild(row);
    renumberStopRows();
}

function removeStopRow(btn) {
    const builder = document.getElementById('stopBuilder');
    if (builder.children.length <= 2) return;
    btn.closest('.stop-builder-row').remove();
    renumberStopRows();
}

function renumberStopRows() {
    document.querySelectorAll('#stopBuilder .stop-number').forEach((el, i) => {
        el.textContent = `${i + 1}.`;
    });
}

function populateStopSelect(sel) {
    const current = sel.value;
    sel.innerHTML = '<option value="">Select station</option>';
    allStations.forEach(s => {
        const opt = new Option(s.city, s.id);
        sel.appendChild(opt);
    });
    sel.value = current;
}

async function addRoute() {
    const name = document.getElementById('newRouteName').value.trim();
    const stationIds = [...document.querySelectorAll('#stopBuilder select')]
        .map(s => s.value)
        .filter(v => v);

    if (!name) {
        showRouteMsg('Enter a route name.', 'error');
        return;
    }
    if (stationIds.length < 2) {
        showRouteMsg('Select at least 2 stops.', 'error');
        return;
    }

    const routeRes = await fetch('/api/routes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    if (!routeRes.ok) {
        showRouteMsg('Failed to create route.', 'error');
        return;
    }
    const route = await routeRes.json();

    for (let i = 0; i < stationIds.length; i++) {
        await fetch('/api/route-stops', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                route: { id: route.id },
                station: { id: parseInt(stationIds[i]) },
                stopNumber: i + 1
            })
        });
    }

    document.getElementById('newRouteName').value = '';
    resetStopBuilder();
    showRouteMsg('Route added.', 'success');
    loadRoutes();
}

function resetStopBuilder() {
    document.getElementById('stopBuilder').innerHTML = '';
    addStopRow();
    addStopRow();
}

async function deleteRoute(id) {
    for (const stop of stopsForRoute(id)) {
        await fetch(`/api/route-stops/${stop.id}`, { method: 'DELETE' });
    }
    await fetch(`/api/routes/${id}`, { method: 'DELETE' });
    if (selectedRouteId === id) closeStopsSection();
    loadRoutes();
}

function editRoute(btn) {
    const tr = btn.closest('tr');
    const id = tr.dataset.id;
    const nameCell = tr.querySelector('.route-name-cell');

    if (btn.textContent === 'Edit') {
        nameCell.innerHTML = `<input type="text" value="${nameCell.textContent}">`;
        btn.textContent = 'Save';
    } else {
        const newName = nameCell.querySelector('input').value.trim();
        if (!newName) return;
        fetch(`/api/routes/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ id, name: newName })
        }).then(() => loadRoutes());
    }
}

function showRouteMsg(text, type) {
    const msg = document.getElementById('routeMsg');
    msg.textContent = text;
    msg.className = `msg msg-${type}`;
    msg.style.display = 'block';
    setTimeout(() => msg.style.display = 'none', 3000);
}

async function loadSchedulePanel() {
    const [schedulesRes, stopsRes] = await Promise.all([
        fetch('/api/schedules'),
        fetch('/api/schedule-stops')
    ]);
    allSchedules = await schedulesRes.json();
    allScheduleStops = await stopsRes.json();
    renderSchedulesTable();
}

function scheduleStopsFor(scheduleId) {
    return allScheduleStops
        .filter(ss => ss.schedule.id === scheduleId)
        .sort((a, b) => a.routeStop.stopNumber - b.routeStop.stopNumber);
}

function renderSchedulesTable() {
    const tbody = document.querySelector('#schedulesTable tbody');
    tbody.innerHTML = '';
    allSchedules.forEach(s => {
        const stops = scheduleStopsFor(s.id);
        const stopsStr = stops.map(ss => ss.routeStop.station.city).join(' → ') || '—';
        const tr = document.createElement('tr');
        tr.dataset.id = s.id;
        tr.innerHTML = `
            <td>${s.train.trainNumber}</td>
            <td>${s.route.name}</td>
            <td class="schedule-stops-str">${stopsStr}</td>
            <td style="width:120px">
                <button class="btn btn-sm btn-edit" onclick="openScheduleStopsSection(${s.id}, this)">Stops</button>
                <button class="btn btn-sm btn-danger" onclick="deleteSchedule(${s.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);
    });

    if (selectedScheduleId !== null) renderScheduleStopsList();
}

function openScheduleStopsSection(scheduleId, btn) {
    selectedScheduleId = scheduleId;
    const s = allSchedules.find(s => s.id === scheduleId);
    document.getElementById('scheduleStopsTitle').textContent = `${s.train.trainNumber} — ${s.route.name}`;
    renderScheduleStopsList();
    document.getElementById('scheduleStopsSection').style.display = '';
}

function closeScheduleStopsSection() {
    selectedScheduleId = null;
    document.getElementById('scheduleStopsSection').style.display = 'none';
}

function renderScheduleStopsList() {
    const stops = scheduleStopsFor(selectedScheduleId);
    const container = document.getElementById('scheduleStopsList');
    container.innerHTML = '';

    if (stops.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'stops-list-item';
        empty.style.color = '#a08060';
        empty.textContent = 'No stops yet.';
        container.appendChild(empty);
        return;
    }

    stops.forEach((ss, i) => {
        const div = document.createElement('div');
        div.className = 'stops-list-item';
        div.dataset.id = ss.id;
        div.innerHTML = `
            <span style="flex:0 0 160px;font-size:0.82rem">${i + 1}. ${ss.routeStop.station.city}</span>
            <span class="time-cell ss-arr">arr: ${toDatetimeDisplay(ss.arrivalTime)}</span>
            <span class="time-cell ss-dep">dep: ${toDatetimeDisplay(ss.departureTime)}</span>
            <button class="btn btn-sm btn-edit" onclick="editScheduleStop(this, ${ss.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deleteScheduleStop(${ss.id})">✕</button>`;
        container.appendChild(div);
    });
}

function toDatetimeDisplay(isoStr) {
    if (!isoStr) return '—';
    return isoStr.substring(0, 16).replace('T', ' ');
}

function toDatetimeLocal(isoStr) {
    if (!isoStr) return '';
    return isoStr.substring(0, 16);
}

function toIso(dtLocal) {
    if (!dtLocal) return '';
    return dtLocal.length === 16 ? dtLocal + ':00' : dtLocal;
}

function editScheduleStop(btn, id) {
    const div = btn.closest('.stops-list-item');
    const ss = allScheduleStops.find(s => s.id === id);

    if (btn.textContent === 'Edit') {
        div.querySelector('.ss-arr').innerHTML = `arr: <input type="datetime-local" value="${toDatetimeLocal(ss.arrivalTime)}">`;
        div.querySelector('.ss-dep').innerHTML = `dep: <input type="datetime-local" value="${toDatetimeLocal(ss.departureTime)}">`;
        btn.textContent = 'Save';
    } else {
        const arrVal = toIso(div.querySelector('.ss-arr input').value);
        const depVal = toIso(div.querySelector('.ss-dep input').value);
        if (!arrVal || !depVal) return;
        fetch(`/api/schedule-stops/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                id,
                schedule: { id: ss.schedule.id },
                routeStop: { id: ss.routeStop.id },
                arrivalTime: arrVal,
                departureTime: depVal
            })
        }).then(async () => {
            await refreshScheduleStops();
            renderScheduleStopsList();
            refreshSchedulesTableStops();
        });
    }
}

async function deleteScheduleStop(id) {
    await fetch(`/api/schedule-stops/${id}`, { method: 'DELETE' });
    await refreshScheduleStops();
    renderScheduleStopsList();
    refreshSchedulesTableStops();
}

async function refreshScheduleStops() {
    const res = await fetch('/api/schedule-stops');
    allScheduleStops = await res.json();
}

function refreshSchedulesTableStops() {
    document.querySelectorAll('#schedulesTable tbody tr').forEach(tr => {
        const scheduleId = parseInt(tr.dataset.id);
        const stops = scheduleStopsFor(scheduleId);
        tr.querySelector('.schedule-stops-str').textContent =
            stops.map(ss => ss.routeStop.station.city).join(' → ') || '—';
    });
}

function buildScheduleStopBuilder() {
    const routeId = parseInt(document.getElementById('newScheduleRoute').value);
    const builder = document.getElementById('scheduleStopBuilder');
    builder.innerHTML = '';
    if (!routeId) return;

    const routeStops = allRouteStops
        .filter(rs => rs.route.id === routeId)
        .sort((a, b) => a.stopNumber - b.stopNumber);

    routeStops.forEach((rs, i) => {
        const row = document.createElement('div');
        row.className = 'stop-builder-row';
        row.dataset.routeStopId = rs.id;
        row.innerHTML = `
            <span class="stop-number">${i + 1}.</span>
            <span style="flex:0 0 150px;font-size:0.82rem">${rs.station.city}</span>
            <label style="font-size:0.75rem;color:#7a4f2d;white-space:nowrap">Arrival</label>
            <input type="datetime-local" style="flex:1">
            <label style="font-size:0.75rem;color:#7a4f2d;white-space:nowrap">Departure</label>
            <input type="datetime-local" style="flex:1">`;
        builder.appendChild(row);
    });
}

async function addSchedule() {
    const trainId = document.getElementById('newScheduleTrain').value;
    const routeId = document.getElementById('newScheduleRoute').value;
    const rows = [...document.querySelectorAll('#scheduleStopBuilder .stop-builder-row')];

    if (!trainId || !routeId) {
        showScheduleMsg('Select a train and a route.', 'error');
        return;
    }
    if (rows.length === 0) {
        showScheduleMsg('The selected route has no stops defined.', 'error');
        return;
    }

    const stopData = rows.map(row => ({
        routeStopId: parseInt(row.dataset.routeStopId),
        arrivalTime: toIso(row.querySelectorAll('input')[0].value),
        departureTime: toIso(row.querySelectorAll('input')[1].value)
    }));

    if (stopData.some(s => !s.arrivalTime || !s.departureTime)) {
        showScheduleMsg('Fill in all arrival and departure times.', 'error');
        return;
    }

    const scheduleRes = await fetch('/api/schedules', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ train: { id: parseInt(trainId) }, route: { id: parseInt(routeId) } })
    });
    if (!scheduleRes.ok) {
        showScheduleMsg('Failed to create schedule.', 'error');
        return;
    }
    const schedule = await scheduleRes.json();

    for (const s of stopData) {
        await fetch('/api/schedule-stops', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                schedule: { id: schedule.id },
                routeStop: { id: s.routeStopId },
                arrivalTime: s.arrivalTime,
                departureTime: s.departureTime
            })
        });
    }

    document.getElementById('newScheduleTrain').value = '';
    document.getElementById('newScheduleRoute').value = '';
    document.getElementById('scheduleStopBuilder').innerHTML = '';
    showScheduleMsg('Schedule added.', 'success');
    loadSchedulePanel();
}

async function deleteSchedule(id) {
    for (const ss of scheduleStopsFor(id)) {
        await fetch(`/api/schedule-stops/${ss.id}`, { method: 'DELETE' });
    }
    await fetch(`/api/schedules/${id}`, { method: 'DELETE' });
    if (selectedScheduleId === id) closeScheduleStopsSection();
    loadSchedulePanel();
}

function showScheduleMsg(text, type) {
    const msg = document.getElementById('scheduleMsg');
    msg.textContent = text;
    msg.className = `msg msg-${type}`;
    msg.style.display = 'block';
    setTimeout(() => msg.style.display = 'none', 3000);
}

async function loadBookings() {
    const trainId = document.getElementById('trainFilter').value;
    const url = trainId ? `/api/tickets?trainId=${trainId}` : '/api/tickets';
    const res = await fetch(url);
    const tickets = await res.json();
    const tbody = document.querySelector('#bookingsTable tbody');
    tbody.innerHTML = '';
    tickets.forEach(t => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${t.booking.user.username}</td>
            <td>${t.departureScheduleStop.routeStop.station.city}</td>
            <td>${t.arrivalScheduleStop.routeStop.station.city}</td>
            <td>${formatTime(t.departureScheduleStop.departureTime)}</td>
            <td>${t.departureScheduleStop.schedule.train.trainNumber}</td>`;
        tbody.appendChild(tr);
    });
}

async function loadSchedules() {
    const res = await fetch('/api/schedules');
    const schedules = await res.json();
    const select = document.getElementById('delaySchedule');
    select.innerHTML = '<option value="">Select schedule</option>';
    schedules.forEach(s => {
        const opt = document.createElement('option');
        opt.value = s.id;
        opt.textContent = `${s.train.trainNumber} — ${s.route.name}`;
        select.appendChild(opt);
    });
}

async function loadStopsForSchedule() {
    const scheduleId = document.getElementById('delaySchedule').value;
    const stopSelect = document.getElementById('delayFromStop');
    stopSelect.innerHTML = '<option value="">Select delay start stop</option>';
    stopSelect.disabled = true;

    if (!scheduleId) return;

    const res = await fetch(`/api/schedule-stops?scheduleId=${scheduleId}`);
    if (!res.ok) return;
    const stops = await res.json();

    stops.sort((a, b) => a.routeStop.stopNumber - b.routeStop.stopNumber);
    stops.forEach(ss => {
        const opt = document.createElement('option');
        opt.value = ss.id;
        opt.textContent = `${ss.routeStop.station.city} (stop ${ss.routeStop.stopNumber}) — dep. ${formatTime(ss.departureTime)}`;
        stopSelect.appendChild(opt);
    });
    stopSelect.disabled = false;
}

async function loadDelays() {
    const res = await fetch('/api/delays');
    const delays = await res.json();
    const tbody = document.querySelector('#delaysTable tbody');
    tbody.innerHTML = '';
    delays.forEach(d => {
        const fromCity = d.fromScheduleStop?.routeStop?.station?.city ?? '—';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${d.schedule.train.trainNumber}</td>
            <td>${d.schedule.route.name}</td>
            <td>${fromCity}</td>
            <td>${d.delayMinutes} min</td>`;
        tbody.appendChild(tr);
    });
}

async function reportDelay() {
    const scheduleId = document.getElementById('delaySchedule').value;
    const fromScheduleStopId = document.getElementById('delayFromStop').value;
    const minutes = document.getElementById('delayMinutes').value;
    const msg = document.getElementById('delayMsg');

    if (!scheduleId || !fromScheduleStopId || !minutes) {
        msg.textContent = 'Please select a schedule, a delay start stop, and enter the minutes.';
        msg.className = 'msg msg-error';
        msg.style.display = 'block';
        return;
    }

    const res = await fetch('/api/delays', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            scheduleId: parseInt(scheduleId),
            fromScheduleStopId: parseInt(fromScheduleStopId),
            delayMinutes: parseInt(minutes)
        })
    });

    if (res.ok) {
        document.getElementById('delayMinutes').value = '';
        document.getElementById('delaySchedule').value = '';
        document.getElementById('delayFromStop').innerHTML = '<option value="">Select delay start stop</option>';
        document.getElementById('delayFromStop').disabled = true;
        msg.textContent = 'Delay reported. Only affected passengers were notified.';
        msg.className = 'msg msg-success';
        msg.style.display = 'block';
        setTimeout(() => msg.style.display = 'none', 3000);
        loadDelays();
    } else {
        msg.textContent = 'Failed to report delay.';
        msg.className = 'msg msg-error';
        msg.style.display = 'block';
    }
}

function formatTime(dt) {
    if (!dt) return '';
    return new Date(dt).toLocaleString('ro-RO', { dateStyle: 'short', timeStyle: 'short' });
}

loadStations().then(() => { addStopRow(); addStopRow(); });
loadTrains().then(() => loadBookings());
loadRoutes();
loadSchedulePanel();
loadSchedules();
loadDelays();
