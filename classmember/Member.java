import java.util.HashMap;
import java.util.Map;

public class Member {
    // Data member
    private String kodeMember;
    private String nama;
    private int point;

    // List hadiah yang bisa ditukar
    private static final Map<String, Integer> HADIAH = new HashMap<>();

    static {
        HADIAH.put("boneka", 10000);
        HADIAH.put("Mainan", 7000);
        HADIAH.put("Mie instant", 5000);
        HADIAH.put("biskuit", 4000);
    }

    // Konstruktor
    public Member(String kodeMember, String nama) {
        this.kodeMember = kodeMember;
        this.nama = nama;
        this.point = 0;
    }

    // Getter dan Setter
    public String getKodeMember() {
        return kodeMember;
    }

    public void setKodeMember(String kodeMember) {
        this.kodeMember = kodeMember;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getPoint() {
        return point;
    }

    // Method untuk menambah point berdasarkan transaksi
    public void transaksi(int jumlahBelanja) {
        int pointTambah = jumlahBelanja / 10000;
        this.point += pointTambah;
    }

    // Method untuk menukar point dengan hadiah
    public String tukarHadiah(String namaHadiah) {
        if (HADIAH.containsKey(namaHadiah)) {
            int pointDiperlukan = HADIAH.get(namaHadiah);
            if (this.point >= pointDiperlukan) {
                this.point -= pointDiperlukan;
                return "Berhasil menukar point untuk hadiah: " + namaHadiah;
            } else {
                return "Point tidak cukup untuk menukar hadiah: " + namaHadiah;
            }
        } else {
            return "Hadiah tidak tersedia: " + namaHadiah;
        }
    }

    // Method untuk menampilkan info member
    public void tampilkanInfo() {
        System.out.println("Kode Member: " + kodeMember);
        System.out.println("Nama: " + nama);
        System.out.println("Point: " + point);
    }

    // Method utama untuk pengujian
    public static void main(String[] args) {
        // Membuat objek Member
        Member member1 = new Member("M001", "Andi");

        // Menampilkan info member sebelum transaksi
        member1.tampilkanInfo();

        // Melakukan transaksi
        member1.transaksi(23000); // Belanja 23000 rupiah
        System.out.println("Point setelah transaksi: " + member1.getPoint());

        // Menukar hadiah
        System.out.println(member1.tukarHadiah("boneka"));
        System.out.println("Point setelah menukar hadiah: " + member1.getPoint());

        // Menampilkan info member setelah transaksi dan penukaran hadiah
        member1.tampilkanInfo();
    }
}
