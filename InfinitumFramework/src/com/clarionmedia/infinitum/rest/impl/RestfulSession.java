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

package com.clarionmedia.infinitum.rest.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.database.SQLException;

import com.clarionmedia.infinitum.context.ContextFactory;
import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.context.RestfulContext;
import com.clarionmedia.infinitum.exception.InfinitumRuntimeException;
import com.clarionmedia.infinitum.internal.Preconditions;
import com.clarionmedia.infinitum.internal.caching.LruCache;
import com.clarionmedia.infinitum.logging.Logger;
import com.clarionmedia.infinitum.orm.Session;
import com.clarionmedia.infinitum.orm.criteria.Criteria;
import com.clarionmedia.infinitum.orm.exception.SQLGrammarException;
import com.clarionmedia.infinitum.orm.persistence.PersistencePolicy;
import com.clarionmedia.infinitum.orm.persistence.TypeAdapter;
import com.clarionmedia.infinitum.rest.AuthenticationStrategy;
import com.clarionmedia.infinitum.rest.RestResponse;
import com.clarionmedia.infinitum.rest.RestfulClient;
import com.clarionmedia.infinitum.rest.RestfulMapper;
import com.clarionmedia.infinitum.rest.RestfulModelMap;

/**
 * <p>
 * {@link Session} implementation for communicating with a RESTful web service
 * using domain objects. Infinitum provides two concrete implementations called
 * {@link RestfulJsonClient}, which is used for web services that respond with
 * JSON, and {@link RestfulXmlClient}, which is used for web services that
 * respond with XML. These can be extended or re-implemented for specific
 * business needs.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 02/27/12
 * @since 1.0
 */
public abstract class RestfulSession implements Session {

	protected static final String ENCODING = "UTF-8";

	protected boolean mIsOpen;
	protected String mHost;
	protected boolean mIsAuthenticated;
	protected AuthenticationStrategy mAuthStrategy;
	protected Logger mLogger;
	protected RestfulMapper mMapper;
	protected PersistencePolicy mPersistencePolicy;
	protected LruCache<Integer, Object> mSessionCache;
	protected int mCacheSize;
	protected InfinitumContext mInfinitumContext;
	protected RestfulContext mRestContext;
	protected RestfulClient mRestClient;

	/**
	 * Creates a new {@code RestfulSession} with the given
	 * {@link InfinitumContext} and cache size.
	 * 
	 * @param context
	 *            the {@code InfinitumContext} of the {@code Session}
	 * @param cacheSize
	 *            the maximum number of {@code Objects} the {@code Session}
	 *            cache can store
	 */
	public RestfulSession() {
		mInfinitumContext = ContextFactory.newInstance().getContext();
		mLogger = Logger.getInstance(mInfinitumContext, getClass().getSimpleName());
		mPersistencePolicy = mInfinitumContext.getPersistencePolicy();
		mCacheSize = DEFAULT_CACHE_SIZE;
		mSessionCache = new LruCache<Integer, Object>(mCacheSize);
		mRestContext = mInfinitumContext.getRestfulConfiguration();
		mRestClient = new BasicRestfulClient(mInfinitumContext);
		mRestClient.setHttpParams(getHttpParams());
		mIsAuthenticated = mRestContext.isRestAuthenticated();
		mAuthStrategy = mRestContext.getAuthStrategy();
		mHost = mRestContext.getRestHost();
		if (!mHost.endsWith("/"))
			mHost += '/';
		switch (mRestContext.getMessageType()) {
		case Xml:
			mMapper = new RestfulXmlMapper(mInfinitumContext);
			break;
		case Json:
			mMapper = new RestfulJsonMapper(mInfinitumContext);
			break;
		default:
			mMapper = new RestfulNameValueMapper(mInfinitumContext);
		}
	}

	/**
	 * Returns an instance of the given persistent model {@link Class} as
	 * identified by the specified primary key or {@code null} if no such entity
	 * exists.
	 * 
	 * @param c
	 *            the {@code Class} of the persistent instance to load
	 * @param id
	 *            the primary key value of the persistent instance to load
	 * @return the persistent instance
	 */
	protected abstract <T> T loadEntity(Class<T> type, Serializable id);

	@Override
	public Session open() throws SQLException {
		mIsOpen = true;
		mLogger.debug("Session opened");
		return this;
	}

	@Override
	public Session close() {
		recycleCache();
		mIsOpen = false;
		mLogger.debug("Session closed");
		return this;
	}

	@Override
	public boolean isOpen() {
		return mIsOpen;
	}

	@Override
	public Session beginTransaction() {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public Session commit() {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public Session rollback() {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public boolean isTransactionOpen() {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public Session setAutocommit(boolean autocommit) {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public boolean isAutocommit() {
		throw new UnsupportedOperationException(
				"RestfulSession does not support transactions!");
	}

	@Override
	public Session recycleCache() {
		mSessionCache.clear();
		return this;
	}

	@Override
	public Session setCacheSize(int cacheSize) {
		mCacheSize = cacheSize;
		return this;
	}

	@Override
	public int getCacheSize() {
		return mCacheSize;
	}

	@Override
	public boolean cache(int hash, Object model) {
		if (mSessionCache.size() >= mCacheSize)
			return false;
		mSessionCache.put(hash, model);
		return true;
	}

	@Override
	public boolean checkCache(int hash) {
		return mSessionCache.containsKey(hash);
	}

	@Override
	public Object searchCache(int hash) {
		return mSessionCache.get(hash);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T load(Class<T> type, Serializable id)
			throws InfinitumRuntimeException, IllegalArgumentException {
		Preconditions.checkPersistenceForLoading(type, mPersistencePolicy);
		// TODO Validate primary key
		int objHash = mPersistencePolicy.computeModelHash(type, id);
		if (checkCache(objHash))
			return (T) searchCache(objHash);
		return loadEntity(type, id);
	}

	@Override
	public long save(Object model) {
		Preconditions.checkPersistenceForModify(model, mPersistencePolicy);
		mLogger.debug("Sending POST request to save entity");
		String uri = mHost
				+ mPersistencePolicy.getRestEndpoint(model.getClass());
		if (mIsAuthenticated && !mAuthStrategy.isHeader())
			uri += '?' + mAuthStrategy.getAuthenticationString();
		Map<String, String> headers = new HashMap<String, String>();
		if (mIsAuthenticated && mAuthStrategy.isHeader())
			headers.put(mAuthStrategy.getAuthenticationKey(),
					mAuthStrategy.getAuthenticationValue());
		RestfulModelMap modelMap = mMapper.mapModel(model);
		RestResponse response = mRestClient.executePost(uri,
				modelMap.toHttpEntity(), headers);
		if (response == null)
			return -1;
		return response.getStatusCode() < 400 ? 0 : -1;
	}

	@Override
	public boolean delete(Object model) {
		Preconditions.checkPersistenceForModify(model, mPersistencePolicy);
		mLogger.debug("Sending DELETE request to delete entity");
		Serializable pk = mPersistencePolicy.getPrimaryKey(model);
		String uri = mHost
				+ mPersistencePolicy.getRestEndpoint(model.getClass()) + "/"
				+ pk.toString();
		if (mIsAuthenticated && !mAuthStrategy.isHeader())
			uri += '?' + mAuthStrategy.getAuthenticationString();
		Map<String, String> headers = new HashMap<String, String>();
		if (mIsAuthenticated && mAuthStrategy.isHeader())
			headers.put(mAuthStrategy.getAuthenticationKey(),
					mAuthStrategy.getAuthenticationValue());
		RestResponse response = mRestClient.executeDelete(uri, headers);
		if (response == null)
			return false;
		switch (response.getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_ACCEPTED:
		case HttpStatus.SC_NO_CONTENT:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean update(Object model) throws InfinitumRuntimeException {
		Preconditions.checkPersistenceForModify(model, mPersistencePolicy);
		mLogger.debug("Sending PUT request to update entity");
		String uri = mHost
				+ mPersistencePolicy.getRestEndpoint(model.getClass());
		if (mIsAuthenticated && !mAuthStrategy.isHeader())
			uri += '?' + mAuthStrategy.getAuthenticationString();
		Map<String, String> headers = new HashMap<String, String>();
		if (mIsAuthenticated && mAuthStrategy.isHeader())
			headers.put(mAuthStrategy.getAuthenticationKey(),
					mAuthStrategy.getAuthenticationValue());
		RestfulModelMap modelMap = mMapper.mapModel(model);
		RestResponse response = mRestClient.executePut(uri,
				modelMap.toHttpEntity(), headers);
		switch (response.getStatusCode()) {
		case HttpStatus.SC_OK:
		case HttpStatus.SC_NO_CONTENT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Makes an HTTP request to the web service to update the given model or
	 * save it if it does not exist in the database.
	 * 
	 * @param model
	 *            the model to save or update
	 * @return 0 if the model was updated, 1 if the model was saved, or -1 if
	 *         the operation failed
	 */
	@Override
	public long saveOrUpdate(Object model) {
		Preconditions.checkPersistenceForModify(model, mPersistencePolicy);
		mLogger.debug("Sending PUT request to save or update entity");
		String uri = mHost
				+ mPersistencePolicy.getRestEndpoint(model.getClass());
		if (mIsAuthenticated && !mAuthStrategy.isHeader())
			uri += '?' + mAuthStrategy.getAuthenticationString();
		Map<String, String> headers = new HashMap<String, String>();
		if (mIsAuthenticated && mAuthStrategy.isHeader())
			headers.put(mAuthStrategy.getAuthenticationKey(),
					mAuthStrategy.getAuthenticationValue());
		RestfulModelMap modelMap = mMapper.mapModel(model);
		RestResponse response = mRestClient.executePut(uri,
				modelMap.toHttpEntity(), headers);
		switch (response.getStatusCode()) {
		case HttpStatus.SC_CREATED:
			return 1;
		case HttpStatus.SC_OK:
		case HttpStatus.SC_NO_CONTENT:
			return 0;
		default:
			return -1;
		}
	}

	@Override
	public int saveOrUpdateAll(Collection<? extends Object> models)
			throws InfinitumRuntimeException {
		int count = 0;
		for (Object model : models) {
			if (saveOrUpdate(model) >= 0)
				count++;
		}
		return count;
	}

	@Override
	public int saveAll(Collection<? extends Object> models)
			throws InfinitumRuntimeException {
		int count = 0;
		for (Object model : models) {
			if (save(model) == 0)
				count++;
		}
		return count;
	}

	@Override
	public int deleteAll(Collection<? extends Object> models)
			throws InfinitumRuntimeException {
		int count = 0;
		for (Object model : models) {
			if (delete(model))
				count++;
		}
		return count;
	}

	@Override
	public Session execute(String sql) throws SQLGrammarException {
		throw new UnsupportedOperationException(
				"RestfulSession does not support SQL operations!");
	}

	@Override
	public <T> Criteria<T> createCriteria(Class<T> entityClass) {
		throw new UnsupportedOperationException(
				"RestfulSession does not support criteria operations!");
	}

	@Override
	public <T> Session registerTypeAdapter(Class<T> type, TypeAdapter<T> adapter) {
		mMapper.registerTypeAdapter(type, adapter);
		return this;
	}

	@Override
	public Map<Class<?>, ? extends TypeAdapter<?>> getRegisteredTypeAdapters() {
		return mMapper.getRegisteredTypeAdapters();
	}

	/**
	 * Returns a {@link HttpParams} configured using the
	 * {@link InfinitumContext}.
	 * 
	 * @return {@code HttpParams}
	 */
	protected HttpParams getHttpParams() {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				mRestContext.getConnectionTimeout());
		HttpConnectionParams.setSoTimeout(httpParams,
				mRestContext.getResponseTimeout());
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		return httpParams;
	}

}
