package online.polp;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CalculatorExceptionMessage {
    NOT_A_NUMBER("not_a_number"),
    UNKNOWN_OPERATOR("unknown_operator"),
    DIV_BY_ZERO("div_by_zero");

    private final String message;

    @Override
    public String toString() {
        return message;
    }
}
