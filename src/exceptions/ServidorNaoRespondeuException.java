package exceptions;

public class ServidorNaoRespondeuException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServidorNaoRespondeuException(){
		super("O servidor n�o respondeu � solicita��o");
	}
}
