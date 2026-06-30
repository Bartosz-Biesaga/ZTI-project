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
    profileLink = <a href="/admin/skills">Zarządzaj umiejętnościami</a>;
  } else if (user.role === 'CANDIDATE') {
    profileLink = (
      <>
        <a href="/candidate/profile">Mój profil</a>
        {' | '}
        <a href="/candidate/offers">Oferty pracy</a>
        {' | '}
        <a href="/candidate/applications">Moje aplikacje</a>
      </>
    );
  } else if (user.role === 'COMPANY') {
    profileLink = (
      <>
        <a href="/company/profile">Profil firmy</a>
        {' | '}
        <a href="/company/offers">Moje oferty pracy</a>
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
