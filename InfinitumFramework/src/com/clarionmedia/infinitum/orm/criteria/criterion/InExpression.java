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

package com.clarionmedia.infinitum.orm.criteria.criterion;

import java.lang.reflect.Field;

import com.clarionmedia.infinitum.orm.criteria.Criteria;
import com.clarionmedia.infinitum.orm.criteria.CriteriaConstants;
import com.clarionmedia.infinitum.orm.exception.InvalidCriteriaException;
import com.clarionmedia.infinitum.orm.persistence.PersistenceResolution;
import com.clarionmedia.infinitum.orm.persistence.TypeResolution;
import com.clarionmedia.infinitum.orm.persistence.TypeResolution.SqliteDataType;
import com.clarionmedia.infinitum.orm.sql.SqlConstants;

/**
 * <p>
 * Represents a condition restraining a {@link Field} value to a specified set
 * of values.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 02/18/12
 */
public class InExpression extends Criterion {

	private static final long serialVersionUID = 1282172886230328002L;

	private Object[] mValues;

	/**
	 * Constructs a new {@code InExpression} with the given {@link Field} name
	 * and array of values.
	 * 
	 * @param fieldName
	 *            the name of the field to check value for
	 * @param values
	 *            the set of values to constrain the {@code Field} to
	 */
	public InExpression(String fieldName, Object[] values) {
		super(fieldName);
		mValues = values;
	}

	@Override
	public String toSql(Criteria<?> criteria) throws InvalidCriteriaException {
		StringBuilder query = new StringBuilder();
		Class<?> c = criteria.getEntityClass();
		Field f = null;
		try {
			f = PersistenceResolution.findPersistentField(c, mFieldName);
			if (f == null)
				throw new InvalidCriteriaException(String.format(CriteriaConstants.INVALID_CRITERIA, c.getName()));
			f.setAccessible(true);
		} catch (SecurityException e) {
			throw new InvalidCriteriaException(String.format(CriteriaConstants.INVALID_CRITERIA, c.getName()));
		}
		String colName = PersistenceResolution.getFieldColumnName(f);
		SqliteDataType sqlType = TypeResolution.getSqliteDataType(f);
		query.append(colName).append(' ').append(SqlConstants.OP_IN).append(" (");
		String prefix = "";
		for (Object val : mValues) {
			query.append(prefix);
			prefix = ", ";
			if (sqlType == SqliteDataType.TEXT)
				query.append("'").append(val.toString()).append("'");
			else
				query.append(val.toString());
		}
		query.append(')');
		return query.toString();
	}

}