import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { clienteService } from '../services/clienteService';
import { Cliente, Obra } from '../types/cliente';
import ClientesList from './ClientesList';
import ClienteEdit from './ClienteEdit';
import ObrasList from './ObrasList';
import ObraEdit from './ObraEdit';
import ClienteProfile from './ClienteProfile';
import './Dashboard.css';

type ViewMode = 'dashboard' | 'clientes' | 'obras' | 'profile';

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [viewMode, setViewMode] = useState<ViewMode>('dashboard');
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [selectedCliente, setSelectedCliente] = useState<Cliente | null>(null);
  const [editingCliente, setEditingCliente] = useState<Cliente | null>(null);
  const [editingObra, setEditingObra] = useState<Obra | null>(null);
  const [loading, setLoading] = useState(true);
  const [userProfile, setUserProfile] = useState<Cliente | null>(null);

  useEffect(() => {
    loadClientes();
    checkUserProfile();
  }, []);

  const loadClientes = async () => {
    try {
      setLoading(true);
      const data = await clienteService.getAllClientes();
      setClientes(data);
    } catch (err) {
      console.error('Error loading clientes:', err);
    } finally {
      setLoading(false);
    }
  };

  const checkUserProfile = async () => {
    try {
      // Buscar si el usuario actual tiene un perfil de cliente
      const allClientes = await clienteService.getAllClientes();
      const userCliente = allClientes.find(cliente => 
        cliente.correoElectronico === user?.email
      );
      setUserProfile(userCliente || null);
    } catch (err) {
      console.error('Error checking user profile:', err);
    }
  };

  const handleLogout = () => {
    logout();
  };

  const handleEditCliente = (cliente: Cliente) => {
    setEditingCliente(cliente);
  };

  const handleSaveCliente = (updatedCliente: Cliente) => {
    setClientes(prev => 
      prev.map(c => c.id === updatedCliente.id ? updatedCliente : c)
    );
    setEditingCliente(null);
    loadClientes(); // Recargar para obtener datos actualizados
  };

  const handleViewObras = (cliente: Cliente) => {
    setSelectedCliente(cliente);
    setViewMode('obras');
  };

  const handleEditObra = (obra: Obra) => {
    setEditingObra(obra);
  };

  const handleSaveObra = (updatedObra: Obra) => {
    setEditingObra(null);
    // Recargar obras si estamos en la vista de obras
    if (viewMode === 'obras' && selectedCliente) {
      // La lista se actualizará automáticamente cuando se recargue
    }
  };

  const handleProfileCreated = () => {
    checkUserProfile();
    setViewMode('dashboard');
  };

  const renderContent = () => {
    switch (viewMode) {
      case 'clientes':
        return (
          <ClientesList
            onEditCliente={handleEditCliente}
            onViewObras={handleViewObras}
          />
        );
      
      case 'obras':
        return selectedCliente ? (
          <ObrasList
            cliente={selectedCliente}
            onBack={() => setViewMode('clientes')}
            onEditObra={handleEditObra}
          />
        ) : null;
      
      case 'profile':
        return (
          <ClienteProfile onProfileCreated={handleProfileCreated} />
        );
      
      default:
        return (
          <div className="dashboard-content">
            <div className="welcome-card">
              <h2>¡Bienvenido!</h2>
              <p>Has iniciado sesión correctamente.</p>
              <div className="user-info">
                <strong>Email:</strong> {user?.email}
              </div>
              {!userProfile && (
                <div className="profile-warning">
                  <p>⚠️ Necesitas completar tu perfil de cliente para continuar</p>
                  <button 
                    onClick={() => setViewMode('profile')}
                    className="btn-primary"
                  >
                    Completar Perfil
                  </button>
                </div>
              )}
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

              <div className="info-card">
                <h3>Perfil de Cliente</h3>
                <p>{userProfile ? '✅ Completado' : '❌ Pendiente'}</p>
              </div>

              <div className="info-card">
                <h3>Total de Clientes</h3>
                <p>👥 {clientes.length}</p>
              </div>
            </div>

            {userProfile && (
              <div className="action-cards">
                <div className="action-card" onClick={() => setViewMode('clientes')}>
                  <h3>👥 Gestión de Clientes</h3>
                  <p>Ver, crear, editar y eliminar clientes</p>
                </div>
                
                <div className="action-card" onClick={() => setViewMode('obras')}>
                  <h3>🏗️ Gestión de Obras</h3>
                  <p>Administrar obras de los clientes</p>
                </div>
              </div>
            )}
          </div>
        );
    }
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div className="header-left">
          <h1>Dashboard</h1>
          {viewMode !== 'dashboard' && (
            <button 
              onClick={() => setViewMode('dashboard')}
              className="btn-back"
            >
              ← Volver al Dashboard
            </button>
          )}
        </div>
        <button onClick={handleLogout} className="logout-button">
          Cerrar Sesión
        </button>
      </div>
      
      {renderContent()}

      {/* Modales */}
      {editingCliente && (
        <ClienteEdit
          cliente={editingCliente}
          onSave={handleSaveCliente}
          onCancel={() => setEditingCliente(null)}
        />
      )}

      {editingObra && (
        <ObraEdit
          obra={editingObra}
          onSave={handleSaveObra}
          onCancel={() => setEditingObra(null)}
        />
      )}
    </div>
  );
};

export default Dashboard; 