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

package edu.co.unal.bioing.jnukak3d.plugin;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Alexander Pinzon Fernandez
 */
public class nkDockServer {
    private Vector <nkDockEngine> m_DockDrivers;
    private int default_engine;

    public nkDockServer() {
        default_engine = 0;
    }

    public interface nkDockEngine{
        public String getName();
        public nkDockManager createDockManager();
    }

    public void addDockEngine(nkDockEngine DM){
        if(m_DockDrivers == null){
            m_DockDrivers = new Vector<nkDockEngine>();
        }
        m_DockDrivers.add(DM);
    }

    public nkDockEngine getDockEngine(int Index) {
      return m_DockDrivers==null ? null: m_DockDrivers.get(Index);
    }

    public nkDockEngine getDockEngine(String dockManagerName) {
        if(m_DockDrivers == null) return null;
        Iterator<nkDockEngine> iter = m_DockDrivers.iterator();
        while(iter.hasNext()){
            nkDockEngine temp = iter.next();
            if(temp.getName().compareTo(dockManagerName) == 0)
                return temp;
        }
      return null;
    }

    public nkDockEngine getDefaultDockEngine(){
        return getDockEngine(default_engine);
    }

    public void setDefaultEngine(int default_engine) {
        this.default_engine = default_engine;
    }

    public void setDefaultEngine(String default_engine_name) {
        if(m_DockDrivers == null) return ;
        Iterator<nkDockEngine> iter = m_DockDrivers.iterator();
        int count = 0;
        while(iter.hasNext()){
            nkDockEngine temp = iter.next();
            if(temp.getName().compareTo(default_engine_name) == 0){
                default_engine = count;
                return;
            }
            count++;
        }
    }

    public boolean existDockEngine(String dockManagerName){
        if(m_DockDrivers == null) return false;
        Iterator<nkDockEngine> iter = m_DockDrivers.iterator();
        while(iter.hasNext()){
            nkDockEngine temp = iter.next();
            if(temp.getName().compareTo(dockManagerName) == 0)
                return true;
        }
        return false;
    }

    public int size(){
        return m_DockDrivers==null? 0 : m_DockDrivers.size();
    }
}
