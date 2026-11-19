import { render, screen } from '@testing-library/react';
import App from './App';

test('renderiza o menu da Biblioteca Web', () => {
  render(<App />);
  expect(screen.getByText(/Biblioteca Web/i)).toBeInTheDocument();
});
