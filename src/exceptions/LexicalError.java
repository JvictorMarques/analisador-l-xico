package exceptions;

public class LexicalError extends RuntimeException {
    private int line;
    private int column;

    public LexicalError(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage(){
        return super.getMessage() + " (linha: " + line + ", coluna: " + column + ")";
    }
}
