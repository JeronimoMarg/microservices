import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import './Dashboard.css';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <button onClick={handleLogout} className="logout-button">
          Cerrar Sesión
        </button>
      </div>
      
      <div className="dashboard-content">
        <div className="welcome-card">
          <h2>¡Bienvenido!</h2>
          <p>Has iniciado sesión correctamente.</p>
          <div className="user-info">
            <strong>Email:</strong> {user?.email}
          </div>
        </div>
        
        <div className="info-cards">
          <div className="info-card">
            <h3>Estado de la Sesión</h3>
            <p>✅ Autenticado</p>
          </div>
          
          <div className="info-card">
            <h3>Token JWT</h3>
            <p>🔐 Válido</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 