package com.clarionmedia.infinitum.orm.sqlite.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.database.Cursor;

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.exception.InfinitumRuntimeException;
import com.clarionmedia.infinitum.orm.persistence.PersistencePolicy;
import com.clarionmedia.infinitum.orm.sql.SqlBuilder;
import com.xtremelabs.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SqliteCriteriaTest {

	private SqlBuilder mockSqlBuilder;
	private SqliteSession mockSqliteSession;
	private InfinitumContext mockInfinitumContext;
	private SqliteModelFactory mockSqliteModelFactory;
	private SqliteMapper mockSqliteMapper;
	private Class<Object> entityClass;
	private SqliteCriteria<Object> sqliteCriteria;
	private PersistencePolicy mockPersistencePolicy;
	private Cursor mockCursor;

	@Before
	public void setup() {
		entityClass = Object.class;
		mockSqliteSession = mock(SqliteSession.class);
		mockInfinitumContext = mock(InfinitumContext.class);
		mockSqlBuilder = mock(SqlBuilder.class);
		mockSqliteModelFactory = mock(SqliteModelFactory.class);
		mockSqliteMapper = mock(SqliteMapper.class);
		mockPersistencePolicy = mock(PersistencePolicy.class);
		when(mockInfinitumContext.getPersistencePolicy()).thenReturn(
				mockPersistencePolicy);
		when(mockPersistencePolicy.isPersistent(entityClass)).thenReturn(true);
		sqliteCriteria = new SqliteCriteria<Object>(mockInfinitumContext,
				entityClass, mockSqliteSession, mockSqliteModelFactory,
				mockSqlBuilder, mockSqliteMapper);
		mockCursor = mock(Cursor.class);
	}

	@Test
	public void testToSql() {
		// Setup
		String expected = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(expected);

		// Run
		String actual = sqliteCriteria.toSql();

		// Verify
		verify(mockSqlBuilder).createQuery(sqliteCriteria);
		assertEquals("Returned query should match expected value", expected,
				actual);
	}

	@Test
	public void testList_noResults() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		when(mockCursor.getCount()).thenReturn(0);

		// Run
		List<Object> actual = sqliteCriteria.list();

		// Verify
		verify(mockSqliteSession).executeForResult(query, true);
		verify(mockCursor).getCount();
		verify(mockCursor).close();
		assertEquals("Returned list should be empty", 0, actual.size());
	}

	@Test
	public void testList_results() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		final int RESULT_COUNT = 3;
		when(mockCursor.getCount()).thenReturn(RESULT_COUNT);
		when(mockCursor.moveToNext()).thenReturn(true).thenReturn(true)
				.thenReturn(true).thenReturn(false);
		when(mockSqliteModelFactory.createFromCursor(mockCursor, entityClass))
				.thenReturn(new Object());

		// Run
		List<Object> actual = sqliteCriteria.list();

		// Verify
		verify(mockSqliteSession).executeForResult(query, true);
		verify(mockCursor).getCount();
		verify(mockCursor).close();
		verify(mockCursor, times(4)).moveToNext();
		verify(mockSqliteModelFactory, times(3)).createFromCursor(mockCursor,
				entityClass);
		verify(mockSqliteSession, times(3)).cache(any(Integer.class),
				any(Object.class));
		assertEquals("Returned list should be empty", RESULT_COUNT,
				actual.size());
	}

	@Test
	public void testUnique_noResult() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		when(mockCursor.getCount()).thenReturn(0);

		// Run
		Object actual = sqliteCriteria.unique();

		// Verify
		verify(mockSqliteSession).executeForResult(query, true);
		verify(mockCursor, times(2)).getCount();
		verify(mockCursor).close();
		assertNull("Returned result should be null", actual);
	}

	@Test
	public void testUnique_result() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		when(mockCursor.getCount()).thenReturn(1);
		when(mockSqliteModelFactory.createFromCursor(mockCursor, entityClass))
				.thenReturn(new Object());

		// Run
		Object actual = sqliteCriteria.unique();

		// Verify
		verify(mockSqliteSession).executeForResult(query, true);
		verify(mockCursor, times(2)).getCount();
		verify(mockCursor).close();
		assertNotNull("Returned result should not be null", actual);
	}

	@Test(expected = InfinitumRuntimeException.class)
	public void testUnique_noUnique() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		when(mockCursor.getCount()).thenReturn(3);
		when(mockSqliteModelFactory.createFromCursor(mockCursor, entityClass))
				.thenReturn(new Object());

		// Run
		sqliteCriteria.unique();

		// Verify
		assertTrue("Exception should have been thrown", false);
	}

	@Test
	public void testGetObjectMapper() {
		// Setup
		when(mockSqliteSession.getSqliteMapper()).thenReturn(mockSqliteMapper);

		// Run
		SqliteMapper actual = sqliteCriteria.getObjectMapper();

		// Verify
		verify(mockSqliteSession).getSqliteMapper();
		assertEquals("Returned result should match expected value",
				mockSqliteMapper, actual);
	}

	@Test
	public void testCount() {
		// Setup
		String query = "SQL criteria query";
		when(mockSqlBuilder.createCountQuery(sqliteCriteria)).thenReturn(query);
		when(mockSqliteSession.executeForResult(query, true)).thenReturn(
				mockCursor);
		when(mockCursor.moveToFirst()).thenReturn(true);
		final long EXPECTED = 5;
		when(mockCursor.getLong(0)).thenReturn(EXPECTED);
		
		// Run
		long actual = sqliteCriteria.count();
		
		// Verify
		verify(mockSqliteSession).executeForResult(query, true);
		verify(mockSqlBuilder).createCountQuery(sqliteCriteria);
		verify(mockCursor).moveToFirst();
		verify(mockCursor).getLong(0);
		verify(mockCursor).close();
		assertEquals("Returned result should match expected value", EXPECTED, actual);
	}

}
