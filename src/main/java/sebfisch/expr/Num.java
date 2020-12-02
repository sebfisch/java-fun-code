package sebfisch.expr;

public class Num implements Exp {
  private final int value;

  public Num(final int value) {
    this.value = value;
  }

  @Override
  public <R> R transform(ExpTransform<R> toResult) {
    return toResult.fromExp(this);
  }

  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
