# Sistema de Controle de Pacientes

Projeto 2 – Estrutura de Dados – Mackenzie (Sistemas de Informação) – Turma 3H – 2026/2.

Aplicação Java em menu textual, com **Árvore Binária de Busca (ABB) por CPF**, **fila encadeada própria** e leitura/gravação de arquivos TXT. Não utiliza `Queue`, `LinkedList`, `TreeMap` nem `TreeSet`.

## Integrantes

- Henrique Haramaki Mataveli - 10752924
- Lukas Barone Sussa - 10753291
- Ryan Silva de Sousa - 10757255

## Como compilar e executar

Abra o terminal na **pasta `Projeto-Clinica-main`** (que contém `src` e `dados`) e execute:

```bash
javac -encoding UTF-8 -d out src/*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

No Windows com PowerShell, os comandos também funcionam. É necessário ter o JDK instalado.

## Menu implementado

1. Carregar pacientes do TXT (ABB por CPF, sem duplicatas).
2. Consultar paciente por CPF.
3. Registrar chegada (cadastrar paciente novo, impedir repetição na fila e limitar a 60 chegadas).
4. Exibir fila de espera.
5. Chamar o primeiro e atualizar última consulta/quantidade na ABB.
6. Relatório dos pacientes com última consulta na data atual.
7. Listar alfabeticamente usando vetor auxiliar e Bubble Sort.
8. Salvar cadastros no TXT atualizado.
9. Ativar fila prioritária (funcionalidade adicional do grupo).
10. Exibir integrantes e sair.

## Opção 8 – Salvar cadastros atualizados

Grava todos os pacientes da ABB, em ordem de CPF, no arquivo **`dados/pacientes_atualizados.txt`**, preservando o formato exigido, sem campos extras:

`CPF;nome;dataNascimento;telefone;ultimaConsulta;qtdeConsultas`

A gravação **não sobrescreve** o arquivo de entrada `dados/pacientes.txt`. Para reutilizar os dados atualizados numa nova execução, substitua o conteúdo de `dados/pacientes.txt` pelo conteúdo de `dados/pacientes_atualizados.txt` antes de abrir o programa. Evite fazer isso antes de concluir a demonstração, caso queira manter os exemplos originais.

## Opção 9 – Funcionalidade adicional: fila prioritária

**Finalidade:** reorganizar os pacientes que aguardam, colocando quem tem prioridade antes dos demais. A implementação usa a própria fila encadeada, sem coleções prontas e sem alterar a ABB.

**Como usar:**

1. Use a opção **1** para carregar os pacientes e a opção **3** para registrar chegadas.
2. Ao registrar cada chegada, o programa calcula automaticamente se o paciente possui **60 anos ou mais** pela data de nascimento, e pergunta se há outra condição de prioridade **nesta chegada**: PCD, gestante, lactante, pessoa com criança de colo ou mobilidade reduzida. É possível selecionar `0` se nenhuma dessas condições se aplica.
3. Inicialmente, a opção **3** enfileira por chegada, conforme o enunciado. Selecione **9** para ativar o modo prioritário: os prioritários já aguardando são colocados antes dos demais.
4. Após a ativação, as próximas chegadas são inseridas automaticamente na posição apropriada. A opção **4** exibe a sequência real e a opção **5** chama o primeiro dessa sequência.

**Critério de desempate:** dentro de cada grupo (prioritário ou comum), prevalece a **ordem de chegada**. Não há distinção de nível entre as categorias prioritárias. A opção 9 permanece ativa até o programa encerrar.

**Por que não existe campo `tipo` no TXT principal?** A idade é calculada com a data de nascimento já existente, sem guardar a informação “60+”. Outras condições são verificadas na recepção, pois podem se referir somente à visita atual. Essa condição fica no nó da fila (daquela chegada), não na ficha permanente, mantendo os **seis campos** obrigatórios no TXT. A aplicação demonstra uma regra simples de prioridade para o projeto acadêmico, não um protocolo de classificação de risco médico.

**Exemplo para demonstrar:** carregue pacientes, registre Ana (não prioritária), Daniel (idade >= 60), e Bruno (não prioritário). Antes de ativar, a ordem é Ana → Daniel → Bruno. Após selecionar **9**, passa a ser Daniel → Ana → Bruno. Outro paciente PCD que chegar depois, caso a fila prioritária já esteja ativada, entra depois de Daniel e antes de Ana.

## Arquivos

```text
Projeto-Clinica-main/
├── src/
│   ├── Main.java
│   ├── Clinica.java
│   ├── Paciente.java
│   ├── Node.java
│   ├── ABB.java
│   └── Fila.java
├── dados/
│   └── pacientes.txt
└── README.md
```

Execute os comandos na pasta principal para o Java encontrar os caminhos relativos de `dados`.
