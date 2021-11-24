package de.systemticks.c4dsl.ls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.systemticks.c4dsl.ls.service.C4LanguageServer;
import de.systemticks.c4dsl.ls.utils.C4Utils;
import picocli.CommandLine;
import picocli.CommandLine.Option;

public class C4LanguageServerLauncher implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(C4LanguageServerLauncher.class);

    @Option(names = {"-c", "--connection"}, description = "socket, process-io")
    private String connectionType = "process-io";

    @Option(names = {"-e", "--echo"}, description = "Echo to the client, to inform that socket can now accept incoming connections")
    private String echo = "READY_TO_CONNECT";

    @Option(names = {"-ir", "--inlineRenderer"}, description = "Echo to the client, to inform that socket can now accept incoming connections")
    private String renderer = C4Utils.RENDERER_STRUCTURIZR;

    @Option(names = "-log") 
    private boolean logActivated;

    @Option(names = "-trace") 
    private boolean traceActivated;

    @Override
    public Integer call() throws Exception {		
		try {
            if(connectionType.contentEquals("socket")) {
                logger.info("Starting Socket Connection");
                final AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress("0.0.0.0", 5008));
                // echo to the client, that server is ready to receive incoming connections
                logger.info(echo);   
                final AsynchronousSocketChannel  socketChannel = serverSocket.accept().get();
                InputStream socketin = Channels.newInputStream(socketChannel);
                OutputStream socketOut = Channels.newOutputStream(socketChannel);
                startServer(socketin, socketOut, renderer);
            }
            else {
                logger.info("Starting ProcessIO Connection");
                startServer(System.in, System.out, renderer);
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
		
        return 1;
	}
	
    private static void startServer(InputStream in, OutputStream out, String renderer) throws ExecutionException, InterruptedException {
        // Initialize the HelloLanguageServer
    	logger.info("Launching C4LanguageServer started");
    	
    	C4LanguageServer c4LanguageServer = new C4LanguageServer(renderer);
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

	public static void main(String[] args) {
        int exitCode = new CommandLine( new C4LanguageServerLauncher()).execute(args);
        System.exit(exitCode);
    }
	
}
