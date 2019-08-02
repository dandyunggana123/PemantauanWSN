
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.imageio.ImageIO;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultCaret;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.virtenio.commander.io.DataConnection;
import com.virtenio.commander.toolsets.preon32.Preon32Helper;

import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JComboBox;

public class GUI {
	private static JFrame frame;
	private static JTextArea log;
	private static JButton btnStart;
	private static JButton btnStop;
	private static JButton btnRestart;
	private static JPanel panelDraw;
	private static JComboBox<Object> comboBox;
	private static String[] state;
	private static String[] name;
	private static int number;
	private static Object[][] dataTable;
	private static JTable table;

	/**
	 * Launch the application.
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		GUI.context_set("context.set.1");
		GUI.time_synchronize();
		init();
	}

	/**
	 * Create the application.
	 */
	public GUI() throws Exception {
		name = SensorName.txt_to_arrayOfString();
		state = new String[name.length];
		for (int i = 0; i < state.length; i++) {
			state[i] = "none";
		}
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() throws Exception {

		frame = new JFrame("Show Me Sensor");
		frame.setResizable(false);
		frame.setBounds(10, 10, 1024, 730);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		BufferedImage image1 = ImageIO.read(new File("C:\\Users\\Dandy\\eclipse-workspace\\base_station.png"));
		BufferedImage image2 = ImageIO.read(new File("C:\\Users\\Dandy\\eclipse-workspace\\node_sensor.png"));
		BufferedImage image3 = ImageIO.read(new File("C:\\Users\\Dandy\\eclipse-workspace\\black_sensor.jpg"));
		BufferedImage image4 = ImageIO.read(new File("C:\\Users\\Dandy\\eclipse-workspace\\no_sensor.jpg"));

		panelDraw = new javax.swing.JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				int x = 120, y = 10;
				g.drawImage(image1, 30, 10, 50, 50, this);
				for (int i = 0; i < state.length; i++) {
					if (state[i].equalsIgnoreCase("on")) {
						g.drawImage(image2, x, y, 50, 50, this);
					} else if (state[i].equalsIgnoreCase("off")) {
						g.drawImage(image3, x, y, 50, 50, this);
					} else {
						g.drawImage(image4, x, y, 50, 50, this);
					}
					if (x + 90 < 710) {
						x += 90;
					} else {
						x = 30;
						y += 90;
					}
				}
				g.setColor(Color.RED);
				g.fillRect(629, 481, 9, 9);
				g.setColor(Color.green);
				g.fillRect(629, 496, 9, 9);
			}
		};
		JLabel baseStationName = new JLabel("Base Station(AFFE)");
		baseStationName.setHorizontalAlignment(SwingConstants.CENTER);
		baseStationName.setBounds(2, 60, 110, 14);
		panelDraw.add(baseStationName);

		JLabel[] namaLabel = new JLabel[name.length];
		int x = 120, y = 60;
		for (int i = 0; i < namaLabel.length; i++) {
			int num = i + 1;
			namaLabel[i] = new JLabel(num + ". " + name[i]);
			namaLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
			namaLabel[i].setBounds(x, y, 52, 14);
			panelDraw.add(namaLabel[i]);
			if (x + 90 < 710) {
				x += 90;
			} else {
				x = 30;
				y += 90;
			}
		}
		panelDraw.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		panelDraw.setBackground(Color.WHITE);
		panelDraw.setBounds(0, 0, 716, 543);
		frame.getContentPane().add(panelDraw);
		panelDraw.setLayout(null);

		JLabel baseStation = new JLabel("Base Station");
		baseStation.setBounds(639, 481, 82, 14);
		panelDraw.add(baseStation);

		JLabel nodeSensor = new JLabel("Node Sensor");
		nodeSensor.setBounds(639, 498, 75, 14);
		panelDraw.add(nodeSensor);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(715, 0, 303, 701);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblCurrentValue = new JLabel("Current Value");
		lblCurrentValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblCurrentValue.setBounds(60, 11, 129, 24);
		panel.add(lblCurrentValue);

		btnStop = new JButton("Turn Off");
		btnStop.setBounds(34, 567, 100, 23);
		btnStop.setEnabled(false);
		panel.add(btnStop);
		btnStop.setToolTipText("change the state of node to off for low power consumption");

		btnRestart = new JButton("Restart");
		btnRestart.setBounds(158, 567, 89, 23);
		btnRestart.setEnabled(false);
		panel.add(btnRestart);
		btnRestart.setToolTipText("change the state of node to off then on again");

		btnStart = new JButton("Start");
		btnStart.setBounds(95, 638, 116, 23);
		btnStart.setEnabled(false);
		panel.add(btnStart);
		btnStart.setToolTipText("Start sensing and time synchronize");

		String[] columnNames = { "Num", "Name", "State", "[°C]", "RH", "mbar" };
		dataTable = new Object[state.length][6];

		for (int i = 0; i < name.length; i++) {
			dataTable[i][0] = i + 1;
			dataTable[i][1] = name[i];
			dataTable[i][2] = state[i];
		}

		table = new JTable(dataTable, columnNames);
		table.setEnabled(false);
		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.setBounds(10, 46, 277, 464);
		panel.add(scrollTable);
		scrollTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		comboBox = new JComboBox<Object>(name);
		comboBox.setBounds(105, 521, 84, 20);
		panel.add(comboBox);

		log = new JTextArea();
		log.setEditable(false);
		log.setLineWrap(true);
		// log.setBounds();
		// log.setVisible(true);

		JScrollPane scroll = new JScrollPane(log);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 554, 695, 136);
		DefaultCaret caret = (DefaultCaret) log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		frame.getContentPane().add(scroll);

	}

	private static void context_set(String target) throws Exception {
		DefaultLogger consoleLogger = getConsoleLogger();
		// Prepare ant project
		File buildFile = new File("D:\\Sandbox\\buildUser.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);

		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			antProject.addReference("ant.ProjectHelper", helper);
			helper.parse(antProject, buildFile);
			//
			antProject.executeTarget(target);
			antProject.fireBuildFinished(null);
		} catch (BuildException e) {
			e.printStackTrace();
		}
	}

	private static void time_synchronize() throws Exception {
		DefaultLogger consoleLogger = getConsoleLogger();
		File buildFile = new File("D:\\Sandbox\\build.xml");
		Project antProject = new Project();
		antProject.setUserProperty("ant.file", buildFile.getAbsolutePath());
		antProject.addBuildListener(consoleLogger);

		try {
			antProject.fireBuildStarted();
			antProject.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			antProject.addReference("ant.ProjectHelper", helper);
			helper.parse(antProject, buildFile);
			//
			String target = "cmd.time.synchronize";
			antProject.executeTarget(target);
			antProject.fireBuildFinished(null);
		} catch (BuildException e) {
			e.printStackTrace();
		}
	}

	private static DefaultLogger getConsoleLogger() {
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);

		return consoleLogger;
	}

	public static void init() throws Exception {
		Preon32Helper nodeHelper = new Preon32Helper("COM5", 115200);
		DataConnection conn = nodeHelper.runModule("BaseStation");

		// variabel in berguna untuk menerima input yang akan dikirim oleh base station
		// ke pc(GUI.java)
		BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
		btnStartPressed(in, conn);
		btnStopOrResPressed();
	}

	private static void btnStartPressed(BufferedInputStream in, DataConnection conn) {
		btnStart.setEnabled(true);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {
						number = 50;
						btnStop.setEnabled(true);
						btnRestart.setEnabled(true);
						btnStart.setEnabled(false);
						boolean[] isON = new boolean[state.length];
						int[] countOFF = new int[state.length];
						for (int i = 0; i < state.length; i++) {
							countOFF[i] = 8;
						}
						do {
							for (int i = 0; i < state.length; i++) {
								isON[i] = false;
							}
							try {

								conn.flush();
								// number lebih kecil dari 50 artinya turn off
								// number lebih besar dari 99 artinya restart
								// number = 50 artinya sense
								conn.write(number);
								byte[] buffer = new byte[1024];
								Thread.sleep(5000);
								while (in.available() > 0) {
									in.read(buffer);
									String s = new String(buffer);
									conn.flush();

									String[] subNode = s.split("%");
									for (int j = 0; j < subNode.length - 1; j++) {
										String[] subStr = subNode[j].split("#");
										for (int i = 0; i < state.length; i++) {
											String result;
											Date timeRaw;
											String clock;
											if (subStr[0].equalsIgnoreCase(name[i])) {
												// untuk menangani duplikat pesan
												if (isON[i]) {
													break;
												}
												if (!subStr[1].equalsIgnoreCase("restart")
														&& !subStr[1].equalsIgnoreCase("stop")) {
													isON[i] = true;
													countOFF[i] = 1;
													// menyimpan nilai sensing dalam float yang awalnya berupa string
													float temperature = Float.parseFloat(subStr[1]);
													float humidity = Float.parseFloat(subStr[2]);
													// satuan awal barometer adalah kpa dan diubah menjadi milibar
													float barometer = Float.parseFloat(subStr[3]) / 100;

													// mengatur waktu
													timeRaw = new Date(Long.parseLong(subStr[4]));
													String timeForFile = new SimpleDateFormat("dd-MMM-yyyy")
															.format(timeRaw);
													clock = new SimpleDateFormat("kk:mm:ss").format(timeRaw);

													// menyimpan nilai log pada file
													File file = new File("C:/Users/Dandy/eclipse-workspace/UI/src/log/"
															+ timeForFile + ".txt");
													FileWriter fw = new FileWriter(file, true);
													BufferedWriter bw = new BufferedWriter(fw);
													PrintWriter save = new PrintWriter(bw);

													state[i] = "on";
													dataTable[i][2] = state[i];
													dataTable[i][3] = String.format("%.0f", temperature);
													dataTable[i][4] = String.format("%.2f", humidity);
													dataTable[i][5] = String.format("%.4f", barometer);

													result = "[ " + clock + " ]" + subStr[0].toUpperCase() + " :"
															+ String.format("%.0f", temperature) + " [°C], "
															+ String.format("%.2f", humidity) + " RH, "
															+ String.format("%.4f", barometer) + " bar" + "\n";

													log.append(result);
													save.println(result);
													save.close();
													break;
												} else {
													isON[i] = true;
													timeRaw = new Date(Long.parseLong(subStr[2]));
													clock = new SimpleDateFormat("kk:mm:ss").format(timeRaw);
													if (subStr[1].equalsIgnoreCase("restart")) {
														subStr[1] = "restarting";
													} else {
														subStr[1] = "stopping";
													}
													result = "[ " + clock + " ]" + subStr[0].toUpperCase() + " is "
															+ subStr[1] + "\n";
													log.append(result);
													state[i] = "off";
													dataTable[i][2] = state[i];
													countOFF[i] = 4;
													break;

												}
											}
										}
									}
								}
								for (int i = 0; i < isON.length; i++) {
									if (!isON[i]) {
										if (countOFF[i] < 4) {
											countOFF[i]++;
										} else if (countOFF[i] >= 4 && countOFF[i] < 8) {
											// mati
											System.out.println("mati" + countOFF[i] + i);
											state[i] = "off";
											dataTable[i][2] = state[i];
											countOFF[i]++;
										} else {
											state[i] = "none";
											dataTable[i][2] = state[i];
										}
									}
								}

								// melakukan update pada gambar dan tabel
								table.repaint();
								panelDraw.repaint();
							} catch (Exception ex) {
							}
						} while (true);
					}
				};
				t.start();
			}
		});
	}

	private static void btnStopOrResPressed() {
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {
						try {
							number = comboBox.getSelectedIndex();
							btnStop.setEnabled(false);
							btnRestart.setEnabled(false);
							Thread.sleep(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						number = 50;
						btnStop.setEnabled(true);
						btnRestart.setEnabled(true);
					}
				};
				t.start();
			}
		});
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread() {
					public void run() {
						try {
							btnStop.setEnabled(false);
							btnRestart.setEnabled(false);
							number = 100 + comboBox.getSelectedIndex();
							Thread.sleep(5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						btnStop.setEnabled(true);
						btnRestart.setEnabled(true);
						number = 50;
					}
				};
				t.start();
			}
		});
	}
}
