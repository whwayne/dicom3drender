/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.ui;

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;

/**
 *
 * @author jleon
 */
public class nkMenuItem extends JMenuItem implements ActionListener{
    private Vector prv_nkToolEvent_listeners;
    private int id;

    public nkMenuItem(String s, int identifier){
        super(s);
        this.id=identifier;
        this.addActionListener(this);
    }

     final synchronized public void addnkToolListener (nkToolListener pl) {
        if( null == prv_nkToolEvent_listeners)
            prv_nkToolEvent_listeners = new Vector();
        if( null == pl || prv_nkToolEvent_listeners.contains( pl))
            return;
        prv_nkToolEvent_listeners.addElement( pl);
    }

    final synchronized public void removenkToolListener(nkToolListener pl) {
        if( null != prv_nkToolEvent_listeners && null != pl)
	    prv_nkToolEvent_listeners.removeElement( pl);

    }

     public void actionPerformed(ActionEvent e) {
        if( null == prv_nkToolEvent_listeners) return;
        nkToolEvent nTE = new nkToolEvent(this, id, "nkMenuEvent");
        for( int i= 0; i < prv_nkToolEvent_listeners.size(); ++i)
            ((nkToolListener)prv_nkToolEvent_listeners.elementAt(i)).nkToolInvoke(nTE);

    }
}
