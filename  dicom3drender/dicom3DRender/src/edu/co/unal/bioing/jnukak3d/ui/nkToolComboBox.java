/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.ui;

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolGenerator;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComboBox;

/**
 *
 * @author jleon
 */
public class nkToolComboBox extends JComboBox implements nkToolGenerator, ActionListener{
     private Vector prv_nkToolEvent_listeners;
     private int id;

     public static enum windowLevelpresets{//name (width,center)
         Default(0,0),
         Abdomen (350,50),
         Bone (2500,500),
         Cerebrum (120,40),
         Liver (150,50),
         Lung (1500,500),
         Mediastinum (300,50),
         Pelvis (400,40),
         PosteriorFossa (250,80);

         private final int width;  
         private final int center; 

         windowLevelpresets(int  _width, int _center) {
             this.width = _width;
             this.center = _center;
         }

         public int width(){ return width; }
         public int center(){ return center; }
    }

     public nkToolComboBox(String[] options,int eventIdentifier){
         super(options);
         this.id=eventIdentifier;
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

    public static final String[] getPresetNames(){
        String[] presets=new String[windowLevelpresets.values().length];
        for(int i=0;i<windowLevelpresets.values().length;i++)
            presets[i]=windowLevelpresets.values()[i].name();
        return presets;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if( null == prv_nkToolEvent_listeners) return;
        nkToolEvent nTE = new nkToolEvent(this, id, "SliderEvent");
        for( int i= 0; i < prv_nkToolEvent_listeners.size(); ++i)
            ((nkToolListener)prv_nkToolEvent_listeners.elementAt(i)).nkToolInvoke(nTE);

    }
}
