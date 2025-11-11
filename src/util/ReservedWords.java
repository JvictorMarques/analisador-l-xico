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
import static util.TokenType.AND_OPERATOR;
import static util.TokenType.OR_OPERATOR;

public class ReservedWords {
    public static final Map<String, TokenType> WORDS = Map.ofEntries(
            Map.entry("int", INTEGER_DATA_TYPE),
            Map.entry("float", FLOATING_DATA_TYPE),
            Map.entry("print", PRINT_STATEMENT),
            Map.entry("input", INPUT_STATEMENT),
            Map.entry("if", CONDITIONAL_IF_STATEMENT),
            Map.entry("else", ALTERNATIVE_ELSE_STATEMENT),
            Map.entry("for", LOOP_FOR_STATEMENT),
            Map.entry("then", DO_STATEMENT),
            Map.entry("main", MAIN_FUNCTION),
            Map.entry("var", VARIABLE_DECLARATION),
            Map.entry("E", AND_OPERATOR),
            Map.entry("OU", OR_OPERATOR)
    );
}
