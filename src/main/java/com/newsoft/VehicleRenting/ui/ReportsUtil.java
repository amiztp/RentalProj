package main.java.com.newsoft.VehicleRenting.ui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsUtil {

    private static class CustomerEntry {
        String nic; LocalDateTime dateTime; String vehicleType;
    }

    private static class PaymentEntry {
        String nic; double amount; LocalDateTime timestamp;
    }

    /**
     * Create a reports panel (charts and header) for the given date range.
     * This can be embedded into any container so the date pickers and results
     * appear in the same window.
     */
    public static JPanel createReportsPanel(LocalDateTime fromLdt, LocalDateTime toLdt) {
        java.util.List<CustomerEntry> customers = loadCustomerEntries();
        java.util.List<PaymentEntry> payments = loadPaymentEntries();

        Map<String,Integer> bookingsCount = new HashMap<>();
        for (CustomerEntry ce : customers) {
            if (ce.dateTime == null) continue;
            if (!ce.dateTime.isBefore(fromLdt) && !ce.dateTime.isAfter(toLdt)) {
                String type = (ce.vehicleType == null || ce.vehicleType.isEmpty()) ? "Unknown" : ce.vehicleType;
                bookingsCount.put(type, bookingsCount.getOrDefault(type, 0) + 1);
            }
        }

        Map<String,Double> paymentsSum = new HashMap<>();
        for (PaymentEntry pe : payments) {
            if (pe.timestamp == null) continue;
            if (pe.timestamp.isBefore(fromLdt) || pe.timestamp.isAfter(toLdt)) continue;

            String type = "Unknown";
            if (pe.nic != null && !pe.nic.isEmpty()) {
                CustomerEntry best = null;
                for (CustomerEntry ce : customers) {
                    if (ce.nic != null && ce.nic.equalsIgnoreCase(pe.nic) && ce.dateTime != null && !ce.dateTime.isAfter(pe.timestamp)) {
                        if (best == null || ce.dateTime.isAfter(best.dateTime)) best = ce;
                    }
                }
                if (best != null && best.vehicleType != null && !best.vehicleType.isEmpty()) type = best.vehicleType;
            }
            paymentsSum.put(type, paymentsSum.getOrDefault(type, 0.0) + pe.amount);
        }

        JPanel content = new JPanel(new BorderLayout(10,10));
        content.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        content.setBackground(Color.WHITE);

        JLabel header = new JLabel("Reports from " + (fromLdt.toLocalDate()) + " to " + (toLdt.toLocalDate()));
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(new Color(11, 111, 175));
        content.add(header, BorderLayout.NORTH);

        JPanel charts = new JPanel(new GridLayout(1,2,10,10));
        charts.setBackground(Color.WHITE);
        charts.add(createBarChartPanel("Bookings by Vehicle Type", bookingsCount));
        charts.add(createBarChartPanelDouble("Payments by Vehicle Type (Rs)", paymentsSum));

        content.add(charts, BorderLayout.CENTER);

        return content;
    }

    private static java.util.List<CustomerEntry> loadCustomerEntries() {
        List<CustomerEntry> list = new ArrayList<>();
        File f = new File("Data/Customerdata.csv"); if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCSVLine(line);
                if (fields.length < 9) continue;
                CustomerEntry ce = new CustomerEntry();
                ce.nic = fields[1].trim();
                String dateStr = fields.length >= 9 ? fields[8].trim() : "";
                ce.vehicleType = fields.length >= 10 ? fields[9].trim() : "";
                ce.dateTime = parseDateTimeFlexible(dateStr);
                list.add(ce);
            }
        } catch (Exception e) { System.err.println("Failed to read Customerdata.csv: " + e.getMessage()); }
        return list;
    }

    private static java.util.List<PaymentEntry> loadPaymentEntries() {
        List<PaymentEntry> list = new ArrayList<>();
        File f = new File("Data/payments.csv"); if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); if (header == null) return list;
            String[] headers = parseCSVLine(header);
            java.util.Map<String,Integer> idx = new java.util.HashMap<>();
            for (int i=0;i<headers.length;i++) idx.put(headers[i].trim().toLowerCase(), i);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = parseCSVLine(line);
                PaymentEntry pe = new PaymentEntry();
                if (idx.containsKey("nic")) { int i = idx.get("nic"); if (i < fields.length) pe.nic = fields[i].trim(); }
                if (idx.containsKey("amount")) { int i = idx.get("amount"); if (i < fields.length) { try { pe.amount = Double.parseDouble(fields[i].trim()); } catch (Exception ex) { pe.amount = 0; } } }
                else if (fields.length >= 8) { try { pe.amount = Double.parseDouble(fields[7].trim()); } catch (Exception ex) { pe.amount = 0; } }
                if (idx.containsKey("timestamp")) { int i = idx.get("timestamp"); if (i < fields.length) pe.timestamp = parseDateTimeFlexible(fields[i].trim()); }
                else if (fields.length >= 10) { pe.timestamp = parseDateTimeFlexible(fields[9].trim()); }
                list.add(pe);
            }
        } catch (Exception e) { System.err.println("Failed to read payments.csv: " + e.getMessage()); }
        return list;
    }

    private static LocalDateTime parseDateTimeFlexible(String s) {
        if (s == null || s.isEmpty()) return null; s = s.trim();
        try { return LocalDateTime.parse(s); } catch (Exception e) {}
        try { DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); return LocalDateTime.parse(s, f); } catch (Exception e) {}
        try { java.time.LocalDate d = java.time.LocalDate.parse(s); return d.atStartOfDay(); } catch (Exception e) {}
        return null;
    }

    private static String[] parseCSVLine(String line) {
        java.util.List<String> fields = new ArrayList<>(); StringBuilder current = new StringBuilder(); boolean inQuotes = false;
        for (int i=0;i<line.length();i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i+1 < line.length() && line.charAt(i+1) == '"') { current.append('"'); i++; } else { inQuotes = !inQuotes; }
            } else if (c == ',' && !inQuotes) { fields.add(current.toString()); current = new StringBuilder(); } else { current.append(c); }
        }
        fields.add(current.toString()); return fields.toArray(new String[0]);
    }

    private static JPanel createBarChartPanel(String title, Map<String,Integer> data) {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,230,230)), BorderFactory.createEmptyBorder(8,8,8,8)));
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(new BarChartPanelInt(data), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createBarChartPanelDouble(String title, Map<String,Double> data) {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,230,230)), BorderFactory.createEmptyBorder(8,8,8,8)));
        panel.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(new BarChartPanelDouble(data), BorderLayout.CENTER);
        return panel;
    }

    private static class BarChartPanelInt extends JPanel {
        private Map<String,Integer> data; BarChartPanelInt(Map<String,Integer> data) { this.data = data; setBackground(Color.WHITE); }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (data == null || data.isEmpty()) { g2.setColor(Color.GRAY); g2.drawString("No data", 10, 20); return; }
            int w = getWidth(); int h = getHeight(); int padding = 30; int availableW = w - padding*2; int availableH = h - padding*2;
            java.util.List<String> keys = new ArrayList<>(data.keySet()); int n = keys.size(); int max = 1; for (int v : data.values()) if (v > max) max = v;
            int barWidth = Math.max(10, availableW / Math.max(1,n) - 10); int x = padding;
            for (String k : keys) { int val = data.get(k); int barH = (int) ((val / (double) max) * (availableH - 40)); int y = padding + (availableH - barH);
                g2.setColor(new Color(11,111,175)); g2.fillRect(x, y, barWidth, barH); g2.setColor(Color.DARK_GRAY); g2.drawString(String.valueOf(val), x, y - 6);
                g2.setColor(Color.BLACK); int labelY = padding + availableH + 14; g2.drawString(k, x, labelY);
                x += barWidth + 10; }
        }
    }

    private static class BarChartPanelDouble extends JPanel {
        private Map<String,Double> data; BarChartPanelDouble(Map<String,Double> data) { this.data = data; setBackground(Color.WHITE); }
        @Override protected void paintComponent(Graphics g) { super.paintComponent(g); Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (data == null || data.isEmpty()) { g2.setColor(Color.GRAY); g2.drawString("No data", 10, 20); return; }
            int w = getWidth(); int h = getHeight(); int padding = 30; int availableW = w - padding*2; int availableH = h - padding*2;
            java.util.List<String> keys = new ArrayList<>(data.keySet()); int n = keys.size(); double max = 1; for (double v : data.values()) if (v > max) max = v;
            int barWidth = Math.max(10, availableW / Math.max(1,n) - 10); int x = padding;
            for (String k : keys) { double val = data.get(k); int barH = (int) ((val / max) * (availableH - 40)); int y = padding + (availableH - barH);
                g2.setColor(new Color(46,125,50)); g2.fillRect(x, y, barWidth, barH); g2.setColor(Color.DARK_GRAY); g2.drawString(String.format("%.0f", val), x, y - 6);
                g2.setColor(Color.BLACK); int labelY = padding + availableH + 14; g2.drawString(k, x, labelY); x += barWidth + 10; }
        }
    }

}
