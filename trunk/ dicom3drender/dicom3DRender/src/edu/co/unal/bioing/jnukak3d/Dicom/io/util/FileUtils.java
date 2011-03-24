/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.co.unal.bioing.jnukak3d.Dicom.io.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 *
 * @author fuanka
 */
public class FileUtils {

    static public final ArrayList listFilesRecursively(File initialPath) {
//System.err.println("FileUtilities.listFilesRecursively(): "+initialPath);
        //System.out.println("Initial Call");
        ArrayList filesFound = new ArrayList();
        if (initialPath != null && initialPath.exists()) {
            if (initialPath.isFile()) {
                filesFound.add(initialPath);
                //System.out.println("Found File");
            } else if (initialPath.isDirectory()) {
                //System.out.println("Found Directory");
                try {
                    File[] filesAndDirectories = initialPath.listFiles((FilenameFilter) null);	// null FilenameFilter means all names
                    //System.out.println("Directory has "+filesAndDirectories.length+" files");
                    if (filesAndDirectories != null && filesAndDirectories.length > 0) {

                        for (int i = 0; i < filesAndDirectories.length; ++i) {
                            //System.out.println("iterating files "+i);
                            if (filesAndDirectories[i].isDirectory()) {
                                //System.out.println("Recursive Call ");
                                ArrayList moreFiles = listFilesRecursively(filesAndDirectories[i]);
                                if (moreFiles != null && !moreFiles.isEmpty()) {
                                    filesFound.addAll(moreFiles);
                                }
                            } else if (filesAndDirectories[i].isFile()) {
                               // System.out.println("adding one file ");// what else could it be ... just being paranoid
//System.err.println("FileUtilities.listFilesRecursively(): found "+filesAndDirectories[i]);
                                filesFound.add(filesAndDirectories[i]);
                            }
                        }
                    }
                } catch (SecurityException e) {
                    e.printStackTrace(System.err);
                    //System.out.println("CatchException");
                }
            }
            // else what else could it be
        }else
            System.out.println("Not supossed to be here");

        return filesFound;
    }
}
