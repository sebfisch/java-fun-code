package sebfisch.parser;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@FunctionalInterface
public interface Parser<A> {
  class Result<R> {
    final R result;
    final String remainingInput;

    Result(final R result, final String remainingInput) {
      this.result = Objects.requireNonNull(result);
      this.remainingInput = Objects.requireNonNull(remainingInput);
    }
  }

  Stream<Result<A>> intermediateResults(String input);

  default Stream<A> results(final String input) {
    return intermediateResults(input) //
        .filter(res -> res.remainingInput.isEmpty()) //
        .map(res -> res.result);
  }

  static <R> Parser<R> failure() {
    return input -> Stream.empty();
  }

  static <R> Parser<R> of(R result) {
    return input -> Stream.of(new Result<R>(result, input));
  }

  static Parser<Character> forChar() {
    return input -> input.isEmpty() //
        ? Stream.empty()
        : Stream.of(new Result<Character>(input.charAt(0), input.substring(1)));
  }

  static Parser<String> forString(final Predicate<Character> predicate) {
    return //
    Parser.forChar().filter(predicate).flatMap(chr -> //
    forString(predicate).map(str -> chr + str) //
    ).or(Parser.of(""));
  }

  default Parser<A> or(final Parser<A> parser) {
    return input -> Stream.concat(//
        intermediateResults(input), //
        parser.intermediateResults(input));
  }

  default <B> Parser<B> flatMap(final Function<A, Parser<B>> function) {
    return input -> intermediateResults(input).flatMap(parsed -> //
    function.apply(parsed.result).intermediateResults(parsed.remainingInput));
  }

  default <B> Parser<B> map(final Function<A, B> function) {
    return flatMap(result -> Parser.of(function.apply(result)));
  }

  default Parser<A> filter(final Predicate<A> predicate) {
    return flatMap(result -> //
    predicate.test(result) ? Parser.of(result) : Parser.failure());
  }

  /*
   * TODO 3.5 Add combinators
   * 
   * Add combinators with the following signatures to the Parser interface.
   * 
   * Parser<Optional<A>> optional()
   * Parser<List<A>> list()
   * 
   * Define the combinators in terms of existing ones without using
   * intermediateResults directly.
   * Write tests documenting the behaviour of the new combinators.
   */
}
