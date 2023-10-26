// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;
import java.util.Observable;
import java.util.Observer;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient implements Observer {
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  ObservableClient oc;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
    this.oc = new ObservableClient(host, port);
    oc.addObserver(this);
    this.clientUI = clientUI;
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    
    boolean isSpecialCommand = checkSpecialCommand(message);
    if(isSpecialCommand) {
      handleSpecialCommand(message);
      return;
    }

    try
    {
      oc.sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  /**
   * Check if the message is a special command and corresponding to a special command (#quit, #logoff, #sethost, #setport, #login, #gethost, #getport)
   *
   * @param message The message to check
   *
   * @return true if the message is a special command
   */
  public boolean checkSpecialCommand(String message) {
    return message.startsWith("#");
  }

  /**
   * This method handle the special command
   *
   * @param message The message to handle
   */
  public void handleSpecialCommand(String message) {
    
    // Stop the client
    if(message.equals("#quit")) {
      System.out.println("Arrêt en cours du client...");
      quit();
    }

    // Close the connection with the server without stopping the client
    else if(message.equals("#logoff")) {
      specialCommandLogoff();
    }

    // Change the host of the server
    else if(message.startsWith("#sethost")) {
      specialCommandSethost(message);
    }

    // Change the port of the server
    else if(message.startsWith("#setport")) {
      specialCommandSetport(message);
    }

    // Connect the client to the server
    else if(message.equals("#login")) {
      specialCommandLogin();
    }

    // #gethost - affiche le nom de l'hôte auquel le client est connecté.
    else if(message.equals("#gethost")) {
      System.out.println(oc.getHost());
    }

    // #getport - affiche le port auquel le client est connecté.
    else if(message.equals("#getport")) {
      System.out.println(oc.getPort());
    }

    // The special command is not recognized
    else {
      System.out.println("The special command is not recognized");
    }
  }

  /**
   * This method handle the special command logoff
   */
  private void specialCommandLogoff() {
    try {
      oc.closeConnection();
    } catch (IOException e) {
      System.out.println("Error while closing connection");
    }
  }

  /**
   * This method handle the special command sethost
   * 
   * @param message The message to handle
   */
  private void specialCommandSethost(String message) {
    if(oc.isConnected()) {
        System.out.println("You can't change the host while you are connected");
        return;
      }
      String[] messageSplitted = message.split(" ");
      if(messageSplitted.length != 2) {
        System.out.println("You must specify a host");
        return;
      }
      oc.setHost(messageSplitted[1]);
      System.out.println("Host changed to " + messageSplitted[1]);
  }

  /**
   * This method handle the special command setport
   * 
   * @param message The message to handle
   */
  private void specialCommandSetport(String message) {
    if(oc.isConnected()) {
      System.out.println("You can't change the port while you are connected");
      return;
    }
    String[] messageSplitted = message.split(" ");
    if(messageSplitted.length != 2) {
      System.out.println("You must specify a port");
      return;
    }
    oc.setPort(Integer.parseInt(messageSplitted[1]));
    System.out.println("Port changed to " + messageSplitted[1]);
  }

  /**
   * This method handle the special command login
   */
  private void specialCommandLogin() {
    if(oc.isConnected()) {
      System.out.println("You are already connected");
      return;
    }
    try {
      oc.openConnection();
    } catch (IOException e) {
      System.out.println("Error while opening connection");
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      oc.closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  protected void connectionEstablished() {
    clientUI.display("Connection successfully established on port " + oc.getPort());
  }

  protected void connectionClosed() {
    clientUI.display("Connection with server has been lost");
  }

  protected void connectionException() {
    clientUI.display("An error as occurred : server shut down now");
    System.exit(0);
  }

  @Override
  public void update(Observable o, Object arg) {
      if (arg instanceof Exception){
        connectionException();
      } else if (arg.equals(ObservableClient.CONNECTION_CLOSED)){
        connectionClosed();
      } else if (arg.equals(ObservableClient.CONNECTION_ESTABLISHED)) {
        connectionEstablished();
      } else {
        handleMessageFromServer(arg);
      }
  }
}
//End of ChatClient class
