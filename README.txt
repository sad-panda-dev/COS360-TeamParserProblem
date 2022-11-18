DISCUSSION OF THE COS 360 TEAM PARSER PROBLEM AND ITS FILES

Due date: 19 November 2021 @ 8 AM

IMPORTANT:

I would like you to do the work on GitHub so I can see that everyone on the
team is making a contribution.

When you have completed all the coding and are satisfied that your parser
works correctly, please put all the source code for the classes you
defined methods for in an archive file and upload the archive file
to Brightspace.

These are the 14 files for the team predictive parsing exercise which will read
text representations of CofinFin expressions, parse them into CFExp objects,
and then evaluate the results in an environment that associates some variables
to CofinFin values.

They are as follows

10 Java template source code, discussed more below.

CFBinary.java
CFConditional.java
CFConst.java
CFExp.java
CFExpParser.java
CFParserDriver.java
CFScanner.java
CFToken.java
CFUnary.java
CFVar.java

18 Test case files
valINJ.txt for J from 1 to 17
parserInvalidTest.txt

18 Test case correct output files
valOutJ.txt for J from 1 to 17
invalidOut.txt

This file
README.txt

The translation is from text files with CofinFin expressions to a Java type,
CFExp, to represent them.  CFExp is an abstract, umbrella super class with 5 subclasses

CFBinary.java        for expressions w binary operators at the root
CFConditional.java   for if test then exp1 else exp2 endif expressions
CFConst.java         for literals like { 1, 2 } or CMP{ 1, 2 }
CFUnary.java         for expressions w unary operators at the root (only complement)
CFVar.java           for variables, which should be bound in the environment

The only coding you will need to do for these is of the
eval, substitute, and deepCopy methods, and only in the classes

CFBinary
CFConditional
CFUnary
CFVar

Those are all fairly simple methods that recurse on the tree structure of the
expressions.

There are 4 classes for the parser work

CFExpParser.java    - where the actual parsing takes place
                      YOU WILL NEED TO CODE MULTIPLE METHODS IN THIS CLASS, a method
                      for each grammar variable except the start symbol

CFParserDriver.java - for reading the source file of expressions
                      to parse and calling the CFExpParser and
                      displaying the results of the call
                      NO CODING TO DO HERE; DO NOT MODIFY

CFScanner.java      - for converting the text of the input file into
                      a sequence of tokens
                      NO CODING TO DO HERE; DO NOT MODIFY

CFToken.java        - defines a class for representing the tokens(terminals)
                      for this application.
                      NO CODING TO DO HERE; DO NOT MODIFY

We will use operators (from high priority to low)

-  prefix, for absolute complement, that is -S = ({ 0, 1, 2, ... } \ S)

   (rest are all infix and associate to the left)
@  for intersection
U  for union
\  for set difference
(+) for symmetric difference

The following are comparison operators that can only appear in test
conditions.

<=  for the is subset of relation  A <= B iff A is a subset of B
=   for set equality

We can inductively/recursively define CofinFin fully parenthesized
expressions as follows.

Basis
  a CofinFin constant is a CofinFin expression
  a variable is a CofinFin expression

If e1, e2, e3, and e4 are four CofinFin expressions then

(-e1)
(e1 @ e2)
(e1 U e2)
(e1 \ e2)
(e1 (+) e2)
(if e1 <= e2 then e3 else e4 endif)
(if e1 = e2 then e3 else e4 endif)

are each CofinFin expressions.

There will also be an expression in the input that gets translated away

let
  x1 = e1;
  x2 = e2;
  ...
  xn = en;
in
  mainExp
endlet

How to convert this source expression into a CFExp object is explained
in CFExpParser.  There is more discussion of all the coding in the source templates.



 
All the methods you need to code are specified in more detail in the classes
where they sit. Some throw exceptions, and you should match the specified messages
perfectly.

There are some initial test cases and "correct" output, but I may do much more
extensive testing of both valid and invalid expressions when I have time to 
generate the test cases.  I will supply them to you when I have them.

The tokens are defined in CFToken.java, and we give them and the grammar
rules (w/o lookaheads; the lookaheads are given in CFExpParser.java).
Their types are coded as int values in CFToken.java

   public static final int // for the token types
      SYMMETRICDIFF = 0, //  "(+)"  an operator
      ID = 1, // [a-zA-Z]+[a-zA-Z0-9]* except for reserved words
      LET = 2, // "let"      a reserved word
      ENDLET = 3, // "endlet"    a reserved word
      IF = 4, //  "if"       a reserved word
      ELSE = 5, // "else"     a reserved word
      ENDIF = 6, // "endif"    a reserved word
      NAT = 7, // 0|[1-9][0-9]*  nonnegative integer constant
      LEFTBRACE = 8,    // '{'
      RIGHTBRACE = 9,    // '}'
      LEFTPAREN = 10,    // '('
      RIGHTPAREN = 11,    // ')'
      SEMICOLON = 12,    // ';'
      COMMA =  13,    // ','
      SUBSETOF = 14, //  "<="  for is subset of 
      EQUALS = 15,  // '='   for equality comparisons of sets
      INTERSECTION = 16,  // '@'   for set intersection  
      UNION = 17,  // 'U'   for set union; in effect a reserved word
      SETDIFF = 18, // '\'   for binary set difference
      COMPLEMENT = 19,  // '-'   for unary set complement
      IN  = 20, //  "in"  for let expressions; so "in" is reserved
      THEN = 21, // "then" a reserved word
      CMP = 22, // "CMP" a reserved word
      EOS = 23, // "$" to stand for reaching the end of the string
      EOF = 24, // to signal the end of the file/string has been reached
      UNRECOGNIZED = 25; // for anything else

Note that for NAT and ID and UNRECOGNIZED, the object will also include an
accessible tokentString data member with the actual string value covered by
the token.

The grammar, which is given in CFParser.java, is reproduced here.
The variables are

<S>
<A>
<B>
<C>
<D>
<E>
<CONST>
<SET INTERIOR>
<NE SET INTERIOR>
<TEST>
<TEST SUFFIX>
<BLIST>

<S> is the start symbol.

The replacement rules are as follows.  In CFExpParser, we indicate where you
should use Arden's lemma to convert the recursion to iteration and what the
lookaheads are.

<S> ::= <E> EOS

<E> ::= <E> SYMMETRICDIFF <A> | <A>

<A> ::= <A> SETDIFF <B> | <B>

<B> ::= <B> UNION <C> | <C>

<C> ::= <C> INTERSECTION <D> | <D>


<D> ::= COMPLEMENT <D> | ID | <CONST> | LEFTPAREN <E> RIGHTPAREN |
        LET <BLIST> IN <E> ENDLET |
        IF <TEST> THEN <E> ELSE <E> ENDIF 

<CONST> ::= LEFTBRACE <SET INTERIOR> RIGHTBRACE | 
            CMP LEFTBRACE <SET INTERIOR> RIGHTBRACE

<SET INTERIOR> ::= "" | <NE SET INTERIOR>

<NE SET INTERIOR ::= NAT | NAT COMMA <NE SET INTERIOR>

<TEST> ::= <E> <TEST SUFFIX>

<TEST SUFFIX> ::= SUBSETOF <E> | EQUALS <E>

<BLIST> ::= "" | ID EQUALS <E> SEMICOLON <BLIST>
