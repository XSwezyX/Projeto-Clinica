public class Fila {

    private class NoFila {
        String cpf;
        NoFila proximo;

        public NoFila(String cpf) {
            this.cpf = cpf;
            this.proximo = null;
        }
    }

    private NoFila inicio;
    private NoFila fim;
    private int tamanho;

    public Fila() {
        inicio = null;
        fim = null;
        tamanho = 0;
    }

    public void enfileirar(String cpf) {
        NoFila novo = new NoFila(cpf);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }

        tamanho++;
    }

    public String desenfileirar() {
        if (estaVazia()) {
            return null;
        }

        String cpf = inicio.cpf;
        inicio = inicio.proximo;

        if (inicio == null) {
            fim = null;
        }

        tamanho--;
        return cpf;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean contem(String cpf) {
        NoFila atual = inicio;

        while (atual != null) {
            if (atual.cpf.equals(cpf)) {
                return true;
            }

            atual = atual.proximo;
        }

        return false;
    }

    public String obter(int posicao) {
        if (posicao < 0 || posicao >= tamanho) {
            return null;
        }

        NoFila atual = inicio;

        for (int i = 0; i < posicao; i++) {
            atual = atual.proximo;
        }

        return atual.cpf;
    }
}