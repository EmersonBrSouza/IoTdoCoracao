package view;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.table.DefaultTableModel;

import controller.MedicoController;
import model.Paciente;
import model.Sensor;

public class MedicoView extends JFrame implements Observer{
	private String nomeMedico;
	private MedicoController controller;
	private JTextField textField;
	private JTable tabelaPacientes;
	private JTable tabelaPacientesPropensos;
	private JTable tabelaPacientesSelecionados;
	
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MedicoView frame = new MedicoView("Médico",MedicoController.getInstance());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
	
	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public MedicoView(String nome,MedicoController controller) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		this.nomeMedico = nome;
		this.controller = controller;
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);

		//dtm.addRow(new String[]{"1","João"});
		setVisible(true);
		setResizable(false);
		getContentPane().setLayout(null);
		
		new PacientesTotais().inicializar();
		new PacientesSelecionados().inicializar();
		new PacientesPropensos().inicializar();
		
		controller.addObserver(this);
		controller.listarTodosPacientes();
		controller.listarPacientesPropensos();
		
		
		listarTodosPacientes();
		listarPacientesPropensos();
		listarPacientesSelecionados();
	}

	
	
	//Povoamento das tabelas
	private void listarTodosPacientes(){
		new Thread(){
			public void run(){
				while(true){
					try {
						sleep(10000);
						controller.listarTodosPacientes();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void listarPacientesPropensos(){
		new Thread(){
			public void run(){
				while(true){
					try {
						sleep(10000);
						controller.listarPacientesPropensos();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void listarPacientesSelecionados(){
		new Thread(){
			public void run(){
				while(true){
					try {
						sleep(10000);
						controller.listarPacientesSelecionados();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	
	private void atualizarTodosPacientes(){
		ArrayList<Paciente> pacientes = controller.getTodosPacientes();
		Iterator<Paciente> iterador = pacientes.iterator();
		
		while(iterador.hasNext()){
			Paciente p = (Paciente) iterador.next();
			adicionarItem(tabelaPacientes,p);
		}
	}
	
	private void atualizarPacientesSelecionados(){
		ArrayList<Paciente> pacientes = controller.getPacientesSelecionados();
		Iterator<Paciente> iterador = pacientes.iterator();
		
		while(iterador.hasNext()){
			Paciente p = (Paciente) iterador.next();
			adicionarItem(tabelaPacientesSelecionados,p);
		}
	}
	
	private void atualizarPacientesPropensos(){
		ArrayList<Paciente> pacientes = controller.getPacientesPropensos();
		Iterator<Paciente> iterador = pacientes.iterator();
		
		while(iterador.hasNext()){
			Paciente p = (Paciente) iterador.next();
			adicionarItem(tabelaPacientesPropensos,p);
		}
	}
	
	//Manipulação das tabelas
	private void selecionarPacientes(){
		
		int index = tabelaPacientes.getSelectedRow();
		String idPaciente = (String) tabelaPacientes.getModel().getValueAt(index, 0);
		
		controller.selecionarPaciente(idPaciente);
		removerItem(tabelaPacientes,index);
	}
	
	private void adicionarItem(JTable tabela, Paciente item){
		DefaultTableModel dtm = (DefaultTableModel) tabela.getModel();
		
		if(tabela.equals(tabelaPacientes)){
			//Verifica se a pessoa já foi selecionada, em caso de positivo, não é adicionada à lista com todos
			if(!jaSelecionado( item) && buscarID(tabela,item) == -1){ 
				dtm.addRow(new Object[]{item.getSensor().getId(),item.getNome()});
			}
		}else {
			Sensor s = item.getSensor();
			String repouso = s.estaEmRepouso()?"Sim":"Não";
			
			int index = buscarID(tabela, item);
			if(index == -1){ //Se não existir, adiciona
				dtm.addRow(new Object[]{s.getId(),item.getNome(),s.getBatimentos(),s.getPressaoSistolica(),s.getPressaoDiastolica(),repouso});
			}else { //Se existir atualiza
				dtm.setValueAt(item.getNome(), index, 1);
				dtm.setValueAt(s.getBatimentos(), index, 2);
				dtm.setValueAt(s.getPressaoSistolica(), index, 3);
				dtm.setValueAt(s.getPressaoDiastolica(), index, 4);
				dtm.setValueAt(repouso, index, 5);	
			}
			
		}
		
	}
	private void removerItem(JTable tabela, int indexLinha){
		DefaultTableModel dtm = (DefaultTableModel) tabela.getModel();
		dtm.removeRow(indexLinha);
	}
	
	
	private boolean jaSelecionado(Paciente item){
		DefaultTableModel dtm = (DefaultTableModel) tabelaPacientesSelecionados.getModel();
		int n = dtm.getRowCount();
		
		for(int i=0;i<n;i++){
			if(dtm.getValueAt(i, 0).equals(item.getSensor().getId())){
				return true;
			}
		}
		
		return false;
	}
	
	private int buscarID(JTable tabela, Paciente item){
		DefaultTableModel dtm = (DefaultTableModel) tabela.getModel();
		int n = dtm.getRowCount();
		
		for(int i=0;i<n;i++){
			if(dtm.getValueAt(i, 0).equals(item.getSensor().getId())){
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof MedicoController){
			if(((MedicoController) o).getTodosPacientes() != null){
				atualizarTodosPacientes();
			}
			
			if(((MedicoController) o).getPacientesPropensos() != null){
				atualizarPacientesPropensos();
			}
			
			if(((MedicoController) o).getPacientesSelecionados() !=null){
				atualizarPacientesSelecionados();
			}
		}
		
	}
	

	// Classes auxiliares
	private class PacientesSelecionados{
		public JPanel inicializar(){
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBounds(10, 309, 595, 251);
			getContentPane().add(panel);
			
			JTextPane txtpnPacientesSelecionados = new JTextPane();
			txtpnPacientesSelecionados.setBounds(0, 0, 134, 20);
			panel.add(txtpnPacientesSelecionados);
			txtpnPacientesSelecionados.setEditable(false);
			txtpnPacientesSelecionados.setText("Pacientes selecionados");

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 26, 595, 225);
			panel.add(scrollPane);
			panel.add(txtpnPacientesSelecionados);
			
			tabelaPacientesSelecionados = new JTable();
			tabelaPacientesSelecionados.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ID do Dispositivo", "Nome do paciente", "Batimentos", "Pressão Sistólica", "Pressão Diastólica", "Em repouso?"
				}
			){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				boolean[] columnEditables = new boolean[] {
					false, false,false,false,false,false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
			tabelaPacientesSelecionados.getColumnModel().getColumn(0).setPreferredWidth(100);
			tabelaPacientesSelecionados.getColumnModel().getColumn(1).setPreferredWidth(100);
			tabelaPacientesSelecionados.getColumnModel().getColumn(2).setPreferredWidth(60);
			tabelaPacientesSelecionados.getColumnModel().getColumn(3).setPreferredWidth(85);
			tabelaPacientesSelecionados.getColumnModel().getColumn(4).setPreferredWidth(85);
			tabelaPacientesSelecionados.getColumnModel().getColumn(5).setPreferredWidth(60);
			
			scrollPane.setViewportView(tabelaPacientesSelecionados);
						
			return panel;
		}
	}
	
	private class PacientesTotais{
		
		public JPanel inicializar(){
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, Color.BLACK, null, null, null));
			panel.setBounds(615, 11, 269, 549);
			getContentPane().add(panel);
			
			textField = new JTextField();
			textField.setBounds(10, 30, 159, 20);
			panel.add(textField);
			textField.setColumns(10);
			
			JButton btnNewButton = new JButton("Buscar");
			btnNewButton.setBounds(179, 29, 80, 23);
			panel.add(btnNewButton);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBounds(10, 61, 249, 477);
			panel.add(scrollPane);
			
			tabelaPacientes = new JTable();
			tabelaPacientes.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ID do Dispositivo", "Nome do Paciente"
				}
			) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				boolean[] columnEditables = new boolean[] {
					false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
			
			tabelaPacientes.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					if (e.getClickCount() == 2 && !e.isConsumed()) {
					     e.consume();
					     selecionarPacientes();
					}
				}
			});
			scrollPane.setViewportView(tabelaPacientes);
			
			JTextPane txtpnBuscaPorNome = new JTextPane();
			txtpnBuscaPorNome.setText("Busca por nome");
			txtpnBuscaPorNome.setEditable(false);
			txtpnBuscaPorNome.setBounds(10, 5, 101, 20);
			panel.add(txtpnBuscaPorNome);
			
			return panel;
		}
	}

	private class PacientesPropensos{
		
		public JPanel inicializar(){
			
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBounds(10, 11, 595, 287);
			getContentPane().add(panel);
			
			JTextPane txtpnPacientesPropensos = new JTextPane();
			txtpnPacientesPropensos.setBounds(0, 0, 218, 20);
			txtpnPacientesPropensos.setEditable(false);
			txtpnPacientesPropensos.setText("Pacientes propensos \u00E0 ataque card\u00EDaco");
			
			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(0, 27, 595, 260);
			panel.add(scrollPane_1);
			panel.add(txtpnPacientesPropensos);
			
			tabelaPacientesPropensos = new JTable();
			tabelaPacientesPropensos.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"ID do Dispositivo", "Nome do paciente", "Batimentos", "Pressão Sistólica", "Pressão Diastólica", "Em repouso?"
				}
			){
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				boolean[] columnEditables = new boolean[] {
					false, false,false,false,false,false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
			tabelaPacientesPropensos.getColumnModel().getColumn(0).setPreferredWidth(100);
			tabelaPacientesPropensos.getColumnModel().getColumn(1).setPreferredWidth(100);
			tabelaPacientesPropensos.getColumnModel().getColumn(2).setPreferredWidth(60);
			tabelaPacientesPropensos.getColumnModel().getColumn(3).setPreferredWidth(85);
			tabelaPacientesPropensos.getColumnModel().getColumn(4).setPreferredWidth(85);
			tabelaPacientesPropensos.getColumnModel().getColumn(5).setPreferredWidth(60);
			
			scrollPane_1.setViewportView(tabelaPacientesPropensos);
			
			return panel;
		}
	}
}
