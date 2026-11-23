/**
 * Fila encadeada de CPFs, implementada sem colecoes prontas.
 * Cada chegada guarda se tem direito a atendimento prioritario.
 */
public class Fila {

    private class NoFila {
        String cpf;
        boolean prioritario;
        NoFila proximo;

        public NoFila(String cpf, boolean prioritario) {
            this.cpf = cpf;
            this.prioritario = prioritario;
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

    // Adiciona ao final: ordem normal de chegada (opcao 3).
    public void enfileirar(String cpf, boolean prioritario) {
        NoFila novo = new NoFila(cpf, prioritario);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }
        tamanho++;
    }

    // Adiciona os prioritarios antes dos comuns, sem alterar a ordem entre iguais.
    public void enfileirarPrioritario(String cpf, boolean prioritario) {
        if (!prioritario || estaVazia()) {
            enfileirar(cpf, prioritario);
            return;
        }

        NoFila novo = new NoFila(cpf, true);

        if (!inicio.prioritario) {
            // Primeiro paciente prioritario da fila.
            novo.proximo = inicio;
            inicio = novo;
        } else {
            // Procura a ultima posicao do grupo prioritario.
            NoFila atual = inicio;
            while (atual.proximo != null && atual.proximo.prioritario) {
                atual = atual.proximo;
            }
            novo.proximo = atual.proximo;
            atual.proximo = novo;
            if (novo.proximo == null) {
                fim = novo;
            }
        }
        tamanho++;
    }

    // Reorganiza os que ja estavam esperando, preservando a ordem de chegada
    // dentro de cada grupo (prioritarios e nao prioritarios).
    public void organizarPorPrioridade() {
        Fila organizada = new Fila();
        NoFila atual = inicio;
        while (atual != null) {
            organizada.enfileirarPrioritario(atual.cpf, atual.prioritario);
            atual = atual.proximo;
        }
        inicio = organizada.inicio;
        fim = organizada.fim;
        tamanho = organizada.tamanho;
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

    // Posicoes internas iniciam em zero.
    private NoFila noNaPosicao(int posicao) {
        if (posicao < 0 || posicao >= tamanho) {
            return null;
        }
        NoFila atual = inicio;
        for (int i = 0; i < posicao; i++) {
            atual = atual.proximo;
        }
        return atual;
    }

    public String obter(int posicao) {
        NoFila no = noNaPosicao(posicao);
        return no == null ? null : no.cpf;
    }

    public boolean ehPrioritario(int posicao) {
        NoFila no = noNaPosicao(posicao);
        return no != null && no.prioritario;
    }

    // Posicao exibida ao usuario comeca em 1.
    public int posicaoDe(String cpf) {
        NoFila atual = inicio;
        int posicao = 1;
        while (atual != null) {
            if (atual.cpf.equals(cpf)) {
                return posicao;
            }
            posicao++;
            atual = atual.proximo;
        }
        return -1;
    }
}
