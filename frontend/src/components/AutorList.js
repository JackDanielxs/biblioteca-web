import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { autorService } from '../services/api';

function AutorList() {
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');

  const carregar = () => {
    autorService
      .listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores. O backend está rodando?'));
  };

  useEffect(() => {
    carregar();
  }, []);

  const excluir = (id) => {
    if (!window.confirm('Deseja excluir este autor?')) return;
    autorService
      .deletar(id)
      .then(() => carregar())
      .catch((e) =>
        setErro(
          e.response?.status === 409
            ? e.response.data.erro
            : 'Erro ao excluir autor. Ele possui livros? Exclua os livros antes.'
        )
      );
  };

  return (
    <div>
      <div className="cabecalho">
        <h2>Autores</h2>
        <Link to="/autores/novo" className="btn">+ Novo autor</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Nacionalidade</th>
            <th>Data de nascimento</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {autores.length === 0 && (
            <tr>
              <td colSpan="4">Nenhum autor cadastrado.</td>
            </tr>
          )}
          {autores.map((autor) => (
            <tr key={autor.id}>
              <td>{autor.nome}</td>
              <td>{autor.nacionalidade || '-'}</td>
              <td>{autor.dataNascimento || '-'}</td>
              <td>
                <Link to={`/autores/editar/${autor.id}`} className="btn btn-sec">Editar</Link>
                <button className="btn btn-perigo" onClick={() => excluir(autor.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AutorList;
