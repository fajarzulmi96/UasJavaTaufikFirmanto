import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Kelas untuk menyimpan informasi mahasiswa
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

// Kelas utama untuk GUI aplikasi mahasiswa
public class MahasiswaGUI extends JFrame {
    private JTextField nimField, namaField, nilaiField; // Field untuk input data mahasiswa
    private JTextArea resultArea; // Area untuk menampilkan hasil
    private JButton submitButton; // Tombol untuk submit data
    private Mahasiswa[] mahasiswaArray; // Array untuk menyimpan objek mahasiswa
    private int currentIndex = 0; // Indeks untuk array mahasiswa
    private Connection connection; // Koneksi database

    // Konstruktor untuk mengatur GUI
    public MahasiswaGUI() {
        setTitle("Input Mahasiswa"); // Judul jendela
        setSize(400, 300); // Ukuran jendela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Menutup aplikasi saat jendela ditutup
        setLayout(new GridBagLayout()); // Mengatur layout menggunakan GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints(); // Konfigurasi untuk GridBagLayout
        gbc.insets = new Insets(5, 5, 5, 5); // Jarak antar komponen
        gbc.fill = GridBagConstraints.HORIZONTAL; // Mengatur komponen untuk mengisi horizontal

        // Label dan field untuk NIM
        JLabel nimLabel = new JLabel("NIM:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nimLabel, gbc);

        nimField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(nimField, gbc);

        // Label dan field untuk Nama
        JLabel namaLabel = new JLabel("Nama:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(namaLabel, gbc);

        namaField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(namaField, gbc);

        // Label dan field untuk Nilai
        JLabel nilaiLabel = new JLabel("Nilai:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(nilaiLabel, gbc);

        nilaiField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(nilaiField, gbc);

        // Tombol Submit
        submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(submitButton, gbc);

        // Area untuk menampilkan hasil
        resultArea = new JTextArea();
        resultArea.setEditable(false); // Tidak dapat diedit
        JScrollPane scrollPane = new JScrollPane(resultArea); // Scroll pane untuk textarea
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; // Mengisi area secara vertikal dan horizontal
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        // Menambahkan ActionListener untuk tombol submit
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

    // Metode untuk menghubungkan ke database
    private void connectToDatabase() {
        try {
            // Memuat driver JDBC untuk MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Menghubungkan ke database
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mahasiswa", // URL database
                "root", // Username database
                "fajar123"  // Password database
            );
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi ke database gagal!");
        }
    }

    // Metode untuk menangani submit data mahasiswa
    private void handleSubmit() {
        String nim = nimField.getText();
        String nama = namaField.getText();
        int nilai = Integer.parseInt(nilaiField.getText());

        // Validasi nama
        if (nama.length() < 3 || nama.length() > 20) {
            JOptionPane.showMessageDialog(this, "Nama harus antara 3 - 20 karakter.");
            return;
        }

        // Validasi nilai
        if (nilai < 0 || nilai > 100) {
            JOptionPane.showMessageDialog(this, "Nilai harus di antara 0 - 100.");
            return;
        }

        // Menyimpan data mahasiswa ke array
        mahasiswaArray[currentIndex] = new Mahasiswa(nim, nama, nilai);
        currentIndex++;

        // Menyimpan data ke database
        saveToDatabase(nim, nama, nilai);

        // Jika semua data sudah diinputkan, tampilkan nilai tertinggi
        if (currentIndex >= mahasiswaArray.length) {
            showHighestScore();
        }
    }

    // Metode untuk menyimpan data mahasiswa ke database
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

    // Metode untuk menampilkan mahasiswa dengan nilai tertinggi
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

    // Metode utama untuk menjalankan aplikasi
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MahasiswaGUI().setVisible(true);
            }
        });
    }
}
