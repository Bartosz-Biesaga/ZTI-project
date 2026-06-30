import { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';

function CompanyProfile({ user, setUser }) {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [loaded, setLoaded] = useState(false);

  if (!user || user.role !== 'COMPANY' || !user.profileId) {
    return <Navigate to="/" />;
  }

  const profileId = user.profileId;

  async function loadProfile() {
    const response = await fetch(`/api/companies/${profileId}`, { credentials: 'include' });
    if (response.ok) {
      const data = await response.json();
      setName(data.name);
      setLoaded(true);
    }
  }

  useEffect(() => {
    loadProfile();
  }, [profileId]);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setMessage('');

    const response = await fetch(`/api/companies/${profileId}`, {
      method: 'PUT',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name }),
    });

    if (response.ok) {
      setMessage('Profil zapisany');
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Zapis nie powiódł się');
    }
  }

  async function handleDeleteAccount() {
    if (!confirm('Na pewno usunąć konto firmy?')) {
      return;
    }

    const response = await fetch(`/api/companies/${profileId}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (response.ok) {
      setUser(null);
      navigate('/login');
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Usuwanie konta nie powiodło się');
    }
  }

  if (!loaded) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Profil firmy</h1>
      <p>
        <a href="/">Strona główna</a>
      </p>

      <form onSubmit={handleSubmit}>
        <label>
          Nazwa firmy
          <input value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <button type="submit">Zapisz</button>
      </form>

      {message && <p>{message}</p>}
      {error && <p className="error">{error}</p>}

      <h2>Konto</h2>
      <button type="button" onClick={handleDeleteAccount}>
        Usuń konto
      </button>
    </div>
  );
}

export default CompanyProfile;
