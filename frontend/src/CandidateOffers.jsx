import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

function CandidateOffers({ user }) {
  const [offers, setOffers] = useState([]);
  const [selectedOffer, setSelectedOffer] = useState(null);
  const [appliedOfferIds, setAppliedOfferIds] = useState([]);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  if (!user || user.role !== 'CANDIDATE' || !user.profileId) {
    return <Navigate to="/" />;
  }

  const candidateId = user.profileId;

  async function loadOffers() {
    const response = await fetch('/api/job-offers', { credentials: 'include' });
    if (response.ok) {
      setOffers(await response.json());
    }
  }

  async function loadApplications() {
    const response = await fetch(`/api/candidates/${candidateId}/applications`, {
      credentials: 'include',
    });
    if (response.ok) {
      const apps = await response.json();
      setAppliedOfferIds(apps.map((app) => app.jobOfferId));
    }
  }

  useEffect(() => {
    loadOffers();
    loadApplications();
  }, [candidateId]);

  async function showDetail(id) {
    setError('');
    setMessage('');
    const response = await fetch(`/api/job-offers/${id}`, { credentials: 'include' });
    if (response.ok) {
      setSelectedOffer(await response.json());
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Nie udało się wczytać oferty');
    }
  }

  async function handleApply() {
    if (!selectedOffer) {
      return;
    }

    setError('');
    setMessage('');

    const response = await fetch(`/api/candidates/${candidateId}/applications`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ jobOfferId: selectedOffer.id }),
    });

    if (response.ok) {
      setMessage('Aplikacja wysłana');
      setAppliedOfferIds([...appliedOfferIds, selectedOffer.id]);
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Aplikowanie nie powiodło się');
    }
  }

  function formatSalary(offer) {
    if (offer.minSalary != null && offer.maxSalary != null) {
      return `${offer.minSalary} - ${offer.maxSalary}`;
    }
    if (offer.minSalary != null) {
      return `od ${offer.minSalary}`;
    }
    if (offer.maxSalary != null) {
      return `do ${offer.maxSalary}`;
    }
    return '—';
  }

  const alreadyApplied = selectedOffer && appliedOfferIds.includes(selectedOffer.id);

  return (
    <div>
      <h1>Oferty pracy</h1>
      <p>
        <a href="/">Strona główna</a> | <a href="/candidate/profile">Mój profil</a> |{' '}
        <a href="/candidate/applications">Moje aplikacje</a>
      </p>

      {error && <p className="error">{error}</p>}
      {message && <p>{message}</p>}

      <ul>
        {offers.map((offer) => (
          <li key={offer.id}>
            <strong>{offer.title}</strong> — {offer.companyName}
            {appliedOfferIds.includes(offer.id) && ' (zaaplikowano)'}
            <br />
            Wynagrodzenie: {formatSalary(offer)}
            <br />
            Umiejętności: {offer.skills.map((s) => s.name).join(', ') || '—'}
            <br />
            <button type="button" onClick={() => showDetail(offer.id)}>
              Szczegóły
            </button>
          </li>
        ))}
      </ul>

      {selectedOffer && (
        <div>
          <h2>{selectedOffer.title}</h2>
          <p>Firma: {selectedOffer.companyName}</p>
          <p>Wynagrodzenie: {formatSalary(selectedOffer)}</p>
          <p>Opis: {selectedOffer.description || '—'}</p>
          <p>
            Umiejętności: {selectedOffer.skills.map((s) => s.name).join(', ') || '—'}
          </p>
          {alreadyApplied ? (
            <p>Już zaaplikowano na tę ofertę.</p>
          ) : (
            <button type="button" onClick={handleApply}>
              Aplikuj
            </button>
          )}{' '}
          <button type="button" onClick={() => setSelectedOffer(null)}>
            Zamknij
          </button>
        </div>
      )}
    </div>
  );
}

export default CandidateOffers;
