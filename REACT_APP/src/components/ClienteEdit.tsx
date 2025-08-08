import React, { useState, useEffect } from 'react';
import { clienteService } from '../services/clienteService';
import { Cliente, ClienteFormData } from '../types/cliente';
import './ClienteEdit.css';

interface ClienteEditProps {
  cliente: Cliente;
  onSave: (cliente: Cliente) => void;
  onCancel: () => void;
}

const ClienteEdit: React.FC<ClienteEditProps> = ({ cliente, onSave, onCancel }) => {
  const [formData, setFormData] = useState<ClienteFormData>({
    nombre: '',
    apellido: '',
    dni: '',
    fechaNacimiento: '',
    calleDomicilio: '',
    numeroDomicilio: '',
    numeroTelefono: '',
    correoElectronico: ''
  });
  const [errors, setErrors] = useState<Partial<ClienteFormData>>({});
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // Convertir la fecha del backend al formato requerido por el input date
    const formatDateForInput = (dateString: string) => {
      const date = new Date(dateString);
      return date.toISOString().split('T')[0];
    };

    setFormData({
      nombre: cliente.nombre || '',
      apellido: cliente.apellido || '',
      dni: cliente.dni?.toString() || '',
      fechaNacimiento: cliente.fechaNacimiento ? formatDateForInput(cliente.fechaNacimiento) : '',
      calleDomicilio: cliente.calleDomicilio || '',
      numeroDomicilio: cliente.numeroDomicilio || '',
      numeroTelefono: cliente.numeroTelefono || '',
      correoElectronico: cliente.correoElectronico || ''
    });
  }, [cliente]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Limpiar error del campo cuando el usuario empiece a escribir
    if (errors[name as keyof ClienteFormData]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<ClienteFormData> = {};

    if (!formData.nombre.trim()) newErrors.nombre = 'El nombre es requerido';
    if (!formData.apellido.trim()) newErrors.apellido = 'El apellido es requerido';
    if (!formData.dni.trim()) {
      newErrors.dni = 'El DNI es requerido';
    } else if (!/^\d{7,8}$/.test(formData.dni)) {
      newErrors.dni = 'El DNI debe tener 7 u 8 dígitos';
    }
    if (!formData.fechaNacimiento) newErrors.fechaNacimiento = 'La fecha de nacimiento es requerida';
    if (!formData.calleDomicilio.trim()) newErrors.calleDomicilio = 'La calle es requerida';
    if (!formData.numeroDomicilio.trim()) newErrors.numeroDomicilio = 'El número de domicilio es requerido';
    if (!formData.numeroTelefono.trim()) {
      newErrors.numeroTelefono = 'El teléfono es requerido';
    } else if (!/^\d{10}$/.test(formData.numeroTelefono)) {
      newErrors.numeroTelefono = 'El teléfono debe tener 10 dígitos sin código de área';
    }
    if (!formData.correoElectronico.trim()) {
      newErrors.correoElectronico = 'El correo electrónico es requerido';
    } else if (!/\S+@\S+\.\S+/.test(formData.correoElectronico)) {
      newErrors.correoElectronico = 'El correo electrónico no es válido';
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
      const updatedCliente = await clienteService.updateCliente(cliente.id!, formData);
      onSave(updatedCliente);
    } catch (error: any) {
      console.error('Error al actualizar cliente:', error);
      alert('Error al actualizar el cliente. Verifique que los datos sean únicos (DNI, teléfono, correo).');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="cliente-edit-overlay">
      <div className="cliente-edit-modal">
        <div className="modal-header">
          <h2>Editar Cliente</h2>
          <button onClick={onCancel} className="close-button">
            ✕
          </button>
        </div>
        
        <form onSubmit={handleSubmit} className="cliente-edit-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="nombre">Nombre *</label>
              <input
                type="text"
                id="nombre"
                name="nombre"
                value={formData.nombre}
                onChange={handleInputChange}
                className={errors.nombre ? 'error' : ''}
                placeholder="Ingrese el nombre"
              />
              {errors.nombre && <span className="error-message">{errors.nombre}</span>}
            </div>
            
            <div className="form-group">
              <label htmlFor="apellido">Apellido *</label>
              <input
                type="text"
                id="apellido"
                name="apellido"
                value={formData.apellido}
                onChange={handleInputChange}
                className={errors.apellido ? 'error' : ''}
                placeholder="Ingrese el apellido"
              />
              {errors.apellido && <span className="error-message">{errors.apellido}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="dni">DNI *</label>
              <input
                type="text"
                id="dni"
                name="dni"
                value={formData.dni}
                onChange={handleInputChange}
                className={errors.dni ? 'error' : ''}
                placeholder="12345678"
                maxLength={8}
              />
              {errors.dni && <span className="error-message">{errors.dni}</span>}
            </div>
            
            <div className="form-group">
              <label htmlFor="fechaNacimiento">Fecha de Nacimiento *</label>
              <input
                type="date"
                id="fechaNacimiento"
                name="fechaNacimiento"
                value={formData.fechaNacimiento}
                onChange={handleInputChange}
                className={errors.fechaNacimiento ? 'error' : ''}
              />
              {errors.fechaNacimiento && <span className="error-message">{errors.fechaNacimiento}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="calleDomicilio">Calle *</label>
              <input
                type="text"
                id="calleDomicilio"
                name="calleDomicilio"
                value={formData.calleDomicilio}
                onChange={handleInputChange}
                className={errors.calleDomicilio ? 'error' : ''}
                placeholder="Av. San Martín"
              />
              {errors.calleDomicilio && <span className="error-message">{errors.calleDomicilio}</span>}
            </div>
            
            <div className="form-group">
              <label htmlFor="numeroDomicilio">Número *</label>
              <input
                type="text"
                id="numeroDomicilio"
                name="numeroDomicilio"
                value={formData.numeroDomicilio}
                onChange={handleInputChange}
                className={errors.numeroDomicilio ? 'error' : ''}
                placeholder="1234"
              />
              {errors.numeroDomicilio && <span className="error-message">{errors.numeroDomicilio}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="numeroTelefono">Teléfono *</label>
              <input
                type="text"
                id="numeroTelefono"
                name="numeroTelefono"
                value={formData.numeroTelefono}
                onChange={handleInputChange}
                className={errors.numeroTelefono ? 'error' : ''}
                placeholder="1123456789"
                maxLength={10}
              />
              <small>Sin código de área (ej: 1123456789)</small>
              {errors.numeroTelefono && <span className="error-message">{errors.numeroTelefono}</span>}
            </div>
            
            <div className="form-group">
              <label htmlFor="correoElectronico">Correo Electrónico *</label>
              <input
                type="email"
                id="correoElectronico"
                name="correoElectronico"
                value={formData.correoElectronico}
                onChange={handleInputChange}
                className={errors.correoElectronico ? 'error' : ''}
                placeholder="usuario@ejemplo.com"
              />
              {errors.correoElectronico && <span className="error-message">{errors.correoElectronico}</span>}
            </div>
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

export default ClienteEdit;
