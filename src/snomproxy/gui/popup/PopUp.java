package snomproxy.gui.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import sas.swing.MultiLineLabel;
import snomproxy.SnomProxy;

/**
 *
 * @author Philip Wassermann <pflipp at gmail.com>, Fabian Dillmeier <fabian at
 * dillmeier.de>
 */
public class PopUp extends JFrame implements ActionListener {

	MainPanel main_panel;
	CloseButton closeBtn;
	private int timeout = 30000;
	private Thread timeout_thread;
	private final int height = 116;
	private final int width = 430;
	private final int gap = 10;

	public PopUp() {
		super();
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setUndecorated(true);
		try {
			com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.95f);
		} catch (Exception e) {
			com.sun.awt.AWTUtilities.setWindowOpaque(this, false);
		}
		main_panel = new MainPanel();

		main_panel.setBackground(new Color(42, 161, 229));
		this.add(main_panel);

		closeBtn = new CloseButton("X");
		closeBtn.addActionListener(this);
		setBounds(PopUpList.max_bounds.width - (width + gap), PopUpList.max_bounds.height - (height + gap), width, height);
	}

	public PopUp(JPanel content) {
		this();
		setContent(content);
		open();
	}

	public PopUp(String content) {
		this();
		setContent(content);
		open();
	}

	public PopUp(String content, boolean html) {
		this();
		setContent(content, html);
		open();
	}

	public void open() {
		if (!isVisible()) {
			setVisible(true);
			if (timeout > 0) {
				timeout_thread = new Thread(new TimeoutRunnable());
				timeout_thread.start();
			}
		}
	}

	public void close() {
		this.setVisible(false);
		this.dispose();
		if (timeout_thread != null) {
			timeout_thread.interrupt();
			timeout_thread = null;
		}
	}

	public final void setContent(JPanel content) {
		main_panel.removeAll();
		main_panel.add(closeBtn, BorderLayout.EAST);
//		content.setSize(main_panel.getSize());
		main_panel.add(content, BorderLayout.NORTH);
	}

	public final void setContent(String content) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 31));
		panel.setBackground(new Color(0, 0, 0, 0));
		MultiLineLabel lbl = new MultiLineLabel(content);
		
		lbl.setVerticalAlignment(JLabel.CENTER);
		lbl.setHorizontalAlignment(JLabel.CENTER);
		panel.add(lbl, BorderLayout.CENTER);
		setContent(panel);
	}

	public final void setContent(String content, boolean html) {
		if (!html) {
			setContent(content);
		} else {
			JPanel panel = new JPanel(new BorderLayout());
			panel.setBackground(new Color(0, 0, 0, 0));
			panel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 31));
			panel.add(createHTMLPane(content), BorderLayout.CENTER);
			setContent(panel);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.close();
	}

	private static JTextPane createHTMLPane(final String content) {
		StyleContext sc = new StyleContext();
		final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
		JTextPane pane = new JTextPane(doc);
		pane.setBackground(new Color(0, 0, 0, 0));
		pane.setCursor(Cursor.getDefaultCursor());
		// Create and add the main document style
		Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
		final Style mainStyle = sc.addStyle("MainStyle", defaultStyle);
		StyleConstants.setFontFamily(mainStyle, "sans-serif");
		StyleConstants.setFontSize(mainStyle, 14);
		StyleConstants.setBold(mainStyle, true);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					doc.setLogicalStyle(0, mainStyle);
					try {
						doc.insertString(0, content, null);
					} catch (BadLocationException e) {
					}
				}
			});
		} catch (Exception e) {
			SnomProxy.getLogger().log(Level.WARNING,"Exception when constructing document: ".concat(e.getMessage()));
		}
		return pane;
	}

	private class MainPanel extends JPanel {

		public MainPanel(LayoutManager layout_manager) {
			super(layout_manager);
		}

		public MainPanel() {
			super(new BorderLayout(3,3));
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(Color.BLACK);
			g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 10, 10);
			g2d.setColor(this.getBackground());
			g2d.fillRoundRect(3, 3, this.getWidth() - 6, this.getHeight() - 6, 10, 10);
		}
	}

	private class TimeoutRunnable implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(timeout);
				close();
			} catch (InterruptedException ex) {
			}
		}
	}
}
