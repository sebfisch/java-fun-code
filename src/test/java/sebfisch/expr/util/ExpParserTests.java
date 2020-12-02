package sebfisch.expr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ExpParserTests {
  private static final ExpParser EXP = new ExpParser();

  @Test
  void testParseError() {
    assertThrows(RuntimeException.class, () -> EXP.parse("").toString());
  }

  @Test
  void testPositiveNumber() {
    assertEquals("42", EXP.parse("42").toString());
  }

  @Test
  void testNegativeNumber() {
    assertEquals("-42", EXP.parse("-42").toString());
  }

  @Test
  void testRightNested() {
    assertEquals("(3 + (4 * 5))", EXP.parse("3+4*5").toString());
  }

  @Test
  void testLeftNested() {
    assertEquals("((3 * 4) + 5)", EXP.parse("3*4+5").toString());
  }

  @Test
  void testExplicitlyRightNested() {
    assertEquals("(3 * (4 + 5))", EXP.parse("3*(4+5)").toString());
  }

  @Test
  void testExplicitlyLeftNested() {
    assertEquals("((3 + 4) * 5)", EXP.parse("(3+4)*5").toString());
  }
}
