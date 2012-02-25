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
 * This abstract class is used to define a relationship between two domain
 * models.
 * 
 * @author Tyler Treat
 * @version 1.0 02/25/12
 */
public abstract class ModelRelationship {

	public static enum RelationType {
		ManyToMany, ManyToOne, OneToMany, OneToOne
	};

	protected Class<?> mFirst;
	protected Class<?> mSecond;
	protected RelationType mRelationType;

	public Class<?> getFirstType() {
		return mFirst;
	}

	public void setFirstType(Class<?> first) {
		mFirst = first;
	}

	public Class<?> getSecondType() {
		return mSecond;
	}

	public void setSecondType(Class<?> second) {
		mSecond = second;
	}

	public boolean contains(Class<?> c) {
		return mFirst == c || mSecond == c;
	}

	public RelationType getRelationType() {
		return mRelationType;
	}

	public void setRelationType(RelationType type) {
		mRelationType = type;
	}

}