public class NoFila {
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
