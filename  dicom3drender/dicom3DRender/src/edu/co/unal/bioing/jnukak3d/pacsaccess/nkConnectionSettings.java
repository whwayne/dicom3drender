/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.pacsaccess;

/**
 *
 * @author fuanka
 */
public class nkConnectionSettings {
   private String server;
   private int port;
   private String calledAE;
   private String callingAE;

   public nkConnectionSettings(String serverIp, int aPort, String theCalledAE,String theCallingAE){
       this.server=serverIp;
       this.port=aPort;
       this.calledAE=theCalledAE;
       this.callingAE=theCallingAE;
   }

    /**
     * @return the serverConected
     */
    public String getServer() {
        return server;
    }

    /**
     * @param serverConected the serverConected to set
     */
    public void setServer(String serverConected) {
        this.server = serverConected;
    }

    /**
     * @return the portConnected
     */
    public int getPort() {
        return port;
    }

    /**
     * @param portConnected the portConnected to set
     */
    public void setPort(int portConnected) {
        this.port = portConnected;
    }

    /**
     * @return the calledAE
     */
    public String getCalledAE() {
        return calledAE;
    }

    /**
     * @param calledAE the calledAE to set
     */
    public void setCalledAE(String calledAE) {
        this.calledAE = calledAE;
    }

    /**
     * @return the callingAE
     */
    public String getCallingAE() {
        return callingAE;
    }

    /**
     * @param callingAE the callingAE to set
     */
    public void setCallingAE(String callingAE) {
        this.callingAE = callingAE;
    }

 

   
}
