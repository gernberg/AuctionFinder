package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import GetWebsite.GetWebsite;
import GetWebsite.Lens;

public class MainWindow extends JPanel{
	JList list;
	DefaultListModel model;

	public MainWindow() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		
		
		
		model = new DefaultListModel();
		
		

		
	}

	protected void createAndShowGUI() {
		final JFrame frame = new JFrame("Auction Finder");
		final JButton getButton = new JButton("Hämta data");
		list = new JList();
		final JScrollPane scrollPane = new JScrollPane(list);
		
		scrollPane.setPreferredSize(new Dimension(500,500));
		
		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());
		pane.add(getButton,BorderLayout.SOUTH);
		pane.add(scrollPane, BorderLayout.NORTH);
		
		getButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						// Hämta listan här
						GetWebsite gw;
						List<Lens> allPages = null;
						try {
							gw = new GetWebsite();
							allPages = gw.getAllPages();
						} catch (Exception e) {
						}
						
						// Töm modellen så listan inte appendas varje gång knappen trycks ned
						model.removeAllElements();
						for (Lens lens : allPages){
							model.addElement(lens.getLens());
						}
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								// Uppdatera listan i framen här
								list.setListData(model.toArray());
							}
						});
					}
				};
				new Thread(runnable).start();
			}
		});
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}

	public static void main(String s[]) throws Exception {
		new MainWindow();
	}
}
