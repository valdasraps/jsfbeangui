package jsf.bean.gui.component.table.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class PeriodConverter implements Converter {

    private static final long[] seconds = new long[]{24 * 60 * 60, 60 * 60, 60, 1};
    private static Pattern periodPattern = Pattern.compile("^([0-9]+:){0,3}[0-9]+$");

    public void ConversionError(String title) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, title, title);
        throw new ConverterException(message);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Long obj = null;
        if (value != null && value.trim().length() > 0) {
            Matcher m = periodPattern.matcher(value);
            if (!m.matches()) {
                ConversionError("Error converting filter string to Period filter. Please fix the filter!");
            }
            long v = 0;
            String[] tokens = value.split(":");
            for (int i = 0; i < tokens.length; i++) {
                long t = Long.parseLong(tokens[i]);
                v += t * seconds[seconds.length - tokens.length + i];
            }
            obj = Long.valueOf(v);
        }
        return obj;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return "";
        }
        
        if (! (value instanceof Long)) {
            return "";
        }

        long secs = ((Number) value).longValue();
        String s = "";
        boolean wasData = false;

        for (long dsec : seconds) {
            long d = secs / dsec;
            secs -= d * dsec;
            s += (wasData ? ":" : "") + String.format("%02d", d);
            wasData = true;
        }

        return s.trim();
    }
}