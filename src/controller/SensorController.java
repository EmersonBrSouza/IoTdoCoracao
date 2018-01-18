package controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import exceptions.ServidorNaoRespondeuException;
import model.Paciente;
import model.Sensor;
import util.Acao;

public class SensorController extends Observable{

	private Sensor sensor;
	private String nome;
	private String enderecoIPServidor;
	private int portaServidor;
	private boolean conectadoAoServidor = false;
	private static int tentativasConexao = 0;
	private Paciente paciente;
	private Thread transmitindo;
	private Socket clienteSocket;
	
	public SensorController(String nome, String enderecoIPServidor, int portaServidor) throws ServidorNaoRespondeuException{
		sensor = new Sensor();
		this.enderecoIPServidor = enderecoIPServidor;
		this.portaServidor = portaServidor;
		this.paciente = new Paciente(nome,sensor);
		try {
			this.clienteSocket = new Socket(enderecoIPServidor,portaServidor);
		} catch (IOException e) {
			System.err.println("Erro de I/O");
			System.exit(1);
		}
		conectar();
		transmitir();
	}
	
	public void conectar() throws ServidorNaoRespondeuException{
		
		Acao solicitacao = new Acao("--connect",this.sensor.getId()); //Instancia a ação a ser executada
		byte[] dados = serializarMensagens(solicitacao); //Recebe os bytes da instancia anterior
		try {
			DatagramSocket clienteSocket = new DatagramSocket(); 
			DatagramPacket pacote = new DatagramPacket(dados,dados.length,InetAddress.getByName(enderecoIPServidor),portaServidor);// Monta o pacote
			clienteSocket.send(pacote); //Envia o pacote
			
			while(true){ //Ouve o servidor e aguarda uma resposta
				byte[] dadosRecebidos = new byte[1024]; 
				DatagramPacket pacoteRecebido = new DatagramPacket(dadosRecebidos,dadosRecebidos.length); 
				try {
					clienteSocket.setSoTimeout(15000); // Define o tempo limite de 15 segundos para o recebimento da resposta
					clienteSocket.receive(pacoteRecebido); // Aguarda o recebimento do pacote
					String mensagemRecebida = new String(pacoteRecebido.getData()); //Observa a resposta recebida
					
					if(mensagemRecebida.startsWith("--accepted")){//Servidor aceita a conexão
						conectadoAoServidor = true;
						return;
					}else { //Caso a solicitação seja recusada, solicita novamente
						clienteSocket.send(pacote);
					}
				}catch (SocketTimeoutException e) {
					System.err.println("O servidor não respondeu");
					throw new ServidorNaoRespondeuException();
				}
			}	
		} catch (UnknownHostException e) {
			System.err.println("Servidor Desconhecido");
		} catch (IOException e) {
			System.err.println("Erro no envio/recebimento dos dados");
		}
	}
	
	public void enviar(){
		byte[] dados = serializarMensagens(new Acao("--save",this.paciente));
		//dados = formatarMensagem().getBytes();
		try {
			DatagramSocket socketSensor = new DatagramSocket(); 
			DatagramPacket pacote = new DatagramPacket(dados,dados.length,InetAddress.getByName(enderecoIPServidor),portaServidor);
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

	public void enviarTCP(Acao acao) throws ServidorNaoRespondeuException, IOException{
		try {
			OutputStream saida = clienteSocket.getOutputStream();
			clienteSocket.setSoTimeout(8000);
			saida.write(serializarMensagens(acao));
			saida.flush();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new ServidorNaoRespondeuException();
		} 
	}
	
	/*public void verificarConexao(){
		new Thread(){
			public void run(){
				try {
					while(true){
						sleep(10000);
						
						System.out.println(clienteSocket.);
						conectadoAoServidor = true;
						if(!transmitindo.isAlive()){
							transmitir();
						}
						
						catch (SocketTimeoutException e) {
							conectadoAoServidor = false;
							setChanged();
							notifyObservers();
						}
						
					}	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}*/
	
	public void transmitir(){
		transmitindo = new Thread(){
			public void run(){
				//verificarConexao();
				conectadoAoServidor = true;
				while(true){
					try {
						sleep(5000);
						if(conectadoAoServidor){
							enviar();
						}
					} catch (InterruptedException e) {
						System.err.println("O sistema foi interrompido inesperadamente");
					}
				}
			}
		};
		transmitindo.start();	
	}
	
	
	public boolean estaConectado(){
		return this.conectadoAoServidor;
	}
	/*public String formatarMensagem(){
		final String comando = "--data";
		
		return comando + ";nome:"+this.nome
				+";bpm:"+this.sensor.getBatimentos()
				+";sistolica:"+this.sensor.getPressaoSistolica()
				+";diastolica:"+this.sensor.getPressaoDiastolica()
				+";emRepouso:"+this.sensor.estaEmRepouso()
				+";idSensor:"+this.sensor.getId()
				+";";
	}*/
	
	public byte[] serializarMensagens(Object mensagem){
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		try {
			ObjectOutput out = new ObjectOutputStream(b);
			out.writeObject(mensagem);
			out.flush();
			return b.toByteArray();
		} catch (IOException e) {
			System.err.println("Erro no envio/recebimento dos dados");
		}
		return null;
		
	}
	
	public void addObservador(Observer o){
		addObserver(o);
	}
	
	public void atualizarDados(int batimentos,int sistolica,int diastolica,boolean emRepouso){
		this.sensor.setBatimentos(batimentos);
		this.sensor.setPressaoSistolica(sistolica);
		this.sensor.setPressaoDiastolica(diastolica);
		this.sensor.setEmRepouso(emRepouso);
	}
}
