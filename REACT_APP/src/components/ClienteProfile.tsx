import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { clienteService } from '../services/clienteService';
import { ClienteFormData } from '../types/cliente';
import './ClienteProfile.css';

interface ClienteProfileProps {
  onProfileCreated: () => void;
}

const ClienteProfile: React.FC<ClienteProfileProps> = ({ onProfileCreated }) => {
  const navigate = useNavigate();
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
      await clienteService.createCliente(formData);
      onProfileCreated();
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Error al crear perfil:', error);
      alert('Error al crear el perfil. Verifique que los datos sean únicos (DNI, teléfono, correo).');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="cliente-profile-container">
      <div className="cliente-profile-card">
        <h2>Completar Perfil de Cliente</h2>
        <p className="profile-description">
          Para continuar, necesitamos que complete su información de cliente.
        </p>
        
        <form onSubmit={handleSubmit} className="cliente-profile-form">
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
                placeholder="Ingrese su nombre"
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
                placeholder="Ingrese su apellido"
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
              type="submit" 
              className="btn-primary"
              disabled={isLoading}
            >
              {isLoading ? 'Creando perfil...' : 'Crear Perfil'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ClienteProfile;
