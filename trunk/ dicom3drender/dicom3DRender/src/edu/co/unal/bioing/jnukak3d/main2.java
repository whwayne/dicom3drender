package edu.co.unal.bioing.jnukak3d;

import javax.swing.SwingUtilities;



public class main2 {
    public static void main( String argv[]){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                nkNukak3d nkn = new nkNukak3d();
                nkn.setVisible(true);
                nkn.setSize(800, 600);
            }
        });
      
    }
}
