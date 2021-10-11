package de.systemticks.c4dsl.ls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.service.C4LanguageServer;

public class C4LanguageServerLauncher {

    private static final Logger logger = LoggerFactory.getLogger(C4LanguageServerLauncher.class);

	public static void main(String[] args) {
		
		try {
            if(args != null && args.length > 1 && args[0].equals("--socket")) {
                logger.info("Starting Socket Connection");
                final AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("0.0.0.0", 5008));
                // echo to the client, that server is ready to receive incoming connections
                logger.info(args[1]);   
                final AsynchronousSocketChannel  socketChannel = serverSocket.accept().get();
                InputStream socketin = Channels.newInputStream(socketChannel);
                OutputStream socketOut = Channels.newOutputStream(socketChannel);
                startServer(socketin, socketOut);
            }
            else {
                logger.info("Starting ProcessIO Connection");
                startServer(System.in, System.out);
            }
		} 
        catch (ExecutionException e) {
			logger.error(e.getMessage());
		} 
        catch (InterruptedException e) {
			logger.error(e.getMessage());
		} 
        catch (IOException e) {
			logger.error(e.getMessage());
        }
		
	}
	
    private static void startServer(InputStream in, OutputStream out) throws ExecutionException, InterruptedException {
        // Initialize the HelloLanguageServer
    	logger.info("Launching C4LanguageServer started");
    	
    	C4LanguageServer c4LanguageServer = new C4LanguageServer();
        // Create JSON RPC launcher for HelloLanguageServer instance.
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(c4LanguageServer, in, out);

        // Get the client that request to launch the LS.
        LanguageClient client = launcher.getRemoteProxy();

        // Set the client to language server
        c4LanguageServer.connect(client);

        // Start the listener for JsonRPC
        Future<?> startListening = launcher.startListening();

        // Get the computed result from LS.
        startListening.get();
    	logger.info("Launching C4LanguageServer finished");
    }
	
}
