package jsf.bean.gui.component.table.converter;

public class FilterIntegerConverter extends FilterNumericConverter {

    @Override
    public String valueToString(Object value) {
        Integer o = (Integer) value;
        return o.toString();
    }

    @Override
    public Object valueToObject(String value) {
        try {
            return (Object) Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ConversionError("Error converting filter string to Integer filter. Please fix the filter!");
        }
        return null;
    }

}
