package PKG;

import PKG.addons.Actions;
//import PKG.addons.About;
//import PKG.Cfg.AppCfgJDOM;
//import PKG.Cfg.AppCfgXerces;
import PKG.addons.TextTransfer;
import java.awt.Dimension;
//import PKG.backup.LoadZipFile;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
//import net.sf.tinylaf.Theme;
import de.muntjak.tinylookandfeel.Theme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@DependsOn(value = {"actions"})
public class PKGgui extends javax.swing.JFrame {
    
    @Inject
    private Actions actions; 
    
    @Inject
    private DaoJDBC ejbDaoJDBC;    
    
    @Value("${skin}")
    private String currentLAF; 
    @Value("${tema}")
    private String tema;       
    
    public static PKGgui frame;
    //public static DbManager dbManager;
    public static DefaultTableModel tableModel = new DefaultTableModel();
    //public static ResultSet rs;
    //public static String pkgTitle;
    public static Date dd = new Date();
    //public static Date dt;  // = new Date();
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy"); 
    //public static SimpleDateFormat stf = new SimpleDateFormat("hh:mm:ss");        
    public static String [] numSymbolsArray  = {
        "chars=7",
        "chars=8",
        "chars=9",
        "chars=10",
        "chars=11",
        "chars=12",
        "chars=13",
        "chars=14",
        "chars=15",
        "chars=16"
    };
    public static String [] typeAlphabetArray= {"aA-zZ_0-9","A-Z_0-9","a-z_0-9"};
    public static String [] arrayExport = { "HTML","XML","CSV","XLS" }; 
    public static String [] arrayImport = { "XML","CSV","XLS" }; 
    public static String [] searchArray= {"title","login","description","url"};    
    public static String Zagolovok="Password Keeper+Generator, v1.0.6, build 19-01-21.";
    private static Dimension frameDimension = new Dimension(702, 480);
    
    public PKGgui() {
        initComponents();
        //AppCfgJDOM.Load();
        //AppCfgXerces.Load();
        //actions.guiInit();
        ImageIcon icone = new ImageIcon(getClass().getResource("/PKG/img/SubFrameIcon.png"));
        this.setIconImage(icone.getImage());
        this.setTitle(Zagolovok + " User="+actions.login+", changed="+actions.DbLastChange);
        this.TFpassw.setComponentPopupMenu(mPopupGenerator);
        this.KeeperTable.setComponentPopupMenu(mPopupKeeper); 
        actions.newTableModel(KeeperTable);
        this.KeeperTable.setModel(tableModel);
        this.KeeperTable.setAutoCreateRowSorter(true);        
        this.bcomboNumSymbols.setModel(new DefaultComboBoxModel<>(numSymbolsArray));        
        //this.bcomboNumSymbols.setSelectedItem(""+actions.numSymbols); 
        this.bcomboAlphabet.setModel(new DefaultComboBoxModel<>(typeAlphabetArray));        
        //this.bcomboAlphabet.setSelectedItem(actions.typeAlfavit);
        this.bExportCombo.setModel(new DefaultComboBoxModel<>(arrayExport)); 
        this.bImportCombo.setModel(new DefaultComboBoxModel<>(arrayImport));
        this.searchCombo.setModel(new DefaultComboBoxModel<>(searchArray));        
        //this.setResizable(false);
        //this.bcomboNumSymbols.set
        this.mSkin.setVisible(false);
        this.bSkin.setVisible(false);
    }
    
    @PostConstruct
    public void afterBirn() {
        File file = new File("db/" + actions.login);
        if (!file.exists()) {        
            ejbDaoJDBC.createTable();
        }
        actions.RefreshJTable(KeeperTable);
        if (actions.login.equals("user")) {
            JOptionPane.showMessageDialog(frame, "Please, don't work under default user ! \nAt first create for yourself your User and DB !");
            actions.addUser(frame);
        }        
    }   
    
    public void setLF(JFrame frame) {
        if (currentLAF.contains("tinylaf")) {
            Theme.loadTheme(new File(tema));
        }        
        try {
            UIManager.setLookAndFeel(currentLAF);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(PKGgui.class.getName()).log(Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(frame);
    }    
    
    public void about() {
        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/PKG/img/pkg_100x75.gif"));
        JOptionPane.showMessageDialog(frame,
                        "Free portable cross-platform multi-user\n"
                      + "Password Manager, 100%-pure Java.\n"
                      + "DB for each pkg-user is encrypted and protected\n"
                      + "by pkg-user hash. In addition - passwords in DB\n"
                      + "are stored in encrypted form. In result - \n"
                      + "stored passwords are double encrypted !\n"
                      + "Passwords of pkg-users are not stored in program -\n"
                      + "stored and compared only hashes of passwords.\n"
                      + "Support md2, md5, sha1, sha256, sha384, sha512 hashes,\n"
                      + "export to CSV/HTML/XLS/XML + import from CSV/XLS/XML.\n"
                      + "Use Java Secure Random for Password Generator.\n"        
                      + "Tested in Windows/Linux. Need Jre-1.8.\n"
                      + "Roman Koldaev, Saratov city, Russia.\n"
                      + "E-mail = harp07@mail.ru, \nHome = http://j-pkg.sf.net/\n",
                Zagolovok,JOptionPane.INFORMATION_MESSAGE,icon);        
    }
    
    public void CopyCP () {
        TextTransfer textTransfer = new TextTransfer();
        textTransfer.setClipboardContents(TFpassw.getText());        
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mPopupGenerator = new javax.swing.JPopupMenu();
        mpGenerate = new javax.swing.JMenuItem();
        mpCopyClipboard = new javax.swing.JMenuItem();
        mpShowClipboard = new javax.swing.JMenuItem();
        mpClearGenerator = new javax.swing.JMenuItem();
        mPopupKeeper = new javax.swing.JPopupMenu();
        mpDbEditMenu = new javax.swing.JMenu();
        mpAddRecord = new javax.swing.JMenuItem();
        mpEditRow = new javax.swing.JMenuItem();
        mpDeleteRow = new javax.swing.JMenuItem();
        mpClearDB = new javax.swing.JMenuItem();
        mpClipCpMenu = new javax.swing.JMenu();
        mpCpPasswToClip = new javax.swing.JMenuItem();
        mpShowClipboard1 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        mpExportMenu = new javax.swing.JMenu();
        mpExportHtm = new javax.swing.JMenuItem();
        mpExportXml = new javax.swing.JMenuItem();
        mpExportCSV = new javax.swing.JMenuItem();
        mpExportXLS = new javax.swing.JMenuItem();
        mpImportMenu = new javax.swing.JMenu();
        mpImportXML = new javax.swing.JMenuItem();
        mpImportCSV = new javax.swing.JMenuItem();
        mpImportXLS = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        mpGotoURL = new javax.swing.JMenuItem();
        jToolBar10 = new javax.swing.JToolBar();
        jToolBar7 = new javax.swing.JToolBar();
        bRunGenerate = new javax.swing.JButton();
        bCopyGenerated = new javax.swing.JButton();
        btnClearGenerate = new javax.swing.JButton();
        jToolBar9 = new javax.swing.JToolBar();
        bExport = new javax.swing.JButton();
        bExportCombo = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        bImport = new javax.swing.JButton();
        bImportCombo = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        bAddRecord = new javax.swing.JButton();
        bDeleteRecord = new javax.swing.JButton();
        bEditRow = new javax.swing.JButton();
        bClearDB = new javax.swing.JButton();
        bCpPasswToClip = new javax.swing.JButton();
        bGoURL = new javax.swing.JButton();
        jToolBar12 = new javax.swing.JToolBar();
        bShowClipboard = new javax.swing.JButton();
        bAddUser = new javax.swing.JButton();
        bSkin = new javax.swing.JButton();
        btnAbout = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        GeneratorToolBar = new javax.swing.JToolBar();
        jToolBar4 = new javax.swing.JToolBar();
        TFpassw = new javax.swing.JTextField();
        checkSpecial = new javax.swing.JCheckBox();
        jToolBar2 = new javax.swing.JToolBar();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        bcomboAlphabet = new javax.swing.JComboBox();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        jToolBar3 = new javax.swing.JToolBar();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        bcomboNumSymbols = new javax.swing.JComboBox();
        jSeparator15 = new javax.swing.JToolBar.Separator();
        KeeperToolBar = new javax.swing.JToolBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        KeeperTable = new javax.swing.JTable();
        searchTBar = new javax.swing.JToolBar();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        searchLabel1 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        searchCombo = new javax.swing.JComboBox<>();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        searhLabel2 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        searchTF = new javax.swing.JTextField();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        searchButton = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        searchShowAll = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        mAddUser = new javax.swing.JMenuItem();
        mSkin = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemGenerate = new javax.swing.JMenuItem();
        jMenuItemCopy = new javax.swing.JMenuItem();
        mClearGenerator = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        mDbEditMenu = new javax.swing.JMenu();
        mAddRecord = new javax.swing.JMenuItem();
        mDeleteRecord = new javax.swing.JMenuItem();
        mEditRow = new javax.swing.JMenuItem();
        mClearDB = new javax.swing.JMenuItem();
        mClipCpMenu = new javax.swing.JMenu();
        mCpPasswToClip = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        mExportMenu = new javax.swing.JMenu();
        mExportHtm = new javax.swing.JMenuItem();
        mExportXml = new javax.swing.JMenuItem();
        mExportCSV = new javax.swing.JMenuItem();
        mExportXLS = new javax.swing.JMenuItem();
        mImportMenu = new javax.swing.JMenu();
        mImportXML = new javax.swing.JMenuItem();
        mImportCSV = new javax.swing.JMenuItem();
        mImportXLS = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        mGotoURL = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        mShowClipboard = new javax.swing.JMenuItem();
        jMenuItemAbout = new javax.swing.JMenuItem();

        mpGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/play_green-1.png"))); // NOI18N
        mpGenerate.setText("Generate");
        mpGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpGenerateActionPerformed(evt);
            }
        });
        mPopupGenerator.add(mpGenerate);

        mpCopyClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_plus.png"))); // NOI18N
        mpCopyClipboard.setText("Copy to Clipboard");
        mpCopyClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpCopyClipboardActionPerformed(evt);
            }
        });
        mPopupGenerator.add(mpCopyClipboard);

        mpShowClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_lupa-16.png"))); // NOI18N
        mpShowClipboard.setText("Show Clipboard");
        mpShowClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpShowClipboardActionPerformed(evt);
            }
        });
        mPopupGenerator.add(mpShowClipboard);

        mpClearGenerator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/edit_clear-16.png"))); // NOI18N
        mpClearGenerator.setText("Clear Generator");
        mpClearGenerator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpClearGeneratorActionPerformed(evt);
            }
        });
        mPopupGenerator.add(mpClearGenerator);

        mpDbEditMenu.setText("View/Edit");

        mpAddRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/add1-green-16.png"))); // NOI18N
        mpAddRecord.setText("Add Record");
        mpAddRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpAddRecordActionPerformed(evt);
            }
        });
        mpDbEditMenu.add(mpAddRecord);

        mpEditRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/document_edit.png"))); // NOI18N
        mpEditRow.setText("View/Edit Record");
        mpEditRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpEditRowActionPerformed(evt);
            }
        });
        mpDbEditMenu.add(mpEditRow);

        mpDeleteRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/list_remove-16.png"))); // NOI18N
        mpDeleteRow.setText("Delete Record");
        mpDeleteRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpDeleteRowActionPerformed(evt);
            }
        });
        mpDbEditMenu.add(mpDeleteRow);

        mpClearDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clear-db-16.png"))); // NOI18N
        mpClearDB.setText("Clear DB");
        mpClearDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpClearDBActionPerformed(evt);
            }
        });
        mpDbEditMenu.add(mpClearDB);

        mPopupKeeper.add(mpDbEditMenu);

        mpClipCpMenu.setText("Copy");

        mpCpPasswToClip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_sign.png"))); // NOI18N
        mpCpPasswToClip.setText("Copy Password to ClipBoard");
        mpCpPasswToClip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpCpPasswToClipActionPerformed(evt);
            }
        });
        mpClipCpMenu.add(mpCpPasswToClip);

        mpShowClipboard1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_lupa-16.png"))); // NOI18N
        mpShowClipboard1.setText("Show Clipboard");
        mpShowClipboard1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpShowClipboard1ActionPerformed(evt);
            }
        });
        mpClipCpMenu.add(mpShowClipboard1);

        mPopupKeeper.add(mpClipCpMenu);
        mPopupKeeper.add(jSeparator4);

        mpExportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/export-16.png"))); // NOI18N
        mpExportMenu.setText("Export");

        mpExportHtm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/html-16.png"))); // NOI18N
        mpExportHtm.setText("Export to HTML");
        mpExportHtm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpExportHtmActionPerformed(evt);
            }
        });
        mpExportMenu.add(mpExportHtm);

        mpExportXml.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xml-well-16.png"))); // NOI18N
        mpExportXml.setText("Export to XML");
        mpExportXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpExportXmlActionPerformed(evt);
            }
        });
        mpExportMenu.add(mpExportXml);

        mpExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/csv-export-16.png"))); // NOI18N
        mpExportCSV.setText("Export to CSV");
        mpExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpExportCSVActionPerformed(evt);
            }
        });
        mpExportMenu.add(mpExportCSV);

        mpExportXLS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xls-16.png"))); // NOI18N
        mpExportXLS.setText("Export to XLS");
        mpExportXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpExportXLSActionPerformed(evt);
            }
        });
        mpExportMenu.add(mpExportXLS);

        mPopupKeeper.add(mpExportMenu);

        mpImportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/import-16.png"))); // NOI18N
        mpImportMenu.setText("Import");

        mpImportXML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xml-well-16.png"))); // NOI18N
        mpImportXML.setText("Import from XML");
        mpImportXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpImportXMLActionPerformed(evt);
            }
        });
        mpImportMenu.add(mpImportXML);

        mpImportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/csv-import-16.png"))); // NOI18N
        mpImportCSV.setText("Import from CSV");
        mpImportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpImportCSVActionPerformed(evt);
            }
        });
        mpImportMenu.add(mpImportCSV);

        mpImportXLS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xls-16.png"))); // NOI18N
        mpImportXLS.setText("Import from XLS");
        mpImportXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpImportXLSActionPerformed(evt);
            }
        });
        mpImportMenu.add(mpImportXLS);

        mPopupKeeper.add(mpImportMenu);
        mPopupKeeper.add(jSeparator7);

        mpGotoURL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/world_connect-16.png"))); // NOI18N
        mpGotoURL.setText("Go to URL");
        mpGotoURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mpGotoURLActionPerformed(evt);
            }
        });
        mPopupKeeper.add(mpGotoURL);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SJpg");
        setUndecorated(true);
        setSize(new java.awt.Dimension(244, 283));

        jToolBar10.setFloatable(false);

        jToolBar7.setBorder(javax.swing.BorderFactory.createTitledBorder("Generator"));
        jToolBar7.setFloatable(false);

        bRunGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/play1hot.png"))); // NOI18N
        bRunGenerate.setToolTipText("Generate Password");
        bRunGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRunGenerateActionPerformed(evt);
            }
        });
        jToolBar7.add(bRunGenerate);

        bCopyGenerated.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/clipboard-blue.png"))); // NOI18N
        bCopyGenerated.setToolTipText("Copy generated to ClipBoard");
        bCopyGenerated.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCopyGeneratedActionPerformed(evt);
            }
        });
        jToolBar7.add(bCopyGenerated);

        btnClearGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/edit_clear.png"))); // NOI18N
        btnClearGenerate.setToolTipText("clear generator");
        btnClearGenerate.setFocusable(false);
        btnClearGenerate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClearGenerate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClearGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearGenerateActionPerformed(evt);
            }
        });
        jToolBar7.add(btnClearGenerate);

        jToolBar10.add(jToolBar7);

        jToolBar9.setBorder(javax.swing.BorderFactory.createTitledBorder("Keeper"));
        jToolBar9.setFloatable(false);

        bExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/export-24.png"))); // NOI18N
        bExport.setToolTipText("Export to:");
        bExport.setFocusable(false);
        bExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bExportActionPerformed(evt);
            }
        });
        jToolBar9.add(bExport);

        bExportCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "exe", "com" }));
        bExportCombo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar9.add(bExportCombo);
        jToolBar9.add(jSeparator1);

        bImport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/import-24.png"))); // NOI18N
        bImport.setToolTipText("Import from:");
        bImport.setFocusable(false);
        bImport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bImport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bImportActionPerformed(evt);
            }
        });
        jToolBar9.add(bImport);

        bImportCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "doc", "rtf" }));
        bImportCombo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar9.add(bImportCombo);
        jToolBar9.add(jSeparator2);

        bAddRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/add1-green.png"))); // NOI18N
        bAddRecord.setToolTipText("Add Record to DB");
        bAddRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddRecordActionPerformed(evt);
            }
        });
        jToolBar9.add(bAddRecord);

        bDeleteRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/list_remove.png"))); // NOI18N
        bDeleteRecord.setToolTipText("Delete Record from DB");
        bDeleteRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteRecordActionPerformed(evt);
            }
        });
        jToolBar9.add(bDeleteRecord);

        bEditRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/edit-blue.png"))); // NOI18N
        bEditRow.setToolTipText("View/Edit DB Record");
        bEditRow.setFocusable(false);
        bEditRow.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bEditRow.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bEditRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bEditRowActionPerformed(evt);
            }
        });
        jToolBar9.add(bEditRow);

        bClearDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/clear-db-24.png"))); // NOI18N
        bClearDB.setToolTipText("Clear DB");
        bClearDB.setFocusable(false);
        bClearDB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bClearDB.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bClearDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bClearDBActionPerformed(evt);
            }
        });
        jToolBar9.add(bClearDB);

        bCpPasswToClip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/clipboard-blue.png"))); // NOI18N
        bCpPasswToClip.setToolTipText("Copy Password to ClipBoard");
        bCpPasswToClip.setFocusable(false);
        bCpPasswToClip.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bCpPasswToClip.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bCpPasswToClip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bCpPasswToClipActionPerformed(evt);
            }
        });
        jToolBar9.add(bCpPasswToClip);

        bGoURL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/url-internet.png"))); // NOI18N
        bGoURL.setToolTipText("Go to URL");
        bGoURL.setFocusable(false);
        bGoURL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bGoURL.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bGoURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bGoURLActionPerformed(evt);
            }
        });
        jToolBar9.add(bGoURL);

        jToolBar10.add(jToolBar9);

        jToolBar12.setBorder(javax.swing.BorderFactory.createTitledBorder("System"));
        jToolBar12.setFloatable(false);

        bShowClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/clipboard_search.png"))); // NOI18N
        bShowClipboard.setToolTipText("Show Clipboard");
        bShowClipboard.setFocusable(false);
        bShowClipboard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bShowClipboard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bShowClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bShowClipboardActionPerformed(evt);
            }
        });
        jToolBar12.add(bShowClipboard);

        bAddUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/plus-icon.png"))); // NOI18N
        bAddUser.setToolTipText("Create User and DB");
        bAddUser.setFocusable(false);
        bAddUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bAddUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddUserActionPerformed(evt);
            }
        });
        jToolBar12.add(bAddUser);

        bSkin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/skin_color_chooser-24.png"))); // NOI18N
        bSkin.setToolTipText("Set Skin");
        bSkin.setFocusable(false);
        bSkin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bSkin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bSkin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSkinActionPerformed(evt);
            }
        });
        jToolBar12.add(bSkin);

        btnAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/info-book-green.png"))); // NOI18N
        btnAbout.setToolTipText("about");
        btnAbout.setFocusable(false);
        btnAbout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAboutActionPerformed(evt);
            }
        });
        jToolBar12.add(btnAbout);

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/24x24/exit.png"))); // NOI18N
        btnExit.setToolTipText("quit");
        btnExit.setFocusable(false);
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jToolBar12.add(btnExit);

        jToolBar10.add(jToolBar12);

        getContentPane().add(jToolBar10, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerSize(11);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        GeneratorToolBar.setBorder(javax.swing.BorderFactory.createTitledBorder("Generator"));
        GeneratorToolBar.setFloatable(false);

        jToolBar4.setBorder(javax.swing.BorderFactory.createTitledBorder("Generated Password"));
        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

        TFpassw.setFont(new java.awt.Font("Lucida Console", 1, 14)); // NOI18N
        TFpassw.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TFpassw.setComponentPopupMenu(mPopupGenerator);
        jToolBar4.add(TFpassw);

        checkSpecial.setText("Use Special symbols");
        checkSpecial.setFocusable(false);
        jToolBar4.add(checkSpecial);

        GeneratorToolBar.add(jToolBar4);

        jToolBar2.setBorder(javax.swing.BorderFactory.createTitledBorder("Alphabet"));
        jToolBar2.setFloatable(false);
        jToolBar2.add(jSeparator17);

        bcomboAlphabet.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bcomboAlphabet.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "a-z_0-9", "aA-zZ_0-9", "A-Z_0-9" }));
        bcomboAlphabet.setSelectedIndex(1);
        bcomboAlphabet.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar2.add(bcomboAlphabet);
        jToolBar2.add(jSeparator16);

        GeneratorToolBar.add(jToolBar2);

        jToolBar3.setBorder(javax.swing.BorderFactory.createTitledBorder("Chars"));
        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.add(jSeparator14);

        bcomboNumSymbols.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bcomboNumSymbols.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "7", "8", "9", "10" }));
        bcomboNumSymbols.setSelectedIndex(3);
        bcomboNumSymbols.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar3.add(bcomboNumSymbols);
        jToolBar3.add(jSeparator15);

        GeneratorToolBar.add(jToolBar3);

        jSplitPane1.setTopComponent(GeneratorToolBar);

        KeeperToolBar.setBorder(javax.swing.BorderFactory.createTitledBorder("Keeper"));
        KeeperToolBar.setFloatable(false);
        KeeperToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        KeeperToolBar.setPreferredSize(new java.awt.Dimension(466, 300));

        jScrollPane1.setViewportView(KeeperTable);

        KeeperToolBar.add(jScrollPane1);

        jSplitPane1.setBottomComponent(KeeperToolBar);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        searchTBar.setBorder(javax.swing.BorderFactory.createTitledBorder("Search ToolBar"));
        searchTBar.setFloatable(false);
        searchTBar.add(jSeparator6);

        searchLabel1.setText(" Search by: ");
        searchTBar.add(searchLabel1);
        searchTBar.add(jSeparator8);

        searchCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "description" }));
        searchCombo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchTBar.add(searchCombo);
        searchTBar.add(jSeparator9);

        searhLabel2.setText(" that contains: ");
        searchTBar.add(searhLabel2);
        searchTBar.add(jSeparator10);

        searchTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchTFKeyPressed(evt);
            }
        });
        searchTBar.add(searchTF);
        searchTBar.add(jSeparator11);

        searchButton.setText("Search");
        searchButton.setFocusable(false);
        searchButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        searchTBar.add(searchButton);
        searchTBar.add(jSeparator13);

        searchShowAll.setText("Show All");
        searchShowAll.setFocusable(false);
        searchShowAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        searchShowAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        searchShowAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchShowAllActionPerformed(evt);
            }
        });
        searchTBar.add(searchShowAll);
        searchTBar.add(jSeparator12);

        getContentPane().add(searchTBar, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText("System");

        mAddUser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/open-folder-plus-16.png"))); // NOI18N
        mAddUser.setText("Create User and DB");
        mAddUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAddUserActionPerformed(evt);
            }
        });
        jMenuFile.add(mAddUser);

        mSkin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/gnome_color_chooser.png"))); // NOI18N
        mSkin.setText("Change Skin");
        mSkin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mSkinActionPerformed(evt);
            }
        });
        jMenuFile.add(mSkin);

        jMenuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/quit.png"))); // NOI18N
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar1.add(jMenuFile);

        jMenu1.setText("Generator");

        jMenuItemGenerate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/play_green-1.png"))); // NOI18N
        jMenuItemGenerate.setText("Generate");
        jMenuItemGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGenerate);

        jMenuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_plus.png"))); // NOI18N
        jMenuItemCopy.setText("Copy generated to ClipBoard");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemCopy);

        mClearGenerator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/edit_clear-16.png"))); // NOI18N
        mClearGenerator.setText("Clear Generator");
        mClearGenerator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mClearGeneratorActionPerformed(evt);
            }
        });
        jMenu1.add(mClearGenerator);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Keeper");

        mDbEditMenu.setText("View/Edit");

        mAddRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/add1-green-16.png"))); // NOI18N
        mAddRecord.setText("Add Record");
        mAddRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mAddRecordActionPerformed(evt);
            }
        });
        mDbEditMenu.add(mAddRecord);

        mDeleteRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/list_remove-16.png"))); // NOI18N
        mDeleteRecord.setText("Delete Record");
        mDeleteRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mDeleteRecordActionPerformed(evt);
            }
        });
        mDbEditMenu.add(mDeleteRecord);

        mEditRow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/document_edit.png"))); // NOI18N
        mEditRow.setText("View/Edit Record");
        mEditRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mEditRowActionPerformed(evt);
            }
        });
        mDbEditMenu.add(mEditRow);

        mClearDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clear-db-16.png"))); // NOI18N
        mClearDB.setText("Clear DB");
        mClearDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mClearDBActionPerformed(evt);
            }
        });
        mDbEditMenu.add(mClearDB);

        jMenu2.add(mDbEditMenu);

        mClipCpMenu.setText("Copy");

        mCpPasswToClip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_sign.png"))); // NOI18N
        mCpPasswToClip.setText("Copy Password to ClipBoard");
        mCpPasswToClip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCpPasswToClipActionPerformed(evt);
            }
        });
        mClipCpMenu.add(mCpPasswToClip);

        jMenu2.add(mClipCpMenu);
        jMenu2.add(jSeparator3);

        mExportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/export-16.png"))); // NOI18N
        mExportMenu.setText("Export");

        mExportHtm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/html-16.png"))); // NOI18N
        mExportHtm.setText("Export to HTML");
        mExportHtm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExportHtmActionPerformed(evt);
            }
        });
        mExportMenu.add(mExportHtm);

        mExportXml.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xml-well-16.png"))); // NOI18N
        mExportXml.setText("Export to XML");
        mExportXml.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExportXmlActionPerformed(evt);
            }
        });
        mExportMenu.add(mExportXml);

        mExportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/csv-export-16.png"))); // NOI18N
        mExportCSV.setText("Export to CSV");
        mExportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExportCSVActionPerformed(evt);
            }
        });
        mExportMenu.add(mExportCSV);

        mExportXLS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xls-16.png"))); // NOI18N
        mExportXLS.setText("Export to XLS");
        mExportXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mExportXLSActionPerformed(evt);
            }
        });
        mExportMenu.add(mExportXLS);

        jMenu2.add(mExportMenu);

        mImportMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/import-16.png"))); // NOI18N
        mImportMenu.setText("Import");

        mImportXML.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xml-well-16.png"))); // NOI18N
        mImportXML.setText("Import from XML");
        mImportXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mImportXMLActionPerformed(evt);
            }
        });
        mImportMenu.add(mImportXML);

        mImportCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/csv-import-16.png"))); // NOI18N
        mImportCSV.setText("Import from CSV");
        mImportCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mImportCSVActionPerformed(evt);
            }
        });
        mImportMenu.add(mImportCSV);

        mImportXLS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/xls-16.png"))); // NOI18N
        mImportXLS.setText("Import from XLS");
        mImportXLS.setToolTipText("");
        mImportXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mImportXLSActionPerformed(evt);
            }
        });
        mImportMenu.add(mImportXLS);

        jMenu2.add(mImportMenu);
        jMenu2.add(jSeparator5);

        mGotoURL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/world_connect-16.png"))); // NOI18N
        mGotoURL.setText("Go to URL");
        mGotoURL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mGotoURLActionPerformed(evt);
            }
        });
        jMenu2.add(mGotoURL);

        jMenuBar1.add(jMenu2);

        jMenuHelp.setText("Info");

        mShowClipboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/clipboard_lupa-16.png"))); // NOI18N
        mShowClipboard.setText("Show Clipboard");
        mShowClipboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mShowClipboardActionPerformed(evt);
            }
        });
        jMenuHelp.add(mShowClipboard);

        jMenuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/PKG/img/16x16/help-green-16.png"))); // NOI18N
        jMenuItemAbout.setText("About");
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar1.add(jMenuHelp);

        setJMenuBar(jMenuBar1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    private void bRunGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRunGenerateActionPerformed
        BLogic.generate(checkSpecial.isSelected(),bcomboAlphabet.getSelectedItem().toString(),Integer.parseInt(bcomboNumSymbols.getSelectedItem().toString().split("=")[1]), TFpassw);
}//GEN-LAST:event_bRunGenerateActionPerformed

    private void bCopyGeneratedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCopyGeneratedActionPerformed
        this.CopyCP();
    }//GEN-LAST:event_bCopyGeneratedActionPerformed

    private void jMenuItemGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateActionPerformed
        BLogic.generate(checkSpecial.isSelected(),bcomboAlphabet.getSelectedItem().toString(),Integer.parseInt(bcomboNumSymbols.getSelectedItem().toString().split("=")[1]), TFpassw);
}//GEN-LAST:event_jMenuItemGenerateActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        actions.quitExit(frame);
}//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        //About ab = new About(frame,true);
        //ab.setVisible(true); 
        about();
}//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
        this.CopyCP();
}//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void mpCopyClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpCopyClipboardActionPerformed
        this.CopyCP();
    }//GEN-LAST:event_mpCopyClipboardActionPerformed

    private void mpShowClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpShowClipboardActionPerformed
        actions.ShowClipboard(this);
    }//GEN-LAST:event_mpShowClipboardActionPerformed

    private void mShowClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mShowClipboardActionPerformed
        actions.ShowClipboard(this);
    }//GEN-LAST:event_mShowClipboardActionPerformed

    private void bShowClipboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bShowClipboardActionPerformed
        actions.ShowClipboard(this);
    }//GEN-LAST:event_bShowClipboardActionPerformed

    private void mSkinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mSkinActionPerformed
        actions.changeLF(frame);
    }//GEN-LAST:event_mSkinActionPerformed

    private void bAddRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddRecordActionPerformed
        //RowAdd ab=new RowAdd(frame,true);
        //ab.setVisible(true);
        actions.rowAdd(KeeperTable, frame);
    }//GEN-LAST:event_bAddRecordActionPerformed

    private void bDeleteRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteRecordActionPerformed
        //Actions akt =new Actions();
        actions.DeleteRecord(KeeperTable,frame);
    }//GEN-LAST:event_bDeleteRecordActionPerformed

    private void mAddRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAddRecordActionPerformed
        //RowAdd ab=new RowAdd(frame,true);
        //ab.setVisible(true);
        actions.rowAdd(KeeperTable, frame);
    }//GEN-LAST:event_mAddRecordActionPerformed

    private void mDeleteRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mDeleteRecordActionPerformed
        //Actions akt =new Actions();
        actions.DeleteRecord(KeeperTable,frame);
    }//GEN-LAST:event_mDeleteRecordActionPerformed

    private void mpDeleteRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpDeleteRowActionPerformed
        //Actions akt =new Actions();
        actions.DeleteRecord(KeeperTable,frame);
    }//GEN-LAST:event_mpDeleteRowActionPerformed

    private void bAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddUserActionPerformed
        actions.addUser(frame);
    }//GEN-LAST:event_bAddUserActionPerformed

    private void mAddUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mAddUserActionPerformed
        actions.addUser(frame);
    }//GEN-LAST:event_mAddUserActionPerformed

    private void bEditRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bEditRowActionPerformed
        //RowEdit ab=new RowEdit(frame,true);
        //ab.setVisible(true);
        actions.rowEdit(KeeperTable, frame);
    }//GEN-LAST:event_bEditRowActionPerformed

    private void mEditRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mEditRowActionPerformed
        //RowEdit ab=new RowEdit(frame,true);
        //ab.setVisible(true);
        actions.rowEdit(KeeperTable, frame);
    }//GEN-LAST:event_mEditRowActionPerformed

    private void mpEditRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpEditRowActionPerformed
        //RowEdit ab=new RowEdit(frame,true);
        //ab.setVisible(true);
        actions.rowEdit(KeeperTable, frame);
    }//GEN-LAST:event_mpEditRowActionPerformed

    private void mpClearGeneratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpClearGeneratorActionPerformed
        TFpassw.setText("");
    }//GEN-LAST:event_mpClearGeneratorActionPerformed

    private void mClearGeneratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mClearGeneratorActionPerformed
        TFpassw.setText("");
    }//GEN-LAST:event_mClearGeneratorActionPerformed

    private void bGoURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bGoURLActionPerformed
        actions.gotoURL(KeeperTable,frame);
    }//GEN-LAST:event_bGoURLActionPerformed

    private void mGotoURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mGotoURLActionPerformed
        actions.gotoURL(KeeperTable,frame);
    }//GEN-LAST:event_mGotoURLActionPerformed

    private void mpGotoURLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpGotoURLActionPerformed
        actions.gotoURL(KeeperTable,frame);
    }//GEN-LAST:event_mpGotoURLActionPerformed

    private void mpGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpGenerateActionPerformed
        BLogic.generate(checkSpecial.isSelected(), bcomboAlphabet.getSelectedItem().toString(), Integer.parseInt(bcomboNumSymbols.getSelectedItem().toString().split("=")[1]), TFpassw);
    }//GEN-LAST:event_mpGenerateActionPerformed

    private void bCpPasswToClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bCpPasswToClipActionPerformed
        actions.CopyPasswToClipboard(KeeperTable,frame);
    }//GEN-LAST:event_bCpPasswToClipActionPerformed

    private void mCpPasswToClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCpPasswToClipActionPerformed
        actions.CopyPasswToClipboard(KeeperTable,frame);
    }//GEN-LAST:event_mCpPasswToClipActionPerformed

    private void mpCpPasswToClipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpCpPasswToClipActionPerformed
        actions.CopyPasswToClipboard(KeeperTable,frame);
    }//GEN-LAST:event_mpCpPasswToClipActionPerformed

    private void mExportHtmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExportHtmActionPerformed
        actions.exportToHTML(frame);
    }//GEN-LAST:event_mExportHtmActionPerformed

    private void mpExportHtmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpExportHtmActionPerformed
        actions.exportToHTML(frame);
    }//GEN-LAST:event_mpExportHtmActionPerformed

    private void mExportXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExportXmlActionPerformed
        actions.exportToXML(frame);
    }//GEN-LAST:event_mExportXmlActionPerformed

    private void mpExportXmlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpExportXmlActionPerformed
        actions.exportToXML(frame);
    }//GEN-LAST:event_mpExportXmlActionPerformed

    private void mImportXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mImportXMLActionPerformed
        actions.importFromXML(KeeperTable,frame);
    }//GEN-LAST:event_mImportXMLActionPerformed

    private void mpImportXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpImportXMLActionPerformed
        actions.importFromXML(KeeperTable,frame);
    }//GEN-LAST:event_mpImportXMLActionPerformed

    private void bClearDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bClearDBActionPerformed
        actions.clearDB(KeeperTable,frame);
    }//GEN-LAST:event_bClearDBActionPerformed

    private void mClearDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mClearDBActionPerformed
        actions.clearDB(KeeperTable,frame);
    }//GEN-LAST:event_mClearDBActionPerformed

    private void mpClearDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpClearDBActionPerformed
        actions.clearDB(KeeperTable,frame);
    }//GEN-LAST:event_mpClearDBActionPerformed

    private void mpAddRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpAddRecordActionPerformed
        //RowAdd ab=new RowAdd(frame,true);
        //ab.setVisible(true);
        actions.rowAdd(KeeperTable, frame);
    }//GEN-LAST:event_mpAddRecordActionPerformed

    private void mpShowClipboard1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpShowClipboard1ActionPerformed
        actions.ShowClipboard(frame);
    }//GEN-LAST:event_mpShowClipboard1ActionPerformed

    private void mImportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mImportCSVActionPerformed
        actions.importFromCSV(KeeperTable,frame);
    }//GEN-LAST:event_mImportCSVActionPerformed

    private void mpImportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpImportCSVActionPerformed
        actions.importFromCSV(KeeperTable,frame);
    }//GEN-LAST:event_mpImportCSVActionPerformed

    private void mExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExportCSVActionPerformed
        actions.exportToCSV(frame);
    }//GEN-LAST:event_mExportCSVActionPerformed

    private void mpExportCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpExportCSVActionPerformed
        actions.exportToCSV(frame);
    }//GEN-LAST:event_mpExportCSVActionPerformed

    private void bExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bExportActionPerformed
        switch (bExportCombo.getSelectedItem().toString()) {
            case "HTML": actions.exportToHTML(frame); break;
            case "XML":  actions.exportToXML(frame);  break;
            case "CSV":  actions.exportToCSV(frame);  break;
            case "XLS":  actions.exportToXLS(frame);  break;            
        }
    }//GEN-LAST:event_bExportActionPerformed

    private void bImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bImportActionPerformed
        switch (bImportCombo.getSelectedItem().toString()) {
            case "XML":  actions.importFromXML(KeeperTable,frame);  break;
            case "CSV":  actions.importFromCSV(KeeperTable,frame);  break;
            case "XLS":  actions.importFromXLS(KeeperTable,frame);  break;
        }
    }//GEN-LAST:event_bImportActionPerformed

    private void mImportXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mImportXLSActionPerformed
        actions.importFromXLS(KeeperTable,frame);
    }//GEN-LAST:event_mImportXLSActionPerformed

    private void mExportXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mExportXLSActionPerformed
        actions.exportToXLS(frame);
    }//GEN-LAST:event_mExportXLSActionPerformed

    private void mpExportXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpExportXLSActionPerformed
        actions.exportToXLS(frame);
    }//GEN-LAST:event_mpExportXLSActionPerformed

    private void mpImportXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mpImportXLSActionPerformed
        actions.importFromXLS(KeeperTable,frame);
    }//GEN-LAST:event_mpImportXLSActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        actions.searchRefreshJTable(KeeperTable,searchTF,searchCombo);
    }//GEN-LAST:event_searchButtonActionPerformed

    private void searchShowAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchShowAllActionPerformed
        actions.RefreshJTable(KeeperTable);
    }//GEN-LAST:event_searchShowAllActionPerformed

    private void searchTFKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchTFKeyPressed
        if (evt.getKeyCode()==KeyEvent.VK_ENTER)
            actions.searchRefreshJTable(KeeperTable, searchTF, searchCombo);
    }//GEN-LAST:event_searchTFKeyPressed

    private void bSkinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bSkinActionPerformed
        actions.changeLF(frame);
    }//GEN-LAST:event_bSkinActionPerformed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        actions.quitExit(frame);
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAboutActionPerformed
        about();
    }//GEN-LAST:event_btnAboutActionPerformed

    private void btnClearGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearGenerateActionPerformed
        TFpassw.setText("");
    }//GEN-LAST:event_btnClearGenerateActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //AppCfgJDOM.Load();
                //AppCfgXerces.Load();
                //ApplicationContext 
                // Shutdown Spring container gracefully in non-web applications !!
                AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class); 
                //ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
                ctx.registerShutdownHook();
                // app runs here...
                // main method exits, hook is called prior to the app shutting down...
                // define @PreDestroy methods for your beans !!! - it is called before close App !!!                
                frame = ctx.getBean(PKGgui.class);                
                //frame=new PKGgui();
                frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
                //JFrame.setDefaultLookAndFeelDecorated(true);
                //JDialog.setDefaultLookAndFeelDecorated(true); 
                //actions.InstallLF();
                frame.setLF(frame);                 
                //frame.setSize(790,484);
                //frame.setSize(new Dimension(650,500));
                frame.setSize(frameDimension);
                frame.setMinimumSize(frameDimension);                 
                frame.setVisible(true);
                //actions.RefreshJTable();
                System.out.println(frame.getSize());
                //if (actions.login.equals("default")) actions.addUser();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar GeneratorToolBar;
    public static javax.swing.JTable KeeperTable;
    private javax.swing.JToolBar KeeperToolBar;
    public static javax.swing.JTextField TFpassw;
    private javax.swing.JButton bAddRecord;
    private javax.swing.JButton bAddUser;
    private javax.swing.JButton bClearDB;
    private javax.swing.JButton bCopyGenerated;
    private javax.swing.JButton bCpPasswToClip;
    private javax.swing.JButton bDeleteRecord;
    private javax.swing.JButton bEditRow;
    private javax.swing.JButton bExport;
    private javax.swing.JComboBox<String> bExportCombo;
    private javax.swing.JButton bGoURL;
    private javax.swing.JButton bImport;
    private javax.swing.JComboBox<String> bImportCombo;
    private javax.swing.JButton bRunGenerate;
    private javax.swing.JButton bShowClipboard;
    private javax.swing.JButton bSkin;
    public static javax.swing.JComboBox bcomboAlphabet;
    public static javax.swing.JComboBox bcomboNumSymbols;
    private javax.swing.JButton btnAbout;
    private javax.swing.JButton btnClearGenerate;
    private javax.swing.JButton btnExit;
    public static javax.swing.JCheckBox checkSpecial;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGenerate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator17;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JToolBar jToolBar12;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar9;
    private javax.swing.JMenuItem mAddRecord;
    private javax.swing.JMenuItem mAddUser;
    private javax.swing.JMenuItem mClearDB;
    private javax.swing.JMenuItem mClearGenerator;
    private javax.swing.JMenu mClipCpMenu;
    private javax.swing.JMenuItem mCpPasswToClip;
    private javax.swing.JMenu mDbEditMenu;
    private javax.swing.JMenuItem mDeleteRecord;
    private javax.swing.JMenuItem mEditRow;
    private javax.swing.JMenuItem mExportCSV;
    private javax.swing.JMenuItem mExportHtm;
    private javax.swing.JMenu mExportMenu;
    private javax.swing.JMenuItem mExportXLS;
    private javax.swing.JMenuItem mExportXml;
    private javax.swing.JMenuItem mGotoURL;
    private javax.swing.JMenuItem mImportCSV;
    private javax.swing.JMenu mImportMenu;
    private javax.swing.JMenuItem mImportXLS;
    private javax.swing.JMenuItem mImportXML;
    private javax.swing.JPopupMenu mPopupGenerator;
    private javax.swing.JPopupMenu mPopupKeeper;
    private javax.swing.JMenuItem mShowClipboard;
    private javax.swing.JMenuItem mSkin;
    private javax.swing.JMenuItem mpAddRecord;
    private javax.swing.JMenuItem mpClearDB;
    private javax.swing.JMenuItem mpClearGenerator;
    private javax.swing.JMenu mpClipCpMenu;
    private javax.swing.JMenuItem mpCopyClipboard;
    private javax.swing.JMenuItem mpCpPasswToClip;
    private javax.swing.JMenu mpDbEditMenu;
    private javax.swing.JMenuItem mpDeleteRow;
    private javax.swing.JMenuItem mpEditRow;
    private javax.swing.JMenuItem mpExportCSV;
    private javax.swing.JMenuItem mpExportHtm;
    private javax.swing.JMenu mpExportMenu;
    private javax.swing.JMenuItem mpExportXLS;
    private javax.swing.JMenuItem mpExportXml;
    private javax.swing.JMenuItem mpGenerate;
    private javax.swing.JMenuItem mpGotoURL;
    private javax.swing.JMenuItem mpImportCSV;
    private javax.swing.JMenu mpImportMenu;
    private javax.swing.JMenuItem mpImportXLS;
    private javax.swing.JMenuItem mpImportXML;
    private javax.swing.JMenuItem mpShowClipboard;
    private javax.swing.JMenuItem mpShowClipboard1;
    private javax.swing.JButton searchButton;
    public static javax.swing.JComboBox<String> searchCombo;
    private javax.swing.JLabel searchLabel1;
    private javax.swing.JButton searchShowAll;
    private javax.swing.JToolBar searchTBar;
    public static javax.swing.JTextField searchTF;
    private javax.swing.JLabel searhLabel2;
    // End of variables declaration//GEN-END:variables
}
