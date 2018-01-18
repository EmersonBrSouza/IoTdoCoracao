package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import controller.MedicoController;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import java.awt.Color;

public class LoginView {

	private JFrame frame;
	private JTextField usuario;
	private JPasswordField password;
	private MedicoController controller = MedicoController.getInstance() ;
	private JTextPane txtpnNome;
	private JTextPane txtpnSenha;
	private JTextField enderecoIP;
	private JTextPane txtpnEndereo;
	private JTextField porta;
	private JTextPane txtpnPorta;
	private JTextPane txtpnUsurioEouSenha;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginView window = new LoginView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public LoginView() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		frame = new JFrame();
		frame.setBounds(100, 100, 534, 300);
		frame.getContentPane().setLayout(null);
		
		usuario = new JTextField();
		usuario.setBounds(75, 75, 156, 27);
		frame.getContentPane().add(usuario);
		usuario.setColumns(10);
		
		password = new JPasswordField();
		password.setBounds(289, 75, 156, 27);
		frame.getContentPane().add(password);
		
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					login();
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					System.err.println("Erro Look and Feel");
				}
			}
		});
		btnEntrar.setBounds(388, 142, 64, 33);
		frame.getContentPane().add(btnEntrar);
		
		txtpnUsurioEouSenha = new JTextPane();
		txtpnUsurioEouSenha.setVisible(false);
		txtpnUsurioEouSenha.setForeground(Color.RED);
		txtpnUsurioEouSenha.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtpnUsurioEouSenha.setEditable(false);
		txtpnUsurioEouSenha.setBounds(174, 196, 156, 20);
		frame.getContentPane().add(txtpnUsurioEouSenha);
		
		txtpnNome = new JTextPane();
		txtpnNome.setEditable(false);
		txtpnNome.setText("Nome");
		txtpnNome.setBounds(76, 44, 33, 20);
		frame.getContentPane().add(txtpnNome);
		
		txtpnSenha = new JTextPane();
		txtpnSenha.setText("Senha");
		txtpnSenha.setBounds(289, 44, 41, 20);
		frame.getContentPane().add(txtpnSenha);
		
		enderecoIP = new JTextField();
		enderecoIP.setBounds(75, 145, 156, 27);
		frame.getContentPane().add(enderecoIP);
		enderecoIP.setColumns(10);
		
		txtpnEndereo = new JTextPane();
		txtpnEndereo.setEditable(false);
		txtpnEndereo.setText("Endere\u00E7o IP");
		txtpnEndereo.setBounds(75, 113, 64, 20);
		frame.getContentPane().add(txtpnEndereo);
		
		porta = new JTextField();
		porta.setBounds(289, 145, 89, 27);
		frame.getContentPane().add(porta);
		porta.setColumns(10);
		
		txtpnPorta = new JTextPane();
		txtpnPorta.setEditable(false);
		txtpnPorta.setText("Porta");
		txtpnPorta.setBounds(289, 113, 33, 20);
		frame.getContentPane().add(txtpnPorta);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@SuppressWarnings("deprecation")
	private void login() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		if(controller.login(usuario.getText(), password.getText(),enderecoIP.getText(),Integer.parseInt(porta.getText()))){
			new MedicoView(usuario.getText(),controller);
			frame.dispose();
		}else{
			txtpnUsurioEouSenha.setVisible(true);
			txtpnUsurioEouSenha.setText("Usuário e/ou Senha Incorretos");
		}
	}
}
