/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2004, 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on May 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.zimbra.cs.object;

/**
 * @author schemers
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MatchedObject {
	
	private ObjectHandler mHandler;
	private String mMatchedText;
	
	public MatchedObject(ObjectHandler handler, String matchedText) {
		mHandler = handler;
		mMatchedText = matchedText;
	}
	
	public ObjectHandler getHandler() {
		return mHandler;
	}
	
	public String getMatchedText() {
		return mMatchedText;
	}
	
	public String toString() {
		return "MatchedObject: {handler:"+mHandler+", matchedText:"+mMatchedText+"}";
	}
}
