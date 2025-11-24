package online.polp;

public class CalculatorException extends RuntimeException {
    public CalculatorException(CalculatorExceptionMessage message) {
        super(message.toString());
    }
}
