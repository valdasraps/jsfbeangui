/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component.table.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author valdor
 */
@Log4j
public class BeanTableDefaultPrimaryTemplate extends BeanTableDefaultExportTemplate implements BeanTableExportTemplatePrimary {

    private static final String SECTION_SEP = "${}SEPARATOR${}";

    public BeanTableDefaultPrimaryTemplate(String key, ResourceBundle bundle) throws IOException {
        super(key, bundle);
    }

    public  Map<TemplatePosition, String> getTemplates() {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream in = this.getClass().getResourceAsStream(String.format(TEMPLATE, key));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            Map<TemplatePosition, String> templates = new EnumMap<TemplatePosition, String>(TemplatePosition.class);
            int separatorNumber = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.equals(SECTION_SEP)) {
                    templates.put(TemplatePosition.values()[separatorNumber], sb.toString());
                    separatorNumber++;
                    sb.setLength(0);
                } else {
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
            }
            templates.put(TemplatePosition.values()[separatorNumber], sb.toString());
            return templates;
        } catch (IOException ex) {
            log.error(ex);
            return null;
        }
    }

    public boolean isPrimary() {
        return true;
    }

}
