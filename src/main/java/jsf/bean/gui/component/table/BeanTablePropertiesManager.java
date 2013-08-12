/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import jsf.bean.gui.JsfBeanBase;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author valdo
 */
@Log4j
public class BeanTablePropertiesManager {
    
    private static final String PROPERTIES_BASE_PATH = "resources/tables/";
    private static final String PROPERTIES_EXTENSION = ".properties";
    private static final String COOKIE_NAME_PATTERN = "table.%s.properties";
    private static Map<String, Properties> cache = new HashMap<String, Properties>();

    public static Properties getProperties(String tableId) {

        // Loading personal properties
        
        {
            // Get cookie for the table
            String cookieName = String.format(COOKIE_NAME_PATTERN, tableId);
            String myId = (String) JsfBeanBase.getCookie(cookieName);

            if (myId != null) {
                File f = JsfBeanBase.getRealFile(PROPERTIES_BASE_PATH.concat(File.separator).concat(myId).concat(PROPERTIES_EXTENSION));
                try {
                    FileInputStream fin = new FileInputStream(f);
                    Properties p = new Properties();
                    p.load(fin);
                    fin.close();
                    return p;
                } catch (Exception ex) {
                    //logger.warn("Table [id = {0}] personal properties not found at [{1}]. Reseting and loading defaults...", tableId, f.getAbsolutePath());
                    JsfBeanBase.setCookie(cookieName, "", 0);
                }
            }
        }

        // Loading default properties
        
        Properties p = new Properties();
        if (!getFromCache(tableId, p)) {
            
            File f = JsfBeanBase.getRealFile(PROPERTIES_BASE_PATH.concat(File.separator).concat(tableId).concat(PROPERTIES_EXTENSION));
            try {
                
                FileInputStream fin = new FileInputStream(f);
                p.load(fin);
                fin.close();
                
                addToCache(tableId, p);
                
            } catch (Exception ex) {
                log.error(ex);
            }
        }

        return p;

    }

    public static void saveProperties(String tableId, BeanTableProperties properties, boolean global) throws IOException {

        String cookieName = String.format(COOKIE_NAME_PATTERN, tableId);

        // Writting a file
        Properties p = properties.getProperties();
        String filename = tableId;
        if (!global) {
            filename = (String) JsfBeanBase.getCookie(cookieName);
            if (filename == null) {
                File f = File.createTempFile(tableId.concat("."), "", JsfBeanBase.getRealFile(PROPERTIES_BASE_PATH));
                filename = f.getName();
                f.delete();
            }
        }
        FileOutputStream fout = new FileOutputStream(JsfBeanBase.getRealFile(PROPERTIES_BASE_PATH.concat(File.separator).concat(filename).concat(PROPERTIES_EXTENSION)));
        p.store(fout, filename);
        fout.close();
        
        // Saving filename in cookie
        if (!global) {
            JsfBeanBase.setCookie(cookieName, filename);
        } else {
            cache.remove(tableId);
        }

    }

    private static boolean getFromCache(final String tableId, Properties p) {
        if (cache.containsKey(tableId)) {
            Properties cached = cache.get(tableId);
            for (String k: cached.stringPropertyNames()) {
                p.put(k, cached.getProperty(k));
            }
            return true;
        }
        return false;
    }
    
    private static void addToCache(final String tableId, final Properties p) {
        Properties cached = new Properties();
        for (String k: p.stringPropertyNames()) {
            cached.put(k, p.getProperty(k));
        }
        cache.put(tableId, cached);
    }
    
}
