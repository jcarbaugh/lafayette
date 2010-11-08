package edu.american.weiss.lafayette.chamber;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.MissingResourceException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.american.weiss.lafayette.event.ChamberEvent;
import edu.american.weiss.lafayette.event.ReinforcerCompleteEvent;
import edu.american.weiss.lafayette.event.ReinforcerEvent;
import edu.american.weiss.lafayette.event.listener.ChamberEventListener;
import edu.american.weiss.lafayette.Application;

public class UserInterface extends JFrame implements ChamberEventListener {

	private static final long serialVersionUID = -8345425150947645683L;
	private static final String SHOW_CURSOR_PROPERTY_KEY = "show_cursor";
	private static final String SHOW_DEBUGBAR_PROPERTY_KEY = "show_debugbar";
	
    private boolean isFullScreen    = false;
    private boolean isInitialized   = false;
    private boolean showCursor		= false;
    private boolean showDebugBar	= false;
    
    private int screenResolution;
    
    private DisplayMode originalDisplayMode;
    private GraphicsDevice device;
    private InputListener listener;
    
    private Cursor cur;
    private JButton btnExit;
    private JLabel lblStatus;
    private JPanel pnlResponseArea;
    private JPanel pnlResponseContainer;
    private JPanel pnlHopperIndicator;
    
    protected UserInterface(GraphicsDevice device) throws MissingResourceException {
    	
    	super(device.getDefaultConfiguration());
	    
        setTitle("Lafayette");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        originalDisplayMode = device.getDisplayMode();
        
        screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
    	
    	this.device = device;
    	listener = new InputListener();
    	
    	showCursor = Application.getBooleanProperty(SHOW_CURSOR_PROPERTY_KEY);
    	showDebugBar = Application.getBooleanProperty(SHOW_DEBUGBAR_PROPERTY_KEY);
	    
    }

    protected void initComponents() {
        
        this.setFocusable(true);
        this.requestFocus(true);
    	
    	Container c = getContentPane();
    	
        btnExit = new JButton("Exit");
        btnExit.addActionListener(listener);
        
        if (showCursor) {
            cur = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        } else {
        	cur = Toolkit.getDefaultToolkit().createCustomCursor(
        			new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
        }
                
        pnlHopperIndicator = new JPanel();
        pnlHopperIndicator.setBackground(Color.GRAY);
        pnlHopperIndicator.setSize(80, 80);
        
        pnlResponseArea = new JPanel();
        pnlResponseArea.setCursor(cur);
        
        pnlResponseContainer = new JPanel(new BorderLayout());
        pnlResponseContainer.add(pnlResponseArea, BorderLayout.CENTER);
        
        lblStatus = new JLabel();
        lblStatus.setForeground(Color.WHITE);

        pnlResponseArea.setBackground(Color.WHITE);

        c.setLayout(new BorderLayout());
        
        if (showDebugBar) {
        	
        	JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            exitPanel.add(pnlHopperIndicator);
            exitPanel.add(btnExit);
            exitPanel.add(lblStatus);
            exitPanel.setBackground(Color.BLACK);
            
        	c.add(exitPanel, BorderLayout.SOUTH);
        	
        }
        
        c.add(pnlResponseContainer, BorderLayout.CENTER);

        setContentPane(c);

    }
    
    public void init() {
        
        if (!isInitialized) {
    	
            isFullScreen = device.isFullScreenSupported();
    		
            setUndecorated(isFullScreen);
            setResizable(!isFullScreen);
            
            if (isFullScreen) {
                device.setFullScreenWindow(this);
                validate();
            } else {
                pack();
                setVisible(true);
            }
            
            isInitialized = true;
            
        }
            
    }
    
    public void registerMouseListener(MouseListener ml) {
        this.addMouseListener(ml);
        pnlResponseArea.addMouseListener(ml);
    }
    
    public void registerKeyListener(KeyListener kl) {
        this.addKeyListener(kl);
        pnlResponseArea.addKeyListener(kl);
    }
    
    public Graphics2D getResponseGraphics() {
        return (Graphics2D) pnlResponseArea.getGraphics();
    }
    
    public Dimension getResponseSize() {
        return pnlResponseArea.getSize();
    }
    
    public Dimension getScreenDimension() {
        //Graphics2D g2 = (Graphics2D) pnlResponseArea.getGraphics();
        return pnlResponseArea.getSize();
    }
    
    public int getScreenResolution() {
    	return screenResolution;
    }
    
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    public void destroy() {
        device.setDisplayMode(originalDisplayMode);
        this.setVisible(false);
    }
    
    private class InputListener implements ActionListener {
    	
        public void actionPerformed(ActionEvent ev) {
            Object source = ev.getSource();
            if (source == btnExit) {
            	Application.shutdown();
            }
        }
        
    }
    
    public void writeMessage(String message) {
        lblStatus.setText(message);
    }
    
    public void setHopperIndicator(boolean isOn) {
        if (isOn) {
            pnlHopperIndicator.setBackground(Color.GREEN);
        } else {
            pnlHopperIndicator.setBackground(Color.GRAY);
        }
    }

	public void handleChamberEvent(ChamberEvent ce) {
		if (ce instanceof ReinforcerEvent) {
			setHopperIndicator(true);
		} else if (ce instanceof ReinforcerCompleteEvent) {
			setHopperIndicator(false);
		}
	}

}