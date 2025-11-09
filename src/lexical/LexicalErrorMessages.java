package lexical;

public class LexicalErrorMessages {
    public static final String GENERIC_ERROR = "Erro léxico desconhecido.";
    public static final String ERROR_INVALID_CHAR = "Caractere inválido encontrado no código-fonte.";
    public static final String ERROR_NUMBER_MISSING_NUMBER_AFTER_DOT = "O número está incompleto: é necessário pelo menos um dígito após o ponto decimal.";
    public static final String ERROR_NUMBER_HAVING_EXTRA_DOT_AFTER_FLOAT = "Número mal formado: ponto decimal extra encontrado.";
    public static final String ERROR_COMMENT = "Comentário não foi fechado antes do fim do arquivo.";
    public static final String ERROR_REL_OPERATOR = "Operador relacional inválido ou incompleto.";
    public static final String ERROR_STRING = "Cadeia de caracteres não finalizada.";
}
