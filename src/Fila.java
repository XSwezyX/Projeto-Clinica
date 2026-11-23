
public class Fila {

    private class NoFila {
        String cpf;
        String tipoPrioridade;
        boolean prioritario;
        NoFila proximo;

        public NoFila(String cpf, String tipoPrioridade) {
            this.cpf = cpf;
            this.tipoPrioridade = tipoPrioridade;
            this.prioritario = !tipoPrioridade.equals("Nenhuma");
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

    // Insere automaticamente conforme a prioridade
    public void enfileirar(String cpf, String tipoPrioridade) {
        NoFila novo = new NoFila(cpf, tipoPrioridade);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;

        } else if (!novo.prioritario) {
            // Paciente comum entra no final
            fim.proximo = novo;
            fim = novo;

        } else if (!inicio.prioritario) {
            // Prioritario entra antes dos comuns
            novo.proximo = inicio;
            inicio = novo;

        } else {
            // Prioritarios respeitam a ordem de chegada
            NoFila atual = inicio;

            while (atual.proximo != null
                    && atual.proximo.prioritario) {
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

    // Remove o primeiro paciente
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

    // Impede CPF duplicado na fila
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

        if (no == null) {
            return null;
        }

        return no.cpf;
    }

    public boolean ehPrioritario(int posicao) {
        NoFila no = noNaPosicao(posicao);
        return no != null && no.prioritario;
    }

    public String tipoPrioridade(int posicao) {
        NoFila no = noNaPosicao(posicao);

        if (no == null) {
            return "Nenhuma";
        }

        return no.tipoPrioridade;
    }

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
