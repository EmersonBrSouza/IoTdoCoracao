package network;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import model.Conexao;
import model.Sensor;

public class Interpretador{

	public Object avaliar(String instrucao){
		
		if(instrucao.startsWith("--data")){
			return restaurarSensor(instrucao.substring("--data".length()+1));
		}else if(instrucao.startsWith("--list")){
			
		}else if(instrucao.startsWith("--select")){
			
		}else if(instrucao.startsWith("--login")){
			return acessarConta(instrucao.substring("--login".length()+1));
		}
		
		return instrucao;
	}
	
	private Sensor restaurarSensor(String dadosRecebidos){
		
		String[] dadosProcessados = separarDados(dadosRecebidos);
		
		String nomeCliente = dadosProcessados[0];
		
		Sensor sensor = new Sensor();
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
	}
}
