package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslatorClientGUI {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JComboBox<String> sourceLangDropdown, targetLangDropdown;
    private JTextArea inputTextArea, outputTextArea;
    private JButton translateButton, swapButton;
    private JLabel imageLabel;

    private static final List<String> LANGUAGES = Arrays.asList(
            "English", "Spanish", "French", "German", "Chinese", "Japanese", "Korean", "Hindi", "Tamil", "Telugu",
            "Kannada", "Malayalam", "Russian", "Portuguese", "Arabic", "Italian", "Dutch", "Greek", "Turkish",
            "Hebrew", "Swedish", "Thai", "Vietnamese", "Bengali", "Gujarati", "Urdu", "Punjabi", "Marathi"
    );

    // Mapping of language names to codes
    private static final Map<String, String> LANGUAGE_CODES = new HashMap<>();

    static {
        LANGUAGE_CODES.put("English", "en");
        LANGUAGE_CODES.put("Spanish", "es");
        LANGUAGE_CODES.put("French", "fr");
        LANGUAGE_CODES.put("German", "de");
        LANGUAGE_CODES.put("Chinese", "zh");
        LANGUAGE_CODES.put("Japanese", "ja");
        LANGUAGE_CODES.put("Korean", "ko");
        LANGUAGE_CODES.put("Hindi", "hi");
        LANGUAGE_CODES.put("Tamil", "ta");
        LANGUAGE_CODES.put("Telugu", "te");
        LANGUAGE_CODES.put("Kannada", "kn");
        LANGUAGE_CODES.put("Malayalam", "ml");
        LANGUAGE_CODES.put("Russian", "ru");
        LANGUAGE_CODES.put("Portuguese", "pt");
        LANGUAGE_CODES.put("Arabic", "ar");
        LANGUAGE_CODES.put("Italian", "it");
        LANGUAGE_CODES.put("Dutch", "nl");
        LANGUAGE_CODES.put("Greek", "el");
        LANGUAGE_CODES.put("Turkish", "tr");
        LANGUAGE_CODES.put("Hebrew", "he");
        LANGUAGE_CODES.put("Swedish", "sv");
        LANGUAGE_CODES.put("Thai", "th");
        LANGUAGE_CODES.put("Vietnamese", "vi");
        LANGUAGE_CODES.put("Bengali", "bn");
        LANGUAGE_CODES.put("Gujarati", "gu");
        LANGUAGE_CODES.put("Urdu", "ur");
        LANGUAGE_CODES.put("Punjabi", "pa");
        LANGUAGE_CODES.put("Marathi", "mr");
    }

    public TranslatorClientGUI() {
        JFrame frame = new JFrame("Translator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Translator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 50));
        headerPanel.add(titleLabel);
        headerPanel.setBackground(Color.lightGray);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        // Title & Subtitle
        JLabel mainTitle = new JLabel("Translate Text");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 40));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST; // Align title to the left
        mainPanel.add(mainTitle, gbc);

        JLabel subtitle = new JLabel("<html><div align='left'>Translate text instantly between any language pairs.</div></html>");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 34));
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align subtitle to the left
        mainPanel.add(subtitle, gbc);

        // Language Selection
        sourceLangDropdown = new JComboBox<>(LANGUAGES.toArray(new String[0]));
        targetLangDropdown = new JComboBox<>(LANGUAGES.toArray(new String[0]));
        sourceLangDropdown.setPreferredSize(new Dimension(200, 30)); // Width: 200, Height: 30
        targetLangDropdown.setPreferredSize(new Dimension(200, 30)); // Width: 200, Height: 30
        swapButton = new JButton("⇄");
        swapButton.addActionListener(e -> swapLanguages());

        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setBackground(Color.WHITE);
        JLabel sourceLabel = new JLabel("Source Language:");
        sourceLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        languagePanel.add(sourceLabel);
        languagePanel.add(sourceLangDropdown);
        languagePanel.add(swapButton);
        JLabel targetLabel = new JLabel("Target Language:");
        targetLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        languagePanel.add(targetLabel);
        languagePanel.add(targetLangDropdown);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST; // Align language panel to the left
        mainPanel.add(languagePanel, gbc);

        // Input and Output Text Areas
        inputTextArea = new JTextArea(5, 20);
        outputTextArea = new JTextArea(5, 20);
        outputTextArea.setEditable(false);

        JPanel textPanel = new JPanel(new GridLayout(1, 2, 40, 10));
        textPanel.setBackground(Color.WHITE);

        // Use JScrollPane for text areas
        textPanel.add(new JScrollPane(inputTextArea));
        textPanel.add(new JScrollPane(outputTextArea));

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST; // Align text areas to the left
        mainPanel.add(textPanel, gbc);

        // Translate Button
        translateButton = new JButton("Translate");
        translateButton.setFont(new Font("Serif", Font.BOLD, 30)); // Custom font
        translateButton.setBackground(new Color(30, 144, 255)); // Blue color
        translateButton.setForeground(Color.BLACK); // Text color
        translateButton.setPreferredSize(new Dimension(200, 50));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(translateButton);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST; // Align button to the left
        mainPanel.add(buttonPanel, gbc);

        // Image Section
        ImageIcon imageIcon = new ImageIcon("/Users/saondaryak/Downloads/translator.jpg");
        Image image = imageIcon.getImage().getScaledInstance(800, 1000, Image.SCALE_SMOOTH);
        imageLabel = new JLabel(new ImageIcon(image));

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imageLabel);

        // Main Layout
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.WEST);
        contentPanel.add(imagePanel, BorderLayout.EAST);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Initialize dropdowns
        sourceLangDropdown.setSelectedItem("English");
        targetLangDropdown.setSelectedItem("Spanish");

        // Add action listener for translation
        translateButton.addActionListener(new TranslateAction());
    }

    private void swapLanguages() {
        int sourceIndex = sourceLangDropdown.getSelectedIndex();
        sourceLangDropdown.setSelectedIndex(targetLangDropdown.getSelectedIndex());
        targetLangDropdown.setSelectedIndex(sourceIndex);
    }

    private class TranslateAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String sourceLang = LANGUAGE_CODES.get(sourceLangDropdown.getSelectedItem());
            String targetLang = LANGUAGE_CODES.get(targetLangDropdown.getSelectedItem());
            String text = inputTextArea.getText();

            if (sourceLang == null || targetLang == null || text.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select valid languages and enter text.");
                return;
            }

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println(sourceLang);
                out.println(targetLang);
                out.println(text);

                String translatedText = in.readLine();
                outputTextArea.setText(translatedText);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error connecting to server.");
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TranslatorClientGUI::new);
    }
}
