/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2010 Zimbra, Inc.
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

com_zimbra_example_simplejspajaxget_HandlerObject = function() {
};
com_zimbra_example_simplejspajaxget_HandlerObject.prototype = new ZmZimletBase;
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.constructor = com_zimbra_example_simplejspajaxget_HandlerObject;

/**
 * Double clicked.
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Single clicked.
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype.singleClicked =
function() {
	this._displayDialog();
};

/**
 * Displays the zimlet jsp page.
 * 
 */
com_zimbra_example_simplejspajaxget_HandlerObject.prototype._displayDialog = 
function() {
	
	var jspUrl = this.getResource("jspfile.jsp");

	var response = AjxRpc.invoke(null, jspUrl, null, null, true);

	if (response.success == true) {
		appCtxt.getAppController().setStatusMsg(response.text);		
	}
	
};

