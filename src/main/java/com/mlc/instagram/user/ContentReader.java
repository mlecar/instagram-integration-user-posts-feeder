package com.mlc.instagram.user;

import java.util.Scanner;

import org.springframework.stereotype.Component;

@Component
public class ContentReader {

    public String search(String content) {

        Scanner scanner = new Scanner(content);

        String line = null;
        while (scanner.hasNext()) {
            line = scanner.nextLine();

            if (line.contains("window._sharedData = ")) {
                break;
            }
        }

        scanner.close();
        return line.replaceFirst("^.*<script type=\"text/javascript\">window._sharedData = ", "").replaceAll(";</script>.*", "");

    }

}
