package GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import GetWebsite.GetWebsite;
import GetWebsite.Lens;

/*
 * TODO: Vore snyggt med laddruta/progressbar när data laddas ned
 * TODO: Fönster med mer info när man klickar på item i listan
 */
public class MainWindow extends JFrame{
	JList list;
	DefaultListModel model;
	GetWebsite gw;
	List<Lens> allPages;
	JPanel panel;
	JLabel pris;
	JLabel url;

	public MainWindow(final String title) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI(title);
			}
		});
		model = new DefaultListModel();
		allPages = null;
	}

	protected void createAndShowGUI(String title) {
		this.setTitle(title);
		final JButton getButton = new JButton("Hämta data");
		list = new JList();
		final JScrollPane scrollPane = new JScrollPane(list);
		panel = new JPanel();
		pris = new JLabel();
		url = new JLabel();
		panel.setLayout(new GridLayout(2,0,-20,0));
		panel.add(pris);
		panel.add(url);
		panel.setPreferredSize(new Dimension(500,100));
		scrollPane.setPreferredSize(new Dimension(500,500));
		
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());
		pane.add(getButton,BorderLayout.SOUTH);
		pane.add(scrollPane, BorderLayout.NORTH);
		pane.add(panel,BorderLayout.EAST);
		
		ListenerHelper lh = new ListenerHelper();
		
		getButton.addActionListener(lh);
		list.addMouseListener(lh);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		
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
						
						// Sortera på pris
						Collections.sort(allPages);
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
			final String stringPrice = Integer.toString(selectedLens.getPrice()).concat(selectedLens.getMonetaryUnit());
			final String stringUrl = selectedLens.getUrl();

			// Uppdatera textlables
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					pris.setText("Kostar: "+stringPrice);
					url.setText("URL: "+stringUrl);
				}
			});
		}
	}
	
	public static void main(String s[]) throws Exception {
		new MainWindow("Auction Finder");
	}
}
