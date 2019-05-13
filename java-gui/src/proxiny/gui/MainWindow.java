package proxiny.gui;

import proxiny.gui.JSONRPC.RpcClient;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainWindow extends JFrame {
    private JPanel rootPanel;
    private JTextField ipPortText;
    private JButton startStopButton;
    private JLabel requestsLabel;
    private JLabel responsesLabel;
    private RpcClient rpcClient = null;
    private boolean proxyOn = false;
    private MainWindow me;

    private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String property = event.getPropertyName();
            if (property.equals(ProxyServerStatus.REQUESTS)) {
                int newReqs = (int)event.getNewValue();
                updateRequests(newReqs);
            }
            if (property.equals(ProxyServerStatus.RESPONSES)) {
                int newResps = (int)event.getNewValue();
                updateResponses(newResps);
            }
            if (property.equals(ProxyServerStatus.FATAL) || property.equals(ProxyServerStatus.ERROR)){
                // there's an error with the proxy server, ouch!
                //JOptionPane.showMessageDialog(new JFrame(),"Successfully Updated.","Alert",JOptionPane.WARNING_MESSAGE);

                /*JOptionPane pane = new JOptionPane();
                JDialog dialog = pane.createDialog("Error");
                pane.setMessage(ProxyServerStatus.getInstance().getErrorMessage());
                pane.setMessageType(JOptionPane.ERROR_MESSAGE);
                dialog.setAlwaysOnTop(true);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        // shutdown
                        //System.exit(-1);
                        dialog.setVisible(false);
                    }
                });
                */
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // Here, we can safely update the GUI
                        // because we'll be called from the
                        // event dispatch thread
                        JOptionPane.showMessageDialog(new JFrame(),ProxyServerStatus.getInstance().getErrorMessage(),"Alert",JOptionPane.WARNING_MESSAGE);
                        if(property.equals(ProxyServerStatus.FATAL)){
                            System.exit(-1);
                        }
                        //dialog.setVisible(true);
                    }
                });

            }

        }
    };

    public MainWindow() {
        me = this;
        setTitle("Proxiny");
        add(rootPanel);
        setSize(300, 120);
        rpcClient = RpcClient.getInstance();
        startStopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!proxyOn)
                    startProxy();
                else
                    stopProxy();
            }
        });
    }

    private void startProxy() {
        // here I need the JSON RPC client
        if( rpcClient.startProxy(ipPortText.getText()) == 0){
            proxyOn = true;
            startStopButton.setText("Stop");
        }else{
            // there's an error, show it
            showErrorMessage();
        }
    }

    private void stopProxy() {
        // here I need the JSON RPC client
        if(rpcClient.stopProxy() == 0){
            proxyOn = false;
            startStopButton.setText("Start");
        }else{
            showErrorMessage();
        }
    }

    private void showErrorMessage(){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Here, we can safely update the GUI
                // because we'll be called from the
                // event dispatch thread
                JOptionPane.showMessageDialog(new JFrame(),ProxyServerStatus.getInstance().getErrorMessage(),"Alert",JOptionPane.WARNING_MESSAGE);
                //dialog.setVisible(true);
            }
        });
    }

    public void updateRequests(final int newReqs) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Here, we can safely update the GUI
                // because we'll be called from the
                // event dispatch thread
                requestsLabel.setText("Requests: " + newReqs);
            }
        });
    }

    public void updateResponses(final int newResps) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Here, we can safely update the GUI
                // because we'll be called from the
                // event dispatch thread
                responsesLabel.setText("Responses: " + newResps);
            }
        });
    }

    public PropertyChangeListener getStatsChangeListener(){
        return propertyChangeListener;
    }
}
