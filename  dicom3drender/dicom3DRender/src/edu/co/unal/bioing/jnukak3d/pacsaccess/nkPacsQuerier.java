/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.pacsaccess;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.dicom.StoredFilePathStrategySingleFolder;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.query.QueryInformationModel;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.StudyRootQueryInformationModel;
import java.io.File;
import java.io.IOException;
import edu.co.unal.bioing.jnukak3d.pacsaccess.runnable.nkGetU;

/**
 *
 * @author fuanka
 */
public class nkPacsQuerier {

    public QueryTreeModel queryPacsContents(nkConnectionSettings cn) throws DicomException, DicomNetworkException, IOException, NumberFormatException{
        AttributeList identifier = new AttributeList();
        AttributeTag t = TagFromName.PatientID;
        Attribute a = new CodeStringAttribute(t);
        a.addValue("");
        identifier.put(t,a);

        //TODO and the debug Level
        QueryInformationModel model = new StudyRootQueryInformationModel(cn.getServer(),cn.getPort(),cn.getCalledAE(),cn.getCallingAE(),0);
        QueryTreeModel tree = model.performHierarchicalQuery(identifier);
        return tree;
    }

    public static void retriveStudyFromPacs(File targetDirectory, String studyUID, nkConnectionSettings cn) throws DicomException,DicomNetworkException,IOException{
         AttributeList identifier = new AttributeList();

         AttributeTag t = TagFromName.QueryRetrieveLevel;
         Attribute a = new CodeStringAttribute(t);
         a.addValue("STUDY");
         identifier.put(t,a);

         AttributeTag t2 = TagFromName.StudyInstanceUID;
         Attribute b = new UniqueIdentifierAttribute(t2);
         b.addValue(studyUID);
         identifier.put(t2,b);

         nkOurReceivedObjectHandler oroh=new nkOurReceivedObjectHandler();

         nkGetU get=new nkGetU(cn, identifier, targetDirectory);
         Boolean success=get.call();

         if(success)
             System.out.println("Exito");
         else
             System.out.println("Falla");
  
         /*new GetSOPClassSCU(cn.getServer(),cn.getPort(),cn.getCalledAE(),cn.getCallingAE(),
                     SOPClass.StudyRootQueryRetrieveInformationModelGet,identifier,new IdentifierHandler(),
                     targetDirectory,new StoredFilePathStrategySingleFolder(),
                     new OurReceivedObjectHandler(),SOPClass.getSetOfStorageSOPClasses(),
                     true,false,false,0);*/

     }
}


