import React from 'react';
import { BrowserRouter, Routes } from 'react-router-dom';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <header className="menu">
        <h1>📚 Biblioteca Web</h1>
        <nav></nav>
      </header>

      <main className="conteudo">
        <Routes></Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;
