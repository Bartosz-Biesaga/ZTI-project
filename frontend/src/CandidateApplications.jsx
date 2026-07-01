import { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';

const STATUS_LABELS = {
  NEW: 'Nowa',
  SCREENING: 'Weryfikacja',
  INTERVIEW: 'Rozmowa',
  OFFER: 'Oferta',
  REJECTED: 'Odrzucona',
};

function CandidateApplications({ user }) {
  const [applications, setApplications] = useState([]);
  const [error, setError] = useState('');

  if (!user || user.role !== 'CANDIDATE' || !user.profileId) {
    return <Navigate to="/" />;
  }

  const candidateId = user.profileId;

  async function loadApplications() {
    const response = await fetch(`/api/candidates/${candidateId}/applications`, {
      credentials: 'include',
    });
    if (response.ok) {
      setApplications(await response.json());
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Nie udało się wczytać aplikacji');
    }
  }

  useEffect(() => {
    loadApplications();
  }, [candidateId]);

  function formatDate(dateStr) {
    if (!dateStr) {
      return '—';
    }
    return dateStr.replace('T', ' ').slice(0, 16);
  }

  return (
    <div>
      <h1>Moje aplikacje</h1>
      <p>
        <Link to="/">Strona główna</Link> | <Link to="/candidate/profile">Mój profil</Link> |{' '}
        <Link to="/candidate/offers">Oferty pracy</Link>
      </p>

      {error && <p className="error">{error}</p>}

      {applications.length === 0 && !error && <p>Brak aplikacji.</p>}

      <ul>
        {applications.map((app) => (
          <li key={app.id}>
            <strong>{app.offerTitle}</strong> — {app.companyName}
            <br />
            Status: {STATUS_LABELS[app.status] || app.status}
            <br />
            Data aplikacji: {formatDate(app.appliedAt)}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default CandidateApplications;
