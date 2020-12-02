package sebfisch.expr;

public interface Exp {
  <R> R transform(ExpTransform<R> toResult);
}
