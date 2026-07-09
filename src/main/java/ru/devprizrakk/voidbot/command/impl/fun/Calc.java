package ru.devprizrakk.voidbot.command.impl.fun;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.devprizrakk.voidbot.command.api.BaseCommand;
import ru.devprizrakk.voidbot.command.api.CommandCategory;
import ru.devprizrakk.voidbot.exceptions.discord.WrongErrorEmbedFactory;
import ru.devprizrakk.voidbot.language.LangMessage;
import ru.devprizrakk.voidbot.logging.LogType;
import ru.devprizrakk.voidbot.logging.Logger;

import java.sql.SQLException;
import java.util.*;

public class Calc extends BaseCommand {

    private static final Map<String, DoubleUnaryOperator> functions = new HashMap<>();

    static {
        functions.put("cos", Math::cos);
        functions.put("sin", Math::sin);
        functions.put("tan", Math::tan);
        functions.put("acos", Math::acos);
        functions.put("asin", Math::asin);
        functions.put("atan", Math::atan);
    }

    @Override
    public String getName() {
        return "calc";
    }

    @Override
    public String getDescription() {
        return getLangManager(event).getInfoLocale(
                LangMessage.Commands.Fun.Calc.FILE,
                LangMessage.Commands.Fun.Calc.Description.COMMAND
        );
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING,
                        "calc",
                        getLangManager(event).getInfoLocale(
                                LangMessage.Commands.Fun.Calc.FILE,
                                LangMessage.Commands.Fun.Calc.Description.Option.CALC),
                        true));
    }

    @Override
    public CommandCategory getCategory() {
        return null;
    }

    @Override
    public void onExecute() throws SQLException {
        String expression = Objects.requireNonNull(event.getOption("calc")).getAsString();

        try {
            double result = evaluate(expression);
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Calc.FILE,
                            LangMessage.Commands.Fun.Calc.Embed.TITLE
                    ));
            embed.setDescription(
                    getLangManager(event).getDescriptionLocale(
                                    LangMessage.Commands.Fun.Calc.FILE,
                                    LangMessage.Commands.Fun.Calc.Embed.DESCRIPTION
                            )
                            .replace("%result%", String.valueOf(result))
            );
            embed.setFooter(
                    getLangManager(event).getDescriptionLocale(
                            LangMessage.Commands.Fun.Calc.FILE,
                            LangMessage.Commands.Fun.Calc.Embed.FOOTER
                    ));
            event.replyEmbeds(embed.build()).queue();
        } catch ( Exception e ) {
            Logger.getLogger().log(LogType.ERROR, "command", "Error in calculation: ", e);
            new WrongErrorEmbedFactory(event).
                    wrongError(getLangManager(event).
                            getDescriptionLocale(
                                    LangMessage.Commands.Fun.Calc.FILE,
                                    LangMessage.Commands.Fun.Calc.Error.WRONG
                            )
                    );
        }
    }

    private double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", "").toLowerCase();
        expression = expression.replace("pi", String.valueOf(Math.PI)).replace("e", String.valueOf(Math.E));

        return parseExpression(expression);
    }

    private double parseExpression(String expr) {
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuffer = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    numBuffer.append(expr.charAt(i++));
                }
                i--;
                values.push(Double.parseDouble(numBuffer.toString()));
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            } else {
                StringBuilder funcBuffer = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    funcBuffer.append(expr.charAt(i++));
                }
                i--;

                String functionName = funcBuffer.toString();
                if (functions.containsKey(functionName)) {
                    i++;
                    int j = i, brackets = 1;
                    while (j < expr.length() && brackets > 0) {
                        if (expr.charAt(j) == '(') brackets++;
                        if (expr.charAt(j) == ')') brackets--;
                        j++;
                    }
                    double arg = parseExpression(expr.substring(i, j - 1));
                    values.push(functions.get(functionName).apply(arg));
                    i = j - 1;
                }
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        return values.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }

    private double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            case '^' -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }

    @FunctionalInterface
    interface DoubleUnaryOperator {
        double apply(double value);
    }
}
