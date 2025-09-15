package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import util.TokenType;

import static util.TokenType.ALTERNATIVE_ELSE_STATEMENT;
import static util.TokenType.CONDITIONAL_IF_STATEMENT;
import static util.TokenType.FLOATING_DATA_TYPE;
import static util.TokenType.INTEGER_DATA_TYPE;
import static util.TokenType.MATH_OPERATOR;
import static util.TokenType.NUMBER_FLOAT;
import static util.TokenType.PRINT_STATEMENT;
import static util.TokenType.REL_OPERATOR;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;

    private Map<String, TokenType> reservedWords = Map.of(
            "int", INTEGER_DATA_TYPE,
            "float", FLOATING_DATA_TYPE,
            "print", PRINT_STATEMENT,
            "if", CONDITIONAL_IF_STATEMENT,
            "else", ALTERNATIVE_ELSE_STATEMENT
    );

    private List<Character> invalidsChar = List.of('@', '`', '´', 'ç', '¨', '°');

	public Scanner(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			sourceCode = content.toCharArray();
			pos = 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Token nextToken() throws Exception {
		char currentChar;
		String content = "";
		state = 0;
		
		while (true) {
            if(isEoF() && state == 7) {
                throw new Exception("Comentário não terminado");
            }
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
                        state= 6;
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
                    } else if(currentChar == '.') {
                        content += currentChar;
                        state = 4;
                    } else if(isCommentLine(currentChar)){
                        content += currentChar;
                        state = 5;
                    } else if(currentChar == '/'){
                        content += currentChar;
                        state = 6;
                    } else if(isInvalidChar(currentChar)){
                        throw  new Exception("Char não reconhecido na linguagem");
                    }

                    break;
                case 1:
                    //estado para identificadores
                    if(isLetter(currentChar) || isDigit(currentChar)) {
                        content+=currentChar;
                        state = 1;
                    } else {
                        back();
                        if(reservedWords.containsKey(content)){
                            throw new Exception("NÃO PODE!! PALAVRA RESERVADA");
                        }
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
                    //estado para numeros inteiros
                    if(isDigit(currentChar)) {
                        content += currentChar;
                    } else if (currentChar == '.') {
                        content += currentChar;
                        state= 4;
                    } else {
                        back();
                        return new Token(TokenType.NUMBER_INTEGER, content);
                    }
                    break;
                case 4:
                    //estado para numeros com ponto flutuante
                    if(isDigit(currentChar)) {
                        content += currentChar;
                        state= 9;
                    } else {
                        throw new Exception("Má formação de número com ponto flutuante");
                    }
                    break;
                case 5:
                    // estado comentário linha
                    if(isEndLine(currentChar)){
                        content = "";
                        state = 0;
                    }
                    break;
                case 6:
                    // estado operador matemático
                    if(isSecondSymbolCommentBlock(currentChar)){
                        content+= currentChar;
                        state = 7;
                    }else{
                        back();
                        return new Token(MATH_OPERATOR, content);
                    }
                    break;
                case 7:
                    // estado comentário bloco (parte 1)
                    if(isSecondSymbolCommentBlock(currentChar)){
                        state = 8;
                    }
                    break;
                case 8:
                    // estado comentário bloco (parte 2)
                    if (isFirstSymbolCommentBlock(currentChar)){
                        content = "";
                        state = 0;
                    }
                    break;
                case 9:
                    // continuação float
                    if(isDigit(currentChar)){
                        content+=currentChar;
                    }else{
                        back();
                        return new Token(NUMBER_FLOAT, content);
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

    private boolean isEndLine(char c) {
        return c == '\n' || c == '\r';
    }

    private boolean isCommentLine(char c){
        return c == '#';
    }

    private boolean isFirstSymbolCommentBlock(char c){
        return c == '/';
    }

    private boolean isSecondSymbolCommentBlock(char c){
        return c == '*';
    }

    private boolean isInvalidChar(char c){
        return invalidsChar.contains(c);
    }
}
