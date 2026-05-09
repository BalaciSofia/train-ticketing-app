const user = JSON.parse(sessionStorage.getItem('user') || 'null');
if (!user || user.role !== 'CLIENT') window.location.href = '/login.html';
document.getElementById('userName').textContent = user.username;

function logout() {
    sessionStorage.removeItem('user');
    window.location.href = '/login.html';
}

let cart = [];

async function loadStations() {
    const res = await fetch('/api/stations');
    const stations = await res.json();
    const fromSel = document.getElementById('fromStation');
    const toSel = document.getElementById('toStation');

    stations.forEach(s => {
        const opt1 = new Option(s.city, s.id);
        const opt2 = new Option(s.city, s.id);
        fromSel.appendChild(opt1);
        toSel.appendChild(opt2);
    });
}

async function search() {
    const fromId = document.getElementById('fromStation').value;
    const toId = document.getElementById('toStation').value;
    const date = document.getElementById('travelDate').value;
    const msg = document.getElementById('searchMsg');

    msg.style.display = 'none';

    if (!fromId || !toId) {
        showMsg(msg, 'Please select both a departure and arrival station.', 'error');
        return;
    }
    if (fromId === toId) {
        showMsg(msg, 'Departure and arrival station cannot be the same.', 'error');
        return;
    }

    const res = await fetch(`/api/search?fromStationId=${fromId}&toStationId=${toId}`);
    if (!res.ok) {
        showMsg(msg, 'Search failed. Please try again.', 'error');
        return;
    }
    const data = await res.json();

    let directs = data.directRoutes || [];
    let changeovers = data.changeoverRoutes || [];

    if (date) {
        directs = directs.filter(r => toDateStr(r.departureTime) === date);
        changeovers = changeovers.filter(r => toDateStr(r.firstLeg.departureTime) === date);
    }

    renderResults(directs, changeovers);
}

function toDateStr(isoStr) {
    if (!isoStr) return '';
    return isoStr.split('T')[0];
}

function renderResults(directs, changeovers) {
    const panel = document.getElementById('resultsPanel');
    const body = document.getElementById('resultsBody');
    body.innerHTML = '';

    if (directs.length === 0 && changeovers.length === 0) {
        body.innerHTML = '<div class="empty-state">No trips found for the selected route and date.</div>';
        panel.style.display = '';
        return;
    }

    if (directs.length > 0) {
        const label = document.createElement('div');
        label.className = 'results-section-label';
        label.textContent = `Direct routes (${directs.length})`;
        body.appendChild(label);

        const wrap = document.createElement('div');
        wrap.className = 'table-wrap';
        wrap.appendChild(buildDirectTable(directs));
        body.appendChild(wrap);
    }

    if (changeovers.length > 0) {
        const label = document.createElement('div');
        label.className = 'results-section-label';
        label.textContent = `Routes with one changeover (${changeovers.length})`;
        body.appendChild(label);

        const wrap = document.createElement('div');
        wrap.className = 'table-wrap';
        wrap.appendChild(buildChangeoverTable(changeovers));
        body.appendChild(wrap);
    }

    panel.style.display = '';
}

function buildDirectTable(routes) {
    const table = document.createElement('table');
    table.innerHTML = `
        <thead>
            <tr>
                <th>Train</th>
                <th>From</th>
                <th>Departure</th>
                <th>To</th>
                <th>Arrival</th>
                <th style="width:40px"></th>
            </tr>
        </thead>`;
    const tbody = document.createElement('tbody');

    routes.forEach(r => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${r.trainNumber}</td>
            <td>${r.fromCity}</td>
            <td>${fmtTime(r.departureTime)}</td>
            <td>${r.toCity}</td>
            <td>${fmtTime(r.arrivalTime)}</td>
            <td><button class="btn-add" title="Add to cart">+</button></td>`;
        tr.querySelector('.btn-add').addEventListener('click', () => {
            addToCart({
                label: '',
                trainNumber: r.trainNumber,
                fromCity: r.fromCity,
                departureTime: r.departureTime,
                toCity: r.toCity,
                arrivalTime: r.arrivalTime,
                departureScheduleStopId: r.departureScheduleStopId,
                arrivalScheduleStopId: r.arrivalScheduleStopId
            });
        });
        tbody.appendChild(tr);
    });

    table.appendChild(tbody);
    return table;
}

function buildChangeoverTable(routes) {
    const table = document.createElement('table');
    table.innerHTML = `
        <thead>
            <tr>
                <th>First leg</th>
                <th>Departure</th>
                <th>Changeover</th>
                <th>Second leg</th>
                <th>Arrival</th>
                <th style="width:40px"></th>
            </tr>
        </thead>`;
    const tbody = document.createElement('tbody');

    routes.forEach(r => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${r.firstLeg.trainNumber}<br><small>${r.firstLeg.fromCity} → ${r.firstLeg.toCity}</small></td>
            <td>${fmtTime(r.firstLeg.departureTime)}</td>
            <td><span class="changeover-via">via ${r.changeoverCity}</span></td>
            <td>${r.secondLeg.trainNumber}<br><small>${r.secondLeg.fromCity} → ${r.secondLeg.toCity}</small></td>
            <td>${fmtTime(r.secondLeg.arrivalTime)}</td>
            <td><button class="btn-add" title="Add both legs to cart">+</button></td>`;
        tr.querySelector('.btn-add').addEventListener('click', () => {
            addToCart({
                label: 'Leg 1',
                trainNumber: r.firstLeg.trainNumber,
                fromCity: r.firstLeg.fromCity,
                departureTime: r.firstLeg.departureTime,
                toCity: r.firstLeg.toCity,
                arrivalTime: r.firstLeg.arrivalTime,
                departureScheduleStopId: r.firstLeg.departureScheduleStopId,
                arrivalScheduleStopId: r.firstLeg.arrivalScheduleStopId
            });
            addToCart({
                label: 'Leg 2',
                trainNumber: r.secondLeg.trainNumber,
                fromCity: r.secondLeg.fromCity,
                departureTime: r.secondLeg.departureTime,
                toCity: r.secondLeg.toCity,
                arrivalTime: r.secondLeg.arrivalTime,
                departureScheduleStopId: r.secondLeg.departureScheduleStopId,
                arrivalScheduleStopId: r.secondLeg.arrivalScheduleStopId
            });
        });
        tbody.appendChild(tr);
    });

    table.appendChild(tbody);
    return table;
}

function addToCart(item) {
    cart.push(item);
    renderCart();
}

function removeFromCart(index) {
    cart.splice(index, 1);
    renderCart();
}

function clearCart() {
    cart = [];
    renderCart();
}

function renderCart() {
    const panel = document.getElementById('cartPanel');
    const tbody = document.querySelector('#cartTable tbody');
    const countEl = document.getElementById('cartCount');
    const bookMsg = document.getElementById('bookMsg');

    bookMsg.style.display = 'none';

    if (cart.length === 0) {
        panel.style.display = 'none';
        return;
    }

    panel.style.display = '';
    countEl.textContent = cart.length;
    tbody.innerHTML = '';

    cart.forEach((item, i) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${item.trainNumber}</td>
            <td>${item.fromCity}</td>
            <td>${fmtTime(item.departureTime)}</td>
            <td>${item.toCity}</td>
            <td>${fmtTime(item.arrivalTime)}</td>
            <td>${item.label ? `<span class="changeover-via">${item.label}</span>` : ''}</td>
            <td><button class="btn-remove" title="Remove">✕</button></td>`;
        tr.querySelector('.btn-remove').addEventListener('click', () => removeFromCart(i));
        tbody.appendChild(tr);
    });
}

async function bookAll() {
    const msg = document.getElementById('bookMsg');
    msg.style.display = 'none';

    const res = await fetch('/api/bookings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            userId: user.id,
            tickets: cart.map(item => ({
                departureScheduleStopId: item.departureScheduleStopId,
                arrivalScheduleStopId: item.arrivalScheduleStopId
            }))
        })
    });

    if (res.ok) {
        const count = cart.length;
        cart = [];
        renderCart();
        showMsg(msg, `${count} ticket(s) booked successfully! A confirmation email has been sent.`, 'success');
        document.getElementById('cartPanel').style.display = '';
        document.getElementById('bookMsg').style.display = 'block';
    } else {
        const text = await res.text();
        showMsg(msg, `Booking failed: ${text}`, 'error');
    }
}

function fmtTime(isoStr) {
    if (!isoStr) return '—';
    const d = new Date(isoStr);
    return d.toLocaleString('ro-RO', { dateStyle: 'short', timeStyle: 'short' });
}

function showMsg(el, text, type) {
    el.textContent = text;
    el.className = `msg msg-${type === 'error' ? 'error' : 'success'}`;
    el.style.display = 'block';
}

loadStations();
