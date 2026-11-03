# Sistema de Controle de Pacientes

Projeto 2 – Estrutura de Dados – Mackenzie (Sistemas de Informação) – Turma 3H – 2026/2

Sistema em Java (menu textual) com ABB por CPF, Fila de chegada e arquivos TXT.

## Integrantes

- Henrique Haramaki Mataveli - 10752924
- Lukas Barone Sussa - 10753291
- Ryan Silva de Sousa - 10757255

## Estrutura

```
├── src/
│   ├── Main.java       # ponto de entrada
│   ├── Clinica.java    # menu e regras de negócio
│   ├── Paciente.java   # dados do paciente
│   ├── Node.java       # nó da ABB
│   ├── ABB.java        # árvore binária de busca (chave: CPF)
│   └── Fila.java       # fila de CPFs
├── dados/pacientes.txt # entrada de exemplo
├── .gitignore
└── README.md
```

## Como compilar e executar

```bash
javac -encoding UTF-8 -d out src/*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

## Andamento

- [x] Menu principal
- [x] 1. Carregar pacientes do TXT
- [x] 2. Consultar paciente por CPF
- [ ] 3. Registrar chegada
- [ ] 4. Exibir fila
- [ ] 5. Chamar próximo
- [ ] 6. Relatório do dia
- [ ] 7. Ordem alfabética
- [ ] 8. Salvar TXT
- [ ] 9. Funcionalidade adicional
- [x] 10. Sair
