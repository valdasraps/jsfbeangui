/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component.table.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;
import lombok.extern.log4j.Log4j;

/**
 *
 * @author valdor
 */
@Log4j
public class BeanTableDefaultSecondaryTemplate extends BeanTableDefaultExportTemplate implements BeanTableExportTemplateSecondary {

    private static final String KEY_TYPE_SUFFIX = ".type";
    
    private final BeanTableExportTemplate previousTemplate;
    private final TemplateType templateType;

    public BeanTableDefaultSecondaryTemplate(String key, BeanTableExportTemplate previousTemplate, ResourceBundle bundle) throws IOException {
        super(key, bundle);
        this.previousTemplate = previousTemplate;
        this.templateType = TemplateType.valueOf(bundle.getString(key.concat(KEY_TYPE_SUFFIX)));
    }

    public BeanTableExportTemplate getPreviousTemplate() {
        return previousTemplate;
    }
    
    public TemplateType getTemplateType() {
        return templateType;
    }

    public  String getTemplate() {
        try {
            StringBuilder sb = new StringBuilder();
            InputStream in = this.getClass().getResourceAsStream(String.format(TEMPLATE, key));
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }
            return sb.toString();
        } catch (IOException ex) {
            log.error(ex);
            return null;
        }
    }

    public boolean isPrimary() {
        return false;
    }

}
