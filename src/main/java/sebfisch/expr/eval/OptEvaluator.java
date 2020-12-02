package sebfisch.expr.eval;

import java.util.Optional;

import sebfisch.expr.Bin;
import sebfisch.expr.BinOp;
import sebfisch.expr.ExpTransform;
import sebfisch.expr.Num;

public class OptEvaluator implements ExpTransform<Optional<Integer>> {
  @Override
  public Optional<Integer> fromExp(final Num num) {
    return Optional.of(num.getValue());
  }

  @Override
  public Optional<Integer> fromExp(final Bin bin) {
    final BinOp op = bin.getOp();
    return //
    fromExp(bin.getLeftArg()).flatMap(left -> //
    fromExp(bin.getRightArg()) //
        .filter(right -> op.getChar() != '/' || right != 0) //
        .map(right -> op.apply(left, right)));
  }
}
