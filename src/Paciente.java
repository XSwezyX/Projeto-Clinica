/**
 * Paciente cadastrado na clínica.
 * Formato no TXT: CPF;nome;dataNascimento;telefone;ultimaConsulta;qtdeConsultas
 */
public class Paciente {
    private String cpf;               // chave da ABB
    private String nome;
    private String dataNascimento;    // DD/MM/AAAA
    private String telefone;
    private String ultimaConsulta;    // DD/MM/AAAA ou "" (vazio = nunca consultou)
    private int quantidadeConsultas;

    public Paciente(String cpf, String nome, String dataNascimento, String telefone,
                    String ultimaConsulta, int quantidadeConsultas) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.ultimaConsulta = ultimaConsulta;
        this.quantidadeConsultas = quantidadeConsultas;
    }

    public String getCpf() { return cpf; }
    public String getNome() { return nome; }
    public String getDataNascimento() { return dataNascimento; }
    public String getTelefone() { return telefone; }
    public String getUltimaConsulta() { return ultimaConsulta; }
    public int getQuantidadeConsultas() { return quantidadeConsultas; }


    public void registrarConsulta(String data) {
    ultimaConsulta = data;
    quantidadeConsultas++;
}
    @Override
    public String toString() {
        String ultima = ultimaConsulta.isEmpty() ? "nenhuma" : ultimaConsulta;
        return "Nome: " + nome + "\n"
             + "CPF: " + cpf + "\n"
             + "Data de nascimento: " + dataNascimento + "\n"
             + "Telefone: " + telefone + "\n"
             + "Última consulta: " + ultima + "\n"
             + "Quantidade de consultas: " + quantidadeConsultas;
    }
}
