package tuke.fei;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * Class used for parallel writing to csv file
 */
public class CsvWriter {
    private Stack<String> lines = new Stack<>();
    private boolean shouldRun = true;
    private Thread t;
    PrintWriter writer;

    /**
     * constructor
     * @param filename name of the file to which will be data written
     */
    public CsvWriter(String filename) {
        try {
            writer = new PrintWriter(filename, "UTF-8");
            t = new Thread(() -> {
                int n = 0;
                while (shouldRun || !lines.isEmpty()) {
                    if (!lines.isEmpty()) {
                        writer.println(lines.pop());
                        n++;
                    }
                }
                System.out.println("writer ended on " + n);
            });
            t.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add line to stack
     * @param line line to write to csv
     */
    synchronized void writeln(String line) {
        lines.push(line);
    }

    /**
     * Csv will finish remaining lines and stops
     */
    void stop() {
        shouldRun = false;
        try {
            t.join();
            writer.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
