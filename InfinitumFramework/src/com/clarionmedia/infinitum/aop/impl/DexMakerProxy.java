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

package com.clarionmedia.infinitum.aop.impl;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.content.Context;

import com.clarionmedia.infinitum.aop.AdvisedProxy;
import com.clarionmedia.infinitum.aop.AopProxy;
import com.clarionmedia.infinitum.aop.JoinPoint;
import com.clarionmedia.infinitum.aop.Pointcut;
import com.clarionmedia.infinitum.internal.DexCaching;
import com.clarionmedia.infinitum.internal.Preconditions;
import com.google.dexmaker.stock.ProxyBuilder;

/**
 * <p>
 * Implementation of {@link AopProxy} that relies on DexMaker in order to proxy
 * non-final classes in addition to interfaces.
 * </p>
 * 
 * @author Tyler Treat
 * @version 1.0 07/13/12
 * @since 1.0
 */
public class DexMakerProxy extends AdvisedProxy {

	private Context mContext;

	/**
	 * Creates a new {@code DexMakerProxy}.
	 * 
	 * @param context
	 *            the {@link Context} used to retrieve the DEX bytecode cache
	 * @param target
	 *            the proxied {@link Object}
	 * @param pointcut
	 *            the {@link Pointcut} to provide advice
	 */
	public DexMakerProxy(Context context, Object target, Pointcut pointcut) {
		super(pointcut);
		Preconditions.checkNotNull(target);
		Preconditions.checkNotNull(context);
		mContext = context;
		mTarget = target;
	}

	/**
	 * Retrieves a {@code DexMakerProxy} instance for the given proxy.
	 * 
	 * @param object
	 *            the {@link Object} to retrieve a proxy instance for
	 * @return {@code DexMakerProxy} or {@code null} if {@code object} is not a
	 *         proxy
	 */
	public static DexMakerProxy getProxy(Object object) {
		if (!ProxyBuilder.isProxyClass(object.getClass()))
			return null;
		return (DexMakerProxy) ProxyBuilder.getInvocationHandler(object);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// TODO revisit
		for (JoinPoint joinPoint : mBeforeAdvice) {
			joinPoint.setMethod(method);
			joinPoint.setArguments(args);
			joinPoint.invoke();
		}
		Object ret = method.invoke(mTarget, args);
		for (JoinPoint joinPoint : mAfterAdvice) {
			joinPoint.setMethod(method);
			joinPoint.setArguments(args);
			joinPoint.invoke();
		}
		return ret;
	}

	@Override
	public Object getTarget() {
		return mTarget;
	}

	@Override
	public Object getProxy() {
		try {
			return ProxyBuilder.forClass(mTarget.getClass()).handler(this)
					.dexCache(DexCaching.getDexCache(mContext)).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isProxy(Object object) {
		return ProxyBuilder.isProxyClass(object.getClass());
	}

	@Override
	public InvocationHandler getInvocationHandler(Object proxy) {
		if (!isProxy(proxy))
			return null;
		return ProxyBuilder.getInvocationHandler(proxy);
	}

}
