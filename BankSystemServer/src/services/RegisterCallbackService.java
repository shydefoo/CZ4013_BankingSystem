package services;

import java.net.InetAddress;

import message.BytePacker;
import message.ByteUnpacker;
import message.OneByteInt;
import services.CallbackHandlerClass.Subscriber;
import socket.Socket;

/**
 * This class handles requests to subscribe to the callback service. 
 * @author Shide
 *
 */
public class RegisterCallbackService extends Service {
	
	protected final static String TIMEOUT = "timeout";
	private CallbackHandlerClass callbackHandler;
	public RegisterCallbackService(CallbackHandlerClass callbackHandler) {
		super(new ByteUnpacker.Builder()
				.setType(TIMEOUT, ByteUnpacker.TYPE.INTEGER)
				.build());
		this.callbackHandler = callbackHandler; 
		
	}

	@Override
	public BytePacker handleService(InetAddress clientAddress, int clientPortNumber, byte[] dataFromClient,
			Socket socket) {
		//clientAddress, portnumber not in used at the moment...used to check at most once semantics/at least once? Hmmm
		ByteUnpacker.UnpackedMsg unpackedMsg = this.getUnpacker().parseByteArray(dataFromClient);
		int messageId = unpackedMsg.getInteger(Service.getMessageId());
		int timeout = unpackedMsg.getInteger(TIMEOUT);
		//Create subscriber object
		callbackHandler.registerSubscriber(clientAddress, clientPortNumber, messageId, timeout);
		OneByteInt status = new OneByteInt(0);
		String reply = "Auto-monitoring registered, waiting for updates...";
		BytePacker replyMessage = super.generateReply(status, messageId, reply);
		return replyMessage;
		
	}

	@Override
	public String ServiceName() {
		return "Register Callback";
	}

}
