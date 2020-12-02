package sebfisch.parser;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

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
}
