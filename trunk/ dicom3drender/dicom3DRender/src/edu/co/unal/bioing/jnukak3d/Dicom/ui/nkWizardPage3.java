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

package edu.co.unal.bioing.jnukak3d.Dicom.ui;

import edu.co.unal.bioing.jnukak3d.Dicom.nkDicomNodeTree;
import edu.co.unal.bioing.jnukak3d.Dicom.io.nkDicomImport;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import jwf.WizardPanel;

/**
 *
 * @author Alexander Pinzon Fernandez
 */
public class nkWizardPage3 extends WizardPanel implements TreeSelectionListener{
    protected static final boolean DEBUG= nkDebug.DEBUG;

    File directory;
    nkDicomImport nkimport;
    JTree tree;
    DefaultMutableTreeNode root;
    DefaultTreeModel content;
    int studyAmount;
    nkDicomNodeTree nodeSelected;

    public nkWizardPage3(File dir) {

        directory = dir;
        studyAmount = 0;
        nodeSelected = null;
        nkimport = new nkDicomImport();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Study's found in directory"));
        root = new DefaultMutableTreeNode(new nkDicomNodeTree("/", null, null, null,null, nkDicomNodeTree.TYPE_ROOT));
        content = new DefaultTreeModel(root);
        tree = new JTree(content);
        tree.setCellRenderer(new Renderer());
        ToolTipManager.sharedInstance().registerComponent(tree);
        tree.addTreeSelectionListener(this);


        JScrollPane sb = new JScrollPane(tree);

        add(sb, BorderLayout.CENTER);

    }

    public void setDir(File dir){
        directory = dir;
    }

    public void display() {
        if(directory == null) return;
        root.setUserObject( new nkDicomNodeTree(directory.toString(), null, null,null, directory.toString(), nkDicomNodeTree.TYPE_ROOT));
        content.nodeChanged(root);
        nkimport.loadDirectory(directory);

        studyAmount = nkimport.getNumberOfStudys();
        if(studyAmount ==0){
            root.setUserObject(new nkDicomNodeTree("Error: "+directory.toString() + " has no valid Dicom files", null, null,null, directory.toString(), nkDicomNodeTree.TYPE_ROOT));
            content.nodeChanged(root);
            return;
        }

        int numberOfStudys, i;
        String keyStudy;
        String strParent;
        int numberOfSeries, j;
        String keySerie;
        String strSon;
        int numberOfAcquisitions, k;
        String keyAcquisition;
        String strGrandson;
        int numberOfImages, l;

        numberOfStudys = studyAmount;

        for(i=0; i< numberOfStudys; i++){
            keyStudy = nkimport.getStudysInstanceUIDAt(i);
            if(keyStudy != null){
                strParent = nkimport.getPatienNameFromStudy(keyStudy);
                numberOfSeries = nkimport.getNumbersOfSeries(keyStudy);
                DefaultMutableTreeNode patientNode = new DefaultMutableTreeNode(new nkDicomNodeTree(strParent+"    "+numberOfSeries+" Series",keyStudy ,null,null,directory.toString(), nkDicomNodeTree.TYPE_STUDY));
                content.insertNodeInto(patientNode,root,i);
                for(j=0; j<numberOfSeries; j++){
                    keySerie = nkimport.getDicomSeriesInstaceUID(keyStudy, j);
                    if(keySerie !=null){
                        numberOfAcquisitions = nkimport.getNumbersOfAcquisitions(keyStudy, keySerie);
                        strSon = "Series number: " + (j+1) + ", Number of acquisitions: " + numberOfAcquisitions;
                        DefaultMutableTreeNode seriesNode = new DefaultMutableTreeNode(new nkDicomNodeTree(strSon,keyStudy ,keySerie,null,directory.toString(), nkDicomNodeTree.TYPE_SERIE));
                        content.insertNodeInto(seriesNode,patientNode,j);
                        for(k = 0; k <numberOfAcquisitions; k++){
                            keyAcquisition = nkimport.getDicomAcquisitionNumber(keyStudy, keySerie, k);
                            if(keyAcquisition !=null){
                                numberOfImages = nkimport.getNumbersOfImages(keyStudy, keySerie, keyAcquisition);
                                strGrandson = "Acquisition number: " + (k+1) + ", Number of images: " + numberOfImages;
                                DefaultMutableTreeNode grandson = new DefaultMutableTreeNode(new nkDicomNodeTree(strGrandson,keyStudy ,keySerie,keyAcquisition,directory.toString(), nkDicomNodeTree.TYPE_ACQUISITION));
                                content.insertNodeInto(grandson, seriesNode,k);
                               //Should JNukak3d care about a single Image?
                                /*ArrayList<AttributeList> images = nkimport.getDicomAcquisition(keyStudy, keySerie, keyAcquisition);
                               
                                 if(images != null){
                                Iterator<AttributeList> iter = images.iterator();
                                l = 0;
                                    while(iter.hasNext()){
                                        AttributeList at = iter.next();
                                        String strImage = Attribute.getSingleStringValueOrEmptyString(at, TagFromName.InstanceNumber);
                                        DefaultMutableTreeNode sonImage = new DefaultMutableTreeNode(new nkDicomNodeTree(strImage, keyStudy, keySerie, keyAcquisition, directory.toString(), nkDicomNodeTree.TYPE_IMAGE));
                                        content.insertNodeInto(sonImage, grandson,l);
                                        l++;
                                    }
                                }*/
                            }
                        }
                    }
                }
            }
        }

        tree.expandRow(0);
        tree.expandRow(1);
        tree.expandRow(2);

    }


    /** Is there be a next panel?
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return false;
    }

    /** Called to validate the panel before moving to next panel.
     * @param list a List of error messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List list) {

        boolean valid = false;
        return valid;
    }

    /** Get the next panel to go to. */
    public WizardPanel next() {
        return null;
    }

    /** Can this panel finish the wizard?
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        boolean valid = true;
        if(studyAmount == 0){
            valid = false;
        }
        return valid;
    }

    /** Called to validate the panel before finishing the wizard. Should
     * return false if canFinish returns false.
     * @param list a List of error messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List list) {
        boolean valid = false;
        if(nodeSelected != null){
            if(nodeSelected.getType()==nkDicomNodeTree.TYPE_ACQUISITION ||  nodeSelected.getType()==nkDicomNodeTree.TYPE_SERIE )
                valid=true;
            else
                JOptionPane.showMessageDialog(null, "A series or an acquisition must be selected", "Bad Selection", JOptionPane.ERROR_MESSAGE);
            getWizardContext().setAttribute("nkDicomNodeTreeSelected", nodeSelected);
        }else
            JOptionPane.showMessageDialog(null, "You must select a series or an acquisition", "Bad Selection", JOptionPane.ERROR_MESSAGE);
        
        getWizardContext().setAttribute("nkDicomImport", this.nkimport);
        if(studyAmount ==0){
            valid = false;
            list.add("No study found.");
            list.add("Please click back to select another DICOM Directory.");
            list.add("Or click Cancel");
        }
        return valid;
        
    }

    /** Handle finishing the wizard. */
    public void finish() {
        
    }

    public void valueChanged(TreeSelectionEvent e) {
        TreePath tp = e.getPath();
        if(DEBUG)
            System.out.println(e.getPath());
        nodeSelected  =  (nkDicomNodeTree)(((DefaultMutableTreeNode)(tp.getPathComponent(tp.getPathCount()-1))).getUserObject());
        
    }

    private class Renderer extends DefaultTreeCellRenderer{

        public Renderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            nkDicomNodeTree demo = (nkDicomNodeTree)( ((DefaultMutableTreeNode)value).getUserObject());
            setToolTipText(demo.toString());
            super.getTreeCellRendererComponent(tree, demo.getText(), sel, expanded, leaf, row, hasFocus);
            return this;
        }

    }

}
