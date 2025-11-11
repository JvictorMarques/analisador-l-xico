package syntactic;

import exceptions.SyntacticException;
import lexical.Scanner;
import lexical.Token;
import util.TokenType;


/**
 * Classe responsável pela análise sintática do código fonte.
 * Implementa um parser recursivo descendente para a linguagem da mini-linguagem.
 * Cada método reconhece uma regra da gramática.
 */
public class Parser {
	// Scanner léxico utilizado para obter os tokens do código fonte
	private Scanner scanner;
	// Token atual sendo analisado pelo parser
	private Token token;

	/**
	 * Construtor do Parser.
	 * Inicializa o scanner e já lê o primeiro token do código fonte.
	 */
	public Parser(Scanner scanner) throws Exception {
		this.scanner = scanner;
		token = this.scanner.nextToken();
	}

	/**
	 * Inicia a análise sintática do programa principal.
	 * Chama o método que reconhece a estrutura principal.
	 */
	public void parse() throws Exception {
		program();
		// Se quiser garantir que não sobrou token após o programa, descomente abaixo:
		// if (token != null) {
		// 	throw new SyntacticException("Token inesperado após o fim do programa: " + token.getType() + "(" + token.getText() + ")");
		// }
	}

	/**
	 * Reconhece a estrutura principal do programa.
	 * Espera encontrar: main { ... }
	 */
	public void program() throws Exception {
		match(TokenType.MAIN_FUNCTION); // main
		match(TokenType.LEFT_BRACE);    // {

		// Seção de variáveis (opcional): se existir, chama varSection()
		if (token != null && token.getType() == TokenType.VARIABLE_DECLARATION) {
			varSection();
		}

		// Lista de comandos: comandos do corpo principal
		commandList();

		match(TokenType.RIGHT_BRACE);   // }
	}

	/**
	 * Reconhece a seção de declaração de variáveis.
	 * Espera encontrar: var { ... }
	 */
	private void varSection() throws Exception {
		match(TokenType.VARIABLE_DECLARATION); // var
		match(TokenType.LEFT_BRACE);           // {
		varDeclarationList();
		match(TokenType.RIGHT_BRACE);          // }
	}

	/**
	 * Reconhece uma lista de declarações de variáveis.
	 * Repete enquanto houver identificadores.
	 */
	private void varDeclarationList() throws Exception {
		while (token != null && token.getType() == TokenType.IDENTIFIER) {
			varDeclaration();
		}
	}

	/**
	 * Reconhece uma declaração de variável.
	 * Espera: IDENTIFIER : tipo ;
	 */
	private void varDeclaration() throws Exception {
		match(TokenType.IDENTIFIER);
		match(TokenType.COLON);
		type();
		match(TokenType.SEMICOLON);
	}

	/**
	 * Reconhece o tipo da variável.
	 * Espera: int ou float.
	 */
	private void type() throws Exception {
		if (token != null && (token.getType() == TokenType.INTEGER_DATA_TYPE || 
			token.getType() == TokenType.FLOATING_DATA_TYPE)) {
			token = scanner.nextToken();
		} else {
			throw new SyntacticException("Esperado tipo (int ou float), mas encontrado " + 
			(token != null ? token.getType() + "(" + token.getText() + ")" : "EOF"));
		}
	}

	/**
	 * Reconhece a lista de comandos de um bloco.
	 * Repete até encontrar o fim do bloco (}).
	 */
	private void commandList() throws Exception {
		while (token != null && token.getType() != TokenType.RIGHT_BRACE) {
			command();
		}
	}

	/**
	 * Reconhece um comando da linguagem.
	 * Pode ser: input, print, if ou atribuição.
	 */
	private void command() throws Exception {
		if (token == null) {
			return;
		}

		switch (token.getType()) {
			case INPUT_STATEMENT:
				inputStatement();
				break;
			case PRINT_STATEMENT:
				printStatement();
				break;
			case CONDITIONAL_IF_STATEMENT:
				ifStatement();
				break;
			case IDENTIFIER:
				assignment();
				break;
			default:
				throw new SyntacticException("Comando inesperado: " + token.getType() + "(" + token.getText() + ")");
		}
	}

	/**
	 * Reconhece o comando de entrada de dados.
	 * Espera: input(IDENTIFIER);
	 */
	private void inputStatement() throws Exception {
		match(TokenType.INPUT_STATEMENT);
		match(TokenType.LEFT_PAREN);
		match(TokenType.IDENTIFIER);
		match(TokenType.RIGHT_PAREN);
		match(TokenType.SEMICOLON);
	}

	/**
	 * Reconhece o comando de saída de dados.
	 * Espera: print(STRING) ou print(expressão);
	 */
	private void printStatement() throws Exception {
		match(TokenType.PRINT_STATEMENT);
		match(TokenType.LEFT_PAREN);

		if (token != null && token.getType() == TokenType.STRING) {
			token = scanner.nextToken();
		} else {
			expression();
		}

		match(TokenType.RIGHT_PAREN);
		match(TokenType.SEMICOLON);
	}

	/**
	 * Reconhece o comando de atribuição.
	 * Espera: IDENTIFIER <- expressão;
	 */
	private void assignment() throws Exception {
		match(TokenType.IDENTIFIER);
		match(TokenType.ASSIGNMENT_OPERATOR);
		expression();
		match(TokenType.SEMICOLON);
	}

	/**
	 * Reconhece o comando condicional.
	 * Espera: if condição then { comandos }
	 */
	private void ifStatement() throws Exception {
		match(TokenType.CONDITIONAL_IF_STATEMENT);
		condition();
		match(TokenType.DO_STATEMENT);
		match(TokenType.LEFT_BRACE);
		commandList();
		match(TokenType.RIGHT_BRACE);
	}

	/**
	 * Reconhece uma condição (simples ou composta).
	 * Pode ser uma expressão relacional ou composta com E/OU.
	 */
	private void condition() throws Exception {
		if (token != null && token.getType() == TokenType.LEFT_PAREN) {
			match(TokenType.LEFT_PAREN);
			conditionExpression();
			match(TokenType.RIGHT_PAREN);
		} else {
			conditionExpression();
		}
	}

	/**
	 * Reconhece uma expressão condicional.
	 * Pode ser: expressão [op_relacional expressão] [E/OU ...]
	 */
	private void conditionExpression() throws Exception {
		expression();

		if (token != null && token.getType() == TokenType.REL_OPERATOR) {
			match(TokenType.REL_OPERATOR);
			expression();
		}

		conditionTail();
	}

	/**
	 * Reconhece operadores lógicos (E/OU) em condições compostas.
	 * Permite encadeamento de condições.
	 */
	private void conditionTail() throws Exception {
		while (token != null && (token.getType() == TokenType.AND_OPERATOR || token.getType() == TokenType.OR_OPERATOR)) {
			token = scanner.nextToken();
			if (token != null && token.getType() == TokenType.LEFT_PAREN) {
				match(TokenType.LEFT_PAREN);
				conditionExpression();
				match(TokenType.RIGHT_PAREN);
			} else {
				expression();
				if (token != null && token.getType() == TokenType.REL_OPERATOR) {
					match(TokenType.REL_OPERATOR);
					expression();
				}
			}
		}
	}

	/**
	 * Reconhece uma expressão aritmética.
	 * Composta por termos e operadores.
	 */
	private void expression() throws Exception {
		term();
		expressionTail();
	}

	/**
	 * Reconhece operadores + e - em expressões aritméticas.
	 * Permite somas e subtrações encadeadas.
	 */
	private void expressionTail() throws Exception {
		while (token != null && token.getType() == TokenType.MATH_OPERATOR) {
			String op = token.getText();
			if (op.equals("+") || op.equals("-")) {
				token = scanner.nextToken();
				term();
			} else {
				break;
			}
		}
	}

	/**
	 * Reconhece um termo de expressão (multiplicação/divisão).
	 * Permite fatores multiplicados ou divididos.
	 */
	private void term() throws Exception {
		factor();
		termTail();
	}

	/**
	 * Reconhece operadores * e / em termos.
	 * Permite multiplicações e divisões encadeadas.
	 */
	private void termTail() throws Exception {
		while (token != null && token.getType() == TokenType.MATH_OPERATOR) {
			String op = token.getText();
			if (op.equals("*") || op.equals("/")) {
				token = scanner.nextToken();
				factor();
			} else {
				break;
			}
		}
	}

	/**
	 * Reconhece um fator: identificador, número ou expressão entre parênteses.
	 * É o elemento mais simples de uma expressão.
	 */
	private void factor() throws Exception {
		if (token == null) {
			throw new SyntacticException("Fim de arquivo inesperado");
		}

		if (token.getType() == TokenType.IDENTIFIER ||
			token.getType() == TokenType.NUMBER_INTEGER ||
			token.getType() == TokenType.NUMBER_FLOAT) {
			token = scanner.nextToken();
		} else if (token.getType() == TokenType.LEFT_PAREN) {
			match(TokenType.LEFT_PAREN);
			expression();
			match(TokenType.RIGHT_PAREN);
		} else {
			throw new SyntacticException("Esperado identificador, número ou '(', mas encontrado " + 
			token.getType() + "(" + token.getText() + ")");
		}
	}

	/**
	 * Verifica se o token atual é do tipo esperado e avança para o próximo token.
	 * Se não for o esperado, lança exceção sintática.
	 */
	private void match(TokenType expectedType) throws Exception {
		if (token == null) {
			throw new SyntacticException("Esperado token " + expectedType + ", mas não foi encontrado");
		}

		if (token.getType() == expectedType) {
			token = scanner.nextToken();
		} else {
			throw new SyntacticException("Esperado token " + expectedType + ", mas foi encontrado " + 
			token.getType() + "(" + token.getText() + ")");
		}
	}
}