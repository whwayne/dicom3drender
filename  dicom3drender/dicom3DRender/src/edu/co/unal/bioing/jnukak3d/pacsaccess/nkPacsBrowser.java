/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.pacsaccess;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.QueryTreeRecord;
import edu.co.unal.bioing.jnukak3d.Dicom.io.nkDicomImport;
import edu.co.unal.bioing.jnukak3d.Dicom.nkDicomNodeTree;
import edu.co.unal.bioing.jnukak3d.Dicom.ui.nkWizardPage3;
import edu.co.unal.bioing.jnukak3d.event.nkEvent;
import edu.co.unal.bioing.jnukak3d.event.nkEventGenerator;
import edu.co.unal.bioing.jnukak3d.event.nkEventListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import jwf.Wizard;
import jwf.WizardListener;

/**
 *
 * @author fuanka
 */
public class nkPacsBrowser extends JFrame implements ActionListener, TreeSelectionListener, nkEventGenerator, WizardListener{
   private JPanel panelSetup;
   private JPanel panelDesc;
   private JPanel panelAuxtree;
   private JTree tree;
   private JScrollPane panelJtree;
   
   private JTextField textCalledAETittle;
   private JTextField textCallingAETittle;
   private JTextField textUrl;
   private JTextField textPort;

   private JLabel labelErrorMessage;
   private JLabel labelPatientValue;
   private JLabel labelStudyTypeValue;
   private JLabel labelStudyIDValue;
   private JLabel labelSeriesUIDValue;

   private JButton buttonOpen;
   private JButton buttonClose;

   JFrame wizardFrame;

   private final String stablishConnection="stablishConnection";
   private final String openWithNukak="openWithNukak";
   private final String closeBrowser="closeBrowser";
   private final int studyInfomationEntity=3;
   private final int seriesInfomationEntity=5;

   private Dimension treePanelDimension=new Dimension(310, 300);

   private DefaultMutableTreeNode defaultNode = new DefaultMutableTreeNode("Not connected to PACS server");

   private String selectedStudyUID;

   private nkConnectionSettings cn;

   private Vector prv_nkEvent_listeners;

   public nkPacsBrowser(){
       super("JNukak3D - Pacs Explorer");

       tree=new JTree(defaultNode);
       tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
       tree.addTreeSelectionListener(this);
       selectedStudyUID=null;

       panelSetup=new JPanel(new GridBagLayout());
       panelDesc=new JPanel(new GridBagLayout());
       //panelDesc.setPreferredSize(new Dimension(200, 200));

       setUpUI();
       doFrameConfig();
   }

   private void setUpUI(){
       Font titleFont=new Font(null, Font.BOLD, 13);
       Font regularFont=new Font(null, Font.PLAIN, 13);
       Font miniFont=new Font(null, Font.PLAIN, 8);

       this.getContentPane().setLayout(new GridBagLayout());

       GridBagConstraints c=new GridBagConstraints();
       
       JLabel labelSetup=new JLabel("Connection Settings");
       labelSetup.setFont(titleFont);
       c.gridx=0;
       c.gridy=0;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(10, 10, 10, 0);
       c.gridwidth=2;
       panelSetup.add(labelSetup,c);

       JLabel labelPacsUrl=new JLabel("PACS url");
       labelPacsUrl.setFont(regularFont);
       c.gridx=0;
       c.gridy=1;
       c.gridwidth=1;
       c.insets=new Insets(0, 10, 10, 0);
       c.anchor=GridBagConstraints.NORTHWEST;
       panelSetup.add(labelPacsUrl,c);

       textUrl=new JTextField(15);
       textUrl.setText("168.176.61.86");
       c.gridx=1;
       c.gridy=1;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 10, 10, 0);
       panelSetup.add(textUrl,c);

       JLabel labelPacsPort=new JLabel("PACS Port");
       labelPacsPort.setFont(regularFont);
       c.gridx=0;
       c.gridy=2;
       c.anchor=GridBagConstraints.NORTHWEST;
       panelSetup.add(labelPacsPort,c);

       textPort=new JTextField(4);
       textPort.setText("11112");
       c.gridx=1;
       c.gridy=2;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 10, 10, 0);
       panelSetup.add(textPort,c);

       JLabel labelCalledAETittle=new JLabel("Called AE ");
       
       labelCalledAETittle.setFont(regularFont);
       c.gridx=2;
       c.gridy=1;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 20, 10, 0);
       panelSetup.add(labelCalledAETittle,c);

       textCalledAETittle=new JTextField(15);
       textCalledAETittle.setText("DCM4CHEE");
       c.gridx=3;
       c.gridy=1;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 10, 10, 0);
       panelSetup.add(textCalledAETittle,c);

       JLabel labelCallingAETittle=new JLabel("Calling AE ");
       labelCallingAETittle.setFont(regularFont);
       c.gridx=2;
       c.gridy=2;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 20, 10, 20);
       panelSetup.add(labelCallingAETittle,c);

       textCallingAETittle=new JTextField(15);
       textCallingAETittle.setText("JNUKAK3D");
       c.gridx=3;
       c.gridy=2;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 10, 10, 20);
       panelSetup.add(textCallingAETittle,c);

       JButton buttonConnect=new JButton("Connect");
       buttonConnect.addActionListener(this);
       buttonConnect.setActionCommand(stablishConnection);
       c.gridx=0;
       c.gridy=3;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(0, 10, 0, 20);
       panelSetup.add(buttonConnect,c);

       labelErrorMessage=new JLabel("");
       labelErrorMessage.setFont(titleFont);
       labelErrorMessage.setForeground(Color.RED);
       c.gridx=1;
       c.gridy=3;
       c.gridwidth=3;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.insets=new Insets(5, 10, 0, 20);
       panelSetup.add(labelErrorMessage,c);

       c.gridx=0;
       c.gridy=0;
       c.gridwidth=2;
       this.getContentPane().add(panelSetup, c);

       panelAuxtree=new JPanel(new GridBagLayout());

       JLabel labelPacsContent=new JLabel("Pacs Explorer");
       labelPacsContent.setFont(titleFont);
       c.gridx=0;
       c.gridy=0;
       c.anchor=GridBagConstraints.NORTHWEST;
       c.gridwidth=1;
       c.insets=new Insets(10, 0, 0, 0);
       panelAuxtree.add(labelPacsContent,c);

       //panelAuxtree.add(tree,c);
       //panelAuxtree.setPreferredSize(new Dimension(250, 350));
       
       panelJtree=new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
       panelJtree.setPreferredSize(treePanelDimension);
       c.gridy=1;
       c.gridx=0;
       c.insets=new Insets(5, 0, 0, 0);
       c.anchor=GridBagConstraints.NORTHWEST;
       panelAuxtree.add(panelJtree,c);

       c.gridy=1;
       c.gridx=0;
       c.insets=new Insets(10, 20, 10, 0);
       this.getContentPane().add(panelAuxtree, c);

       JLabel labelDescripcion=new JLabel("Study Description");
       labelPacsContent.setFont(titleFont);
       c.gridx=0;
       c.gridy=0;
       c.insets=new Insets(10, 10, 0, 0);
       c.anchor=GridBagConstraints.CENTER;
       c.gridwidth=1;
       panelDesc.add(labelDescripcion,c);

       JLabel labelPatient=new JLabel("Patient ID:");
       labelPatient.setFont(regularFont);
       c.gridx=0;
       c.gridy=1;
       c.gridwidth=1;
       c.insets=new Insets(10, 10, 0, 0);
       c.anchor=GridBagConstraints.NORTHWEST;
       panelDesc.add(labelPatient,c);

       labelPatientValue=new JLabel("");
       labelPatientValue.setFont(regularFont);
       c.gridx=0;
       c.gridy=2;
       c.insets=new Insets(10, 15, 0, 0);
       panelDesc.add(labelPatientValue,c);

       JLabel labelStudyID=new JLabel("Study UID:");
       labelStudyID.setFont(regularFont);
       c.gridx=0;
       c.gridy=3;
       c.insets=new Insets(10, 10, 0, 0);
       panelDesc.add(labelStudyID,c);

       labelStudyIDValue=new JLabel("");
       labelStudyIDValue.setFont(miniFont);
       //labelStudyIDValue.setPreferredSize(new Dimension(100, 10));
       c.gridx=0;
       c.gridy=4;
       c.anchor=GridBagConstraints.WEST;
       c.insets=new Insets(10, 15, 0, 0);
       panelDesc.add(labelStudyIDValue,c);

       JLabel labelStudyType=new JLabel("Study Type:");
       labelStudyType.setFont(regularFont);
       c.gridx=0;
       c.gridy=5;
       c.insets=new Insets(10, 10, 0, 0);
       panelDesc.add(labelStudyType,c);

       labelStudyTypeValue=new JLabel("");
       labelStudyTypeValue.setFont(regularFont);
       c.gridx=0;
       c.gridy=6;
       c.insets=new Insets(10, 15, 0, 0);
       panelDesc.add(labelStudyTypeValue,c);

      /* JLabel labelBodyPart=new JLabel("Body Part Examined:");
       labelBodyPart.setFont(regularFont);
       c.gridx=0;
       c.gridy=7;
       c.insets=new Insets(10, 10, 0, 0);
       panelDesc.add(labelBodyPart,c);*/

       JLabel labelSeriesUID=new JLabel("Series UID");
       labelSeriesUID.setFont(regularFont);
       c.gridx=0;
       c.gridy=8;
       c.insets=new Insets(10, 10, 0, 0);
       panelDesc.add(labelSeriesUID,c);

       labelSeriesUIDValue=new JLabel("");
       labelSeriesUIDValue.setFont(miniFont);
       c.gridx=0;
       c.gridy=9;
       c.insets=new Insets(10, 15, 0, 0);
       panelDesc.add(labelSeriesUIDValue,c);

       buttonOpen=new JButton("Download and Visualize Study");
       buttonOpen.setEnabled(false);
       buttonOpen.addActionListener(this);
       buttonOpen.setActionCommand(openWithNukak);
       c.gridx=0;
       c.gridy=10;
       c.insets=new Insets(20, 10, 0, 0);
       c.anchor=GridBagConstraints.NORTHWEST;
       panelDesc.add(buttonOpen,c);

       c.gridx=1;
       c.gridy=1;
       c.insets=new Insets(0, 10, 0, 0);
       c.anchor=GridBagConstraints.NORTHWEST;
       this.getContentPane().add(panelDesc, c);

       buttonClose=new JButton("Close");
       buttonClose.addActionListener(this);
       buttonClose.setActionCommand(closeBrowser);
       c.gridx=1;
       c.gridy=2;
       c.insets=new Insets(0, 0, 5, 25);
       c.anchor=GridBagConstraints.NORTHEAST;
       this.getContentPane().add(buttonClose,c);
   }

    public void actionPerformed(ActionEvent e) {
       if(e.getActionCommand().equals(stablishConnection)){
            labelErrorMessage.setText("");

            boolean errorOcurred=false;
            nkPacsQuerier querier=new nkPacsQuerier();
            QueryTreeModel Qtree=null;
            try{
                this.cn=new nkConnectionSettings(this.textUrl.getText(),  new Integer(this.textPort.getText()), this.textCalledAETittle.getText(),this.textCallingAETittle.getText());
                Qtree=querier.queryPacsContents(cn);
                //Qtree=querier.queryPacsContents(this.textUrl.getText(),  new Integer(this.textPort.getText()),this.textCalledAETittle.getText(), this.textCallingAETittle.getText());
                QueryTreeRecord qtr=(QueryTreeRecord)Qtree.getRoot();
                if(qtr.children()==null)
                   throw new DicomNetworkException("None");
            }catch(DicomException de){
                labelErrorMessage.setText("Unable to query specified PACS, check connection settings");
                errorOcurred=true;
            }catch(DicomNetworkException dne){
                labelErrorMessage.setText("Unable to query specified PACS, check connection settings");
                errorOcurred=true;
            }catch(IOException ioe){
                labelErrorMessage.setText("Unable to query specified PACS, check connection settings");
                errorOcurred=true;
            }catch (NumberFormatException nfe){
                labelErrorMessage.setText("Unable to query specified PACS, check connection settings");
                errorOcurred=true;
            }

            if(!errorOcurred)
                updateTree(Qtree);
            else
                updateTree(null);
            
        }else if(e.getActionCommand().equals(openWithNukak)){
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select Local Directory To Store Study");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showOpenDialog(this);

            //this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try{
                    //this.setEnabled(false);
                    nkPacsQuerier.retriveStudyFromPacs(file,this.selectedStudyUID,this.cn);
                }catch(DicomNetworkException dne){
                    labelErrorMessage.setText("Unable to retrive specified study");
                }catch(IOException ioe){
                    labelErrorMessage.setText("Unable to retrive specified study");
                    ioe.printStackTrace();
                }catch(DicomException de){
                    labelErrorMessage.setText("Unable to retrive specified study");
                    de.printStackTrace();
                }

                Wizard nkw = new Wizard();
                
                nkw.addWizardListener(this);
                nkWizardPage3 nkwp3=new nkWizardPage3(file);
                nkwp3.setDir(file);

                nkw.start(nkwp3);

                wizardFrame=new JFrame("test");
                wizardFrame.getContentPane().add(nkw);
                wizardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                wizardFrame.pack();
                wizardFrame.setVisible(true);

              
                this.dispose();
            }else {
                System.out.println("Y aqui Que?");
            }

        }else if(e.getActionCommand().equals(closeBrowser)){
            this.dispose();
        }else
             System.out.println("Unknown Action Command "+e.getActionCommand());
    }

    public void valueChanged(TreeSelectionEvent e) {
        this.buttonOpen.setEnabled(false);
        QueryTreeRecord qtr=(QueryTreeRecord)tree.getLastSelectedPathComponent();
        if(qtr.getParent()!=null){
            updateDescriptionPanelWithAttributeList(qtr);
            if(qtr.getInformationEntity().getValue()==studyInfomationEntity)
                this.buttonOpen.setEnabled(true);
        }        
    }

    private void updateDescriptionPanelWithAttributeList(QueryTreeRecord qtr){
        labelPatientValue.setText("");
        labelStudyIDValue.setText("");
        labelSeriesUIDValue.setText("");

        //System.out.println(qtr.getInformationEntity().getValue());
        AttributeList al=qtr.getAllAttributesReturnedInIdentifier();
        
        if(qtr.getInformationEntity().getValue()==studyInfomationEntity){
            String s1=Attribute.getStringValues(al.get(TagFromName.PatientID))[0];
            labelPatientValue.setText(s1);
            String s2=Attribute.getStringValues(al.get(TagFromName.StudyInstanceUID))[0];
            labelStudyIDValue.setText(s2);
            System.out.println(s2);
            this.selectedStudyUID=s2;
        }else if(qtr.getInformationEntity().getValue()==seriesInfomationEntity){
            QueryTreeRecord qtrParent=(QueryTreeRecord)qtr.getParent();
            AttributeList al2=qtrParent.getAllAttributesReturnedInIdentifier();

            String s1=Attribute.getStringValues(al2.get(TagFromName.PatientID))[0];
            labelPatientValue.setText(s1);
            String s2=Attribute.getStringValues(al2.get(TagFromName.StudyInstanceUID))[0];
            labelStudyIDValue.setText(s2);
            
            String s3=Attribute.getStringValues(al.get(TagFromName.SeriesInstanceUID))[0];
            labelSeriesUIDValue.setText(s3);
        }else
            System.out.println("Not supossed to handle information entity "+qtr.getInformationEntity().getValue());
        
    }

    private void updateTree(QueryTreeModel qtm){
        //TODO there should be a better way
        if(qtm==null){
            tree=new JTree(defaultNode);
            tree.revalidate();
        }else{
            tree=new JTree(qtm);
            tree.revalidate();
        }
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.addTreeSelectionListener(this);

        panelAuxtree.remove(panelJtree);

        panelJtree=new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelJtree.setPreferredSize(treePanelDimension);

        GridBagConstraints c=new GridBagConstraints();
        c.gridy=1;
        c.gridx=0;
        c.insets=new Insets(5, 0, 0, 0);
        c.anchor=GridBagConstraints.NORTHWEST;
        panelAuxtree.add(panelJtree,c);
        panelAuxtree.revalidate();
        panelAuxtree.repaint();
        //this.pack();
    }


    final synchronized public void removenkEventListener (nkEventListener pl) {
        if( null != prv_nkEvent_listeners && null != pl)
	    prv_nkEvent_listeners.removeElement( pl);
    }

    final synchronized public void addnkEventListener (nkEventListener pl) {
        if( null == prv_nkEvent_listeners)
        prv_nkEvent_listeners = new Vector();
        if( null == pl || prv_nkEvent_listeners.contains( pl))
            return;
        prv_nkEvent_listeners.addElement( pl);
    }

    public void wizardPanelChanged(Wizard wizard) {
        System.out.println("wizard new panel");
    }

    public void wizardCancelled(Wizard wizard) {
        dispose();
    }

    public void wizardFinished(Wizard wizard) {
        firenkMenuEvent(wizard);
        wizardFrame.dispose();
        dispose();
    }

    public void firenkMenuEvent(Wizard wizard){
        if( null == prv_nkEvent_listeners) return;

        nkDicomNodeTree nodeSelect;
        nodeSelect = ((nkDicomNodeTree)(wizard.getWizardContext().getAttribute("nkDicomNodeTreeSelected")));
        nkDicomImport nkio;
        nkio = ((nkDicomImport)(wizard.getWizardContext().getAttribute("nkDicomImport")));
        if(nkio == null || nodeSelect == null) return;
        nkEvent e = new nkEvent(this);
        e.setAttribute("nkDicomNodeTreeSelected", nodeSelect);
        e.setAttribute("nkDicomImportSelected", nkio);
        for( int i= 0; i < prv_nkEvent_listeners.size(); ++i)
            ((nkEventListener)prv_nkEvent_listeners.elementAt( i)).nkEventInvoke(e);
    }

   private void doFrameConfig(){
       this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       this.pack();
       this.setLocationRelativeTo(null);
       //this.setVisible(true);
   }
}


