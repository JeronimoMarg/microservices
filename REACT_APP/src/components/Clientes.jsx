import {useState, useEffect} from 'react';
import clienteService from '../services/clienteService';
import ElementoCliente from './ElementoCliente';
import ModalCliente from './ModalCliente';
import './Clientes.css';
import Notificacion from './Notificacion';

const Clientes = () => {

    const [clienteBuscado, setClienteBuscado] = useState('');
    const [clientes, setClientes] = useState([]);
    const [clienteEditado, setClienteEditado] = useState('');

    const [mostrarModalCreacion, setMostrarModalCreacion] = useState(false);
    const [mostrarModalEdicion, setMostrarModalEdicion] = useState(false);

    const [notificacion, setNotificacion] = useState({mensaje:null, tipo:''});

    //HANDLERS
    const handleBusquedaCliente = (event) => {
        console.log(event.target.value);
        setClienteBuscado(event.target.value);
    }

    const handleAbrirModalCrearCliente = (event) => {
        console.log("Se apreto boton para crear un cliente.");
        setMostrarModalCreacion(true);
    }

    const handleCerrarModalCrearCliente = (event) => {
        setMostrarModalCreacion(false);
        setTimeout(() => {
          setNotificacion({ mensaje: null, tipo: '' })
        }, 3000)
    }

    const handleAbrirModalEditarCliente = (cliente) => {
        console.log("Se apreto boton para editar el cliente con id: ", cliente.id);
        setClienteEditado(cliente);
        setMostrarModalEdicion(true);
    }

    const handleCerrarModalEditarCliente = (event) => {
        setMostrarModalEdicion(false);
        setClienteEditado('');
        setTimeout(() => {
          setNotificacion({ mensaje: null, tipo: '' })
        }, 3000)
    }

    const handleFinEliminarCliente = () => {
        hookListarClientes();
        setTimeout(() => {
          setNotificacion({ mensaje: null, tipo: '' })
        }, 3000)
    }
    //FIN HANDLERS

    //HOOKS
    const hookListarClientes = () => {
        clienteService
        .getAll()
        .then(data => {
            console.log("Promise fullfiled de traer todos los clientes");
            setClientes(data);
        })
    }
    //FIN HOOKS

    //Se corre este efecto solamente con el primer render del componente.
    useEffect(hookListarClientes, []);

    //clientesMapeados es otro componente react que a su vez tiene dos botones mas editar eliminar
    const clientesMapeados = 
        clientes
        .filter(c => 
            c.dni.toString().startsWith(clienteBuscado))
        .map(c => 
            <ElementoCliente 
            key={c.id}
            cliente={c} 
            handlerEditar={handleAbrirModalEditarCliente}
            onSuccessDelete={handleFinEliminarCliente}
            setNotificacion={setNotificacion}> 
            </ElementoCliente>);
    console.log('Se renderizaron: ', clientesMapeados.length, 'clientes');

    //Necesita una barra buscador
    //Necesita un boton de +CREAR CLIENTE
    //Por cada cliente mostrado debe tener un boton de lapiz para modificar
    //Por cada cliente mostrado debe tener un boton de tacho para eliminar
    //Por cada cliente mostrado debe tener un boton para ver las obras
    return (
        <div className="clientes-container">

            <Notificacion mensaje={notificacion.mensaje} tipo={notificacion.tipo}></Notificacion>
            
            <div className="barra-busqueda-clientes">
                <form>
                    <input
                    placeholder="Buscar por DNI" 
                    value={clienteBuscado} 
                    onChange={handleBusquedaCliente}>
                    </input>
                </form>
            </div>

            <div className="crear-clientes">
                <button onClick={handleAbrirModalCrearCliente}>
                    Crear cliente
                </button>
            </div>

            <div className="lista-clientes">
                {clientes.length === 0 ? (
                    <div className="empty-state">
                        <h3>No hay clientes registrados</h3>
                        <p>Haz clic en "Crear cliente" para agregar el primer cliente</p>
                    </div>
                ) : (
                    <ul>
                        {clientesMapeados}
                    </ul>
                )}
            </div>

            <ModalCliente
                open={mostrarModalCreacion}
                onClose={handleCerrarModalCrearCliente}
                onSuccess={hookListarClientes}
                modo="creacion"
                setNotificacion={setNotificacion}>
            </ModalCliente>

            <ModalCliente
                open={mostrarModalEdicion}
                onClose={handleCerrarModalEditarCliente}
                onSuccess={hookListarClientes}
                modo="edicion"
                cliente={clienteEditado}
                setNotificacion={setNotificacion}>
            </ModalCliente>

        </div>
    )

}

export default Clientes;