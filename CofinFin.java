import java.util.*;

public class CofinFin {

   /// THIS IS THE TEMPLATE YOU SHOULD USE FOR THE TEAM PROJECT
   /*
    * 
    * 
    * Team members: Aleksandra Milinovic, Rick Beaudet, Stephen Lanna, Ryan McNally
    * 
    * 
    * 
    * Replace the stub code for each of the constructors and methods given below
    * with
    * correct code.
    * 
    * NOTE, TESTING FOR THE ERROR CONDITIONS IS WORTH 2 POINTS EACH AND GIVING THE
    * CORRECT ERROR MESSAGE IS ALSO WORTH 2 POINTS EACH.
    * 
    * 
    */
   private static final long RAND_SEED = 0L;

   private static final Random rng = new Random(RAND_SEED);

   private static final int Max = Integer.MAX_VALUE,
         MaxDiv2 = Max / 2;

   private static CofinFin emT;

   private boolean isComplemented;
   private TreeSet<Integer> theSet; // should never be null, even if
   // the set is empty

   /*
    * 
    * This class implements an instance of a subset of the natural numbers,
    * { 0, 1, 2, ... }, that is either finite or cofinite(that is, its complement
    * with respect to the natural numbers is theSet) according to the following
    * scheme.
    * 
    * There are two mutually exclusive cases
    * 
    * Case I: the represented set is finite
    * 
    * isComplemented is false
    * 
    * theSet has exactly the members of the set which are ordered ascending in
    * the tree.
    * 
    * Examples
    * 
    * 1. empty set
    * isComplemented is false
    * theSet is empty, BUT NOT NULL
    * 
    * 2. { 0, 2, 4 }
    * isComplemented is false
    * theSet has the values { 0, 2, 4 }
    * 
    * 
    * Case II: the represented is cofinite
    * 
    * isComplemented is true and theSet has all the values that are NOT in the
    * represented value.
    * 
    * Below we use N to stand for the set of natural numbers { 0, 1, 2, 3, ... }
    * 
    * Examples
    * 
    * 1. N = ( 0, 1, 2, 3, ... }
    * isComplemented is true
    * theSet is empty, BUT NOT NULL
    * 
    * 2. { 1, 3, 5, 6, 7, ... }
    * isComplemented is true
    * theSet has the values { 0, 2, 4 }
    * 
    * class invariant:
    * theSet is not null and never contains a value < 0
    * 
    * Constructors should establish the class invariant and mutators
    * should preserve it.
    * 
    */

   // constructors

   // I WILL CODE THIS ONE SO I CAN USE IT BELOW
   public CofinFin() {
      // constructs the rep of the empty set;
      // isComplemented is initialized to false by default
      theSet = new TreeSet<Integer>();

   }

   public CofinFin(boolean comp, int n) {

      this.theSet = new TreeSet<Integer>();
      this.isComplemented = comp;

      // Add n to theSet
      if (n >= 0) {
         theSet.add(n);
      }
   }

   public CofinFin(boolean comp, int[] A) {

      this.theSet = new TreeSet<Integer>();
      this.isComplemented = comp;

      // Iterate through A and add to theSet
      if (A != null) {
         for (Integer i : A) {
            if (i >= 0) {
               theSet.add(i);
            }
         }
      }
   }

   // mutators

   public void remove(int n) {
      if (n >= 0) {
         if (!this.isComplemented) {
            this.theSet.remove(n);
         } else {
            // If this isComplemented, add n
            this.theSet.add(n);
         }
      }

   }

   public void add(int n) {
      if (n >= 0) {
         if (!this.isComplemented) {
            this.theSet.add(n);
         } else {
            // If this isComplemented, remove n
            this.theSet.remove(n);
         }
      }
   }

   // operations

   public CofinFin union(CofinFin other) throws Exception {
      if (other == null) {
         throw new Exception("Invalid call to union: other is null");
      } else {
         // If the two sets are equal, this == the union of this and other
         if (this.equals(other)) {
            return this;
         } else {
            CofinFin union = new CofinFin();
            union.isComplemented = true;
            // If both sets are complemented, we only want to retain other
            if (this.isComplemented && other.isComplemented) {
               union.theSet = new TreeSet<Integer>(this.theSet);
               union.theSet.retainAll(other.theSet);
            }
            // If this isComplemented and not other, union will be other - this
            else if ((!this.isComplemented) && other.isComplemented) {
               union.theSet = new TreeSet<Integer>(other.theSet);
               union.theSet.removeAll(this.theSet);
            }
            // If other isComplemented and not this union will be this - other
            else if (this.isComplemented && (!other.isComplemented)) {
               union.theSet = new TreeSet<Integer>(this.theSet);
               union.theSet.removeAll(other.theSet);
            }
            // If both sets are complemented, union will be the this + other
            else {
               union.isComplemented = false;
               union.theSet = new TreeSet<Integer>(this.theSet);
               union.theSet.addAll(other.theSet);
            }
            return union;
         }
      }

   }

   public CofinFin intersect(CofinFin other) throws Exception {
      if (other == null) {
         throw new Exception("Invalid call to intersect: other is null");
      } else {
         /*
          * If both sets are equal, the intersection of this and other will be either
          * set.
          * return this
          */
         if (this.equals(other)) {
            return this;
         } else {
            CofinFin intersect = new CofinFin();
            intersect.isComplemented = false;

            // If neither set isComplemented, intersection will be this + other
            if (this.isComplemented && other.isComplemented) {
               intersect.isComplemented = true;
               intersect.theSet = new TreeSet<Integer>(this.theSet);
               intersect.theSet.addAll(other.theSet);
            }
            // If this isComplemented and not other, intersection will be this - other
            else if ((!this.isComplemented) && other.isComplemented) {
               intersect.theSet = new TreeSet<Integer>(this.theSet);
               intersect.theSet.removeAll(other.theSet);
            }
            // If other isComplemented and not this, intersection will be other - this
            else if (this.isComplemented && (!other.isComplemented)) {
               intersect.theSet = new TreeSet<Integer>(other.theSet);
               intersect.theSet.removeAll(this.theSet);
            }
            // If both sets are complemented, intersection will be this.retainAll(other)
            else {
               intersect.theSet = new TreeSet<Integer>(this.theSet);
               intersect.theSet.retainAll(other.theSet);
            }

            return intersect;
         }

      }
   }

   public CofinFin complement() {
      CofinFin complement = new CofinFin();
      complement.theSet = new TreeSet<Integer>(this.theSet);
      // Reverse isComplemented
      complement.isComplemented = !this.isComplemented;

      return complement;
   }

   public boolean isIn(int n) throws Exception {
      if (n < 0) {
         throw new Exception("Invalid call to isIn: n < 0");
      } else {
         if (this.isComplemented) {
            return !this.theSet.contains(n);
         } else {
            return this.theSet.contains(n);
         }
      }
   }

   public String toString() {
      /*
       * If isComplemented is false if the set is empty returns the string "{}" else
       * returns the string "{ v1, v2, ... , vk }" where the vi are the distinct
       * k values in the set sorted in increasing order For example, { 0, 2, 3 }
       * should return the string "{ 0, 2, 3 }" else returns the string CMPx
       * where x is the string for the set if isComplemented were false, for
       * example, all the natural numbers would have the string "CMP{}", the set
       * that is all natural numbers except { 1, 3, 5 }, would have the string
       * "CMP{ 1, 3, 5 }", etc.
       * 
       */

      boolean notEmpt = !theSet.isEmpty();

      StringBuffer bf = new StringBuffer();

      if (isComplemented)
         bf.append("CMP");

      bf.append('{');
      if (notEmpt) {
         bf.append(' ');

         Iterator<Integer> iter = theSet.iterator();

         while (iter.hasNext()) {
            bf.append(iter.next().toString());
            if (iter.hasNext())
               bf.append(", ");
         }
         bf.append(' ');
      }

      bf.append('}');

      return bf.toString();
   }

   public boolean equals(CofinFin other) throws Exception {
      if (other == null) {
         throw new Exception("Invalid call to equals: other is null");
      }
      // If neither set is complemented and both sets are equal, return true
      else {
         if (this.isComplemented == other.isComplemented && this.theSet.equals(other.theSet)) {
            return true;
         }
      }

      return false;
   }

   public boolean isSubsetOf(CofinFin other) throws Exception {
      /*
       * 
       * Error checking:
       * 
       * 1. checks that other is not null; if it is, it throws an exception with the
       * message
       * 
       * "Invalid call to isSubsetOf: other is null"
       * 
       * 
       * otherwise it returns true when the set represented by this is a subset
       * of the set represented by, and false if it is not a subset of the set
       * represented by other.
       * 
       * YOU NEED TO CODE THIS
       * 
       * 
       * Hint: do a case by case analysis
       * 
       * this other what do you need to test
       * 
       * finite finite
       * finite cofinite
       * cofinite finite
       * cofinite cofinite
       * 
       */
      if (other == null) {
         throw new Exception("Invalid call to isSubsetOf: other is null");
      } else {
         TreeSet<Integer> dif;

         if (this.isComplemented && !other.isComplemented) {
            return false;
         } else if (!this.isComplemented && other.isComplemented) {
            dif = new TreeSet<Integer>(this.theSet);
            dif.retainAll(other.theSet);
            return dif.isEmpty();
         } else if (!this.isComplemented && !other.isComplemented) {
            dif = new TreeSet<Integer>(this.theSet);
            dif.removeAll(other.theSet);
            return dif.isEmpty();
         } else {
            dif = new TreeSet<Integer>(other.theSet);
            dif.removeAll(this.theSet);
            return dif.isEmpty();
         }

      }
   }

   public int maximum() throws Exception {
      if (this.isComplemented) {
         throw new Exception("Invalid call to maximum: receiver is cofinite");
      } else if (!this.isComplemented && this.theSet.isEmpty()) {
         throw new Exception("Invalid call to maximum: receiver is empty");
      }

      // TreeSets are sorted ascending, returning last will give us the maximum
      return this.theSet.last();
   }

   public int minimum() throws Exception {
      if (this.theSet.isEmpty() && !this.isComplemented) {
         throw new Exception("Invalid call to minimum: receiver is empty");
      }

      // TreeSets are sorted ascending, if this !isComplemented, first will give us minimum
      if (!this.isComplemented) {
         return this.theSet.first();
      }
      // If we have an empty set, return 0
      if (this.theSet.isEmpty()) {
         return 0;
      }

      // If this isComplemented, iterate through theSet searching for the first value
      int i = 0;
      for (i = 0; i < Max; i++) {
         if (this.theSet.contains(i)) {
            continue;
         } else {
            break;
         }
      }

      return i;

   }

   private static // so they will all be initialized to null
   CofinFin
   // I don't use l for an identifier
   a, b, c, d, e, f, g, h, i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z,

         aa, ab, ac, ad, ae, af, ag, ah, ai, aj, ak, al, am, an,
         ao, ap, aq, ar, as, at, au, av, aw, ax, ay, az,

         ba, bb, bc, bd, be, bf, bg, bh, bi, bj, bk, bl, bm, bn,
         bo, bp, bq, br, bs, bt, bu, bv, bw, bx, by, bz,

         cc, dd, ee,

         newA, newB, newC; // 25 + 52 + 6 = 83 variables

   // the driver tests the methods
   public static void main(String[] args) throws Exception {

      boolean res;

      int ii, jj, kk;

      int[][] arrayConstants = {
            { -1, -2, -3, -4 },
            { 0, 1, 2, 3 },
            { -1, 0, -2, 1, -3, 2, -4 },
            { 2, 3, 5, 7 },
            { 1, 3, 5, 7 },
            { 1, 2, 3, 4 },
            { 0, 1, 5, 6, 7, 10, 11, 13 },
            { 2, 3, 6, 8, 9 },
            { 2, 4, 6, 8, 10, 13 },
            { 13, 15, 17, 19, 21 },
            { 3, 5, 7, 11 },
            { 1, 2, 3, 7 },
            { 10, 30, 50, 70 },
            { 10, 20, 30, 70 },
            { 0, 1, 4, 5, 6, 7, 9 },
            { 0, 1, 4, 5, 6, 7, 9, 10 },
            { 8, 9, 11, 12 },
            { 0, 1, 4, 5, 6, 7, 8, 10, 11, 12 },
            { 100, 200, 300, 400, 500, 1000 },
            { 0, 2, 4, 6, 8, 10, 12, 14 },
            { 1, 3, 5, 7, 9, 11, 13, 15, 17 },
            new int[0],
            null,
            { -1 },
            { 0 },
            { 100 },
            {}
      };

      CofinFin[] fromStaticVariables = {

            a, b, c, d, e, f, g, h, i, j, k, m, n, o, p, q, r, s, t, u, v, w, x, y, z,

            aa, ab, ac, ad, ae, af, ag, ah, ai, aj, ak, al, am, an,
            ao, ap, aq, ar, as, at, au, av, aw, ax, ay, az,

            ba, bb, bc, bd, be, bf, bg, bh, bi, bj, bk, bl, bm, bn,
            bo, bp, bq, br, bs, bt, bu, bv, bw, bx, by, bz,

            cc, dd, ee,

            newA, newB, newC },
            // 25 + 52 + 6 = 83 variables

            allTestCases = new CofinFin[arrayConstants.length * 2 + fromStaticVariables.length];

      //////////////// SIMPLE CONSTRUCTOR TESTS

      System.out.println("Simple Constructor Tests.\n");

      a = new CofinFin(false, -1);
      System.out.println("a = " + a);
      b = new CofinFin(true, -2);
      System.out.println("b = " + b);
      c = new CofinFin(false, 1000);
      System.out.println("c = " + c);
      d = new CofinFin(true, 1234);
      System.out.println("d = " + d);
      e = new CofinFin(false, 0);
      System.out.println("e = " + e);
      f = new CofinFin(true, 0);
      System.out.println("f = " + f);
      g = new CofinFin(false, 1);
      System.out.println("g = " + g);
      h = new CofinFin(true, 1);
      System.out.println("h = " + h);
      i = new CofinFin(false, Integer.MAX_VALUE);
      System.out.println("i = " + i);

      //////////////// ARRAY CONSTRUCTOR TESTS

      System.out.println("\nArray Constructor Tests\n");

      for (int i = 0; i < arrayConstants.length * 2; i++) {
         if (i < arrayConstants.length)
            allTestCases[i] = new CofinFin(false, arrayConstants[i]);
         else
            allTestCases[i] = new CofinFin(true, arrayConstants[i - arrayConstants.length]);
         System.out.println("i = " + i + " CofinFin is " + allTestCases[i]);
      }

      //////////////// ISIN, REMOVE AND ADD TESTS

      System.out.println("\nisIn and remove and add Tests\n");

      for (int i = 0; i < arrayConstants.length * 2; i++) {
         CofinFin temp = allTestCases[i];
         boolean isIn0 = temp.isIn(0),
               isIn1 = temp.isIn(1),
               isInMaxDiv2 = temp.isIn(MaxDiv2),
               isInMax = temp.isIn(Max),
               isInNeg = false;

         try {
            isInNeg = temp.isIn(-1);
         } catch (Exception e) {
            System.out.println("Call to isIn on " + temp.toString() + " with input value -1\n"
                  + "threw an exception with message '" + e.getMessage() + "'");
         }

         System.out.println("The five isIn results are " + isIn0 + " " + isIn1
               + " " + isInMaxDiv2 + " " + isInMax + " " + isInNeg);

         temp.remove(-1);
         temp.remove(0);
         temp.remove(1);
         temp.remove(MaxDiv2);
         temp.remove(Max);
         System.out.println("i = " + i + " and after the four removes, temp is " + temp);

         // restore prior value
         if (isIn0)
            temp.add(0);
         if (isIn1)
            temp.add(1);
         if (isInMaxDiv2)
            temp.add(MaxDiv2);
         if (isInMax)
            temp.add(Max);

         System.out.println("After restoration, value is " + temp);
      }

      //////////////// MORE ADD TESTS

      // construct a few explicitly
      j = new CofinFin();
      j.isComplemented = false;
      j.theSet.add(1000);

      k = new CofinFin();
      k.isComplemented = true;
      k.theSet.add(1000);

      m = new CofinFin();
      m.isComplemented = true;
      m.theSet.add(500);

      j.add(-1);
      k.add(-2);
      m.add(Integer.MIN_VALUE);

      System.out.println("j = " + j.toString());
      System.out.println("k = " + k.toString());
      System.out.println("m = " + m.toString());

      System.out.println("\nAdd Tests\n");

      j = new CofinFin(true, new int[] { 1, 2, 3 });
      j.add(-1);
      j.add(Integer.MIN_VALUE);
      System.out.println("After adding -1 and " + Integer.MIN_VALUE +
            " to j, j = " + j.toString());

      for (ii = arrayConstants.length * 2; ii < allTestCases.length; ii++) {
         if (allTestCases[ii] == null) { // create a new instance
            int size = rng.nextInt(10);
            allTestCases[ii] = new CofinFin(rng.nextBoolean(), null);
            for (int v = 0; v < size; v++)
               allTestCases[ii].add(rng.nextInt(Max));
         }
         // make a copy of the current value
         CofinFin tc = new CofinFin();
         tc.isComplemented = allTestCases[ii].isComplemented;
         tc.theSet.addAll(allTestCases[ii].theSet);

         System.out.println("Testing add for " + tc.toString());
         tc.add(-2);
         System.out.println("After adding -2, new value is " + tc.toString());
         tc.add(Integer.MIN_VALUE);
         System.out.println("After adding " + Integer.MIN_VALUE + ", new value is " + tc.toString());

         for (jj = 0; jj < 100;) {
            tc.add(jj);
            System.out.println("After adding " + jj + ", new value is " + tc.toString());
            jj = 2 * jj + 1;
         }

      }

      //////////////// UNION TESTS

      System.out.println("\nUnion Tests\n");

      for (int i = 0; i < allTestCases.length; i++) {
         CofinFin outer = allTestCases[i];
         for (int j = 0; j < allTestCases.length; j++)
            System.out.println(" (i,j) = (" + i + ", " + j + ") result = " +
                  outer.union(allTestCases[j]));
      }

      // test exception
      try {
         CofinFin xxx = allTestCases[0].union(null);
         System.out.println("result is " + xxx);
      } catch (Exception e) {
         System.out.println("Union with null other threw an exception with message '"
               + e.getMessage() + "'");
      }

      //////////////////// INTERSECTION TESTS

      System.out.println("\nIntersection Tests\n");

      for (int i = 0; i < allTestCases.length; i++) {
         CofinFin outer = allTestCases[i];
         for (int j = 0; j < allTestCases.length; j++)
            System.out.println(" (i,j) = (" + i + ", " + j + ") result = " +
                  outer.intersect(allTestCases[j]));
      }

      // test exception
      try {
         CofinFin xxx = allTestCases[0].intersect(null);
         System.out.println("result is " + xxx);
      } catch (Exception e) {
         System.out.println("Intersection with null other threw an exception with message '"
               + e.getMessage() + "'");
      }

      //////////////////// COMPLEMENT TESTS

      System.out.println("\nComplement Tests\n");

      for (int i = 0; i < allTestCases.length; i++)
         System.out.println("i = " + i + " result = " +
               allTestCases[i].complement());

      // test that complement does not share the Set data member
      j = new CofinFin(false, new int[] { 1, 2, 3 });
      m = j.complement();
      m.add(1);
      m.remove(4);
      System.out.println("after adding 1 to m and removing 4, j is " + j);
      j = new CofinFin(true, new int[] { 1, 2, 3 });
      m = j.complement();
      m.remove(1);
      m.add(4);
      System.out.println("after adding 4 to m and removing 1, j is " + j);

      ///////////////// IS IN TESTS

      System.out.println("\nisIn Tests\n");

      for (int i = 0; i < allTestCases.length; i++) {
         CofinFin outer = allTestCases[i];
         for (int j = 0; j < 50; j++)
            System.out.println(" (i,j) = (" + i + ", " + j + ") isIn result = " +
                  outer.isIn(j));
      }

      // test exception
      try {
         CofinFin xxx = new CofinFin();
         xxx.add(-1);
         System.out.println("result is " + xxx);
      } catch (Exception e) {
         System.out.println("Adding -1 threw an exception with message '"
               + e.getMessage() + "'");
      }
      try {
         CofinFin xxx = new CofinFin();
         xxx.add(Integer.MIN_VALUE);
         System.out.println("result is " + xxx);
      } catch (Exception e) {
         System.out.println("Adding integer min value threw an exception with message '"
               + e.getMessage() + "'");
      }

      ///////////////// SUBSET TESTS

      System.out.println("\nSubset Tests\n");

      for (int i = 0; i < allTestCases.length; i++) {
         CofinFin outer = allTestCases[i];
         for (int j = 0; j < allTestCases.length; j++)
            System.out.println(" (i,j) = (" + i + ", " + j + ") subset result = " +
                  outer.isSubsetOf(allTestCases[j]));
      }

      int[] f22arr = { 1, 2, 3 };
      CofinFin f22A = new CofinFin(false, f22arr),
            f22B = new CofinFin(true, f22arr);

      System.out.println("\n{ 1,2,3 } subset of CMP{ 1,2,3 } = " +
            f22A.isSubsetOf(f22B) + " the other way around is " + f22B.isSubsetOf(f22A) + "\n");

      // test exception
      emT = new CofinFin();
      try {

         System.out.println("Result for empty set is subset of null = " + emT.isSubsetOf(null));

      } catch (Exception ee) {
         System.out.println("Call to isSubsetOf with null other threw an exception with message '"
               + ee.getMessage() + "'");
      }

      ///////////////// EQUALS TESTS

      System.out.println("\nEquals Tests\n");

      for (int i = 0; i < allTestCases.length; i++) {
         CofinFin outer = allTestCases[i];
         for (int j = 0; j < allTestCases.length; j++)
            System.out.println(" (i,j) = (" + i + ", " + j + ") equals result = " +
                  outer.equals(allTestCases[j]));
      }

      // test exception
      try {

         System.out.println("empty set equals null is " + emT.equals(null));

      } catch (Exception e) {
         System.out.println("Call to equals with null other threw an exception with message '"
               + e.getMessage() + "'");
      }

      /////////////// MINIMUM TESTS

      System.out.println("\nMinimum Tests\n");

      for (ii = 0; ii < allTestCases.length; ii++) {
         System.out.println("Minimum test # " + ii);
         try {
            System.out.println("Min value is " + allTestCases[ii].minimum());
         } catch (Exception e) {
            System.out.println("The call threw an exception with message '" +
                  e.getMessage() + "'");
         }
      }

      /////////////// MAXIMUM TESTS

      System.out.println("\nMaximum Tests\n");

      for (ii = 0; ii < allTestCases.length; ii++) {
         System.out.println("Maximum test # " + ii);
         try {
            System.out.println("Max value is " + allTestCases[ii].maximum());
         } catch (Exception e) {
            System.out.println("The call threw an exception with message '" +
                  e.getMessage() + "'");
         }
      }

      ////////////// TESTS FOR SIDE EFFECTS OF THE OPERATIONS

      System.out.println("\nSide Effect Tests.\n");

      CofinFin x1 = new CofinFin(),
            x2 = new CofinFin();

      x1.theSet.add(Integer.MAX_VALUE - 1);
      x2.theSet.add(Integer.MAX_VALUE - 2);
      x2.isComplemented = true;

      for (ii = 0; ii < allTestCases.length; ii++) {
         allTestCases[ii].union(x2);
         x2.union(allTestCases[ii]);
         allTestCases[ii].intersect(x1);
         x1.intersect(allTestCases[ii]);
         allTestCases[ii].complement();
         System.out.println("ii = " + ii + " " + allTestCases[ii]);
      }

   }
}
