import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Student Management System - Swing GUI
 * Provides Add, Update, Delete, Search, and View functionality
 * backed by a MySQL database via StudentDAO.
 */
public class StudentManagementApp extends JFrame {

    private final StudentDAO dao = new StudentDAO();

    // Form fields
    private JTextField idField, rollField, nameField, courseField, yearField, emailField, phoneField;
    private JTextField searchField;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentManagementApp() {
        setTitle("Student Management System");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        loadTableData();
    }

    // ---------------- Form Panel ----------------
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(5);
        idField.setEditable(false);
        rollField = new JTextField(10);
        nameField = new JTextField(15);
        courseField = new JTextField(10);
        yearField = new JTextField(5);
        emailField = new JTextField(15);
        phoneField = new JTextField(10);

        int row = 0;
        addRow(panel, gbc, row++, "ID:", idField, "Roll No:", rollField);
        addRow(panel, gbc, row++, "Name:", nameField, "Course:", courseField);
        addRow(panel, gbc, row++, "Year:", yearField, "Email:", emailField);

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Search:"), gbc);
        searchField = new JTextField(15);
        gbc.gridx = 3;
        panel.add(searchField, gbc);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                         String label1, JComponent field1, String label2, JComponent field2) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label1), gbc);
        gbc.gridx = 1;
        panel.add(field1, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel(label2), gbc);
        gbc.gridx = 3;
        panel.add(field2, gbc);
    }

    // ---------------- Table Panel ----------------
    private JScrollPane buildTablePanel() {
        String[] columns = {"ID", "Roll No", "Name", "Course", "Year", "Email", "Phone"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
        return new JScrollPane(table);
    }

    // ---------------- Button Panel ----------------
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");

        addBtn.addActionListener(this::onAdd);
        updateBtn.addActionListener(this::onUpdate);
        deleteBtn.addActionListener(this::onDelete);
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(this::onSearch);
        refreshBtn.addActionListener(e -> { searchField.setText(""); loadTableData(); });

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(clearBtn);
        panel.add(searchBtn);
        panel.add(refreshBtn);
        return panel;
    }

    // ---------------- Actions ----------------
    private void onAdd(ActionEvent e) {
        try {
            Student s = readFormAsStudent(false);
            if (dao.addStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student added successfully.");
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onUpdate(ActionEvent e) {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Student s = readFormAsStudent(true);
            if (dao.updateStudent(s)) {
                JOptionPane.showMessageDialog(this, "Student updated successfully.");
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onDelete(ActionEvent e) {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idField.getText());
            if (dao.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "Student deleted.");
                clearForm();
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        List<Student> results = keyword.isEmpty() ? dao.getAllStudents() : dao.searchStudents(keyword);
        populateTable(results);
    }

    // ---------------- Helpers ----------------
    private Student readFormAsStudent(boolean withId) {
        String roll = rollField.getText().trim();
        String name = nameField.getText().trim();
        String course = courseField.getText().trim();
        String yearText = yearField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (roll.isEmpty() || name.isEmpty() || course.isEmpty() || yearText.isEmpty()) {
            throw new IllegalArgumentException("Roll No, Name, Course, and Year are required.");
        }
        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Year must be a number.");
        }

        Student s = new Student(roll, name, course, year, email, phone);
        if (withId) {
            s.setId(Integer.parseInt(idField.getText()));
        }
        return s;
    }

    private void populateFormFromSelectedRow() {
        int row = table.getSelectedRow();
        idField.setText(tableModel.getValueAt(row, 0).toString());
        rollField.setText(tableModel.getValueAt(row, 1).toString());
        nameField.setText(tableModel.getValueAt(row, 2).toString());
        courseField.setText(tableModel.getValueAt(row, 3).toString());
        yearField.setText(tableModel.getValueAt(row, 4).toString());
        emailField.setText(tableModel.getValueAt(row, 5).toString());
        phoneField.setText(tableModel.getValueAt(row, 6).toString());
    }

    private void clearForm() {
        idField.setText("");
        rollField.setText("");
        nameField.setText("");
        courseField.setText("");
        yearField.setText("");
        emailField.setText("");
        phoneField.setText("");
        table.clearSelection();
    }

    private void loadTableData() {
        populateTable(dao.getAllStudents());
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{
                    s.getId(), s.getRollNo(), s.getName(), s.getCourse(), s.getYear(), s.getEmail(), s.getPhone()
            });
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new StudentManagementApp().setVisible(true));
    }
}
