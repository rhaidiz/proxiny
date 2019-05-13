package proxiny.gui;

import proxiny.gui.JSONRPC.RpcServer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Main {

    private static MainWindow newGui;

    private static Process proxyProcess;

    public static void main (String args[]){

        // start the GUI interface
        newGui = new MainWindow();
        newGui.setVisible(true);

        System.setProperty("java.net.preferIPv4Stack", "true");

        ProxyServerStatus serverStatus = ProxyServerStatus.getInstance();
        serverStatus.addPropertyChangeListener(newGui.getStatsChangeListener());

        // start the JSONRPC server
        try {
            RpcServer.StartJSONRPCServer();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(),"Address in use, change it.","Alert",JOptionPane.WARNING_MESSAGE);
            System.exit(-1);
        }

        // spawn the core proxy
        Process process = null;
        try {
            String proxinyCore = System.getProperty("proxinycore");
            process = new ProcessBuilder(proxinyCore,"gui").redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // terminate everything at the end
        Process finalProcess = process;
        proxyProcess = process;
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                close();

            }
        });


        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);


        String line;



        try {
        while (true) {
                if ((line = br.readLine()) == null) break;
                System.out.println(line);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    public static void close(){
        proxyProcess.destroy();
        System.out.println("exiting");
    }

}
