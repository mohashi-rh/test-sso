package com.redhat.sso.poc.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PessoaList {
	private List<Pessoa> list;

	public List<Pessoa> getList() {
		return list;
	}

	public void setList(List<Pessoa> pessoa) {
		this.list = pessoa;
	}
	
}
