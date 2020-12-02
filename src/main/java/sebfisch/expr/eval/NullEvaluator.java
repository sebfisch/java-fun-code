package sebfisch.expr.eval;

import sebfisch.expr.Bin;
import sebfisch.expr.BinOp;

public class NullEvaluator extends Evaluator {
  @Override
  public Integer fromExp(final Bin bin) {
    final Integer left = fromExp(bin.getLeftArg());
    final Integer right = fromExp(bin.getRightArg());
    return applyOp(bin.getOp(), left, right);
  }

  private Integer applyOp(final BinOp op, final Integer left, final Integer right) {
    if (left == null || right == null //
        || op.getChar() == '/' && right == 0) {
      return null;
    } else {
      return op.apply(left, right);
    }
  }
}
