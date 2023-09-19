package sebfisch;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SrcFileSearch {

  /*
   * TODO 1.2 Avoid reopening files
   * 
   * The presented implementation opens some files twice.
   * Modify the stream pipeline in such a way that no files are opened more than
   * once and only matching lines are held in memory.
   */

  private static class Result {
    private final Path fileName;
    private final List<String> matchingLines;

    Result(final Path fileName, final Predicate<String> matches) {
      this.fileName = Objects.requireNonNull(fileName);

      try (Stream<String> lines = Files.lines(fileName)) {
        this.matchingLines = lines //
            .filter(matches) //
            .collect(Collectors.toList());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }

    Path getFileName() {
      return fileName;
    }

    List<String> getMatchingLines() {
      return matchingLines;
    }
  }

  public static void main(final String[] args) {
    final Path srcPath = Path.of("src");
    final String regExp = "public static[^=]*\\(";
    final Predicate<String> containsMatch = Pattern.compile(regExp).asPredicate();

    try (Stream<Path> javaFiles = walkJavaFiles(srcPath)) {
      javaFiles //
          .map(Path::toAbsolutePath) //
          .map(file -> new Result(file, containsMatch)) //
          .filter(result -> !result.getMatchingLines().isEmpty()) //
          .peek(result -> System.out.println(result.getFileName())) //
          .flatMap(result -> result.getMatchingLines().stream()) //
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
}
