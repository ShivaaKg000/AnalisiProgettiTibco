package org.gaf;

import org.w3c.dom.Node;

import java.io.*;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static int countLines(File file){
        int lineCount = 0;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lineCount;
    }

    public static int countCharsExcludingSpaces(String filePath) {
        int charCount = 0;

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            int currentChar;
            while ((currentChar = bufferedReader.read()) != -1) {
                if (!Character.isSpaceChar(currentChar)) {
                    charCount++;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return charCount;
    }
}
