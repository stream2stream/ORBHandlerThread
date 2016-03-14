/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corbastuff;

import  corbastuff.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Selvyn
 */
public class codeWalker 
{
    static  public  void main( String args[] )
    {
        Thread tt = new Thread( KeyboardHandler.getInstance() );
        
        tt.start();
        
        System.out.println("Waiting for a quit command");
        synchronized( KeyboardHandler.getInstance().itsMonitor )
        {
            try {
                KeyboardHandler.getInstance().itsMonitor.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(codeWalker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Application has quit...");
    }
}
