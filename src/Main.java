import java.util.Scanner;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

class Swappable {
  private char[] loadedRow;
  private int currRow = 0;

  public Swappable(int size) throws Exception {
    loadedRow = new char[size];

    Files.createDirectories(Paths.get("swap"));

    for (int row = 0; row < size; row++){
      char[] tmp = new char[size];
      for (int col = 0; col < size; col++){
        tmp[col] = '0';
      }
      BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(Paths.get("swap/swap_" + row), StandardCharsets.UTF_8), 1000000);
      writer.write(LZString.compressToBase64(String.valueOf(tmp)));
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
    if (currRow == row) return;
    if(unload) unloadRow();
    currRow = row;
    BufferedReader reader = new BufferedReader(new FileReader("swap/swap_" + currRow), 1000000);
    loadedRow = (LZString.decompressFromBase64(reader.readLine())).toCharArray();
    reader.close();
  }

  private void unloadRow() throws Exception {
    BufferedWriter writer = new BufferedWriter(new FileWriter("swap/swap_" + currRow), 1000000);
    writer.write(LZString.compressToBase64(String.valueOf(loadedRow)));
    writer.close();
  }

  public boolean get(int row, int col) throws Exception {
    if (row != currRow) loadRow(row);

    return loadedRow[col] == '1';
  }

  public void set(int row, int col, boolean val) throws Exception {
    if (row != currRow) loadRow(row);

    loadedRow[col] = (val ? '1' : '0');
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
    scanner.nextLine();

    if (recurse <= 0) {
      System.err.println("Recurse cannot be less than 1");
      System.exit(1);
    }

    final int rows = (int) Math.pow(2, recurse - 1);

    final float storageEstimate = (float) (Math.pow(2, recurse - 11) * rows) / 1000000;
    boolean cont = true;

    if (storageEstimate >= 1){
      System.out.print("This operation will take " + storageEstimate + "GB of disk space while the program runs (uncompressed; compression ratio unknown). Continue? (y/n) ");
      final String n = scanner.nextLine().toLowerCase();
      cont = (n.equals("y"));
      System.out.print('\n');
    }

    scanner.close();

    if (!cont) return;

    final Swappable shape;
    if (recurse > 1){
      shape = buildShape(new Swappable(rows), recurse, new int[0]);
    } else {
      shape = new Swappable(1);
      shape.set(0, 0, true);
    }
    
    BufferedWriter ihatethis = new BufferedWriter(new FileWriter("triangle.txt"), 64000);

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
    int[] boxids
  ) throws Exception {
    if (recurse > 1){
      // recurse
      for (int i = 0; i < 3; i++){
        int[] nboxids = new int[boxids.length + 1];
        System.arraycopy(boxids, 0, nboxids, 0, boxids.length);
        nboxids[boxids.length] = i;
        shape = buildShape(shape, recurse - 1, nboxids);
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
