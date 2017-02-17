package com.redhat.sso.poc.processors;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.redhat.sso.poc.model.Pessoa;
import com.redhat.sso.poc.model.PessoaList;

public class PessoaListWrapperProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Pessoa> list = (List<Pessoa>) exchange.getIn().getBody();
		PessoaList pessoaList = new PessoaList();
		pessoaList.setList(list);
		exchange.getOut().setBody(pessoaList);
	}

}
