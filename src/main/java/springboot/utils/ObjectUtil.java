package springboot.utils;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class ObjectUtil {
    /**
     * Convert from string to target type
     *
     * @param targetType target type
     * @param s          the string
     * @return the target
     */
    public static Object convert(Class<?> targetType, String s) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(s);
        return editor.getValue();
    }
}
