/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.oracle;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vagrant
 */
public class GPIOConsoleClient {
    public static void main(String args[]) {
        Socket echoSocket;
        if(args.length>=2)
        try {
            echoSocket = new Socket(args[0], 55443);
            DataOutputStream out = new DataOutputStream(echoSocket.getOutputStream());
            String cmd = args[1];
            if(args.length>2)
            {
               cmd = cmd + " "+args[2] ;
            }
            out.writeUTF(cmd);
            out.writeUTF("exit");

        } catch (IOException ex) {
            Logger.getLogger(GPIOConsoleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
