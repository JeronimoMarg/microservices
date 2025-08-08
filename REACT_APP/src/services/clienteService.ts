import axios from 'axios';
import { Cliente, Obra, ClienteFormData, ObraFormData } from '../types/cliente';

const API_BASE_URL = 'http://localhost:8082'; // Puerto del microservicio de clientes

// Configurar axios con interceptores para manejar tokens
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar el token a las peticiones
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor para manejar errores de autenticación
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const clienteService = {
  // Clientes
  async getAllClientes(): Promise<Cliente[]> {
    const response = await api.get<Cliente[]>('/api/clientes/todos');
    return response.data;
  },

  async getClienteById(id: number): Promise<Cliente> {
    const response = await api.get<Cliente>(`/api/clientes/${id}`);
    return response.data;
  },

  async getClienteByDni(dni: number): Promise<Cliente> {
    const response = await api.get<Cliente>(`/api/clientes/dni/${dni}`);
    return response.data;
  },

  async createCliente(clienteData: ClienteFormData): Promise<Cliente> {
    const clienteToSend = {
      ...clienteData,
      dni: parseInt(clienteData.dni),
      maximoDescubierto: 0,
      obrasEnEjecucion: 0,
      maximoObrasEnEjecucion: 3
    };
    const response = await api.post<Cliente>('/api/clientes/crear', clienteToSend);
    return response.data;
  },

  async updateCliente(id: number, clienteData: ClienteFormData): Promise<Cliente> {
    const clienteToSend = {
      id,
      ...clienteData,
      dni: parseInt(clienteData.dni)
    };
    const response = await api.put<Cliente>(`/api/clientes/${id}`, clienteToSend);
    return response.data;
  },

  async deleteCliente(id: number): Promise<void> {
    await api.delete(`/api/clientes/${id}`);
  },

  // Obras
  async getAllObras(): Promise<Obra[]> {
    const response = await api.get<Obra[]>('/api/obras/todos');
    return response.data;
  },

  async getObraById(id: number): Promise<Obra> {
    const response = await api.get<Obra>(`/api/obras/${id}`);
    return response.data;
  },

  async createObra(obraData: ObraFormData, clienteId: number): Promise<Obra> {
    const obraToSend = {
      ...obraData,
      presupuestoEstimado: parseFloat(obraData.presupuestoEstimado),
      cliente: { id: clienteId }
    };
    const response = await api.post<Obra>('/api/obras/crear', obraToSend);
    return response.data;
  },

  async updateObra(id: number, obraData: ObraFormData): Promise<Obra> {
    const obraToSend = {
      id,
      ...obraData,
      presupuestoEstimado: parseFloat(obraData.presupuestoEstimado)
    };
    const response = await api.put<Obra>(`/api/obras/${id}`, obraToSend);
    return response.data;
  },

  async deleteObra(id: number): Promise<void> {
    await api.delete(`/api/obras/${id}`);
  }
};
