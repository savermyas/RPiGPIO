/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GPIOServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import jdk.dio.DeviceManager;
import jdk.dio.gpio.GPIOPin;

/**
 *
 * @author vagrant
 */
public class GPIOSocketServer extends MIDlet {

    private final Set<GPIOPin> changedPins;

    public GPIOSocketServer() {
        this.changedPins = new HashSet();
    }

    public boolean getPinStatus(int pinID) throws RemoteException {

        try {
            GPIOPin pin = (GPIOPin) DeviceManager.open(pinID, GPIOPin.class);
            changedPins.add(pin);
            pin.close();
            return pin.getValue();
            
        } catch (IOException ex) {
            Logger.getLogger(GPIOSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false; //FFFUUUUUUUUUUUUUUUU
    }

    public void setPinStatus(int pinID, boolean status) throws RemoteException {

        try {
            GPIOPin pin = (GPIOPin) DeviceManager.open(pinID, GPIOPin.class);
            changedPins.add(pin);
            pin.setValue(status);
            pin.close();
        } catch (IOException ex) {
            Logger.getLogger(GPIOSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exitPreparation() {
        for (GPIOPin i : changedPins) {
            //System.out.println("i");
            try {
                i.close();
            } catch (IOException ex) {
                Logger.getLogger(GPIOSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    ServerSocketConnection connection = null;
    DataInputStream in;
    DataOutputStream out;
    StreamConnection con;
    
    @Override
    protected void startApp() throws MIDletStateChangeException {
        String currIPAddress = getIPAddr();
        System.out.println(currIPAddress);

        boolean moveon = true;
        try {
          connection = (ServerSocketConnection) Connector.open("socket://:55443");  
          while(moveon) {
              System.out.println("NEW CYCLE");            
              
              System.out.println("WAITING FOR NEXT CLIENT");
              con = connection.acceptAndOpen();
              in = con.openDataInputStream();
              System.out.println("ServerSocket started");
              out = con.openDataOutputStream();

            
             while(moveon)
             {
                 
                String recx = in.readUTF();
                if(recx.startsWith("exit")) moveon = false;
                   else processMessage(recx, out);
             }
             
             in.close();
             out.close();
             con.close();
             
             
             //connection.close();
             
             moveon = true;
             
       
          } 
        
        

    }
    catch (IOException ex) {
            //Logger.getLogger(GPIOSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
    }

}

private String getIPAddr()
    {
         try {
            ServerSocketConnection ssc = null;
            try {                                           
               ssc = (ServerSocketConnection) Connector.open("socket://:1234");
            } catch (IOException ex) {
         
                ex.printStackTrace();
            }
                return ssc.getLocalAddress();
 
        } catch (IOException ex) {

                ex.printStackTrace();
        }
        return null;
    }


    private void processMessage(String recx, DataOutputStream out) {
        System.out.println(recx);
        try {
            if(recx.startsWith("STATUS"))
            {
                int pinID = Integer.parseInt(recx.substring(6, recx.length()).trim());
                System.out.println(this.getPinStatus(pinID));
            }
            else if(recx.startsWith("HIGH")) 
            {
                int pinID = Integer.parseInt(recx.substring(5, recx.length()).trim());
                this.setPinStatus(pinID, true);
            }
            else if(recx.startsWith("LOW")) 
            {
                int pinID = Integer.parseInt(recx.substring(4, recx.length()).trim());
                this.setPinStatus(pinID, false);
            }
            else if(recx.startsWith("DESTROY")) 
            {
                exitPreparation();
                in.close();
                out.close();
                con.close();
                connection.close();
                notifyDestroyed();
                
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(GPIOSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
