package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import exceptions.LexicalError;
import util.TokenType;

import static lexical.LexicalErrorMessages.ERROR_COMMENT;
import static lexical.LexicalErrorMessages.ERROR_INVALID_CHAR;
import static lexical.LexicalErrorMessages.ERROR_NUMBER_HAVING_EXTRA_DOT_AFTER_FLOAT;
import static lexical.LexicalErrorMessages.ERROR_NUMBER_MISSING_NUMBER_AFTER_DOT;
import static lexical.LexicalErrorMessages.ERROR_REL_OPERATOR; // Qual que era esse erro?
import static lexical.LexicalErrorMessages.ERROR_STRING;
import static lexical.LexicalErrorMessages.GENERIC_ERROR;
import static util.InvalidChars.CHARS;
import static util.ReservedWords.WORDS;
import static util.TokenType.ASSIGNMENT_OPERATOR;
import static util.TokenType.MATH_OPERATOR;
import static util.TokenType.NUMBER_FLOAT;
import static util.TokenType.REL_OPERATOR;

public class Scanner {
	private int state;
	private char[] sourceCode;
	private int pos;
    private int line=1;
    private int column;


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
            if(isEoF() && (state == 8 || state == 9) ) {
                throw new LexicalError(
                        ERROR_COMMENT,
                        line,
                        column > 0 ? column : 1
                );
            }
			if(isEoF()) {
				return null;
			}

            currentChar = nextChar();
            if(isEndLine(currentChar)){
                line++;
                column=0;
            }

			switch(state) {
                case 0:
                    //estado inicial
                    if(isLetter(currentChar) || currentChar == '_') {
                        content+=currentChar;
                        state = 1;
                    } else if(isRelationalOrAssignmentOperator(currentChar)) {
                        content += currentChar;
                        if(currentChar == '<') {
                            state = 12;
                        } else {
                            state = 13;
                        }
                    } else if(isDigit(currentChar)) {
                        content += currentChar;
                        state = 3;
                    } else if(currentChar == '.') {
                        content += currentChar;
                        state = 4;
                    } else if(isCommentLine(currentChar)){
                        content += currentChar;
                        state = 6;
                    } else if(isFirstSymbolCommentBlock(currentChar)){
                        content += currentChar;
                        state = 7;
                    } else if(isMathOperator(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.MATH_OPERATOR, content);
                    } else if(isLeftParen(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.LEFT_PAREN, content);
                    } else if(isRightParen(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.RIGHT_PAREN, content);
                    } else if(isLeftBrace(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.LEFT_BRACE, content);
                    } else if(isRightBrace(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.RIGHT_BRACE, content);
                    } else if(isInvalidChar(currentChar)){
                        throw new LexicalError(ERROR_INVALID_CHAR, line, column);
                    } else if (isColon(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.COLON, content);
                    } else if (isSemicolon(currentChar)) {
                        content += currentChar;
                        return new Token(TokenType.SEMICOLON, content);
                    } else if(isQuotes(currentChar)){
                        content += currentChar;
                        state = 11;
                    } else {
                        if (currentChar != ' ' && currentChar != '\t' && !isEndLine(currentChar)) {
                            throw new LexicalError(GENERIC_ERROR, line, column);
                        }
                    }

                    break;
                case 1:
                    //estado para identificadores
                    if(isLetter(currentChar) || isDigit(currentChar) || currentChar == '_') {
                        content+=currentChar;
                    } else {
                        back();
                        if(WORDS.containsKey(content)){
                            TokenType type = WORDS.get(content);
                            return new Token(type, content);
                        }
                        return new Token(TokenType.IDENTIFIER, content);
                    }
                    break;
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
                        state= 5;
                    } else {
                        back();
                        throw new LexicalError(ERROR_NUMBER_MISSING_NUMBER_AFTER_DOT, line, column);
                    }
                    break;
                case 5:
                    // continuação float
                    if(isDigit(currentChar)){
                        content+=currentChar;
                    } else if (currentChar == '.') {
                        throw new LexicalError(ERROR_NUMBER_HAVING_EXTRA_DOT_AFTER_FLOAT, line, column);
                    }
                    else{
                        back();
                        return new Token(NUMBER_FLOAT, content);
                    }
                    break;
                case 6:
                    // estado comentário linha
                    if(isEndLine(currentChar)){
                        content = "";
                        state = 0;
                    }
                    break;
                case 7:
                    // estado /
                    if(isSecondSymbolCommentBlock(currentChar)){
                        content+= currentChar;
                        state = 8;
                    }else{
                        back();
                        return new Token(MATH_OPERATOR, content);
                    }
                    break;
                case 8:
                    // estado comentário bloco (parte 1)
                    if(isSecondSymbolCommentBlock(currentChar)){
                        state = 9;
                    }
                    break;
                case 9:
                    // estado comentário bloco (parte 2)
                    if (isFirstSymbolCommentBlock(currentChar)){
                        content = "";
                        state = 0;
                    }else{
                        state = 8;
                    }
                    break;
                case 10:
                    // estado =
                    if(isRelationalOrAssignmentOperator(currentChar)){
                        content+= currentChar;
                        return new Token(REL_OPERATOR, content);
                    }else{
                        back();
                        return new Token(ASSIGNMENT_OPERATOR, content);
                    }
                case 11:
                    // estado para fechamento de aspas
                    if(isQuotes(currentChar)){
                        content += currentChar;
                        return new Token(TokenType.STRING, content);
                    } else if (isEoF()){
                        throw new LexicalError(ERROR_STRING, line, column);
                    } else {
                        content += currentChar;
                    }
                    break;
                case 12:
                    // estado para < (pode ser <, <= ou <-)
                    if(currentChar == '=') {
                        content += currentChar;
                        return new Token(TokenType.REL_OPERATOR, content); // <=
                    }
                    else if (currentChar == '-') {
                        content += currentChar;
                        return new Token(TokenType.ASSIGNMENT_OPERATOR, content); // <-
                    } else {
                        back();
                        return new Token(TokenType.REL_OPERATOR, content); // <
                    }
                case 13:
                    // estado para >, !, = (pode ser >=, >, !=, ==)
                    if(currentChar == '='){
                        content += currentChar;
                        return new Token(TokenType.REL_OPERATOR, content); // >=, !=, ==
                    } else {
                        back();
                        return new Token(TokenType.REL_OPERATOR, content); // >, !
                    }
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
	
	private boolean isRelationalOrAssignmentOperator(char c) {
		return c == '>' ||  c == '<' || c == '!' || c == '=';
	}

	private boolean isLeftParen(char c) {
		return c == '(';
	}

	private boolean isRightParen(char c) {
		return c == ')';
	}

    private boolean isLeftBrace(char c) {
        return c == '{';
    }

	private boolean isRightBrace(char c) {
        return c == '}';
    }

	private char nextChar() {
        column ++;
		return sourceCode[pos++];
	}

	private void back() {
        column--;
        pos--;
        if(isEndLine(sourceCode[pos])){
            line--;
        }
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

    private boolean isColon(char c){
        return c == ':';
    }

    private boolean isSemicolon(char c){
        return c == ';';
    }

    private boolean isQuotes(char c){
        return c == '"';
    }

    private boolean isInvalidChar(char c){
        return CHARS.contains(c);
    }
}
