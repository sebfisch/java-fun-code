package sebfisch.expr.eval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sebfisch.expr.Bin;
import sebfisch.expr.Exp;
import sebfisch.expr.Num;

public class EvaluatorTests {
  private static final Evaluator VALUE = new Evaluator();

  @Test
  void testThatAllOperationsWorkAsExpected() {
    final Exp exp = // (((3 * 4) / (1 + 2)) - 3)
        new Bin('-', //
            new Bin('/', //
                new Bin('*', new Num(3), new Num(4)), //
                new Bin('+', new Num(1), new Num(2))), //
            new Num(3));

    assertEquals(1, VALUE.fromExp(exp));
  }

  @Test
  void testThatDivisionByZeroThrowsException() {
    Exp exp = // (3 * (4 / ((1 + 2) - 3)))
        new Bin('*', new Num(3), //
            new Bin('/', new Num(4), //
                new Bin('-', //
                    new Bin('+', new Num(1), new Num(2)), //
                    new Num(3))));

    assertThrows(ArithmeticException.class, () -> VALUE.fromExp(exp));
  }
}
