package GUI;

import java.awt.BorderLayout;
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

import GetWebsite.GetWebsite;
import GetWebsite.Lens;

public class MainWindow extends JPanel{
	JList list;

	DefaultListModel model;

	public MainWindow() throws Exception {
		
		GetWebsite gw = new GetWebsite();
		List<Lens> allPages = gw.getAllPages();
		
		setLayout(new BorderLayout());
		model = new DefaultListModel();
		
//		list.setPreferredSize(new Dimension(500,800));
		
		
		for (Lens lens : allPages){
			model.addElement(lens.getLens());
		}

		list = new JList(model);
		JScrollPane pane = new JScrollPane(list);
		int height = (int) Math.round(17.1*model.getSize());
		pane.setPreferredSize(new Dimension(500,height));
		add(pane, BorderLayout.NORTH);
	}

	public static void main(String s[]) throws Exception {
		JFrame frame = new JFrame("Auction Finder");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new MainWindow());
//		frame.setSize(500, 800);
		frame.pack();
		frame.setVisible(true);
	}
}
