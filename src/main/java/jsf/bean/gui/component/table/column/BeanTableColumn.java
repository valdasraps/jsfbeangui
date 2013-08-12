package jsf.bean.gui.component.table.column;

import javax.faces.event.ActionEvent;
import jsf.bean.gui.EntityBeanBase;
import jsf.bean.gui.component.table.BeanTable;
import jsf.bean.gui.metadata.PropertyMd;
import lombok.extern.log4j.Log4j;
import org.apache.commons.beanutils.PropertyUtils;

@Log4j
public abstract class BeanTableColumn extends BeanTableColumnBase {

    protected final BeanTable table;

    public BeanTableColumn(BeanTable table, PropertyMd propertyMd) {
        super(propertyMd);
        this.table = table;
    }

    /**
     *
     * Specific methods
     *
     */

    public void clearFilterListener(ActionEvent ev) {
        clearFilter();
        table.refresh();
    }

    public Object getCellValue() {
        EntityBeanBase o = table.getCurrentRow();
        if (o != null) {
            try {
                return PropertyUtils.getSimpleProperty(o, getName());
            } catch (Exception ex) {
                log.error("Error while retrieving bean value", ex);
            }
        }
        return null;
    }

    public Integer getWidth() {
        return table.getProperties().getColumnWidth(name);
    }

    public void setWidth(Integer width) {
        table.getProperties().setColumnWidth(name, width);
    }

    public BeanTable getTable() {
        return table;
    }
 
}
