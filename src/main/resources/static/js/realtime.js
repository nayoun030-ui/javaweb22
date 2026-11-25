// [12주차] 실시간 : 연결 · 구독 · 화면 고치기
// 강의 제공 코드(stomp.umd.min.js · realtime.js)가 없어 STOMP 1.2 를 직접 짧게 구현했다.
// STOMP 프레임 = "명령\n헤더:값\n...\n\n본문\0"  (CONNECT → CONNECTED → SUBSCRIBE → MESSAGE)
(function () {
    const ctx = (window.RT_CTX || '/').replace(/\/$/, '');
    const $status = document.getElementById('rt-status');
    const $toasts = document.getElementById('rt-toasts');
    const $count = document.getElementById('rt-bell-count');
    const $banner = document.getElementById('rt-banner');
    let unread = 0, retry = 0, everConnected = false;

    // [화면 상태 ②] on : 연결됨 / retry : 다시 연결 중 (n초 뒤) / off : 연결 안 됨 (여러 번 실패)
    function setStatus(state, wait) {
        $status.classList.remove('on', 'retry', 'off');
        $status.classList.add(state);
        $status.textContent = state === 'on' ? '● 실시간 연결됨'
            : state === 'retry' ? '◌ 다시 연결 중… (' + Math.round(wait / 1000) + '초 뒤)'
            : '○ 실시간 연결 안 됨';
        const stale = state !== 'on' && everConnected; // 한 번이라도 받은 숫자가 있을 때만 "오래된 값" 표시
        if ($banner) $banner.hidden = !stale;
        document.body.classList.toggle('rt-offline', stale); // 안내 띠가 보이면 상태 표시를 그 아래로
        document.querySelectorAll('[data-seat-post]').forEach(el => el.classList.toggle('rt-stale', stale));
    }

    function frame(command, headers, body) {
        let s = command + '\n';
        for (const k in headers) s += k + ':' + headers[k] + '\n';
        return s + '\n' + (body || '') + '\0';
    }

    function parse(raw) { // 한 덩어리에 여러 프레임이 올 수 있다 → \0 으로 나눈다
        return raw.split('\0').map(f => f.replace(/^\n+/, '')).filter(Boolean).map(f => {
            const i = f.indexOf('\n\n');
            const head = (i < 0 ? f : f.slice(0, i)).split('\n');
            const headers = {};
            head.slice(1).forEach(line => { const c = line.indexOf(':'); if (c > 0) headers[line.slice(0, c)] = line.slice(c + 1); });
            return { command: head[0], headers, body: i < 0 ? '' : f.slice(i + 2) };
        });
    }

    function connect() {
        const proto = location.protocol === 'https:' ? 'wss:' : 'ws:';
        const ws = new WebSocket(proto + '//' + location.host + ctx + '/ws', ['v12.stomp']); // 단계 1 : /ws 로 101 Switching Protocols
        ws.onopen = () => ws.send(frame('CONNECT', { 'accept-version': '1.2', host: location.host, 'heart-beat': '0,0' }));
        ws.onmessage = (e) => {
            for (const f of parse(e.data)) {
                if (f.command === 'CONNECTED') {
                    retry = 0; everConnected = true; setStatus('on');
                    ws.send(frame('SUBSCRIBE', { id: 'seats', destination: '/topic/seats' }));                 // 모두에게 : 남은 자리
                    if (window.RT_LOGIN) ws.send(frame('SUBSCRIBE', { id: 'me', destination: '/user/queue/notifications' })); // 나에게만
                } else if (f.command === 'MESSAGE') {
                    const data = JSON.parse(f.body);
                    if (f.headers.destination === '/topic/seats') onSeats(data); else onNotification(data);
                } else if (f.command === 'ERROR') {
                    console.warn('[실시간] 오류', f.headers.message || f.body);
                }
            }
        };
        ws.onclose = () => { // 끊기면 1 · 2 · 4 · 8 · 10초 간격으로 다시 연결, 3번 넘게 실패하면 "연결 안 됨"
            const wait = Math.min(10000, 1000 * 2 ** retry++);
            setStatus(retry > 3 ? 'off' : 'retry', wait);
            setTimeout(connect, wait);
        };
    }

    function flash(el) { el.classList.remove('rt-flash'); void el.offsetWidth; el.classList.add('rt-flash'); }

    // 단계 2 · 3 : 남은 자리 {postId, appliedCount, capacity, remaining}
    function onSeats(m) {
        console.log('[실시간] 남은 자리', JSON.stringify(m));
        const full = m.remaining <= 0;
        document.querySelectorAll('[data-seat-post="' + m.postId + '"]').forEach(box => {
            const pick = (k) => box.matches('[data-seat="' + k + '"]') ? [box] : box.querySelectorAll('[data-seat="' + k + '"]');
            pick('applied').forEach(el => { el.textContent = m.appliedCount; flash(el); });
            pick('remaining').forEach(el => { el.textContent = m.remaining; el.classList.toggle('text-danger', full); flash(el); });
            pick('remaining-cart').forEach(el => { el.textContent = full ? '마감' : m.remaining; el.classList.toggle('full', full); flash(el); });
            pick('badge').forEach(el => { el.textContent = full ? '마감' : '모집 ' + m.appliedCount + '/' + m.capacity; el.classList.toggle('full', full); flash(el); });
            pick('bar').forEach(el => { const pct = m.capacity ? Math.floor(m.appliedCount * 100 / m.capacity) : 0; el.style.width = pct + '%'; el.textContent = pct + '%'; el.classList.toggle('bg-danger', full); });
            pick('fullmsg').forEach(el => { el.hidden = !full; });
            pick('form').forEach(el => { el.hidden = full; });
        });
    }

    // 단계 4 : 개인 알림 {to, text, postId}
    function onNotification(n) {
        console.log('[실시간] 알림', JSON.stringify(n));
        unread++;
        if ($count) { $count.hidden = false; $count.textContent = unread; }
        const t = document.createElement('a');
        t.className = 'rt-toast';
        t.href = ctx + '/board/' + n.postId; // 알림 클릭 → 그 모집글 상세
        const icon = document.createElement('span'); icon.className = 'rt-toast-ico'; icon.innerHTML = '<i class="bi-bell-fill"></i>';
        const text = document.createElement('span'); text.className = 'rt-toast-text'; text.textContent = n.text; // textContent : 글자로만 (XSS 방지)
        const close = document.createElement('button'); close.type = 'button'; close.className = 'rt-toast-close'; close.setAttribute('aria-label', '닫기'); close.textContent = '×';
        close.onclick = (e) => { e.preventDefault(); t.remove(); };
        t.append(icon, text, close);
        $toasts.prepend(t);
        setTimeout(() => t.classList.add('rt-hide'), 8000);
        setTimeout(() => t.remove(), 8600);
    }

    const bell = document.getElementById('rt-bell');
    if (bell) bell.onclick = () => { unread = 0; $count.hidden = true; }; // 종을 누르면 읽음

    connect();
})();
