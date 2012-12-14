/*
 *   Copyright (C) 2006  The Concord Consortium, Inc.,
 *   25 Love Lane, Concord, MA 01742
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

package org.concord.jmol;

import java.util.EventObject;

public class CommandEvent extends EventObject {

    public final static byte COMPILATION_SUCCESS=0;
    public final static byte COMPILATION_ERROR=1;
    
    private byte type=COMPILATION_SUCCESS;
    private String description;

    public CommandEvent(Object source, byte type, String description){
	super(source);
	this.type=type;
	this.description=description;
    }

    public byte getType(){
	return type;
    }

    public String getDescription(){
	return description;
    }

}
