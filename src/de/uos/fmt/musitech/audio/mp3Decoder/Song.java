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
 * File Song.java
 * Created on 26.03.2004
 * 
 */
package de.uos.fmt.musitech.audio.mp3Decoder;


import java.io.File;
import java.util.Hashtable;

import de.uos.fmt.musitech.data.structure.Piece;


/**
 * Java class to store music content and metadata about a song.
 * 
 * @author Felix Bieﬂmann
 */
public class Song extends Piece
{
	
	/**
	 * Hashtable containing the metadata of the Song
	 */
	private Hashtable mv;	
	
	/**
	 * Default constructor
	 *
	 */
	public Song()
	{
	  this("","","","","","");	
	}
	
	/**
	 * Initialize a new Song object.
	 * @param path		Path of the Song
	 * @param title		the Songs Title
	 * @param interpret	the Interpret, might be a Band
	 * @param genre		the Songs Genre
	 * @param year		the Songs year of release
	 * @param desc		description text
	 */
	public Song (String path, String title, String interpret, 
				 String genre, String year, String desc) 
	{
	mv = new Hashtable();
	File tmp = new File(path);
	set("Path",tmp.getAbsolutePath());	
	set("Title",title);
	set("Interpret",interpret);
	set("Genre",genre);
	set("Year",year);
	set("Description",desc);
	set("Rating","-1");
	if(this.get("Title").equals("")){
		if(tmp.exists())
		this.set("Title",tmp.getName());
		else this.set("Title",path);
		
	}
	if(this.get("Year").equals(""))this.set("Year","0");
	}

	/**
	 * underspecified constructor 
	 * @param path of soundfile
	 */
	public Song(String filepath)
	{
	this(filepath,"","","","","");	
	}
	
/**
 * sets the specified key with its object 
 *  
 * @param key
 * @param o
 */
	public void set(String key,Object o){
		mv.put(key,o);
	}

	public String get(String key){
		return (String)mv.get(key);
	}
	/**
	 * Return a string representation of this object
	 */
	public String toString()
	{
	return "\""+this.get("Title")+"\""+" | "+this.get("Interpret");
	}
	
	public String getStringFromSong(){

		return  get("Path")+'\t'+get("Title")+'\t'+get("Interpret")+'\t'+
				get("Genre")+'\t'+get("Year")+'\t'+get("Description")+'\t'+
				get("Rating");
	}

	public Hashtable getMv(){
		return mv;
	}
	
	public void setMv(Hashtable ht){
		mv = ht;
	}
	
	public Song copy(){
			if(mv!= null && get("Path").endsWith("mp3")){
			Song ret = new Song(get("Path"),get("Title"),get("Interpret"),
					get("Genre"),get("Year"),get("Description"));
			ret.set("Rating","-1");
			return ret;
			}
			else return null;
	}
	
	/**
	 * TODO: problem here: when can two files be regarded as equal? 
	 * different recordings result in different files, hence different
	 * audioobjects 
	 * @param s
	 * @return true if the two songs share the same audiofile
	 */
	public boolean equals(Song s){
		if((s==null)){
			return false;
			}
		else{			
			return (this.mv.get("Path").equals(s.get("Path"))
				|| (this.mv.get("Title").equals(s.get("Title"))
				&& this.mv.get("Interpret").equals(s.get("Interpret"))
				&& this.mv.get("Genre").equals(s.get("Genre"))
				&& this.mv.get("Year").equals(s.get("Year"))));
		}
	}


}



