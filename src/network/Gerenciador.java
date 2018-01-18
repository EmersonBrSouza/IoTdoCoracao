package network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;

import controller.ServidorController;
import model.Paciente;
import util.Acao;
import util.MensagemUDP;

public class Gerenciador implements Runnable{

	private DatagramPacket pacote;
	private InetAddress ip;
	private int porta;
	private Object mensagem;
	private Socket socketServidor;
	public Gerenciador(DatagramPacket pacote){
		this.pacote = pacote;
		this.ip = pacote.getAddress();
		this.porta = pacote.getPort();
		mensagem = desserializarMensagem(pacote.getData());
		
	}
	
	public Gerenciador(Socket socket){
		this.socketServidor = socket;
		try {
			InputStream tcpInput = new ObjectInputStream(socketServidor.getInputStream());

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutputStream obj = new ObjectOutputStream(bytes);
			
			try {
				obj.writeObject(((ObjectInputStream) tcpInput).readObject());
				bytes.toByteArray();
				mensagem = desserializarMensagem(bytes.toByteArray());
			} catch (ClassNotFoundException e) {
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Object desserializarMensagem(byte[] data) {
		ByteArrayInputStream mensagem = new ByteArrayInputStream(data);
		
		try {
			ObjectInput leitor = new ObjectInputStream(mensagem);
			return (Object)leitor.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void run() {
		
		
		if(mensagem instanceof Acao){
			Acao acaoRequisitada = (Acao) mensagem;
			
			if(acaoRequisitada.getAcao().equals("--connect")){
				MensagemUDP mensagem = new MensagemUDP("--accepted".getBytes(),pacote.getAddress(),pacote.getPort());
				ServidorController.getInstance().enviar(mensagem);
			}
			else if(acaoRequisitada.getAcao().equals("--save")){
				Paciente paciente = (Paciente) acaoRequisitada.getObjeto();
				ServidorController.getInstance().salvarPaciente(paciente, ip, porta);
				//ServidorController.getInstance().responderSensor(paciente.getSensor().getId(), "--saved");
			}else if(acaoRequisitada.getAcao().equals("--login")){
				String mensagemRecebida = (String) acaoRequisitada.getObjeto();
				String[] credenciais = mensagemRecebida.split(":");
				ServidorController.getInstance().salvarMedico(credenciais[2], socketServidor.getInetAddress(), socketServidor.getPort());
				ServidorController.getInstance().logar(credenciais[0],credenciais[1],socketServidor);
			}else if(acaoRequisitada.getAcao().equals("--list")){
				if(acaoRequisitada.getObjeto() instanceof String){
					String filtro = (String)acaoRequisitada.getObjeto();
					if(filtro.equals("-all")){
						ServidorController.getInstance().listarTodos(socketServidor);
					}else if(filtro.startsWith("-selected")){
						String id = filtro.substring(filtro.indexOf(":")+1);
						ServidorController.getInstance().listarSelecionados(id,socketServidor);
					}else if(filtro.equals("-danger")){
						ServidorController.getInstance().listarPropensos(socketServidor);
					}
				}
			}else if(acaoRequisitada.getAcao().equals("--select")){
				if(acaoRequisitada.getObjeto() instanceof String){
					String filtro = (String) acaoRequisitada.getObjeto();
					String[] credenciais = filtro.split(":");
					
					ServidorController.getInstance().selecionarPaciente(credenciais[0],credenciais[1],socketServidor); //(idMedico,idPaciente)
				}
			}
		}else if(mensagem instanceof String){
			
		}
		
	}
	
	/*private Sensor restaurarSensor(String dadosRecebidos){
		
		String[] dadosProcessados = separarDados(dadosRecebidos);
		
		String nomeCliente = dadosProcessados[0];
		
		Sensor sensor = new Sensor(nomeCliente);
		sensor.setBatimentos(Integer.parseInt(dadosProcessados[1]));
		sensor.setPressaoSistolica(Integer.parseInt(dadosProcessados[2]));
		sensor.setPressaoDiastolica(Integer.parseInt(dadosProcessados[3]));
		sensor.setEmRepouso(Boolean.parseBoolean(dadosProcessados[4]));
		sensor.setId(dadosProcessados[5]);
		
		return sensor;
	}
	
	private String acessarConta(String dadosRecebidos){
		String[] credenciais = separarDados(dadosRecebidos);
		String cliente = credenciais[0];
		String usuario = credenciais[1];
		String senha = credenciais[2];
		
		Scanner bd = new Scanner(getClass().getResourceAsStream("bd.txt"));
		
		while(bd.hasNext()){
			String texto = bd.nextLine();
			String[] partes = {texto.substring(texto.indexOf("#")+1,texto.indexOf(":")),texto.substring(texto.indexOf(":")+1, texto.indexOf(";"))};
			if(usuario.equals(partes[0]) && senha.equals(partes[1])){
				return "--login-sucesso::"+cliente;
			}
		}
		
		return "--login-falha::"+cliente;
	}
	
	private String[] separarDados(String texto){
		String[] partes = texto.split(";");
		String[] aux = new String[partes.length];
		
		for(int i=0;i<partes.length;i++){
			if(partes[i].contains(":")){
				aux[i] = partes[i].substring(partes[i].indexOf(":")+1);
			}
		}
		
		return aux;
	}*/

}
