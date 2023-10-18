package classifier;

import classifier.MarkovChain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class MarkovLetterCategory extends MarkovChain<String,Character> {

    public void countFrom(File languageFile, String language) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(languageFile))) {
            Optional<Character> prev = Optional.empty();
            for (;;) {
                int read = reader.read();
                if (read < 0) {
                    break;
                } else {
                    Optional<Character> next = vowelConsonant((char)read);
                    if (next.isPresent() && prev.isPresent()) {
                        char c = next.get();
                        count(prev, language, c);
                        prev = next;
                    }
                    else if (!prev.isPresent()) {
                        prev = next;
                    }
                }
            }
        }
    }

    public static Optional<Character> vowelConsonant(char c) {
        c = Character.toLowerCase(c);
        if (Character.isSpaceChar(c)) {
            c = ' ';
        }
        if (Character.isAlphabetic(c) || c == ' ') {
            if (("aeiou").contains(c+"")) {
                return Optional.of('v');
            }
            else if (c == ' ') {
                return Optional.of(' ');
            }
            else {
                return Optional.of('c');
            }
        } else {
            return Optional.empty();
        }
    }

    public static ArrayList<Character> vowelConsonants(String input) {
        ArrayList<Character> result = new ArrayList<>();
        for (char c: input.toCharArray()) {
            vowelConsonant(c).ifPresent(result::add);
        }
        return result;
    }
}