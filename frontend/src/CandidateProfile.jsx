import { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';

function CandidateProfile({ user, setUser }) {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [allSkills, setAllSkills] = useState([]);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  if (!user || user.role !== 'CANDIDATE' || !user.profileId) {
    return <Navigate to="/" />;
  }

  const profileId = user.profileId;

  async function loadProfile() {
    const response = await fetch(`/api/candidates/${profileId}`, { credentials: 'include' });
    if (response.ok) {
      const data = await response.json();
      setProfile(data);
      setFirstName(data.firstName);
      setLastName(data.lastName);
      setEmail(data.email);
    }
  }

  async function loadSkills() {
    const response = await fetch('/api/skills', { credentials: 'include' });
    if (response.ok) {
      setAllSkills(await response.json());
    }
  }

  useEffect(() => {
    loadProfile();
    loadSkills();
  }, [profileId]);

  function hasSkill(skillId) {
    return profile && profile.skills.some((s) => s.id === skillId);
  }

  async function handleProfileSubmit(event) {
    event.preventDefault();
    setError('');
    setMessage('');

    const response = await fetch(`/api/candidates/${profileId}`, {
      method: 'PUT',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ firstName, lastName, email }),
    });

    if (response.ok) {
      const data = await response.json();
      setProfile(data);
      setMessage('Profil zapisany');
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Zapis nie powiódł się');
    }
  }

  async function toggleSkill(skillId) {
    setError('');
    setMessage('');

    const url = `/api/candidates/${profileId}/skills/${skillId}`;
    const method = hasSkill(skillId) ? 'DELETE' : 'POST';

    const response = await fetch(url, {
      method,
      credentials: 'include',
    });

    if (response.ok) {
      const data = await response.json();
      setProfile(data);
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Zmiana umiejętności nie powiodła się');
    }
  }

  async function handleDeleteAccount() {
    if (!confirm('Na pewno usunąć konto?')) {
      return;
    }

    const response = await fetch(`/api/candidates/${profileId}`, {
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

  if (!profile) {
    return <div>Ładowanie...</div>;
  }

  return (
    <div>
      <h1>Mój profil</h1>
      <p>
        <a href="/">Strona główna</a>
      </p>

      <form onSubmit={handleProfileSubmit}>
        <label>
          Imię
          <input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </label>
        <label>
          Nazwisko
          <input value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </label>
        <label>
          E-mail
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <button type="submit">Zapisz profil</button>
      </form>

      <h2>Umiejętności</h2>
      <p>Wybierz umiejętności z listy:</p>
      <ul>
        {allSkills.map((skill) => (
          <li key={skill.id}>
            <label>
              <input
                type="checkbox"
                checked={hasSkill(skill.id)}
                onChange={() => toggleSkill(skill.id)}
              />
              {skill.name}
            </label>
          </li>
        ))}
      </ul>

      {message && <p>{message}</p>}
      {error && <p className="error">{error}</p>}

      <h2>Konto</h2>
      <button type="button" onClick={handleDeleteAccount}>
        Usuń konto
      </button>
    </div>
  );
}

export default CandidateProfile;
