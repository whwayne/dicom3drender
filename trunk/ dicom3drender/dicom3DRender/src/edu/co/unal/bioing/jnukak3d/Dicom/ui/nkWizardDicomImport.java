/*
  Copyright (C) 2009, Bioingenium Research Group
  http://www.bioingenium.unal.edu.co
  Author: Alexander Pinzon Fernandez

  JNukak3D is free software; you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free
  Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  JNukak3D is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
  License for more details.

  You should have received a copy of the GNU General Public License
  along with JNukak3D; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA,
  or see http://www.gnu.org/copyleft/gpl.html
*/

package edu.co.unal.bioing.jnukak3d.Dicom.ui;

import edu.co.unal.bioing.jnukak3d.Dicom.nkDicomNodeTree;
import edu.co.unal.bioing.jnukak3d.event.nkEvent;
import edu.co.unal.bioing.jnukak3d.event.nkEventListener;
import edu.co.unal.bioing.jnukak3d.event.nkEventGenerator;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import edu.co.unal.bioing.jnukak3d.Dicom.io.nkDicomImport;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import jwf.Wizard;
import jwf.WizardListener;
import java.util.Vector;
import javax.swing.JFrame;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkWizardDicomImport extends JDialog implements WizardListener, nkEventGenerator{
    protected static final boolean DEBUG = nkDebug.DEBUG;
    
    public nkWizardDicomImport(JFrame parent) {
        super(parent);
        Wizard nkw = new Wizard();
        nkw.addWizardListener(this);
        setTitle("jNukak3D: Dicom import");
        this.setContentPane(nkw);

        nkWizardPage1 page1 = new nkWizardPage1();
        nkw.start(page1);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        pack();
        setVisible(true);
    }


    /** Called when the wizard finishes.
     * @param wizard the wizard that finished.
     */
    public void wizardFinished(Wizard wizard) {
        firenkMenuEvent(wizard);
        dispose();
    }


    /** Called when the wizard is cancelled.
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard) {
        dispose();
    }


    /** Called when a new panel has been displayed in the wizard.
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard) {
        if(DEBUG)
            System.out.println("wizard new panel");
    }

    private Vector prv_nkEvent_listeners;

    final synchronized public void addnkEventListener (nkEventListener pl) {
        if( null == prv_nkEvent_listeners)
        prv_nkEvent_listeners = new Vector();
        if( null == pl || prv_nkEvent_listeners.contains( pl))
            return;
        prv_nkEvent_listeners.addElement( pl);
    }

    final synchronized public void removenkEventListener (nkEventListener pl) {
        if( null != prv_nkEvent_listeners && null != pl)
	    prv_nkEvent_listeners.removeElement( pl);
    }

    public void firenkMenuEvent(Wizard wizard){
        if( null == prv_nkEvent_listeners) return;

        nkDicomNodeTree selectedNode;
        selectedNode = ((nkDicomNodeTree)(wizard.getWizardContext().getAttribute("nkDicomNodeTreeSelected")));
        nkDicomImport nkio;
        nkio = ((nkDicomImport)(wizard.getWizardContext().getAttribute("nkDicomImport")));
        if(nkio == null || selectedNode == null) return;
        nkEvent e = new nkEvent(this);
        e.setAttribute("nkDicomNodeTreeSelected", selectedNode);
        e.setAttribute("nkDicomImportSelected", nkio);
        for( int i= 0; i < prv_nkEvent_listeners.size(); ++i)
            ((nkEventListener)prv_nkEvent_listeners.elementAt( i)).nkEventInvoke(e);
    }
}