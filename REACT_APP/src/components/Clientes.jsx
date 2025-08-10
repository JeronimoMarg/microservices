import {useState, useEffect} from 'react';
import clienteService from '../services/clienteService';
import ElementoCliente from './ElementoCliente';
import ModalCliente from './ModalCliente';
import './Clientes.css';

const Clientes = () => {

    const [clienteBuscado, setClienteBuscado] = useState('');
    const [clientes, setClientes] = useState([]);
    const [clienteEditado, setClienteEditado] = useState('');

    const [mostrarModalCreacion, setMostrarModalCreacion] = useState(false);
    const [mostrarModalEdicion, setMostrarModalEdicion] = useState(false);

    //HANDLERS
    const handleBusquedaCliente = (event) => {
        console.log(event.target.value);
        setClienteBuscado(event.target.value);
    }

    const handleAbrirModalCrearCliente = (event) => {
        console.log("Se apreto boton para crear un cliente.");
        //Abrir panel form con datos para crear cliente.
        setMostrarModalCreacion(true);
    }

    const handleCerrarModalCrearCliente = (event) => {
        //Cerrar el modal
        setMostrarModalCreacion(false);
    }

    const handleAbrirModalEditarCliente = (event) => {
        console.log("Se apreto boton para editar un cliente.");
        setClienteEditado(event.target.value);
        //Abrir panel form con datos para crear cliente.
        setMostrarModalEdicion(true);
    }

    const handleCerrarModalEditarCliente = (event) => {
        //Cerrar el modal
        setMostrarModalEdicion(false);
        setClienteEditado('');
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
    console.log('se renderizaron: ', clientes.length, 'clientes');

    const clientesMapeados = clientes.map(c => 
        <ElementoCliente 
        key={c.id} 
        cliente={c} 
        handlerEditar={handleAbrirModalEditarCliente}
        onSuccessDelete={hookListarClientes}> 
        </ElementoCliente>)
    //clientesMapeados es otro componente react que a su vez tiene dos botones mas editar eliminar

    //Necesita una barra buscador
    //Necesita un boton de +CREAR CLIENTE
    //Por cada cliente mostrado debe tener un boton de lapiz para modificar
    //Por cada cliente mostrado debe tener un boton de tacho para eliminar
    //Por cada cliente mostrado debe tener un boton para ver las obras
    return (
        <div>
            
            <div className="barra-busqueda-clientes">
                <form>
                    <input
                    placeholder="Buscar por nombre, apellido o DNI" 
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
                <ul>
                    {clientesMapeados}
                </ul>
            </div>

            <ModalCliente
                open={mostrarModalCreacion}
                onClose={handleCerrarModalCrearCliente}
                onSuccess={hookListarClientes}
                modo="creacion">
            </ModalCliente>

            <ModalCliente
                open={mostrarModalEdicion}
                onClose={handleCerrarModalEditarCliente}
                onSuccess={hookListarClientes}
                modo="edicion"
                cliente={clienteEditado}>
            </ModalCliente>

        </div>
    )

}

export default Clientes;