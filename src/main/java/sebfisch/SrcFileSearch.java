package sebfisch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SrcFileSearch {

  /*
   * TODO 1.2 Avoid reopening files
   * 
   * The presented implementation opens some files twice.
   * Modify the stream pipeline in such a way that no files are opened more than
   * once and only matching lines are held in memory.
   */

  public static void main(final String[] args) {
    final Path srcPath = Path.of("src");
    final String regExp = "public static[^=]*\\(";
    final Predicate<String> containsMatch = Pattern.compile(regExp).asPredicate();

    try (Stream<Path> javaFiles = walkJavaFiles(srcPath)) {
      javaFiles //
          .map(Path::toAbsolutePath) //
          .filter(file -> readLines(file).anyMatch(containsMatch)) //
          .peek(System.out::println) //
          .flatMap(SrcFileSearch::readLines) //
          .filter(containsMatch) //
          .forEach(System.out::println);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (UncheckedIOException e) {
      System.err.println(e.getMessage());
    }
  }

  private static Stream<Path> walkJavaFiles(final Path root) throws IOException {
    return Files.walk(root) //
        .filter(Files::isReadable) //
        .filter(path -> path.toString().endsWith(".java"));
  }

  private static Stream<String> readLines(final Path file) {
    try {
      return Stream.of(Files.lines(file)).flatMap(s -> s);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
