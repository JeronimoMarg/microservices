import React from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import AuthContainer from './components/AuthContainer';
import Dashboard from './components/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();

  return (
    <div className="App">
      {isAuthenticated ? (
        <ProtectedRoute>
          <Dashboard />
        </ProtectedRoute>
      ) : (
        <AuthContainer />
      )}
    </div>
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;
