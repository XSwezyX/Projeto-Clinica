
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

/**
 * Funcionalidade adicional: FILA PRIORITARIA AUTOMATICA.
 * A fila ja organiza os atendimentos por prioridade desde a primeira chegada.
 * Na opcao 3, o programa identifica idosos de 60 anos ou mais pela data de
 * nascimento e permite informar outras condicoes: PCD, gestante, lactante,
 * pessoa com crianca de colo ou mobilidade reduzida.
 * A opcao 9 apenas explica como funciona, sem ativar nem desativar nada.
 * A opcao 4 mostra o resultado e a opcao 5 chama o proximo paciente.
 */
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
        System.out.println("9. Conhecer a fila prioritária (funcionalidade adicional)");
        System.out.println("10. Sair");
    }

    // OPCAO 1: carrega arquivo de seis campos na ABB por CPF.
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

    // OPCAO 2: consulta sem alterar a ABB.
    private void consultarPaciente() {
        String cpf = lerCpfValido("Digite o CPF: ");
        Paciente paciente = arvore.buscar(cpf);
        if (paciente == null) {
            System.out.println("Paciente não cadastrado.");
        } else {
            System.out.println(paciente);
        }
    }

    // OPCAO 3: registra chegada. A prioridade fica vinculada a esta chegada,
    // e nao ao cadastro permanente do paciente (arquivo de seis campos).
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

        // A prioridade fica sempre ativa e organiza a fila na propria chegada.
        fila.enfileirar(cpf, tipoPrioridade);
        chegadasHoje++;

        System.out.println("Chegada registrada com sucesso!");
        System.out.println("Classificação: "
            + (prioritario ? "PRIORITÁRIO" : "COMUM"));
        System.out.println("Tipo de prioridade: " + tipoPrioridade);
        System.out.println("Posição na fila: " + fila.posicaoDe(cpf));
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

    // OPCAO 4: exibe a ordem real da fila.
    private void exibirFila() {
        if (fila.estaVazia()) {
            System.out.println("Não existem pacientes aguardando atendimento.");
            return;
        }
        System.out.println("===== FILA DE ESPERA =====");
        System.out.println("Modo: FILA PRIORITÁRIA AUTOMÁTICA");
        for (int i = 0; i < fila.tamanho(); i++) {
            String cpf = fila.obter(i);
            Paciente paciente = arvore.buscar(cpf);
            System.out.println("Posição: " + (i + 1));
            System.out.println("CPF: " + cpf);
            if (paciente != null) {
                System.out.println("Nome: " + paciente.getNome());
            }
            System.out.println("Classificação: "
                + (fila.ehPrioritario(i) ? "PRIORITÁRIO" : "COMUM"));
            System.out.println("Tipo de prioridade: " + fila.tipoPrioridade(i));
            System.out.println("------------------------");
        }
        System.out.println("Total de pacientes: " + fila.tamanho());
    }

    // OPCAO 5: retira o primeiro da fila e atualiza o cadastro na ABB.
    private void chamarProximo() {
        if (fila.estaVazia()) {
            System.out.println("Não existem pacientes na fila.");
            return;
        }
        // Guardar a condicao antes de remover o no da fila.
        String tipoPrioridade = fila.tipoPrioridade(0);
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

    // OPCAO 6: relatorio conforme o estado dos cadastros (nao e historico).
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

    // OPCAO 7: vetor auxiliar e Bubble Sort, sem mudar a ABB.
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

    // OPCAO 8: percorre a ABB e grava exatamente os seis campos obrigatorios.
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
            System.out.println("Cadastros salvos com sucesso em: " + ARQUIVO_SAIDA);
            System.out.println("Total de pacientes salvos: " + arvore.tamanho());
        } catch (IOException e) {
            System.out.println("Erro ao salvar os pacientes: " + e.getMessage());
        }
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

    // OPCAO 9: somente informa, a funcionalidade JA esta ativa por padrao.
    private void funcionalidadeAdicional() {
        System.out.println("===== FUNCIONALIDADE ADICIONAL DO GRUPO =====");
        System.out.println("Nome: Fila prioritária automática.");
        System.out.println("Finalidade: atender pacientes prioritários antes dos comuns,");
        System.out.println("mantendo a ordem de chegada dentro de cada grupo.");
        System.out.println("A funcionalidade funciona automaticamente desde o início.");
        System.out.println("Na opção 3, a idade de 60 anos ou mais é reconhecida");
        System.out.println("automaticamente pela data de nascimento; também é possível");
        System.out.println("informar PCD, gestante, lactante, pessoa com criança de colo");
        System.out.println("ou mobilidade reduzida para a chegada atual.");
        System.out.println("A opção 4 mostra a ordem real da fila e a opção 5 chama");
        System.out.println("o próximo paciente, indicando seu tipo de prioridade.");
        System.out.println("Nenhuma opção precisa ser ativada ou desativada.");
    }

    // OPCAO 10
    private void sair() {
        System.out.println("Grupo:");
        for (int i = 0; i < INTEGRANTES.length; i++) {
            System.out.println(" - " + INTEGRANTES[i]);
        }
        System.out.println("Atenção: os cadastros alterados só são preservados em TXT");
    }

    // AUXILIARES
    private Paciente cadastrarPaciente(String cpf) {
        System.out.println("Paciente não cadastrado. Realize o cadastro.");
        if (!cpfValido(cpf)) {
            System.out.println("CPF inválido. Informe exatamente 11 dígitos.");
            return null;
        }
        // Valida cada campo imediatamente: se houver erro, pede o mesmo campo
        // novamente sem abandonar o cadastro nem voltar ao menu principal.
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

    // Repete a pergunta até o usuário fornecer um CPF no formato esperado.
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

    // Não deixa nome ou telefone vazios, nem com o separador usado no TXT.
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

    // Uma data inválida não cancela o cadastro; solicita a data novamente.
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

    // Calcula a idade, considerando dia e mes de aniversario.
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
