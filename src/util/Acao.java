package util;

import java.io.Serializable;

public class Acao implements Serializable{


	private static final long serialVersionUID = 1L;
	private String acao;
	private Object objeto;
	
	public Acao(String acao, Object objeto) {
		this.acao = acao;
		this.objeto = objeto;
	}
	
	public Acao(String acao){
		this.acao = acao;
	}
	public String getAcao() {
		return acao;
	}
	public void setAcao(String acao) {
		this.acao = acao;
	}
	public Object getObjeto() {
		return objeto;
	}
	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}
}
