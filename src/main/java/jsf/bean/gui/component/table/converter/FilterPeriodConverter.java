package jsf.bean.gui.component.table.converter;

import java.util.Iterator;
import jsf.bean.gui.component.table.BeanTableFilterItem;
import jsf.bean.gui.component.table.BeanTableFilter;

public class FilterPeriodConverter extends FilterConverter {

    private PeriodConverter converter = new PeriodConverter();

    @Override
    public String valueToString(Object value) {
        return converter.getAsString(null, null, value);
    }

    @Override
    public Object valueToObject(String value) {
        return converter.getAsObject(null, null, value);
    }

    @Override
    public void checkFilter(BeanTableFilter filter) {
        for (Iterator<BeanTableFilterItem> itemItr = filter.getItems().iterator(); itemItr.hasNext();) {
            BeanTableFilterItem item = itemItr.next();
            BeanTableFilter.Operation op = item.getOperation();
            if (op == BeanTableFilter.Operation.LIKE || 
                op == BeanTableFilter.Operation.NOTLIKE) {
                ConversionError("Period filter does not support LIKE operation. Please fix the filter!");
            }
        }
    }
}

