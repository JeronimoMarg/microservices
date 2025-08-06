import axios from 'axios';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '../types/auth';

const API_BASE_URL = 'http://localhost:5000/auth';

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

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/login', credentials);
    return response.data;
  },

  async register(userData: RegisterRequest): Promise<{ msg: string }> {
    const response = await api.post<{ msg: string }>('/register', userData);
    return response.data;
  },

  async getProfile(): Promise<User> {
    const response = await api.get<User>('/profile');
    return response.data;
  },

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },

  getToken(): string | null {
    return localStorage.getItem('token');
  },

  setToken(token: string): void {
    localStorage.setItem('token', token);
  },

  getUser(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  },

  setUser(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  },
}; 