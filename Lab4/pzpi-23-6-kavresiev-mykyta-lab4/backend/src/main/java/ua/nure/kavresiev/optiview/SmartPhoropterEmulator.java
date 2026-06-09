package ua.nure.kavresiev.optiview;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class SmartPhoropterEmulator extends JFrame {

    private String serverUrl = "http://localhost:8080/api/visits/save";
    private long doctorId = 3;

    // елементи інтерфейсу
    private JLabel statusLabel;
    private JLabel infoLabel;
    private JTextArea logArea;
    private JButton measureButton;
    private JButton settingsButton;
    private JTextField patientIdField;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Random random = new Random();

    public SmartPhoropterEmulator() {
        setTitle("IoT Device: Smart Phoropter v2.1");
        setSize(500, 500); // Трохи збільшив висоту
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(3, 1));
        displayPanel.setBackground(new Color(20, 20, 20)); // Темний фон
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("SMART PHOROPTER", SwingConstants.CENTER);
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 22));

        infoLabel = new JLabel("DocID: " + doctorId + " | Ready to Scan", SwingConstants.CENTER);
        infoLabel.setForeground(Color.GREEN);
        infoLabel.setFont(new Font("Consolas", Font.PLAIN, 14));

        statusLabel = new JLabel("STATUS: ONLINE", SwingConstants.CENTER);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));

        displayPanel.add(titleLabel);
        displayPanel.add(infoLabel);
        displayPanel.add(statusLabel);


        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.LIGHT_GRAY);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);


        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        // панель введення пацієнта
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(50, 50, 50));
        JLabel patientLabel = new JLabel("ID Пацієнта:");
        patientLabel.setForeground(Color.WHITE);

        patientIdField = new JTextField("7", 10); // "7" - значення за замовчуванням
        patientIdField.setFont(new Font("Arial", Font.BOLD, 14));
        patientIdField.setHorizontalAlignment(JTextField.CENTER);

        inputPanel.add(patientLabel);
        inputPanel.add(patientIdField);

        // панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(50, 50, 50));

        measureButton = new JButton("🔬 ВИМІРЯТИ (SCAN)");
        measureButton.setBackground(new Color(0, 150, 0));
        measureButton.setForeground(Color.WHITE);
        measureButton.setFont(new Font("Arial", Font.BOLD, 12));
        measureButton.setFocusPainted(false);
        measureButton.addActionListener(e -> performMeasurement());

        settingsButton = new JButton("⚙️ НАЛАШТУВАННЯ");
        settingsButton.setBackground(Color.GRAY);
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setFocusPainted(false);
        settingsButton.addActionListener(e -> openSettings());

        buttonPanel.add(measureButton);
        buttonPanel.add(settingsButton);

        controlPanel.add(inputPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        // додавання на форму
        add(displayPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        log("Пристрій ініціалізовано.");
        log("Сервер: " + serverUrl);
    }

    private void performMeasurement() {
        // зчитування ID пацієнта перед початком
        String patientIdText = patientIdField.getText().trim();
        long currentPatientId;

        try {
            currentPatientId = Long.parseLong(patientIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Введіть коректний числовий ID пацієнта!", "Помилка введення", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("STATUS: MEASURING...");
                    statusLabel.setForeground(Color.YELLOW);
                    measureButton.setEnabled(false);
                    patientIdField.setEnabled(false);
                });

                log("--- Початок вимірювання для Пацієнта #" + currentPatientId + " ---");
                Thread.sleep(1000);
                log("Сканування сітківки...");
                Thread.sleep(800);

                // бізнес логіка
                String[] diagnoses = {"Міопія слабкого ступеня", "Гіперметропія", "Астигматизм", "Здоровий", "Спазм акомодації"};
                String diagnosis = diagnoses[random.nextInt(diagnoses.length)];

                double leftEye = 0.1 + (1.0 - 0.1) * random.nextDouble();
                double rightEye = 0.1 + (1.0 - 0.1) * random.nextDouble();
                String visualAcuity = String.format("%.1f/%.1f", leftEye, rightEye).replace(',', '.');
                String treatment = diagnosis.equals("Здоровий") ? "Не потребує" : "Рекомендовано окуляри/лінзи";

                // формування JSON вручну
                String jsonBody = String.format(
                        "{\"patientId\":%d, \"doctorId\":%d, \"diagnosis\":\"%s\", \"treatment\":\"%s\", \"visualAcuity\":\"%s\"}",
                        currentPatientId, doctorId, diagnosis, treatment, visualAcuity
                );

                log("Дані сформовано: " + jsonBody);
                log("Відправка на сервер...");


                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(serverUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                SwingUtilities.invokeLater(() -> {
                    if (response.statusCode() == 200) {
                        statusLabel.setText("STATUS: SUCCESS (200 OK)");
                        statusLabel.setForeground(Color.GREEN);
                        log("УСПІХ! Сервер зберіг дані.");
                    } else {
                        statusLabel.setText("STATUS: ERROR " + response.statusCode());
                        statusLabel.setForeground(Color.RED);
                        log("Помилка сервера: " + response.body());
                    }
                    measureButton.setEnabled(true);
                    patientIdField.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("STATUS: CONNECTION ERROR");
                    statusLabel.setForeground(Color.RED);
                    log("Критична помилка: " + e.getMessage());
                    measureButton.setEnabled(true);
                    patientIdField.setEnabled(true);
                });
            }
        }).start();
    }

    private void openSettings() {
        JTextField urlField = new JTextField(serverUrl);
        JTextField docIdField = new JTextField(String.valueOf(doctorId));

        Object[] message = {
                "URL Сервера:", urlField,
                "ID Лікаря (поточний користувач):", docIdField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Налаштування IoT Клієнта", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                serverUrl = urlField.getText();
                doctorId = Long.parseLong(docIdField.getText());

                infoLabel.setText("DocID: " + doctorId + " | Ready to Scan");
                log("Налаштування оновлено.");
                log("Новий URL: " + serverUrl);
                log("Новий лікар: " + doctorId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Некоректні дані!", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    public static void main(String[] args) {
        // запуск
        SwingUtilities.invokeLater(() -> {
            new SmartPhoropterEmulator().setVisible(true);
        });
    }
}