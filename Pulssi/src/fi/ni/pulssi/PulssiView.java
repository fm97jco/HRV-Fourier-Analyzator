/*
 * Created on 15.9.2007
 *
 */
package fi.ni.pulssi;

import java.awt.Dimension;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;


/*
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class PulssiView
	extends JFrame
	implements ActionListener {

	public PulssiKomponentti pk;
	public PulssiView() {
		super();
		createGUI();
		pack();
		setVisible(true);
	}

	private void createGUI() {
		setSize(new Dimension(150, 1000));
		getContentPane().setLayout(
			new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		pk = new PulssiKomponentti();

		getContentPane().add(pk);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});


	}

	public void actionPerformed(ActionEvent e) {

	}

	public static void main(String[] args) {
		new PulssiView();
	}

}
