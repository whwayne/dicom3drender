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

package edu.co.unal.bioing.jnukak3d.plugin.extern;

import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.plugin.nkKernel;
import edu.co.unal.bioing.jnukak3d.plugin.nkPlugin;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockServer;

/**
 * @author Alexander Pinzon Fernandez
 */
public class nkDockingFramesEngine implements nkPlugin, nkDockServer.nkDockEngine{

    public int getEngineVersion() {
        return 1;
    }

    public void registerPlugin(nkKernel K) {
        K.getDockServer().addDockEngine(this);
    }

    public nkDockManager createDockManager() {
        return new nkDockingFramesAdapter();
    }

    public String getName() {
        return "DockingFrames";
    }



}
