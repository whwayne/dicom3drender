/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.ui;

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolGenerator;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import java.awt.GridBagLayout;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author jleon
 */
public class nkToolSlider extends JSlider implements nkToolGenerator, ChangeListener{
     private Vector prv_nkToolEvent_listeners;
     private int id;

     public nkToolSlider(int a,int b,int c,int d,int eventIdentifier){
         super(a,b,c,d);
         this.id=eventIdentifier;
         this.addChangeListener(this);
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

    public void stateChanged(ChangeEvent arg0) {
        //System.out.println("State Changed");
        if( null == prv_nkToolEvent_listeners) return;
            nkToolEvent e = new nkToolEvent(this, id, "SliderEvent");
        for( int i= 0; i < prv_nkToolEvent_listeners.size(); ++i)
            ((nkToolListener)prv_nkToolEvent_listeners.elementAt(i)).nkToolInvoke(e);
    }

}
