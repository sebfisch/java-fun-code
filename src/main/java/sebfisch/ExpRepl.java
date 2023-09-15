package sebfisch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.stream.Stream;

import sebfisch.expr.Exp;
import sebfisch.expr.eval.OptEvaluator;
import sebfisch.expr.util.ExpParser;

public class ExpRepl {

  /*
   * TODO 3.4 Interactive evaluation
   * 
   * Implement an interactive evaluator of arithmetic expressions as command line
   * program.
   * Here is an example interaction:
   * 
   * Enter arithmetic expressions to evaluate them, press ^C to quit.
   * eval> 6*3 + 4
   * ((6 * 3) + 4) = 22
   * eval> 6 * (3+4)
   * (6 * (3 + 4)) = 42
   * eval> 5 % 2
   * parse error for: 5 % 2
   * eval> 1/0
   * (1 / 0) = undefined
   * eval> 50 - 50/6
   * (50 - (50 / 6)) = 42
   * eval> ^C
   * 
   * Use the lines method provided by BufferedReader to process inputs in a stream
   * pipeline.
   * Implement error handling for parse errors and division by zero as shown in
   * the example as well as for possible I/O exceptions.
   */

  private static final BufferedReader STDIN = //
      new BufferedReader(new InputStreamReader(System.in));

  private static final ExpParser PARSER = new ExpParser();
  private static final OptEvaluator EVALUATOR = new OptEvaluator();

  public static void main(final String[] args) {
    prompt("Enter arithmetic expressions to evaluate them, press ^C to quit.");
    try (Stream<String> lines = STDIN.lines()) {
      lines //
          .map(ExpRepl::parse) //
          .flatMap(Optional::stream) //
          .peek(exp -> {
            System.out.print(exp.toString() + " = ");
          }) //
          .map(EVALUATOR::fromExp) //
          .peek(res -> {
            if (res.isEmpty()) {
              prompt("undefined");
            }
          }) //
          .flatMap(Optional::stream) //
          .forEach(ExpRepl::prompt);
    } catch (UncheckedIOException e) {
      System.out.println(e.getMessage());
    }
  }

  private static void prompt(Object output) {
    System.out.println(output);
    System.out.print("eval> ");
  }

  private static Optional<Exp> parse(String input) {
    try {
      return Optional.of(PARSER.parse(input));
    } catch (RuntimeException e) {
      prompt(e.getMessage());
      return Optional.empty();
    }
  }
}
