/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corbastuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Selvyn
 */
public class KeyboardHandler implements Runnable
{
    public  Object itsMonitor = new Object();
    static  private KeyboardHandler self = null;
    
    public  static  KeyboardHandler  getInstance()
    {
        if( self == null )
            self = new KeyboardHandler();

        return self;
    }
    
    public	String	readString()
    {
        String result = null;
        InputStreamReader isr = new InputStreamReader( System.in );
        BufferedReader br = new BufferedReader( isr );
        
        try
        {
            result = br.readLine();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }

    @Override
    public void run() 
    {
        String  textInput = "";
        
        while( ! textInput.equalsIgnoreCase("quit"))
        {
            System.out.print("\ntype quit to exit: ");
            textInput = readString();
        }
        synchronized( itsMonitor )
        {
            itsMonitor.notify();
        }
    }
}
