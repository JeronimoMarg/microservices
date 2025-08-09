import {useState, useEffect} from 'react';

const ElementoCliente = ({key, cliente, handlerEditar, handlerEliminar}) => {

    //Poner mas datos despues para probar como se ve
    //Implementar el tema de las obras

    return(
        <li key={key}>
            <h2>{cliente.nombre + " " + cliente.apellido}</h2>
            <h3>{cliente.dni}</h3>
            <button onClick={handlerEditar}>Editar datos</button>
            <button onClick={handlerEliminar}>Eliminar</button>
            <button>Ver obras</button>
        </li>
    )

}

export default ElementoCliente;