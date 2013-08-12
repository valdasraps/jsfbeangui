/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.bean.gui.component.table;

import org.hibernate.Session;

/**
 *
 * @author valdo
 */
public interface HibernateSessionProvider {
    
    Session getSession();
    
}
