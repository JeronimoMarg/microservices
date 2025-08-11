
import clienteService from "../services/clienteService";
import './ElementoCliente.css';

const ElementoCliente = ({cliente, handlerEditar, onSuccessDelete, setNotificacion}) => {

    const handleEliminar = async (clienteID) => {
        console.log('Se presiono el boton para eliminar el cliente con id ', clienteID);
        const confirmado = window.confirm("Estas seguro que queres eliminar este cliente?");
        if(!confirmado) return;

        await clienteService
        .eliminar(clienteID)
        .then(data => {
            //Renderizar todos los clientes
            setNotificacion({mensaje:"Cliente borrado exitosamente.", tipo:"success"});
            onSuccessDelete();
        })
        .catch(error => {
            console.log(error)
            setNotificacion({mensaje: 'Error al eliminar cliente', tipo:'error'});
        })
    }

    return(
        <li className="cliente-card">
            <h2>{cliente.nombre + " " + cliente.apellido}</h2>
            <h3>{"DNI: " + cliente.dni}</h3>
            <h3>{"Fecha Nacimiento: " + cliente.fechaNacimiento}</h3>
            <h3>{"Calle: " + cliente.calleDomicilio}</h3>
            <h3>{"Numero domicilio: " + cliente.numeroDomicilio}</h3>
            <h3>{"Numero de telefono: " + cliente.numeroTelefono}</h3>
            <h3>{"Correo electronico: " + cliente.correoElectronico}</h3>
            <div className="cliente-actions">
                <button onClick={ () => handlerEditar(cliente) }>Editar datos</button>
                <button onClick={ () => handleEliminar(cliente.id) }>Eliminar</button>
                <button>Ver obras</button>
            </div>
        </li>
    )

}

export default ElementoCliente;