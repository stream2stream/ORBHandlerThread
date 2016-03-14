/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corbastuff;

import org.omg.PortableServer.IdAssignmentPolicy;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

/**
 *
 * @author selvyn
 */
public class ORBHandler extends Thread
{
    public  static  ORBHandler self = null;
    
    //public static   boolean blocker = true;
    public static   String[] its_args;
    public static   POA its_rootPOA;
    public static   POA its_POA;
    public static   org.omg.CORBA.ORB its_ORB = null;
    static private String  itsProxyIdCommand = "-proxyid:";
    private String  itsProxyId = "127.0.0.1";
    static private String  itsServerPortCommand = "-serverport:";
    private String  itsServerPort = "5555";

    public  Object itsMonitor = new Object();
    
    public ORBHandler()
    {
    }

    public void setServerPort(String itsServerPort)
    {
        this.itsServerPort = itsServerPort;
    }

    public void setArgs(String[] p_args)
    {
        its_args = p_args;
    }

    public  static  ORBHandler  getInstance()
    {
        if( self == null )
            self = new ORBHandler();

        return self;
    }
    public void shutdownOrderly()
    {
        its_ORB.shutdown(true);
        its_ORB.destroy();
    }

    @Override
    public void run()
    {
        try
        {
            its_ORB = null;

            Properties props = System.getProperties();
            props.put("org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB");
            props.put("org.omg.CORBA.ORBSingletonClass",
                "org.jacorb.orb.ORB");

            for( int argv = 0; argv < its_args.length; argv++ )
            {
                int idx = its_args[argv].indexOf(itsProxyIdCommand);
                if( idx > -1 )
                {
                    String tempProxyId = its_args[argv].substring(idx + itsProxyIdCommand.length());
                    if( tempProxyId.length() > 0 )
                        itsProxyId = tempProxyId;
                }
                idx = its_args[argv].indexOf(itsServerPortCommand);
                if( idx > -1 )
                {
                    String tempServerPort = its_args[argv].substring(idx + itsServerPortCommand.length());
                    if( tempServerPort.length() > 0 )
                        itsServerPort = tempServerPort;
                }
            }

            // These lines are not needed unless you run the server behind a firewall within a DMZ
            // For some reason using the host name it doesn't work, needs to be the public IP address
            // They have no effect when server and clients are run locally
            props.put("jacorb.ior_proxy_host", itsProxyId );
            props.put("OAPort", itsServerPort);
            props.put("jacorb.log.default.verbosity", 0 );
            System.setProperties(props);

            its_ORB = ORB.init(its_args, props);
            its_rootPOA = POAHelper.narrow(its_ORB.resolve_initial_references("RootPOA"));

            Policy[] _policies;

            _policies = new Policy[]
                {
                    its_rootPOA.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID),
                    its_rootPOA.create_lifespan_policy(LifespanPolicyValue.TRANSIENT),
                    its_rootPOA.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION)
                };
            its_POA = its_rootPOA.create_POA("MyThreadedPOA", its_rootPOA.the_POAManager(), _policies);

            its_rootPOA.the_POAManager().activate();

            synchronized( itsMonitor )
            {
                itsMonitor.notify();
            }
            
            // Now kick off the ORB, whoo hoo...
            its_ORB.run();

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
