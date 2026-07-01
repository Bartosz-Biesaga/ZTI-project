import { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';

const STATUS_OPTIONS = [
  { value: 'NEW', label: 'Nowa' },
  { value: 'SCREENING', label: 'Weryfikacja' },
  { value: 'INTERVIEW', label: 'Rozmowa' },
  { value: 'OFFER', label: 'Oferta' },
  { value: 'REJECTED', label: 'Odrzucona' },
];

function CompanyOffers({ user }) {
  const [offers, setOffers] = useState([]);
  const [allSkills, setAllSkills] = useState([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [minSalary, setMinSalary] = useState('');
  const [maxSalary, setMaxSalary] = useState('');
  const [selectedSkillIds, setSelectedSkillIds] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [applicantsOfferId, setApplicantsOfferId] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [applicantsSortedByMatch, setApplicantsSortedByMatch] = useState(false);
  const [applicantEdits, setApplicantEdits] = useState({});
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  if (!user || user.role !== 'COMPANY' || !user.profileId) {
    return <Navigate to="/" />;
  }

  const companyId = user.profileId;

  async function loadOffers() {
    const response = await fetch(`/api/companies/${companyId}/offers`, { credentials: 'include' });
    if (response.ok) {
      setOffers(await response.json());
    }
  }

  async function loadSkills() {
    const response = await fetch('/api/skills', { credentials: 'include' });
    if (response.ok) {
      setAllSkills(await response.json());
    }
  }

  useEffect(() => {
    loadOffers();
    loadSkills();
  }, [companyId]);

  async function loadApplicants(offerId, sortByMatch = false) {
    setError('');
    const query = sortByMatch ? '?sort=match' : '';
    const response = await fetch(
      `/api/companies/${companyId}/offers/${offerId}/applications${query}`,
      { credentials: 'include' }
    );
    if (response.ok) {
      const data = await response.json();
      setApplicants(data);
      setApplicantsSortedByMatch(sortByMatch);
      const edits = {};
      data.forEach((app) => {
        edits[app.id] = { status: app.status, note: '' };
      });
      setApplicantEdits(edits);
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Nie udało się wczytać aplikacji');
    }
  }

  async function sortApplicantsByMatch() {
    if (!applicantsOfferId) {
      return;
    }
    setError('');
    setMessage('');
    await loadApplicants(applicantsOfferId, true);
  }

  function showApplicants(offerId) {
    if (applicantsOfferId === offerId) {
      setApplicantsOfferId(null);
      setApplicants([]);
      setApplicantsSortedByMatch(false);
      setApplicantEdits({});
    } else {
      setApplicantsOfferId(offerId);
      loadApplicants(offerId);
    }
  }

  function updateApplicantEdit(appId, field, value) {
    setApplicantEdits({
      ...applicantEdits,
      [appId]: { ...applicantEdits[appId], [field]: value },
    });
  }

  async function saveApplicant(appId) {
    setError('');
    setMessage('');

    const edit = applicantEdits[appId];
    const body = {};
    const currentApp = applicants.find((a) => a.id === appId);
    if (edit.status !== currentApp.status) {
      body.status = edit.status;
    }
    if (edit.note && edit.note.trim()) {
      body.note = edit.note.trim();
    }

    if (!body.status && !body.note) {
      setError('Wybierz nowy status lub wpisz notatkę');
      return;
    }

    const response = await fetch(
      `/api/companies/${companyId}/offers/${applicantsOfferId}/applications/${appId}`,
      {
        method: 'PATCH',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      }
    );

    if (response.ok) {
      setMessage('Aplikacja zaktualizowana');
      loadApplicants(applicantsOfferId);
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Aktualizacja nie powiodła się');
    }
  }

  function resetForm() {
    setTitle('');
    setDescription('');
    setMinSalary('');
    setMaxSalary('');
    setSelectedSkillIds([]);
    setEditingId(null);
    setError('');
  }

  function startEdit(offer) {
    setEditingId(offer.id);
    setTitle(offer.title);
    setDescription(offer.description || '');
    setMinSalary(offer.minSalary != null ? String(offer.minSalary) : '');
    setMaxSalary(offer.maxSalary != null ? String(offer.maxSalary) : '');
    setSelectedSkillIds(offer.skills.map((s) => s.id));
    setError('');
    setMessage('');
  }

  function toggleSkill(skillId) {
    if (selectedSkillIds.includes(skillId)) {
      setSelectedSkillIds(selectedSkillIds.filter((id) => id !== skillId));
    } else {
      setSelectedSkillIds([...selectedSkillIds, skillId]);
    }
  }

  function buildRequestBody() {
    const body = {
      title,
      description: description || null,
      skillIds: selectedSkillIds,
    };
    if (minSalary !== '') {
      body.minSalary = Number(minSalary);
    }
    if (maxSalary !== '') {
      body.maxSalary = Number(maxSalary);
    }
    return body;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setMessage('');

    const url = editingId
      ? `/api/companies/${companyId}/offers/${editingId}`
      : `/api/companies/${companyId}/offers`;
    const method = editingId ? 'PUT' : 'POST';

    const response = await fetch(url, {
      method,
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(buildRequestBody()),
    });

    if (response.ok) {
      const wasEditing = editingId;
      resetForm();
      setMessage(wasEditing ? 'Oferta zaktualizowana' : 'Oferta dodana');
      loadOffers();
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Operacja nie powiodła się');
    }
  }

  async function handleDelete(id) {
    if (!confirm('Usunąć tę ofertę?')) {
      return;
    }

    setError('');
    setMessage('');

    const response = await fetch(`/api/companies/${companyId}/offers/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (response.ok) {
      if (editingId === id) {
        resetForm();
      }
      if (applicantsOfferId === id) {
        setApplicantsOfferId(null);
        setApplicants([]);
        setApplicantsSortedByMatch(false);
        setApplicantEdits({});
      }
      loadOffers();
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Usuwanie nie powiodło się');
    }
  }

  function formatSalary(offer) {
    if (offer.minSalary != null && offer.maxSalary != null) {
      return `${offer.minSalary} – ${offer.maxSalary}`;
    }
    if (offer.minSalary != null) {
      return `od ${offer.minSalary}`;
    }
    if (offer.maxSalary != null) {
      return `do ${offer.maxSalary}`;
    }
    return '—';
  }

  function formatDate(dateStr) {
    if (!dateStr) {
      return '—';
    }
    return dateStr.replace('T', ' ').slice(0, 16);
  }

  return (
    <div>
      <h1>Moje oferty pracy</h1>
      <p>
        <Link to="/">Strona główna</Link> | <Link to="/company/profile">Profil firmy</Link>
      </p>

      <form onSubmit={handleSubmit}>
        <h2>{editingId ? 'Edytuj ofertę' : 'Nowa oferta'}</h2>
        <label>
          Tytuł
          <input value={title} onChange={(e) => setTitle(e.target.value)} />
        </label>
        <label>
          Opis
          <textarea value={description} onChange={(e) => setDescription(e.target.value)} />
        </label>
        <label>
          Wynagrodzenie minimalne
          <input
            type="number"
            value={minSalary}
            onChange={(e) => setMinSalary(e.target.value)}
          />
        </label>
        <label>
          Wynagrodzenie maksymalne
          <input
            type="number"
            value={maxSalary}
            onChange={(e) => setMaxSalary(e.target.value)}
          />
        </label>

        <h3>Wymagane umiejętności</h3>
        <ul>
          {allSkills.map((skill) => (
            <li key={skill.id}>
              <label>
                <input
                  type="checkbox"
                  checked={selectedSkillIds.includes(skill.id)}
                  onChange={() => toggleSkill(skill.id)}
                />
                {skill.name}
              </label>
            </li>
          ))}
        </ul>

        <button type="submit">{editingId ? 'Zapisz zmiany' : 'Dodaj ofertę'}</button>
        {editingId && (
          <button type="button" onClick={resetForm}>
            Anuluj
          </button>
        )}
      </form>

      {message && <p>{message}</p>}
      {error && <p className="error">{error}</p>}

      <h2>Lista ofert</h2>
      <ul>
        {offers.map((offer) => (
          <li key={offer.id}>
            <strong>{offer.title}</strong> — {formatSalary(offer)}
            <br />
            Umiejętności: {offer.skills.map((s) => s.name).join(', ') || '—'}
            <br />
            <button type="button" onClick={() => startEdit(offer)}>
              Edytuj
            </button>{' '}
            <button type="button" onClick={() => handleDelete(offer.id)}>
              Usuń
            </button>{' '}
            <button type="button" onClick={() => showApplicants(offer.id)}>
              {applicantsOfferId === offer.id ? 'Ukryj aplikantów' : 'Aplikanci'}
            </button>

            {applicantsOfferId === offer.id && (
              <div>
                <h3>Aplikanci — {offer.title}</h3>
                <button type="button" onClick={sortApplicantsByMatch}>
                  Sortuj według dopasowania kandydata
                </button>
                {applicants.length === 0 && <p>Brak aplikacji.</p>}
                <ul>
                  {applicants.map((app) => (
                    <li key={app.id}>
                      <strong>
                        {app.candidateFirstName} {app.candidateLastName}
                      </strong>{' '}
                      ({app.candidateEmail})
                      <br />
                      Data aplikacji: {formatDate(app.appliedAt)}
                      {applicantsSortedByMatch && app.matchPercent != null && (
                        <>
                          <br />
                          Dopasowanie: {app.matchPercent}%
                        </>
                      )}
                      <br />
                      <label>
                        Status:{' '}
                        <select
                          value={applicantEdits[app.id]?.status || app.status}
                          onChange={(e) => updateApplicantEdit(app.id, 'status', e.target.value)}
                        >
                          {STATUS_OPTIONS.map((opt) => (
                            <option key={opt.value} value={opt.value}>
                              {opt.label}
                            </option>
                          ))}
                        </select>
                      </label>
                      <br />
                      <label>
                        Notatka:{' '}
                        <input
                          value={applicantEdits[app.id]?.note || ''}
                          onChange={(e) => updateApplicantEdit(app.id, 'note', e.target.value)}
                        />
                      </label>{' '}
                      <button type="button" onClick={() => saveApplicant(app.id)}>
                        Zapisz
                      </button>
                      {app.companyNotes && (
                        <>
                          <br />
                          Notatki rekrutacyjne:
                          <pre>{app.companyNotes}</pre>
                        </>
                      )}
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default CompanyOffers;
