/**********************************************

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see
<http://www.gnu.org/licenses/>.
In addition to the rights granted to the GNU General Public License,
you opt to use this program as specified in the following:

MUSITECH LINKING EXCEPTION

Linking this library statically or dynamically with other modules is making
a combined work based on this library. Thus, the terms and conditions of the
GNU General Public License cover the whole combination.

As a special exception, the copyright holders of this library give you permission
to link this library with independent modules to produce an executable, regardless
of the license terms of these independent modules, and to copy and distribute the
resulting executable under terms of your choice, provided that you also meet,
for each linked independent module, the terms and conditions of the license of
that module. An independent module is a module which is not derived from or based
on this library.

For the MUSITECH library, this exceptional permission described in the paragraph
above is subject to the following three conditions:
- If you modify this library, you must extend the GNU General Public License and
       this exception including these conditions to your version of the MUSITECH library.
- If you distribute a combined work with this library, you have to mention the
       MUSITECH project and link to its web site www.musitech.org in a location
       easily accessible to the users of the combined work (typically in the "About"
       section of the "Help" menu) and in any advertising material for the combined
       software.
- If you distribute a combined work with the MUSITECH library, you allow the MUSITECH
               project to use mention your combined work for promoting the MUSITECH project.
       For the purpose of this licence, 'distribution' includes the provision of software
       services (e.g. over the World Wide Web).

**********************************************/
package de.uos.fmt.musitech.structure.container;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.Named;
import de.uos.fmt.musitech.data.audio.AudioFileObject;
import de.uos.fmt.musitech.data.audio.AudioObject;
import de.uos.fmt.musitech.data.structure.MusicCollection;
import de.uos.fmt.musitech.data.structure.Note;
import de.uos.fmt.musitech.data.structure.Piece;
import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Containable;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.editor.DisplayTypeFilter;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.time.Locator;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.performance.midi.MidiReader;
import de.uos.fmt.musitech.utility.collection.TypedCollection;

/**
 * Use this class to display the structure of musical objects as a tree.
 * <P>
 * This JPanel contains a JSplitPane. On its left there is a JScrollPaneTree
 * containing a JTree and on its right there is a MulitDisplayPanel.
 * 
 * TODO make this implement Editor, write TestCase
 * 
 * @author Tillman Weyde
 */
public class MusicTreeView extends JPanel implements javax.swing.event.TreeSelectionListener {

    private String lineStyle = "Angled";

    private JSplitPane ivjJSplitPane = null;

    private JTree ivjJTree = null;

    MObject rootObject;

    MultiDisplayPanel displayPanel = new MultiDisplayPanel();

    private JScrollPane ivjJScrollPaneTree = null;

    private JPanel ivjJPanel = null;

    javax.swing.tree.TreeModel treeModel;

    private MusicTreeFilter filter = null;

    /**
     * Construtor of the class MusicTreeView.
     */
    public MusicTreeView() {
        super();
        initialize();
        initializeFilter();
    }

    /**
     * Sets the MusicTreeFilter which will be used with this MusicTreeView. It
     * is recommended to call this method before setting the MObject. <br>
     * The MusicTreeFilter determines which kinds of nodes are set for the
     * MObject's elements. TypedCollection elements will get a node with
     * children nodes if not specified differently in the filter. Elements other
     * than TypedCollection will get a node without children if not specified
     * differently in the filter. That is, the filter must specify which
     * subclasses of TypedCollection should get a simple node or no node instead
     * of a node with children and which other elements should get a node with
     * children or no node instead of a simple node.
     * 
     * @param filter MusicTreeFilter to be set as the <code>filter</code> of
     *            this MusicTreeView
     */
    public void setMusicTreeFilter(MusicTreeFilter filter) {
        this.filter = filter;
    }

    /**
     * This method creates a node object for the given MObject.
     * 
     * @param obj MObject The user obejct for the tree node.
     * @param parent DefaultMutableTreeNode The parent of the tree node to
     *            create.
     * @return DefaultMutableTreeNode The dreated tree node.
     */
    private DefaultMutableTreeNode createNodes(MObject obj, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode node = null;
        if (obj instanceof Piece) {
            node = createPieceNode((Piece) obj, parent);
            //        } else if (obj instanceof TypedCollection) {
            //            if (obj instanceof NotationChord)
            //                return null;
            //            if (!(obj instanceof NotationVoice))
            //                node = createContainerNodes(obj, parent);
            //        } else if (obj instanceof Named) {
            //            node = createSimpleNode(obj, ((Named) obj).getName());
            //        } else {
            //            node = createSimpleNode(obj, obj.getClass().getName());
            //        }

            //Änderung 07/12/04 (K.N.)
        } else {

            if (obj instanceof TypedCollection
                && !filter.isAcceptedObject(obj.getClass().getName(), MusicTreeFilter.SIMPLE_NODE)) {
                if (!filter.isAcceptedObject(obj.getClass().getName(), MusicTreeFilter.NO_NODE)) {
                    node = createContainerNodes(obj, parent);
                }
            } else {
                if (!filter.isAcceptedObject(obj.getClass().getName(), MusicTreeFilter.NO_NODE)) {
                    if (obj instanceof Named) {
                        node = createSimpleNode(obj, ((Named) obj).getName());
                    } else {
                        node = createSimpleNode(obj, obj.getClass().getName());
//                        node = createSimpleNode(obj, "Not named");	//for testing
                    }
                }
            }
        }
        //Ende Änderung 07/12/04 (K.N.)

        return node;
    }

    /**
     * This method creates a DefaultMutableTreeNode. The given name and
     * userObject will be add to the created node.
     * 
     * @return DefaultMutableTreeNode
     * @param userObject MObject
     * @param name String
     */
    private DefaultMutableTreeNode createSimpleNode(MObject userObject, String name) {

        if (userObject == null || filter.isAcceptedObject(userObject.getClass().getName(), MusicTreeFilter.NO_NODE))
            return null;

        DefaultMutableTreeNode node;
        if (name != null)
            node = new DefaultMutableTreeNode(name);
        else
            node = new DefaultMutableTreeNode();
        node.setUserObject(userObject);
        return node;
    }

    /**
     * This method creates a DefaultMutableTreeNode. The name, pools, TimeLine
     * and MetricalTimeLine of the given work object will be add to the created
     * node. be set to the created node.
     * 
     * @param piece The piece to create the node for.
     * @param parent The parent node.
     * @return DefaultMutableTreeNode The created node.
     */
    private DefaultMutableTreeNode createPieceNode(Piece piece, DefaultMutableTreeNode parent) {
        if (piece == null)
            return null;
        DefaultMutableTreeNode node = createSimpleNode(piece, piece.getName());
        //        node.add(createContainerNodes(piece.getNotePool(), node));
        // TODO: develop better concept for displaying metadata
        //        node.add(createContainerNodes((MObject)
        // piece.getMetaMap().get(piece),
        //                node));
//        DefaultMutableTreeNode nodes = createContainerNodes((MObject) piece.getMetaMap().get(piece), node);
//        if (nodes != null) {
//            node.add(nodes);
//        }
        DefaultMutableTreeNode nodesContainer = createContainerNodes(piece.getContainerPool(), node);
        if (nodesContainer!= null) {
            node.add(nodesContainer);
        }
        DefaultMutableTreeNode nodesSelections = createContainerNodes(piece.getSelectionPool(), node);
        if (nodesSelections!= null) {
            node.add(nodesSelections);
        }
        if (piece.getScore()!=null){
                DefaultMutableTreeNode nodesScore = createContainerNodes(piece.getScore(), node);
                if (nodesScore!=null){
                    node.add(nodesScore);
                }
        }
        DefaultMutableTreeNode nodesAudio = createContainerNodes(piece.getAudioPool(), node);
        if (nodesAudio!=null){
            node.add(nodesAudio);
        }
//        DefaultMutableTreeNode nodesTl = createContainerNodes(piece.getTimeLine(), node);
//        if (nodesTl!=null){
//            node.add(nodesTl);
//        }
        DefaultMutableTreeNode nodesMtl = createContainerNodes(piece.getMetricalTimeLine(), node);
        if (nodesMtl!=null){
            node.add(nodesMtl);
        }
        DefaultMutableTreeNode nodesHarm = createContainerNodes(piece.getHarmonyTrack(), node);
        if (nodesHarm!=null){
            node.add(nodesHarm);
        }
        return node;

    }

    /**
     * This method creates several DefaultMutableTreeNode if the given mObject
     * is a Container or TypedCollection. If mObject is just an instance of
     * Named only one node will be created.
     * <P>
     * Note: If there is a cycle (because mObject contains the parent) a node
     * called 'cycle' will be created.
     * 
     * @param mObject The container object to create the node from.
     * @param parent The parent node.
     * @return DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode createContainerNodes(MObject mObject, DefaultMutableTreeNode parent) {

        if (mObject == null || filter.isAcceptedObject(mObject.getClass().getName(), MusicTreeFilter.NO_NODE))
            return null;
        if (filter.isAcceptedObject(mObject.getClass().getName(), MusicTreeFilter.SIMPLE_NODE)) {
            if (mObject instanceof Named) {
                return createSimpleNode(mObject, ((Named) mObject).getName());
            } else {
                return createSimpleNode(mObject, null);
            }
        }

        Collection cont;
        if (mObject instanceof TypedCollection) {
            TypedCollection tc = (TypedCollection) mObject;
            cont = tc.getContent();
        } else if (mObject instanceof Collection) {
            cont = (Collection) mObject;
        } else
            return createSimpleNode(mObject, null);

        if (parent != null) {
            Object[] path = parent.getUserObjectPath();
            for (int i = 0; i < path.length; i++) {
                if (path[i] == cont) {

                    new Exception("Cycle found !").printStackTrace();

                    // bei Zyklus speziellen Displayknoten reinhängen
                    // Vielleicht mal mit schickem Icon...
                    return new DefaultMutableTreeNode("[Cycle]");
                }
            }
        }

        DefaultMutableTreeNode node;
        if (cont instanceof Named)
            node = createSimpleNode(mObject, ((Named) cont).getName());
        else
            node = createSimpleNode(mObject, null);
//            node = createSimpleNode(mObject, "Not named");	//for testing
        for (Iterator it = cont.iterator(); it.hasNext();) { // über Kinder von
            // cont
            // iterieren
            Object element = it.next();

            if (element instanceof Containable) {
                DefaultMutableTreeNode childNode = createNodes((Containable) element, node);
                if (childNode != null)
                    node.add(childNode);
            }
            //node.
            else
                ;
            //System.out.println("Nicht Containable:"+element.getClass());
        }
        return node;
    }

    //	private DefaultMutableTreeNode createNodes_x(Containable mObject,
    // DefaultMutableTreeNode parent) {
    //		DefaultMutableTreeNode node = null;
    //
    //		node = new DefaultMutableTreeNode("[Container- Bezeichnung]");
    //		node.setUserObject(mObject);
    //
    //
    //// DefaultMutableTreeNode notePoolNode =
    //// new DefaultMutableTreeNode(music.getNotePool());
    //// extendNode(notePoolNode);
    //// node.add(notePoolNode);
    //
    //
    //		return node;
    //	}

    //	/**
    //	 * Extends the nodes of collections and containers
    //	 * Creation date: (09.02.2002 18:56:31)
    //	 * @return int
    //	 */
    //	int extendNode(DefaultMutableTreeNode node) {
    //		Object userObj = node.getUserObject();
    //		if (userObj != null) {
    //			Iterator iter = null;
    //			if (userObj instanceof Collection) {
    //				iter = ((Collection) userObj).iterator();
    //				while (iter.hasNext()) {
    //					DefaultMutableTreeNode childNote =
    //						new DefaultMutableTreeNode(iter.next());
    //					node.add(childNote);
    //					extendNode(childNote);
    //				}
    //			}
    //		}
    //		return 1;
    //	}

    /**
     * Return the JPanel1 property value.
     * 
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanel() {
        if (ivjJPanel == null) {
            try {
                ivjJPanel = new javax.swing.JPanel();
                ivjJPanel.setName("JPanel");
                //				ivjJPanel.setPreferredSize(new java.awt.Dimension(200, 200));
                ivjJPanel.setLayout(new BorderLayout());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanel;
    }

    /**
     * Return the JScrollPane1 property value.
     * 
     * @return javax.swing.JScrollPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JScrollPane getJScrollPaneTree() {
        if (ivjJScrollPaneTree == null) {
            try {
                ivjJScrollPaneTree = new javax.swing.JScrollPane();
                ivjJScrollPaneTree.setName("JScrollPaneTree");
                ivjJScrollPaneTree.setOpaque(true);
                ivjJScrollPaneTree.setMinimumSize(new java.awt.Dimension(100, 50));
                getJScrollPaneTree().setViewportView(getJTree());
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJScrollPaneTree;
    }

    /**
     * Return the JSplitPane property value.
     * 
     * @return javax.swing.JSplitPane
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JSplitPane getJSplitPane() {
        if (ivjJSplitPane == null) {
            try {
                ivjJSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
                ivjJSplitPane.setName("JSplitPane");
                ivjJSplitPane.setLastDividerLocation(100);
                ivjJSplitPane.setBackground(new java.awt.Color(204, 204, 204));
                ivjJSplitPane.setForeground(new java.awt.Color(0, 0, 0));
                ivjJSplitPane.setDividerLocation(100);
                ivjJSplitPane.setFont(new java.awt.Font("Dialog", 0, 12));
                ivjJSplitPane.setMinimumSize(new java.awt.Dimension(200, 100));
                ivjJSplitPane.setContinuousLayout(false);
                getJSplitPane().add(getJScrollPaneTree(), "left");
                getJSplitPane().add(displayPanel, "right");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJSplitPane;
    }

    /**
     * Return the JTree1 property value.
     * 
     * @return javax.swing.JTree
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTree getJTree() {
        if (ivjJTree == null) {
            try {
                ivjJTree = new javax.swing.JTree();
                ivjJTree.setName("JTree");
                ivjJTree.setMaximumSize(new java.awt.Dimension(203, 54));
                ivjJTree.setForeground(new java.awt.Color(0, 0, 0));
                ivjJTree.setRowHeight(20);
                ivjJTree.setBounds(0, 0, 95, 389);
                // user code begin {1}
                ivjJTree.getSelectionModel()
                        .setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION);
                ivjJTree.getSelectionModel().addTreeSelectionListener(this);
                ivjJTree.putClientProperty("JTree.lineStyle", "Angled");

                //				DisplayInfo di =
                // DisplayInfoManager.getInstance().displayInfo(MObject.class);

                // XXX
                //ivjJTree.setCellRenderer(di);
                ivjJTree.setCellRenderer(EditorFactory.getTreeCellRenderer());

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJTree;
    }

    /**
     * This method returns the root object of the tree.
     * 
     * @return de.uos.fmt.musitech.mObject.Container
     */
    public MObject getMObject() {
        return rootObject;
    }

    /**
     * This method returns the tree model of the tree.
     * 
     * @return javax.swing.tree.TreeModel
     */
    public javax.swing.tree.TreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * Called whenever the part throws an exception.
     * 
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("MusicTree");
            setLayout(new java.awt.BorderLayout());
            setSize(200, 200);
            setMinimumSize(new java.awt.Dimension(112, 114));
            add(getJSplitPane(), "Center");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * Method to set the root object mObject.
     * 
     * @param mObject MObject
     */
    public void setMObject(MObject mObject) {
        this.rootObject = mObject;
        TreeNode root = createNodes(mObject, null);
        treeModel = new javax.swing.tree.DefaultTreeModel(root);
        getJTree().setModel(treeModel);
    }

    /**
     * Method to set the root object mObject.
     * 
     * @param mObject the object to set as root.
     * @param name the name of the object.
     */
    public void setMObject(MObject mObject, String name) {
        this.rootObject = mObject;
        TreeNode root = createNodes(mObject, null);
        treeModel = new javax.swing.tree.DefaultTreeModel(root);
        getJTree().setModel(treeModel);
    }

    /**
     * Method to set the tree model
     * 
     * @param newTreeModel javax.swing.tree.TreeModel
     */
    public void setTreeModel(javax.swing.tree.TreeModel newTreeModel) {
        treeModel = newTreeModel;
    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e the event that characterizes the change.
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {

        TreeSelectionModel treeModel = (TreeSelectionModel) e.getSource();
        TreePath path = (TreePath) treeModel.getSelectionPath();
        if (path == null)
            return;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object selectionObject = null;
        if (node != null)
            selectionObject = node.getUserObject();

        if (selectionObject instanceof MObject) {
            MObject mObject = (MObject) selectionObject;
            if (selectionObject instanceof Container) {
                ObjectPlayer.getInstance().removeAFOPlayer();
                ObjectPlayer.getInstance().setContainer((Container) selectionObject);
                if (((Container) selectionObject).getContext() != null) {
                    ObjectPlayer.getInstance().getPlayTimer().setContext(((Container) selectionObject).getContext());
                    //                    ObjectPlayer.getInstance().setContainerTime();
                    // //ersetzen, wenn locator in PlayTimer eingeführt
                    Locator startLocator = new Locator(((Container) selectionObject).getTime());
                    ObjectPlayer.getInstance().getPlayTimer().addLocator(startLocator);
                }
            } else {
                if (selectionObject instanceof Piece){
                    ObjectPlayer.getInstance().removeAFOPlayer();
                    Container container = new BasicContainer();
                    Container containerPool = ((Piece)selectionObject).getContainerPool();
                    if (containerPool!=null){
                        Collection contents = containerPool.getContentsRecursiveList(null);
                        for (Iterator iter = contents.iterator(); iter.hasNext();) {
                            Object element = (Object) iter.next();
                            if (element instanceof Note){
                                container.add(element);
                            }
                        }
                    }
                    ObjectPlayer.getInstance().setContainer(container);
                    if (container.getContext() != null) {
                        ObjectPlayer.getInstance().getPlayTimer().setContext(container.getContext());
                        Locator startLocator = new Locator(container.getTime());
                        ObjectPlayer.getInstance().getPlayTimer().addLocator(startLocator);
                    }             
                } else {
                    if (selectionObject instanceof AudioFileObject){
                        ObjectPlayer.getInstance().addAudioFileObject((AudioFileObject)selectionObject);
                        if (((AudioFileObject)selectionObject).getContext()!=null){
                            ObjectPlayer.getInstance().getPlayTimer().setContext(((AudioFileObject)selectionObject).getContext());
                            Locator startLocator = new Locator(((AudioFileObject)selectionObject).getTime());
                            ObjectPlayer.getInstance().getPlayTimer().addLocator(startLocator);
                        }
//                        ObjectPlayer.getInstance().setContainer(new BasicContainer()); CHECK what should happen here ?
                    }
                }
            }
            displayPanel.setMObject(mObject);
        } else {
            // XXX TimedMetrical liefert keines -> behoben
            System.out.println(getClass() + ".valueChanged: Kein DisplayObjekt gefunden für " + selectionObject + " .");
            displayPanel.setMObject(null);
        }

        if (selectionObject instanceof Named) {
            displayPanel.setTitle(((Named) selectionObject).getName());
        }
        displayPanel.validate();
    }

    /**
     * Just for testing.
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {

        // Create a WorkCollection
        MusicCollection workCollection = new MusicCollection();
        workCollection.setName("Test Collection");

        //		Work work = new Work();
        //		music.setSampleData();

        try {
            Piece work2 = null;
            work2 = new MidiReader().getPiece(new File("src/de/uos/fmt/musitech/score/DadmeAlbricias.mid").toURL());
            ;
            workCollection.add(work2);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        try {
            Piece work1 = new MidiReader().getPiece(new File("src/de/uos/fmt/musitech/bach.mid").toURL());
            workCollection.add(work1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Create a MusicTreeView and add the WorkCollection
        MusicTreeView musicTree = new MusicTreeView();
        musicTree.setMObject(workCollection, "test");

        // Create a JFrame and add the MusicTreeView
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(musicTree, BorderLayout.CENTER);

        frame.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        frame.setTitle("MusicTreeView");
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Sets <code>filter</code> to a MusicTreeFilter with default
     * registrations. (Cf. method <code>fillInRegistrations</code> in class
     * MusicTreeFilter.)
     */
    private void initializeFilter() {
        filter = new MusicTreeFilter();
        filter.fillInRegistrations();
    }
    
    public void setDisplayTypeFilter(DisplayTypeFilter filter){
        displayPanel.setFilter(filter);
    }
    
    public void setPlayTimerOptions(boolean player, boolean timeable, boolean metricTimeable){
        displayPanel.setPlayerOption(player);
        displayPanel.setTimeableOption(timeable);
        displayPanel.setMetricTimeableOption(metricTimeable);
    }
}