async function handleRegister(event) {
    event.preventDefault();

    const btn = document.getElementById('submitBtn');
    const errorMsg = document.getElementById('errorMsg');
    const successMsg = document.getElementById('successMsg');

    const username = document.getElementById('username').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    errorMsg.style.display = 'none';
    successMsg.style.display = 'none';

    if (password !== confirmPassword) {
        errorMsg.textContent = 'Passwords do not match.';
        errorMsg.style.display = 'block';
        return;
    }

    if (password.length < 6) {
        errorMsg.textContent = 'Password must be at least 6 characters.';
        errorMsg.style.display = 'block';
        return;
    }

    btn.disabled = true;
    btn.textContent = 'Registering…';

    try {
        const res = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        const text = await res.text();

        if (res.status === 201) {
            successMsg.style.display = 'block';
            setTimeout(() => { window.location.href = '/login.html'; }, 1500);
        } else {
            errorMsg.textContent = text;
            errorMsg.style.display = 'block';
            btn.disabled = false;
            btn.textContent = 'Register';
        }
    } catch (e) {
        errorMsg.textContent = 'Something went wrong. Please try again.';
        errorMsg.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Register';
    }
}
