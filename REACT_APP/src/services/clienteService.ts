import axios from 'axios';

const BASE_URL = 'http://localhost:8082/api/clientes';

// metodos del service de clientes.

export type ClienteCrearTemplate = {
    nombre: string;
    apellido: string;
    dni: number;
    fechaNacimiento: string; // YYYY-MM-DD
    calleDomicilio: string;
    numeroDomicilio: string;
    numeroTelefono: string;
    correoElectronico: string;
}

const getAll = () => {
    const promesa = axios.get(`${BASE_URL}/todos`);
    return promesa.then(respuesta => respuesta.data);
}

const crear = (nuevoCliente: ClienteCrearTemplate) => {
    const promesa = axios.post(`${BASE_URL}/crear`, nuevoCliente);
    return promesa.then(respuesta => respuesta.data);
}

const actualizar = () => {
    //completar
}

export default {getAll, crear, actualizar}