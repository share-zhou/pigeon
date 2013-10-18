/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.remoting.invoker.component.async;

import org.apache.log4j.Logger;

import com.dianping.dpsf.component.DPSFRequest;
import com.dianping.dpsf.component.DPSFResponse;
import com.dianping.pigeon.exception.PigeonRuntimeException;
import com.dianping.pigeon.remoting.common.exception.NetworkException;
import com.dianping.pigeon.remoting.common.util.Constants;
import com.dianping.pigeon.remoting.invoker.Client;
import com.dianping.pigeon.util.ContextUtils;

public class ServiceCallbackWrapper implements Callback {

	private static final Logger logger = Logger.getLogger(ServiceCallbackWrapper.class);

	private DPSFResponse response;

	private DPSFRequest request;

	private Client client;

	private ServiceCallback callback;

	public ServiceCallbackWrapper(ServiceCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			if (ContextUtils.getContext() != null) {
				if (this.response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
					// 传递业务上下文
					ContextUtils.addSuccessContext(this.response.getContext());
				} else {
					// 传递业务上下文
					ContextUtils.addFailedContext(this.response.getContext());
				}
			}
			if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
				StringBuffer sb = new StringBuffer();
				sb.append("Service Exception Info *************\r\n")
						// .append(" token:").append(ContextUtil.getToken(this.response.getContext())).append("\r\n")
						.append(" seq:").append(request.getSequence()).append(" callType:")
						.append(request.getCallType()).append("\r\n serviceName:").append(request.getServiceName())
						.append(" methodName:").append(request.getMethodName()).append("\r\n host:")
						.append(client.getHost()).append(":").append(client.getPort());
				response.setReturn(new PigeonRuntimeException(request.getServiceName(), client.getHost() + ":"
						+ client.getPort(), sb.toString(), (Exception) response.getReturn()));
			}
			try {
				if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE) {
					logger.error("----------response is:" + response);
					logger.error("----------callback is:" + callback);

					this.callback.callback(response.getReturn());
				} else if (response.getMessageType() == Constants.MESSAGE_TYPE_EXCEPTION) {
					logger.error(response.getCause());
					this.callback.frameworkException(new PigeonRuntimeException(response.getCause()));
				} else if (response.getMessageType() == Constants.MESSAGE_TYPE_SERVICE_EXCEPTION) {
					this.callback.serviceException((Exception) response.getReturn());
				}
			} catch (Exception e) {
				logger.error("ServiceCallback error", e);
			}
		} catch (NetworkException e) {
			this.callback.frameworkException(e);
		}
	}

	@Override
	public void setClient(Client client) {
		this.client = client;
	}

	@Override
	public Client getClient() {
		return this.client;
	}

	@Override
	public void callback(DPSFResponse response) {
		this.response = response;
	}

	@Override
	public void setRequest(DPSFRequest request) {
		this.request = request;
	}

}