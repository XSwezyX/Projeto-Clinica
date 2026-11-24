import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Clinica {

    private static final String[] INTEGRANTES = {
        "Henrique Haramaki Mataveli - 10752924",
        "Lukas Barone Sussa - 10753291",
        "Ryan Silva de Sousa - 10757255"
    };

    private static final String ARQUIVO_ENTRADA = "dados/pacientes.txt";
    private static final String ARQUIVO_SAIDA = "dados/pacientes_atualizados.txt";
    private static final int LIMITE_DIARIO = 60;

    private ABB arvore;
    private Fila fila;
    private Scanner teclado;
    private boolean carregado;
    private int chegadasHoje;

    public Clinica() {
        arvore = new ABB();
        fila = new Fila();
        teclado = new Scanner(System.in);
        carregado = false;
        chegadasHoje = 0;
    }

    public void executar() {
        int opcao = 0;
        while (opcao != 10) {
            exibirMenu();
            opcao = lerInteiro("Opção: ");
            System.out.println();

            if (opcao >= 2 && opcao <= 9 && !carregado) {
                System.out.println("Carregue o arquivo TXT primeiro (opção 1).");
            } else if (opcao == 1) {
                carregarPacientes();
            } else if (opcao == 2) {
                consultarPaciente();
            } else if (opcao == 3) {
                registrarChegada();
            } else if (opcao == 4) {
                exibirFila();
            } else if (opcao == 5) {
                chamarProximo();
            } else if (opcao == 6) {
                relatorioDoDia();
            } else if (opcao == 7) {
                listarAlfabetico();
            } else if (opcao == 8) {
                salvarCadastros();
            } else if (opcao == 9) {
                funcionalidadeAdicional();
            } else if (opcao == 10) {
                sair();
            } else {
                System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n===== CONTROLE DE PACIENTES =====");
        System.out.println("1. Carregar pacientes do TXT");
        System.out.println("2. Consultar paciente por CPF");
        System.out.println("3. Registrar chegada para atendimento");
        System.out.println("4. Exibir fila de espera");
        System.out.println("5. Chamar próximo paciente");
        System.out.println("6. Relatório de atendimentos realizados no dia");
        System.out.println("7. Listar pacientes em ordem alfabética");
        System.out.println("8. Salvar cadastros atualizados em TXT");
        System.out.println("9. Consultar regras da fila prioritária (funcionalidade adicional)");
        System.out.println("10. Sair");
    }
    private void carregarPacientes() {
        if (carregado) {
            System.out.println("Os pacientes já foram carregados.");
            return;
        }

        int lidos = 0;
        int ignorados = 0;
        int numeroLinha = 0;

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(new FileInputStream(ARQUIVO_ENTRADA),
                                      StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue;
                }

                Paciente p = lerPaciente(linha);
                if (p == null || !arvore.insere(p)) {
                    ignorados++;
                    System.out.println("Linha " + numeroLinha
                        + " ignorada (formato inválido ou CPF repetido).");
                } else {
                    lidos++;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo " + ARQUIVO_ENTRADA
                + ": " + e.getMessage());
            return;
        }

        carregado = true;
        System.out.println("Pacientes carregados: " + lidos);
        if (ignorados > 0) {
            System.out.println("Linhas ignoradas: " + ignorados);
        }
    }

    private Paciente lerPaciente(String linha) {
        String[] campos = linha.split(";", -1);
        if (campos.length != 6) {
            return null;
        }
        try {
            String cpf = campos[0].trim();
            String nome = campos[1].trim();
            String nascimento = campos[2].trim();
            String telefone = campos[3].trim();
            String ultimaConsulta = campos[4].trim();
            int consultas = Integer.parseInt(campos[5].trim());

            if (!cpfValido(cpf) || nome.isEmpty() || telefone.isEmpty()
                    || !nascimentoValido(nascimento) || consultas < 0
                    || (!ultimaConsulta.isEmpty() && !nascimentoValido(ultimaConsulta))) {
                return null;
            }
            return new Paciente(cpf, nome, nascimento, telefone,
                                ultimaConsulta, consultas);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private void consultarPaciente() {
        String cpf = lerCpfValido("Digite o CPF: ");
        Paciente paciente = arvore.buscar(cpf);
        if (paciente == null) {
            System.out.println("Paciente não cadastrado.");
        } else {
            System.out.println(paciente);
        }
    }
    private void registrarChegada() {
        if (chegadasHoje >= LIMITE_DIARIO) {
            System.out.println("Limite de 60 chegadas no dia atingido.");
            return;
        }

        String cpf = lerCpfValido("Digite o CPF (11 dígitos): ");
        if (fila.contem(cpf)) {
            System.out.println("Paciente já está na fila.");
            return;
        }

        Paciente paciente = arvore.buscar(cpf);
        if (paciente == null) {
            paciente = cadastrarPaciente(cpf);
            if (paciente == null) {
                return;
            }
        }
        System.out.println("Paciente: " + paciente.getNome());

        int idade = calcularIdade(paciente.getDataNascimento());
        boolean idoso = idade >= 60;
        if (idoso) {
            System.out.println("Prioridade automática: idoso de " + idade + " anos.");
        }

        String condicao = lerCondicaoPrioritaria();
        String tipoPrioridade;
        if (idoso && !condicao.equals("Nenhuma")) {
            tipoPrioridade = condicao + " e Idoso (60 anos ou mais)";
        } else if (idoso) {
            tipoPrioridade = "Idoso (60 anos ou mais)";
        } else {
            tipoPrioridade = condicao;
        }
        boolean prioritario = !tipoPrioridade.equals("Nenhuma");
        fila.enfileirar(cpf, tipoPrioridade);
        chegadasHoje++;

        System.out.println("Chegada registrada com sucesso!");
        System.out.println("Classificação: "
            + (prioritario ? "PRIORITÁRIO" : "COMUM"));
        System.out.println("Tipo de prioridade: " + tipoPrioridade);
        System.out.println("Posição prevista na fila: " + fila.posicaoDe(cpf));
    }

    private String lerCondicaoPrioritaria() {
        while (true) {
            System.out.println("Condição para prioridade nesta chegada:");
            System.out.println("0. Nenhuma condição adicional");
            System.out.println("1. PCD");
            System.out.println("2. Gestante");
            System.out.println("3. Lactante");
            System.out.println("4. Pessoa com criança de colo");
            System.out.println("5. Mobilidade reduzida");
            int opcao = lerInteiro("Categoria: ");
            if (opcao == 0) return "Nenhuma";
            if (opcao == 1) return "PCD";
            if (opcao == 2) return "Gestante";
            if (opcao == 3) return "Lactante";
            if (opcao == 4) return "Pessoa com criança de colo";
            if (opcao == 5) return "Mobilidade reduzida";
            System.out.println("Categoria inválida. Digite de 0 a 5.");
        }
    }
    private void exibirFila() {
        if (fila.estaVazia()) {
            System.out.println("Não existem pacientes aguardando atendimento.");
            return;
        }
        System.out.println("===== FILA DE ESPERA =====");
        System.out.println("Modo: FILA PRIORITÁRIA AUTOMÁTICA");
        System.out.println("Regra: 3 prioritários para 1 comum");
        int[] ordem = fila.ordemAtendimento();
        for (int i = 0; i < ordem.length; i++) {
            int posicao = ordem[i];
            String cpf = fila.obter(posicao);
            Paciente paciente = arvore.buscar(cpf);
            System.out.println("Posição: " + (i + 1));
            System.out.println("CPF: " + cpf);
            if (paciente != null) {
                System.out.println("Nome: " + paciente.getNome());
            }
            System.out.println("Classificação: "
                + (fila.ehPrioritario(posicao) ? "PRIORITÁRIO" : "COMUM"));
            System.out.println("Tipo de prioridade: " + fila.tipoPrioridade(posicao));
            System.out.println("------------------------");
        }
        System.out.println("Total de pacientes: " + fila.tamanho());
    }
    private void chamarProximo() {
        if (fila.estaVazia()) {
            System.out.println("Não existem pacientes na fila.");
            return;
        }
        int posicao = fila.ordemAtendimento()[0];
        String tipoPrioridade = fila.tipoPrioridade(posicao);
        String cpf = fila.desenfileirar();
        Paciente paciente = arvore.buscar(cpf);
        if (paciente == null) {
            System.out.println("Paciente não encontrado na ABB.");
            return;
        }
        String hoje = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        paciente.registrarConsulta(hoje);
        System.out.println("===== PACIENTE CHAMADO =====");
        System.out.println(paciente);
        System.out.println("Tipo de prioridade: " + tipoPrioridade);
        System.out.println("Data da consulta registrada: " + hoje);
        System.out.println("Atendimento concluído!");
    }
    private void relatorioDoDia() {
        String hoje = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        System.out.println("===== RELATÓRIO DE ATENDIMENTOS DO DIA =====");
        System.out.println("Data: " + hoje);
        int total = exibirAtendidosDoDia(arvore.root(), hoje, 0);
        if (total == 0) {
            System.out.println("Nenhum atendimento realizado hoje.");
        }
        System.out.println("Total de pacientes atendidos: " + total);
    }

    private int exibirAtendidosDoDia(Node no, String hoje, int total) {
        if (no == null) {
            return total;
        }
        total = exibirAtendidosDoDia(no.getEsq(), hoje, total);
        Paciente paciente = no.getDado();
        if (hoje.equals(paciente.getUltimaConsulta())) {
            System.out.println("Nome: " + paciente.getNome());
            System.out.println("CPF: " + paciente.getCpf());
            System.out.println("Quantidade de consultas: "
                + paciente.getQuantidadeConsultas());
            System.out.println("------------------------------");
            total++;
        }
        return exibirAtendidosDoDia(no.getDir(), hoje, total);
    }
    private void listarAlfabetico() {
        int quantidade = arvore.tamanho();
        if (quantidade == 0) {
            System.out.println("Não existem pacientes cadastrados.");
            return;
        }
        Paciente[] pacientes = new Paciente[quantidade];
        int posicao = copiarPacientes(arvore.root(), pacientes, 0);

        for (int i = 0; i < posicao - 1; i++) {
            for (int j = 0; j < posicao - 1 - i; j++) {
                if (pacientes[j].getNome().compareToIgnoreCase(
                        pacientes[j + 1].getNome()) > 0) {
                    Paciente auxiliar = pacientes[j];
                    pacientes[j] = pacientes[j + 1];
                    pacientes[j + 1] = auxiliar;
                }
            }
        }
        System.out.println("===== PACIENTES EM ORDEM ALFABÉTICA =====");
        for (int i = 0; i < posicao; i++) {
            System.out.println("Nome: " + pacientes[i].getNome());
            System.out.println("CPF: " + pacientes[i].getCpf());
            System.out.println("------------------------------");
        }
        System.out.println("Total de pacientes: " + posicao);
    }

    private int copiarPacientes(Node no, Paciente[] pacientes, int posicao) {
        if (no == null) {
            return posicao;
        }
        posicao = copiarPacientes(no.getEsq(), pacientes, posicao);
        pacientes[posicao] = no.getDado();
        posicao++;
        return copiarPacientes(no.getDir(), pacientes, posicao);
    }
    private void salvarCadastros() {
        File pasta = new File("dados");
        if (!pasta.exists() && !pasta.mkdirs()) {
            System.out.println("Não foi possível criar a pasta dados.");
            return;
        }
        try (BufferedWriter escritor = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ARQUIVO_SAIDA),
                                       StandardCharsets.UTF_8))) {
            salvarEmOrdem(arvore.root(), escritor);
        } catch (IOException e) {
            System.out.println("Erro ao salvar os pacientes: " + e.getMessage());
            return;
        }
        System.out.println("Cadastros salvos com sucesso em: " + ARQUIVO_SAIDA);
        System.out.println("Total de pacientes salvos: " + arvore.tamanho());
    }

    private void salvarEmOrdem(Node no, BufferedWriter escritor)
            throws IOException {
        if (no == null) {
            return;
        }
        salvarEmOrdem(no.getEsq(), escritor);
        Paciente p = no.getDado();
        escritor.write(p.getCpf() + ";" + p.getNome() + ";"
            + p.getDataNascimento() + ";" + p.getTelefone() + ";"
            + p.getUltimaConsulta() + ";" + p.getQuantidadeConsultas());
        escritor.newLine();
        salvarEmOrdem(no.getDir(), escritor);
    }
    private void funcionalidadeAdicional() {
        System.out.println("===== FUNCIONALIDADE ADICIONAL DO GRUPO =====");

        System.out.println("\nNome: Fila prioritária automática 3 para 1.");

        System.out.println("\nObjetivo:");
        System.out.println("Garantir atendimento preferencial aos pacientes prioritários,");
        System.out.println("evitando que pacientes comuns aguardem indefinidamente.");

        System.out.println("\nQuem possui prioridade:");
        System.out.println("- Idosos com 60 anos ou mais;");
        System.out.println("- Pessoas com deficiência (PCD);");
        System.out.println("- Gestantes;");
        System.out.println("- Lactantes;");
        System.out.println("- Pessoas com criança de colo;");
        System.out.println("- Pessoas com mobilidade reduzida.");

        System.out.println("\nRegras de funcionamento:");
        System.out.println("1. A prioridade funciona automaticamente desde o início.");
        System.out.println("2. Pacientes prioritários possuem preferência no atendimento.");
        System.out.println("3. A cada 3 prioritários atendidos consecutivamente,");
        System.out.println("   será chamado 1 paciente comum, caso exista na fila.");
        System.out.println("4. Após atender um paciente comum, a contagem é reiniciada.");
        System.out.println("5. Se não houver pacientes de uma categoria,");
        System.out.println("   os pacientes da outra categoria serão atendidos normalmente.");
        System.out.println("6. A ordem de chegada dentro de cada categoria é respeitada.");
        System.out.println("7. Todas as condições prioritárias têm o mesmo nível de prioridade.");

        System.out.println("\nComo utilizar:");
        System.out.println("Na opção 3, o sistema identifica automaticamente os idosos");
        System.out.println("pela data de nascimento. As demais condições de prioridade");
        System.out.println("podem ser informadas durante o registro da chegada.");

        System.out.println("\nExemplo de sequência de atendimento:");
        System.out.println("Prioritário -> Prioritário -> Prioritário -> Comum");

        System.out.println("\nEssa funcionalidade busca equilibrar a preferência");
        System.out.println("dos pacientes prioritários com a organização da fila,");
        System.out.println("evitando que pacientes comuns aguardem indefinidamente.");

        System.out.println("=============================================");
    }

    private void sair() {
        System.out.println("Os dados alterados somente serão preservados em arquivo "
            + "se a opção 8 tiver sido utilizada.");
        System.out.println("Grupo:");
        for (int i = 0; i < INTEGRANTES.length; i++) {
            System.out.println(" - " + INTEGRANTES[i]);
        }
    }
    private Paciente cadastrarPaciente(String cpf) {
        System.out.println("Paciente não cadastrado. Realize o cadastro.");
        if (!cpfValido(cpf)) {
            System.out.println("CPF inválido. Informe exatamente 11 dígitos.");
            return null;
        }
        String nome = lerCampoObrigatorio("Nome: ", "Nome");
        String nascimento = lerNascimentoValido();
        String telefone = lerCampoObrigatorio("Telefone: ", "Telefone");

        Paciente paciente = new Paciente(cpf, nome, nascimento, telefone, "", 0);
        if (!arvore.insere(paciente)) {
            System.out.println("Não foi possível cadastrar o paciente.");
            return null;
        }
        System.out.println("Paciente cadastrado com sucesso!");
        return paciente;
    }
    private String lerCpfValido(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String cpf = teclado.nextLine().trim();
            if (cpfValido(cpf)) {
                return cpf;
            }
            System.out.println("CPF inválido. Informe exatamente 11 dígitos.");
        }
    }
    private String lerCampoObrigatorio(String mensagem, String nomeCampo) {
        while (true) {
            System.out.print(mensagem);
            String valor = teclado.nextLine().trim();
            if (!valor.isEmpty() && !valor.contains(";")) {
                return valor;
            }
            System.out.println(nomeCampo
                + " inválido. Preencha o campo sem ponto e vírgula.");
        }
    }
    private String lerNascimentoValido() {
        while (true) {
            System.out.print("Data de nascimento (DD/MM/AAAA): ");
            String nascimento = teclado.nextLine().trim();
            if (nascimentoValido(nascimento)) {
                return nascimento;
            }
            System.out.println("Data de nascimento inválida. Use DD/MM/AAAA.");
        }
    }

    private boolean cpfValido(String cpf) {
        return cpf.matches("[0-9]{11}");
    }

    private boolean nascimentoValido(String nascimento) {
        if (!nascimento.matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
            return false;
        }
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        formato.setLenient(false);
        try {
            Date data = formato.parse(nascimento);
            return !data.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
    private int calcularIdade(String nascimento) {
        if (!nascimentoValido(nascimento)) {
            return -1;
        }
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);
            Calendar aniversario = Calendar.getInstance();
            aniversario.setTime(formato.parse(nascimento));
            Calendar hoje = Calendar.getInstance();

            int idade = hoje.get(Calendar.YEAR) - aniversario.get(Calendar.YEAR);
            if (hoje.get(Calendar.MONTH) < aniversario.get(Calendar.MONTH)
                    || (hoje.get(Calendar.MONTH) == aniversario.get(Calendar.MONTH)
                    && hoje.get(Calendar.DAY_OF_MONTH)
                        < aniversario.get(Calendar.DAY_OF_MONTH))) {
                idade--;
            }
            return idade;
        } catch (ParseException e) {
            return -1;
        }
    }

    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        try {
            return Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
