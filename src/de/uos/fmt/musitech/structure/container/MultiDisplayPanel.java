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
import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.uos.fmt.musitech.data.MObject;
import de.uos.fmt.musitech.data.time.MetricTimeable;
import de.uos.fmt.musitech.data.time.Timeable;
import de.uos.fmt.musitech.framework.editor.DisplayTypeFilter;
import de.uos.fmt.musitech.framework.editor.EditorFactory;
import de.uos.fmt.musitech.framework.editor.EditorFactory.EditorConstructionException;
import de.uos.fmt.musitech.framework.time.ObjectPlayer;
import de.uos.fmt.musitech.framework.time.Player;

/**
 * This class displays a editor for a musical object. This object is responsible
 * for the used editor typ. This JPanel is subdivided in three parts: <BR>-
 * North: the name of the musical object (JTextField) <BR>- Center: the
 * appropriate editor (JTabbedPane) <BR>- South: a button to create a new
 * Window (JButton) <BR>
 * <P>
 * This class is used by the MusicTreeView (on the right-hand side).
 */
class MultiDisplayPanel extends JPanel {

    MObject mObject;

    JTabbedPane jTabbedPane = new JTabbedPane();

    JTextField textField = new JTextField();

    //JButton openWindowButton = new JButton("Open in New Window");
    
    DisplayTypeFilter filter;
    
    boolean player=true;
    boolean timeable=true;
    boolean metricTimeable=true;

    /**
     * Constructor of the class MultiDisplayPanel.
     */
    public MultiDisplayPanel() {
        setLayout(new BorderLayout());
        add(jTabbedPane, BorderLayout.CENTER);
        add(textField, BorderLayout.NORTH);
//        openWindowButton.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                String type = jTabbedPane.getTitleAt(jTabbedPane
//                        .getSelectedIndex());
//                JComponent display;
//                try {
//                    display = (JComponent) EditorFactory.createDisplay(mObject,
//                            null, type, null);
//                } catch (EditorConstructionException e1) {
//                    e1.printStackTrace();
//                    return;
//                }
//                JFrame frame;
//                if (display instanceof Editor) {
//                    Editor editor = (Editor) display;
//                    EditorWindow ew = new EditorWindow(textField.getText());
//                    ew.addEditor(editor);
//                    frame = ew;
//                } else {
//                    frame = new JFrame(textField.getText());
//                    frame.getContentPane().add(display);
//                }
//                frame.pack();
//                frame.setSize(400, 300);
//                frame.show();
//            }
//        });
//        openWindowButton.setEnabled(false);
        //        add(openWindowButton, BorderLayout.SOUTH);
    }

    /** the index of the tab selected before the current one. */
    int previousSelectedIndex = jTabbedPane.getSelectedIndex();

    /**
     * Method to set the displayed musical object and add a appropriate editor
     * to the jTabbedPane.
     * 
     * @param newObj
     *            MObject
     */
    public void setMObject(MObject newObj) {
        if (newObj == mObject)
            return;

        if (newObj == null) {
            jTabbedPane.removeAll();
            textField.setText("");
//            openWindowButton.setEnabled(false);
            return;
        }

        // Set the new musical object
        mObject = newObj;
        // Get appropriate editor types for the new musical object
//        String types[] = EditorFactory.getDisplayTypeNames(mObject.getClass());
        
        //Ergänzung 
        String[] allTypes = EditorFactory.getDisplayTypeNames(mObject.getClass());
        if (filter==null)
            filter = new DisplayTypeFilter();
        filter.setAllInCategory(false);
        String[] categories = new String[]{DisplayTypeFilter.CONTENT, DisplayTypeFilter.CLASS};
        Collection accepted = filter.getAcceptedObjects(allTypes, categories);
        if (accepted==null || accepted.size()==0){
            accepted = filter.getAcceptedObjects(allTypes, new String[]{DisplayTypeFilter.DEFAULT});
        }
        String[] types = new String[accepted.size()];
        int k=0;
        for (Iterator iter = accepted.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            types[k++] = element;
        }
//        String typesE[] = EditorFactory.getEditortypeNames(mObject, null);
        // Clear the jTabbedPane
        jTabbedPane.removeAll();
        jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
			public void stateChanged(ChangeEvent e) {
                //System.out.println("Selected index:
                // "+jTabbedPane.getSelectedIndex());
                registerAtPlayTimer();
            }
        });

        for (int i = 0; types != null && i < types.length; i++) {
            String type = types[i];
            if (types.length > 1 && type.equals("Object"))
                continue;
            JComponent display;
            try {
                display = (JComponent) EditorFactory.createDisplay(mObject,
                        types[i], false);
            } catch (EditorConstructionException e) {
                e.printStackTrace();
                continue;
            }
            // Add the new editor to the jTabbedPane
            // (mObject,ep,type);
            jTabbedPane.add(type, new JScrollPane(display));
        }
        //        JComponent display = null;
        //        try {
        //            display = (JComponent) EditorFactory.createEditor(mObject);
        //        } catch (EditorConstructionException e) {
        //            e.printStackTrace();
        //        }
        //        // Add the new editor to the jTabbedPane
        //        // (mObject,ep,type);
        //        if(display!=null)
        //            jTabbedPane.add("Default", new JScrollPane(display));
//        openWindowButton.setEnabled(true);
    }
    
    public void setFilter(DisplayTypeFilter filter){
        this.filter = filter;
    }
    
    protected void setPlayerOption(boolean player){
        this.player = player;
    }
    
    protected void setTimeableOption(boolean timeable){
        this.timeable = timeable;
    }
    
    protected void setMetricTimeableOption(boolean metricTimeable){
        this.metricTimeable = metricTimeable;
    }
    
    private Object selectedComp;

    /**
     * Registers the Component at the selected index of the
     * <code>jTabbedPane</code> at the PlayTimer of the ObjectPlayer if the
     * Component is a Player, Timeable or MetricTimeable. This allows to "play
     * back" the display currently being in the foreground.
     */
    void registerAtPlayTimer() {
        //unregister previously selected display
        if (selectedComp!=null && selectedComp instanceof Player){
            ObjectPlayer.getInstance().getPlayTimer().unRegisterPlayer((Player)selectedComp);
        }
        if (selectedComp!=null && selectedComp instanceof Timeable){
            ObjectPlayer.getInstance().getPlayTimer().unRegisterForPush((Timeable)selectedComp);
        }
        if (selectedComp!=null && selectedComp instanceof MetricTimeable){
            ObjectPlayer.getInstance().getPlayTimer().unRegisterMetricForPush((MetricTimeable)selectedComp);
        }
        //register display at selectedIndex at PlayTimer (if Player...)
        if (ObjectPlayer.getInstance().getPlayTimer() != null) {
            selectedComp = jTabbedPane.getSelectedComponent();
            //TODO verbessern?
            if (selectedComp instanceof JScrollPane){
                if (((JScrollPane)selectedComp).getViewport()!=null && ((JScrollPane)selectedComp).getViewport().getComponent(0)!=null){
                    selectedComp = ((JScrollPane)selectedComp).getViewport().getComponent(0);
                }
            }
            if (selectedComp instanceof Player && player) {
         
                ObjectPlayer.getInstance().getPlayTimer().registerPlayer(
                        (Player) selectedComp);
            }
            if (selectedComp instanceof Timeable && timeable) {
                ObjectPlayer.getInstance().getPlayTimer().registerForPush(
                        (Timeable) selectedComp);
            }
            if (selectedComp instanceof MetricTimeable && metricTimeable) {
                
                ObjectPlayer.getInstance().getPlayTimer()
                        .registerMetricForPush(
                                (MetricTimeable) selectedComp);
            }
            //unregister previously selected display at PlayTimer
            if (previousSelectedIndex >= 0) {
                Component previousSelected = jTabbedPane
                        .getComponentAt(previousSelectedIndex);
                if (previousSelected instanceof JScrollPane){
                    if (((JScrollPane)previousSelected).getViewport()!=null && ((JScrollPane)previousSelected).getViewport().getComponent(0)!=null){
                        previousSelected = ((JScrollPane)previousSelected).getViewport().getComponent(0);
                    }
                }
//                if (previousSelected instanceof Player) {
//                    ObjectPlayer.getInstance().getPlayTimer().unRegisterPlayer(
//                            (Player) previousSelected);
//                }
//                if (previousSelected instanceof Timeable) {
//                    ObjectPlayer.getInstance().getPlayTimer()
//                            .unRegisterForPush((Timeable) previousSelected);
//                }
//                if (previousSelected instanceof MetricTimeable) {
//                    ObjectPlayer.getInstance().getPlayTimer()
//                            .unRegisterMetricForPush(
//                                    (MetricTimeable) previousSelected);
//                }
            }
        }
        //update previousSelectedIndex
        previousSelectedIndex = jTabbedPane.getSelectedIndex();
    }

    /**
     * Method to set the title of the textField.
     * 
     * @param title
     *            String
     */
    public void setTitle(String title) {
        textField.setText(title);
    }

}