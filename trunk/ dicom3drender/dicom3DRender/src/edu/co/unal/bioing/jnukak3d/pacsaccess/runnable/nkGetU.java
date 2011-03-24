/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.pacsaccess.runnable;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.StoredFilePathStrategySingleFolder;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import edu.co.unal.bioing.jnukak3d.pacsaccess.nkConnectionSettings;
import edu.co.unal.bioing.jnukak3d.pacsaccess.nkOurReceivedObjectHandler;

/**
 *
 * @author fuanka
 */
public class nkGetU implements Callable<Boolean>{
    
    private nkConnectionSettings settings;
    private AttributeList identifier;
    private File targetDirectory;

    public nkGetU(nkConnectionSettings sets, AttributeList ident, File target) {
        this.settings=sets;
        this.targetDirectory=target;
        this.identifier=ident;
    }

    public Boolean call()  throws DicomException,DicomNetworkException,IOException{
        new GetSOPClassSCU(settings.getServer(),settings.getPort(),settings.getCalledAE(),settings.getCallingAE(),
                     SOPClass.StudyRootQueryRetrieveInformationModelGet,identifier,new IdentifierHandler(),
                     targetDirectory,new StoredFilePathStrategySingleFolder(),
                     new nkOurReceivedObjectHandler(),SOPClass.getSetOfStorageSOPClasses(),
                     true,false,false,0);
      
        return true;
    }

  

    /*private ConnectionSettings settings;
    private AttributeList identifier;
    private File targetDirectory;

    public GetU(ConnectionSettings sets, AttributeList ident, File target) {
        this.settings=sets;
        this.targetDirectory=target;
        this.identifier=ident;
    }

    public void run() {
        try{
        new GetSOPClassSCU(settings.getServer(),settings.getPort(),settings.getCalledAE(),settings.getCallingAE(),
                     SOPClass.StudyRootQueryRetrieveInformationModelGet,identifier,new IdentifierHandler(),
                     targetDirectory,new StoredFilePathStrategySingleFolder(),
                     new OurReceivedObjectHandler(),SOPClass.getSetOfStorageSOPClasses(),
                     true,false,false,0);
        }catch(DicomException de){

        }catch(DicomNetworkException de){

        }catch(IOException de){

        }

    }*/
}
