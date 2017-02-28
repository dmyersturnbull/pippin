Here are the parsers (Parsing Expressing Grammars). Each description is currently simplified here for clarity, and the syntax is a slightly flawed EBNF.
Whitespace is always ignored, and these operator "long-hand" forms are converted to what the grammar uses:

- == → =
- != → ≠
- <= → ≤
- \>= → ≥
- and → ∧
- or → ∨
- nand → ⊼
- nor → ⊽
- xor → ⊻

|'≠'|'≈'|≉'|'<'|'>'|'≥'|'≤'|'&'|'|'|'∧'|'∨'|'⊽'|'⊼'|'⊻'

## real number math

```
<digit>  ::= '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
<number> ::= {<digit>}['.'{<digit>}]
<params> ::= <expr>{','<expr>}
<fn>     ::= <function>'('<params>')'
<factor> ::= <parens>|<fn>|<number>
<parens> ::= '('<terms>')'
<opF>    ::= '*'|'×'|'/'|'%'
<opT>    ::= '+'|'-'|'−'
<term>   ::= <factor>{<opF><factor>}
<expr>   ::= <term>{<opT><term>}
```

## integer math

## boolean expressions of real numbers

## boolean expressions of integers

## if–elif–else expressions of real numbers

## if–elif–else expressions of integers

## time-series real numbers

## alphanumeric grid ranges



## math

#### grammar

```
<digit>  ::= '0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9'
<number> ::= {<digit>}['.'{<digit>}]
<fn>     ::= <function>'('<expr>')'
<factor> ::= <parens>|<fn>|<number>
<parens> ::= '('<terms>')'
<opF>    ::= '*'|'×'|'/'|'%'
<opT>    ::= '+'|'-'|'−'
<opE>    ::= '='|'≠'|'≈'|≉'|'<'|'>'|'≥'|'≤'|'&'|'|'|'∧'|'∨'|'⊽'|'⊼'|'⊻'
<term>   ::= <factor>{<opF><factor>}
<terms>  ::= <term>{<opT><term>}
<expr>   ::= <terms>{<opE><terms>}
```

#### functions

- sqrt, √, ∛
- sin, cos, tan, asin, acos, atan
- exp, pow, exp, ln, sinh, cosh
- abs, round, ceil, floor, min, max
- sgn, bool (0 if 0, 1 otherwise), not (1 if 0, 0 otherwise)
- random sampling: unifR, normR, betaR, gammaR

#### logical and comparison operators

- =, ≠, ≈, ≉ (not approximately equal; rendered badly in some fonts)
- <, >, ≥, ≤
- &, |, ∧, ∨, ⊽, ⊼, ⊻, → (material implication)

#### bitwise operators

- ~ (bitwise not)
- &, |


## alphanumeric grid

#### grammar

```
<row>   ::= {[A-Z]}
<col>   ::= {<digit>}
<well>  ::= <row><col>
<oper>  ::= '-'|'*'|'...'
<range> ::= <well><oper><well>
```

#### operators

The operators `-`, `*`, and `...` have distinct meanings for a grid of dimensions $(n, m)$.

- `-` can only span across a row or down a column: `A2-A12` or `C4-H4`, but not `A1-C8`.
- `*` means span across a block of $(r_2-r_1)(c_2-r_2)$ cells.
- `...` means span across all of the cells between the two indices, capturing $n*r_2 + c_2 - n*r_1 - c_1$ cells.


## statements

#### grammar

Using `<expr>` from the math parser...

```
<if>     ::= 'if ' <p>':' <p>
<elif>   ::= ' elif ' <p>':' <p>
<else>   ::= ' else: ' <p>
<ifelse> ::= <if>[{<elif>}<else>]
<p>      ::= '( '<ifelse>' )'|<expr>
<terms>  ::= <parens>|<ifelse>
```


## time-series

#### grammar

This is just the statements grammer with two modifications.

```
<z>      ::= <expr>|'['$['<expr>']'
<if>     ::= 'if '<p>':' <p>
<elif>   ::= 'elif '<p>':' <p>
<else>   ::= 'else '<p>
<ifelse> ::= <if>[{<elif>}<else>]
<p>      ::= '('<ifelse>')'|<z>
<terms>  ::= <parens>|<ifelse>
<freq>   ::= <digit>
<res>    ::= <terms>('@'|'sample every')<freq>
```

#### examples

Assuming that `$t` the step (time):

```
if $t<10000: 50 elif $t<2*pow(10,4): 100 else: $[$t-1] + norm(0, 1)  sample every 100
```

The result is 50 for the first 10s, 100 for the next 10s, and follows Brownian motion after that. The value only gets updated every 100 steps.
