import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.ArrayList;
public class Server{
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	private ArrayList<String> names=new ArrayList<String>(11);
	private ArrayList<Integer> scores=new ArrayList<Integer>(11);
	public static void main(String args[]){
		Server Anna=new Server();
		Anna.start();
	}
	//Set up and run the server
	public void start(){
		try{
			//6789 is port, number of people waiting to connect
			server=new ServerSocket(6789,10);
			while(true){
				try{
					//wait for someone to connect
					waitForConnection();
					setupStream();
					check();
				}catch(EOFException eof){
					showMessage("\n Server ended the connection! ");
				}finally{
					closeStreamAndConnection();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect...");
		connection=server.accept();
		showMessage("Now connected to "+connection.getInetAddress().getHostName());
	}

	//Get stream to receive data.
	private void setupStream() throws IOException{
		input=new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now set-up!");
	}

	private void check() throws IOException{
		try{
			showMessage("Checking Leaderboard...");
			String[] data=((String)input.readObject()).split(" ");
			int gameid=Integer.parseInt(data[0]);
			String name=data[1];
			int score=Integer.parseInt(data[2]);
			showMessage("Game: "+gameid+" Name: "+name+" Score: "+score);
			String file="";
			if(gameid==93604586)
				file="AsteroidsLeaderboard.dat";
			if(gameid==93604587)
				file="SnakeLeaderboard.dat";
			loadFromFile(file);
			compare(score,name);
			saveToFile(file);
		}catch(ClassNotFoundException cnf){
			cnf.printStackTrace();
		}
	}

	private void closeStreamAndConnection() throws IOException{
		showMessage("Closing connection");
		input.close();
		connection.close();
	}

	private void showMessage(String str){
		System.out.println(str);
	}

	private void loadFromFile(String filename) throws IOException{
		try{
			FileInputStream file=new FileInputStream(filename);
			Scanner in=new Scanner(file);
				for(int i=0;i<10&&in.hasNextLine();i++){
					String[] line=in.nextLine().split("\\|");
					names.add(line[1]);
					scores.add(Integer.parseInt(line[2]));
				}
			in.close();
			file.close();
		}catch(FileNotFoundException fnf){
			showMessage("File Not Found");
		}
	}

	private void saveToFile(String filename) throws IOException{
		try{
			FileWriter file=new FileWriter(filename);
			BufferedWriter out=new BufferedWriter(file);
				for(int i=0;i<10;i++){
					out.write((i+1)+"|"+names.get(i)+"|"+scores.get(i)+"\n");
				}
			out.close();
			file.close();
		}catch(FileNotFoundException fnf){
			showMessage("File Not Found");
		}
	}
	private void compare(int scr,String name){
		for(int i=0;i<10;i++)
			if(scr>scores.get(i)){
				scores.add(i,scr);
				names.add(i,name);
				break;
			}
	}
}
