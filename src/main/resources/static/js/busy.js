// [화면 상태 ①] 처리 중 버튼 : data-busy="신청 확정 중…" 이 붙은 폼을 보내면
// 제출 버튼을 바로 비활성화하고 글자를 바꾼다 → 응답을 기다리는 동안 두 번 눌러 두 번 보내는 걸 막는다.
// (화면 1차 방어일 뿐 — 두 사람이 동시에 누르는 건 서버가 막아야 한다 : 13주차)
document.addEventListener('submit', (e) => {
    const form = e.target;
    if (!form.matches('form[data-busy]')) return;
    if (form.dataset.sent) { e.preventDefault(); return; } // 이미 보낸 폼 (Enter 연타 등)
    if (!form.checkValidity()) return;                     // required 가 비어 브라우저가 막을 때는 그대로 둔다
    form.dataset.sent = '1';
    const btn = form.querySelector('button[type="submit"], button:not([type])');
    if (!btn) return;
    btn.disabled = true;
    btn.classList.add('is-busy');
    btn.setAttribute('aria-busy', 'true');
    btn.innerHTML = '<span class="busy-spin" aria-hidden="true"></span>';
    btn.append(document.createTextNode(form.dataset.busy));
});

// 뒤로 가기로 돌아왔을 때 (bfcache) 버튼이 "처리 중" 에 멈춰 있지 않게 새로 그린다
window.addEventListener('pageshow', (e) => { if (e.persisted) location.reload(); });
