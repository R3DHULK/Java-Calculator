import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JFrame {

    private JTextField displayField;

    public Calculator() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the display field
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        add(displayField, BorderLayout.NORTH);

        // Create the button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        // Buttons for digits, operators, and actions
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "<"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String buttonLabel = e.getActionCommand();

            switch (buttonLabel) {
                case "=":
                    calculateResult();
                    break;
                case "C":
                    displayField.setText("");
                    break;
                case "<":
                    removeLastCharacter();
                    break;
                default:
                    displayField.setText(displayField.getText() + buttonLabel);
                    break;
            }
        }
        public class EvalCalculator {
            public static double evaluate(String expression) {
                try {
                    return (double) new Object() {
                        int pos = -1, ch;

                        void nextChar() {
                            ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                        }

                        boolean eat(int charToEat) {
                            while (ch == ' ') nextChar();
                            if (ch == charToEat) {
                                nextChar();
                                return true;
                            }
                            return false;
                        }

                        double parse() {
                            nextChar();
                            double x = parseExpression();
                            if (pos < expression.length()) throw new IllegalArgumentException("Unexpected: " + (char) ch);
                            return x;
                        }

                        double parseExpression() {
                            double x = parseTerm();
                            for (;;) {
                                if (eat('+')) x += parseTerm();
                                else if (eat('-')) x -= parseTerm();
                                else return x;
                            }
                        }

                        double parseTerm() {
                            double x = parseFactor();
                            for (;;) {
                                if (eat('*')) x *= parseFactor();
                                else if (eat('/')) x /= parseFactor();
                                else return x;
                            }
                        }

                        double parseFactor() {
                            if (eat('+')) return parseFactor();
                            if (eat('-')) return -parseFactor();

                            double x;
                            int startPos = this.pos;
                            if (eat('(')) {
                                x = parseExpression();
                                eat(')');
                            } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                                x = Double.parseDouble(expression.substring(startPos, this.pos));
                            } else {
                                throw new IllegalArgumentException("Unexpected: " + (char) ch);
                            }

                            return x;
                        }
                    }.parse();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid expression: " + expression);
                }
            }
        }

        private void calculateResult() {
            String expression = displayField.getText();
            try {
                double result = EvalCalculator.evaluate(expression);
                displayField.setText(String.valueOf(result));
            } catch (IllegalArgumentException ex) {
                displayField.setText("Error");
            }
        }

        private void removeLastCharacter() {
            String currentText = displayField.getText();
            if (!currentText.isEmpty()) {
                displayField.setText(currentText.substring(0, currentText.length() - 1));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}
