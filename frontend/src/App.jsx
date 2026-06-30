import { useEffect, useState } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AdminSkills from './AdminSkills';
import CandidateApplications from './CandidateApplications';
import CandidateOffers from './CandidateOffers';
import CandidateProfile from './CandidateProfile';
import CompanyOffers from './CompanyOffers';
import CompanyProfile from './CompanyProfile';
import Home from './Home';
import Login from './Login';
import Register from './Register';

function App() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetch('/api/auth/me', { credentials: 'include' })
      .then((response) => {
        if (response.ok) {
          return response.json();
        }
        return null;
      })
      .then((data) => setUser(data));
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login user={user} setUser={setUser} />} />
        <Route path="/register" element={<Register user={user} setUser={setUser} />} />
        <Route
          path="/"
          element={user ? <Home user={user} setUser={setUser} /> : <Navigate to="/login" />}
        />
        <Route path="/admin/skills" element={<AdminSkills user={user} />} />
        <Route
          path="/candidate/profile"
          element={<CandidateProfile user={user} setUser={setUser} />}
        />
        <Route
          path="/company/profile"
          element={<CompanyProfile user={user} setUser={setUser} />}
        />
        <Route path="/company/offers" element={<CompanyOffers user={user} />} />
        <Route path="/candidate/offers" element={<CandidateOffers user={user} />} />
        <Route
          path="/candidate/applications"
          element={<CandidateApplications user={user} />}
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
