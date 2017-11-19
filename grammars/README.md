Here are the parsers (Parsing Expressing Grammars). Each description is currently simplified here for clarity, and the syntax is a slightly flawed EBNF.
Whitespace is always ignored, and these operator long forms are converted to what the grammar uses:

- == → =
- != → ≠
- <= → ≤
- \>= → ≥
- and → ∧
- or → ∨
- nand → ⊼
- nor → ⊽
- xor → ⊻

## real number math

```
<digit>  ::= '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
<number> ::= <digit>{<digit>} ['.'{<digit>}]
<params> ::= <expr> {',' <expr>}
<fn>     ::= <function> '(' <params> ')'
<factor> ::= <parens>|<fn>|<number>
<parens> ::= '(' <terms> ')'
<opF>    ::= '*'|'×'|'/'|'%'
<opT>    ::= '+'|'-'|'−'
<term>   ::= <factor> {<opF> <factor>}
<real>   ::= <term> {<opT> <term>}
```

#### functions

- sqrt, √, ∛
- sin, cos, tan, asin, acos, atan
- exp, pow, exp, ln, sinh, cosh
- abs, round, ceil, floor, min, max
- sgn, bool (0 if 0, 1 otherwise)
- random sampling: unifR, normR, betaR, gammaR


## integer math

This is identical to the real number grammar, except that division (`/`) and functions that can yield real numbers are excluded, and `pow` does not allow negative exponents.

## boolean expressions of real numbers

This requires a *tolerance* for approximately equal (`≈`) and not approximately equal (`≉`).

```
condition   ::= ('='|'≠'|'≈'|≉'|'<'|'>'|'≥'|'≤') <real>
junction    ::= <expr> <condition> {<condition>}
wrapped     ::= '('<bool>')' | <junction>
boolean     ::= <wrapped> {('∧'|'∨'|'⊽'|'⊼'|'⊻') <wrapped>}
```

## boolean expressions of integers

This is identical to the grammar for boolean expressions of real numbers, except substituting `<int>` for `<real>`.

## if–elif–else expressions of real numbers

```
<rule>      ::= <bool> ':' <real>
<ifelse>    ::= 'if' <boolean>':' <real> [{'elif' <boolean>':' <real>} 'else:' <real>]
<ifresult>  ::= <real> | <ifelse>
```

## if–elif–else expressions of integers

This is the same as if–elif–else expressions of real numbers, except substituting `<int>` for `<real>`.

## if–elif–else expressions of strings

This is the same as if–elif–else expressions of real numbers, except substituting arbitrary strings for `<real>`.

## time-series for real numbers

```
<series> ::= <modifresult> [('@'|'evaluate every') <int>]
```

Where the *evaluation interval (i)* is set to 1 by default, and `modifresult` is `ifresult` with these substitutions:

- `$t` → time (index)
- `$[<int>]` → value at previous time `int` (from the integer grammar)

This time-series gets built from index 0 to n, with each interval `ki...(k+1)i` for each integer `k = 1 ... n/i` set to the expression evalutated at `t = ki`.

#### example

```
if $t<10000: 50 elif $t<2*pow(10,4): 100 else: $[$t-1] + norm(0, 1)  sample every 100
```

The result is 50 for the first 10s, 100 for the next 10s, and follows Brownian motion after that. The value only gets updated every 100 steps.


## alphanumeric grid ranges

```
<row>   ::= [A-Z]{[A-Z]}
<col>   ::= <digit>{<digit>}
<point> ::= <row><col>
<op>    ::= '-'|'*'|'...'
<range> ::= <point> [<op> <point>]
<multi> ::= <range> {',' <range>}
```

The operators `-`, `*`, and `...` have distinct meanings for a grid of dimensions `(n, m)`.

- `-` can only span across a row or down a column: `A2-A12` or `C4-H4`, but not `A1-C8`.
- `*` means span across a block of `(r_2-r_1)(c_2-r_2)` cells.
- `...` means span across all of the cells between the two indices, capturing `n*r_2 + c_2 - n*r_1 - c_1` cells.


