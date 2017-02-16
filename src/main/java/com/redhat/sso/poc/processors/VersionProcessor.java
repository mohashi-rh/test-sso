package com.redhat.sso.poc.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.redhat.sso.poc.model.Version;

public class VersionProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		Version version = new Version();
		version.setNumber("1.0");
		exchange.getIn().setBody(version);
	}

}
