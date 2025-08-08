export interface Cliente {
  id?: number;
  nombre: string;
  apellido: string;
  dni: number;
  fechaNacimiento: string; // formato DD/MM/AA
  calleDomicilio: string;
  numeroDomicilio: string;
  numeroTelefono: string; // 10 dígitos sin código de área
  correoElectronico: string;
  maximoDescubierto?: number;
  obrasEnEjecucion?: number;
  maximoObrasEnEjecucion?: number;
  usuariosHabilitados?: UsuarioHabilitado[];
}

export interface Obra {
  id?: number;
  direccion: string;
  coordenadas: string; // formato: [latitud,longitud]
  cliente?: Cliente;
  presupuestoEstimado: number;
  estadoObra: EstadoObra;
}

export enum EstadoObra {
  HABILITADA = 'HABILITADA',
  PENDIENTE = 'PENDIENTE',
  FINALIZADA = 'FINALIZADA'
}

export interface UsuarioHabilitado {
  id?: number;
  nombre: string;
  apellido: string;
  dni: number;
  cliente?: Cliente;
}

export interface ClienteFormData {
  nombre: string;
  apellido: string;
  dni: string;
  fechaNacimiento: string;
  calleDomicilio: string;
  numeroDomicilio: string;
  numeroTelefono: string;
  correoElectronico: string;
}

export interface ObraFormData {
  direccion: string;
  coordenadas: string;
  presupuestoEstimado: string;
  estadoObra: EstadoObra;
}
