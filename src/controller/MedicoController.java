package controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import model.Paciente;
import util.Acao;
import util.HashUtil;

public class MedicoController extends Observable{

	private static MedicoController controller;
	private static String id;
	private String enderecoIP;
	private int porta;
	private boolean acessoAutorizado = false;
	private ArrayList<Paciente> todosPacientes;
	private ArrayList<Paciente> pacientesSelecionados;
	private ArrayList<Paciente> pacientesPropensos;
	
	private MedicoController(){}

	public static MedicoController getInstance(){
		if(controller == null){
			controller = new MedicoController();
			id = HashUtil.md5(controller.toString()); 
		}
		return controller;
	}
		
	public boolean login(String username,String password,String enderecoIP, int porta){
		this.enderecoIP = enderecoIP;
		this.porta = porta;
		
		Acao acao = new Acao("--login",username+":"+password+":"+id);
		boolean resposta = false;
		
		try {
			Socket clienteSocket = new Socket(enderecoIP,porta);
			ObjectOutputStream saida = new ObjectOutputStream(clienteSocket.getOutputStream());
			BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
			
			saida.writeObject(acao);
			saida.flush();
			
			String mensagem = entrada.readLine();
			
			if(mensagem.contains("--login-accepted")){
				resposta = true;
			} else if(mensagem.contains("--login-refused")){
				resposta = false;
			}
			
			clienteSocket.close();
			
		} catch (UnknownHostException e) {
			System.err.println("Servidor não encontrado");
		} catch (SocketException e) {
			System.err.println("Erro no canal de comunicação");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Erro no envio dos dados");
		}
		
		return true;
	}
		
	private Object desserializarMensagem(byte[] data) {
		ByteArrayInputStream mensagem = new ByteArrayInputStream(data);
		
		try {
			ObjectInput leitor = new ObjectInputStream(mensagem);
			return (Object)leitor.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void selecionarPaciente(String idPaciente){
		try {
			Socket localSocket = new Socket(enderecoIP,porta);
			ObjectOutputStream saida = new ObjectOutputStream(localSocket.getOutputStream());
			Acao acao = new Acao("--select",id+":"+idPaciente);
			
			saida.writeObject(acao);
			saida.flush();
			
			new Thread(){
				public void run(){
					try {
						InputStream entrada = new ObjectInputStream(localSocket.getInputStream());
						while(true){
							Object resposta = ((ObjectInputStream) entrada).readObject();
							if(resposta instanceof ArrayList){
								pacientesSelecionados = ((ArrayList<Paciente>) resposta);
								setChanged();
								notifyObservers();
							}
						}
					} catch(EOFException e){
						try {
							saida.close();
							localSocket.close();
						} catch (IOException e1) {}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void listarTodosPacientes(){
		try {
			Socket localSocket = new Socket(enderecoIP,porta);
			ObjectOutputStream saida = new ObjectOutputStream(localSocket.getOutputStream());
			Acao acao = new Acao("--list","-all");
			
			saida.writeObject(acao);
			saida.flush();
			
			new Thread(){
				public void run(){
					try {
						InputStream entrada = new ObjectInputStream(localSocket.getInputStream());
						while(true){
							Object resposta = ((ObjectInputStream) entrada).readObject();
							if(resposta instanceof ArrayList){
								todosPacientes = ((ArrayList<Paciente>) resposta);
								setChanged();
								notifyObservers();
							}
						}
					} catch(EOFException e){
						try {
							saida.close();
							localSocket.close();
						} catch (IOException e1) {}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listarPacientesSelecionados(){
		try {
			Socket localSocket = new Socket(enderecoIP,porta);
			ObjectOutputStream saida = new ObjectOutputStream(localSocket.getOutputStream());
			Acao acao = new Acao("--list","-selected:"+id);
			
			saida.writeObject(acao);
			saida.flush();
			
			new Thread(){
				public void run(){
					try {
						InputStream entrada = new ObjectInputStream(localSocket.getInputStream());
						while(true){
							Object resposta = ((ObjectInputStream) entrada).readObject();
							if(resposta instanceof ArrayList){
								pacientesSelecionados = (ArrayList<Paciente>) resposta;
								setChanged();
								notifyObservers();
							}
						}
					} catch(EOFException e){
						try {
							saida.close();
							localSocket.close();
						} catch (IOException e1) {}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listarPacientesPropensos(){
		try {
			Socket localSocket = new Socket(enderecoIP,porta);
			ObjectOutputStream saida = new ObjectOutputStream(localSocket.getOutputStream());
			Acao acao = new Acao("--list","-danger");
			
			saida.writeObject(acao);
			saida.flush();
			new Thread(){
				public void run(){
					try {
						InputStream entrada = new ObjectInputStream(localSocket.getInputStream());
						while(true){
							Object resposta = ((ObjectInputStream) entrada).readObject();
							if(resposta instanceof ArrayList){
								pacientesPropensos = (ArrayList<Paciente>) resposta;
								setChanged();
								notifyObservers();
							}
						}
					} catch(EOFException e){
						try {
							saida.close();
							localSocket.close();
						} catch (IOException e1) {}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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

	public ArrayList<Paciente> getTodosPacientes() {
		return todosPacientes;
	}

	public ArrayList<Paciente> getPacientesSelecionados() {
		return pacientesSelecionados;
	}

	public ArrayList<Paciente> getPacientesPropensos() {
		return pacientesPropensos;
	}

}
