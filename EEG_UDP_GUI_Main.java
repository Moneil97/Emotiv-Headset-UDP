package oneil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

public class EEG_UDP_GUI_Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ipAddressInput;
	private SendEEGOverUDP eeg;
	private JTextField systemMessageBox;
	private JButton btnPause, btnStart, btnStop;
	
	private String ip;
	private int port;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.loadLibrary("edk");
					EEG_UDP_GUI_Main frame = new EEG_UDP_GUI_Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EEG_UDP_GUI_Main() {
		setTitle("Emotiv Simulink EEG Importer (UDP)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 360, 600);
		setMinimumSize(new Dimension(360, 350));
		setLocationRelativeTo(null);
		
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
		
		systemMessageBox = new JTextField();
		systemMessageBox.setEditable(false);
		systemMessageBox.setText("0Hz");
		panel.add(systemMessageBox);
		systemMessageBox.setColumns(10);
		
		JPanel northPanel = new JPanel();
		contentPane.add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_5 = new JPanel();
		northPanel.add(panel_5);
		
		JLabel lblSimulinkEegImporter = new JLabel("Emotiv Simulink EEG Importer (UDP)");
		lblSimulinkEegImporter.setFont(new Font("Tahoma", Font.PLAIN, 20));
		panel_5.add(lblSimulinkEegImporter);
		
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
		portInput.setModel(new SpinnerNumberModel(9092, 1024, 65534, 1));
		portInput.setFont(new Font("Tahoma", Font.PLAIN, 14));
		portInput.setValue(9092);
		panel_7.add(portInput, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);
		southPanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				btnStart.setEnabled(false);
				btnPause.setEnabled(true);
				btnStop.setEnabled(true);
				
				if (eeg == null) {
					
					//make this changeable later
					ipAddressInput.setEnabled(false);
					portInput.setEnabled(false);
					
					ip = ipAddressInput.getText();
					port = (int)portInput.getValue();
					
					try {
						eeg = new SendEEGOverUDP(ip, port);
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					}
				}

				try {
					eeg.start(systemMessageBox);
					Files.write(Paths.get("port"), Integer.toString(port).getBytes());
					messagesTextArea.append("\nUDP packets are now being sent to: " + ipAddressInput.getText() + " : " + portInput.getValue() + "\n");

				} catch (IOException err) {
					err.printStackTrace();
					return;
				}
			}
		});
		southPanel.add(btnStart);
		
		btnPause = new JButton("Pause");
		btnPause.setEnabled(false);
		btnPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(true);
				btnPause.setEnabled(false);
				eeg.pause();
				messagesTextArea.append("\nUDP packets have stopped\n");
			}
		});
		southPanel.add(btnPause);
		
		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnStart.setEnabled(true);
				btnPause.setEnabled(false);
				btnStop.setEnabled(false);
				
				eeg.stop();
				eeg = null;
			}
		});
		southPanel.add(btnStop);
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
		    	if (eeg != null)
		    		eeg.stop();
		    }
		});
		
	}
}
