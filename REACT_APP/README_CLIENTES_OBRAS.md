# Funcionalidad de Gestión de Clientes y Obras

## Descripción General

Se ha implementado una funcionalidad completa para gestionar clientes y obras en la aplicación React, que se conecta con el microservicio de clientes (MS_CLIENTES) del backend Java.

## Características Implementadas

### 1. Gestión de Clientes

#### Crear Perfil de Cliente
- **Componente**: `ClienteProfile.tsx`
- **Funcionalidad**: Formulario para crear el perfil de cliente después del registro
- **Campos requeridos**:
  - Nombre y Apellido
  - DNI (7-8 dígitos)
  - Fecha de Nacimiento
  - Dirección (Calle y Número)
  - Teléfono (10 dígitos sin código de área)
  - Correo Electrónico
- **Validaciones**: Formato de DNI, teléfono, email y campos obligatorios
- **Valores automáticos**: `maximoDescubierto=0`, `obrasEnEjecucion=0`, `maximoObrasEnEjecucion=3`

#### Listar Clientes
- **Componente**: `ClientesList.tsx`
- **Funcionalidad**: 
  - Muestra todos los clientes en formato de tarjetas
  - Búsqueda por nombre, apellido, DNI o email
  - Información detallada de cada cliente
  - Acciones: Ver obras, Editar, Eliminar

#### Editar Clientes
- **Componente**: `ClienteEdit.tsx`
- **Funcionalidad**: Modal para editar información de clientes existentes
- **Validaciones**: Mismas que en la creación

#### Eliminar Clientes
- **Funcionalidad**: Confirmación antes de eliminar
- **Integración**: Actualización automática de la lista

### 2. Gestión de Obras

#### Listar Obras por Cliente
- **Componente**: `ObrasList.tsx`
- **Funcionalidad**:
  - Muestra obras de un cliente específico
  - Filtrado automático por cliente
  - Estados visuales (Habilitada, Pendiente, Finalizada)
  - Acciones: Editar, Eliminar

#### Crear Obras
- **Funcionalidad**: Modal integrado en `ObrasList.tsx`
- **Campos requeridos**:
  - Dirección
  - Coordenadas (formato: [latitud,longitud])
  - Presupuesto Estimado
  - Estado (Habilitada/Pendiente/Finalizada)
- **Validaciones**: Formato de coordenadas, presupuesto positivo
- **Restricciones**: Máximo de obras por cliente

#### Editar Obras
- **Componente**: `ObraEdit.tsx`
- **Funcionalidad**: Modal para editar obras existentes

#### Eliminar Obras
- **Funcionalidad**: Confirmación antes de eliminar

### 3. Dashboard Integrado

#### Vista Principal
- **Componente**: `Dashboard.tsx` actualizado
- **Funcionalidades**:
  - Verificación de perfil de cliente
  - Estadísticas básicas
  - Navegación entre vistas
  - Acceso a gestión de clientes y obras

#### Flujo de Usuario
1. **Registro/Login**: Usuario se registra o inicia sesión
2. **Perfil de Cliente**: Si no tiene perfil, debe completarlo
3. **Dashboard**: Acceso a todas las funcionalidades
4. **Gestión**: Navegación entre clientes y obras

## Estructura de Archivos

### Tipos TypeScript
- `src/types/cliente.ts`: Interfaces y tipos para Cliente, Obra, EstadoObra

### Servicios
- `src/services/clienteService.ts`: Llamadas al backend con axios

### Componentes
- `src/components/ClienteProfile.tsx`: Crear perfil de cliente
- `src/components/ClientesList.tsx`: Listar y gestionar clientes
- `src/components/ClienteEdit.tsx`: Editar clientes
- `src/components/ObrasList.tsx`: Listar y gestionar obras
- `src/components/ObraEdit.tsx`: Editar obras
- `src/components/Dashboard.tsx`: Dashboard principal (actualizado)

### Estilos CSS
- `src/components/ClienteProfile.css`
- `src/components/ClientesList.css`
- `src/components/ClienteEdit.css`
- `src/components/ObrasList.css`
- `src/components/ObraEdit.css`
- `src/components/Dashboard.css` (actualizado)

## Integración con Backend

### Endpoints Utilizados

#### Clientes
- `GET /api/clientes/todos`: Obtener todos los clientes
- `GET /api/clientes/{id}`: Obtener cliente por ID
- `GET /api/clientes/dni/{dni}`: Obtener cliente por DNI
- `POST /api/clientes/crear`: Crear nuevo cliente
- `PUT /api/clientes/{id}`: Actualizar cliente
- `DELETE /api/clientes/{id}`: Eliminar cliente

#### Obras
- `GET /api/obras/todos`: Obtener todas las obras
- `GET /api/obras/{id}`: Obtener obra por ID
- `POST /api/obras/crear`: Crear nueva obra
- `PUT /api/obras/{id}`: Actualizar obra
- `DELETE /api/obras/{id}`: Eliminar obra

### Configuración
- **URL Base**: `http://localhost:8080` (microservicio de clientes)
- **CORS**: Configurado en el backend para permitir `http://localhost:3000`
- **Autenticación**: Interceptores de axios para manejar tokens JWT

## Características Técnicas

### Validaciones
- **Frontend**: Validaciones en tiempo real con mensajes de error
- **Backend**: Validaciones de datos únicos (DNI, teléfono, email, coordenadas)
- **Formato de datos**: Conversión automática entre tipos

### Estados de Obra
- **HABILITADA**: Obra activa y operativa
- **PENDIENTE**: Obra en espera (máximo de obras alcanzado)
- **FINALIZADA**: Obra completada

### Responsive Design
- **Desktop**: Layout en grid con tarjetas
- **Tablet**: Adaptación de columnas
- **Mobile**: Layout vertical optimizado

### UX/UI
- **Loading states**: Indicadores de carga
- **Error handling**: Manejo de errores con mensajes claros
- **Confirmaciones**: Diálogos de confirmación para acciones destructivas
- **Animaciones**: Transiciones suaves y efectos hover
- **Modales**: Formularios en modales para mejor UX

## Uso

### Para Desarrolladores

1. **Instalación de dependencias**:
   ```bash
   cd REACT_APP
   npm install
   ```

2. **Configuración del backend**:
   - Asegurar que MS_CLIENTES esté ejecutándose en puerto 8080
   - Verificar que Eureka Server esté activo
   - Configurar base de datos

3. **Ejecutar aplicación**:
   ```bash
   npm start
   ```

### Para Usuarios

1. **Registro/Login**: Crear cuenta o iniciar sesión
2. **Completar Perfil**: Llenar información de cliente
3. **Dashboard**: Acceder a funcionalidades principales
4. **Gestión**: Usar las herramientas de clientes y obras

## Notas Importantes

- Los valores de `maximoDescubierto`, `obrasEnEjecucion`, `maximoObrasEnEjecucion` y `usuariosHabilitados` se manejan automáticamente
- Las coordenadas deben seguir el formato `[latitud,longitud]`
- El teléfono debe ingresarse sin código de área (10 dígitos)
- El DNI debe tener 7 u 8 dígitos
- Cada cliente tiene un límite de 3 obras en ejecución
