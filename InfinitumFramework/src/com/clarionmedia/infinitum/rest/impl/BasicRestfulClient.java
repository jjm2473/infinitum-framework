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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.clarionmedia.infinitum.context.InfinitumContext;
import com.clarionmedia.infinitum.context.InfinitumContextFactory;
import com.clarionmedia.infinitum.exception.InfinitumRuntimeException;
import com.clarionmedia.infinitum.internal.Preconditions;
import com.clarionmedia.infinitum.orm.persistence.PersistenceResolution;
import com.clarionmedia.infinitum.rest.RestfulClient;

/**
 * <p>
 * A basic implementation of {@link RestfulClient} for standard CRUD operations.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 03/21/12
 */
public class BasicRestfulClient implements RestfulClient {
	
	private static final String TAG = "BasicRestfulClient";
	private static final String ENCODING = "UTF-8";
	
	protected final String mHost;
	protected  final InfinitumContext mContext;
	protected RestfulMapper mMapper;
	
	/**
	 * Constructs a new {@code BasicRestfulClient}. You must call
	 * {@link InfinitumContextFactory#configure(android.content.Context, int)}
	 * before invoking this constructor.
	 */
	public BasicRestfulClient() {
		mContext = InfinitumContextFactory.getInfinitumContext();
		mHost = mContext.getRestHost();
		mMapper = new RestfulMapper();
	}

	@Override
	public boolean save(Object model) {
		Preconditions.checkPersistenceForModify(model);
		if (mContext.isDebug())
		    Log.d(TAG, "Sending POST request to save entity");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(mHost + PersistenceResolution.getRestfulResource(model.getClass()));
		RestfulModelMap map = mMapper.mapModel(model);
		List<NameValuePair> pairs = map.getNameValuePairs();
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
		    HttpResponse response = httpClient.execute(httpPost);
		    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), ENCODING));
		    StringBuilder s = new StringBuilder();
		    String ln;
			while ((ln = reader.readLine()) != null)
				s = s.append(ln);
			// TODO Check response?
			return true;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean delete(Object model) {
		Preconditions.checkPersistenceForModify(model);
		if (mContext.isDebug())
		    Log.d(TAG, "Sending DELETE request to delete entity");
		HttpClient httpClient = new DefaultHttpClient();
		Object pk = PersistenceResolution.getPrimaryKey(model);
		HttpDelete httpDelete = new HttpDelete(mHost + PersistenceResolution.getRestfulResource(model.getClass()) + "/" + pk.toString());
		HttpResponse response;
		try {
			response = httpClient.execute(httpDelete);
			StatusLine statusLine = response.getStatusLine();
			return statusLine.getStatusCode() == HttpStatus.SC_OK;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public int saveOrUpdate(Object model) {
		Preconditions.checkPersistenceForModify(model);
		if (mContext.isDebug())
		    Log.d(TAG, "Sending PUT request to save or update entity");
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(mHost + PersistenceResolution.getRestfulResource(model.getClass()));
		RestfulModelMap map = mMapper.mapModel(model);
		List<NameValuePair> pairs = map.getNameValuePairs();
		try {
			httpPut.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPut);
		    StatusLine statusLine = response.getStatusLine();
		    switch (statusLine.getStatusCode()) {
		    case HttpStatus.SC_CREATED:
		    	return 1;
		    case HttpStatus.SC_OK:
		    	return 0;
		    default:
		    	return -1;
		    }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public <T> T load(Class<T> type, Serializable id) throws InfinitumRuntimeException, IllegalArgumentException {
		// TODO
		Preconditions.checkPersistenceForLoading(type);
		if (mContext.isDebug())
		    Log.d(TAG, "Sending GET request to retrieve entity");
		T ret = null;
		HttpClient httpClient = new DefaultHttpClient(getHttpParams());
		return null;
	}
	
	private HttpParams getHttpParams() {
		// TODO
		HttpParams httpParams = new BasicHttpParams();
		//HttpConnectionParams.setConnectionTimeout(httpParams, Constants.CONNECTION_TIMEOUT);
		//HttpConnectionParams.setSoTimeout(httpParams, Constants.RESPONSE_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		return httpParams;
	}

}
