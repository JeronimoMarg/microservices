import React, { useState, useEffect } from 'react';
import { clienteService } from '../services/clienteService';
import { Obra, EstadoObra } from '../types/cliente';
import './ObraEdit.css';

interface ObraEditProps {
  obra: Obra;
  onSave: (obra: Obra) => void;
  onCancel: () => void;
}

const ObraEdit: React.FC<ObraEditProps> = ({ obra, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    direccion: '',
    coordenadas: '',
    presupuestoEstimado: '',
    estadoObra: EstadoObra.HABILITADA
  });
  const [errors, setErrors] = useState<{[key: string]: string}>({});
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setFormData({
      direccion: obra.direccion || '',
      coordenadas: obra.coordenadas || '',
      presupuestoEstimado: obra.presupuestoEstimado?.toString() || '',
      estadoObra: obra.estadoObra || EstadoObra.HABILITADA
    });
  }, [obra]);

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
      const updatedObra = await clienteService.updateObra(obra.id!, formData);
      onSave(updatedObra);
    } catch (error: any) {
      console.error('Error al actualizar obra:', error);
      alert('Error al actualizar la obra. Verifique que las coordenadas sean únicas.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="obra-edit-overlay">
      <div className="obra-edit-modal">
        <div className="modal-header">
          <h3>Editar Obra</h3>
          <button onClick={onCancel} className="close-button">
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="obra-edit-form">
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
              {isLoading ? 'Guardando...' : 'Guardar Cambios'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ObraEdit;
