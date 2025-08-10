import React from 'react';
import {useState} from 'react';
import { useAuth } from '../contexts/AuthContext';
import './Dashboard.css';
import Clientes from './Clientes.jsx';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [vista, setVista] = useState<'dashboard' | 'clientes' | 'productos' | 'pedidos'>('dashboard');

  const handleLogout = () => {
    logout();
  };

  const handleIrClientes = () => {
    console.log("Se apreto boton de ir clientes");
    setVista('clientes');
  }

  const handleIrProductos = () => {
    console.log("Se apreto boton de ir productos");
  }

  const handleIrPedidos = () => {
    console.log("Se apreto boton de ir pedidos");
  }

  const handleVolverDashboard = () => {
    console.log('Se apreto boton para volver al dashboard.');
    setVista('dashboard');
  }

  if (vista === 'clientes') {
    return (
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h1>Gestión de Clientes</h1>
          <div className="header-buttons">
            <button onClick={handleVolverDashboard} className="back-button">
              Volver al Dashboard
            </button>
            <button onClick={handleLogout} className="logout-button">
              Cerrar Sesión
            </button>
          </div>
        </div>
        <Clientes />
      </div>
    );
  }

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

        <div className="botones-servicios">
          <div className="boton-servicio">
            <button onClick={handleIrClientes} className="boton-servicio-clientes">Ir a clientes</button>
          </div>

          <div className="boton-servicio">
            <button onClick={handleIrProductos} className="boton-servicio-productos">Ir a productos</button>
          </div>

          <div className="boton-servicio">
            <button onClick={handleIrPedidos} className="boton-servicio-pedidos">Ir a pedidos</button>
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard; 