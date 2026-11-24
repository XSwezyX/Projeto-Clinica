
# Sistema de Controle de Pacientes

Projeto 2 – Estrutura de Dados – Universidade Presbiteriana Mackenzie – Sistemas de Informação – Turma 3H – 2026/2.

## Sobre o projeto

Sistema desenvolvido em Java para gerenciar o cadastro de pacientes e organizar os atendimentos de uma clínica médica.

O projeto utiliza:
- Árvore Binária de Busca (ABB), organizada pelo CPF.
- Fila encadeada implementada pelo grupo.
- Leitura e gravação de arquivos TXT.
- Bubble Sort para ordenação alfabética.
- Interface com menu textual.
- Controle de atendimento prioritário.

## Integrantes

- Henrique Haramaki Mataveli - 10752924
- Lukas Barone Sussa - 10753291
- Ryan Silva de Sousa - 10757255

## Como compilar e executar

É necessário possuir o JDK instalado.

Abra o terminal na pasta principal do projeto e execute:

```bash
javac -encoding UTF-8 -d out src/*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

## Funcionalidades do sistema

1. Carregar pacientes do TXT.
2. Consultar paciente por CPF.
3. Registrar chegada para atendimento.
4. Exibir fila de espera.
5. Chamar próximo paciente.
6. Relatório de atendimentos realizados no dia.
7. Listar pacientes em ordem alfabética.
8. Salvar cadastros atualizados em TXT.
9. Consultar funcionalidade adicional do grupo.
10. Exibir integrantes e sair.

## Funcionalidade adicional — Fila prioritária automática 3 para 1

### Objetivo

Garantir atendimento preferencial aos pacientes prioritários, evitando que pacientes comuns aguardem indefinidamente devido à chegada constante de novos pacientes com prioridade.

### Categorias prioritárias

- Idosos com 60 anos ou mais.
- Pessoas com deficiência (PCD).
- Gestantes.
- Lactantes.
- Pessoas com criança de colo.
- Pessoas com mobilidade reduzida.

A prioridade dos idosos é identificada automaticamente pelo sistema através da data de nascimento. As demais condições são informadas durante o registro da chegada.

### Regras de funcionamento

- A prioridade funciona automaticamente desde o início do programa.
- Pacientes prioritários possuem preferência no atendimento.
- A cada três pacientes prioritários atendidos consecutivamente, um paciente comum será chamado, caso exista na fila.
- Após o atendimento de um paciente comum, o contador de prioridades é reiniciado.
- Caso não existam pacientes comuns, os prioritários continuam sendo atendidos normalmente.
- Caso não existam pacientes prioritários, os comuns são atendidos normalmente.
- A ordem de chegada é respeitada dentro de cada categoria.
- Todas as categorias prioritárias possuem o mesmo nível de prioridade.

### Exemplo de atendimento

| Atendimento | Classificação |
|---|---|
| 1º | Prioritário |
| 2º | Prioritário |
| 3º | Prioritário |
| 4º | Comum |
| 5º | Prioritário |
| 6º | Prioritário |
| 7º | Prioritário |
| 8º | Comum |

### Como utilizar

1. Carregar os pacientes utilizando a opção 1.
2. Registrar a chegada dos pacientes utilizando a opção 3.
3. Informar a condição de prioridade, caso exista.
4. Consultar a ordem prevista de atendimento na opção 4.
5. Realizar os atendimentos utilizando a opção 5.
6. Consultar a explicação da funcionalidade adicional na opção 9.

A funcionalidade utiliza a própria fila encadeada implementada pelo grupo, sem utilizar coleções prontas do Java e sem modificar a organização da ABB.

A condição de prioridade é armazenada apenas na fila, pois pode estar relacionada à visita atual do paciente. Dessa forma, o arquivo TXT mantém os seis campos exigidos no projeto.

A proporção de três prioritários para um comum é uma regra proposta pelo grupo para fins acadêmicos, e não um protocolo de classificação de risco médico.

## Regras do sistema

- Limite de 60 chegadas durante a execução do sistema.
- CPF único na ABB.
- Não permite pacientes duplicados na fila.
- Impede o carregamento duplicado do arquivo TXT.
- As opções 2 a 9 somente são liberadas após o carregamento dos pacientes.
- A data da última consulta e a quantidade de consultas são atualizadas após o atendimento.
- Os cadastros alterados devem ser salvos pela opção 8.

## Estrutura do projeto

Projeto-Clinica-main/
├── src/
│   ├── Main.java
│   ├── Clinica.java
│   ├── Paciente.java
│   ├── Node.java
│   ├── ABB.java
│   └── Fila.java
├── dados/
│   ├── pacientes.txt
│   └── pacientes_atualizados.txt
└── README.md

## Responsabilidade das classes

| Classe | Responsabilidade |
|---|---|
| Main | Inicializar a aplicação. |
| Clinica | Gerenciar o menu e as funcionalidades do sistema. |
| Paciente | Armazenar os dados do paciente. |
| Node | Representar um nó da ABB. |
| ABB | Armazenar, organizar e consultar pacientes pelo CPF. |
| Fila | Controlar a espera e a ordem de atendimento dos pacientes. |

## Formato do arquivo TXT

CPF;nome;dataNascimento;telefone;ultimaConsulta;qtdeConsultas

O arquivo de entrada é pacientes.txt.

Os cadastros atualizados são gravados em pacientes_atualizados.txt através da opção 8.
