import { AuthProvider } from '@/context/AuthContext'
import AppRoutes from '@/routes'
import { BrowserRouter } from 'react-router-dom'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
