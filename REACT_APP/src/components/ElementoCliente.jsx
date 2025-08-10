import {useState, useEffect} from 'react';
import clienteService from "../services/clienteService";

const ElementoCliente = ({key, cliente, handlerEditar, onSuccessDelete}) => {

    //key es el ID del cliente

    //Poner mas datos despues para probar como se ve
    //Implementar el tema de las obras

    const handleEliminar = (clienteID) => {
        console.log('Se presiono el boton para eliminar el cliente con id ', clienteID);
        clienteService
        .eliminar(clienteID)
        .then(data => {
            //Renderizar todos los clientes
            onSuccessDelete();
        })
        .catch(error => {
            //hacer otra cosa...
            //setear una notificacion
        })
    }

    return(
        <li key={key}>
            <h2>{cliente.nombre + " " + cliente.apellido}</h2>
            <h3>{cliente.dni}</h3>
            <button onClick={handlerEditar}>Editar datos</button>
            <button onClick={ () => handleEliminar(key)}>Eliminar</button>
            <button>Ver obras</button>
        </li>
    )

}

export default ElementoCliente;