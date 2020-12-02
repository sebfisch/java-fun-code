package sebfisch.expr;

public interface ExpTransform<R> {
  default R fromExp(Exp exp) {
    return exp.transform(this);
  }

  R fromExp(Num num);

  R fromExp(Bin bin);
}
