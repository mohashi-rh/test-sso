package com.redhat.sso.poc.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCustomerProcessor implements Processor {
	private static Logger logger = LoggerFactory.getLogger(CreateCustomerProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Creating customer: {}", exchange.getIn().getBody());
	}
}
