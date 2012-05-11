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

package com.clarionmedia.infinitum.orm.persistence;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clarionmedia.infinitum.exception.MapFileException;
import com.clarionmedia.infinitum.exception.InfinitumRuntimeException;
import com.clarionmedia.infinitum.orm.annotation.Unique;
import com.clarionmedia.infinitum.orm.exception.ModelConfigurationException;
import com.clarionmedia.infinitum.orm.relationship.ManyToManyRelationship;
import com.clarionmedia.infinitum.orm.relationship.ModelRelationship;
import com.clarionmedia.infinitum.reflection.ClassReflector;

/**
 * <p>
 * Provides a runtime resolution policy for model persistence based on the
 * Infinitum configuration. There are two types of persistence policies:
 * annotations and XML.
 * </p>
 * <p>
 * Domain classes should be individually registered in {@code infinitum.cfg.xml}
 * using {@code <model resource="com.foo.domain.MyModel" />} in the
 * {@code domain} element.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 05/09/12
 */
public abstract class PersistencePolicy {

	// This Map caches which fields are persistent
	protected Map<Class<?>, List<Field>> mPersistenceCache;

	// This Map caches the field-column map
	protected Map<Field, String> mColumnCache;

	// This Map caches the primary key Field for each persistent class
	protected Map<Class<?>, Field> mPrimaryKeyCache;

	// This Map caches the "nullability" of Fields
	protected Map<Field, Boolean> mFieldNullableCache;

	// This Map caches the uniqueness of Fields
	protected Map<Field, Boolean> mFieldUniqueCache;

	// This Set caches the many-to-many relationships
	protected Set<ManyToManyRelationship> mManyToManyCache;

	// This Map caches the lazy-loading status for each persistent class
	protected Map<Class<?>, Boolean> mLazyLoadingCache;

	// This Map caches the resource names for models
	protected Map<Class<?>, String> mRestResourceCache;

	// This Map caches the resource field names for model Fields
	protected Map<Field, String> mRestFieldCache;

	/**
	 * Constructs a new {@code PersistencePolicy}.
	 */
	public PersistencePolicy() {
		mPersistenceCache = new HashMap<Class<?>, List<Field>>();
		mColumnCache = new HashMap<Field, String>();
		mPrimaryKeyCache = new HashMap<Class<?>, Field>();
		mFieldNullableCache = new HashMap<Field, Boolean>();
		mFieldUniqueCache = new HashMap<Field, Boolean>();
		mManyToManyCache = new HashSet<ManyToManyRelationship>();
		mLazyLoadingCache = new HashMap<Class<?>, Boolean>();
		mRestResourceCache = new HashMap<Class<?>, String>();
		mRestFieldCache = new HashMap<Field, String>();
	}

	/**
	 * Indicates if the given {@code Class} is persistent or transient.
	 * 
	 * @param c
	 *            the {@code Class} to check persistence for
	 * @return {@code true} if persistent, {@code false} if transient
	 */
	public abstract boolean isPersistent(Class<?> c);

	/**
	 * Retrieves the name of the database table for the specified {@code Class}.
	 * If the {@code Class} is transient, this method will return {@code null}.
	 * 
	 * @param c
	 *            the {@code Class} to retrieve the table name for
	 * @return the name of the database table for the specified domain model
	 *         {@code Class}
	 * @throws IllegalArgumentException
	 *             if the given {@code Class} is transient
	 * @throws MapFileException
	 *             if the map file for the given {@code Class} is invalid
	 */
	public abstract String getModelTableName(Class<?> c)
			throws IllegalArgumentException, MapFileException;

	/**
	 * Retrieves a {@code List} of all persistent {@code Fields} for the given
	 * {@code Class}.
	 * 
	 * @param c
	 *            the <code>Class</code> to retrieve persistent
	 *            <code>Fields</code> for
	 * @return <code>List</code> of all persistent <code>Fields</code> for the
	 *         specified <code>Class</code>
	 */
	public abstract List<Field> getPersistentFields(Class<?> c);

	/**
	 * Finds the persistent {@link Field} for the given {@link Class} which has
	 * the specified name. Returns {@code null} if no such {@code Field} exists.
	 * 
	 * @param c
	 *            the {@code Class} containing the {@code Field}
	 * @param name
	 *            the name of the {@code Field} to retrieve
	 * @return {@code Field} with specified name
	 */
	public abstract Field findPersistentField(Class<?> c, String name);

	/**
	 * Retrieves the primary key {@code Field} for the given {@code Class}.
	 * 
	 * @param c
	 *            the {@code Class} to retrieve the primary key {@code Field}
	 *            for
	 * @return the primary key {@code Field} for the specified {@code Class}
	 * @throws ModelConfigurationException
	 *             if multiple primary keys are declared in {@code c}
	 */
	public abstract Field getPrimaryKeyField(Class<?> c)
			throws ModelConfigurationException;

	/**
	 * Retrieves a {@code List} of all unique {@code Fields} for the given
	 * {@code Class}. {@code Fields} can be marked unique using the
	 * {@link Unique} annotation.
	 * 
	 * @param c
	 *            the {@code Class} to retrieve unique {@code Fields} for
	 * @return {@code List} of all unique {@code Fields} for the specified
	 *         {@code Class}
	 */
	public abstract List<Field> getUniqueFields(Class<?> c);

	/**
	 * Retrieves the name of the database column the specified {@code Field}
	 * maps to.
	 * 
	 * @param f
	 *            the {@code Field} to retrieve the column for
	 * @return the name of the column
	 */
	public abstract String getFieldColumnName(Field f);

	/**
	 * Determines if the given {@link Field} is a primary key.
	 * 
	 * @param f
	 *            the {@code Field} to check
	 * @return {@code true} if it is a primary key, {@code false} if it's not
	 */
	public abstract boolean isFieldPrimaryKey(Field f);

	/**
	 * Determines if the given primary key {@link Field} is set to
	 * autoincrement. This method assumes, as a precondition, that the
	 * {@code Field} being passed is guaranteed to be a primary key, whether
	 * implicitly or explicitly.
	 * 
	 * @param f
	 *            the primary key {@code Field} to check if it's set to
	 *            autoincrement
	 * @return {@code true} if it is set to autoincrement, {@code false} if it's
	 *         not
	 * @throws InfinitumRuntimeException
	 *             if an explicit primary key that is set to autoincrement is
	 *             not of type int or long
	 */
	public abstract boolean isPrimaryKeyAutoIncrement(Field f)
			throws InfinitumRuntimeException;

	/**
	 * Checks if the specified {@code Field's} associated column is nullable.
	 * 
	 * @param f
	 *            the {@code Field} to check if nullable
	 * @return {@code true} if the field is nullable, {@code false} if it is not
	 *         nullable
	 */
	public abstract boolean isFieldNullable(Field f);

	/**
	 * Checks if the specified {@code Field} is unique, meaning each record must
	 * have a different value in the table. This is a way of implementing a
	 * unique constraint on a column.
	 * 
	 * @param f
	 *            the {@code Field} to check for uniqueness
	 * @return {@code true} if it is unique, {@code false} if not
	 */
	public abstract boolean isFieldUnique(Field f);

	/**
	 * Retrieves a {@link Set} of all {@link ManyToManyRelationship} instances
	 * for the given {@link Class}.
	 * 
	 * @param c
	 *            the {@code Class} to get relationships for
	 * @return {@code Set} of all many-to-many relationships
	 */
	public abstract Set<ManyToManyRelationship> getManyToManyRelationships(
			Class<?> c);

	/**
	 * Indicates if the given persistent {@link Class} has cascading enabled.
	 * 
	 * @param c
	 *            the {@code Class} to check for cascading
	 * @return {@code true} if it is cascading, {@code false} if not
	 */
	public abstract boolean isCascading(Class<?> c);

	/**
	 * Indicates if the primary key {@link Field} for the given model is 0 or
	 * {@code null}.
	 * 
	 * @param model
	 *            the model to check the primary key value for
	 * @return {@code true} if it is 0 or {@code null}, false if not
	 */
	public abstract boolean isPKNullOrZero(Object model);

	/**
	 * Indicates if the given persistent {@link Field} is part of an entity
	 * relationship, either many-to-many, many-to-one, one-to-many, or
	 * one-to-one.
	 * 
	 * @param f
	 *            the {@code Field} to check
	 * @return {@code true} if it is part of a relationship, {@code false} if
	 *         not
	 */
	public abstract boolean isRelationship(Field f);

	/**
	 * Retrieves the {@link ModelRelationship} the given {@link Field} is a part
	 * of.
	 * 
	 * @param f
	 *            the {@code Field} to retrieve the relationship for
	 * @return the {@code ModelRelationship} for {@code f} or {@code null} if
	 *         there is none
	 */
	public abstract ModelRelationship getRelationship(Field f);

	/**
	 * Retrieves the {@link Field} pertaining to the given
	 * {@link ModelRelationship} for the specified {@link Class}. If no such
	 * {@code Field} exists, {@code null} is returned.
	 * 
	 * @param c
	 *            the {@code Class} to retrieve the {@code Field} from
	 * @param rel
	 *            the {@code ModelRelationship} to retrieve the {@code Field}
	 *            for
	 * @return {@code Field} pertaining to the relationship or {@code null}
	 */
	public abstract Field findRelationshipField(Class<?> c,
			ModelRelationship rel);

	/**
	 * Indicates if the given persistent {@link Class} has lazy loading enabled
	 * or not.
	 * 
	 * @param c
	 *            the {@code Class} to check lazy-loading status
	 * @return {@code true} if lazy loading is enabled, {@code false} if not
	 */
	public abstract boolean isLazy(Class<?> c);

	/**
	 * Retrieves the RESTful resource name for the given persistent
	 * {@link Class}.
	 * 
	 * @param c
	 *            the {@code Class} to retrieve the RESTful resource name for
	 * @return resource name
	 * @throws IllegalArgumentException
	 *             if the given {@code Class} is not a domain model or
	 *             persistent
	 */
	public abstract String getRestfulResource(Class<?> c)
			throws IllegalArgumentException;

	/**
	 * Retrieves the RESTful resource field name for the given persistent
	 * {@link Field}.
	 * 
	 * @param f
	 *            the {@code Field} to retrieve the resource field name for
	 * @return resource field name
	 * @throws IllegalArgumentException
	 *             if the containing {@link Class} of the given {@code Field} is
	 *             transient or if the {@code Field} itself is marked transient
	 */
	public abstract String getResourceFieldName(Field f)
			throws IllegalArgumentException;

	/**
	 * Calculates a hash code for the specified persistent model based on its
	 * {@link Class} and primary key.
	 * 
	 * @param model
	 *            the model entity to compute the hash for
	 * @return hash code for the model
	 */
	public int computeModelHash(Object model) {
		final int PRIME = 31;
		int hash = 7;
		hash *= PRIME + model.getClass().hashCode();
		Field f = getPrimaryKeyField(model.getClass());
		f.setAccessible(true);
		Object o = null;
		try {
			o = f.get(model);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		if (o != null)
			hash *= PRIME + o.hashCode();
		return hash;
	}

	/**
	 * Retrieves the primary key value for the given persistent model.
	 * 
	 * @param model
	 *            the model to retrieve the primary key for
	 * @return primary key value
	 */
	public Object getPrimaryKey(Object model) {
		Object ret = null;
		Field pkField = getPrimaryKeyField(model.getClass());
		pkField.setAccessible(true);
		if (TypeResolution.isDomainProxy(model.getClass())) {
			// Need to invoke getter if it's a proxy
			ret = ClassReflector.invokeGetter(pkField, model);
		} else {
			try {
				ret = pkField.get(model);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 * Retrurns the many-to-many relationship cache.
	 * 
	 * @return many-to-many cache
	 */
	public Set<ManyToManyRelationship> getManyToManyCache() {
		return mManyToManyCache;
	}

	protected Field findPrimaryKeyField(Class<?> c) {
		List<Field> fields = getPersistentFields(c);
		for (Field f : fields) {
			if (f.getName().equals("mId") || f.getName().equals("mID")
					|| f.getName().equalsIgnoreCase("id"))
				return f;
		}
		return null;
	}

}