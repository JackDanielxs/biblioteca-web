import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService } from '../services/api';

function AutorForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [autor, setAutor] = useState({ nome: '', nacionalidade: '', dataNascimento: '' });
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (id) {
      autorService
        .buscar(id)
        .then((res) =>
          setAutor({
            nome: res.data.nome || '',
            nacionalidade: res.data.nacionalidade || '',
            dataNascimento: res.data.dataNascimento || '',
          })
        )
        .catch(() => setErro('Autor não encontrado.'));
    }
  }, [id]);

  const handleChange = (e) => {
    setAutor({ ...autor, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!autor.nome.trim()) {
      setErro('O nome é obrigatório.');
      return;
    }
    const dados = { ...autor, dataNascimento: autor.dataNascimento || null };
    const requisicao = id ? autorService.atualizar(id, dados) : autorService.criar(dados);
    requisicao
      .then(() => navigate('/autores'))
      .catch(() => setErro('Erro ao salvar autor.'));
  };

  return (
    <div>
      <h2>{id ? 'Editar autor' : 'Novo autor'}</h2>
      {erro && <p className="erro">{erro}</p>}

      <form onSubmit={handleSubmit}>
        <label>
          Nome *
          <input name="nome" value={autor.nome} onChange={handleChange} maxLength={100} required />
        </label>
        <label>
          Nacionalidade
          <input name="nacionalidade" value={autor.nacionalidade} onChange={handleChange} maxLength={50} />
        </label>
        <label>
          Data de nascimento
          <input type="date" name="dataNascimento" value={autor.dataNascimento} onChange={handleChange} />
        </label>

        <div className="acoes">
          <button type="submit" className="btn">Salvar</button>
          <button type="button" className="btn btn-sec" onClick={() => navigate('/autores')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default AutorForm;
