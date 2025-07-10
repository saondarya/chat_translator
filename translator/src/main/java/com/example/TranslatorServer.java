package com.example;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class TranslatorServer {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Translation Server running on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String sourceLang = in.readLine();
                    String targetLang = in.readLine();

                while (true) {
                    String text = in.readLine();
                    if (text == null || text.equalsIgnoreCase("exit")) {
                        break;
                    }

                    String translatedText = translateWithPython(text, sourceLang, targetLang);
                    out.println(translatedText);  // Removed "Translated: " prefix
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String translateWithPython(String text, String sourceLang, String targetLang) 
            throws IOException, InterruptedException {
            
            // Create a temporary Python script file
            Path tempScript = Files.createTempFile("translator", ".py");
            try {
                // Write the Python code to the file
                String pythonCode = 
                    "from googletrans import Translator\n" +
                    "import sys\n" +
                    "translator = Translator()\n" +
                    "try:\n" +
                    "    translation = translator.translate(sys.argv[1], src=sys.argv[2], dest=sys.argv[3])\n" +
                    "    print(translation.text)\n" +
                    "except Exception as e:\n" +
                    "    print(f\"Translation error: {str(e)}\")";
                
                Files.writeString(tempScript, pythonCode);

                // Prepare the command with arguments
                Process process = new ProcessBuilder(
                    "python3",
                    tempScript.toString(),
                    text,
                    sourceLang,
                    targetLang
                ).start();

                // Read output
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line);
                    }
                }

                // Read error stream
                try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        System.err.println("Python Error: " + errorLine);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    return "Translation failed (Python process error)";
                }

                return output.toString();
            } finally {
                Files.deleteIfExists(tempScript);
            }
        }
    }
}