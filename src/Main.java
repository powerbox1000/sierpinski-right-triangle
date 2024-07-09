import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter recurse amount: ");
    final int recurse = scanner.nextInt();
    //if (recurse > 15) throw new Exception("Summertech has banned crashing computers sorry :(");
    final int numShapes = (int) Math.pow(3, recurse-1);
    scanner.close();
    if (recurse <= 0) {
      System.err.println("Recurse cannot be less than 1");
      System.exit(1);
    }

    final int rows = (int) Math.pow(2, recurse - 1);

    final boolean[][] shape;
    if (recurse > 1){
      shape = buildShape(new boolean[rows][rows], recurse, numShapes, new int[0]);
    } else {
      shape = new boolean[1][1];
      shape[0][0] = true;
    }
    
    FileWriter ihatethis = new FileWriter("triangle.txt");

    for (int row = 0; row < shape.length; row++) {
      final int len = (row + 1);
      for (int col = 0; col < len * 2; col++) {
        final int i = col >= len ? col - len : col;
        if (shape[row][i]) {
          if (col < len) {
            if (col + 1 == len){
              ihatethis.write("*");
            } else {
              ihatethis.write("* ");
            }
          } else {
            ihatethis.write("**");
          }
        } else {
          ihatethis.write("  ");
        }
        if (i == len - 1)
          ihatethis.write('\n');
      }
      System.out.println("Finished row " + (row + 1) + " out of " + shape.length);
    }
    ihatethis.close();

    System.out.println("Finished in " + (System.currentTimeMillis() - startTime) + " ms");
    System.exit(0);
  }

  public static int addArray(final int[] num){
    int x = 0;
    for (int i: num){
      x += i;
    }
    return x;
  }

  public static boolean[][] buildShape(
    boolean[][] shape,
    int recurse,
    int numShapes,
    int[] boxids
  ){
    if (recurse > 1){
      // recurse
      for (int i = 0; i < 3; i++){
        int[] nboxids = new int[boxids.length + 1];
        System.arraycopy(boxids, 0, nboxids, 0, boxids.length);
        nboxids[boxids.length] = i;
        shape = buildShape(shape, recurse - 1, numShapes, nboxids);
      }
    } else {
      // draw
      // calculate coordinates
      String firstBit = "";
      String secondBit = "";
      for (int i: boxids){
        if (i == 2){
          firstBit += "1";
          secondBit += "1";
        } else {
          firstBit += "0";
          secondBit += i;
        }
      }
      final int x = Integer.parseInt(firstBit, 2);
      final int y = Integer.parseInt(secondBit, 2);

      shape[y][x] = true;
    }
    
    return shape;
  }
}
