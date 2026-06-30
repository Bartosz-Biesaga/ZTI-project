import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';

function Login({ user, setUser }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  if (user) {
    return <Navigate to="/" />;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    const response = await fetch('/api/auth/login', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      const data = await response.json();
      setUser(data);
      navigate('/');
    } else {
      const data = await response.json();
      setError(data.error || data.detail || 'Logowanie nie powiodło się');
    }
  }

  return (
    <div>
      <h1>Logowanie</h1>
      <form onSubmit={handleSubmit}>
        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Hasło
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit">Zaloguj się</button>
      </form>
      <p>
        <a href="/register">Zarejestruj się</a>
      </p>
    </div>
  );
}

export default Login;
