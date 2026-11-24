
public class Fila {

    private NoFila inicio;
    private NoFila fim;
    private int tamanho;
    private int prioritariosConsecutivos = 0;

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

    // Calcula a ordem prevista: ate tres prioritarios e um comum.
    public int[] ordemAtendimento() {
        int[] ordem = new int[tamanho];
        int totalPrioritarios = 0;
        NoFila atual = inicio;

        while (atual != null && atual.prioritario) {
            totalPrioritarios++;
            atual = atual.proximo;
        }

        int p = 0;
        int c = totalPrioritarios;
        int contador = prioritariosConsecutivos;

        for (int i = 0; i < tamanho; i++) {
            if (c < tamanho && (contador >= 3 || p >= totalPrioritarios)) {
                ordem[i] = c++;
                contador = 0;
            } else {
                ordem[i] = p++;
                if (contador < 3) {
                    contador++;
                }
            }
        }
        return ordem;
    }

    public int getPrioritariosConsecutivos() {
        return prioritariosConsecutivos;
    }

    // Remove o proximo paciente conforme a regra 3 para 1.
    public String desenfileirar() {
        if (estaVazia()) {
            return null;
        }

        int posicao = ordemAtendimento()[0];
        NoFila anterior = null;
        NoFila atual = inicio;

        for (int i = 0; i < posicao; i++) {
            anterior = atual;
            atual = atual.proximo;
        }

        if (anterior == null) {
            inicio = atual.proximo;
        } else {
            anterior.proximo = atual.proximo;
        }
        if (atual == fim) {
            fim = anterior;
        }

        if (atual.prioritario) {
            if (prioritariosConsecutivos < 3) {
                prioritariosConsecutivos++;
            }
        } else {
            prioritariosConsecutivos = 0;
        }
        tamanho--;
        return atual.cpf;
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
        int[] ordem = ordemAtendimento();
        for (int i = 0; i < ordem.length; i++) {
            if (obter(ordem[i]).equals(cpf)) {
                return i + 1;
            }
        }
        return -1;
    }
}
