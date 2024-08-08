import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class Mahasiswa {
    String nim;
    String nama;
    int nilai;

    public Mahasiswa(String nim, String nama, int nilai) {
        this.nim = nim;
        this.nama = nama;
        this.nilai = nilai;
    }
}

public class MahasiswaGUI extends JFrame {
    private JTextField nimField, namaField, nilaiField;
    private JTextArea resultArea;
    private JButton submitButton;
    private Mahasiswa[] mahasiswaArray;
    private int currentIndex = 0;
    private Connection connection;

    public MahasiswaGUI() {
        setTitle("Input Mahasiswa");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nimLabel = new JLabel("NIM:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nimLabel, gbc);

        nimField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(nimField, gbc);

        JLabel namaLabel = new JLabel("Nama:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(namaLabel, gbc);

        namaField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(namaField, gbc);

        JLabel nilaiLabel = new JLabel("Nilai:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(nilaiLabel, gbc);

        nilaiField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(nilaiField, gbc);

        submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(submitButton, gbc);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        // Menginisialisasi array mahasiswa
        int jumlahData = Integer.parseInt(JOptionPane.showInputDialog(this, "Masukkan jumlah data:"));
        mahasiswaArray = new Mahasiswa[jumlahData];

        // Koneksi ke database
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mahasiswa", // Ganti dengan URL database Anda
                "root", // Ganti dengan username database Anda
                "fajar123"  // Ganti dengan password database Anda
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal!");
        }
    }

    private void handleSubmit() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        int nilai = Integer.parseInt(nilaiField.getText());

        if (nama.length() < 3 || nama.length() > 20) {
            JOptionPane.showMessageDialog(this, "Nama harus antara 3 - 20 karakter.");
            return;
        }

        if (nilai < 0 || nilai > 100) {
            JOptionPane.showMessageDialog(this, "Nilai harus di antara 0 - 100.");
            return;
        }

        mahasiswaArray[currentIndex] = new Mahasiswa(nim, nama, nilai);
        currentIndex++;

        // Simpan ke database
        saveToDatabase(nim, nama, nilai);

        if (currentIndex >= mahasiswaArray.length) {
            showHighestScore();
        }
    }

    private void saveToDatabase(String nim, String nama, int nilai) {
        String query = "INSERT INTO mahasiswa (nim, nama, nilai) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nim);
            stmt.setString(2, nama);
            stmt.setInt(3, nilai);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data ke database!");
        }
    }

    private void showHighestScore() {
        Mahasiswa mahasiswaTertinggi = mahasiswaArray[0];
        for (int i = 1; i < mahasiswaArray.length; i++) {
            if (mahasiswaArray[i].nilai > mahasiswaTertinggi.nilai) {
                mahasiswaTertinggi = mahasiswaArray[i];
            }
        }

        resultArea.setText("Mahasiswa dengan nilai tertinggi:\n");
        resultArea.append("NIM: " + mahasiswaTertinggi.nim + "\n");
        resultArea.append("Nama: " + mahasiswaTertinggi.nama + "\n");
        resultArea.append("Nilai: " + mahasiswaTertinggi.nilai + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MahasiswaGUI().setVisible(true);
            }
        });
    }
}
