/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import jsf.bean.gui.ClassFinderIf;
import jsf.bean.gui.EntityBeanBase;
import jsf.bean.gui.component.table.BeanTable;
import jsf.bean.gui.component.table.BeanTableDaoIf;
import jsf.bean.gui.component.table.api.BeanTableApi;
import jsf.bean.gui.component.table.api.BeanTableApiConfig;
import jsf.bean.gui.component.table.api.BeanTableApiConfigProviderIf;
import jsf.bean.gui.component.table.api.BeanTableApiTemplate;
import jsf.bean.gui.component.table.column.BeanTableQueryColumn;
import jsf.bean.gui.component.table.export.BeanTableExportTemplateProvider;

/**
 * Holds configurations for the single table API
 * @author valdo
 */
public abstract class BeanTableApiManager {
    
    /**
     * Table configuration provider
     */
    private final BeanTableApiConfigProviderIf configProvider;
    
    /**
     * Table columns
     */
    private Map<String, Collection<BeanTableQueryColumn>> cacheColumns;
    
    /**
     * Table templates
     */
    private Map<String, Collection<BeanTableApiTemplate>> cacheTemplates;

    public abstract ClassFinderIf getClassFinder();
    public abstract BeanTableExportTemplateProvider getTemplateProvider(BeanTable table);
    
    public BeanTableApiManager(BeanTableApiConfigProviderIf configProvider) {
        this.configProvider = configProvider;
        this.cacheColumns = new HashMap<String, Collection<BeanTableQueryColumn>>();
        this.cacheTemplates = new HashMap<String, Collection<BeanTableApiTemplate>>();
    }
    
    public Collection<BeanTableQueryColumn> getColumns(String id, BeanTableDaoIf dao) throws Exception {
        if (!cacheColumns.containsKey(id)) {
            cacheColumns.put(id, getApi(id, dao).getColumns());
        }
        return cacheColumns.get(id);
    }
    
    public Collection<BeanTableApiTemplate> getTemplates(String id, BeanTableDaoIf dao) throws Exception {
        if (!cacheTemplates.containsKey(id)) {
            cacheTemplates.put(id, getApi(id, dao).getTemplates());
        }
        return cacheTemplates.get(id);
    }

    public boolean isTemplateExists(String id, String name, BeanTableDaoIf dao) throws Exception {
        for (BeanTableApiTemplate t: getTemplates(id, dao)) {
            if (t.getId().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public BeanTableApiTemplate getTemplate(String id, String name, BeanTableDaoIf dao) throws Exception {
        for (BeanTableApiTemplate t: getTemplates(id, dao)) {
            if (t.getId().equals(name)) {
                return t;
            }
        }
        return null;
    }
    
    public void refreshTemplates(Class<? extends EntityBeanBase> rowClass) {
        BeanTableApiConfig config = configProvider.getConfig(rowClass);
        if (config != null) {
            this.cacheTemplates.remove(config.getId());
        }
    }
    
    public BeanTableApi getApi(String id, BeanTableDaoIf tableDao) throws Exception {
        BeanTableApiConfig config = configProvider.getConfig(id);
        if (config != null) {
            return new BeanTableApi(config, this, tableDao);
        }
        throw new Exception(String.format("Bean table API with ID = [%s] not found?!", id));
    }

    public BeanTableApiConfigProviderIf getConfigProvider() {
        return configProvider;
    }
    
}
