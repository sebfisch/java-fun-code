package sebfisch.expr;

import java.util.Objects;

public class Bin implements Exp {
  private final BinOp op;
  private final Exp leftArg;
  private final Exp rightArg;

  public Bin(final char op, final Exp leftArg, final Exp rightArg) {
    this.op = new BinOp(op);
    this.leftArg = Objects.requireNonNull(leftArg);
    this.rightArg = Objects.requireNonNull(rightArg);
  }

  @Override
  public <R> R transform(ExpTransform<R> toResult) {
    return toResult.fromExp(this);
  }

  public BinOp getOp() {
    return op;
  }

  public Exp getLeftArg() {
    return leftArg;
  }

  public Exp getRightArg() {
    return rightArg;
  }

  @Override
  public String toString() {
    return "(" + leftArg + " " + op.getChar() + " " + rightArg + ")";
  }
}
