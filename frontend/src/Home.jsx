import { Link } from 'react-router-dom';

function Home({ user, setUser }) {
  const roleNames = {
    CANDIDATE: 'Kandydat',
    COMPANY: 'Firma',
    ADMIN: 'Administrator',
  };

  async function handleLogout() {
    await fetch('/api/auth/logout', {
      method: 'POST',
      credentials: 'include',
    });
    setUser(null);
  }

  const roleLabel = roleNames[user.role] || user.role;

  let profileLink = null;
  if (user.role === 'ADMIN') {
    profileLink = <Link to="/admin/skills">Zarządzaj umiejętnościami</Link>;
  } else if (user.role === 'CANDIDATE') {
    profileLink = (
      <>
        <Link to="/candidate/profile">Mój profil</Link>
        {' | '}
        <Link to="/candidate/offers">Oferty pracy</Link>
        {' | '}
        <Link to="/candidate/applications">Moje aplikacje</Link>
      </>
    );
  } else if (user.role === 'COMPANY') {
    profileLink = (
      <>
        <Link to="/company/profile">Profil firmy</Link>
        {' | '}
        <Link to="/company/offers">Moje oferty pracy</Link>
      </>
    );
  }

  return (
    <div>
      <h1>HireMe</h1>
      <p>
        Zalogowano jako {user.email} ({roleLabel})
      </p>
      <p>{profileLink}</p>
      <button type="button" onClick={handleLogout}>
        Wyloguj się
      </button>
    </div>
  );
}

export default Home;
