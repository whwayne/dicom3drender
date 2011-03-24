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

package edu.co.unal.bioing.jnukak3d.ui;

import edu.co.unal.bioing.jnukak3d.ui.event.nkToolGenerator;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolListener;
import edu.co.unal.bioing.jnukak3d.ui.event.nkToolEvent;
import javax.swing.Icon;
import java.util.Vector;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkTool implements nkToolGenerator {
    String name;
    String textHelp;
    int id;
    Icon icon;

    private Vector prv_nkToolEvent_listeners;

    public nkTool() {
        this.name = "";
        this.textHelp = "";
        this.id = -1;
        this.icon = null;
    }

    public nkTool(String name, String textHelp, int id) {
        this(name, textHelp, id, (Icon)null);
    }
    public nkTool(String name, String textHelp, int id, Icon icon) {
        this.name = name;
        this.textHelp = textHelp;
        this.id = id;
        this.icon = icon;
    }

    
    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTextHelp() {
        return textHelp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTextHelp(String textHelp) {
        this.textHelp = textHelp;
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

    public void firenkToolEvent(){
        if( null == prv_nkToolEvent_listeners) return;
        nkToolEvent e = new nkToolEvent(this, id, name);
        for( int i= 0; i < prv_nkToolEvent_listeners.size(); ++i)
            ((nkToolListener)prv_nkToolEvent_listeners.elementAt( i)).nkToolInvoke(e);

    }


}
