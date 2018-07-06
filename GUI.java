package oneil;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.JPasswordField;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextArea;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JTextField textField_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		centerPanel.add(textArea);
		
		JLabel lblSystemMessages = new JLabel("System Messages:");
		centerPanel.add(lblSystemMessages, BorderLayout.NORTH);
		
		JPanel northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_5 = new JPanel();
		northPanel.add(panel_5);
		
		JLabel lblSimulinkEegImporter = new JLabel("Simulink EEG Importer (using UDP)");
		lblSimulinkEegImporter.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_5.add(lblSimulinkEegImporter);
		
		JPanel panel_2 = new JPanel();
		northPanel.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JLabel lblUsername = new JLabel("Username:   ");
		panel_2.add(lblUsername, BorderLayout.WEST);
		
		textField = new JTextField();
		panel_2.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		northPanel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPassword = new JLabel("Password:    ");
		panel_3.add(lblPassword, BorderLayout.WEST);
		
		passwordField = new JPasswordField();
		panel_3.add(passwordField, BorderLayout.CENTER);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Hide");
		chckbxNewCheckBox.setSelected(true);
		panel_3.add(chckbxNewCheckBox, BorderLayout.EAST);
		
		JPanel panel_4 = new JPanel();
		northPanel.add(panel_4);
		
		JCheckBox chckbxSaveLogin = new JCheckBox("Save Login");
		panel_4.add(chckbxSaveLogin);
		
		JPanel panel_6 = new JPanel();
		northPanel.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JLabel lblIpAddress = new JLabel("IP Address:   ");
		panel_6.add(lblIpAddress, BorderLayout.WEST);
		
		textField_1 = new JTextField();
		panel_6.add(textField_1, BorderLayout.CENTER);
		textField_1.setColumns(10);
		
		JPanel panel_7 = new JPanel();
		northPanel.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPort = new JLabel("Port #:            ");
		panel_7.add(lblPort, BorderLayout.WEST);
		
		JSpinner spinner = new JSpinner();
		spinner.setValue(9092);
		panel_7.add(spinner, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnStart = new JButton("Start");
		southPanel.add(btnStart);
		
		JButton btnStop = new JButton("Stop");
		southPanel.add(btnStop);
	}

}
