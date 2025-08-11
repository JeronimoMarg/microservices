import { useState, useEffect } from "react";
import clienteService from "../services/clienteService";
import './ModalCliente.css';

const ClienteModal = ( {onClose, onSuccess, open, modo, cliente, setNotificacion}) => {

    const [formData, setFormData] = useState({
        nombre: '',
        apellido: '',
        dni: '',
        fechaNacimiento: '',
        calleDomicilio: '',
        numeroDomicilio: '',
        numeroTelefono: '',
        correoElectronico: ''
    });
    const [error, setError] = useState('');

    const handlerLimpiarFichaClose = () => {
      //Cuando se cancela la edicion deberia de limpiarse la ficha de carga de datos
      setFormData({
          nombre: '',
          apellido: '',
          dni: '',
          fechaNacimiento: '',
          calleDomicilio: '',
          numeroDomicilio: '',
          numeroTelefono: '',
          correoElectronico: ''
        });
      onClose();
    }

    //Se cargan datos o no dependiendo del modo
    useEffect(() => {
      if (modo === 'edicion' && cliente) {
        setFormData({
          nombre: cliente.nombre || '',
          apellido: cliente.apellido || '',
          dni: String(cliente.dni ?? ''),
          fechaNacimiento: cliente.fechaNacimiento || '', // YYYY-MM-DD
          calleDomicilio: cliente.calleDomicilio || '',
          numeroDomicilio: cliente.numeroDomicilio || '',
          numeroTelefono: cliente.numeroTelefono || '',
          correoElectronico: cliente.correoElectronico || '',
        });
      }
      if (modo === 'creacion') {
        setFormData({
          nombre: '',
          apellido: '',
          dni: '',
          fechaNacimiento: '',
          calleDomicilio: '',
          numeroDomicilio: '',
          numeroTelefono: '',
          correoElectronico: ''
        });
      }
      setError('');
    }, [modo, cliente]);

    if (!open) return null;
    
    //HANDLERS
    const handleSubmit = async (event) => {
        event.preventDefault();
        setError('');
        try {
          if (modo === 'creacion'){
            const nuevoCliente = {
                nombre: formData.nombre.trim(),
                apellido: formData.apellido.trim(),
                dni: Number(formData.dni),
                fechaNacimiento: formData.fechaNacimiento, // YYYY-MM-DD
                calleDomicilio: formData.calleDomicilio.trim(),
                numeroDomicilio: formData.numeroDomicilio.trim(),
                numeroTelefono: formData.numeroTelefono.trim(),
                correoElectronico: formData.correoElectronico.trim()
            }
            await clienteService.crear(nuevoCliente);
            setNotificacion({mensaje: 'Cliente creado exitosamente', tipo:'success'});
          }
          else if (modo === 'edicion'){
            const modifCliente = {
                id: cliente.id,
                nombre: formData.nombre.trim(),
                apellido: formData.apellido.trim(),
                dni: Number(formData.dni),
                fechaNacimiento: formData.fechaNacimiento, // YYYY-MM-DD
                calleDomicilio: formData.calleDomicilio.trim(),
                numeroDomicilio: formData.numeroDomicilio.trim(),
                numeroTelefono: formData.numeroTelefono.trim(),
                correoElectronico: formData.correoElectronico.trim()
            }
            await clienteService.actualizar(cliente.id, modifCliente);
            setNotificacion({mensaje: 'Cliente actualizado exitosamente', tipo:'success'});
          }

            if (onSuccess) {
                await onSuccess();
            }
            onClose();
        } catch (error) {
            console.error('Error al guardar el cliente:', error);
            setError('Error al guardar el cliente. Verificar los datos.');
            setNotificacion({mensaje: 'Error al guardar cliente', tipo:'error'});
        }
    }

    const handleChange = (event) => {
        const {name, value} = event.target;
        //name es el nombre de la etiquetea del input y value es lo tipeado
        setFormData((p) => {
            const nuevo = {...p, [name]: value};
            return nuevo;
        })
        //Se cambia solo el valor del input que se modifico
    }
    //FIN HANDLERS

    return (
        <div className="modal-overlay" onClick={onClose}>

          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>{modo==='creacion' ? "Nuevo cliente" : "Editar cliente"}</h3>
    
            <form onSubmit={handleSubmit} className="form-grid">
              <label>
                Nombre
                <input name="nombre" value={formData.nombre} onChange={handleChange} disabled={modo === 'edicion'} required />
              </label>
    
              <label>
                Apellido
                <input name="apellido" value={formData.apellido} onChange={handleChange} disabled={modo === 'edicion'} required />
              </label>
    
              <label>
                DNI
                <input name="dni" type="number" value={formData.dni} onChange={handleChange} disabled={modo === 'edicion'} required />
              </label>
    
              <label>
                Fecha de nacimiento
                <input name="fechaNacimiento" type="date" value={formData.fechaNacimiento} onChange={handleChange} required />
              </label>
    
              <label>
                Calle
                <input name="calleDomicilio" value={formData.calleDomicilio} onChange={handleChange} required />
              </label>
    
              <label>
                Número
                <input name="numeroDomicilio" value={formData.numeroDomicilio} onChange={handleChange} required />
              </label>
    
              <label>
                Teléfono
                <input name="numeroTelefono" value={formData.numeroTelefono} onChange={handleChange} required />
              </label>
    
              <label>
                Email
                <input name="correoElectronico" type="email" value={formData.correoElectronico} onChange={handleChange} required />
              </label>
    
              {error && <div className="error">{error}</div>}
    
              <div className="actions">
                <button type="button" onClick={handlerLimpiarFichaClose}>Cancelar</button>
                <button type="submit">Guardar</button>
              </div>
              
            </form>
          </div>
        </div>
      );
}

export default ClienteModal;