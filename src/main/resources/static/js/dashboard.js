/* ==============================
   VESPA Fleet OS — Dashboard JS
   ============================== */

(function () {
  'use strict';

  const WS_URL = window.location.origin + '/ws';
  const AUTH = { login: 'admin', passcode: 'admin123' };
  const MAP_CENTER = [8.5241, 76.9366];
  const MAP_ZOOM = 16;

  // Fixed warehouse positions for 6 robots (arranged around the warehouse)
  const POSITIONS = {
    'BOT-01': [8.5250, 76.9375],
    'BOT-02': [8.5238, 76.9360],
    'BOT-03': [8.5248, 76.9355],
    'BOT-04': [8.5235, 76.9378],
    'BOT-05': [8.5255, 76.9368],
    'BOT-06': [8.5240, 76.9352],
  };

  let robots = {}, reconnectTimer;
  let map, markers = {};

  const $ = (id) => document.getElementById(id);
  const wsDot = $('wsDot');
  const wsStatus = $('wsStatus');
  const clock = $('clock');

  // ---- Clock ----
  function updateClock() {
    const now = new Date();
    const d = now.toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });
    const t = now.toLocaleTimeString('en-IN', { hour12: false });
    clock.innerHTML = `<span class="time">${t}</span> ${d}`;
  }
  setInterval(updateClock, 1000);
  updateClock();

  // ---- WS Status ----
  function setWsStatus(status) {
    wsDot.className = 'status-dot ' + status;
    wsStatus.textContent = status;
  }
  setWsStatus('connecting');

  // ---- Helpers ----
  const STATUS_COLORS = { MOVING: '#10b981', CHARGING: '#f59e0b', ERROR: '#ef4444', OBSTACLE: '#ef4444', IDLE: '#6b7280' };
  function statusColor(s) { return STATUS_COLORS[s] || '#6b7280'; }
  function statusLetter(s) { return { MOVING: 'M', CHARGING: 'C', ERROR: '!', OBSTACLE: '!' }[s] || 'I'; }

  // ---- Map ----
  function initMap() {
    map = L.map('fleetMap', { zoomControl: true }).setView(MAP_CENTER, MAP_ZOOM);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap',
    }).addTo(map);

    // Warehouse marker at center
    L.circleMarker(MAP_CENTER, {
      radius: 6, color: '#f59e0b', fillColor: '#f59e0b', fillOpacity: 0.6,
    }).addTo(map).bindTooltip('WAREHOUSE', { permanent: true, direction: 'top' });

    // Legend
    const legend = L.control({ position: 'bottomright' });
    legend.onAdd = () => {
      const div = L.DomUtil.create('div', '');
      div.style.cssText = 'background:rgba(17,24,39,0.92);padding:10px 14px;border-radius:8px;border:1px solid #1e2a45;font-size:11px;color:#94a3b8;line-height:1.8';
      div.innerHTML = `
        <div style="font-weight:600;color:#e2e8f0;margin-bottom:4px">Legend</div>
        <div><span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#ef4444;border:2px dashed #fff;margin-right:6px"></span>BOT-01 (REAL)</div>
        <div><span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#3b82f6;border:2px solid #fff;margin-right:6px"></span>BOT-02 to BOT-06 (SIM)</div>
        <div><span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#f59e0b;margin-right:6px"></span>Warehouse</div>
      `;
      return div;
    };
    legend.addTo(map);

    // Initial markers
    Object.keys(POSITIONS).forEach(id => {
      const r = robots[id];
      addOrUpdateMarker(id, r);
    });
  }

  function addOrUpdateMarker(id, r) {
    if (!r) return;
    const pos = POSITIONS[id] || MAP_CENTER;
    const isReal = r.isReal;
    const color = isReal ? '#ef4444' : '#3b82f6';
    const fillColor = statusColor(r.status);
    const radius = isReal ? 14 : 10;

    if (markers[id]) {
      markers[id].setStyle({ color, fillColor, dashArray: isReal ? '5 4' : undefined });
      markers[id].setRadius(radius);
    } else {
      markers[id] = L.circleMarker(pos, {
        radius, color, fillColor, fillOpacity: 0.85,
        weight: isReal ? 3 : 2.5, dashArray: isReal ? '5 4' : undefined,
      }).addTo(map);
    }

    markers[id].unbindTooltip().bindTooltip(statusLetter(r.status), {
      permanent: true, direction: 'center',
      className: 'marker-label',
    });

    const tasks = r.tasksCompleted || 0;
    const dist = r.ultrasonicDistance != null ? r.ultrasonicDistance.toFixed(1) : '--';
    const speed = r.speed != null ? r.speed.toFixed(1) : '0';

    markers[id].bindPopup(`
      <div style="font-family:Inter,sans-serif;min-width:160px">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
          <strong style="font-size:14px">${id}</strong>
          <span style="font-size:10px;padding:1px 6px;border-radius:3px;${isReal ? 'background:rgba(239,68,68,0.15);color:#ef4444' : 'background:rgba(59,130,246,0.15);color:#3b82f6'}">${isReal ? 'REAL' : 'SIM'}</span>
        </div>
        <div style="font-size:12px;line-height:1.8">
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Name</span>${r.name || ''}</div>
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Status</span><span style="color:${fillColor}">${r.status || 'IDLE'}</span></div>
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Battery</span>${r.battery != null ? r.battery + '%' : '--'}</div>
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Speed</span>${speed} m/s</div>
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Distance</span>${dist} cm</div>
          <div style="display:flex;justify-content:space-between"><span style="color:#64748b">Tasks</span>${tasks}</div>
        </div>
      </div>
    `);
  }

  // ---- Render Robot Cards ----
  function renderRobotCards() {
    const grid = document.getElementById('fleetGrid');
    if (!grid) return;
    grid.innerHTML = '';
    Object.values(robots).sort((a, b) => a.id.localeCompare(b.id)).forEach(r => {
      const card = document.createElement('div');
      card.className = 'robot-card';
      card.dataset.id = r.id;
      const isReal = r.isReal;
      card.innerHTML = `
        <div class="rc-header">
          <div><div class="rc-id">${r.id}</div><div class="rc-name">${r.name || ''}</div></div>
          <span class="robot-badge ${isReal ? 'badge-red' : 'badge-blue'}">${isReal ? 'REAL' : 'SIM'}</span>
        </div>
        <div class="rc-body">
          <div class="rc-row"><span class="rc-label">Status</span><span class="rc-status" style="color:${statusColor(r.status)}">${r.status || 'IDLE'}</span></div>
          <div class="rc-row"><span class="rc-label">Battery</span><span class="rc-value">${r.battery != null ? r.battery + '%' : '--'}</span></div>
          <div class="rc-row"><span class="rc-label">Speed</span><span class="rc-value">${r.speed != null ? r.speed.toFixed(1) + ' m/s' : '0 m/s'}</span></div>
          <div class="rc-row"><span class="rc-label">Distance</span><span class="rc-value">${r.ultrasonicDistance != null ? r.ultrasonicDistance.toFixed(1) + ' cm' : '--'}</span></div>
          ${r.rfidTag ? `<div class="rc-row"><span class="rc-label">RFID</span><span class="rc-value rc-mono">${r.rfidTag}</span></div>` : ''}
          ${r.currentDestination ? `<div class="rc-row"><span class="rc-label">Dest</span><span class="rc-value">${r.currentDestination}</span></div>` : ''}
          <div class="rc-row"><span class="rc-label">Tasks</span><span class="rc-value">${r.tasksCompleted || 0}</span></div>
          <div class="rc-row"><span class="rc-label">Online</span><span class="rc-value" style="color:${r.online !== false ? '#10b981' : '#ef4444'}">${r.online !== false ? 'YES' : 'NO'}</span></div>
        </div>
      `;
      grid.appendChild(card);
    });
  }

  // ---- Update Summary Stats ----
  function updateStats() {
    const list = Object.values(robots);
    const total = list.length;
    const active = list.filter(r => r.status === 'MOVING').length;
    const avgBattery = list.length ? Math.round(list.reduce((s, r) => s + r.battery, 0) / list.length) : 0;
    const tasks = list.reduce((s, r) => s + (r.tasksCompleted || 0), 0);
    const avgUptime = list.length ? Math.round(list.reduce((s, r) => s + (r.uptimeSeconds || 0), 0) / list.length / 3600) : 0;
    $('statTotal').textContent = total;
    $('statActive').textContent = active;
    $('statBattery').textContent = avgBattery;
    $('statTasks').textContent = tasks;
    $('statAlerts').textContent = document.querySelectorAll('.notif-item.unread').length;
    $('statUptime').textContent = avgUptime;
  }

  // ---- Update Sensor Strip (random fallback) ----
  function randomSensor() {
    const data = {
      temperature: 26 + Math.random() * 8,
      humidity: 55 + Math.random() * 25,
      distance: 20 + Math.random() * 180,
      gasLevel: 10 + Math.random() * 90,
      vibration: 0.2 + Math.random() * 3,
      lightIntensity: 300 + Math.random() * 700,
    };
    const map = { temperature: 'senTemp', humidity: 'senHumidity', distance: 'senDistance', gasLevel: 'senGas', vibration: 'senVibration', lightIntensity: 'senLight' };
    Object.entries(map).forEach(([key, id]) => {
      const el = $(id);
      if (el) el.textContent = data[key].toFixed(1);
    });
  }
  setInterval(randomSensor, 3000);

  // ---- Update Insights ----
  function updateInsights() {
    const list = Object.values(robots);
    const low = list.filter(r => r.battery < 20).length;
    $('insightBattery').textContent = low > 0 ? `${low} robot(s) critical` : 'All nominal';
    const avg = list.length ? list.reduce((s, r) => s + (r.tasksCompleted || 0), 0) / list.length : 0;
    $('insightEfficiency').textContent = `${Math.min(100, Math.round(avg * 5 + 70))}%`;
    const due = list.filter(r => (r.totalDistance || 0) > 5000).length;
    $('insightMaintenance').textContent = due > 0 ? `${due} robot(s)` : 'None pending';
  }

  // ---- Add Notification ----
  function addNotification(n) {
    const list = $('notifList');
    const empty = list.querySelector('.tl-empty');
    if (empty) empty.remove();
    const el = document.createElement('div');
    el.className = 'notif-item unread';
    el.innerHTML = `<div class="notif-top"><span class="notif-type" data-type="${n.type || 'INFO'}">${n.type || 'INFO'}</span><span class="notif-new">NEW</span></div><div class="notif-msg">${n.message}</div><div class="notif-time">just now</div>`;
    list.prepend(el);
    if (list.children.length > 50) list.removeChild(list.lastChild);
    updateStats();
  }

  // ---- Add Timeline Event ----
  function addTimelineEvent(type, robotId, message) {
    const tl = document.getElementById('timeline');
    const empty = tl.querySelector('.tl-empty');
    if (empty) empty.remove();
    const dotClass = type === 'MOVING' ? 'moving' : type === 'BATTERY_LOW' || type === 'OBSTACLE' ? 'alert' : 'scan';
    const el = document.createElement('div');
    el.className = 'tl-item';
    el.innerHTML = `<span class="tl-dot ${dotClass}"></span><div class="tl-content"><div class="tl-robot">${robotId || 'System'}</div><div class="tl-action">${message}</div></div><span class="tl-time">now</span>`;
    tl.prepend(el);
    if (tl.children.length > 20) tl.removeChild(tl.lastChild);
  }

  // ---- STOMP WebSocket ----
  function connectWS() {
    try {
      const socket = new SockJS(WS_URL);
      const client = Stomp.over(socket);
      client.connect(AUTH.login, AUTH.passcode, () => {
        setWsStatus('connected');
        client.subscribe('/topic/robots', (msg) => {
          try {
            const r = JSON.parse(msg.body);
            robots[r.id] = r;
            addOrUpdateMarker(r.id, r);
            renderRobotCards();
            updateStats();
            updateInsights();
            addTimelineEvent(r.status, r.id, r.status === 'MOVING' ? `Moving` : `${r.status}`);
          } catch (e) {}
        });
        client.subscribe('/topic/zones', (msg) => {
          try {
            const z = JSON.parse(msg.body);
            if (z.isReal) updateSensors(z);
          } catch (e) {}
        });
        client.subscribe('/topic/notifications', (msg) => {
          try { addNotification(JSON.parse(msg.body)); } catch (e) {}
        });
        client.subscribe('/topic/rfid', (msg) => {
          try {
            const r = JSON.parse(msg.body);
            addTimelineEvent('RFID_SCAN', r.robotId, `RFID scanned: ${r.rfidTag}`);
          } catch (e) {}
        });
      }, () => {
        setWsStatus('disconnected');
        reconnectTimer = setTimeout(connectWS, 3000);
      });
    } catch (e) {
      setWsStatus('disconnected');
      reconnectTimer = setTimeout(connectWS, 3000);
    }
  }

  // ---- Init ----
  document.addEventListener('DOMContentLoaded', () => {
    // Capture initial robots from server-rendered cards
    document.querySelectorAll('.robot-card').forEach(card => {
      const id = card.dataset.id;
      if (!id) return;
      const rows = card.querySelectorAll('.rc-row');
      const statusEl = card.querySelector('.rc-status');
      const nameEl = card.querySelector('.rc-name');
      const badgeEl = card.querySelector('.robot-badge');
      const getVal = (idx) => {
        const el = rows[idx]?.querySelector('.rc-value');
        return el ? el.textContent.trim() : '';
      };
      robots[id] = {
        id,
        name: nameEl ? nameEl.textContent : id,
        status: statusEl ? statusEl.textContent : 'IDLE',
        battery: parseInt(getVal(1)) || 0,
        speed: parseFloat(getVal(2)) || 0,
        ultrasonicDistance: parseFloat(getVal(3)) || 0,
        tasksCompleted: parseInt(getVal(6)) || 0,
        isReal: badgeEl ? badgeEl.textContent === 'REAL' : false,
        online: true,
      };
    });
    initMap();
    updateStats();
    updateInsights();
    connectWS();
  });

})();
