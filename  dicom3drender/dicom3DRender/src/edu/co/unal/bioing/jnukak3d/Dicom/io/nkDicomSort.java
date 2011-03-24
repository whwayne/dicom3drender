/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.Dicom.io;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.util.Comparator;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkDicomSort implements Comparator{
    protected static final boolean DEBUG = nkDebug.DEBUG;

    final static AttributeTag InstanceNumber = TagFromName.InstanceNumber; //0020,0013
    final static AttributeTag SliceLocation = TagFromName.SliceLocation; //0020,1041
    final static AttributeTag StudyInstanceUID = TagFromName.StudyInstanceUID;
    final static AttributeTag SeriesInstanceUID = TagFromName.SeriesInstanceUID;
    final static AttributeTag SeriesNumber = TagFromName.SeriesNumber;
    final static AttributeTag ImageSetNumber = TagFromName.ImageSetNumber;

    /**
     * Constructor
     */
    public nkDicomSort() {
    }

    /**
     * Compare function for Dicom Images.
     * <p>
     * Compare based on 3 characteristics:
     * <p>
     * 1. InstanceNumber<p>
     * 2. ImageSetNumber<p>
     * 3. SliceLocation<p>
     * <p>
     * If InstaceNumber is diferent in both files, return InstanceNumber_1 - InstanceNumber_2;<p>
     * Else If sortImageSetNumber is diferent in both files, return ImageSetNumber_1 - ImageSetNumber_2;<p>
     * Else use sortSliceLocation , return  SliceLocation_1 - SliceLocation_2;<p>
     * <p>
     * @param o1 Dicom File To compare
     * @param o2 Dicom File To compare
     */
    public int compare(Object o1, Object o2) {
        AttributeList ao1 = (AttributeList)o1;
        AttributeList ao2 = (AttributeList)o2;

        if(ao1 ==null || ao2 ==null) return 0;

        int InstanceNumber_1 = Attribute.getSingleIntegerValueOrDefault(ao1, InstanceNumber, -123456789);
        int ImageSetNumber_1 =  Attribute.getSingleIntegerValueOrDefault(ao1, ImageSetNumber, -123456789);
        double SliceLocation_1 = Attribute.getSingleDoubleValueOrDefault(ao1, SliceLocation, -123456789.0);

        int InstanceNumber_2 = Attribute.getSingleIntegerValueOrDefault(ao2, InstanceNumber, -123456789);
        int ImageSetNumber_2 =  Attribute.getSingleIntegerValueOrDefault(ao2, ImageSetNumber, -123456789);
        double SliceLocation_2 = Attribute.getSingleDoubleValueOrDefault(ao2, SliceLocation, -123456789.0);

        if(DEBUG){
        System.out.println("nkDicomSort.compare(); Comparation 1: " + InstanceNumber_1 + ", " + InstanceNumber_2);
        System.out.println("nkDicomSort.compare(); Comparation 2: " + ImageSetNumber_1 + ", " + ImageSetNumber_2);
        System.out.println("nkDicomSort.compare(); Comparation 3: " + SliceLocation_1 + ", " + SliceLocation_2);
        }

        boolean sortInstanceNumber = true;

        if(InstanceNumber_1 == -123456789 || InstanceNumber_2 == -123456789)
            sortInstanceNumber = false;
        if(InstanceNumber_1 == InstanceNumber_2)
            sortInstanceNumber = false;

        boolean sortImageSetNumber = true;

        if(ImageSetNumber_1 == -123456789 || ImageSetNumber_2 == -123456789)
            sortImageSetNumber = false;
        if(InstanceNumber_1 == InstanceNumber_2)
            sortImageSetNumber = false;

        boolean sortSliceLocation = true;

        if(SliceLocation_1 == -123456789.0 || SliceLocation_2 == -123456789.0)
            sortSliceLocation = false;
        if(SliceLocation_1 == SliceLocation_2)
            sortSliceLocation = false;


        if(sortInstanceNumber){
                return InstanceNumber_1 - InstanceNumber_2;
        }else{
            if(sortImageSetNumber){
                return ImageSetNumber_1 - ImageSetNumber_2;
            }else{
                if(sortSliceLocation){
                    double sum = SliceLocation_1 - SliceLocation_2;
                    if(sum<0) return -1;
                    if(sum==0) return 0;
                    if(sum>0) return 1;
                }

            }
        }

        return 0;
    }
}
