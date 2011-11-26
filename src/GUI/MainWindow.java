package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import GetWebsite.GetWebsite;
import GetWebsite.Lens;
/*
 * TODO: Vore snyggt med laddruta/progressbar när data laddas ned
 * TODO: Fönster med mer info när man klickar på item i listan
 */
public class MainWindow extends JPanel{
	JList list;
	DefaultListModel model;
	GetWebsite gw;
	List<Lens> allPages;

	public MainWindow() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		model = new DefaultListModel();
		allPages = null;
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
		
		ListenerHelper lh = new ListenerHelper();
		
		getButton.addActionListener(lh);
		list.addMouseListener(lh);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	class ListenerHelper implements ActionListener, MouseListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					// Hämta listan här
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

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			System.out.println("Valt: "+list.getSelectedValue());
			Lens selectedLens = null;
			for(Lens lens:allPages){
				if(lens.getLens().equals(list.getSelectedValue())){
					selectedLens = lens;
				}
			}
			System.out.println("Kostar: "+selectedLens.getPrice()+selectedLens.getMonetaryUnit());
			System.out.println("URL: "+selectedLens.getUrl());
		}
	}
	
	public static void main(String s[]) throws Exception {
		new MainWindow();
	}
}
