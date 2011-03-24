/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.plugin.extern;

import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockServer;
import edu.co.unal.bioing.jnukak3d.plugin.nkKernel;
import edu.co.unal.bioing.jnukak3d.plugin.nkPlugin;

/**
 *
 * @author apinzonf
 */
public class nkJavaSwigEngine implements nkPlugin, nkDockServer.nkDockEngine{

    public int getEngineVersion() {
        return 6;
    }

    public void registerPlugin(nkKernel K) {
        K.getDockServer().addDockEngine(this);
    }

    public nkDockManager createDockManager() {
        return new nkJavaSwigAdapter();
    }

    public String getName() {
        return "javax.swing";
    }



}