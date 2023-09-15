package sebfisch.parser;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class ParserTests {

  static <A> void //
      assertStreamEquals(final Stream<A> expected, final Stream<A> actual) {
    assertIterableEquals(//
        (Iterable<A>) expected::iterator, //
        (Iterable<A>) actual::iterator //
    );
  }

  @Test
  void testThatFailingParserFailsForEmptyInput() {
    final Parser<Object> parser = Parser.failure();
    final String input = "";
    final Stream<Object> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatFailingParserFailsForNonEmptyInput() {
    final Parser<Object> parser = Parser.failure();
    final String input = "X";
    final Stream<Object> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatParserOfGivenResultSucceedsOnEmptyInput() {
    final Integer number = 42;
    final Parser<Integer> parser = Parser.of(number);
    final String input = "";
    final Stream<Integer> results = parser.results(input);
    assertStreamEquals(Stream.of(number), results);
  }

  @Test
  void testThatParserOfGivenResultFailsOnNonEmptyInput() {
    final Integer number = 42;
    final Parser<Integer> parser = Parser.of(number);
    final String input = "42";
    final Stream<Integer> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatParserForCharYieldsFirstInputChar() {
    final Character character = 'X';
    final Parser<Character> parser = Parser.forChar();
    final String input = character.toString();
    final Stream<Character> results = parser.results(input);
    assertStreamEquals(Stream.of(character), results);
  }

  @Test
  void testThatParserForCharFailsOnEmptyInput() {
    final Parser<Character> parser = Parser.forChar();
    final String input = "";
    final Stream<Character> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatParserForStringYieldsMatchingInput() {
    final Parser<String> parser = Parser.forString(Character::isLetter);
    final String input = "hello";
    final Stream<String> results = parser.results(input);
    assertStreamEquals(Stream.of(input), results);
  }

  @Test
  void testThatParserForLettersFailsOnDigits() {
    final Parser<String> parser = Parser.forString(Character::isLetter);
    final String input = "0815";
    final Stream<String> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatMapAdjustsResultOfParser() {
    final Parser<String> parser = Parser.forString(Character::isLetter);
    final Parser<Integer> adjusted = parser.map(String::length);
    assertStreamEquals(Stream.of(5), adjusted.results("hello"));
  }

  @Test
  void testThatParserForDigitYieldsInputDigit() {
    final Character digit = '7';
    final Parser<Character> p = Parser.forChar().filter(Character::isDigit);
    assertStreamEquals(Stream.of(digit), p.results(digit.toString()));
  }

  @Test
  void testThatParserForDigitFailsForNonDigitInput() {
    final Parser<Character> p = Parser.forChar().filter(Character::isDigit);
    assertStreamEquals(Stream.empty(), p.results("X"));
  }

  @Test
  void testThatParserForRepeatedDigitsParsesAllDigits() {
    final Parser<String> parser = Parser.forString(Character::isDigit);
    final String input = "12345";
    final Stream<String> results = parser.results(input);
    assertStreamEquals(Stream.of(input), results);
  }

  @Test
  void testThatParserForRepeatedDigitsFailsOnNonDigits() {
    final Parser<String> parser = Parser.forString(Character::isDigit);
    final String input = "12abc";
    final Stream<String> results = parser.results(input);
    assertStreamEquals(Stream.empty(), results);
  }

  @Test
  void testThatSequentialParserCanCombineResults() {
    final Parser<String> digits = Parser.forString(Character::isDigit);
    final Parser<String> letters = Parser.forString(Character::isLetter);
    final Parser<String> digitsAndLetters = //
        digits.flatMap(ds -> letters.map(ls -> ds + ls));
    final String input = "12abc";
    assertStreamEquals(Stream.of(input), digitsAndLetters.results(input));
  }

  @Test
  void testThatParserForDigitsOrLettersParsesEitherButFailsOnBoth() {
    final Parser<String> digits = Parser.forString(Character::isDigit);
    final Parser<String> letters = Parser.forString(Character::isLetter);
    final Parser<String> digitsOrLetters = digits.or(letters);

    assertStreamEquals(Stream.of("12"), digitsOrLetters.results("12"));
    assertStreamEquals(Stream.of("abc"), digitsOrLetters.results("abc"));
    assertStreamEquals(Stream.empty(), digitsOrLetters.results("12abc"));
  }

  /*
   * TODO 3.1 Ambiguous parsers
   * 
   * Some parsers can consume their inputs in more than one way and as a
   * consequence produce more than one result.
   * A simple example would be a parser that is combined with itself using or.
   * Another, more subtle, example would be a sequential parser whose parts can be
   * combined in different ways to parse given input.
   *
   * Write at least two more tests for parsers documenting the behaviour of
   * alternative and sequential parsers with more than one result.
   * 
   * TODO 3.2 Reasoning
   * 
   * Can you spot a connection between the or combinator for parsers and the
   * corresponding logic operation on boolean values?
   * Write another test to document such a connection.
   */

  @Test
  void testThatAlternativeAmbigousParserHasManyResults() {
    final Parser<String> digits = Parser.forString(Character::isDigit);
    final Parser<String> parser = digits.or(digits);

    final String input = "123";
    assertStreamEquals(Stream.of(input, input), parser.results(input));
  }

  @Test
  void testThatSequentialAmbigousParserHasManyResults() {
    final Parser<String> digits = Parser.forString(Character::isDigit);
    final Parser<String> parser = //
        digits.flatMap(fst -> digits.map(snd -> fst + "|" + snd));

    final Stream<String> results = parser.results("123");
    assertStreamEquals(Stream.of("123|", "12|3", "1|23", "|123"), results);
  }

  @Test
  void testThatAlternativeFiltersCanBeCombined() {
    final Parser<Character> parser = Parser.forChar();
    final Parser<Character> alternative = //
        parser.filter(Character::isDigit) //
            .or(parser.filter(Character::isLetter));
    final Parser<Character> combined = //
        parser.filter(chr -> Character.isDigit(chr) || Character.isLetter(chr));
    assertStreamEquals(alternative.results("1"), combined.results("1"));
    assertStreamEquals(alternative.results("a"), combined.results("a"));
    assertStreamEquals(alternative.results("#"), combined.results("#"));
    assertStreamEquals(alternative.results(""), combined.results(""));
  }

  // Tests for combinators added in task 3.5

  @Test
  void testThatOptionalParserSucceedsForMissingInput() {
    final Parser<Optional<Character>> parser = //
        Parser.forChar().filter(Character::isLetter).optional();
    assertStreamEquals(Stream.of(Optional.empty()), parser.results(""));
  }

  @Test
  void testThatOptionalParserSucceedsForMatchingInput() {
    final Parser<Optional<Character>> parser = //
        Parser.forChar().filter(Character::isLetter).optional();
    final char character = 'X';
    assertStreamEquals(//
        Stream.of(Optional.of(character)), //
        parser.results(Character.toString(character)));
  }

  @Test
  void testThatListParserYieldsMultipleResults() {
    final Parser<List<Character>> parser = //
        Parser.forChar().filter(Character::isLetter).list();
    assertStreamEquals(//
        Stream.of('h', 'e', 'l', 'l', 'o'), //
        parser.results("hello").flatMap(List::stream));
  }

  @Test
  void testThatListParserYieldsEmptyResultForMissingInput() {
    final Parser<List<Character>> parser = //
        Parser.forChar().filter(Character::isLetter).list();
    assertTrue(parser.results("").findFirst().map(List::isEmpty).orElse(false));
  }

  @Test
  void testThatListParserCrashesIfUnderlyingParserAcceptsEmptyInput() {
    final Parser<List<Integer>> parser = Parser.of(42).list();
    final Stream<List<Integer>> results = parser.results("").limit(1);
    assertThrows(StackOverflowError.class, () -> results.count());
  }

}
