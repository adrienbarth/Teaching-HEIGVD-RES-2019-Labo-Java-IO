package ch.heigvd.res.labio.impl.filters;

import ch.heigvd.res.labio.impl.Utils;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\tHello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());
  private int index = 0;
  private boolean isNewLine = true;
  private String last = "";

  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    str = str.substring(off, off + len);

    if(str.contains("\n") || str.contains("\r") || str.contains("\r\n")) {
      String[] lines = Utils.getNextLine(str);
      if (isNewLine) {
        str = ++index + "\t" + lines[0];
      }

      isNewLine = lines[0].contains("\n") || lines[0].contains("\r") || lines[0].contains("\r\n");

      super.write(str, 0, str.length());
      write(lines[1], 0, lines[1].length());

    } else {
      if(isNewLine) {
        str = ++index + "\t" + str;
        isNewLine = false;
      }

      super.write(str, 0, str.length());
    }
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    super.write(new String(cbuf).toUpperCase(), off, len);
  }

  @Override
  public void write(int c) throws IOException {
    String current = new String("" + (char)c);

    if(last.equals("\r") && current.equals("\n"))
      return;

    last = current;

    write(new String("" + (char)c));
  }

}
