import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {
    private final Color BACKGROUND = new Color(15, 23, 42);
    private final Color SIDEBAR = new Color(30, 41, 59);
    private final Color PANEL = new Color(30, 41, 59);
    private final Color TEXT = new Color(226, 232, 240);
    private final Color MUTED = new Color(148, 163, 184);
    private final Color ACCENT = new Color(56, 189, 248);

    private JComboBox<String> algorithmBox;
    private JTextField quantumField, contextField;
    private JTextField idField, arrivalField, burstField, priorityField;
    private DefaultTableModel processModel;
    private JTable processTable;
    private JButton runButton;
    private JLabel simulationStatus;
    private GanttPanel ganttPanel;
    private JLabel totalTimeValue;
    private DefaultTableModel resultsModel;
    private JLabel averageWaitingValue;
    private JLabel averageTurnaroundValue;

    public MainFrame() {
        setTitle("CloudSched - CPU Scheduling Simulator");
        setSize(1250, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);
        add(createSidebar(), BorderLayout.WEST);
        add(createMainArea(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 18, 20, 18));
        JLabel logo = new JLabel("CloudSched");
        logo.setFont(new Font("Arial", Font.BOLD, 25));
        logo.setForeground(ACCENT);
        JLabel subtitle = new JLabel("CPU Scheduling Simulator");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitle.setForeground(MUTED);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(45));
        sidebar.add(createMenuLabel("Dashboard"));
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createMenuLabel("Process Management"));
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createMenuLabel("CPU Visualization"));
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(createMenuLabel("Results"));
        return sidebar;
    }

    private JLabel createMenuLabel(String text) {
        JLabel label = new JLabel("   " + text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel createMainArea() {
        JPanel mainArea = new JPanel(new BorderLayout(0, 16));
        mainArea.setBackground(BACKGROUND);
        mainArea.setBorder(BorderFactory.createEmptyBorder(22, 25, 22, 25));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        JLabel title = new JLabel("CPU Scheduling Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT);
        JLabel status = new JLabel("Simulation Preview");
        status.setFont(new Font("Arial", Font.PLAIN, 13));
        status.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        JPanel dashboard = new JPanel(new BorderLayout(0, 15));
        dashboard.setBackground(BACKGROUND);
        JPanel topRow = new JPanel(new GridLayout(1, 2, 15, 0));
        topRow.setBackground(BACKGROUND);
        topRow.add(createConfigurationPanel());
        topRow.add(createProcessManagementPanel());
        topRow.setPreferredSize(new Dimension(800, 285));
        dashboard.add(topRow, BorderLayout.NORTH);
        dashboard.add(createCpuVisualizationPanel(), BorderLayout.CENTER);

        mainArea.add(header, BorderLayout.NORTH);
        mainArea.add(dashboard, BorderLayout.CENTER);
        return mainArea;
    }

    private JPanel createPanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(TEXT);
        panel.add(title, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createConfigurationPanel() {
        JPanel panel = createPanel("Scheduling Configuration");
        JPanel inputs = new JPanel(new GridLayout(0, 2, 12, 12));
        inputs.setBackground(PANEL);
        inputs.add(createConfigLabel("Scheduling Algorithm"));
        algorithmBox = createStyledComboBox(new String[]{"Preemptive SJF", "ACS"});
        inputs.add(algorithmBox);
        inputs.add(createConfigLabel("Time Quantum (ACS)"));
        quantumField = createStyledTextField("3");
        inputs.add(quantumField);
        inputs.add(createConfigLabel("Context Switching"));
        contextField = createStyledTextField("1");
        inputs.add(contextField);
        panel.add(inputs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 0));
        bottom.setBackground(PANEL);
        runButton = createStyledButton("Run Simulation");
        runButton.addActionListener(e -> runSimulator());
        simulationStatus = new JLabel("Ready");
        simulationStatus.setForeground(MUTED);
        simulationStatus.setFont(new Font("Arial", Font.PLAIN, 12));
        bottom.add(runButton, BorderLayout.WEST);
        bottom.add(simulationStatus, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createProcessManagementPanel() {
        JPanel panel = createPanel("Process Management");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setBackground(PANEL);

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 5));
        form.setBackground(PANEL);
        form.add(createConfigLabel("Process ID"));
        form.add(createConfigLabel("Arrival"));
        form.add(createConfigLabel("Burst"));
        form.add(createConfigLabel("Priority"));
        idField = createStyledTextField("");
        arrivalField = createStyledTextField("");
        burstField = createStyledTextField("");
        priorityField = createStyledTextField("");
        form.add(idField);
        form.add(arrivalField);
        form.add(burstField);
        form.add(priorityField);

        String[] columns = {"Process ID", "Arrival", "Burst", "Priority"};
        processModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        processTable = new JTable(processModel);
        styleTable(processTable);
        JScrollPane scroll = new JScrollPane(processTable);
        scroll.getViewport().setBackground(new Color(15, 23, 42));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));

        JButton addButton = createStyledButton("+ Add Process");
        addButton.addActionListener(e -> addProcess());
        JButton removeButton = createStyledButton("Remove Selected");
        removeButton.addActionListener(e -> {
            int selected = processTable.getSelectedRow();
            if (selected >= 0) processModel.removeRow(selected);
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setBackground(PANEL);
        buttons.add(addButton);
        buttons.add(removeButton);

        content.add(form, BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(15, 23, 42));
        table.setForeground(TEXT);
        table.setGridColor(new Color(51, 65, 85));
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(37, 99, 235));
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void addProcess() {
        try {
            String processId = idField.getText().trim();
            int numericId = getNumericProcessId(processId);
            int arrival = Integer.parseInt(arrivalField.getText().trim());
            int burst = Integer.parseInt(burstField.getText().trim());
            int priority = Integer.parseInt(priorityField.getText().trim());
            if (arrival < 0) throw new IllegalArgumentException("Arrival time cannot be negative.");
            if (burst <= 0) throw new IllegalArgumentException("Burst time must be greater than zero.");
            for (int row = 0; row < processModel.getRowCount(); row++) {
                int existingId = getNumericProcessId(processModel.getValueAt(row, 0).toString());
                if (existingId == numericId) throw new IllegalArgumentException("This process ID already exists.");
            }
            processModel.addRow(new Object[]{processId, arrival, burst, priority});
            idField.setText(""); arrivalField.setText(""); burstField.setText(""); priorityField.setText("");
            simulationStatus.setText("Process added");
        } catch (NumberFormatException ex) {
            showError("Enter a valid integer for arrival, burst, and priority.", "Invalid Input");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage(), "Invalid Process");
        }
    }

    private JPanel createCpuVisualizationPanel() {
        JPanel panel = createPanel("CPU Visualization  Gantt Chart");
        ganttPanel = new GanttPanel();

        JScrollPane chartScroll = new JScrollPane(ganttPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chartScroll.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        chartScroll.getViewport().setBackground(new Color(15, 23, 42));

        // Results section: per-process metrics plus averages.
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 7));
        resultsPanel.setBackground(PANEL);
        JLabel resultsTitle = new JLabel("Scheduling Results");
        resultsTitle.setFont(new Font("Arial", Font.BOLD, 15));
        resultsTitle.setForeground(TEXT);
        resultsPanel.add(resultsTitle, BorderLayout.NORTH);

        String[] resultColumns = {"Process", "Arrival", "Burst", "Completion", "Waiting Time", "Turnaround Time", "Response Time"};
        resultsModel = new DefaultTableModel(resultColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable resultsTable = new JTable(resultsModel);
        styleTable(resultsTable);
        JScrollPane resultsScroll = new JScrollPane(resultsTable);
        resultsScroll.setPreferredSize(new Dimension(700, 125));
        resultsScroll.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        resultsScroll.getViewport().setBackground(new Color(15, 23, 42));
        resultsPanel.add(resultsScroll, BorderLayout.CENTER);

        JPanel averagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 2));
        averagesPanel.setBackground(PANEL);

        averageWaitingValue = new JLabel("Average Waiting Time: ");
        averageTurnaroundValue = new JLabel("Average Turnaround Time: ");

        averageWaitingValue.setForeground(ACCENT);
        averageTurnaroundValue.setForeground(ACCENT);
        averageWaitingValue.setFont(new Font("Arial", Font.BOLD, 12));
        averageTurnaroundValue.setFont(new Font("Arial", Font.BOLD, 12));
        averagesPanel.add(averageWaitingValue);
        averagesPanel.add(averageTurnaroundValue);
        resultsPanel.add(averagesPanel, BorderLayout.SOUTH);

        JPanel visualizationAndResults = new JPanel();
        visualizationAndResults.setBackground(PANEL);
        visualizationAndResults.setLayout(new BoxLayout(visualizationAndResults, BoxLayout.Y_AXIS));
        chartScroll.setPreferredSize(new Dimension(700, 165));
        chartScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        visualizationAndResults.add(chartScroll);
        visualizationAndResults.add(Box.createVerticalStrut(14));
        visualizationAndResults.add(resultsPanel);
        panel.add(visualizationAndResults, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        footer.setBackground(PANEL);
        footer.add(legendItem("Process", new Color(37, 99, 235)));
        footer.add(legendItem("Idle", new Color(71, 85, 105)));
        footer.add(legendItem("Context Switch", new Color(217, 119, 6)));
        totalTimeValue = new JLabel("Total time: —");
        totalTimeValue.setForeground(ACCENT);
        footer.add(totalTimeValue);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel legendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(PANEL);
        JLabel swatch = new JLabel("  ");
        swatch.setOpaque(true);
        swatch.setBackground(color);
        item.add(swatch);
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        item.add(label);
        return item;
    }

    private void runSimulator() {
        try {
            if (processModel.getRowCount() == 0)
                throw new IllegalArgumentException("Please add at least one process first.");

            String selectedAlgorithm = (String) algorithmBox.getSelectedItem();
            int contextSwitch = Integer.parseInt(contextField.getText().trim());
            if (contextSwitch < 0) throw new IllegalArgumentException("Context switching cannot be negative.");
            int quantum = 3;
            if ("ACS".equals(selectedAlgorithm)) {
                quantum = Integer.parseInt(quantumField.getText().trim());
                if (quantum <= 0) throw new IllegalArgumentException("Time quantum must be greater than zero.");
            }

            List<PreemptiveSJF.range> timeline;
            List<Object[]> resultRows = new ArrayList<>();
            double totalWaiting = 0;
            double totalTurnaround = 0;

            if ("Preemptive SJF".equals(selectedAlgorithm)) {
                List<PreemptiveSJF.Process> processes = new ArrayList<>();
                for (int row = 0; row < processModel.getRowCount(); row++) {
                    int id = getNumericProcessId(processModel.getValueAt(row, 0).toString());
                    int arrival = Integer.parseInt(processModel.getValueAt(row, 1).toString());
                    int burst = Integer.parseInt(processModel.getValueAt(row, 2).toString());
                    processes.add(new PreemptiveSJF.Process(id, arrival, burst));
                }
                timeline = PreemptiveSJF.runFromGUI(processes, contextSwitch);

                for (PreemptiveSJF.Process p : processes) {
                    int firstStart = Integer.MAX_VALUE;
                    for (PreemptiveSJF.range segment : timeline) {
                        if (segment.id == p.id) {
                            firstStart = Math.min(firstStart, segment.start);
                        }
                    }
                    int response = firstStart == Integer.MAX_VALUE ? 0 : firstStart - p.arrival;
                    resultRows.add(new Object[]{"P" + p.id, p.arrival, p.burst, p.compeltion,
                            p.waiting, p.turnaround, response});
                    totalWaiting += p.waiting;
                    totalTurnaround += p.turnaround;
                }
            } else {
                ArrayList<ACSProcess> processes = new ArrayList<>();
                for (int row = 0; row < processModel.getRowCount(); row++) {
                    int id = getNumericProcessId(processModel.getValueAt(row, 0).toString());
                    int arrival = Integer.parseInt(processModel.getValueAt(row, 1).toString());
                    int burst = Integer.parseInt(processModel.getValueAt(row, 2).toString());
                    int priority = Integer.parseInt(processModel.getValueAt(row, 3).toString());
                    processes.add(new ACSProcess(id, arrival, burst, priority));
                }
                timeline = ACS_Main.runFromGUI(processes, quantum, contextSwitch);

                for (ACSProcess p : processes) {
                    resultRows.add(new Object[]{"P" + p.id, p.arrival, p.burst, p.completion,
                            p.waiting, p.turnaround, p.response});
                    totalWaiting += p.waiting;
                    totalTurnaround += p.turnaround;
                }
            }

            // Populate the results table using metrics calculated by the selected scheduler.
            resultsModel.setRowCount(0);
            for (Object[] result : resultRows) resultsModel.addRow(result);
            averageWaitingValue.setText(String.format(Locale.US,"Average Waiting Time: %.2f", totalWaiting / resultRows.size()));
            averageTurnaroundValue.setText(String.format(Locale.US,"Average Turnaround Time: %.2f", totalTurnaround / resultRows.size()));

            ganttPanel.setTimeline(timeline);
            int end = timeline.isEmpty() ? 0 : timeline.get(timeline.size() - 1).end;
            totalTimeValue.setText("Total time: " + end);
            simulationStatus.setText("Completed: " + selectedAlgorithm);
        } catch (NumberFormatException ex) {
            showError("Please enter valid integer values.", "Invalid Input");
            simulationStatus.setText("Invalid input");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage(), "Invalid Input");
            simulationStatus.setText("Invalid input");
        } catch (Exception ex) {
            showError("Simulation failed: " + ex.getMessage(), "Simulation Error");
            simulationStatus.setText("Simulation failed");
        }
    }

    private int getNumericProcessId(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) throw new IllegalArgumentException("Process ID must contain a number, e.g. P1.");
        return Integer.parseInt(digits);
    }

    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private JLabel createConfigLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }

    private JTextField createStyledTextField(String value) {
        JTextField field = new JTextField(value);
        field.setOpaque(true);
        field.setBackground(new Color(15, 23, 42));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(new Font("Arial", Font.BOLD, 13));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setPreferredSize(new Dimension(90, 34));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return button;
    }

    private JComboBox<String> createStyledComboBox(String[] options) {
        JComboBox<String> box = new JComboBox<>(options);
        box.setBackground(new Color(15, 23, 42));
        box.setForeground(TEXT);
        box.setFont(new Font("Arial", Font.PLAIN, 13));
        return box;
    }

    private class GanttPanel extends JPanel {
        private List<PreemptiveSJF.range> segments = new ArrayList<>();
        private final int unitWidth = 58;
        private final int chartHeight = 115;

        GanttPanel() {
            setBackground(new Color(15, 23, 42));
            setPreferredSize(new Dimension(700, 155));
        }

        void setTimeline(List<PreemptiveSJF.range> timeline) {
            segments = new ArrayList<>(timeline);
            int end = segments.isEmpty() ? 0 : segments.get(segments.size() - 1).end;
            setPreferredSize(new Dimension(Math.max(700, 35 + end * unitWidth), 155));
            revalidate();
            repaint();
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (segments.isEmpty()) {
                g.setColor(MUTED);
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString("Add processes and click Run Simulation to generate the Gantt chart.", 20, 65);
                g.dispose();
                return;
            }
            int x0 = 20;
            int y = 32;
            int h = 48;
            g.setFont(new Font("Arial", Font.BOLD, 12));
            for (PreemptiveSJF.range s : segments) {
                int x = x0 + s.start * unitWidth;
                int width = Math.max(2, (s.end - s.start) * unitWidth);
                Color color = s.id == -1 ? new Color(71, 85, 105)
                        : s.id == -2 ? new Color(217, 119, 6) : new Color(37, 99, 235);
                g.setColor(color);
                g.fillRoundRect(x, y, width, h, 8, 8);
                g.setColor(new Color(148, 163, 184));
                g.drawRoundRect(x, y, width, h, 8, 8);
                String label = s.id == -1 ? "IDLE" : s.id == -2 ? "CS" : "P" + s.id;
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                if (width > fm.stringWidth(label) + 8)
                    g.drawString(label, x + (width - fm.stringWidth(label)) / 2, y + 29);
                g.setColor(MUTED);
                g.setFont(new Font("Arial", Font.PLAIN, 11));
                g.drawString(String.valueOf(s.start), x - 3, y + h + 20);
                g.setFont(new Font("Arial", Font.BOLD, 12));
            }
            int end = segments.get(segments.size() - 1).end;
            g.setColor(MUTED);
            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.drawString(String.valueOf(end), x0 + end * unitWidth - 3, y + h + 20);
            g.dispose();
        }
    }
}
