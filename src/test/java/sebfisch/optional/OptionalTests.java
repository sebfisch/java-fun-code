package sebfisch.optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class OptionalTests {
  @Test
  void testThatEmptyIsEmpty() {
    assertTrue(Optional.empty().isEmpty());
  }

  @Test
  void testThatNonEmptyIsPresent() {
    assertTrue(Optional.of("Hello").isPresent());
  }

  @Test
  void testThatNullIsNotAllowedInside() {
    assertThrows(NullPointerException.class, () -> Optional.of(null));
  }

  @Test
  void testThatNullableOfNullIsEmpty() {
    assertTrue(Optional.ofNullable(null).isEmpty());
  }

  @Test
  void testThatNullableOfNonNullIsPresent() {
    assertTrue(Optional.ofNullable("Hello").isPresent());
  }

  @Test
  void testThatGetReturnsPresentValue() {
    final String value = "Hello";
    assertEquals(value, Optional.of(value).get());
  }

  @Test
  void testThatGetFailsOnEmpty() {
    assertThrows(NoSuchElementException.class, () -> Optional.empty().get());
  }

  @Test
  void testThatMapOnEmptyYieldsEmpty() {
    final Optional<String> empty = Optional.empty();
    final Optional<Integer> result = empty.map(wrd -> wrd.length());
    assertTrue(result.isEmpty());
  }

  @Test
  void testThatMapAppliesGivenFunctionToPresentElement() {
    final Optional<String> word = Optional.of("Hello");
    final Optional<Integer> result = word.map(wrd -> wrd.length());
    assertEquals(Optional.of(5), result);
  }

  @Test
  void testThatFilterRemovesNonMatchingElement() {
    final Optional<String> word = Optional.of("Hello");
    final Optional<String> result = word.filter(wrd -> wrd.length() > 6);
    assertTrue(result.isEmpty());
  }

  @Test
  void testThatFilterKeepsMatchingElement() {
    final Optional<String> word = Optional.of("Optionals");
    final Optional<String> result = word.filter(wrd -> wrd.length() > 6);
    assertEquals(word, result);
  }

  @Test
  void testThatFlatMapOnEmptyYieldsEmpty() {
    final Optional<String> empty = Optional.empty();
    final Optional<Integer> result = //
        empty.flatMap(wrd -> wrd.chars().boxed().findFirst());
    assertTrue(result.isEmpty());
  }

  @Test
  void testThatFlatMapOnPresentReturnsEmptyResultOfGivenFunction() {
    final String value = "";
    final Optional<String> word = Optional.of(value);
    final Optional<Integer> result = //
        word.flatMap(wrd -> wrd.chars().boxed().findFirst());
    assertTrue(result.isEmpty());
  }

  @Test
  void testThatFlatMapOnPresentReturnsPresentResultOfGivenFunction() {
    final String value = "Hello";
    final Optional<String> word = Optional.of(value);
    final Optional<Integer> result = //
        word.flatMap(wrd -> wrd.chars().boxed().findFirst());
    assertEquals(Character.codePointAt(value, 0), result.get());
  }

  /*
   * TODO 2.1 Reasoning
   * 
   * Think about properties that can be used to manipulate or reason about
   * expressions involving the presented combinators and write more tests to check
   * these properties.
   * Can you express some of the presented combinators using others in a way that
   * would allow arbitrary applications of one combinator to be replaced by a
   * corresponding application of another?
   */

  @Test
  void testThatFlatMapWithIdentityFunctionFlattensNestedOptionals() {
    final Optional<Optional<Integer>> nested = Optional.of(Optional.of(42));
    final Optional<Integer> flat = nested.flatMap(s -> s);
    assertEquals(Optional.of(42), flat);
  }

  @Test
  void testThatMapIsSpecialCaseOfFlatMap() {
    final Optional<String> word = Optional.of("Hello");
    final Optional<Integer> result = word.flatMap(w -> Optional.of(w.length()));
    assertEquals(word.map(String::length), result);
  }

  @Test
  void testThatFilterIsSpecialCaseOfFlatMap() {
    final Optional<String> words = Optional.of("Optionals");
    final Optional<String> result = //
        words.flatMap(w -> w.length() > 6 ? Optional.of(w) : Optional.empty());
    assertEquals(words.filter(w -> w.length() > 6), result);
  }

  @Test
  void testThatMapYieldsEmptyIfFunctionYieldsNull() {
    final Optional<String> word = Optional.of("Hello");
    final Optional<Object> result = word.map(w -> null);
    assertTrue(result.isEmpty());
  }

  @Test
  void testThatFlatMapCombinesMultipleOptionals() {
    Optional<Boolean> result = //
        Optional.of(3).flatMap(a -> //
        Optional.of(4).flatMap(b -> //
        Optional.of(5).map(c -> a * a + b * b == c * c)));
    assertTrue(result.orElseThrow());
  }
}
