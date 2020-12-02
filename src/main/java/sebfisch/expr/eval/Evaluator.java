package sebfisch.expr.eval;

import sebfisch.expr.Bin;
import sebfisch.expr.ExpTransform;
import sebfisch.expr.Num;

public class Evaluator implements ExpTransform<Integer> {
  @Override
  public Integer fromExp(final Num num) {
    return num.getValue();
  }

  @Override
  public Integer fromExp(final Bin bin) {
    final Integer left = fromExp(bin.getLeftArg());
    final Integer right = fromExp(bin.getRightArg());
    return bin.getOp().apply(left, right);
  }
}
