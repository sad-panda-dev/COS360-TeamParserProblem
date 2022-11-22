/*
YOU WILL NEED TO CODE SOME OF THE METHODS IN THIS CLASS.
Basically, all the methods for the grammar variables except the method for
<S>.
This is the class that provides the parsing service.
It is passed a CFScanner to use in the constructor and that is
the CFScanner it will use while it exists.  Generally, a scanner 
breaks up the raw input text into "tokens" such as reserved words, identifiers,
constants, operators, etc.  The chief scanner methods are "lookahead", which 
delivers the value of the next token in the input, and "consume", which removes
the next token from the input.
The only externally exposed method that is called  is parseNext, which either
returns a CFExp object based on the parse of a prefix of the file used by the
CFScanner, or throws an exception.  If it returns a CFExp object, the scanner
is left at the EOS token ($).
You should not modify the S method, but you will need to code all of the
methods for the other grammar variables.
Generally, the rules for a variable will be of the form
<VAR> ::= rhs1 | rhs2 | ... | rhsn
and your method for VAR will have the structure
private returnTypeForVAR VAR(){
   
   if (lookahead is not in the set for VAR)
      throw an exception w a message for that set using the
      private static String getErrorMessage(String methodName, Set<Integer> tokenIds, CFScanner sc)
      method, where tokenIds is the set of tokens for VAR
   else
      if (lookahead is one of the ones in the lookahead set for rhs1)
         process rhs1;
      else if (lookahead is one of the ones in rhs2)
         process rhs2;
      ...
      else // must be in the last lookahead set
         process rhsn;
         
}
The sets for each grammar variable are given in CFToken.  If the variable's
name is NAME, then CFToken.NAMESet will contain the lookaheads.  For example,
for grammar variable A, CFToken.ASet is the set that contains all of A's 
lookaheads.
If rhsj is of the form
X1 X2 ... Xm
where each Xi is either a variable or a token, then they are dealt with
sequentially.  Each Xi will have a scrap of code.  If Xi is a variable, then
there will be a call to the method for the variable which should return some 
result to be used.
If Xi is a token then the lookahead should be checked for a match with the 
token.  If it does not match, an exception should be thrown with the message from
a call to
private static String getErrorMessage(String methodName, int id, CFScanner sc){
   
where methodName is the name of this variable's method,int id is the 
id for the expected token, and CFScanner is the CFScanner object being used.
If it matches, and if Xi is an information bearing token, such as a NAT or
an ID, or a specific operator, the information it carries should be
noted (there is a getTokenString method for obtaining the actual string of
the NAT or ID instances).  Punctuation tokens are usually just to help with
the parse.  All tokens should be consumed by their code fragment, that is,
the CFScanner's consume method should be called to advance the lookahead
to the next token.
The overall control structure of the parser is based on the grammar, which 
we give in full here with lookahead sets for each rhs.  The rules for each variable
are also given below with the method for the variable.  The tokens, which are
given as upper case identifiers , are described in CFToken.java
Generally speaking, for an input token sequence that is in the language
generated by the grammar, the parser will excecute code according to a
preorder traversal of the parse tree, but the conversion of some 
recursive replacement rules to iterations does alter that.
<S> is the start symbol.
<S> ::= <E> EOS
Lookahead sets
union of the lookahead sets for <E>
LET, IF, LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID
<E> ::= <E> SYMMETRICDIFF <A> | <A>
You should convert the two righthand sides to   <A>(SYMMETRICDIFF <A>)*
to construct the method for <E>.  This eliminates the left recursion and
allows you to use a loop.  The lookahead sets for  the variables
<E>, <A>, <B>, <C>, and <D> are all the same.
LEFTPAREN, COMPLEMENT, CMP, LEFTBRACE, ID, LET, IF
<D> has several different rhs's that will partition this set of
tokens, but the others all convert to a single rhs of the form
<VAR>(OPERATORTOKEN <VAR>)*
which you process with a loop, as described with <E> above.
CFExp result = VAR();
while (lookahead is OPERATORTOKEN){
   consume();
   CFExp temp = VAR();
   result = result OPERATORTOKEN temp; // conceptually; you are building the expression
                                       // object whose left sub is the prior value of result,
                                       // and whose right sub is temp, and whose root is
                                       // the operator token;
}
It could be done differently by introducing new variables and
getting rid of the left recursion, but then you might throw a
different error message in some circumstances.  Here's an 
illustration of how that would be accomplished for <E>.
Originally,
<E> ::= <E> SYMMETRICDIFF <A> | <A>
Add a new variable <NEW> and replacements
<E> ::=  <A> <NEW>
<NEW> ::= ""  |  SYMMETRICDIFF <A> <NEW>
The lookahead set for the first <NEW> rhs is Follow(<NEW>),
which should be (to accurately calculate it, I'd have to 
change the rest of the grammar).
RIGHTPAREN, SUBSETOF, EQUALS, THEN, ELSE, ENDIF, SEMICOLON,
ENDLET, EOS
any token that can follow a completely parsed expression.
the NEW method would need to take a CFExp input parameter
for the expression returned by the prior call to A, call
it prevExp, and if the lookahead is for "", just return it.
If the lookahead is SYMMETRICDIFF, NEW consumes the token,
then calls A to get an expression nextExp, and then calls 
itself, passing in an expression object for
prevExp SYMMETRICDIFF nextExp
and it returns the result of the final, recursive call to NEW
Left associative operators are more easily handled by using
Arden't lemma to convert to a loop rather than a recursion.
Here are the rest of the replacement rules.
<A> ::= <A> SETDIFF <B> | <B>
convert to  <B> (SETDIFF <B>)*
<B> ::= <B> UNION <C> | <C>
convert to  <C> (UNION <C>)*
<C> ::= <C> INTERSECTION <D> | <D>
convert to <D> (INTERSECTION <D>)*
<D> accomplishes both the complement operation and the
    "atoms";  Note the let and if expressions in effect have 
    their own punctuation parentheses, let-endlet and if-endif. 
    
<D> ::= COMPLEMENT <D> | ID | <CONST> | LEFTPAREN <E> RIGHTPAREN
        LET <BLIST> IN <E> ENDLET |
        IF <TEST> THEN <E> ELSE <E> ENDIF
        
Lookahead sets are 
COMPLEMENT | ID | CMP, LEFTBRACE | LEFTPAREN | LET | IF
Converting using Arden's lemma we get
<D> = COMPLEMENT*(LET <BLIST> IN <E> ENDLET |
                  IF <TEST> THEN <E> ELSE <E> ENDIF |
                  ID | <CONST> | LEFTPAREN <E> RIGHTPAREN)
You would use a loop to count the number of occurrences of
COMPLEMENT, and then below the loop check for the lookahead
in a six branch if or switch, five for the valid lookaheads,
and the sixth for an invalid lookahead.
<CONST> ::= LEFTBRACE <SET INTERIOR> RIGHTBRACE | CMP LEFTBRACE <SET INTERIOR> RIGHTBRACE
Lookahead sets
LEFTBRACE | CMP
<SET INTERIOR> ::= "" | <NE SET INTERIOR>
Lookahead sets
RIGHTBRACE | NAT
<NE SET INTERIOR> ::= NAT | NAT COMMA <NE SET INTERIOR>
converts to NAT (COMMA NAT)*
Lookahead sets
NAT
<TEST> ::= <E> <TEST SUFFIX>
Lookahead sets
(union of the lookahead sets for rhs's of <E>)
<TEST SUFFIX> ::= SUBSETOF <E> | EQUALS <E>
Lookahead sets
SUBSETOF | EQUALS
<BLIST> ::= "" | ID EQUALS <E> SEMICOLON <BLIST>  // x = {};  y = a (+) b \ x; 
Lookahead sets
IN | ID
**********************************************************************************/
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CFExpParser {
   // this scanner object will be used repeatedly;
   // it does not appear to work consistently in between calls when it is bound to
   // standard in by the driver, so it's better to read from the test data files
   private static CFScanner lex;

   public CFExpParser(CFScanner sc) throws Exception {

      if (sc == null)
         throw new Exception("error in CFExpParser constructor : scanner sc is null");
      else
         lex = sc;
   }

   /*
    * 
    * Builds the error message when there is a set of tokens that are expected.
    * 
    * 
    * 
    ***********************************************************************/
   private static String getErrorMessage(String methodName, Set<Integer> tokenIds, CFScanner sc) {

      StringBuilder bld = new StringBuilder();
      CFToken curr = sc.lookahead(),
            prev = sc.getPrevToken();

      bld.append("error in method " + methodName + " : unexpected token " + curr);

      bld.append("\nis token number " + sc.getCurrTokenNumber());
      if (prev == null)
         bld.append("\nNo previous token.");
      else
         bld.append("\nprevious token was " + prev);
      bld.append("\ncurrent line number is " + sc.getCurrLineNum());
      bld.append("\nposition after the current token is " + sc.getCurrPos());
      bld.append("\nExpected tokens are : ");
      for (Integer i : tokenIds) {
         bld.append(CFToken.TOKEN_LABELS[i]);
         bld.append(' ');
      }
      return bld.toString();
   }

   /*
    * 
    * Builds the error message when there is a single token that is expected.
    * 
    ***********************************************************************/
   private static String getErrorMessage(String methodName, int id, CFScanner sc) {

      StringBuilder bld = new StringBuilder();
      CFToken curr = sc.lookahead(),
            prev = sc.getPrevToken();

      bld.append("error in method " + methodName + " : unexpected token " +
            curr);
      bld.append("\nis token number " + sc.getCurrTokenNumber());
      if (prev == null)
         bld.append("\nNo previous token.");
      else
         bld.append("\nprevious token was " + prev);
      bld.append("\ncurrent line number is " + sc.getCurrLineNum());
      bld.append("\nposition after the current token is " + sc.getCurrPos());
      bld.append("\nExpected token is " + CFToken.TOKEN_LABELS[id]);
      return bld.toString();
   }

   /*
    * 
    * parse the next expression and return it or throw an exception
    * 
    */
   public CFExp parseNext() throws Exception {
      if (lex.getAtEOF())
         throw new Exception("error in parseNext : scanner is at end of file at start of parse");
      else
         return S();
   }
   /*
    * 
    * The grammar rule is
    * 
    * <S> ::= <E> EOS
    * 
    * but we will not consume the EOS symbol so we can put all the
    * expressions in a single file.
    * 
    * YOU DO NOT CODE MODIFY THIS METHOD.
    */

   private CFExp S() throws Exception {
      CFToken tk = lex.lookahead();
      int tkT = tk.getTokenType();

      if (CFToken.SSet.contains(tkT)) {
         CFExp result = E();
         tk = lex.lookahead();
         tkT = tk.getTokenType();
         if (tkT != CFToken.EOS)
            throw new Exception("error in S method : expression is not followed by $");
         else
            return result;
      } else
         throw new Exception(getErrorMessage("S", CFToken.SSet, lex));

   }
   /*
    * 
    * YOU MUST CODE THE REMAINING METHODS FOR THE GRAMMAR VARIABLES.
    * 
    * The general scheme is based on the replacement rules for the variable
    * and is described above.
    * 
    **************************************************************************/

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * The grammar rule is
    * 
    * 
    * <E> ::= <E> SYMMETRICDIFF <A> | <A>
    * 
    * convert the two righthand sides to <A>(SYMMETRICDIFF <A>)*
    * 
    * return the appropriate CFExp object or throw an exception.
    * 
    *******************************************************************************/
   private CFExp E() throws Exception { // struct based on Briggs comments in header

      CFToken tk = lex.lookahead(); // get next token
      CFExp result; // for compiler

      // If the token type is in the set E.
      if (CFToken.ESet.contains(tk.getTokenType())) {

         result = A(); // Set results exp to A
         CFExp temp;
         tk = lex.lookahead(); // Get the next token

         // As long as the token type is 0 (symmdiff),
         // build the expression object whose
         // left sub is the prior value of result,
         // and whose right sub is temp,
         // and whose root is the operator token;
         while (tk.getTokenType() == CFToken.SYMMETRICDIFF) {
            lex.consume();
            temp = A(); // get prior expression
            //lex.consume(); // Remove unwanted ws. No effect if tk is EOF.
            tk = lex.lookahead(); // Next token without advancing

            result = new CFBinary(CFToken.SYMMETRICDIFF, result, temp);
         }

      } else { // not in Eset
         throw new Exception(getErrorMessage("E", CFToken.ESet, lex));
      }

      return result;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <A> ::= <A> SETDIFF <B> | <B>
    * convert to <B> (SETDIFF <B>)*
    * return the appropriate CFExp object or throw an exception.
    * 
    *************************************************************************/
   private CFExp A() throws Exception {
      // see comments from E()
      CFToken tk = lex.lookahead();
      CFExp result;

      if (CFToken.ASet.contains(tk.getTokenType())) {

         result = B();
         CFExp temp;
         tk = lex.lookahead();

         while (tk.getTokenType() == CFToken.SETDIFF) { // 18
            lex.consume();
            temp = B();
            //lex.consume();
            tk = lex.lookahead();
            result = new CFBinary(CFToken.SETDIFF, result, temp);
         }

      } else {
         throw new Exception(getErrorMessage("A", CFToken.ASet, lex));
      }

      return result;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <B> ::= <B> UNION <C> | <C>
    * convert to <C> (UNION <C>)*
    * return the appropriate CFExp object or throw an exception.
    * 
    *************************************************************************/
   private CFExp B() throws Exception {

      // see comments from E()
      CFToken tk = lex.lookahead();
      CFExp result;

      if (CFToken.BSet.contains(tk.getTokenType())) {

         result = C();
         CFExp temp;
         tk = lex.lookahead();

         while (tk.getTokenType() == CFToken.UNION) { // 18
            lex.consume();
            temp = C();
            //lex.consume();
            tk = lex.lookahead();
            result = new CFBinary(CFToken.UNION, result, temp);
         }

      } else {
         throw new Exception(getErrorMessage("B", CFToken.BSet, lex));
      }

      return result;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <C> ::= <D> INTERSECTION <C> | <D>
    * 
    * by Arden's lemma, this means <C> = (<D> INTERSECTION)*<D>
    * but intersection is an associative operator, so we can
    * convert to <D>(INTERSECTION <D>)*
    * return the appropriate CFExp object or throw an exception.
    * 
    *************************************************************************/
   private CFExp C() throws Exception {

      // see comments from E()
      CFToken tk = lex.lookahead();
      CFExp result;

      if (CFToken.CSet.contains(tk.getTokenType())) {

         result = D();
         CFExp temp;
         tk = lex.lookahead();

         while (tk.getTokenType() == CFToken.INTERSECTION) {
            lex.consume();
            temp = D();

            tk = lex.lookahead();
            result = new CFBinary(CFToken.INTERSECTION, result, temp);
         }

      } else {
         throw new Exception(getErrorMessage("C", CFToken.CSet, lex));
      }

      return result;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * Here is the rule
    * 
    * <D> ::= COMPLEMENT <D> | ID | <CONST> | LEFTPAREN <E> RIGHTPAREN
    * LET <BLIST> IN <E> ENDLET |
    * IF <TEST> THEN <E> ELSE <E> ENDIF
    * 
    * Lookahead sets are
    * COMPLEMENT | ID | CMP, LEFTBRACE | LEFTPAREN | LET | IF
    * Converting using Arden's lemma we get
    * <D> = COMPLEMENT*(LET <BLIST> IN <E> ENDLET |
    * IF <TEST> THEN <E> ELSE <E> ENDIF |
    * 
    * and if you use this you can have an initial loop that counts how many
    * COMPLEMENT's there are and then only use CFUnary on the result if the
    * count is odd, since two in a row cancel out. This is an optimization
    * and will affect the display, so you must do it to match my results.
    * 
    * return the appropriate CFExp object or throw an exception.
    * 
    * You should start, roughly, with
    * 
    * if (lookahead is in set for D){
    * int compCount = 0;
    * while lookahead is complement{
    * compCount++;
    * consume the token;
    * }
    * // and here it's wise to again test for the lookahead
    * // being in the set, so that you can assume it is
    * if (lookahead is in set for D){
    * // use a switch to choose the correct rhs based
    * // on the lookahead, and construct the expression
    * // for that rhs in some local variable;
    * // if the compCount is odd, create a
    * // unary expression object from the expression in the local
    * // variable and return it, if even then just return the local
    * // variable
    * }
    * else
    * throw an exception with the set message
    * }
    * else
    * throw an exception with the set message
    * 
    * 
    *************************************************************************/
   private CFExp D() throws Exception {

      CFToken tk = lex.lookahead();
      boolean isComp = false;
      int compCount = 0;

      // Error out if not in D set
      if (!CFToken.DSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("D", CFToken.DSet, lex));
      }

      // Get compliment count
      // Set boolean based on odd/true or even/false
      while (tk.getTokenType() == CFToken.COMPLEMENT) {
         compCount++;
         lex.consume();
         tk = lex.lookahead();
      }
      if (compCount % 2 == 1) {
         isComp = true;
      }

      // Next token. Error out if not in D set.
      if (!CFToken.DSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("D", CFToken.DSet, lex));
      }

      // for LET IN ENDLET
      if (tk.getTokenType() == CFToken.LET) {

         lex.consume();
         tk = lex.lookahead();
         Map<String, CFExp> bmap = BLIST(); // provides expr object modified by substitutions
         
         tk = lex.lookahead();
         // Set check for IN in D set
         if (!(tk.getTokenType() == CFToken.IN)) { // 20
            throw new Exception(getErrorMessage("D", CFToken.IN, lex));
         }

         lex.consume(); // ws
         CFExp result = E().substitute(bmap);
         tk = lex.lookahead();

         // Set check ENDLET in D
         if (!(tk.getTokenType() == CFToken.ENDLET)) {
            throw new Exception(getErrorMessage("D", CFToken.ENDLET, lex));
         }

         lex.consume(); // ws

         // If it is complement, return unary
         // else just return local variable
         if (isComp) {
            return new CFUnary(result);
         }
         return result;

      }

      // Left Paren E Right Paren
      else if (tk.getTokenType() == CFToken.LEFTPAREN) {

         lex.consume();
         CFExp result = E(); // hold E expression for CFUn
         tk = lex.lookahead();

         // Check RPREM in D set
         if (!(tk.getTokenType() == CFToken.RIGHTPAREN)) {
            throw new Exception(getErrorMessage("D", CFToken.RIGHTPAREN, lex));
         }

         lex.consume();

         // If it is complement, return unary
         // else just return local variable
         if (isComp) {
            return new CFUnary(result);
         }
         return result;
      }

      // for id 1
      else if (tk.getTokenType() == CFToken.ID) {

         CFVar result = new CFVar(tk.getTokenString());
         lex.consume();

         // If it is complement, return unary
         // else just return local variable
         if (isComp)
            return new CFUnary(result);
         return result;
      }

      else if (tk.getTokenType() == CFToken.IF) {

         lex.consume();
         Object subs[] = TEST();

         tk = lex.lookahead();

         // set check for THEN in D
         if (!(tk.getTokenType() == CFToken.THEN)) {
            throw new Exception(getErrorMessage("D", CFToken.THEN, lex));
         }

         lex.consume();
         CFExp exp = E(); // hold E expression for CFConditional
         tk = lex.lookahead();

         // set check for ELSE in d
         if (!(tk.getTokenType() == CFToken.ELSE)) {
            throw new Exception(getErrorMessage("D", CFToken.ELSE, lex));
         }

         lex.consume();
         CFExp exp1 = E(); // hold E expression for CFConditional
         tk = lex.lookahead();

         // set check for ENDIF
         if (!(tk.getTokenType() == CFToken.ENDIF)) {
            throw new Exception(getErrorMessage("D", CFToken.ENDIF, lex));
         }

         lex.consume();
         CFConditional result = new CFConditional((Integer) subs[1], (CFExp) subs[0], (CFExp) subs[2], exp, exp1);

         // If it is complement, return unary
         // else just return local variable
         if (isComp) {
            return new CFUnary(result);
         }
         return result;

      }

      // For Const
      else {

         /* Get CONST() and initialize new CFCONS
            If it is complement, return unary
            else just return local variable */

         CofinFin resultConst = CONST();
         CFConst result = new CFConst(resultConst);

         if (isComp) {
            return new CFUnary(result);
         }

         return result;

      }

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <CONST> ::= LEFTBRACE <SET INTERIOR> RIGHTBRACE | CMP LEFTBRACE <SET
    * INTERIOR> RIGHTBRACE
    * Lookahead sets
    * LEFTBRACE | CMP
    * return the appropriate CofinFin object or throw an exception.
    * 
    *************************************************************************/
   private CofinFin CONST() throws Exception {

      CFToken tk = lex.lookahead();
      boolean cmp = false;
      CofinFin result;
      // Check to see if it's in the lookahead set, otherwise throw exception
      if (!CFToken.CONSTSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("CONST", CFToken.CONSTSet, lex));
      }

      // Check to see if it's a compliment, if yes then trip flag and consume
      if (tk.getTokenType() == CFToken.CMP) {
         cmp = true;
         lex.consume();
         tk = lex.lookahead();
      }

      if (tk.getTokenType() == CFToken.LEFTBRACE) {
         // Check for { equivalence, if it is consume otherwise throw exception
         lex.consume();
         result = new CofinFin(cmp, SETINTERIOR());
         tk = lex.lookahead();

         //Check to see if lookahead is int SETINTERIOR, if not throw exception, other
         //wise build CofinFin object with cmp flag value and call to SETINTERIOR() then consume
         if(!CFToken.SETINTERIORSet.contains(tk.getTokenType())) {
            throw new Exception(getErrorMessage("CONST", CFToken.SETINTERIORSet, lex));
         }

         // Check for } equivalence, if not throw exception otherwise return the CofinFin
         // object
         if (!(tk.getTokenType() == CFToken.RIGHTBRACE)) {
            throw new Exception(getErrorMessage("CONST", 9, lex));
         }

         lex.consume();
         return result;
      } else {
         throw new Exception(getErrorMessage("CONST", 8, lex));
      }

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <SET INTERIOR> ::= "" | <NE SET INTERIOR>
    * Lookahead sets
    * RIGHTBRACE | NAT
    * 
    * 
    * return the appropriate int[] object for the numbers in the set,
    * or throw an exception if the parse fails.
    * 
    * Note
    * 
    * new int[0]
    * 
    * is fine for the empty string alternative.
    *************************************************************************/
   private int[] SETINTERIOR() throws Exception {

      CFToken tk = lex.lookahead();

      // Check the lookahead set, if token is not in throw exception
      if (!CFToken.SETINTERIORSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("SETINTERIOR", CFToken.SETINTERIORSet, lex));
      }
      // If were dealing with a right brace this signifies empty string, so we return
      // empty array
      if (tk.getTokenType() == CFToken.RIGHTBRACE) {
         int[] result = new int[0];
         return result;
      }

      // //Otherwise we create an int array of size[size of the list returned by
      // NESETINTERIOR]
      // int[] result = new int[NESETINTERIOR().size()];

      List<Integer> nSetList = NESETINTERIOR(); // Local list with NSETINT reuslts. TODO this method
      int[] result = new int[nSetList.size()]; // Array to be filled with nSetList ints

      // //Loop through List returned by NESETINTERIOR and add items to the array we
      // created and return it
      // for(int i = 0; i < result.length; i++) {
      for (int i = 0; i < nSetList.size(); i++) { // Get size of list not length
         result[i] = nSetList.get(i); // get val of local list, not call NSETIT again
      }

      // lex.consume();
      return result;
   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <NE SET INTERIOR ::= NAT | NAT COMMA <NE SET INTERIOR>
    * 
    * which converts to NAT (COMMA NAT)*
    * Lookahead sets
    * NAT
    * return the appropriate List<Integer> object for the sequence of NAT instances
    * or throw an exception.
    * 
    *************************************************************************/
   private List<Integer> NESETINTERIOR() throws Exception {

      // Catch error of token not in NESTINT set
      // Create list and add token string to it
      // While next tokens are comma add its string to the list
      // check for token not = to nat
      // Return completed list containing sequence of NAT insts

      // Token
      CFToken tk = lex.lookahead();

      // Catch error
      if (!CFToken.NESETINTERIORSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("NESETINTERIOR", CFToken.NESETINTERIORSet, lex));
      }

      // Else convert to sequence of nat
      // List to hold the sequence
      List<Integer> resultList = new LinkedList<Integer>();

      // Add the int
      resultList.add(Integer.parseInt(tk.getTokenString()));
      
      lex.consume(); // skip to next token
      tk = lex.lookahead(); // return the token

      // While the token is a comma
      while (tk.getTokenType() == CFToken.COMMA) {
         lex.consume(); // get next token
         tk = lex.lookahead();

         // Catch error if token not non-neg int const
         if (tk.getTokenType() != CFToken.NAT) {
            throw new Exception(getErrorMessage("NESETINTERIOR", CFToken.NAT, lex));
         }

         // Else add the ints representing the current string
         resultList.add(Integer.parseInt(tk.getTokenString()));
         lex.consume();

         // Continue loop with next token, until token is not ","
         tk = lex.lookahead();
      }

      // End of loop, return list of nats
      return resultList;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <TEST> ::= <E> <TEST SUFFIX>
    * Lookahead sets
    * (union of the lookahead sets for rhs's of <E>)
    * 
    * We'll have it return an Object[] res of size 3
    * 
    * res[0] is the first expression, for <E>, of type CFExp
    * res[1] is Integer, the token type of the relational operator in <TEST SUFFIX>
    * res[2] is the second expression of the test, which comes from <TEST SUFFIX>
    * 
    *************************************************************************/
   private Object[] TEST() throws Exception {

      CFToken tk = lex.lookahead();

      if (!CFToken.TESTSet.contains(tk.getTokenType())) {
         getErrorMessage("TEST", CFToken.TESTSet, lex);
      }

      Object[] result = new Object[3];
      Object[] suffix;

      result[0] = E(); // contains CFExp for E BEFORE TESTSUFF()

      // set the return variable with results of textSuffix
      suffix = TESTSUFFIX();
      result[1] = suffix[0]; // Integer, the token type of the relational operator in <TEST SUFFIX>
      result[2] = suffix[1]; // second expression of the test, which comes from <TEST SUFFIX>

      return result;
   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * <TEST SUFFIX> ::= SUBSETOF <E> | EQUALS <E>
    * Lookahead sets
    * SUBSETOF | EQUALS
    * 
    * We'll have it return Object[] res of size 2
    * 
    * res[0] is Integer, the token type of the relational operator
    * res[1] is the second expression of the test, the value given in <E>, of type
    * CFExp
    * 
    *************************************************************************/
   private Object[] TESTSUFFIX() throws Exception {

      CFToken tk = lex.lookahead();

      if (!CFToken.TESTSUFFIXSet.contains(tk.getTokenType())) {
         throw new Exception(getErrorMessage("TESTSUFFIX", CFToken.TESTSUFFIXSet, lex));
      }

      Object[] result = new Object[2];
      result[0] = tk.getTokenType(); // set as the token type of the operator

      lex.consume();

      result[1] = E(); // rhs of expr

      return result;

   }

   /*
    * 
    * YOU MUST CODE THIS
    * 
    * This one is trickier.
    * 
    * <BLIST> ::= "" | ID EQUALS <E> SEMICOLON <BLIST>
    * Lookahead sets
    * IN | ID
    * 
    * Convert to (ID EQUALS <E> SEMICOLON)*, which amounts to a
    * list of single bindings
    * 
    * ID EQUALS <E> SEMICOLON
    * 
    * Initialize a Map object to the empty (but not null) map.
    * 
    * process each
    * 
    * 
    * ID EQUALS <E> SEMICOLON
    * 
    * in the list as follows. Obtain the string of the ID, s, and
    * the CFExp object the E method returns, call it exp. Then use the current
    * value of the Map object to obtain a substituted version of exp (it's okay
    * to store that in exp itself), where the substituted version is
    * constructed from exp's substitute method using the current value
    * for the map object. Then install (s, substituted version of exp)
    * in the map object.
    * 
    * When you have processed all the single bindings in the list, return
    * the final map object.
    * 
    * Of course, throw an exception if you encounter a wrong token.
    * 
    * Note, you might overwrite an earlier binding, for example
    * let
    * x = {1};
    * x = x U {2};
    * in
    * x U {3}
    * endlet
    * would first install (x, {1}) in the map, and then replace that entry
    * with (x, {1} U {2}) because the substitution would replace the x in x U {2}
    * with
    * {1} and then replace the (x, {1}) in the map with (x, {1} U {2}).
    * The later var = expression of the let are allowed to use variables introduced
    * by earlier ones.
    * 
    * When the substitute replaces a variable x in an expression with the
    * entry for x in the map, call that entry exp, then it should make a deep
    * copy of exp. The reason is so there will not be side effects to the
    * exp object, which might happen if the reference to the it were directly
    * shared.
    * 
    * During the processing of the rhs of DATA_CONVERSION
    * 
    * LET <BLIST> IN <E> ENDLET
    * 
    * the <BLIST> build of the map object, and then that map is used to replace
    * the variables of the map in the CFExp object that <E> returns, call that
    * object
    * expr. The final return value of the rhs is (ignoring the possibility of
    * a complement operator) the expr object modified by the substitutions.
    *************************************************************************/
   private Map<String, CFExp> BLIST() throws Exception {

      CFToken tk = lex.lookahead();
      Map<String, CFExp> result = new HashMap<String, CFExp>();

      // In blistset
      if (CFToken.BLISTSet.contains(tk.getTokenType())) {

         // Loop for id equals <E> semi
         // While 1
         while (tk.getTokenType() == CFToken.ID) {

            lex.consume();

            //
            tk = lex.lookahead();
            // not 15
            if (!(tk.getTokenType() == CFToken.EQUALS)) {
               throw new Exception(getErrorMessage("BLIST", CFToken.EQUALS, lex));
            }

            // Put substiution string in results map as <token string, E() substitution
            // version>
            lex.consume(); //needed to consume1
            result.put(lex.lookahead().getTokenString(), E().substitute(result));
            
            lex.consume(); //needed to consume2
            tk = lex.lookahead(); // next loop
         }

      } else { // not in set
         throw new Exception(getErrorMessage("BLIST", CFToken.BLISTSet, lex));
      }

      return result;
   }

}