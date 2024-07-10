import java.util.Scanner;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

class Swappable {
  private boolean[] loadedRow;
  private int currRow = 0;

  public Swappable(int size) throws Exception {
    loadedRow = new boolean[size];

    for (int row = 0; row < size; row++){
      FileWriter writer = new FileWriter("swap/swap_" + row);
      for (int col = 0; col < size; col++){
        writer.write('0');
      }
      writer.close();
    }

    loadRow(0, false);

    System.out.println("Swap files generated.");
  }

  public int length(){
    return loadedRow.length;
  }

  private void loadRow(int row) throws Exception {
    loadRow(row, true);
  }

  private void loadRow(int row, boolean unload) throws Exception {
    if(unload) unloadRow();
    currRow = row;
    File currswap = new File("swap/swap_" + row);
    Scanner reader = new Scanner(currswap);
    char[] data = reader.nextLine().toCharArray();
    reader.close();
    for (int i = 0; i < data.length; i++){
      loadedRow[i] = (data[i] == '1');
    }
  }

  private void unloadRow() throws Exception {
    FileWriter writer = new FileWriter("swap/swap_" + currRow);
    for (int col = 0; col < loadedRow.length; col++){
      writer.write(loadedRow[col] ? '1' : '0');
    }
    writer.close();
  }

  public boolean get(int row, int col) throws Exception {
    if (row != currRow) loadRow(row);

    return loadedRow[col];
  }

  public void set(int row, int col, boolean val) throws Exception {
    if (row != currRow) loadRow(row);

    loadedRow[col] = val;
  }

  public void cleanSwap() throws Exception {
    for(File file: (new File("swap")).listFiles()) 
    if (!file.isDirectory()) 
        file.delete();
  }
}

public class Main {
  public static void main(String[] args) throws Exception {
    long startTime = System.currentTimeMillis();

    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter recurse amount: ");
    final int recurse = scanner.nextInt();
    final int numShapes = (int) Math.pow(3, recurse-1);
    scanner.close();
    if (recurse <= 0) {
      System.err.println("Recurse cannot be less than 1");
      System.exit(1);
    }

    final int rows = (int) Math.pow(2, recurse - 1);

    final Swappable shape;
    if (recurse > 1){
      shape = buildShape(new Swappable(rows), recurse, numShapes, new int[0]);
    } else {
      shape = new Swappable(1);
      shape.set(0, 0, true);
    }
    
    FileWriter ihatethis = new FileWriter("triangle.txt");

    for (int row = 0; row < shape.length(); row++) {
      final int len = (row + 1);
      for (int col = 0; col < len * 2; col++) {
        final int i = col >= len ? col - len : col;
        if (shape.get(row, i)) {
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
      System.out.println("Finished row " + (row + 1) + " out of " + shape.length());
    }
    ihatethis.close();

    System.out.println("Finished in " + (System.currentTimeMillis() - startTime) + " ms");
    shape.cleanSwap();
    System.exit(0);
  }

  public static Swappable buildShape(
    Swappable shape,
    int recurse,
    int numShapes,
    int[] boxids
  ) throws Exception {
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

      System.out.println("Drawing (" + x + ", " + y + ")");

      shape.set(y, x, true);
    }
    
    return shape;
  }
}
