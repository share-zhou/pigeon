/**
 * Dianping.com Inc.
 * Copyright (c) 2003-2013 All Rights Reserved.
 */
package com.dianping.pigeon.test.integration.zone;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.pigeon.test.PigeonAutoTest;
import com.dianping.pigeon.test.SingleServerBaseTest;
import com.dianping.pigeon.test.service.EchoService;

public class ZoneServiceTest extends SingleServerBaseTest {

	@PigeonAutoTest(serviceName = "http://service.dianping.com/testService__ForMultiServer/echoService_1.0.0", zone = "pre")
	public EchoService echoService;

	@Test
	public void test() {
		String echo = echoService.echo("dianping");
		System.err.println(echo);
		Assert.assertEquals("Echo: dianping", echo);
	}
}