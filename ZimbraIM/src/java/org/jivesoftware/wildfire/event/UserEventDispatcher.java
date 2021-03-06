/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
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
package org.jivesoftware.wildfire.event;

import org.jivesoftware.util.Log;
import org.jivesoftware.wildfire.user.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Dispatches user events. Each event has a {@link EventType type}
 * and optional parameters, as follows:<p>
 *
 * <table border="1">
 * <tr><th>Event Type</th><th>Extra Params</th></tr>
 * <tr><td>{@link EventType#user_created user_created}</td><td><i>None</i></td></tr>
 * <tr><td>{@link EventType#user_deleting user_deleting}</td><td><i>None</i></td></tr>
 * <tr valign="top"><td>{@link EventType#user_modified user_modified}</td><td>
 * <table><tr><td><b>Reason</b></td><td><b>Key</b></td><td><b>Value</b></td></tr>
 *      <tr><td colspan="3">Name modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>nameModified</td></tr>
 *      <tr><td>&nbsp;</td><td>originalValue</td><td><i>(Name before it was modified)</i></td></tr>
 *
 *      <tr><td colspan="3"><hr></td></tr>
 *      <tr><td colspan="3">Email modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>emailModified</td></tr>
 *      <tr><td>&nbsp;</td><td>originalValue</td><td><i>(Email before it was
 * modified)</i></td></tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *
 *      <tr><td colspan="3">Password modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>passwordModified</td></tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *
 *      <tr><td colspan="3">Creation date modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>creationDateModified</td></tr>
 *      <tr><td>&nbsp;</td><td>originalValue</td><td><i>(Creation date before it was
 * modified)</i></td></tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *
 *      <tr><td colspan="3">Modification date modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>modificationDateModified</td></tr>
 *      <tr><td>&nbsp;</td><td>originalValue</td><td><i>(Modification date before it was
 * modified)</i></td></tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *
 *      <tr><td colspan="3">Property modified</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>propertyModified</td></tr>
 *      <tr><td>&nbsp;</td><td>propertyKey</td><td><i>(Name of the property)</i></td>
 * </tr>
 *      <tr><td>&nbsp;</td><td>originalValue</td><td><i>(Property value before it was
 * modified)</i></td></tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *      <tr><td colspan="3">Property added</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>propertyAdded</td></tr>
 *      <tr><td>&nbsp;</td><td>propertyKey</td><td><i>(Name of the new property)</i></td>
 * </tr>
 *      <tr><td colspan="3"><hr></td></tr>
 *      <tr><td colspan="3">Property deleted</td></tr>
 *      <tr><td>&nbsp;</td><td>type</td><td>propertyDeleted</td></tr>
 *      <tr><td>&nbsp;</td><td>propertyKey</td><td><i>(Name of the property deleted)</i></td></tr></table></td></tr>
 * </table>
 *
 * @author Matt Tucker
 */
public class UserEventDispatcher {

    private static List<UserEventListener> listeners =
            new CopyOnWriteArrayList<UserEventListener>();

    private UserEventDispatcher() {
        // Not instantiable.
    }

    /**
     * Registers a listener to receive events.
     *
     * @param listener the listener.
     */
    public static void addListener(UserEventListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        listeners.add(listener);
    }

    /**
     * Unregisters a listener to receive events.
     *
     * @param listener the listener.
     */
    public static void removeListener(UserEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Dispatches an event to all listeners.
     *
     * @param user the user.
     * @param eventType the event type.
     * @param params event parameters.
     */
    public static void dispatchEvent(User user, EventType eventType, Map params) {
        for (UserEventListener listener : listeners) {
            try {
                switch (eventType) {
                    case user_created: {
                        listener.userCreated(user, params);
                        break;
                    }
                    case user_deleting: {
                        listener.userDeleting(user, params);
                        break;
                    }
                    case user_modified: {
                        listener.userModified(user, params);
                        break;
                    }
                    default:
                        break;
                }
            }
            catch (Exception e) {
                Log.error(e);
            }
        }
    }

    /**
     * Represents valid event types.
     */
    public enum EventType {

        /**
         * A new user was created.
         */
        user_created,

        /**
         * A user is about to be deleted. Note that this event is fired before
         * a user is actually deleted. This allows for referential cleanup
         * before the user is gone.
         */
        user_deleting,

        /**
         * The name, email, password, or extended property of a user was changed.
         */
        user_modified,
    }
}