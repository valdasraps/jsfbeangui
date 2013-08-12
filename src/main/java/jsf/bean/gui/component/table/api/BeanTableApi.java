/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component.table.api;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import jsf.bean.gui.ClassFinderIf;
import jsf.bean.gui.component.BeanTableApiManager;
import jsf.bean.gui.component.BeanTableManager;
import jsf.bean.gui.component.table.BeanTable;
import jsf.bean.gui.component.table.BeanTableDaoIf;
import jsf.bean.gui.component.table.column.BeanTableColumn;
import jsf.bean.gui.component.table.column.BeanTableColumnSortable;
import jsf.bean.gui.component.table.column.BeanTableQueryColumn;
import jsf.bean.gui.component.table.export.BeanTableExportManager.ExportResult;
import jsf.bean.gui.component.table.export.BeanTableExportResource;
import jsf.bean.gui.component.table.export.BeanTableExportTemplateProvider;
import org.json.JSONObject;

/**
 *
 * @author valdo
 */
public class BeanTableApi {
    
    private final BeanTableApiConfig config;
    private final BeanTableManager manager;
    private final BeanTableApiManager apimanager;
    private final BeanTableDaoIf tableDao;
    private final Map<String, BeanTableExportResource> exportResources = new HashMap<String, BeanTableExportResource>();
    
    public BeanTableApi(BeanTableApiConfig config, 
                        BeanTableApiManager apimanager_,
                        BeanTableDaoIf tableDao_) throws Exception {
        this.config = config;
        this.apimanager = apimanager_;
        this.tableDao = tableDao_;
        this.manager = new BeanTableManager(config.getId(), this.config.getRowClass(), false) {

            @Override
            public BeanTableDaoIf getBeanTableDao() {
                return tableDao;
            }

            @Override
            public ClassFinderIf getClassFinder() {
                return apimanager.getClassFinder();
            }

            @Override
            public BeanTableExportTemplateProvider getTemplateProvider() {
                return apimanager.getTemplateProvider(this.getTable());
            }
            
        };
        
        for (BeanTableApiConfig.BeanTableApiConfigPropertyFilter pf: this.config.getPropertyFilters()) {
            this.manager.addPropertyFilter(
                    pf.getName(), 
                    pf.getOperation(), 
                    pf.getValue());
        }
        
        List<BeanTableExportResource> erl = new LinkedList<BeanTableExportResource>();
        erl.addAll(this.manager.getTable().getExportTemplateList().getDefaultExportResources());
        erl.addAll(this.manager.getTable().getExportTemplateList().getPublicExportResources());
        
        for (BeanTableExportResource r: erl) {
            String oid = this.config.getTemplateId(r.getTemplate().getTitle());
            if (oid != null) {
                int i = 1;
                String id = oid;
                while (exportResources.containsKey(oid)) {
                    oid = id.concat(String.valueOf(i++));
                }
                exportResources.put(oid, r);
            }
        }
        
    }

    public Collection<BeanTableQueryColumn> getColumns() {
        return manager.getTable().getQueryColumns();
    }

    public SortedSet<BeanTableApiTemplate> getTemplates() {
        SortedSet<BeanTableApiTemplate> templates = new TreeSet<BeanTableApiTemplate>();
        for (String rid: exportResources.keySet()) {
            templates.add(
               new BeanTableApiTemplate(rid, 
                    exportResources.get(rid).getTemplate().getTitle(), 
                    exportResources.get(rid).getTemplate().getMimeType()));
        }
        return templates;
    }

    public void setSelectedColumns(final String [] columns) throws IllegalArgumentException {
        BeanTable btable = this.manager.getTable();
        btable.getSelectedColumns().getTarget().clear();
        for (String colName: columns) {
            BeanTableColumn column = btable.getColumn(colName);
            if (column == null) {
                throw new IllegalArgumentException(String.format("Column [{%s}] not found. Fix column list.", colName));
            }
            btable.getSelectedColumns().getTarget().add(column);
        }
    }
    
    public void setSortingColumns(final String [] order) throws IllegalArgumentException {
        BeanTable btable = this.manager.getTable();
        btable.getSortingColumns().getTarget().clear();
        for (String orderItem: order) {
            String[] orderItems = orderItem.split(" ");

            if (orderItems.length != 2) {
                throw new IllegalArgumentException(String.format("Wrong order list format at [%s].", orderItem));
            }

            BeanTableColumn column = btable.getColumn(orderItems[0]);
            if (column == null) {
                throw new IllegalArgumentException(String.format("Column [{%s}] not found. Fix order list.", orderItems[0]));
            }

            if (!(column instanceof BeanTableColumnSortable)) {
                throw new IllegalArgumentException(String.format("Column [{%s}] not sortable.", orderItems[0]));
            }

            BeanTableColumnSortable scolumn = (BeanTableColumnSortable) column;

            if (!("asc".equals(orderItems[1]) || "desc".equals(orderItems[1]))) {
                throw new IllegalArgumentException(String.format("Column [{%s}] ordering [%s] is incorrect.", orderItems[0], orderItems[1]));
            }

            scolumn.setAscending("asc".equals(orderItems[1]));
            btable.getSortingColumns().getTarget().add(scolumn);

        }
    }
    
    public ExportResult export(String templateId, JSONObject apiFilter) throws Exception {
        
        if (!exportResources.containsKey(templateId)) {
            throw new Exception(String.format("Template [%s] not found?!", templateId));
        }
        
        if (apiFilter != null) {
            this.manager.getTablePack().setSerializedFilter(apiFilter);
        }
        
        return exportResources.get(templateId).export();
        
    }
    
    public Long count(JSONObject apiFilter) throws Exception {
        
        if (apiFilter != null) {
            this.manager.getTablePack().setSerializedFilter(apiFilter);
        }
        
        BeanTableDaoIf dao = this.manager.getBeanTableDao();
        return dao.getDataCount(this.manager.getTable());
        
    }
    
}
