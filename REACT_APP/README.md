# React App - Sistema de Autenticación

Esta es una aplicación React con TypeScript que implementa un sistema de autenticación completo conectado a un backend Flask.

## Características

- ✅ Autenticación con JWT
- ✅ Gestión de estado global con Context API
- ✅ Interceptores de Axios para manejo automático de tokens
- ✅ Rutas protegidas
- ✅ UI moderna y responsiva
- ✅ TypeScript para type safety
- ✅ Manejo de errores robusto

## Estructura del Proyecto

```
src/
├── components/
│   ├── Login.tsx          # Componente de login
│   ├── Login.css          # Estilos del login
│   ├── Dashboard.tsx      # Dashboard principal
│   ├── Dashboard.css      # Estilos del dashboard
│   └── ProtectedRoute.tsx # Componente para rutas protegidas
├── contexts/
│   └── AuthContext.tsx    # Contexto de autenticación
├── services/
│   └── authService.ts     # Servicio de autenticación
├── types/
│   └── auth.ts           # Tipos TypeScript
└── App.tsx               # Componente principal
```

## Instalación

1. Instalar dependencias:
```bash
npm install
```

2. Iniciar el servidor de desarrollo:
```bash
npm start
```

## Configuración del Backend

La aplicación se conecta al backend Flask en `MS_USUARIOS` en el puerto 5000. Asegúrate de que el backend esté ejecutándose antes de usar la aplicación.

### Rutas del Backend utilizadas:

- `POST /login` - Iniciar sesión
- `POST /register` - Registrar usuario
- `GET /profile` - Obtener perfil del usuario (requiere token)

## Uso

1. **Login**: Ingresa tu email y contraseña para iniciar sesión
2. **Dashboard**: Una vez autenticado, verás el dashboard con información del usuario
3. **Logout**: Usa el botón "Cerrar Sesión" para salir

## Tecnologías Utilizadas

- React 18
- TypeScript
- Axios para HTTP requests
- CSS3 con animaciones y gradientes
- Context API para state management
- JWT para autenticación

## Mejores Prácticas Implementadas

- ✅ Separación de responsabilidades
- ✅ Componentes reutilizables
- ✅ Type safety con TypeScript
- ✅ Manejo de errores centralizado
- ✅ Interceptores para requests HTTP
- ✅ Persistencia de sesión en localStorage
- ✅ UI/UX moderna y accesible
- ✅ Código limpio y bien documentado

## Desarrollo

Para desarrollo, la aplicación se ejecuta en `http://localhost:3000` y se conecta al backend en `http://localhost:5000`.

### Scripts Disponibles

- `npm start` - Inicia el servidor de desarrollo
- `npm run build` - Construye la aplicación para producción
- `npm test` - Ejecuta las pruebas
- `npm run eject` - Expone la configuración de webpack (irreversible)
