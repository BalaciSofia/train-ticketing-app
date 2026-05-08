async function handleLogin(event) {
    event.preventDefault();

    const btn = document.getElementById('submitBtn');
    const errorMsg = document.getElementById('errorMsg');
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    btn.disabled = true;
    btn.textContent = 'Signing in…';
    errorMsg.style.display = 'none';

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Unauthorized');
        }

        const data = await response.json();
        sessionStorage.setItem('user', JSON.stringify(data));

        if (data.role === 'ADMIN') {
            window.location.href = '/admin.html';
        } else {
            window.location.href = '/user.html';
        }

    } catch (e) {
        errorMsg.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Sign in';
    }
}
