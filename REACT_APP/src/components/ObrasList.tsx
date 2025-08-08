import React, { useState, useEffect } from 'react';
import { clienteService } from '../services/clienteService';
import { Obra, Cliente, EstadoObra } from '../types/cliente';
import ObraEdit from './ObraEdit';
import './ObrasList.css';

interface ObrasListProps {
  cliente: Cliente;
  onBack: () => void;
  onEditObra: (obra: Obra) => void;
}

const ObrasList: React.FC<ObrasListProps> = ({ cliente, onBack, onEditObra }) => {
  const [obras, setObras] = useState<Obra[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingObra, setEditingObra] = useState<Obra | null>(null);

  useEffect(() => {
    loadObras();
  }, []);

  const loadObras = async () => {
    try {
      setLoading(true);
      const allObras = await clienteService.getAllObras();
      // Filtrar obras que pertenecen al cliente actual
      const clienteObras = allObras.filter(obra => obra.cliente?.id === cliente.id);
      setObras(clienteObras);
      setError(null);
    } catch (err) {
      setError('Error al cargar las obras');
      console.error('Error loading obras:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteObra = async (id: number, direccion: string) => {
    if (window.confirm(`¿Está seguro que desea eliminar la obra en ${direccion}?`)) {
      try {
        await clienteService.deleteObra(id);
        setObras(obras.filter(obra => obra.id !== id));
        alert('Obra eliminada exitosamente');
      } catch (err) {
        alert('Error al eliminar la obra');
        console.error('Error deleting obra:', err);
      }
    }
  };

  const getEstadoColor = (estado: EstadoObra) => {
    switch (estado) {
      case EstadoObra.HABILITADA:
        return '#28a745';
      case EstadoObra.PENDIENTE:
        return '#ffc107';
      case EstadoObra.FINALIZADA:
        return '#6c757d';
      default:
        return '#6c757d';
    }
  };

  const getEstadoText = (estado: EstadoObra) => {
    switch (estado) {
      case EstadoObra.HABILITADA:
        return 'Habilitada';
      case EstadoObra.PENDIENTE:
        return 'Pendiente';
      case EstadoObra.FINALIZADA:
        return 'Finalizada';
      default:
        return estado;
    }
  };

  if (loading) {
    return (
      <div className="obras-list-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Cargando obras...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="obras-list-container">
        <div className="error-message">
          <p>{error}</p>
          <button onClick={loadObras} className="btn-retry">
            Reintentar
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="obras-list-container">
      <div className="obras-header">
        <div className="header-left">
          <button onClick={onBack} className="btn-back">
            ← Volver
          </button>
          <h2>Obras de {cliente.nombre} {cliente.apellido}</h2>
        </div>
        <button 
          onClick={() => setShowCreateForm(true)}
          className="btn-primary"
          disabled={(cliente.obrasEnEjecucion || 0) >= (cliente.maximoObrasEnEjecucion || 3)}
        >
          ➕ Nueva Obra
        </button>
      </div>

      {(cliente.obrasEnEjecucion || 0) >= (cliente.maximoObrasEnEjecucion || 3) && (
        <div className="warning-message">
          <p>⚠️ El cliente ha alcanzado el máximo de obras permitidas ({cliente.maximoObrasEnEjecucion || 3})</p>
        </div>
      )}

      {obras.length === 0 ? (
        <div className="no-obras">
          <p>No hay obras registradas para este cliente</p>
        </div>
      ) : (
        <div className="obras-grid">
          {obras.map((obra) => (
            <div key={obra.id} className="obra-card">
              <div className="obra-header">
                <h3>{obra.direccion}</h3>
                <span 
                  className="estado-badge"
                  style={{ backgroundColor: getEstadoColor(obra.estadoObra) }}
                >
                  {getEstadoText(obra.estadoObra)}
                </span>
              </div>
              
              <div className="obra-info">
                <div className="info-row">
                  <span className="label">Coordenadas:</span>
                  <span className="value">{obra.coordenadas}</span>
                </div>
                <div className="info-row">
                  <span className="label">Presupuesto:</span>
                  <span className="value">${obra.presupuestoEstimado?.toLocaleString() || 0}</span>
                </div>
                <div className="info-row">
                  <span className="label">Estado:</span>
                  <span className="value">{getEstadoText(obra.estadoObra)}</span>
                </div>
              </div>

              <div className="obra-actions">
                <button
                  onClick={() => onEditObra(obra)}
                  className="btn-edit"
                  title="Editar obra"
                >
                  ✏️ Editar
                </button>
                <button
                  onClick={() => handleDeleteObra(obra.id!, obra.direccion)}
                  className="btn-delete"
                  title="Eliminar obra"
                >
                  🗑️ Eliminar
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

             {showCreateForm && (
         <ObraCreateForm
           cliente={cliente}
           onSave={(newObra) => {
             setObras([...obras, newObra]);
             setShowCreateForm(false);
           }}
           onCancel={() => setShowCreateForm(false)}
         />
       )}

       {editingObra && (
         <ObraEdit
           obra={editingObra}
           onSave={(updatedObra) => {
             setObras(obras.map(o => o.id === updatedObra.id ? updatedObra : o));
             setEditingObra(null);
           }}
           onCancel={() => setEditingObra(null)}
         />
       )}
    </div>
  );
};

// Componente para crear nueva obra
interface ObraCreateFormProps {
  cliente: Cliente;
  onSave: (obra: Obra) => void;
  onCancel: () => void;
}

const ObraCreateForm: React.FC<ObraCreateFormProps> = ({ cliente, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    direccion: '',
    coordenadas: '',
    presupuestoEstimado: '',
    estadoObra: EstadoObra.HABILITADA
  });
  const [errors, setErrors] = useState<{[key: string]: string}>({});
  const [isLoading, setIsLoading] = useState(false);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: {[key: string]: string} = {};

    if (!formData.direccion.trim()) newErrors.direccion = 'La dirección es requerida';
    if (!formData.coordenadas.trim()) {
      newErrors.coordenadas = 'Las coordenadas son requeridas';
    } else if (!/^\[-?\d+\.?\d*,-?\d+\.?\d*\]$/.test(formData.coordenadas)) {
      newErrors.coordenadas = 'Formato inválido. Use: [latitud,longitud]';
    }
    if (!formData.presupuestoEstimado.trim()) {
      newErrors.presupuestoEstimado = 'El presupuesto es requerido';
    } else if (isNaN(Number(formData.presupuestoEstimado)) || Number(formData.presupuestoEstimado) <= 0) {
      newErrors.presupuestoEstimado = 'El presupuesto debe ser un número positivo';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsLoading(true);
    try {
      const newObra = await clienteService.createObra(formData, cliente.id!);
      onSave(newObra);
    } catch (error: any) {
      console.error('Error al crear obra:', error);
      alert('Error al crear la obra. Verifique que las coordenadas sean únicas.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="obra-create-overlay">
      <div className="obra-create-modal">
        <div className="modal-header">
          <h3>Nueva Obra</h3>
          <button onClick={onCancel} className="close-button">
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="obra-create-form">
          <div className="form-group">
            <label htmlFor="direccion">Dirección *</label>
            <input
              type="text"
              id="direccion"
              name="direccion"
              value={formData.direccion}
              onChange={handleInputChange}
              className={errors.direccion ? 'error' : ''}
              placeholder="Av. San Martín 1234"
            />
            {errors.direccion && <span className="error-message">{errors.direccion}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="coordenadas">Coordenadas *</label>
            <input
              type="text"
              id="coordenadas"
              name="coordenadas"
              value={formData.coordenadas}
              onChange={handleInputChange}
              className={errors.coordenadas ? 'error' : ''}
              placeholder="[-34.6037,-58.3816]"
            />
            <small>Formato: [latitud,longitud]</small>
            {errors.coordenadas && <span className="error-message">{errors.coordenadas}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="presupuestoEstimado">Presupuesto Estimado *</label>
            <input
              type="number"
              id="presupuestoEstimado"
              name="presupuestoEstimado"
              value={formData.presupuestoEstimado}
              onChange={handleInputChange}
              className={errors.presupuestoEstimado ? 'error' : ''}
              placeholder="100000"
              min="0"
              step="0.01"
            />
            {errors.presupuestoEstimado && <span className="error-message">{errors.presupuestoEstimado}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="estadoObra">Estado</label>
            <select
              id="estadoObra"
              name="estadoObra"
              value={formData.estadoObra}
              onChange={handleInputChange}
            >
              <option value={EstadoObra.HABILITADA}>Habilitada</option>
              <option value={EstadoObra.PENDIENTE}>Pendiente</option>
              <option value={EstadoObra.FINALIZADA}>Finalizada</option>
            </select>
          </div>

          <div className="form-actions">
            <button 
              type="button" 
              onClick={onCancel}
              className="btn-secondary"
              disabled={isLoading}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              className="btn-primary"
              disabled={isLoading}
            >
              {isLoading ? 'Creando...' : 'Crear Obra'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ObrasList;
