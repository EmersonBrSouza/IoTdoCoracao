package model;

import java.io.Serializable;
import util.HashUtil;

public class Sensor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private int batimentos;
	private int pressaoSistolica;
	private int pressaoDiastolica;
	private boolean emRepouso;
	
	
	public Sensor(){
		this.setId(HashUtil.md5()); 
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getBatimentos() {
		return batimentos;
	}
	public void setBatimentos(int batimentos) {
		this.batimentos = batimentos;
	}
	public int getPressaoSistolica() {
		return pressaoSistolica;
	}
	public void setPressaoSistolica(int pressaoSistolica) {
		this.pressaoSistolica = pressaoSistolica;
	}
	public int getPressaoDiastolica() {
		return pressaoDiastolica;
	}
	public void setPressaoDiastolica(int pressaoDiastolica) {
		this.pressaoDiastolica = pressaoDiastolica;
	}
	public boolean estaEmRepouso() {
		return emRepouso;
	}
	public void setEmRepouso(boolean emRepouso) {
		this.emRepouso = emRepouso;
	}
	
	
}
