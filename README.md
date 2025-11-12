# mini_compiler

## Descrição
O **mini_compiler** é um projeto acadêmico em **Java** desenvolvido para a disciplina de **Construção de Compiladores I**.
Este módulo corresponde ao **Checkpoint 02: Analisador Sintático** e tem como objetivo implementar e estender um analisador sintático simples, capaz de reconhecer a estrutura gramatical de um programa, validando a ordem e combinação de identificadores, números, operadores, palavras reservadas, parênteses e comentários, além de tratar erros sintáticos e léxicos.

---

## Estrutura do Projeto

```
mini_compiler/
│
├── programa.mc                # Arquivo de entrada de exemplo
├── README.md                  # Documentação do projeto
├── tokens_desc.txt            # Descrição dos tokens reconhecidos
└── src/
	├── module-info.java
	├── exceptions/
	│   ├── LexicalError.java         # Exceção para erros léxicos
	│   └── SyntacticException.java   # Exceção para erros sintáticos
	├── lexical/
	│   ├── LexicalErrorMessages.java # Mensagens de erro léxico
	│   ├── Scanner.java              # Analisador léxico
	│   └── Token.java                # Estrutura de dados para tokens
	├── mini_compiler/
	│   └── Main.java                 # Classe principal do compilador
	├── syntactic/
	│   └── Parser.java               # Analisador sintático
	└── util/
		├── InvalidChars.java         # Conjunto de caracteres inválidos
		├── ReservedWords.java        # Palavras reservadas e seus tipos
		└── TokenType.java            # Enumeração dos tipos de tokens
```

---

## Requisitos  

- **Java 11+** (recomendado: OpenJDK 17 ou superior)  
- IDE ou editor de texto de sua preferência (IntelliJ, VS Code, Eclipse, etc.)  
> Caso você não tenha o Java instalado na sua máquina, é possível você utilizar o Devcontainer como **JDK**.
[Documentação](https://code.visualstudio.com/docs/devcontainers/containers)
---

## Compilação e Execução  

Dentro da pasta `src`, compile os arquivos:  

```
javac mini_compiler/Main.java
```

E execute o programa:

```
java mini_compiler.Main
```

O arquivo de entrada pode ser configurado dentro da classe Main.java ou passado como argumento (dependendo da sua implementação).


## Contribuição

Este projeto é desenvolvido no contexto da disciplina. Sugestões e melhorias podem ser feitas via Merge Requests ou discutidas em sala de aula.

## Licença

Uso acadêmico restrito à disciplina de Construção de Compiladores I.

## Status

Atualmente em desenvolvimento no módulo de Analisador Sintático. Próximos módulos incluirão Analisador Semântico.
