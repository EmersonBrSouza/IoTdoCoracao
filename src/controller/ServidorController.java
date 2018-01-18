package controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import model.Conexao;
import model.Paciente;
import model.Sensor;
import util.MensagemUDP;

public class ServidorController {

	private ServidorController(){}

	
	private Map<String,Conexao> conexoes = new HashMap<String,Conexao>(); //ID = id do sensor ou do médico
	private Map<String,Paciente> pacientesPropensos = new HashMap<String,Paciente>();
	private Map<String,ArrayList<Paciente>> pacientesSelecionados = new HashMap<String,ArrayList<Paciente>>(); //ID = Id do médico
	
	private static ServidorController controller;
	
	public static ServidorController getInstance(){
		if(controller == null){
			controller = new ServidorController();
		}
		return controller;
	}
	
	public synchronized void enviar(MensagemUDP mensagem){
		try {
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket dados = new DatagramPacket(mensagem.getMensagem(),mensagem.getMensagem().length,mensagem.getDestinoIP(),mensagem.getDestinoPorta());
			socket.send(dados);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void responderSensor(String idSensor, String mensagem){
		Conexao conexao = conexoes.get(idSensor);
		enviar(new MensagemUDP(mensagem.getBytes(),conexao.getEnderecoIP(),conexao.getPorta()));
	}
	
	public synchronized void salvarPaciente(Paciente paciente,InetAddress ip, int porta){
		Conexao conexao = new Conexao(paciente.getSensor().getId(),paciente,ip,porta);
		conexoes.put(paciente.getSensor().getId(), conexao);
	}
	
	public synchronized void salvarMedico(String id,InetAddress ip, int porta){
		Conexao conexao = new Conexao(id,ip,porta);
		conexoes.put(id, conexao);
	}
	
	public synchronized void logar(String username, String password,Socket socket){
		try {
			if(acessarConta(username,password)){
				PrintStream saida = new PrintStream(socket.getOutputStream(),true);
				saida.flush();
				saida.println("--login-accepted");
				
			}else{
				PrintStream saida = new PrintStream(socket.getOutputStream(),true);
				saida.flush();
				saida.println("--login-refused");
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void monitorarPacientes(){
		new Thread(){
			public void run(){
				while(true){
					try {
						sleep(15000);
						
						Iterator iterador= conexoes.keySet().iterator();
						
						while(iterador.hasNext()){
							Conexao c = conexoes.get(iterador.next());
							if(c.getPaciente() != null){
								if(c.getSensor().getBatimentos() > 100){
									pacientesPropensos.put(c.getId(),c.getPaciente());
								}
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
			}
		}.start();
	}
	
	private boolean acessarConta(String username, String password){	
		String fs = System.getProperty("file.separator");
		Scanner bd = new Scanner(getClass().getResourceAsStream(".."+fs+"network"+fs+"bd.txt"));
		
		while(bd.hasNext()){
			String texto = bd.nextLine();
			String[] partes = {texto.substring(texto.indexOf("#")+1,texto.indexOf(":")),texto.substring(texto.indexOf(":")+1, texto.indexOf(";"))};
			if(username.equals(partes[0]) && password.equals(partes[1])){
				return true;
			}
		}
		
		return false;
	}

	public synchronized void listarTodos(Socket socketServidor) {
		ArrayList<Paciente> pacientes = new ArrayList<Paciente>();
		Iterator<String> iterador= conexoes.keySet().iterator();
		
		while(iterador.hasNext()){
			Conexao c = conexoes.get(iterador.next());
			if(c.getPaciente() != null){ // Se for um paciente
				pacientes.add(c.getPaciente());
			}
		}
		
		enviarPacientes(socketServidor,pacientes);
		
	}

	public synchronized void listarSelecionados(String id,Socket socketServidor) {	
		ArrayList<Paciente> lista = pacientesSelecionados.get(id);
		enviarPacientes(socketServidor,lista);		
	}

	public synchronized void listarPropensos(Socket socketServidor) {
		ArrayList<Paciente> pacientes = new ArrayList<Paciente>();
		Iterator<String> iterador= pacientesPropensos.keySet().iterator();
		
		while(iterador.hasNext()){
			Paciente p = pacientesPropensos.get(iterador.next());
			pacientes.add(p);
		}
		
		enviarPacientes(socketServidor,pacientes);	
	}
	
	private synchronized void enviarPacientes(Socket socket,ArrayList<Paciente> pacientes){
		try {
			ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
			saida.writeObject(pacientes);
			saida.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void selecionarPaciente(String idMedico, String idPaciente,Socket socket) {
		Paciente p = conexoes.get(idPaciente).getPaciente();
		
		if(pacientesSelecionados.containsKey(idMedico)){
			ArrayList<Paciente> pacientes = pacientesSelecionados.get(idMedico);
			Iterator<Paciente> i = pacientes.iterator();
			
			boolean jaExiste = false;
			while(i.hasNext()){
				Sensor s = i.next().getSensor();// Sensor do paciente iterado
				if(s.getId().equals(idPaciente)){ //Atualiza os dados do sensor já existente
					jaExiste = true;
					s.setBatimentos(p.getSensor().getBatimentos());
					s.setPressaoDiastolica(p.getSensor().getPressaoDiastolica());
					s.setPressaoSistolica(p.getSensor().getPressaoSistolica());
					s.setEmRepouso(p.getSensor().estaEmRepouso());
				}
			}
			if(!jaExiste){
				pacientes.add(p);
			}
		} else{
			ArrayList<Paciente> selecao = new ArrayList<Paciente>();
			selecao.add(p);
			System.out.println("idMedico:"+idMedico+"idSensor"+p.getSensor().getId());
			pacientesSelecionados.put(idMedico,selecao);
		}
		
		listarSelecionados(idMedico,socket);
	}
}
