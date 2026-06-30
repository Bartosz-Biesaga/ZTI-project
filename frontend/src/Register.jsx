import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';

function Register({ user, setUser }) {
  const navigate = useNavigate();
  const [role, setRole] = useState('CANDIDATE');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [error, setError] = useState('');

  if (user) {
    return <Navigate to="/" />;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    const body = {
      email,
      password,
      role,
      firstName,
      lastName,
      companyName,
    };

    const registerResponse = await fetch('/api/auth/register', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body),
    });

    if (!registerResponse.ok) {
      const data = await registerResponse.json();
      setError(data.detail || data.error || 'Rejestracja nie powiodła się');
      return;
    }

    const loginResponse = await fetch('/api/auth/login', {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (loginResponse.ok) {
      const data = await loginResponse.json();
      setUser(data);
      navigate('/');
    } else {
      setError('Konto zostało utworzone, ale logowanie nie powiodło się');
    }
  }

  return (
    <div>
      <h1>Rejestracja</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Typ konta
          <select value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="CANDIDATE">Kandydat</option>
            <option value="COMPANY">Firma</option>
          </select>
        </label>
        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label>
          Hasło
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {role === 'CANDIDATE' ? (
          <div>
            <label>
              Imię
              <input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
            </label>
            <label>
              Nazwisko
              <input value={lastName} onChange={(e) => setLastName(e.target.value)} />
            </label>
          </div>
        ) : (
          <label>
            Nazwa firmy
            <input value={companyName} onChange={(e) => setCompanyName(e.target.value)} />
          </label>
        )}
        {error && <p className="error">{error}</p>}
        <button type="submit">Utwórz konto</button>
      </form>
      <p>
        <a href="/login">Zaloguj się</a>
      </p>
    </div>
  );
}

export default Register;
