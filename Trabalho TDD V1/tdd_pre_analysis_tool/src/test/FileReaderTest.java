package test;

import main.java.app.FileReader;
import main.java.app.exception.ArquivoNaoEncontradoException;
import main.java.app.exception.DelimitadorInvalidoException;
import main.java.app.exception.DisposicaoInvalidaException;
import main.java.app.exception.EscritaNaoPermitidaException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileReaderTest {
	
	private final FileReader fileReader = new FileReader();
	
	private static final String NOT_EXISTING_PATH = "C:/Users/NotExistingPath/file.txt";
	private static final String DELIMITER = ";";
	private static final String INVALID_DELIMITER = "--";
	private static final String EXISTING_PATH = System.getProperty("user.dir") +
	                                            "/tdd_pre_analysis_tool/src/main/resources/analysisMemory.out";
	
	@Test(expected = ArquivoNaoEncontradoException.class)
	public void fileNotFoundShouldThrowException() throws IOException, ArquivoNaoEncontradoException,
	                                                      DelimitadorInvalidoException, EscritaNaoPermitidaException {
		fileReader.loadFile(NOT_EXISTING_PATH, DELIMITER, "colunas");
	}
	
	@Test(expected = DisposicaoInvalidaException.class)
	public void dispositionInvalidException() throws IOException, ArquivoNaoEncontradoException,
	                                                 DelimitadorInvalidoException, EscritaNaoPermitidaException {
		
		fileReader.loadFile(EXISTING_PATH,
		                    DELIMITER, "qualquercoisa");
	}
	
	@Test(expected = EscritaNaoPermitidaException.class)
	public void fileWithoutPermissionToWriteShouldThrowException() throws IOException, ArquivoNaoEncontradoException,
	                                                                      DelimitadorInvalidoException,
	                                                                      EscritaNaoPermitidaException {
		
		File readOnlyFile = new File(EXISTING_PATH);
		
		readOnlyFile.setReadOnly();
		fileReader.loadFile(EXISTING_PATH, readOnlyFile.getPath(), DELIMITER);
		readOnlyFile.setReadable(true);
	}
	
	@Test(expected = DelimitadorInvalidoException.class)
	public void invalidDelimiterShouldThrowException() throws IOException, ArquivoNaoEncontradoException,
	                                                          DelimitadorInvalidoException, EscritaNaoPermitidaException {
		
		fileReader.loadFile(EXISTING_PATH, INVALID_DELIMITER, "colunas");
	}
	
}
