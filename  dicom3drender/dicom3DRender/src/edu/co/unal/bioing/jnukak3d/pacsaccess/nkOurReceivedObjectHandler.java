/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.co.unal.bioing.jnukak3d.pacsaccess;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author fuanka
 */
public class nkOurReceivedObjectHandler extends ReceivedObjectHandler {


    public nkOurReceivedObjectHandler() {
 
    }

    public void sendReceivedObjectIndication(final String dicomFileName, final String transferSyntax, final String callingAETitle) throws DicomNetworkException, DicomException, IOException {
        if (dicomFileName != null) {
            System.out.println("Received: " + dicomFileName + " from " + callingAETitle + " in " + transferSyntax);
        } else {
            //TODO aca que?
        }
    }

    
}
