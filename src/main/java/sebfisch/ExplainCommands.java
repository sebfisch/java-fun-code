package sebfisch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

public class ExplainCommands {
  private static final int HTTP_STATUS_OK = 200;
  private static final BufferedReader STDIN = //
      new BufferedReader(new InputStreamReader(System.in));

  public static void main(final String[] args) {
    prompt("Enter a unix command to print explanations, press ^C to quit.");
    try (Stream<String> lines = STDIN.lines()) {
      lines.map(ExplainCommands::explain).forEach(ExplainCommands::prompt);
    } catch (UncheckedIOException e) {
      System.err.println(e.getMessage());
    }
  }

  private static void prompt(final String output) {
    System.out.println(output);
    System.out.print("explain> ");
  }

  private static boolean isValidCommand(final String input) {
    return input.codePoints().allMatch(Character::isLetterOrDigit);
  }

  /*
   * This method serves as an example of an API which might return no result.
   * 
   * It loads an explanation for a given Unix command from the internet and wraps
   * it in an optional value.
   * 
   * The returned value is empty, when no explanation could be loaded within 5
   * seconds.
   */
  private static Optional<String> loadExplanation(final String command) {
    final HttpRequest request = HttpRequest.newBuilder() //
        .setHeader("User-Agent", "curl") //
        .uri(URI.create("https://cheat.sh/" + command + "?qT")) //
        .build();
    final HttpClient client = HttpClient.newBuilder() //
        .connectTimeout(Duration.ofSeconds(5)) //
        .build();

    try {
      return Optional.of(client.send(request, BodyHandlers.ofString())) //
          .filter(response -> response.statusCode() == HTTP_STATUS_OK) //
          .map(HttpResponse::body);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /*
   * Refactor the definitions below to avoid common anti-patterns when programming
   * with optional values.
   * 
   * The resulting implementation of `explain` should return the same result as
   * before for all given inputs.
   * 
   * In the refactored code, optionals should not be used in the argument position
   * of defined methods.
   * 
   * There should be no calls to `get` directly after a test if an optional value
   * is present.
   * 
   * Conditionals should be replaced with a call to `filter` where possible.
   */

  private static String explain(final String input) {
    final Optional<String> cmd = //
        Optional.ofNullable(isValidCommand(input) ? input : null);
    final Optional<String> explanation = explainCommand(cmd);
    return explanation.orElse("invalid command");
  }

  private static Optional<String> explainCommand(final Optional<String> cmd) {
    if (cmd.isEmpty()) {
      return Optional.empty();
    } else {
      final Optional<String> explanation = loadExplanation(cmd.get());
      return explanation;
    }
  }
}
