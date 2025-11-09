package util;

import java.util.Map;

import static util.TokenType.ALTERNATIVE_ELSE_STATEMENT;
import static util.TokenType.CONDITIONAL_IF_STATEMENT;
import static util.TokenType.FLOATING_DATA_TYPE;
import static util.TokenType.INTEGER_DATA_TYPE;
import static util.TokenType.LOOP_FOR_STATEMENT;
import static util.TokenType.PRINT_STATEMENT;
import static util.TokenType.INPUT_STATEMENT;
import static util.TokenType.DO_STATEMENT;
import static util.TokenType.MAIN_FUNCTION;
import static util.TokenType.VARIABLE_DECLARATION;

public class ReservedWords {
    public static final Map<String, TokenType> WORDS = Map.of(
            "int", INTEGER_DATA_TYPE,
            "float", FLOATING_DATA_TYPE,
            "print", PRINT_STATEMENT,
            "input", INPUT_STATEMENT,
            "if", CONDITIONAL_IF_STATEMENT,
            "else", ALTERNATIVE_ELSE_STATEMENT,
            "for", LOOP_FOR_STATEMENT,
            "then", DO_STATEMENT,
            "main", MAIN_FUNCTION,
            "var", VARIABLE_DECLARATION
    );
}
