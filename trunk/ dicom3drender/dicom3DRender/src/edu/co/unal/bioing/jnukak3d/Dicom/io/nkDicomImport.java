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

package edu.co.unal.bioing.jnukak3d.Dicom.io;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomFileUtilities;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.display.SourceImage;
import edu.co.unal.bioing.jnukak3d.Dicom.io.util.nkDicomFileUtil;
import edu.co.unal.bioing.jnukak3d.Dicom.io.util.FileUtils;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkDicomImport {
    protected static final boolean DEBUG = false;

    //protected Map<String, Map<String, ArrayList<AttributeList>>> study_series_files;
    //Map->Study ID->Series->ID->Adquisition Number ID->Files
    protected Map<String, Map<String, Map<String, ArrayList<AttributeList> > > > study_series_files;

    public nkDicomImport() {
        study_series_files = new HashMap<String, Map<String, Map<String, ArrayList<AttributeList>> >>();
    }

    public void loadDirectory(File initialPath){
        ArrayList<File> filesInDirectory = FileUtils.listFilesRecursively(initialPath);
        System.out.println("Found "+filesInDirectory.size()+" files in the dir");
        Iterator<File> itr = filesInDirectory.iterator();
        try{
            while(itr.hasNext()){
                //System.out.println("iterating");
                File keyFile = itr.next();
                 //System.out.println("iterating2");
                //second conditiond discards multiframe images, as far as Im concerned they are only for cine loop
                if(nkDicomFileUtil.isDicomOrAcrNemaFile(keyFile)){
                    //System.out.println("Processing file"+keyFile.getAbsolutePath());
                    AttributeList al = new AttributeList();
                    try{
                        al.read(keyFile.getPath());
                        //System.out.println("Read done");
                    }catch(Exception e){
                        e.printStackTrace();
                    }                   
                    
                    if(DEBUG)
                        System.out.println("nkDicomImport.loadDirectory(); Dicom File:" + keyFile.getPath());
                    String studyUID = Attribute.getSingleStringValueOrEmptyString(al, TagFromName.StudyInstanceUID);
                     if(DEBUG)
                        System.out.println("nkDicomImport.loadDirectory();Study:" + studyUID);
                    String seriesUID = Attribute.getSingleStringValueOrEmptyString(al, TagFromName.SeriesInstanceUID);
                     if(DEBUG)
                        System.out.println("nkDicomImport.loadDirectory(); Series:" + seriesUID);
                    String keyAcquisition = Attribute.getSingleStringValueOrEmptyString(al, TagFromName.AcquisitionNumber);
                    //String keyAcquisition = Attribute.getSingleStringValueOrEmptyString(al, TagFromName.SOPInstanceUID);

                     if(DEBUG)
                        System.out.println("nkDicomImport.loadDirectory(); Acq:" +keyAcquisition);

                    Map<String, Map<String, ArrayList<AttributeList> > > study = study_series_files.get(studyUID);
                    Map<String, ArrayList<AttributeList> > series;
                    ArrayList<AttributeList> acquisitionFiles;
                    if(study == null){
                        study = new HashMap<String, Map<String, ArrayList<AttributeList> > >();
                        series = new HashMap<String, ArrayList<AttributeList> >();
                        acquisitionFiles = new ArrayList<AttributeList>();
                        acquisitionFiles.add(al);
                        series.put(keyAcquisition, acquisitionFiles);
                        study.put(seriesUID, series);
                        study_series_files.put(studyUID, study);
                    }else{
                        series = study.get(seriesUID);
                        if(series==null){
                            series = new HashMap<String, ArrayList<AttributeList> >();
                            acquisitionFiles = new ArrayList<AttributeList>();
                            acquisitionFiles.add(al);
                            series.put(keyAcquisition, acquisitionFiles);
                            study.put(seriesUID, series);
                            study_series_files.put(studyUID, study);
                        }else{
                            acquisitionFiles = series.get(keyAcquisition);
                            if(acquisitionFiles == null){
                                series.put(keyAcquisition, acquisitionFiles = new ArrayList<AttributeList>());
                            }
                            acquisitionFiles.add(al);
                        }
                    }
                }else
                    System.out.println("Ignoring file"+keyFile.getAbsolutePath());

                 //System.out.println("iterating3");
            }
        }catch(Exception e){
            if(DEBUG)
                System.out.println("Dicom Import error " + e);
        }
    }

    public Map<String, Map<String, Map<String, ArrayList<AttributeList>>>> getAllStudysFilesPaths(){
        return study_series_files;
    }
    
    public Set<String> getAllStudysInstanceUID(){
        return study_series_files == null? null: study_series_files.keySet();
    }

    public String getStudysInstanceUIDAt(int keyStudy){
        if(study_series_files == null) return null;
        if(keyStudy >= getNumberOfStudys()) return null;
        String study = null;
        Iterator<String> iter = study_series_files.keySet().iterator();
        int i = -1;
        while(iter.hasNext() && i < keyStudy){
            i++;
            study = iter.next();
        }
        if(i==keyStudy) return study;
        else return null;
    }
    
    public Map<String, Map<String, ArrayList<AttributeList>>> getDicomStudy(String keyStudy){
        return (study_series_files == null ||  keyStudy == null) ?
            null :
            study_series_files.get(keyStudy);
    }

    public Map<String, Map<String, ArrayList<AttributeList>>> getDicomStudy(int keyStudy){
        if(study_series_files == null) return null;
        if(keyStudy >= getNumberOfStudys()) return null;
        String study = null;
        Iterator<String> iter = study_series_files.keySet().iterator();
        int i = -1;
        while(iter.hasNext() && i < keyStudy){
            i++;
            study = iter.next();
        }
        if(i==keyStudy) return getDicomStudy(study);
        else return null;
    }

    public Map<String, ArrayList<AttributeList>> getDicomSerie(String keyStudy, String keySerie){
        Map<String, Map<String, ArrayList<AttributeList>>> study = getDicomStudy(keyStudy);
        return study==null ? null: study.get(keySerie);
    }

    public Map<String, ArrayList<AttributeList>> getDicomSerie(String keyStudy, int keySerie){
        Map<String, Map<String, ArrayList<AttributeList>>> study = getDicomStudy(keyStudy);
        if(study==null) return null;
        Iterator<String> iter = study.keySet().iterator();
        String serie = null;
        int i = -1;
        while(iter.hasNext() && i < keySerie){
            i++;
            serie = iter.next();
        }
        if(i==keySerie) return getDicomSerie(keyStudy, serie);
        else return null;
    }

    public Map<String, ArrayList<AttributeList>> getDicomSerie(int keyStudy, int keySerie){
        Map<String, Map<String, ArrayList<AttributeList>>> study = getDicomStudy(keyStudy);
        if(study==null) return null;
        Iterator<String> iter = study.keySet().iterator();
        String serie = null;
        int i = -1;
        while(iter.hasNext() && i < keySerie){
            i++;
            serie = iter.next();
        }
        if(i==keySerie) return study.get(serie);
        else return null;
    }

    public ArrayList<AttributeList> getDicomAcquisition(String keyStudy, String keySerie, String keyAcquisition){
        Map<String, ArrayList<AttributeList>> series = getDicomSerie(keyStudy, keySerie);
        return series==null ? null: series.get(keyAcquisition);
    }

    public ArrayList<AttributeList> getDicomAcquisition(String keyStudy, String keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        Iterator<String> iter = serie.keySet().iterator();
        String str_acquisition = null;
        int i = -1;
        while(iter.hasNext() && i < keyAcquisition){
            i++;
            str_acquisition = iter.next();
        }
        if(i==keyAcquisition) return getDicomAcquisition(keyStudy, keySerie, str_acquisition);
        else return null;
    }

    public ArrayList<AttributeList> getDicomAcquisition(String keyStudy, int keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        return getDicomAcquisition(keyStudy,getDicomSeriesInstaceUID(keyStudy, keySerie),keyAcquisition);
    }

    public ArrayList<AttributeList> getDicomAcquisition(int keyStudy, int keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        return getDicomAcquisition(getStudysInstanceUIDAt(keyStudy) ,getDicomSeriesInstaceUID(keyStudy, keySerie),keyAcquisition);
    }

    public Set<String> getDicomSeriesInstaceUID(String keyStudy){
        Map<String, Map<String, ArrayList<AttributeList>>> series = getDicomStudy(keyStudy);
        return series==null ? null: series.keySet();
    }

    public String getDicomSeriesInstaceUID(String keyStudy, int keySerie){
        Map<String, Map<String, ArrayList<AttributeList>>> study = getDicomStudy(keyStudy);
        if(study==null) return null;
        Iterator<String> iter = study.keySet().iterator();
        String serie = null;
        int i = -1;
        while(iter.hasNext() && i < keySerie){
            i++;
            serie = iter.next();
        }
        if(i==keySerie) return serie;
        else return null;
    }

    public String getDicomSeriesInstaceUID(int keyStudy, int keySerie){
        Map<String, Map<String, ArrayList<AttributeList>>> study = getDicomStudy(keyStudy);
        if(study==null) return null;
        Iterator<String> iter = study.keySet().iterator();
        String serie = null;
        int i = -1;
        while(iter.hasNext() && i < keySerie){
            i++;
            serie = iter.next();
        }
        if(i==keySerie) return serie;
        else return null;
    }

    public String getDicomAcquisitionNumber(String keyStudy, String keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        Iterator<String> iter = serie.keySet().iterator();
        String acquisition = null;
        int i = -1;
        while(iter.hasNext() && i < keyAcquisition){
            i++;
            acquisition = iter.next();
        }
        if(i==keyAcquisition) return acquisition;
        else return null;
    }

    public String getDicomAcquisitionNumber(String keyStudy, int keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        Iterator<String> iter = serie.keySet().iterator();
        String acquisition = null;
        int i = -1;
        while(iter.hasNext() && i < keyAcquisition){
            i++;
            acquisition = iter.next();
        }
        if(i==keyAcquisition) return acquisition;
        else return null;
    }

    public String getDicomAcquisitionNumber(int keyStudy, int keySerie, int keyAcquisition){
        Map<String, ArrayList<AttributeList>> serie = getDicomSerie(keyStudy, keySerie);
        if(serie==null) return null;
        Iterator<String> iter = serie.keySet().iterator();
        String acquisition = null;
        int i = -1;
        while(iter.hasNext() && i < keyAcquisition){
            i++;
            acquisition = iter.next();
        }
        if(i==keyAcquisition) return acquisition;
        else return null;
    }

    public int getNumberOfStudys(){
        return study_series_files == null? 0: study_series_files.size();
    }

    public int getNumbersOfSeries(String keyStudy){
        Map<String, Map<String, ArrayList<AttributeList>>> series = getDicomStudy(keyStudy);
        return series==null ? 0: series.size();
    }
    
    public int getNumbersOfSeries(int keyStudy){
        Map<String, Map<String, ArrayList<AttributeList>>> series = getDicomStudy(keyStudy);
        return series==null ? 0: series.size();
    }

    public int getNumbersOfAcquisitions(String keyStudy, String keySerie){
        Map<String, ArrayList<AttributeList>> series = getDicomSerie(keyStudy, keySerie);
        return series==null ? 0: series.size();
    }
    
    public int getNumbersOfAcquisitions(String keyStudy, int keySerie){
        Map<String, ArrayList<AttributeList>> series = getDicomSerie(keyStudy, keySerie);
        return series==null ? 0: series.size();
    }
    
    public int getNumbersOfAcquisitions(int keyStudy, int keySerie){
        Map<String, ArrayList<AttributeList>> series = getDicomSerie(keyStudy, keySerie);
        return series==null ? 0: series.size();
    }


    public int getNumbersOfImages(String keyStudy, String keySeries, String keyAcquisition){
        ArrayList<AttributeList> images = getDicomAcquisition(keyStudy, keySeries, keyAcquisition);
        Iterator<AttributeList> iter=images.iterator();
        SourceImage si;
        int imageAmount=0;
        while(iter.hasNext()){
            try{
                si=new SourceImage((AttributeList)iter.next());
                imageAmount+=si.getNumberOfFrames();
            }catch(DicomException ignore){
                //ignore
            }
        }
        return imageAmount;
    }

    public int getNumbersOfImages(String keyStudy, String keySeries, int keyAcquisition){
        ArrayList<AttributeList> images = getDicomAcquisition(keyStudy, keySeries, keyAcquisition);
        return images==null ? 0: images.size();
    }

    public int getNumbersOfImages(String keyStudy, int keySeries, int keyAcquisition){
        ArrayList<AttributeList> images = getDicomAcquisition(keyStudy, keySeries, keyAcquisition);
        return images==null ? 0: images.size();
    }

    public int getNumbersOfImages(int keyStudy, int keySeries, int keyAcquisition){
        ArrayList<AttributeList> images = getDicomAcquisition(keyStudy, keySeries, keyAcquisition);
        return images==null ? 0: images.size();
    }

    public String getPatienNameFromStudy(String keyStudy){
        String name = "";
        int tam;
        tam = getNumbersOfSeries(keyStudy);
        if(tam>0){
            ArrayList<AttributeList> images = getDicomAcquisition(keyStudy, 0, 0);
            if(images==null) return "";
            if(images.iterator().hasNext()){
                AttributeList at = images.iterator().next();
                name = Attribute.getSingleStringValueOrEmptyString(at, TagFromName.PatientName);
            }
        }
        return name;
    }

    public String getPatienNameFromStudy(int keyStudy){
        String name = "";
        int tam;
        tam = getNumbersOfSeries(keyStudy);
        if(tam>0){
            ArrayList<AttributeList> serie = getDicomAcquisition(keyStudy, 0,0);
            if(serie.iterator().hasNext()){
                AttributeList at = serie.iterator().next();
                name = Attribute.getSingleStringValueOrEmptyString(at, TagFromName.PatientName);
            }
        }
        return name;
    }

}
