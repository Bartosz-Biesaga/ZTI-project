import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

function AdminSkills({ user }) {
  const [skills, setSkills] = useState([]);
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [error, setError] = useState('');

  if (!user || user.role !== 'ADMIN') {
    return <Navigate to="/" />;
  }

  async function loadSkills() {
    const response = await fetch('/api/skills', { credentials: 'include' });
    if (response.ok) {
      setSkills(await response.json());
    }
  }

  useEffect(() => {
    loadSkills();
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');

    const url = editingId ? `/api/skills/${editingId}` : '/api/skills';
    const method = editingId ? 'PUT' : 'POST';

    const response = await fetch(url, {
      method,
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name }),
    });

    if (response.ok) {
      setName('');
      setEditingId(null);
      loadSkills();
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Operacja nie powiodła się');
    }
  }

  function startEdit(skill) {
    setEditingId(skill.id);
    setName(skill.name);
    setError('');
  }

  function cancelEdit() {
    setEditingId(null);
    setName('');
    setError('');
  }

  async function handleDelete(id) {
    if (!confirm('Usunąć tę umiejętność?')) {
      return;
    }

    const response = await fetch(`/api/skills/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });

    if (response.ok) {
      loadSkills();
    } else {
      const data = await response.json();
      setError(data.detail || data.error || 'Usuwanie nie powiodło się');
    }
  }

  return (
    <div>
      <h1>Umiejętności</h1>
      <p>
        <a href="/">Strona główna</a>
      </p>

      <form onSubmit={handleSubmit}>
        <label>
          Nazwa umiejętności
          <input value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <button type="submit">{editingId ? 'Zapisz zmiany' : 'Dodaj'}</button>
        {editingId && (
          <button type="button" onClick={cancelEdit}>
            Anuluj
          </button>
        )}
      </form>
      {error && <p className="error">{error}</p>}

      <ul>
        {skills.map((skill) => (
          <li key={skill.id}>
            {skill.name}{' '}
            <button type="button" onClick={() => startEdit(skill)}>
              Edytuj
            </button>{' '}
            <button type="button" onClick={() => handleDelete(skill.id)}>
              Usuń
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default AdminSkills;
