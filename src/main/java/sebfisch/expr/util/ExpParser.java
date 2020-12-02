package sebfisch.expr.util;

import sebfisch.expr.Bin;
import sebfisch.expr.Exp;
import sebfisch.expr.Num;
import sebfisch.parser.Parser;

// cf. https://github.com/katef/kgt/blob/main/examples/expr.bnf
public class ExpParser {

  public Exp parse(final String input) {
    return expr().results(input).findFirst() //
        .orElseThrow(() -> new RuntimeException("parse error for: " + input));
  }

  private static final Parser<Integer> DIGITS = //
      Parser.forString(Character::isDigit) //
          .filter(s -> !s.isEmpty()) //
          .map(Integer::parseInt);

  // <integer> ::= "-" <digits> | <digits>
  private static final Parser<Integer> INTEGER = //
      Parser.forChar().filter(chr -> chr == '-').flatMap(_sign -> //
      DIGITS.map(n -> -n) //
      ).or(DIGITS);

  // <const> ::= <integer>
  private static final Parser<Exp> CONST = INTEGER.map(Num::new);

  // <expr> ::= <term> "+" <expr> | <term>
  private Parser<Exp> expr() {
    final Parser<Exp> opExpr = //
        term().flatMap(fst -> //
        Parser.forChar().filter(chr -> chr == '+').flatMap(chr -> //
        expr().map(snd -> //
        new Bin(chr, fst, snd))));
    return opExpr.or(term());
  }

  // <term> ::= <factor> "*" <term> | <factor>
  private Parser<Exp> term() {
    final Parser<Exp> opTerm = //
        factor().flatMap(fst -> //
        Parser.forChar().filter(chr -> chr == '*').flatMap(chr -> //
        term().map(snd -> //
        new Bin(chr, fst, snd))));
    return opTerm.or(factor());
  }

  // <factor> ::= "(" <expr> ")" | <const>
  private Parser<Exp> factor() {
    final Parser<Exp> parenthezised = //
        Parser.forChar().filter(chr -> chr == '(').flatMap(_open -> //
        expr().flatMap(exp -> //
        Parser.forChar().filter(chr -> chr == ')').map(_close -> //
        exp)));
    return parenthezised.or(CONST);
  }
}
