package com.redhat.sso.poc.processors;

import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.redhat.sso.poc.model.Pessoa;

public class ListCustomerProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Pessoa> pessoaList = new LinkedList<>();
		pessoaList.add(createPessoa("John", "john@gmail.com"));
		pessoaList.add(createPessoa("Silvio", "ssantos@gmail.com"));
		pessoaList.add(createPessoa("Jo", "jsoares@gmail.com"));
		exchange.getOut().setBody(pessoaList);
	}

	static Pessoa createPessoa(String name, String email) {
		Pessoa p = new Pessoa();
		p.setName(name);
		p.setEmail(email);
		return p;
	}

}
