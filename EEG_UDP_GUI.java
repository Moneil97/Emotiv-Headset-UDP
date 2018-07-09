package oneil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class EEG_UDP_GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField userNameInput;
	private JPasswordField passwordInput;
	private JTextField ipAddressInput;
	private SendEEGOverUDP eeg;
	private JTextField txthz;
	
	private String userName, password, ip;
	private int port;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary("edk");
					EEG_UDP_GUI frame = new EEG_UDP_GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EEG_UDP_GUI() {
		setTitle("Simulink EEG Importer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 360, 600);
		setMinimumSize(new Dimension(360, 350));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea messagesTextArea = new JTextArea();
		messagesTextArea.setEditable(false);
		//centerPanel.add(messagesTextArea);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(messagesTextArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		JLabel lblSystemMessages = new JLabel("System Messages:");
		centerPanel.add(lblSystemMessages, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		centerPanel.add(panel, BorderLayout.SOUTH);
		
		JLabel lblAverageSampleTime = new JLabel("Average Sample Time:");
		panel.add(lblAverageSampleTime);
		
		txthz = new JTextField();
		txthz.setEditable(false);
		txthz.setText("0Hz");
		panel.add(txthz);
		txthz.setColumns(10);
		
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
		
		userNameInput = new JTextField();
		userNameInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_2.add(userNameInput, BorderLayout.CENTER);
		userNameInput.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		northPanel.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPassword = new JLabel("Password:    ");
		panel_3.add(lblPassword, BorderLayout.WEST);
		
		passwordInput = new JPasswordField();
		passwordInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		panel_3.add(passwordInput, BorderLayout.CENTER);
		
		JCheckBox hideCheckBox = new JCheckBox("Hide");
		hideCheckBox.setSelected(true);
		hideCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hideCheckBox.isSelected())
					passwordInput.setEchoChar('*');
				else 
					passwordInput.setEchoChar((char) 0);
			}
		});
		panel_3.add(hideCheckBox, BorderLayout.EAST);
		
		JPanel panel_4 = new JPanel();
		northPanel.add(panel_4);
		
		JCheckBox saveLoginCheckBox = new JCheckBox("Save Login");
		saveLoginCheckBox.setToolTipText("WARNING: username and password will not be encrypted");
		panel_4.add(saveLoginCheckBox);
		
		JCheckBox skipLoginCheckBox = new JCheckBox("Skip Login");
		skipLoginCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				userNameInput.setEnabled(!skipLoginCheckBox.isSelected());
				passwordInput.setEnabled(!skipLoginCheckBox.isSelected());
			}
		});
		skipLoginCheckBox.setToolTipText("Login can be skipped if machine already has unused sessions ");
		panel_4.add(skipLoginCheckBox);
		
		JButton deleteSavedLoginButton = new JButton("Delete Saved Login");
		deleteSavedLoginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (new File("EmotivLogin").exists())
					new File("EmotivLogin").delete();
			}
		});
		panel_4.add(deleteSavedLoginButton);
		
		JPanel panel_6 = new JPanel();
		northPanel.add(panel_6);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JLabel lblIpAddress = new JLabel("IP Address:   ");
		panel_6.add(lblIpAddress, BorderLayout.WEST);
		
		ipAddressInput = new JTextField();
		ipAddressInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		ipAddressInput.setText("127.0.0.1");
		panel_6.add(ipAddressInput, BorderLayout.CENTER);
		ipAddressInput.setColumns(10);
		
		JPanel panel_7 = new JPanel();
		northPanel.add(panel_7);
		panel_7.setLayout(new BorderLayout(0, 0));
		
		JLabel lblPort = new JLabel("Port #:            ");
		panel_7.add(lblPort, BorderLayout.WEST);
		
		JSpinner portInput = new JSpinner();
		portInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		portInput.setValue(9092);
		panel_7.add(portInput, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (eeg == null || !userName.equals(userNameInput.getText()) || !password.equals(new String(passwordInput.getPassword()))
						        || !ip.equals(ipAddressInput.getText()) || port != (int)portInput.getValue()) {
					
					userName = userNameInput.getText();
					password = new String(passwordInput.getPassword());
					ip = ipAddressInput.getText();
					port = (int)portInput.getValue();
					
					if (!skipLoginCheckBox.isSelected()) {
						EmotivLicenseActivator ela = new EmotivLicenseActivator(userNameInput.getText(), new String(passwordInput.getPassword()));
						messagesTextArea.append(ela.login() + "\n");
						messagesTextArea.append(ela.getSessionsInfo() + "\n");
						messagesTextArea.append(ela.addSessions(1) + "\n");
						messagesTextArea.append(ela.getLicenseInfo() + "\n");
					}
					else {
						messagesTextArea.append("Skipping Login\n");
					}
					
					try {
						eeg = new SendEEGOverUDP(ip, port);
						eeg.start(txthz);
						Files.write(Paths.get("port"), Integer.toString(port).getBytes());

					} catch (IOException err) {
						err.printStackTrace();
						return;
					}
					
					if (saveLoginCheckBox.isSelected()) {
						try {
							byte data[] = (userName + "\n" + password + "\n").getBytes();
							for (int i = 0; i < data.length; i++)
								data[i] += 128;
							Path file = Paths.get("EmotivLogin");
							Files.write(file, data);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				
				messagesTextArea.append("\nUDP packets are now being sent to: " + ipAddressInput.getText() + " : " + portInput.getValue() + "\n");
			}
		});
		southPanel.add(btnStart);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eeg.stop();
				messagesTextArea.append("\nUDP packets have stopped\n");
			}
		});
		southPanel.add(btnStop);
		
		try {
			if (new File("EmotivLogin").exists()) {
				byte[] data = Files.readAllBytes(Paths.get("EmotivLogin"));
				for (int i = 0; i < data.length; i++)
					data[i] -= 128;
				
				String in = new String(data);
				userName = in.substring(0, in.indexOf("\n")).trim();
				password = in.substring(in.indexOf("\n")).trim();
				
				userNameInput.setText(userName);
				passwordInput.setText(password);
				saveLoginCheckBox.setSelected(true);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
