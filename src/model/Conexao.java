package model;

import java.net.InetAddress;

public class Conexao {

	private String id;
	private String nomeCliente;
	private InetAddress enderecoIP;
	private int porta;
	private Paciente paciente;

	public Conexao(String nomeCliente,Paciente paciente,InetAddress endereco, int porta){
		this.id = paciente.getSensor().getId();
		this.nomeCliente = nomeCliente;
		this.paciente = paciente;
		this.enderecoIP = endereco;
		this.setPorta(porta);
	}
	
	public Conexao(String id, InetAddress endereco, int porta){
		this.id = id;
		this.enderecoIP = endereco;
		this.setPorta(porta);
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public Paciente getPaciente() {
		return this.paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Sensor getSensor(){
		return this.paciente.getSensor();
	}
	
	public InetAddress getEnderecoIP() {
		return enderecoIP;
	}

	public void setEnderecoIP(InetAddress enderecoIP) {
		this.enderecoIP = enderecoIP;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}
	
	
}
