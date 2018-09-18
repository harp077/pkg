package PKG.addons;
import com.romanenco.configloader.ConfigLoader;
import java.io.*;

public class UserCfgXerces {
    
  // ПОДДЕРЖИВАЕТ ВЛОЖЕННЫЕ ТЕГИ
  public static FileWriter writeCFG = null;

  public static void Load () {
    ConfigLoader cfg=new ConfigLoader("org.apache.xerces.parsers.SAXParser");
    try {
        cfg.LoadFromFile("users/"+Actions.addlogin+"/"+Actions.addlogin+".xml");
        Actions.addpassw = cfg.getTagValue("pkg."+Actions.addlogin+".password");
        Actions.addhash  = cfg.getTagValue("pkg."+Actions.addlogin+".hash");
        Actions.addDbLastChange  = cfg.getTagValue("pkg."+Actions.addlogin+".change");        
    } 
    catch (NullPointerException e)  {  System.out.println(e); }
    catch (RuntimeException e)      {  System.out.println(e); }
  }
  
 public static void Save () {
    try {
            File cfgdir = new File("users/"+Actions.addlogin);
            cfgdir.mkdir();
            //cfgfile.mkdirs();            
            File cfgfile = new File("users/"+Actions.addlogin+"/"+Actions.addlogin+".xml");
            writeCFG = new FileWriter(cfgfile);
            writeCFG.append("<pkg>\n");            
            writeCFG.append("  <"+Actions.addlogin+">\n");
            writeCFG.append("     <password>"+Actions.addpassw+"</password>\n");
            writeCFG.append("     <hash>"+Actions.addhash+"</hash>\n");
            writeCFG.append("     <change>"+Actions.addDbLastChange+"</change>\n");            
            writeCFG.append("  </"+Actions.addlogin+">\n");
            writeCFG.append("</pkg>\n");            
        } 
    catch (IOException ex) {  ex.printStackTrace();  }
    finally 
            {
                if(writeCFG != null) 
                {
                 try { writeCFG.close(); } 
                 catch (IOException e) { e.printStackTrace(); }
                }
            }            
    }
}
