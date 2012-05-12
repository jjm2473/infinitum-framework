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

package com.clarionmedia.infinitum.orm.relationship;

import java.lang.reflect.Field;

import com.clarionmedia.infinitum.orm.annotation.OneToOne;
import com.clarionmedia.infinitum.reflection.PackageReflector;

/**
 * <p>
 * This class encapsulates a one-to-one relationship between two models.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 03/03/12
 */
public class OneToOneRelationship extends ForeignKeyRelationship {
	
	public OneToOneRelationship() {
		mRelationType = RelationType.OneToOne;
	}

	public OneToOneRelationship(Field f) {
		this();
		OneToOne oto = f.getAnnotation(OneToOne.class);
		mFirst = f.getDeclaringClass();
		mSecond = PackageReflector.getClass(oto.className());
		mName = oto.name();
		setColumn(oto.column());
		
	}

}
