package online.polp;

import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@AllArgsConstructor
public class Listener implements Runnable {
    private Socket socket;

    @Override
    public void run() {
        try {
            System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("v:" + Main.VERSION);

            runCalculator(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void runCalculator(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String operatorString = in.readLine();

            int operator;
            try {
                operator = Integer.parseInt(operatorString);
            } catch (NumberFormatException e) {
                throw new CalculatorException(CalculatorExceptionMessage.UNKNOWN_OPERATOR);
            }

            if (operator < 1 || operator > 5) {
                throw new CalculatorException(CalculatorExceptionMessage.UNKNOWN_OPERATOR);
            }

            double[] numbers = new double[2];

            for (int i = 0; i < numbers.length; i++) {
                String doubleString = in.readLine();
                try {
                    numbers[i] = Double.parseDouble(doubleString);
                } catch (NumberFormatException e) {
                    throw new CalculatorException(CalculatorExceptionMessage.NOT_A_NUMBER);
                }
            }

            double result;
            switch (operatorString) {
                case "1":
                    result = numbers[0] + numbers[1];
                    break;
                case "2":
                    result = numbers[0] - numbers[1];
                    break;
                case "3":
                    result = numbers[0] * numbers[1];
                    break;
                case "4":
                    if (numbers[1] == 0) {
                        throw new CalculatorException(CalculatorExceptionMessage.DIV_BY_ZERO);
                    }

                    result = numbers[0] / numbers[1];
                    break;
                case "5":
                    // Exit
                    return;
                default:
                    throw new CalculatorException(CalculatorExceptionMessage.UNKNOWN_OPERATOR);
            }

            out.println("ok:" + result);
        } catch (CalculatorException e) {
            out.println("ko:" + e.getMessage());
        }
    }
}
