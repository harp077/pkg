package PKG.addons;

//import PKG.clipboard.ShowClipboard;
import PKG.DaoJDBC;
//import PKG.DbManager;
//import PKG.PKGgui;
//import static PKG.PKGgui.dbManager;
import static PKG.PKGgui.dd;
import static PKG.PKGgui.Zagolovok;
import static PKG.PKGgui.sdf;
import static PKG.PKGgui.tableModel;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.romanenco.configloader.ConfigLoader;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.inject.Inject;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
//import net.sf.tinylaf.Theme;
import de.muntjak.tinylookandfeel.Theme;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Lazy(false)
public class Actions {

    @Inject
    private DaoJDBC ejbDaoJDBC;

    public static String DbLastChange = "";
    public static String addDbLastChange = "";
    public static String currentTheme;
    public static String currentLAF;
    public static String login = "";
    public static String passw = "";
    public static String addlogin = "";
    public static String addpassw = "";
    public static SecretKey skey;
    public static String hash;
    public static String addhash;
    public static String[] typeHashArray = {"md2", "md5", "sha1", "sha256", "sha384", "sha512"};
    public static List<String> lookAndFeelsDisplay = new ArrayList<>();
    public static List<String> lookAndFeelsRealNames = new ArrayList<>();
    public static List<String> tinyTemes = new ArrayList<>();

    @PostConstruct
    public void afterBirn() {
        InstallLF();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    public void setLF(JFrame frame) {
        if (currentLAF.contains("tinylaf")) {
            Theme.loadTheme(new File(currentTheme));
        }
        try {
            UIManager.setLookAndFeel(currentLAF);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(frame);
        //frame.pack();
    }

    public void changeLF(JFrame frame) {
        //Actions.currentTheme="";
        //String changeLook = (String) JOptionPane.showInputDialog(frame, "Choose Look and Feel Here:", "Select Look and Feel", JOptionPane.QUESTION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/PKG/img/color_swatch.png")), lookAndFeelsDisplay.toArray(), null);
        String changeLook = "net.sf.tinylaf.TinyLookAndFeel";
        ImageIcon iconLF = new ImageIcon(getClass().getResource("/PKG/img/color_swatch.png"));
        if (changeLook.contains("tinylaf")) {
            currentTheme = (String) JOptionPane.showInputDialog(frame, "Set TinyLF Theme:", "Select TinyLF Theme", JOptionPane.QUESTION_MESSAGE, iconLF, tinyTemes.toArray(), null);
        }
        if (changeLook != null) {
            for (int a = 0; a < lookAndFeelsDisplay.size(); a++) {
                if (changeLook.equals(lookAndFeelsDisplay.get(a))) {
                    currentLAF = lookAndFeelsRealNames.get(a);
                    setLF(frame);
                    break;
                }
            }
        }
    }

    public void ShowClipboard(JFrame frame) {
        TextTransfer textTransfer = new TextTransfer();
        String buf = textTransfer.getClipboardContents();
        JTextArea taClip = new JTextArea(15,45);
        taClip.setText(buf);
        //taClip.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        JScrollPane jsc=new JScrollPane(taClip);
        jsc.setViewportView(taClip);        
        Object[] ob = {jsc};
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/clipboard_search.png"));
        JOptionPane.showMessageDialog(frame, ob, "ClipBoard", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    public void MyInstLF(String lf) {
        //UIManager.installLookAndFeel(lf,lf); 
        lookAndFeelsDisplay.add(lf);
        lookAndFeelsRealNames.add(lf);
    }

    public void InstallLF() {
        tinyTemes.add("lib/themes/Default.theme");
        tinyTemes.add("lib/themes/Forest.theme");
        tinyTemes.add("lib/themes/Golden.theme");
        tinyTemes.add("lib/themes/Plastic.theme");
        tinyTemes.add("lib/themes/Silver.theme");
        tinyTemes.add("lib/themes/Nightly.theme");
        tinyTemes.add("lib/themes/My_Cyan.theme");
        tinyTemes.add("lib/themes/My_Yellow.theme");
        tinyTemes.add("lib/themes/My_AquaMarine.theme");
        tinyTemes.add("lib/themes/My_Magenta.theme");
        tinyTemes.add("lib/themes/My_Green.theme");
        MyInstLF("net.sf.tinylaf.TinyLookAndFeel");
        MyInstLF("javax.swing.plaf.metal.MetalLookAndFeel");
    }

    /*public static void cCopyToClipBoard(String cps) {
        new TextTransfer().setClipboardContents(cps);
    }*/

    public void DeleteRecord(JTable KeeperTable, JFrame frame) {
        try {
            KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1);
        } catch (IndexOutOfBoundsException ai) {
            JOptionPane.showMessageDialog(frame, "Select row please !", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(frame, "Delete Record with DB-id = " + (String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1) + " ?", "Delete Record", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            ejbDaoJDBC.deleteByID((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1));
            //dbManager.executeUpdate("DELETE FROM hosts WHERE id = " + (String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1));
            RefreshJTable(KeeperTable);
            dbLastChange(frame);
        }
    }

    public static SecretKey getSecretKey() {
        try {
            // DESERIALIZATION            
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users/" + login + "/" + login + ".des"));
            SecretKey skey = (SecretKey) ois.readObject();
            ois.close();
            return skey;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void setSecretKey(String user) {
        SecretKey skey = null;
        try {
            skey = KeyGenerator.getInstance("DES").generateKey();
            // SERIALIZATION
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users/" + user + "/" + user + ".des"));
            oos.writeObject(skey);
            oos.flush();
            oos.close();
        } catch (NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CopyPasswToClipboard(JTable KeeperTable, JFrame frame) {
        try {
            KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 4);
        } catch (IndexOutOfBoundsException ai) {
            JOptionPane.showMessageDialog(frame, "Select row please !", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String pass = (String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 4);
        SecretKey skey = getSecretKey();
        try {
            DesEncrypter encrypter = new DesEncrypter(skey);
            String res = encrypter.decrypt(pass);
            StringSelection ss = new StringSelection(res);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void newTableModel(JTable KeeperTable) {
        int wid = 3;
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Num");
        tableModel.addColumn("DB-id");
        tableModel.addColumn("Title");
        tableModel.addColumn("Login");
        tableModel.addColumn("Encrypted Password");
        tableModel.addColumn("Description");
        tableModel.addColumn("URL");
        KeeperTable.setModel(tableModel);
        KeeperTable.getColumn("Num").setPreferredWidth(wid);
        KeeperTable.getColumn("DB-id").setPreferredWidth(wid);
    }

    public void RefreshJTable(JTable KeeperTable) {
        newTableModel(KeeperTable);
        int N = 1;
        SqlRowSet rs = null;
        rs = ejbDaoJDBC.getAllasSRS();
        //rs = dbManager.executeQuery("SELECT * FROM hosts");
        while (rs.next()) {
            String[] rowdata = {"" + N, "" + Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)};
            tableModel.addRow(rowdata);
            N++;
        }
        //rs.close();
    }

    public static Boolean userCheck() {
        JLabel jUserName = new JLabel("UserName for DataBase owner: ");
        JComboBox userName = new JComboBox();
        userName.setModel(new javax.swing.DefaultComboBoxModel(new File("users").list()));
        JLabel jPassword = new JLabel("Password for DataBase and DataBase-owner: ");
        JTextField password = new JPasswordField();
        Object[] ob = {jUserName, userName, jPassword, password};
        int result = JOptionPane.showConfirmDialog(null, ob, "Default: login=user, password=user", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (!userName.getSelectedItem().toString().equals("") && !password.getText().equals("")) {
                addlogin = userName.getSelectedItem().toString();
                String passwBufer = "";
                UserCfgXerces.Load(); // load addpassw + addhash + addDbLastChange !!!
                switch (addhash) {
                    case "md2":
                        passwBufer = DigestUtils.md2Hex(password.getText());    break;
                    case "md5":
                        passwBufer = DigestUtils.md5Hex(password.getText());    break;
                    case "sha1":
                        passwBufer = DigestUtils.sha1Hex(password.getText());   break;
                    case "sha256":
                        passwBufer = DigestUtils.sha256Hex(password.getText()); break;
                    case "sha384":
                        passwBufer = DigestUtils.sha384Hex(password.getText()); break;
                    case "sha512":
                        passwBufer = DigestUtils.sha512Hex(password.getText()); break;
                }
                if (!addpassw.equals(passwBufer)) {
                    JOptionPane.showMessageDialog(null, "Wrong Login or Password !", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    login = addlogin;
                    passw = addpassw;
                    hash = addhash;
                    //System.out.println(hash);
                    DbLastChange = addDbLastChange;
                    /*try {
                        if (dbManager != null) {
                            dbManager.con.commit();
                            dbManager.con.close();
                            dbManager = null;
                        }
                    } catch (SQLException | NullPointerException ex) {    }
                    DbManager.dbLogin = login;
                    DbManager.dbPassw = passw;
                    DbManager.dbStartCmd = ";create=true;dataEncryption=true;bootPassword=hohners1974" + passw;
                    DbManager.dbPath = "db/" + login;
                    dbManager = new DbManager(DbManager.dbPath);*/
                    skey = getSecretKey(); 
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Login or Password empty !", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } 
        else return false;
    }

    public void rowEdit(JTable KeeperTable, JFrame frame) {
        JLabel lbTitle = new JLabel("Title: ");
        JTextField tfTitle = new JTextField();
        JLabel lbLogin = new JLabel("Login: ");
        JTextField tfLogin = new JTextField();
        JLabel lbPassw = new JLabel("Password: ");
        JTextField tfPassw = new JTextField();
        JLabel lbDescr = new JLabel("Description: ");
        JTextField tfDescr = new JTextField();
        JLabel lbUrl = new JLabel("URL: ");
        JTextField tfUrl = new JTextField();
        try {
            tfTitle.setText((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 2));
            tfLogin.setText((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 3));
            String PassBuf = "";
            try {
                //SecretKey skey=Actions.GetSecretKey();
                DesEncrypter encrypter = new DesEncrypter(skey);
                PassBuf = encrypter.decrypt((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 4));
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            }
            tfPassw.setText(PassBuf);
            tfDescr.setText((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 5));
            tfUrl.setText((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 6));            
        } catch (IndexOutOfBoundsException ai) {
            JOptionPane.showMessageDialog(frame, "Select row please !", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        Object[] ob = {lbTitle, tfTitle, lbLogin, tfLogin, lbPassw, tfPassw, lbDescr, tfDescr, lbUrl, tfUrl};
        int result = JOptionPane.showConfirmDialog(frame, ob, "View/Edit Row", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String PassBufer = "";
            try {
                //SecretKey skey=Actions.GetSecretKey();
                DesEncrypter encrypter = new DesEncrypter(skey);
                PassBufer = encrypter.encrypt(tfPassw.getText());
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            }
            ///////////////////
            UrlValidator url_valid = UrlValidator.getInstance();
            if (tfPassw.getText().isEmpty() || tfLogin.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Login/Password Fields don't be empty !", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!url_valid.isValid(tfUrl.getText()) && !tfUrl.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "URL is not valid !", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                ejbDaoJDBC.editRowByID(tfTitle.getText(), tfLogin.getText(), PassBufer, tfDescr.getText(), tfUrl.getText(), Integer.parseInt((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1)));
                //dbManager.executeUpdate("UPDATE hosts SET title='" + TitleTF.getText() + "',login='" + LoginTF.getText() + "',passw='" + PassBufer + "',descr='" + CommentTF.getText() + "',url='" + UrlTF.getText() + "'WHERE id = " + Integer.parseInt((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 1)));
                RefreshJTable(KeeperTable);
                dbLastChange(frame);
                //doClose(RET_OK);
            }
        }
    }

    public void rowAdd(JTable KeeperTable, JFrame frame) {
        JLabel lbTitle = new JLabel("Title: ");
        JTextField tfTitle = new JTextField();
        JLabel lbLogin = new JLabel("Login: ");
        JTextField tfLogin = new JTextField();
        JLabel lbPassw = new JLabel("Password: ");
        JTextField tfPassw = new JTextField();
        JLabel lbDescr = new JLabel("Description: ");
        JTextField tfDescr = new JTextField();
        JLabel lbUrl = new JLabel("URL: ");
        JTextField tfUrl = new JTextField();
        Object[] ob = {lbTitle, tfTitle, lbLogin, tfLogin, lbPassw, tfPassw, lbDescr, tfDescr, lbUrl, tfUrl};
        int result = JOptionPane.showConfirmDialog(frame, ob, "Add Row to DB", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String PassBuf = "";
            try {
                //SecretKey skey=Actions.GetSecretKey();
                DesEncrypter encrypter = new DesEncrypter(skey);
                PassBuf = encrypter.encrypt(tfPassw.getText());
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            }
            UrlValidator url_valid = UrlValidator.getInstance();
            if (tfPassw.getText().isEmpty() || tfLogin.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Login/Password Fields don't be empty !", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!url_valid.isValid(tfUrl.getText()) && !tfUrl.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "URL is not valid !", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                ejbDaoJDBC.insertRow(tfTitle.getText(), tfLogin.getText(), PassBuf, tfDescr.getText(), tfUrl.getText());
                //dbManager.executeUpdate("INSERT INTO hosts(title,login,passw,descr,url) VALUES('" + TitleTF.getText() + "','" + LoginTF.getText() + "','" + PassBuf + "','" + CommentTF.getText() + "','" + UrlTF.getText() + "')");
                RefreshJTable(KeeperTable);
                dbLastChange(frame);
            }
        }
    }

    public static void addUser(JFrame frame) {
        JLabel jUserName = new JLabel("UserName for DataBase owner: ");
        JTextField userName = new JTextField();
        JLabel jPassword = new JLabel("Password for DB and DB-owner: ");
        JTextField password = new JPasswordField();
        JLabel jRePassw = new JLabel("Retype Password : ");
        JTextField repassw = new JPasswordField();
        JLabel jUserHash = new JLabel("Select Hash Type for password: ");
        JComboBox userHash = new JComboBox();
        userHash.setModel(new javax.swing.DefaultComboBoxModel(typeHashArray));
        Object[] ob = {jUserName, userName, jPassword, password, jRePassw, repassw, jUserHash, userHash};
        int result = JOptionPane.showConfirmDialog(frame, ob, "Add new User and new DataBase", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            File file = new File("users/" + userName.getText() + "/" + userName.getText() + ".xml");
            if (password.getText().equals(repassw.getText()) && !password.getText().contains(" ") && !userName.getText().equals("") && !password.getText().equals("") && !file.exists()) {
                addlogin = userName.getText();
                addhash = userHash.getSelectedItem().toString();
                addDbLastChange = "never";
                switch (addhash) {
                    case "md2":
                        addpassw = DigestUtils.md2Hex(password.getText());
                        break;
                    case "md5":
                        addpassw = DigestUtils.md5Hex(password.getText());
                        break;
                    case "sha1":
                        addpassw = DigestUtils.sha1Hex(password.getText());
                        break;
                    case "sha256":
                        addpassw = DigestUtils.sha256Hex(password.getText());
                        break;
                    case "sha384":
                        addpassw = DigestUtils.sha384Hex(password.getText());
                        break;
                    case "sha512":
                        addpassw = DigestUtils.sha512Hex(password.getText());
                        break;
                }
                //addpassw = DigestUtils.md5Hex(password.getText());
                //Md5Blogic.md5Custom(password.getText());
                UserCfgXerces.Save();
                setSecretKey(addlogin);
                JOptionPane.showMessageDialog(frame, "User succefull added, restart PKG and login as '" + addlogin + "'");
            } else {
                JOptionPane.showMessageDialog(frame, "user already exists or login/password incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    public static void gotoURL(JTable KeeperTable, JFrame frame) {
        try {
            KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 6);
        } catch (IndexOutOfBoundsException ai) {
            JOptionPane.showMessageDialog(frame, "Select row please !", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Mail_Url.goURL((String) KeeperTable.getValueAt(KeeperTable.getSelectedRow(), 6));
    }

    public void quitExit(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "Exit PKG ?", "Quit PKG", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void exportToXML(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "All passwords will be decrypted in export-file, continuue ?", "Attention !", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            FileWriter writeCFG = null;
            int N = 1;
            try {
                File cfgfile = new File("pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".xml");
                writeCFG = new FileWriter(cfgfile);
                writeCFG.append("<" + Actions.login + ">\n");
                //+ " DB last change = "+Actions.DbLastChange+">\n");
                //SqlRowSet rs = null;
                SqlRowSet rs = ejbDaoJDBC.getAllasSRS();
                //rs = null;
                //rs = dbManager.executeQuery("SELECT * FROM hosts");
                while (rs.next()) {
                    writeCFG.append("  <record_" + N + ">\n");
                    writeCFG.append("     <title>" + rs.getString(2) + "</title>\n");
                    writeCFG.append("     <login>" + rs.getString(3) + "</login>\n");
                    String PassBuf = "";
                    try {
                        //SecretKey skey=Actions.GetSecretKey();
                        DesEncrypter encrypter = new DesEncrypter(Actions.skey);
                        PassBuf = encrypter.decrypt(rs.getString(4));
                    } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                        Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    writeCFG.append("     <password>" + PassBuf + "</password>\n");
                    writeCFG.append("     <description>" + rs.getString(5) + "</description>\n");
                    writeCFG.append("     <url>" + rs.getString(6) + "</url>\n");
                    writeCFG.append("  </record_" + N + ">\n");
                    N++;
                }
                //rs.close();
                writeCFG.append("</" + Actions.login + ">\n");
                JOptionPane.showMessageDialog(frame, "XML export success: pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".xml");
            } catch (IOException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (writeCFG != null) {
                    try {
                        writeCFG.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void exportToHTML(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "All passwords will be decrypted in export-file, continuue ?", "Attention !", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            FileWriter writeCFG = null;
            int N = 1;
            try {
                File cfgfile = new File("pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".html");
                writeCFG = new FileWriter(cfgfile);
                writeCFG.append("<html>\n");
                writeCFG.append("<head>\n");
                writeCFG.append("   <meta charset=UTF-8>\n");
                writeCFG.append("   <title>\n");
                writeCFG.append("   PKG export for user " + Actions.login + ", DB last change = " + Actions.DbLastChange + "\n");
                writeCFG.append("   </title>\n");
                writeCFG.append("</head>\n");
                writeCFG.append("<body>\n");
                writeCFG.append("<h3>\n");
                writeCFG.append("PKG html export for user " + Actions.login + ", DB last change = " + Actions.DbLastChange + "\n");
                writeCFG.append("</h3>\n");
                writeCFG.append("<table border=1 cellpading=3 cellspacing=1>\n");
                writeCFG.append("<tr>\n");
                writeCFG.append("  <td><b>N</b></td>\n");
                writeCFG.append("  <td><b>Title</b></td>\n");
                writeCFG.append("  <td><b>Login</b></td>\n");
                writeCFG.append("  <td><b>Password</b></td>\n");
                writeCFG.append("  <td><b>Description</b></td>\n");
                writeCFG.append("  <td><b>URL</b></td>\n");
                writeCFG.append("</tr>\n");
                //SqlRowSet rs = null;
                SqlRowSet rs = ejbDaoJDBC.getAllasSRS();
                //rs = null;
                //rs = dbManager.executeQuery("SELECT * FROM hosts");
                while (rs.next()) {
                    writeCFG.append("<tr>\n");
                    writeCFG.append("  <td>" + N + "</td>\n");
                    writeCFG.append("  <td>" + rs.getString(2) + "</td>\n");
                    writeCFG.append("  <td>" + rs.getString(3) + "</td>\n");
                    String PassBuf = "";
                    try {
                        //SecretKey skey=Actions.GetSecretKey();
                        DesEncrypter encrypter = new DesEncrypter(Actions.skey);
                        PassBuf = encrypter.decrypt(rs.getString(4));
                    } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                        Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    writeCFG.append("   <td>" + PassBuf + "</td>\n");
                    writeCFG.append("   <td>" + rs.getString(5) + "</td>\n");
                    writeCFG.append("   <td>" + rs.getString(6) + "</td>\n");
                    writeCFG.append("</tr>\n");
                    N++;
                }
                //rs.close();
                writeCFG.append("</table>\n");
                writeCFG.append("</body>\n");
                writeCFG.append("</html>\n");
                JOptionPane.showMessageDialog(frame, "HTML export success: pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".html");
            } catch (IOException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (writeCFG != null) {
                    try {
                        writeCFG.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void importFromXML(JTable KeeperTable, JFrame frame) {
        String rezult = "false", putf = "", putd = "", ititle = "", ilogin = "", ipassw = "", idescr = "", iurl = "", PassBuf = "";
        JFileChooser myf = new JFileChooser();
        myf.addChoosableFileFilter(new MFileFilter(".xml"));
        myf.setAcceptAllFileFilterUsed(false);
        myf.setCurrentDirectory(new File(new File("").getAbsolutePath()));
        switch (myf.showDialog(frame, "Import from XML for user=" + Actions.login)) {
            case JFileChooser.APPROVE_OPTION:
                putf = myf.getSelectedFile().getPath();
                putd = myf.getSelectedFile() + "";
                ConfigLoader cfg = new ConfigLoader("org.apache.xerces.parsers.SAXParser");
                int N = 1;
                try {
                    cfg.LoadFromFile(putf);
                    do {
                        ititle = cfg.getTagValue(Actions.login + ".record_" + N + ".title");
                        ilogin = cfg.getTagValue(Actions.login + ".record_" + N + ".login");
                        ipassw = cfg.getTagValue(Actions.login + ".record_" + N + ".password");
                        idescr = cfg.getTagValue(Actions.login + ".record_" + N + ".description");
                        iurl = cfg.getTagValue(Actions.login + ".record_" + N + ".url");
                        if (ititle.isEmpty() && ilogin.isEmpty() && ipassw.isEmpty() && idescr.isEmpty() && iurl.isEmpty()) {
                            return;
                        }
                        try {
                            DesEncrypter encrypter = new DesEncrypter(Actions.skey);
                            PassBuf = encrypter.encrypt(ipassw);
                        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ejbDaoJDBC.insertRow(ititle, ilogin, PassBuf, idescr, iurl);
                        //dbManager.executeUpdate("INSERT INTO hosts(title,login,passw,descr,url) VALUES('" + ititle + "','" + ilogin + "','" + PassBuf + "','" + idescr + "','" + iurl + "')");
                        RefreshJTable(KeeperTable);
                        N++;
                        rezult = "true";
                    } while (true);
                } catch (NullPointerException e) {
                    System.out.println(e);
                    if (N > 2) {
                        dbLastChange(frame);
                        JOptionPane.showMessageDialog(frame, "XML import success");
                    } else {
                        if (rezult.equals("false")) {
                            JOptionPane.showMessageDialog(frame, "Import error", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e);
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    public void clearDB(JTable KeeperTable, JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "Delete ALL records from DB ?", "All delete ?", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            ejbDaoJDBC.clearAll();
            //dbManager.executeUpdate("DELETE FROM hosts");
            RefreshJTable(KeeperTable);
            dbLastChange(frame);
        }
    }

    public void dbLastChange(JFrame frame) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
        Date dd = new Date();
        Date dt = new Date();
        addDbLastChange = sdf.format(dd) + " " + stf.format(dt);
        DbLastChange = addDbLastChange;
        UserCfgXerces.Save();
        frame.setTitle(Zagolovok + " User=" + login + ", changed=" + DbLastChange);
    }

    public void exportToCSV(JFrame frame) {
        try {
            //rs = null;
            //rs = dbManager.executeQuery("SELECT * FROM hosts");
            ResultSet rs = ejbDaoJDBC.getAllasRS();
            CSVWriter csv_writer = new CSVWriter(new FileWriter("pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".csv"), ';');
            csv_writer.writeAll(rs, true); // true = write a header !!! 
            csv_writer.flush();
            csv_writer.close();
            //rs.close();
            JOptionPane.showMessageDialog(frame, "CSV export success: pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".csv");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Export error", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void importFromCSV(JTable KeeperTable, JFrame frame) {
        String rezult = "false", putf = "", putd = "", ititle = "", ilogin = "", ipassw = "", idescr = "", iurl = "", PassBuf = "";
        JFileChooser myf = new JFileChooser();
        myf.addChoosableFileFilter(new MFileFilter(".csv"));
        myf.setAcceptAllFileFilterUsed(false);
        myf.setCurrentDirectory(new File(new File("").getAbsolutePath()));
        int N = 1;
        switch (myf.showDialog(frame, "Import from CSV for user=" + login)) {
            case JFileChooser.APPROVE_OPTION:
                putf = myf.getSelectedFile().getPath();
                putd = myf.getSelectedFile() + "";
                try {
                    CSVReader reader = new CSVReader(new FileReader(putf), ';');
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        ititle = nextLine[1];
                        ilogin = nextLine[2];
                        ipassw = nextLine[3];
                        idescr = nextLine[4];
                        iurl = nextLine[5];
                        if (ititle.isEmpty() && ilogin.isEmpty() && ipassw.isEmpty() && idescr.isEmpty() && iurl.isEmpty()) {
                            return;
                        }
                        if (N != 1) {
                            ejbDaoJDBC.insertRow(ititle, ilogin, ipassw, idescr, iurl);
                            //dbManager.executeUpdate("INSERT INTO hosts(title,login,passw,descr,url) VALUES('" + ititle + "','" + ilogin + "','" + ipassw + "','" + idescr + "','" + iurl + "')");
                        }
                        N++;
                        rezult = "true";
                    }
                    RefreshJTable(KeeperTable);
                    //JOptionPane.showMessageDialog(frame,"CSV import success");
                } catch (NullPointerException e) {
                    System.out.println(e);
                    if (N > 2) {
                        dbLastChange(frame);
                        JOptionPane.showMessageDialog(frame, "CSV import success");
                    } else {
                        if (rezult.equals("false")) {
                            JOptionPane.showMessageDialog(frame, "Import error", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (RuntimeException e) {
                    System.out.println(e);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    public void exportToXLS(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "All passwords will be decrypted in export-file, continuue ?", "Attention !", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try {
                //SqlRowSet rs = null;
                SqlRowSet rs = ejbDaoJDBC.getAllasSRS();
                //rs = null;
                //rs = dbManager.executeQuery("SELECT * FROM hosts");
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("lawix10");
                HSSFRow rowhead = sheet.createRow((short) 0);
                rowhead.createCell((short) 0).setCellValue("N");
                rowhead.createCell((short) 1).setCellValue("Title");
                rowhead.createCell((short) 2).setCellValue("Login");
                rowhead.createCell((short) 3).setCellValue("Password");
                rowhead.createCell((short) 4).setCellValue("Description");
                rowhead.createCell((short) 5).setCellValue("URL");
                int i = 1;
                String PassBuf = "";
                while (rs.next()) {
                    HSSFRow row = sheet.createRow((short) i);
                    row.createCell((short) 0).setCellValue(Integer.toString(i));
                    row.createCell((short) 1).setCellValue(rs.getString(2));
                    row.createCell((short) 2).setCellValue(rs.getString(3));
                    try {
                        DesEncrypter encrypter = new DesEncrypter(Actions.skey);
                        PassBuf = encrypter.decrypt(rs.getString(4));
                    } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                        Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    row.createCell((short) 3).setCellValue(PassBuf);
                    row.createCell((short) 4).setCellValue(rs.getString(5));
                    row.createCell((short) 5).setCellValue(rs.getString(6));
                    i++;
                }
                FileOutputStream fileOut = new FileOutputStream("pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".xls");
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
                JOptionPane.showMessageDialog(frame, "XLS export success: pkg_" + Actions.login + "_export_" + sdf.format(dd) + ".xls");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void importFromXLS(JTable KeeperTable, JFrame frame) {
        String rezult = "false", putf = "", putd = "", ititle = "", ilogin = "", ipassw = "", idescr = "", iurl = "", PassBuf = "";
        JFileChooser myf = new JFileChooser();
        myf.addChoosableFileFilter(new MFileFilter(".xls"));
        myf.setAcceptAllFileFilterUsed(false);
        myf.setCurrentDirectory(new File(new File("").getAbsolutePath()));
        switch (myf.showDialog(frame, "Import from XLS for user=" + Actions.login)) {
            case JFileChooser.APPROVE_OPTION:
                putf = myf.getSelectedFile().getPath();
                putd = myf.getSelectedFile() + "";
                try {
                    FileInputStream input = new FileInputStream(putf);
                    POIFSFileSystem fs = new POIFSFileSystem(input);
                    HSSFWorkbook wb = new HSSFWorkbook(fs);
                    HSSFSheet sheet = wb.getSheetAt(0);
                    Row row;
                    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                        row = sheet.getRow(i);
                        ititle = row.getCell(1).getStringCellValue();
                        ilogin = row.getCell(2).getStringCellValue();
                        try {
                            DesEncrypter encrypter = new DesEncrypter(Actions.skey);
                            PassBuf = encrypter.encrypt(row.getCell(3).getStringCellValue());
                        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                            Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ipassw = PassBuf;
                        idescr = row.getCell(4).getStringCellValue();
                        iurl = row.getCell(5).getStringCellValue();
                        if (ititle.isEmpty() && ilogin.isEmpty() && ipassw.isEmpty() && idescr.isEmpty() && iurl.isEmpty()) {
                            return;
                        }
                        ejbDaoJDBC.insertRow(ititle, ilogin, ipassw, idescr, iurl);
                        //dbManager.executeUpdate("INSERT INTO hosts(title,login,passw,descr,url) VALUES('" + ititle + "','" + ilogin + "','" + ipassw + "','" + idescr + "','" + iurl + "')");
                        rezult = "true";
                    }
                    input.close();
                    RefreshJTable(KeeperTable);
                    JOptionPane.showMessageDialog(frame, "XLS import success");
                    dbLastChange(frame);
                } catch (NullPointerException e) {
                    JOptionPane.showMessageDialog(frame, "Import error", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RuntimeException | IOException e) {
                    JOptionPane.showMessageDialog(frame, "Import error", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }

    public void searchRefreshJTable(JTable KeeperTable, JTextField searchTF, JComboBox searchCombo) {
        newTableModel(KeeperTable);
        int N = 1;
        String searchString = searchTF.getText().trim();
        //rs = null;
        SqlRowSet rs = null;
        //SqlRowSet rs = ejbDaoJDBCS();
        switch (searchCombo.getSelectedItem().toString()) {
            case "title":
                //rs = dbManager.executeQuery("SELECT * FROM hosts WHERE title LIKE '%" + searchString + "%'");
                rs = ejbDaoJDBC.searchByFieldasSRS("title", searchString);
                break;
            case "login":
                //rs = dbManager.executeQuery("SELECT * FROM hosts WHERE login LIKE '%" + searchString + "%'");
                rs = ejbDaoJDBC.searchByFieldasSRS("login", searchString);
                break;
            case "description":
                //rs = dbManager.executeQuery("SELECT * FROM hosts WHERE descr LIKE '%" + searchString + "%'");
                rs = ejbDaoJDBC.searchByFieldasSRS("descr", searchString);
                break;
            case "url":
                //rs = dbManager.executeQuery("SELECT * FROM hosts WHERE url LIKE '%" + searchString + "%'");
                rs = ejbDaoJDBC.searchByFieldasSRS("url", searchString);
                break;
        }
        //rs = dbManager.executeQuery("SELECT * FROM hosts");
        while (rs.next()) {
            String[] rowdata = {"" + N, "" + Integer.parseInt(rs.getString(1)), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)};
            tableModel.addRow(rowdata);
            N++;
        }
        //rs.close();
    }

}
