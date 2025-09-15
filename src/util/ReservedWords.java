package util;

import java.util.Map;

import static util.TokenType.ALTERNATIVE_ELSE_STATEMENT;
import static util.TokenType.CONDITIONAL_IF_STATEMENT;
import static util.TokenType.FLOATING_DATA_TYPE;
import static util.TokenType.INTEGER_DATA_TYPE;
import static util.TokenType.LOOP_FOR_STATEMENT;
import static util.TokenType.PRINT_STATEMENT;

public class ReservedWords {
    public static final Map<String, TokenType> WORDS = Map.of(
            "int", INTEGER_DATA_TYPE,
            "float", FLOATING_DATA_TYPE,
            "print", PRINT_STATEMENT,
            "if", CONDITIONAL_IF_STATEMENT,
            "else", ALTERNATIVE_ELSE_STATEMENT,
            "for", LOOP_FOR_STATEMENT
    );
}
