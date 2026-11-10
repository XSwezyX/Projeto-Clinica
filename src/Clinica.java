
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Clinica {

    private static final String[] INTEGRANTES = {
        "Integrante 1", "Integrante 2", "Integrante 3"
    };

    private static final String ARQUIVO_ENTRADA = "dados/pacientes.txt";

    private ABB arvore;
    private Fila fila;
    private Scanner teclado;
    private boolean carregado;
    private int chegadasHoje;

    private static final int LIMITE_DIARIO = 60;

    public Clinica() {
        arvore = new ABB();
        fila = new Fila();
        teclado = new Scanner(System.in);
        carregado = false;
        chegadasHoje = 0;
    }

    // --------- MENU

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
        System.out.println("9. Funcionalidade adicional do grupo");
        System.out.println("10. Sair");
    }

    // -------------------------------------------------
    // OPÇÃO 1

    private void carregarPacientes() {

        if (carregado) {
            System.out.println("Os pacientes já foram carregados.");
            return;
        }

        int lidos = 0;
        int ignorados = 0;
        int numeroLinha = 0;

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(ARQUIVO_ENTRADA),
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

                    System.out.println(
                        "Linha " + numeroLinha +
                        " ignorada (formato inválido ou CPF repetido)."
                    );

                } else {
                    lidos++;
                }
            }

        } catch (IOException e) {

            System.out.println(
                "Erro ao ler o arquivo " +
                ARQUIVO_ENTRADA + ": " + e.getMessage()
            );

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

            int qtde = Integer.parseInt(campos[5].trim());

            return new Paciente(
                campos[0].trim(),
                campos[1].trim(),
                campos[2].trim(),
                campos[3].trim(),
                campos[4].trim(),
                qtde
            );

        } catch (NumberFormatException e) {
            return null;
        }
    }

    // -------------------------------------------------
    // OPÇÃO 2

    private void consultarPaciente() {

        boolean encontrado = false;

        do {

            System.out.print("Digite o CPF (0 para sair): ");
            String cpf = teclado.nextLine().trim();

            if (cpf.equals("0")) {
                return;
            }

            Paciente p = arvore.buscar(cpf);

            if (p == null) {

                System.out.println(
                    "Cadastro não encontrado para o CPF " + cpf + "."
                );

            } else {

                System.out.println(p);
                encontrado = true;
            }

        } while (!encontrado);
    }

    // -------------------------------------------------
    // OPÇÃO 3 - REGISTRAR CHEGADA

    private void registrarChegada() {

        if (chegadasHoje >= LIMITE_DIARIO) {

            System.out.println("Limite de 60 pacientes atingido.");
            return;
        }

        System.out.print("Digite o CPF: ");
        String cpf = teclado.nextLine().trim();

        if (fila.contem(cpf)) {

            System.out.println("Paciente já está na fila.");
            return;
        }

        Paciente paciente = arvore.buscar(cpf);

        if (paciente == null) {

            System.out.println("Paciente não cadastrado. Realize o cadastro.");

            System.out.print("Nome: ");
            String nome = teclado.nextLine();

            System.out.print("Data de nascimento: ");
            String nascimento = teclado.nextLine();

            System.out.print("Telefone: ");
            String telefone = teclado.nextLine();

            paciente = new Paciente(
                cpf,
                nome,
                nascimento,
                telefone,
                "",
                0
            );

            boolean inserido = arvore.insere(paciente);

            if (!inserido) {

                System.out.println("Não foi possível cadastrar o paciente.");
                return;
            }

            System.out.println("Paciente cadastrado com sucesso!");
        }

        fila.enfileirar(cpf);

        chegadasHoje++;

        System.out.println("Chegada registrada com sucesso!");
    }

    // -------------------------------------------------
    // OPÇÃO 4 - EXIBIR FILA

    private void exibirFila() {

        if (fila.estaVazia()) {

            System.out.println("A fila está vazia.");
            return;
        }

        System.out.println("===== FILA DE ESPERA =====");

        for (int i = 0; i < fila.tamanho(); i++) {

            String cpf = fila.obter(i);

            Paciente paciente = arvore.buscar(cpf);

            System.out.println("Posição: " + (i + 1));
            System.out.println("CPF: " + cpf);

            if (paciente != null) {
                System.out.println("Nome: " + paciente.getNome());
            }

            System.out.println("------------------------");
        }

        System.out.println(
            "Total de pacientes: " + fila.tamanho()
        );
    }

    // -------------------------------------------------
    // OPÇÃO 5 - CHAMAR PRÓXIMO PACIENTE

    private void chamarProximo() {

        if (fila.estaVazia()) {

            System.out.println("Não existem pacientes na fila.");
            return;
        }

        String cpf = fila.desenfileirar();

        Paciente paciente = arvore.buscar(cpf);

        if (paciente == null) {

            System.out.println("Paciente não encontrado na ABB.");
            return;
        }

        System.out.print("Digite a data da consulta (DD/MM/AAAA): ");

        String hoje = teclado.nextLine();

        paciente.registrarConsulta(hoje);

        System.out.println("===== PACIENTE CHAMADO =====");
        System.out.println(paciente);
        System.out.println("Atendimento iniciado!");
    }

    // -------------------------------------------------
    // OPÇÃO 6

    private void relatorioDoDia() {

        System.out.println("[opção 6 ainda não implementada]");
    }

    // -------------------------------------------------
    // OPÇÃO 7

    private void listarAlfabetico() {

        System.out.println("[opção 7 ainda não implementada]");
    }

    // -------------------------------------------------
    // OPÇÃO 8

    private void salvarCadastros() {

        System.out.println("[opção 8 ainda não implementada]");
    }

    // -------------------------------------------------
    // OPÇÃO 9

    private void funcionalidadeAdicional() {

        System.out.println("[opção 9 ainda não implementada]");
    }

    // -------------------------------------------------

    private void sair() {

        System.out.println("Grupo:");

        for (int i = 0; i < INTEGRANTES.length; i++) {
            System.out.println(" - " + INTEGRANTES[i]);
        }

        System.out.println(
            "Atenção: os dados alterados só são preservados " +
            "em arquivo se a opção 8 foi utilizada."
        );
    }

    // -------------------------------------------------
    // AUXILIARES

    private int lerInteiro(String mensagem) {

        System.out.print(mensagem);

        try {

            return Integer.parseInt(teclado.nextLine().trim());

        } catch (NumberFormatException e) {

            return -1;
        }
    }
}
