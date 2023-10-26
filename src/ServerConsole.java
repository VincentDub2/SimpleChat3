import common.ChatIF;

import java.io.BufferedReader;
import server.*;

import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {

    static final int DEFAULT_PORT = 2023;

    EchoServer server;

    public ServerConsole() {
        this.server = new EchoServer(DEFAULT_PORT, this);
    }

    public ServerConsole(int port) {
        this.server = new EchoServer(port, this);
    }

    public void accept()
    {
        try
        {
            BufferedReader fromConsole =
                    new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true)
            {
                message = fromConsole.readLine();
                server.handleMessageFromServerUI(message);
            }
        }
        catch (Exception ex)
        {
            System.out.println
                    ("Unexpected error while reading from console!");
        }
    }

    /**
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args[0] The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
     */
    public static void main(String[] args)
    {
        int port = 0; //Port to listen on

        System.out.println("Hello type #start for start the serveur and read the doc for more command");

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t) //if the program is lunch without params
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole chat = new ServerConsole(port);

        try
        {
            chat.accept();
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }
}
