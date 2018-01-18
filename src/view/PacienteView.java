package view;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controller.SensorController;
import exceptions.ServidorNaoRespondeuException;
import java.awt.Color;

public class PacienteView extends JFrame implements Observer{

	private JPanel contentPane;
	private SensorController controller;
	private JSlider batimentos;
	private JSlider pressaoSistolica;
	private JSlider pressaoDiastolica;
	private JCheckBox emRepouso;
	private JTextPane diastolicaPane;
	private JTextPane sistolicaPane;
	private JTextPane batimentosPane;
	private String nomePaciente;
	private String enderecoIP;
	private JTextPane alerta;
	private int porta;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PacienteView frame = new PacienteView();
					frame.controller.addObservador(frame);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public PacienteView() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		//Configuração do endereço
		nomePaciente = JOptionPane.showInputDialog("Nome do Paciente");
		nomePaciente = (nomePaciente.isEmpty() || nomePaciente.trim().isEmpty()) ? "Desconhecido":nomePaciente;
		
		enderecoIP = JOptionPane.showInputDialog("Endereço IP:");
		
		if(enderecoIP.isEmpty() || enderecoIP.trim().isEmpty()){
			System.err.println("Endereço IP deve ser informado");
			System.exit(1);
		};
		
		String textoPorta = JOptionPane.showInputDialog("Porta:");
		if(textoPorta.isEmpty() || textoPorta.trim().isEmpty()){
			System.err.println("Endereço IP deve ser informado");
			System.exit(1);
		}else{
			porta = Integer.parseInt(textoPorta);
		}
		
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 335);
		setTitle(nomePaciente+"- @"+enderecoIP+":"+porta);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		

		try {
			controller = new SensorController(nomePaciente,enderecoIP,porta);
		} catch (ServidorNaoRespondeuException e1) {
			JOptionPane.showMessageDialog(null,"O servidor não respondeu à solicitação de conexão"); //Solução Provisória
			System.exit(1);
		}
		//Inicialização Batimentos
		JTextPane tituloBatimentos = new JTextPane();
		tituloBatimentos.setEditable(false);
		tituloBatimentos.setText("Batimentos");
		tituloBatimentos.setBounds(10, 11, 59, 20);
		contentPane.add(tituloBatimentos);
		
		batimentos = new JSlider();
		batimentos.setValue(100);
		batimentos.setMaximum(300);
		batimentos.setBounds(10, 41, 200, 26);
		contentPane.add(batimentos);
		
		batimentosPane = new JTextPane();
		batimentosPane.setText(Integer.toString(batimentos.getValue()));
		batimentosPane.setEditable(false);
		batimentosPane.setBounds(220, 41, 34, 20);
		contentPane.add(batimentosPane);
		
		
		//Inicialização Pressão Sistólica
		JTextPane txtpnPressoSistlica = new JTextPane();
		txtpnPressoSistlica.setText("Press\u00E3o Sist\u00F3lica");
		txtpnPressoSistlica.setBounds(10, 78, 85, 20);
		contentPane.add(txtpnPressoSistlica);
		
		pressaoSistolica = new JSlider();
		pressaoSistolica.setValue(12);
		pressaoSistolica.setMaximum(40);
		pressaoSistolica.setBounds(10, 109, 200, 26);
		contentPane.add(pressaoSistolica);
		
		sistolicaPane = new JTextPane();
		sistolicaPane.setEditable(false);
		sistolicaPane.setText(Integer.toString(pressaoSistolica.getValue()));
		sistolicaPane.setBounds(220, 109, 34, 20);
		contentPane.add(sistolicaPane);
		
		//Inicialização Pressão Diastólica
		JTextPane tituloPressaoDiastolica = new JTextPane();
		tituloPressaoDiastolica.setEditable(false);
		tituloPressaoDiastolica.setText("Press\u00E3o Diast\u00F3lica");
		tituloPressaoDiastolica.setBounds(10, 146, 92, 20);
		contentPane.add(tituloPressaoDiastolica);
		
		pressaoDiastolica = new JSlider();
		pressaoDiastolica.setMaximum(40);
		pressaoDiastolica.setValue(8);
		pressaoDiastolica.setBounds(10, 177, 200, 26);
		contentPane.add(pressaoDiastolica);
		
		diastolicaPane = new JTextPane();
		diastolicaPane.setBounds(220, 177, 34, 20);
		diastolicaPane.setText(Integer.toString(pressaoDiastolica.getValue()));
		contentPane.add(diastolicaPane);
		
		//Checkbox repouso
		emRepouso = new JCheckBox("Em repouso?");
		emRepouso.setBounds(10, 210, 97, 23);
		contentPane.add(emRepouso);
		
		alerta = new JTextPane();
		alerta.setForeground(Color.RED);
		alerta.setBounds(10, 255, 200, 20);
		contentPane.add(alerta);
		
		//Adição dos listeners
		batimentos.addChangeListener(new atualizarDados());
		pressaoSistolica.addChangeListener(new atualizarDados());
		pressaoDiastolica.addChangeListener(new atualizarDados());
		emRepouso.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				new atualizarDados().changeOnClick();
			}
		});
		
		new atualizarDados().atualiza();
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof SensorController){
			System.out.println("Opa");
			if(!controller.estaConectado()){
				JOptionPane.showMessageDialog(null, "Conexão perdida");
				alerta.setText("Conexão perdida");
			}
		}	
	}
	
	private class atualizarDados implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			atualiza();
		}
		
		public void changeOnClick(){
			atualiza();
		}
		
		public void atualiza(){
			int bpm = batimentos.getValue();
			int sistolica = pressaoSistolica.getValue();
			int diastolica = pressaoDiastolica.getValue();
			boolean repouso = emRepouso.isSelected();
			
			batimentosPane.setText(Integer.toString(bpm));
			diastolicaPane.setText(Integer.toString(diastolica));
			sistolicaPane.setText(Integer.toString(sistolica));
			
			controller.atualizarDados(bpm,sistolica,diastolica,repouso);
		}
		
	}

	
}
