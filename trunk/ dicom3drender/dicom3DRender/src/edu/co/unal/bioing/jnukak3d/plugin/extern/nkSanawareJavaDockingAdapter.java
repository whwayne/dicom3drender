/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.co.unal.bioing.jnukak3d.plugin.extern;

import com.javadocking.DockingManager;
import com.javadocking.dock.Position;
import com.javadocking.dock.SingleDock;
import com.javadocking.dock.SplitDock;
import com.javadocking.dockable.DefaultDockable;
import com.javadocking.dockable.Dockable;
import com.javadocking.dockable.DockableState;
import com.javadocking.dockable.StateActionDockable;
import com.javadocking.dockable.action.DefaultDockableStateActionFactory;
import com.javadocking.model.FloatDockModel;
import com.javadocking.visualizer.FloatExternalizer;
import com.javadocking.visualizer.LineMinimizer;
import com.javadocking.visualizer.SingleMaximizer;
import edu.co.unal.bioing.jnukak3d.nkDebug;
import edu.co.unal.bioing.jnukak3d.plugin.nkDockManager;
import edu.co.unal.bioing.jnukak3d.ui.nkBinaryTreeLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author apinzonf
 */
public class nkSanawareJavaDockingAdapter extends nkDockManager{
    FloatDockModel dockModel;
    SplitDock dock;
    JFrame m_frame;
    JPanel mainPanel;

    Container content;

    nkBinaryTreeLayout treeLayout;

    public nkSanawareJavaDockingAdapter() {
    }

    public void readTree(nkBinaryTreeLayout root){
        treeLayout = root;
    }

    private SplitDock processLeaf(nkBinaryTreeLayout node){
        if(node == null) return null;
        SplitDock sd = new SplitDock();
        if(node.getNodeType() == nkBinaryTreeLayout.NODE_LEAF){
            Dockable m_dock = new DefaultDockable(node.getTitle(), (Component)node.getContent(), node.getTitle(), node.getIcon(),node.getNodeType());
            SingleDock docksingle = new SingleDock();
            docksingle.addDockable( addActions(m_dock), SingleDock.SINGLE_POSITION);
            sd.addChildDock(docksingle, new Position(Position.CENTER));
            //sd.setDividerLocation((int)(((Component)node.getContent()).getPreferredSize().height*node.getWeight()));
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_HORIZONTAL){
            sd.addChildDock(processLeaf(node.getLeft()), new Position(Position.TOP));
            sd.addChildDock(processLeaf(node.getRight()), new Position(Position.BOTTOM));
            sd.setDividerLocation((int)(sd.getPreferredSize().height*node.getWeight()));
            
        }else if(node.getNodeType() == nkBinaryTreeLayout.NODE_VERTICAL){
            sd.addChildDock(processLeaf(node.getLeft()), new Position(Position.LEFT));
            sd.addChildDock(processLeaf(node.getRight()), new Position(Position.RIGHT));
            sd.setDividerLocation((int)(sd.getPreferredSize().height*node.getWeight()));
            
        }
        return sd;
    }

    @Override
    public void config(Object windowParent, Object panelContent) {
        content = (Container)panelContent;
        mainPanel = new JPanel(new BorderLayout());
        dockModel = new FloatDockModel();
        m_frame = (JFrame)windowParent;
        dockModel.addOwner("frame0" + m_frame.hashCode(), m_frame);
        DockingManager.setDockModel(dockModel);
    }

    @Override
    public void pack() {

        dock = processLeaf(treeLayout);
        dockModel.addRootDock("dock", dock, m_frame);

        FloatExternalizer externalizer = new FloatExternalizer(m_frame);
		dockModel.addVisualizer("externalizer", externalizer, m_frame);

        LineMinimizer minimizer = new LineMinimizer(dock);
		dockModel.addVisualizer("minimizer", minimizer, m_frame);

        SingleMaximizer maximizer = new SingleMaximizer(minimizer);
		dockModel.addVisualizer("maximizer", maximizer, m_frame);

        mainPanel.add(maximizer, BorderLayout.CENTER);

        //mainPanel.add(dock, BorderLayout.CENTER);

        //m_frame.getContentPane().setLayout(new BorderLayout());
        content.setLayout(new BorderLayout());
        //m_frame.getContentPane().add(mainPanel);
        content.add(mainPanel);


		// Add the split pane to the panel.
		//m_frame.getContentPane().add(dock, BorderLayout.CENTER);
    }

    private Dockable addActions(Dockable dockable){

		Dockable wrapper = new StateActionDockable(dockable, new DefaultDockableStateActionFactory(), new int[0]);
		int[] states = {DockableState.NORMAL, DockableState.MINIMIZED, DockableState.MAXIMIZED, DockableState.EXTERNALIZED};
		wrapper = new StateActionDockable(wrapper, new DefaultDockableStateActionFactory(), states);
		return wrapper;

	}
}
