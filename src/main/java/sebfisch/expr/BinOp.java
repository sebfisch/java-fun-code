package sebfisch.expr;

public class BinOp {
  private final char chr;

  public BinOp(final char chr) {
    this.chr = chr;
  }

  public char getChar() {
    return chr;
  }

  public int apply(final int left, final int right) {
    switch (chr) {
      case '+':
        return left + right;
      case '-':
        return left - right;
      case '*':
        return left * right;
      case '/':
        return left / right;
      default:
        throw new RuntimeException("Unexpected operator: " + chr);
    }
  }
}
