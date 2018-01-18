package util;

import java.io.Serializable;
import java.net.InetAddress;

public class MensagemUDP{

	private static final long serialVersionUID = 1L;
	private InetAddress origemIP;
	private int origemPorta;
	private byte[] mensagem;
	private InetAddress destinoIP;
	private int destinoPorta;
	
	
	
	public MensagemUDP(InetAddress origemIP, int origemPorta, byte[] mensagem, InetAddress destinoIP,
			int destinoPorta) {
		this.origemIP = origemIP;
		this.origemPorta = origemPorta;
		this.mensagem = mensagem;
		this.destinoIP = destinoIP;
		this.destinoPorta = destinoPorta;
	}
	
	public MensagemUDP(byte[] mensagem, InetAddress destinoIP,int destinoPorta) {
		this.mensagem = mensagem;
		this.destinoIP = destinoIP;
		this.destinoPorta = destinoPorta;
	}
	
	public InetAddress getOrigemIP() {
		return origemIP;
	}
	public void setOrigemIP(InetAddress origemIP) {
		this.origemIP = origemIP;
	}
	public int getOrigemPorta() {
		return origemPorta;
	}
	public void setOrigemPorta(int origemPorta) {
		this.origemPorta = origemPorta;
	}
	public byte[] getMensagem() {
		return mensagem;
	}
	public void setMensagem(byte[] mensagem) {
		this.mensagem = mensagem;
	}
	public InetAddress getDestinoIP() {
		return destinoIP;
	}
	public void setDestinoIP(InetAddress destinoIP) {
		this.destinoIP = destinoIP;
	}
	public int getDestinoPorta() {
		return destinoPorta;
	}
	public void setDestinoPorta(int destinoPorta) {
		this.destinoPorta = destinoPorta;
	}
	
}
