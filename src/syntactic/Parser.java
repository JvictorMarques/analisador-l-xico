package syntactic;

import exceptions.SyntacticException;
import lexical.Scanner;
import lexical.Token;
import util.TokenType;


public class Parser {
	private Scanner scanner;	
	private Token token;

	public Parser(Scanner scanner) throws Exception {
		this.scanner = scanner;
		token = this.scanner.nextToken();
	}

	public void parse() throws Exception {
		program();
	}

	public void program() throws Exception {
		match(TokenType.MAIN_FUNCTION); 
		match(TokenType.LEFT_BRACE);    

		if (token != null && token.getType() == TokenType.VARIABLE_DECLARATION) {
			varSection();
		}
		commandList();
		match(TokenType.RIGHT_BRACE);   
	}

	private void varSection() throws Exception {
		match(TokenType.VARIABLE_DECLARATION); 
		match(TokenType.LEFT_BRACE);           
		varDeclarationList();
		match(TokenType.RIGHT_BRACE);          
	}

	private void varDeclarationList() throws Exception {
		while (token != null && token.getType() == TokenType.IDENTIFIER) {
			varDeclaration();
		}
	}

	private void varDeclaration() throws Exception {
		match(TokenType.IDENTIFIER);
		match(TokenType.COLON);
		type();
		match(TokenType.SEMICOLON);
	}

	private void type() throws Exception {
		if (token != null && (token.getType() == TokenType.INTEGER_DATA_TYPE || 
			token.getType() == TokenType.FLOATING_DATA_TYPE)) {
			token = scanner.nextToken();
		} else {
			throw new SyntacticException("Esperado tipo (int ou float), mas encontrado " + 
			(token != null ? token.getType() + "(" + token.getText() + ")" : "EOF"));
		}
	}

	private void commandList() throws Exception {
		while (token != null && token.getType() != TokenType.RIGHT_BRACE) {
			command();
		}
	}

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

	private void inputStatement() throws Exception {
		match(TokenType.INPUT_STATEMENT);
		match(TokenType.LEFT_PAREN);
		match(TokenType.IDENTIFIER);
		match(TokenType.RIGHT_PAREN);
		match(TokenType.SEMICOLON);
	}

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

	private void assignment() throws Exception {
		match(TokenType.IDENTIFIER);
		match(TokenType.ASSIGNMENT_OPERATOR);
		expression();
		match(TokenType.SEMICOLON);
	}

	private void ifStatement() throws Exception {
		match(TokenType.CONDITIONAL_IF_STATEMENT);
		condition();
		match(TokenType.DO_STATEMENT);
		match(TokenType.LEFT_BRACE);
		commandList();
		match(TokenType.RIGHT_BRACE);
	}

	private void condition() throws Exception {
		if (token != null && token.getType() == TokenType.LEFT_PAREN) {
			match(TokenType.LEFT_PAREN);
			conditionExpression();
			match(TokenType.RIGHT_PAREN);
		} else {
			conditionExpression();
		}
	}

	private void conditionExpression() throws Exception {
		expression();
		if (token != null && token.getType() == TokenType.REL_OPERATOR) {
			match(TokenType.REL_OPERATOR);
			expression();
		}
		conditionTail();
	}

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

	private void expression() throws Exception {
		term();
		expressionTail();
	}

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

	private void term() throws Exception {
		factor();
		termTail();
	}

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