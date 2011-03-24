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

package edu.co.unal.bioing.jnukak3d.image3d;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.GeometryOfSliceFromAttributeList;
import com.pixelmed.dicom.GeometryOfVolumeFromAttributeList;
import com.pixelmed.dicom.ModalityTransform;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.VOITransform;
import com.pixelmed.display.SourceImage;

import edu.co.unal.bioing.jnukak3d.ImageUtil.WindowCenterAndWidth;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.Date;
import javax.vecmath.Matrix4d;


/**
 *
 * @author Alexander Pinzon Fernandez, Juan Carlos Leon Alcazar
 */
public class nkBufferedImage3dCreator extends nkImage3dCreator{
    //protected static final boolean DEBUG = nkDebug.DEBUG;
    protected static final boolean DEBUG = false;
    private AttributeList [] attributeListArray;

    //private static long acomulatedTime=0;

    public nkBufferedImage3dCreator(AttributeList [] si) {
        this.attributeListArray = si;
    }

    //TODO frames cargar todas las imagenes, como se organizan en la seri
    //TODO gated Images, lanzar excepcion

    @Override
    protected nkImage3d factoryMethod() {
       try{
            int numberOfDicomHeaders, i=0;
            int numberOfFramesInDicomImage, j;
            int numberOfBufferedImages, k=0;
            int numberOfResizeImages, l;
            GeometryOfSliceFromAttributeList gos;
            boolean doFlip=false;

            try{
                gos=new GeometryOfSliceFromAttributeList(attributeListArray[0]);
                if(gos.getNormal().z==-1)
                    doFlip=true;
            }catch(NullPointerException npe){
                System.out.println("No geometry of slice Aviable");
                //TODO then...
            }

            numberOfDicomHeaders = attributeListArray.length;
            numberOfBufferedImages = getImageAmount();
            numberOfResizeImages=getSizeZinPixels();

            BufferedImage [] bufferedImageArray = new BufferedImage[numberOfBufferedImages];
                             
            AffineTransform at=AffineTransform.getScaleInstance(1, -1);
            at.translate(0, -1*new SourceImage(attributeListArray[0]).getHeight());

            SourceImage tempSI;
            AffineTransformOp transformOperation;
            BufferedImage grayScaleAdjustedImage;
            while(k<numberOfBufferedImages && i<numberOfDicomHeaders){
                tempSI= new SourceImage(attributeListArray[i]);
                numberOfFramesInDicomImage = tempSI.getNumberOfBufferedImages();
                
                for(j = 0; j<numberOfFramesInDicomImage; j++){
                    grayScaleAdjustedImage=adjustGrayScaleDisplay(tempSI, tempSI.getBufferedImage(j));
                    if(doFlip){
                        transformOperation= new AffineTransformOp(at,AffineTransformOp.TYPE_BILINEAR);
                        bufferedImageArray[k] = transformOperation.filter(grayScaleAdjustedImage, null);
                    }else
                         bufferedImageArray[k] =grayScaleAdjustedImage;
                    k = k + 1;
                }
                i = i + 1 ;
            }

            BufferedImage [] ResizedImageArray = new BufferedImage[numberOfResizeImages];
            for(l = 0; l <numberOfResizeImages; l++){
                ResizedImageArray[l] = bufferedImageArray[(int)(Math.floor((double)(l*numberOfBufferedImages)/((double)numberOfResizeImages)))];
            }

            nkImage3d nimg = new nkBufferedImage3d(ResizedImageArray);
            return nimg;

            }catch(Exception e){
               
                    System.out.println("nkBufferedImage3dCreator.factoryMethod(): " + e);
                    e.printStackTrace();

            }
                    
        return null;//TODO null might crash other parts of nukak
    }

    private BufferedImage adjustGrayScaleDisplay(SourceImage SIM,BufferedImage bi) {
        boolean useVOILUTNotFunction=false;
        double  windowWidth=0;
        double windowCenter=0;
        double voiLUTIdentityWindowCenter=0;
        double voiLUTIdentityWindowWidth=0;
        int voiLUTNumberOfEntries=0;
        int voiLUTFirstValueMapped=0;
        int voiLUTBitsPerEntry=0;
        short[] voiLUTData=null;
        int voiLUTEntryMin=0;
        int voiLUTEntryMax=0;
        int voiLUTTopOfEntryRange=0;

        double useSlope=1.0;
        double useIntercept=0.0;

        int imgMin=SIM.getMinimum();	
	int imgMax=SIM.getMaximum();
        int currentVOITransformInUse;

        //extract the intercept and slope
        ModalityTransform modalityTransform = SIM.getModalityTransform();
        if (modalityTransform != null) {
            if(DEBUG)
                System.out.println("nkBufferedImage3dCreator.establishInitialWindowOrVOILUT(): modality transform exists");
            useSlope = modalityTransform.getRescaleSlope(0);		
            useIntercept = modalityTransform.getRescaleIntercept(0);
            if(DEBUG)
                System.out.println("using "+useSlope+" as slope "+useIntercept+" as intercept");
        }else{
             if(DEBUG)
                System.out.println("Using default slope and intercept");
                useSlope=1.0;
                useIntercept=0.0;
        }
        //find voi transforms
        VOITransform voiTransform=SIM.getVOITransform();
        for(int frameIndex=0;frameIndex<SIM.getNumberOfFrames();frameIndex++){//iterate througth the frames
            if (voiTransform != null) {
                if(DEBUG)
                    System.out.println("VOI is not null");
                
                final int nTransforms = voiTransform.getNumberOfTransforms(frameIndex);
                currentVOITransformInUse=0;
                if(DEBUG)
                    System.out.println("nkBufferedImage3dCreator establishInitialWindowOrVOILUT Found "+nTransforms+" transform for frame "+frameIndex);

                // first look for actual LUT, and prefer LUT over window values
                while (currentVOITransformInUse < nTransforms){
                        if (voiTransform.isLUTTransform(frameIndex,currentVOITransformInUse)) {
                            if(DEBUG)
                                System.out.println("nkBufferedImage3dCreator establishInitialWindowOrVOILUT Found LUT Transform");

                            voiLUTNumberOfEntries=voiTransform.getNumberOfEntries (frameIndex,currentVOITransformInUse);
                            voiLUTFirstValueMapped=voiTransform.getFirstValueMapped(frameIndex,currentVOITransformInUse);
                            voiLUTBitsPerEntry=voiTransform.getBitsPerEntry(frameIndex,currentVOITransformInUse);
                            voiLUTData=voiTransform.getLUTData(frameIndex,currentVOITransformInUse);
                            voiLUTEntryMin=voiTransform.getEntryMinimum(frameIndex,currentVOITransformInUse);
                            voiLUTEntryMax=voiTransform.getEntryMaximum(frameIndex,currentVOITransformInUse);
                            voiLUTTopOfEntryRange=voiTransform.getTopOfEntryRange(frameIndex,currentVOITransformInUse);

                            if (voiLUTData != null && voiLUTData.length == voiLUTNumberOfEntries) {
                                    if(DEBUG)
                                        System.out.println("We Have Voi data, and lenght is "+voiLUTNumberOfEntries);
                                    useVOILUTNotFunction=true;	// only if LUT is "good"
                                    voiLUTIdentityWindowWidth  = voiLUTNumberOfEntries;
                                    voiLUTIdentityWindowCenter = voiLUTFirstValueMapped + voiLUTNumberOfEntries/2;
                                    windowWidth  = voiLUTIdentityWindowWidth;
                                    windowCenter = voiLUTIdentityWindowCenter;
                            }
                            break;
                        }
                        ++currentVOITransformInUse;
                }

                if (!useVOILUTNotFunction){// no LUT, so search transforms again for window values
                    if(DEBUG)
                        System.out.println("No Lut found, looking for window values");
                    currentVOITransformInUse=0;
                    while (currentVOITransformInUse < nTransforms) {
                        if (voiTransform.isWindowTransform(frameIndex,currentVOITransformInUse)){

                            useVOILUTNotFunction=false;
                            windowWidth=voiTransform.getWidth(frameIndex,currentVOITransformInUse);
                            windowCenter=voiTransform.getCenter(frameIndex,currentVOITransformInUse);
                            if(DEBUG)
                                System.out.println("Found window level values center: "+windowCenter+" width "+windowWidth);
                            break;
                        }
                        ++currentVOITransformInUse;
                    }
                    //Some images just have an "unapropiate" center and width setting here
                    //so ignore this selection and use min max method
                    if(windowCenter>1500 || windowWidth>500){
                        if(DEBUG)
                            System.out.println("rejecting window level from dicom header, using minmax");
                        useVOILUTNotFunction=false;
                        windowWidth=0;
                    }

                }
            }
            if (!useVOILUTNotFunction && windowWidth == 0) {// if no LUT, use supplied window only if there was one, and if its width was not zero (center may legitimately be zero)
                double ourMin = imgMin*useSlope+useIntercept;
                double ourMax = imgMax*useSlope+useIntercept;
                windowWidth=(ourMax-ourMin);
                windowCenter=(ourMax+ourMin)/2.0;
                currentVOITransformInUse=-1;
                if(DEBUG)
                    System.out.println("Found window level values (MINMAX) center: "+windowCenter+" width "+windowWidth);
            }
        }

        if (useVOILUTNotFunction) {
                return  com.pixelmed.display.WindowCenterAndWidth.applyVOILUT(bi,windowCenter,windowWidth,voiLUTIdentityWindowCenter,voiLUTIdentityWindowWidth,SIM.isSigned(),SIM.isInverted(),useSlope,useIntercept,SIM.isPadded(),SIM.getPadValue(),SIM.getPadRangeLimit(),
                        voiLUTNumberOfEntries,voiLUTFirstValueMapped,voiLUTBitsPerEntry,voiLUTData,voiLUTEntryMin,voiLUTEntryMax,voiLUTTopOfEntryRange);
        }
       /* else if (useVOIFunction == 1) {
                cachedPreWindowedImage = applyWindowCenterAndWidthLogistic(useSrcImage,windowCenter,windowWidth,signed,inverted,useSlope,useIntercept,hasPad,pad,padRangeLimit);
        }*/
        else {
            if(DEBUG)
                System.out.println("Calling Jnukak Source");

            return WindowCenterAndWidth.applyWindowCenterAndWidthLinear(bi,windowCenter,windowWidth,SIM.isSigned(),SIM.isInverted(),useSlope,useIntercept,SIM.isPadded(),SIM.getPadValue(),SIM.getPadRangeLimit());
        }
    }

    private int getImageAmount(){
        int imageAmount=0;
        for(int i=0;i<attributeListArray.length;i++){
            if(DEBUG)
                System.out.println("nkBufferedImage3dCreator.getImageAmount: amountOfFrames"+Attribute.getSingleIntegerValueOrDefault(attributeListArray[i], TagFromName.NumberOfFrames,1));
            imageAmount=imageAmount+Attribute.getSingleIntegerValueOrDefault(attributeListArray[i], TagFromName.NumberOfFrames,1);
        }
        return imageAmount;
    }


    //TODO slice postion can be calculeted in an altervantive (better?)way
    private int getSizeZinPixels(){
        
        double [] pixelSpacing;
        double [] imageOrientationINI;
        double [] imageOrientationEND;
        double [] imagePositionINI;
        double [] imagePositionEND;
        int rows, columns;

        Matrix4d mINI, mEND;
        Matrix4d pINI, pEND;

        if (DEBUG)
            System.out.println( "nkBufferedImage3dCreator: attributeListArray.length " + attributeListArray.length);
        if(attributeListArray.length <1) return 0;
        imageOrientationINI = Attribute.getDoubleValues(attributeListArray[0], TagFromName.ImageOrientationPatient);
        imageOrientationEND = Attribute.getDoubleValues(attributeListArray[attributeListArray.length-1], TagFromName.ImageOrientationPatient);
        imagePositionINI = Attribute.getDoubleValues(attributeListArray[0], TagFromName.ImagePositionPatient);
        imagePositionEND = Attribute.getDoubleValues(attributeListArray[attributeListArray.length-1], TagFromName.ImagePositionPatient);
        pixelSpacing = Attribute.getDoubleValues(attributeListArray[0], TagFromName.PixelSpacing);
        rows = Attribute.getSingleIntegerValueOrDefault(attributeListArray[0], TagFromName.Rows, 0);
        columns = Attribute.getSingleIntegerValueOrDefault(attributeListArray[0], TagFromName.Columns, 0);

        if (DEBUG) {
            System.out.println("imageOrientationINI \n"+ imageOrientationINI);
            System.out.println("imageOrientationEND \n"+ imageOrientationEND);
            System.out.println("imagePositionINI \n"+ imagePositionINI);
            System.out.println("imagePositionEND \n"+ imagePositionEND);
            System.out.println("pixelSpacing \n"+ pixelSpacing);
            System.out.println("rows "+ rows);
            System.out.println("columns "+ columns);
            System.out.println("imageOrientationINI[0];"+imageOrientationINI[0]);
            System.out.println("imageOrientationINI[1];"+imageOrientationINI[1]);
            System.out.println("imageOrientationINI[2];"+imageOrientationINI[3]);
            System.out.println("pixelSpacing[0];"+pixelSpacing[0]);
        }
        //slice_thickness = Attribute.getSingleDoubleValueOrDefault(attributeListArray[0], TagFromName.SpacingBetweenSlices, 1.0);


        try{
            mINI = new Matrix4d(new double[]{
            imageOrientationINI[0] * pixelSpacing[0], imageOrientationINI[3] * pixelSpacing[1], 0, imagePositionINI[0],
            imageOrientationINI[1] * pixelSpacing[0], imageOrientationINI[4] * pixelSpacing[1], 0, imagePositionINI[1],
            imageOrientationINI[2] * pixelSpacing[0], imageOrientationINI[5] * pixelSpacing[1], 0, imagePositionINI[2],
            0,0,0,1});

    //>>>>>>>>>>>>>>>>>>>>>
            if (DEBUG) System.out.println("mINI \n" + mINI);

            mEND = new Matrix4d(new double[]{
            imageOrientationEND[0] * pixelSpacing[0], imageOrientationEND[3] * pixelSpacing[1], 0, imagePositionEND[0],
            imageOrientationEND[1] * pixelSpacing[0], imageOrientationEND[4] * pixelSpacing[1], 0, imagePositionEND[1],
            imageOrientationEND[2] * pixelSpacing[0], imageOrientationEND[5] * pixelSpacing[1], 0, imagePositionEND[2],
            0,0,0,1});
            if (DEBUG) System.out.println("mEND \n" + mEND);

            pINI = new Matrix4d(new double[]{
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            1.0, 0.0, 0.0, 0.0,
            });


            if (DEBUG) System.out.println("pINI \n" + pINI);

            pEND = new Matrix4d(new double[]{
            columns, 0.0, 0.0, 0.0,
            rows, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            1.0, 0.0, 0.0, 0.0,
            });

            if (DEBUG) System.out.println("pEND \n" + pEND);

            mINI.mul(pINI);

            if (DEBUG) System.out.println("mINI.mul(pINI); \n" + mINI);

            mEND.mul(pEND);
            if (DEBUG) System.out.println("mEND.mul(pEND); \n" + mEND);

            Matrix4d m_result = new Matrix4d();
            m_result.sub(mEND, mINI);

            if (DEBUG) System.out.println("m_result.sub(mEND, mINI); \n" + m_result);
            return (int) Math.abs(Math.floor((m_result.m20 * columns)/ m_result.m00));
         }catch(NullPointerException npe){
            try{//juts return the nuumber of images for the adquisition
                return attributeListArray[0].get(TagFromName.ImagesInAcquisition).getIntegerValues()[0];
            }catch(Exception e){
                e.printStackTrace();
                return 0;
            }
        }
    }
}
