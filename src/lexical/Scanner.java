package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import util.TokenType;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;
	
	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() {
		char currentChar;
		String content = "";
		state = 0;
		
		while (true) {
			if(isEoF()) {
				return null;
			}
			currentChar = nextChar();
			
			switch(state) {
			case 0:
				//estado inicial
				if(isLetter(currentChar)) {
					content+=currentChar;
					state = 1;
				} else if(isMathOperator(currentChar)) {
					content += currentChar;
					return new Token(TokenType.MATH_OPERATOR, content);
				} else if(isAssignmentOperator(currentChar)) {
					content += currentChar;
					return new Token(TokenType.ASSIGNMENT, content);
				} else if(isRelOperator(currentChar)) {
					content += currentChar;
					state = 2;
				} else if(isLeftParen(currentChar)) {
					content += currentChar;
					return new Token(TokenType.LEFT_PAREN, content);
				} else if(isRightParen(currentChar)) {
					content += currentChar;
					return new Token(TokenType.RIGHT_PAREN, content);
				} else if(isDigit(currentChar)) {
					content += currentChar;
					state = 3;
				}
				break;
			case 1:
				//estado para identificadores
				if(isLetter(currentChar) || isDigit(currentChar)) {
					content+=currentChar;
					state = 1;
				} else {
					back();
					return new Token(TokenType.IDENTIFIER, content);
				}
				break;
			case 2:
				//estado para operadores relacionais
				if(currentChar == '=') {
					content += currentChar;
                    return new Token(TokenType.REL_OPERATOR, content);
				} else {
					back();
					return new Token(TokenType.REL_OPERATOR, content);
				}
			case 3:
				//estado para numeros
				if(isDigit(currentChar) || currentChar == '.') {
					content += currentChar;
				} else if (content.endsWith(".")) {
					content = content.substring(0, content.length() - 1);
				} else {
					back();
					return new Token(TokenType.NUMBER, content);
				}
				break;
			}
		}
	}
	
	private boolean isLetter(char c) {
		return (c>='a' && c <= 'z') || (c>='A' && c <= 'Z');		
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private boolean isMathOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	
	private boolean isAssignmentOperator(char c) {
		return c == '=';
	}

	private boolean isRelOperator(char c) {
		return c == '>' ||  c == '<' || c == '!';
	}

	private boolean isLeftParen(char c) {
		return c == '(';
	}

	private boolean isRightParen(char c) {
		return c == ')';
	}

	private char nextChar() {
		return sourceCode[pos++];
	}

	private void back() {
		pos--;
	}
	
	private boolean isEoF() {
		return pos >= sourceCode.length;
	}
	
}
