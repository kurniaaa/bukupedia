/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaksi;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
//NEW
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.koneksi;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
/**
 *
 * @author Roy
 */
public class formTransaksi extends javax.swing.JFrame {
    DefaultTableModel table = new DefaultTableModel();
    /**
     * Creates new form formTransaksi
     */
    public formTransaksi() {
        initComponents();
        koneksi.getKoneksi();
        totalnya();
        tanggal();
        
        tb_keranjang.setModel(table);
        table.addColumn("ID");
        table.addColumn("Nama Barang");
        table.addColumn("Harga");
        table.addColumn("Jumlah");
        table.addColumn("Total Harga");
        
        tampilData();
    }
     public void tanggal(){
        Date now = new Date();  
        tgl_transaksi.setDate(now);    
    }
     private void tampilData(){
        //untuk mengahapus baris setelah input
        int row = tb_keranjang.getRowCount();
        for(int a = 0 ; a < row ; a++){
            table.removeRow(0);
        }
        
        String query = "SELECT * FROM `tb_keranjang` ";
        String procedures = "CALL `total_harga_transaksi`()";
        
        try{
            Connection connect = koneksi.getKoneksi();//memanggil koneksi
            Statement sttmnt = connect.createStatement();//membuat statement
            ResultSet rslt = sttmnt.executeQuery(query);//menjalanakn query
            
            while (rslt.next()){
                //menampung data sementara
                   
                    String kode = rslt.getString("id_transaksi");
                    String nama = rslt.getString("nama_barang");
                    String harga = rslt.getString("harga");
                    String jumlah = rslt.getString("jumlah");
                    String total = rslt.getString("total_harga");
                    
                //masukan semua data kedalam array
                String[] data = {kode,nama,harga,jumlah,total};
                //menambahakan baris sesuai dengan data yang tersimpan diarray
                table.addRow(data);
            }
                //mengeset nilai yang ditampung agar muncul di table
                tb_keranjang.setModel(table);
            
        }catch(Exception e){
            System.out.println(e);
        }
       
    }
    private void clear(){
        txt_kodebarang2.setText(null);
        txt_namabarang2.setText(null);
        txt_harga2.setText(null);
        txt_jumlah2.setText(null);
        txt_totalharga.setText(null);
    }
    private void keranjang(){
        String kode = txt_kodebarang2.getText();
        String nama = txt_namabarang2.getText();
        String harga = txt_harga2.getText();
        String jumlah = txt_jumlah2.getText();
        String total = txt_totalharga.getText();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String tanggal = String.valueOf(date.format(tgl_transaksi.getDate()));
        
        //panggil koneksi
        Connection connect = koneksi.getKoneksi();
        //query untuk memasukan data
        String query = "INSERT INTO `transaksi` (`tgl_transaksi`, `id_transaksi`, `kode_barang`, `nama_barang`, `harga`, `jumlah_barang`, `total_harga`) "
                + "VALUES ('"+tanggal+"', NULL, '"+kode+"', '"+nama+"', '"+harga+"', '"+jumlah+"', '"+total+"')";
        
        try{
            //menyiapkan statement untuk di eksekusi
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(query);
            ps.executeUpdate(query);
            JOptionPane.showMessageDialog(null,"Data Masuk Ke-Keranjang");
            
        }catch(SQLException | HeadlessException e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null,"Data Gagal Disimpan");
            
        }finally{
            tampilData();
            clear();
            
        }
        totalnya();
    }
    private void hapusData(){
        //ambill data no pendaftaran
        int i = tb_keranjang.getSelectedRow();
        
        String kode = table.getValueAt(i, 0).toString();
        
        Connection connect = koneksi.getKoneksi();
        
        String query = "DELETE FROM `tb_keranjang` WHERE `tb_keranjang`.`id_transaksi` = '"+kode+"' ";
        try{
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(query);
            ps.execute();
        }catch(SQLException | HeadlessException e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Data Gagal Dihapus");
        }finally{
            tampilData();
            clear();
        }
        totalnya();
    }
    private void totalnya(){
        String procedures = "CALL `total_harga_transaksi`()";
        
        try{
            Connection connect = koneksi.getKoneksi();//memanggil koneksi
            Statement sttmnt = connect.createStatement();//membuat statement
            ResultSet rslt = sttmnt.executeQuery(procedures);//menjalanakn query\
                while(rslt.next()){
                    txt_totalharga2.setText(rslt.getString(1));
                }
                
        }catch(Exception e){
            System.out.println(e);
        }
        
        
    }
    private void total(){
        String harga = txt_harga2.getText();
        String jumlah = txt_jumlah2.getText();
        
        int hargaa = Integer.parseInt(harga);
        try{
        int jumlahh = Integer.parseInt(jumlah);
        
        int total = hargaa * jumlahh;
        String total_harga = Integer.toString(total);
        
        txt_totalharga.setText(total_harga);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Only Number");
            txt_jumlah2.setText(null);
        }
    }
    private void reset(){
        txt_uang.setText(null);
    }
    private void kembalian(){
        String total = txt_totalharga2.getText();
        String uang = txt_uang.getText();
        
        int totals = Integer.parseInt(total);
        try{
            int uangs = Integer.parseInt(uang);     
            int kembali = (uangs - totals);
            String fix = Integer.toString(kembali);
            txt_kembalian.setText(fix);
            JOptionPane.showMessageDialog(null, "Transaksi Berhasil!");
        }catch(NumberFormatException | HeadlessException e){
            JOptionPane.showMessageDialog(null, "Invalid Payment");
        }
    }
//    private void tambahData(){
//        String kode = txt_kodebarang2.getText();
//        String nama = txt_namabarang2.getText();
//        String harga = txt_harga2.getText();
//        String jumlah = txt_jumlah2.getText();
//        String total = txt_totalharga.getText();
//        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
//        String tanggal = String.valueOf(date.format(tgl_transaksi.getDate()));
//        
//        //panggil koneksi
//        Connection connect = koneksi.getKoneksi();
//        //query untuk memasukan data
//        String query = "INSERT INTO `transaksi` (`tgl_transaksi`, `id_transaksi`, `kode_barang`, `nama_barang`, `harga`, `jumlah_barang`, `total_harga`) "
//                     + "VALUES ( '"+tanggal+"', NULL, '"+kode+"', '"+nama+"', '"+harga+"', '"+jumlah+"', '"+total+"')";
//        
//        try{
//            //menyiapkan statement untuk di eksekusi
//            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(query);
//            ps.executeUpdate(query);
//            
//            
//        }catch(SQLException | HeadlessException e){
//            System.out.println(e);
//            
//            
//        }finally{
//            
//            
//            
//        }
//    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        txt_jumlah2 = new javax.swing.JTextField();
        txt_kodebarang2 = new javax.swing.JTextField();
        txt_namabarang2 = new javax.swing.JTextField();
        txt_totalharga = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        txt_harga2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        txt_uang = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_kembalian = new javax.swing.JTextField();
        tgl_transaksi = new com.toedter.calendar.JDateChooser();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_keranjang = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        txt_totalharga2 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();

        jMenuBar2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(983, 635));
        getContentPane().setLayout(null);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txt_jumlah2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_jumlah2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_jumlah2ActionPerformed(evt);
            }
        });
        txt_jumlah2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_jumlah2KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_jumlah2KeyTyped(evt);
            }
        });

        txt_kodebarang2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_kodebarang2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_kodebarang2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_kodebarang2ActionPerformed(evt);
            }
        });

        txt_namabarang2.setEditable(false);
        txt_namabarang2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_namabarang2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_namabarang2ActionPerformed(evt);
            }
        });

        txt_totalharga.setEditable(false);
        txt_totalharga.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_totalharga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_totalhargaMouseReleased(evt);
            }
        });
        txt_totalharga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalhargaActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton1.setText("PEMBAYARAN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton3.setText("CARI");
        jButton3.setToolTipText("");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        txt_harga2.setEditable(false);
        txt_harga2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_harga2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_harga2ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(51, 51, 255));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        txt_uang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_uang.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_uang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_uangActionPerformed(evt);
            }
        });
        txt_uang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_uangKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_uangKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_uangKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("Buku");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setText("Harga");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Jumlah");

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel11.setText("Total Harga");

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("KEMBALIAN");

        txt_kembalian.setEditable(false);
        txt_kembalian.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        txt_kembalian.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_kembalian.setEnabled(false);
        txt_kembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_kembalianActionPerformed(evt);
            }
        });

        tgl_transaksi.setDateFormatString("dd-MM-yyyy");
        tgl_transaksi.setEnabled(false);
        tgl_transaksi.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton2.setText("CETAK");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        tb_keranjang.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        tb_keranjang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tb_keranjang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tb_keranjangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tb_keranjang);

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton5.setText("TAMBAH");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        txt_totalharga2.setEditable(false);
        txt_totalharga2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        txt_totalharga2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_totalharga2.setEnabled(false);
        txt_totalharga2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_totalharga2MouseReleased(evt);
            }
        });
        txt_totalharga2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalharga2ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton6.setText("HAPUS");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 255, 255));
        jButton7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton7.setText("ULANG");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TRANSAKSI");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(207, 207, 207))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_kodebarang2, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton2)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_namabarang2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel11)
                                .addComponent(txt_totalharga, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_harga2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(txt_jumlah2, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(72, 72, 72)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tgl_transaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton7)))
                                .addComponent(txt_totalharga2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txt_uang, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_kembalian)))))
                .addGap(395, 395, 395))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 858, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tgl_transaksi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_kodebarang2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(31, 31, 31)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txt_namabarang2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_harga2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(txt_totalharga2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_uang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_jumlah2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_totalharga, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(txt_kembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 1262, 634);

        jMenuBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_harga2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_harga2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_harga2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:
//        new stok_barang().setVisible(true);
//        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        kembalian();
//        tambahData();
//        JOptionPane.showMessageDialog(null, "Transaksi Berhasil !");
//        new struk.struk().setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txt_totalhargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalhargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalhargaActionPerformed

    private void txt_namabarang2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_namabarang2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_namabarang2ActionPerformed

    private void txt_kodebarang2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_kodebarang2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kodebarang2ActionPerformed

    private void txt_jumlah2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_jumlah2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_jumlah2ActionPerformed

    private void txt_totalhargaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_totalhargaMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalhargaMouseReleased

    private void txt_jumlah2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah2KeyReleased
        // TODO add your handling code here:
        total();
    }//GEN-LAST:event_txt_jumlah2KeyReleased

    private void txt_uangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_uangKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_uangKeyReleased

    private void txt_kembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_kembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_kembalianActionPerformed

    private void txt_uangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_uangKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_uangKeyTyped

    private void txt_jumlah2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlah2KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_jumlah2KeyTyped

    private void txt_uangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_uangKeyPressed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_txt_uangKeyPressed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        keranjang();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txt_uangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_uangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_uangActionPerformed

    private void txt_totalharga2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_totalharga2MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalharga2MouseReleased

    private void txt_totalharga2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalharga2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalharga2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        hapusData();
        txt_uang.setText(null);
        txt_kembalian.setText(null);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void tb_keranjangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tb_keranjangMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tb_keranjangMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        try{
            String file = "/struk/struk.jasper";
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            HashMap param = new HashMap();
            
            param.put("total",txt_totalharga2.getText());
            param.put("uang",txt_uang.getText());
            param.put("kembalian",txt_kembalian.getText());
            
            JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream(file),param,koneksi.getKoneksi());
            JasperViewer.viewReport(print, false);
            
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | JRException e){
            System.out.println(e);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
       
        try{
            String clear = "TRUNCATE `tb_keranjang`";
            Connection connect = koneksi.getKoneksi();
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(clear);
            ps.execute();
//            keranjang();
            
            
        }catch(Exception e){
            System.out.println(e);
        }finally{
            tampilData();
            totalnya();
            txt_uang.setText(null);
            txt_kembalian.setText(null);
        }
        
    }//GEN-LAST:event_jButton7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(formTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(formTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(formTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(formTransaksi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new formTransaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tb_keranjang;
    private com.toedter.calendar.JDateChooser tgl_transaksi;
    public javax.swing.JTextField txt_harga2;
    public javax.swing.JTextField txt_jumlah2;
    public static javax.swing.JTextField txt_kembalian;
    public javax.swing.JTextField txt_kodebarang2;
    public javax.swing.JTextField txt_namabarang2;
    public javax.swing.JTextField txt_totalharga;
    public static javax.swing.JTextField txt_totalharga2;
    public static javax.swing.JTextField txt_uang;
    // End of variables declaration//GEN-END:variables

    static class dispose {

        public dispose() {
        }
    }

}