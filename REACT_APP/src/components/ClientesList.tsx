import React, { useState, useEffect } from 'react';
import { clienteService } from '../services/clienteService';
import { Cliente } from '../types/cliente';
import './ClientesList.css';

interface ClientesListProps {
  onEditCliente: (cliente: Cliente) => void;
  onViewObras: (cliente: Cliente) => void;
}

const ClientesList: React.FC<ClientesListProps> = ({ onEditCliente, onViewObras }) => {
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadClientes();
  }, []);

  const loadClientes = async () => {
    try {
      setLoading(true);
      const data = await clienteService.getAllClientes();
      setClientes(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar los clientes');
      console.error('Error loading clientes:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteCliente = async (id: number, nombre: string) => {
    if (window.confirm(`¿Está seguro que desea eliminar al cliente ${nombre}?`)) {
      try {
        await clienteService.deleteCliente(id);
        setClientes(clientes.filter(cliente => cliente.id !== id));
        alert('Cliente eliminado exitosamente');
      } catch (err) {
        alert('Error al eliminar el cliente');
        console.error('Error deleting cliente:', err);
      }
    }
  };

  const filteredClientes = clientes.filter(cliente =>
    cliente.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
    cliente.dni.toString().includes(searchTerm) ||
    cliente.correoElectronico.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-AR');
  };

  if (loading) {
    return (
      <div className="clientes-list-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Cargando clientes...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="clientes-list-container">
        <div className="error-message">
          <p>{error}</p>
          <button onClick={loadClientes} className="btn-retry">
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="clientes-list-container">
      <div className="clientes-header">
        <h2>Gestión de Clientes</h2>
        <div className="search-container">
          <input
            type="text"
            placeholder="Buscar por nombre, apellido, DNI o email..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
      </div>

      {filteredClientes.length === 0 ? (
        <div className="no-clientes">
          <p>{searchTerm ? 'No se encontraron clientes con los criterios de búsqueda' : 'No hay clientes registrados'}</p>
        </div>
      ) : (
        <div className="clientes-grid">
          {filteredClientes.map((cliente) => (
            <div key={cliente.id} className="cliente-card">
              <div className="cliente-header">
                <h3>{cliente.nombre} {cliente.apellido}</h3>
                <span className="cliente-dni">DNI: {cliente.dni}</span>
              </div>
              
              <div className="cliente-info">
                <div className="info-row">
                  <span className="label">Email:</span>
                  <span className="value">{cliente.correoElectronico}</span>
                </div>
                <div className="info-row">
                  <span className="label">Teléfono:</span>
                  <span className="value">{cliente.numeroTelefono}</span>
                </div>
                <div className="info-row">
                  <span className="label">Dirección:</span>
                  <span className="value">{cliente.calleDomicilio} {cliente.numeroDomicilio}</span>
                </div>
                <div className="info-row">
                  <span className="label">Fecha Nacimiento:</span>
                  <span className="value">{formatDate(cliente.fechaNacimiento)}</span>
                </div>
                <div className="info-row">
                  <span className="label">Obras en Ejecución:</span>
                  <span className="value">{cliente.obrasEnEjecucion || 0} / {cliente.maximoObrasEnEjecucion || 3}</span>
                </div>
                <div className="info-row">
                  <span className="label">Máximo Descubierto:</span>
                  <span className="value">${cliente.maximoDescubierto || 0}</span>
                </div>
              </div>

              <div className="cliente-actions">
                <button
                  onClick={() => onViewObras(cliente)}
                  className="btn-secondary"
                  title="Ver obras"
                >
                  🏗️ Obras
                </button>
                <button
                  onClick={() => onEditCliente(cliente)}
                  className="btn-edit"
                  title="Editar cliente"
                >
                  ✏️ Editar
                </button>
                <button
                  onClick={() => handleDeleteCliente(cliente.id!, `${cliente.nombre} ${cliente.apellido}`)}
                  className="btn-delete"
                  title="Eliminar cliente"
                >
                  🗑️ Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ClientesList;
