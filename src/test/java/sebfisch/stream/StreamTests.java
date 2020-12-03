package sebfisch.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class StreamTests {

  static <A> void //
      assertStreamEquals(final Stream<A> expected, final Stream<A> actual) {
    assertIterableEquals(//
        (Iterable<A>) expected::iterator, //
        (Iterable<A>) actual::iterator //
    );
  }

  @Test
  void testThatStreamCanBeCreatedFromGivenElements() {
    assertStreamEquals(Stream.of(2, 3, 4), Stream.of(2, 3, 4));
  }

  @Test
  void testThatMapAppliesGivenFunctionToEachElement() {
    final Stream<String> words = Stream.of("Hello", "Streams");
    final Stream<Integer> result = words.map(word -> word.length());
    assertStreamEquals(Stream.of(5, 7), result);
  }

  @Test
  void testThatFilterRemovesNonMatchingElements() {
    final Stream<String> words = Stream.of("Hello", "Streams");
    final Stream<String> result = words.filter(word -> word.length() > 6);
    assertStreamEquals(Stream.of("Streams"), result);
  }

  @Test
  void testThatFlatMapCombinesStreamResults() {
    final Stream<String> words = Stream.of("Hello", "Streams");
    final Stream<Integer> result = words.flatMap(word -> word.chars().boxed());
    assertEquals(12, result.count());
  }

  @Test
  void testThatFlatMapWithIdentityFunctionFlattensNestedStreams() {
    final Stream<Stream<Integer>> nested = //
        Stream.of(Stream.of(2), Stream.of(3, 4));
    final Stream<Integer> flat = nested.flatMap(stream -> stream);
    assertStreamEquals(Stream.of(2, 3, 4), flat);
  }

  @Test
  void testThatStreamsCanBeUsedOnlyOnce() {
    final Stream<String> words = Stream.of("Hello", "Streams");
    words.count();
    assertThrows(IllegalStateException.class, () -> words.count());
  }

  @Test
  void testThatStreamIsNotClosedByTerminalOperation() {
    final List<String> closingLog = new ArrayList<>();
    final Stream<String> words = Stream.of("Hello", "Streams");
    words.onClose(() -> closingLog.add("closed"));
    words.count();
    assertTrue(closingLog.isEmpty());
  }

  @Test
  void testThatStreamCanBeClosed() {
    final List<String> closingLog = new ArrayList<>();
    final Stream<String> words = Stream.of("Hello", "Streams");
    words.onClose(() -> closingLog.add("closed"));
    words.count();
    words.close();
    assertFalse(closingLog.isEmpty());
  }

  @Test
  void testThatStreamIsClosedByFlatMap() {
    final List<String> closingLog = new ArrayList<>();
    final Stream<String> words = Stream.of("Hello", "Streams");
    words.onClose(() -> closingLog.add("closed"));
    Stream.of(words).flatMap(stream -> stream).count();
    assertFalse(closingLog.isEmpty());
  }
}
