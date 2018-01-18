package model;

import java.io.Serializable;

public class Paciente implements Serializable{

	private static final long serialVersionUID = 1L;
	private String nome;
	private Sensor sensor;
	
	
	public Paciente(String nome, Sensor sensor) {
		this.nome = nome;
		this.sensor = sensor;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Sensor getSensor() {
		return sensor;
	}
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
}
