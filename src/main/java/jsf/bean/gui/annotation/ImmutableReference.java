/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsf.bean.gui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on entity properties which point to other entities to tell the GUI editor
 * that it should not let the user to edit the referenced entity.
 * e.g. you may want to let a user to choose a component, but you don't want to let him edit it,
 * then you use this annotation on the property which is referencing a component.
 * @author Evka
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ImmutableReference {
}
