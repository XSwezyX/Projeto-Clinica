
/**
 * Fila encadeada de CPFs, implementada sem colecoes prontas.
 * Funcionalidade adicional: prioridade automatica nas chegadas.
 * Os prioritarios sempre ficam antes dos comuns, respeitando a ordem de
 * chegada dentro de cada grupo. A condicao so vale para esta chegada.
 */
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

    // Uma so operacao de chegada, com prioridade automaticamente ativa.
    public void enfileirar(String cpf, String tipoPrioridade) {
        NoFila novo = new NoFila(cpf, tipoPrioridade);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else if (!novo.prioritario) {
            // Pacientes comuns entram depois de todos os demais.
            fim.proximo = novo;
            fim = novo;
        } else if (!inicio.prioritario) {
            // Primeiro paciente prioritario: entra antes dos comuns.
            novo.proximo = inicio;
            inicio = novo;
        } else {
            // Preserva a ordem de chegada dos pacientes prioritarios.
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

    // Posicoes internas comecam em zero.
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

    public String tipoPrioridade(int posicao) {
        NoFila no = noNaPosicao(posicao);
        return no == null ? "Nenhuma" : no.tipoPrioridade;
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
