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
 * Created on 26.02.2005
 */
package de.uos.fmt.musitech.data.media.image;


import java.net.URL;
import java.util.ArrayList;

/**
 * This is a TimedImage with Information
 * about the location of staffs to realize
 * Cursor 
 * 
 * @author Jan
 *
 */
public class TimedImageStaffPostion extends TimedImage {
    
    ArrayList staffPos = new ArrayList();
    
    /**
     * 
     */
    public TimedImageStaffPostion() {
    }
    
    /**
     * 
     */
    public TimedImageStaffPostion(URL url) {
        super(url);
    }
    
    /**
     * 
     */
    public TimedImageStaffPostion(URL url, long time) {
        super(url, time);
    }
    
    /**
     * 
     */
    public TimedImageStaffPostion(URL url, long time, StaffPosition[] staffPositions) {
        this(url, time);
        addStaffPosition(staffPositions);
    }
    
    /**
     * 
     */
    public TimedImageStaffPostion(URL url, long time, StaffPosition staffPositions) {
        this(url, time);
        addStaffPosition(staffPositions);
    }
    
    /**
     * 
     */
    public TimedImageStaffPostion(URL url, long time, ArrayList staffPositions) {
        this(url, time);
        this.staffPos = staffPositions;
    }
    
    public void addStaffPosition(StaffPosition staffPos){
        this.staffPos.add(staffPos);
        
    }
    
    public void addStaffPosition(StaffPosition[] staffPos){
        for (int i = 0; i < staffPos.length; i++) {
            addStaffPosition(staffPos[i]);
        }
        
    }
    
    public void removeStaffPosition(StaffPosition staffPos){
        this.staffPos.remove(staffPos);
    }
    
    public ArrayList getStaffPositions(){
        return staffPos;
    }
    
    public StaffPosition getStaffPositionAt(int index){
        return (StaffPosition) staffPos.get(index);
    }

}
