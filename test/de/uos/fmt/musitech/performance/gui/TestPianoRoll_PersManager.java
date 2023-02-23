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
/*
 * Created on 26.05.2005
 */
package de.uos.fmt.musitech.performance.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import de.uos.fmt.musitech.data.structure.container.BasicContainer;
import de.uos.fmt.musitech.data.structure.container.Container;
import de.uos.fmt.musitech.framework.persistence.PersistenceFactory;
import de.uos.fmt.musitech.framework.persistence.PersistenceManager;
import de.uos.fmt.musitech.framework.persistence.exceptions.PersistenceException;

/**
 * @author Jan
 *
 */
public class TestPianoRoll_PersManager {
    
    PersistenceManager persManager;
    Container cont1;
    Container cont2;
    Long idCont1;
    
    /**
     * 
     */
    public TestPianoRoll_PersManager() {
        init();
        createGUI();
       
    }
    
    /**
     * 
     */
    private void createGUI() {
        
        final PianoRollEditor editor1 = new PianoRollEditor();
        final PianoRollEditor editor2 = new PianoRollEditor();
        cont1 = new BasicContainer();
        cont2 = new BasicContainer();
        editor1.setContainer(cont1);
//        editor2.setContainer(cont2);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editor1, editor2);
        JPanel buttons = new JPanel();
        
        JButton update1 = new JButton("update 1");
        update1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    persManager.update(cont1);
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton update2 = new JButton("update 2");
        update2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    persManager.update(cont2);
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton store1 = new JButton("store 1");
        store1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    idCont1 = persManager.store(cont1);
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton store2 = new JButton("store 2");
        store2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    persManager.store(cont2);
                    
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton storebyName1 = new JButton("str by Name 1");
        storebyName1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    persManager.storeByName(cont1, "container");
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton storebyName2 = new JButton("str by Name 2");
        storebyName2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    persManager.storeByName(cont2, "container");
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton read2 = new JButton("read for 2");
        read2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                try {
                    cont2 = (Container) persManager.read(idCont1);
                    editor2.setContainer(cont2);
                } catch (PersistenceException e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton get1 = new JButton("get for 1");
        get1.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                    cont1 = (Container) persManager.getByName("container");
                    editor1.setContainer(cont1);
            }
        });
        JButton get2 = new JButton("get 'cont' for 2");
        get2.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                    cont2 = (Container) persManager.getByName("container");
                    editor2.setContainer(cont2);
            }
        });
        buttons.add(store1);
//        buttons.add(store2);
        buttons.add(storebyName1);
//        buttons.add(storebyName2);
//        buttons.add(get1);
        buttons.add(get2);
        buttons.add(read2);
        buttons.add(update1);
        buttons.add(update2);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(splitPane);
        frame.getContentPane().add(buttons, BorderLayout.SOUTH);
        frame.setBounds(0, 0, 900, 600);
        frame.show();
        
    }

    /**
     * 
     */
    private void init() {
        persManager = PersistenceFactory.getDefaultPersistenceManager();
    }

    public static void main(String[] args) {
        TestPianoRoll_PersManager test = new TestPianoRoll_PersManager();
    }

}
