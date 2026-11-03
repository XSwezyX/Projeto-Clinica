import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Clinica {
    private static final String[] INTEGRANTES = {"Integrante 1", "Integrante 2", "Integrante 3"};
    private static final String ARQUIVO_ENTRADA = "dados/pacientes.txt";

    private ABB arvore;
    private Fila fila;
    private Scanner teclado;
    private boolean carregado;

    public Clinica() {
        arvore = new ABB();
        fila = new Fila();
        teclado = new Scanner(System.in);
        carregado = false;
    }

    // --------- MENU

    public void executar() {
        int opcao = 0;
        while (opcao != 10) {
            exibirMenu();
            opcao = lerInteiro("Opção: ");
            System.out.println();

            if (opcao >= 2 && opcao <= 9 && !carregado) {
                // Regra 7 do enunciado: opções 2 a 9 só após carregar o TXT
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

    // ------------------------------------------------- OPÇÕES

    /** Opção 1: lê o TXT, cria os Pacientes e insere cada um na ABB pelo CPF. */
    private void carregarPacientes() {
        if (carregado) {
            System.out.println("Os pacientes já foram carregados. Nova carga bloqueada para evitar duplicidades.");
            return;
        }

        int lidos = 0;
        int ignorados = 0;
        int numeroLinha = 0;

        try (BufferedReader leitor = new BufferedReader(
                new InputStreamReader(new FileInputStream(ARQUIVO_ENTRADA), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.trim().isEmpty()) {
                    continue; // ignora linhas em branco
                }
                Paciente p = lerPaciente(linha);
                if (p == null || !arvore.insere(p)) {
                    ignorados++;
                    System.out.println("Linha " + numeroLinha + " ignorada (formato inválido ou CPF repetido).");
                } else {
                    lidos++;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo " + ARQUIVO_ENTRADA + ": " + e.getMessage());
            return; // não libera as demais opções
        }

        carregado = true;
        System.out.println("Pacientes carregados: " + lidos);
        if (ignorados > 0) {
            System.out.println("Linhas ignoradas: " + ignorados);
        }
    }

    /** Converte uma linha do TXT em um Paciente. Devolve null se o formato estiver errado. */
    private Paciente lerPaciente(String linha) {
        // split com -1 mantém campos vazios (ex.: ultimaConsulta em branco)
        String[] campos = linha.split(";", -1);
        if (campos.length != 6) {
            return null;
        }
        try {
            int qtde = Integer.parseInt(campos[5].trim());
            return new Paciente(
                    campos[0].trim(), // CPF
                    campos[1].trim(), // nome
                    campos[2].trim(), // data de nascimento
                    campos[3].trim(), // telefone
                    campos[4].trim(), // última consulta
                    qtde);            // quantidade de consultas
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Opção 2: pede o CPF, busca na ABB e mostra os dados (ou avisa que não existe). */
    private void consultarPaciente() {
    boolean encontrado = false;
    do{
        System.out.print("Digite o CPF (caso queira sair, digite 0):");
        String cpf = teclado.nextLine().trim();
        if(cpf.equals("0")){
            return;
    }


    Paciente p = arvore.buscar(cpf);
        if (p == null) {
            System.out.println("Cadastro não encontrado para o CPF " + cpf + ".");
        } else {
            System.out.println(p);
            encontrado = true;
        }
        }while(!encontrado);
    }

    private void registrarChegada() {
        // TODO: pedir CPF, buscar na ABB (cadastrar se for novo), enfileirar,
        //       respeitar limite de 60 por dia e CPF repetido na fila.
        System.out.println("[opção 3 ainda não implementada]");
    }

    private void exibirFila() {
        // TODO: mostrar posição, CPF e nome (buscado na ABB); avisar se estiver vazia.
        System.out.println("[opção 4 ainda não implementada]");
    }

    private void chamarProximo() {
        // TODO: desenfileirar, buscar na ABB, atualizar última consulta (data de hoje) e quantidade.
        System.out.println("[opção 5 ainda não implementada]");
    }

    private void relatorioDoDia() {
        // TODO: percorrer a ABB e listar quem tem última consulta = data de hoje; mostrar total.
        System.out.println("[opção 6 ainda não implementada]");
    }

    private void listarAlfabetico() {
        // TODO: copiar a ABB para um vetor auxiliar e ordenar por nome (Bubble ou Insertion Sort).
        System.out.println("[opção 7 ainda não implementada]");
    }

    private void salvarCadastros() {
        // TODO: percorrer a ABB e gravar em pacientes_atualizados.txt (mesmo formato da entrada).
        System.out.println("[opção 8 ainda não implementada]");
    }

    private void funcionalidadeAdicional() {
        // TODO: operação proposta pelo grupo, usando a ABB ou a fila.
        System.out.println("[opção 9 ainda não implementada]");
    }

    private void sair() {
        System.out.println("Grupo:");
        for (int i = 0; i < INTEGRANTES.length; i++) {
            System.out.println(" - " + INTEGRANTES[i]);
        }
        System.out.println("Atenção: os dados alterados só são preservados em arquivo se a opção 8 foi utilizada.");
    }

    // ---------------------------------------------------------- AUXILIARES

    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        try {
            return Integer.parseInt(teclado.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
