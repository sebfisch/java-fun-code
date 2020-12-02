package sebfisch.expr.eval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import sebfisch.expr.Bin;
import sebfisch.expr.Exp;
import sebfisch.expr.Num;

public class NullEvaluatorTests {
  private static final NullEvaluator VALUE = new NullEvaluator();

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
  void testThatDivisionByZeroYieldsNull() {
    Exp exp = // (3 * (4 / ((1 + 2) - 3)))
        new Bin('*', new Num(3), //
            new Bin('/', new Num(4), //
                new Bin('-', //
                    new Bin('+', new Num(1), new Num(2)), //
                    new Num(3))));

    assertNull(VALUE.fromExp(exp));
  }
}
