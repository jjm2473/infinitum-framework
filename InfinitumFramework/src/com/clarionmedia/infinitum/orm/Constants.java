/*
 * Copyright (c) 2012 Tyler Treat
 * 
 * This file is part of Infinitum Framework.
 *
 * Infinitum Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Infinitum Framework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Infinitum Framework.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.clarionmedia.infinitum.orm;

/**
 * <p>
 * Contains constants related to the Infinitum ORM.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 02/12/12
 */
public class Constants {

	public static enum PersistenceMode {
		Transient, Persistent
	}

	// Errors
	public static final String NO_PERSISTENT_FIELDS = "No persistent fields defined in '{?}'!";

}