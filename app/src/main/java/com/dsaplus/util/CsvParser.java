package com.dsaplus.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    private CsvParser() {}

    public static List<String[]> parseResource(String resourcePath) {
        List<String[]> records = new ArrayList<>();

        try (InputStream is = CsvParser.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                Logger.error("CSV", "Arquivo não encontrado: " + resourcePath);
                return records;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            boolean isFirstLine = true;
            StringBuilder multiline = new StringBuilder();

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                if (multiline.length() > 0) {
                    multiline.append("\n");
                }
                multiline.append(line);

                if (!hasBalancedQuotes(multiline.toString())) {
                    continue;
                }

                records.add(parseLine(multiline.toString()));
                multiline = new StringBuilder();
            }
        } catch (Exception e) {
            Logger.error("CSV", "Erro ao ler arquivo: " + e.getMessage());
        }

        return records;
    }

    private static boolean hasBalancedQuotes(String line) {
        int count = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '"') count++;
        }
        return count % 2 == 0;
    }

    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ';' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());

        return fields.toArray(new String[0]);
    }
}
