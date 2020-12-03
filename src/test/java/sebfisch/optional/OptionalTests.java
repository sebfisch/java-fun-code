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
}
