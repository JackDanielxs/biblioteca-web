import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService, livroService } from '../services/api';

function LivroForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [autores, setAutores] = useState([]);
  const [livro, setLivro] = useState({
    titulo: '',
    isbn: '',
    anoPublicacao: '',
    genero: '',
    autorId: '',
  });
  const [erro, setErro] = useState('');

  useEffect(() => {
    // Carrega os autores para o <select>
    autorService
      .listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores.'));

    if (id) {
      livroService
        .buscar(id)
        .then((res) =>
          setLivro({
            titulo: res.data.titulo || '',
            isbn: res.data.isbn || '',
            anoPublicacao: res.data.anoPublicacao ?? '',
            genero: res.data.genero || '',
            autorId: res.data.autor ? String(res.data.autor.id) : '',
          })
        )
        .catch(() => setErro('Livro não encontrado.'));
    }
  }, [id]);

  const handleChange = (e) => {
    setLivro({ ...livro, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!livro.titulo.trim() || !livro.autorId) {
      setErro('Título e autor são obrigatórios.');
      return;
    }
    // Monta o JSON com o autor só pelo id: "autor": { "id": 1 }
    const dados = {
      titulo: livro.titulo,
      isbn: livro.isbn || null,
      anoPublicacao: livro.anoPublicacao ? parseInt(livro.anoPublicacao, 10) : null,
      genero: livro.genero || null,
      autor: { id: parseInt(livro.autorId, 10) },
    };
    const requisicao = id ? livroService.atualizar(id, dados) : livroService.criar(dados);
    requisicao
      .then(() => navigate('/livros'))
      .catch(() => setErro('Erro ao salvar livro.'));
  };

  return (
    <div>
      <h2>{id ? 'Editar livro' : 'Novo livro'}</h2>
      {erro && <p className="erro">{erro}</p>}

      <form onSubmit={handleSubmit}>
        <label>
          Título *
          <input name="titulo" value={livro.titulo} onChange={handleChange} maxLength={200} required />
        </label>
        <label>
          Autor *
          <select name="autorId" value={livro.autorId} onChange={handleChange} required>
            <option value="">Selecione um autor</option>
            {autores.map((autor) => (
              <option key={autor.id} value={autor.id}>
                {autor.nome}
              </option>
            ))}
          </select>
        </label>
        <label>
          ISBN
          <input name="isbn" value={livro.isbn} onChange={handleChange} maxLength={20} />
        </label>
        <label>
          Ano de publicação
          <input type="number" name="anoPublicacao" value={livro.anoPublicacao} onChange={handleChange} />
        </label>
        <label>
          Gênero
          <input name="genero" value={livro.genero} onChange={handleChange} maxLength={50} />
        </label>

        <div className="acoes">
          <button type="submit" className="btn">Salvar</button>
          <button type="button" className="btn btn-sec" onClick={() => navigate('/livros')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default LivroForm;
