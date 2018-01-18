package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import controller.ServidorController;
import model.Conexao;
import model.Paciente;

public class Servidor implements Runnable{

	private static Map<String,Conexao> conexoes = new HashMap<String,Conexao>();
	private Thread principal;
	private Interpretador interpretador = new Interpretador();
	private DatagramSocket socketUDPServidor;
	private ServerSocket socketTCPServidor;

	
	public static void main(String[] args){
		String listen = JOptionPane.showInputDialog("Porta a ser ouvida:");
		Servidor s = new Servidor(listen);
	}
	
	private Servidor(String listen){
		int porta = 0;
		if(listen.isEmpty() || listen.trim().isEmpty()){
			System.err.println("A porta deve ser informada");
			System.exit(1);
		}else{
			porta = Integer.parseInt(listen);
		}
		
		try {
			 socketUDPServidor = new DatagramSocket(porta);
			 socketTCPServidor = new ServerSocket(porta);
			 principal = new Thread(this);
			 principal.start();
		} catch (SocketException e) {
			System.err.println("Erro no canal de comunicação");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		ServidorController.getInstance().monitorarPacientes();
		ouvirUDP();
		ouvirTCP();
		
	}
	
	private void ouvirUDP(){
		new Thread(){
			public void run(){
				while(true){
					try {
						byte[] dadosRecebidos = new byte[1024];
						DatagramPacket pacote = new DatagramPacket(dadosRecebidos,dadosRecebidos.length);
						socketUDPServidor.receive(pacote);	
						new Thread(new Gerenciador(pacote)).start();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void ouvirTCP(){
		new Thread(){
			public void run(){
				while(true){
					try {
						Socket recebido = socketTCPServidor.accept();
						new Thread(new Gerenciador(recebido)).start();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public void enviar(String mensagem,InetAddress ip,int porta){
		byte[] dados = new byte[1024];
		dados = mensagem.getBytes();
		try {
			DatagramSocket socketSensor = new DatagramSocket();
			DatagramPacket pacote = new DatagramPacket(dados,dados.length,ip,porta);
			System.out.println(pacote.getAddress().toString()+pacote.getPort());
			socketSensor.send(pacote);
			socketSensor.close();
		} catch (UnknownHostException e) {
			System.err.println("Servidor não encontrado");
		} catch (SocketException e) {
			System.err.println("Erro no canal de comunicação");
		} catch (IOException e) {
			System.err.println("Erro no envio dos dados");
		}
	}
	
	public void enviar(){
		
	}
}
