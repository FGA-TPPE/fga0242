package main.java.app;

import main.java.app.exception.ArquivoNaoEncontradoException;
import main.java.app.exception.DelimitadorInvalidoException;
import main.java.app.exception.DisposicaoInvalidaException;
import main.java.app.exception.EscritaNaoPermitidaException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class FileReader {
	
	public void loadFile(final String path, final String delimiter, final String disposition,
	                     final String outputPath) throws IOException,
	                                                     ArquivoNaoEncontradoException,
	                                                     DelimitadorInvalidoException,
	                                                     EscritaNaoPermitidaException, DisposicaoInvalidaException {
		File file = new File(path);
		
		//verify if file exists
		verifyIsExistentFile(path, file);

		//verify delimiter length
		verifyInvalidDelimiterLength(delimiter);

		//verify input disposition - 'linhas' or 'colunas'
		verifyInputDisposition(disposition);

		//verify if file is writable
		verifyIsWritableFile(path, outputPath);

		writeOnFile(path, delimiter, disposition, outputPath, file);
	}

	private void writeOnFile(final String path,
							 final String delimiter,
							 final String disposition,
							 final String outputPath,
							 File file) throws IOException {

		int counter = 1;

		final String COLUMN_DELIMITER_NAME = "colunas";

		BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(file));
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath, false));

		readAndWriteInLineDirection(delimiter, bufferedReader, bufferedWriter, counter); //TODO change name of this method

		if (COLUMN_DELIMITER_NAME.equals(disposition)) {
			readAndWriteColumnDirection(path, delimiter, outputPath);
		}
	}

	private void readAndWriteColumnDirection(String path, String delimiter, String outputPath) throws IOException {

		Scanner scanner = new Scanner(new File(outputPath));
		ArrayList<String> linesArray = new ArrayList<>();

		while (scanner.hasNextLine()) {
			linesArray.add(scanner.nextLine());
		}
		scanner.close();

		int i = 0; //TODO refactor this variable name
		int j = 0; //TODO refactor this variable name

		String[][] newArray = new String[100][100];

		for (String linha: linesArray) {
			for (String palavra: linha.split(delimiter)) {
				newArray[i][j] = palavra;
				j++;
			}
			i++;
			j = 0;
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false));

		for (j = 0; j < 100; j++) {
			for (i = 0; i < 100; i++) {
				if ((newArray[i][j] != null)) {
					writer.append(newArray[i][j]);
					writer.append(delimiter);
				} else {
					writer.append("");
				}

			}
			writer.append(System.lineSeparator());
		}
		writer.close();
	}

	private void readAndWriteInLineDirection(final String delimiter,
											 final BufferedReader bufferedReader,
											 final BufferedWriter bufferedWriter,
											 int counter) throws IOException {

		String currentLine;
		String evolution;

		while ((currentLine = bufferedReader.readLine()) != null) {
			final boolean isNotEvolutionHeaderLine = !currentLine.contains("-");

			if (isNotEvolutionHeaderLine) {
				bufferedWriter.append(delimiter);
				bufferedWriter.append(currentLine);
			} else {
				final String PATTERN = "[^0-9]";
				final String EMPTY_REPLACEMENT = "";

				if (counter > 1 && currentLine.contains("-")) {
					bufferedWriter.append(System.lineSeparator());
					evolution = currentLine.replaceAll(PATTERN, EMPTY_REPLACEMENT);
					bufferedWriter.append(evolution);

					counter++;
				} else if (counter == 1) {
					evolution = currentLine.replaceAll(PATTERN, EMPTY_REPLACEMENT);
					bufferedWriter.append(evolution);

					counter++;
				}
			}
		}
		bufferedWriter.close();
	}

	private void verifyIsWritableFile(final String path, final String outputPath) throws IOException,
																				   		 EscritaNaoPermitidaException {

		File outputFile = new File(outputPath);
		Boolean isNewFileCreated = outputFile.createNewFile(); //TODO test this

		if (!outputFile.canWrite()) {
			throw new EscritaNaoPermitidaException("File not writable on path: " + path);
		}
	}

	private void verifyInputDisposition(final String disposition) throws DisposicaoInvalidaException {

		final String ROW_DISPOSITION_NAME = "linhas";
		final String COLUMN_DISPOSITION_NAME = "colunas";

		final boolean isValidDisposition = disposition.equalsIgnoreCase(COLUMN_DISPOSITION_NAME)
				|| disposition.equalsIgnoreCase(ROW_DISPOSITION_NAME);

		if (disposition.isEmpty() || !isValidDisposition) {
			throw new DisposicaoInvalidaException(
							"Invalid disposition: " + disposition + ". It should be 'linhas' or 'colunas'");
		}
	}

	private void verifyInvalidDelimiterLength(final String delimiter) throws DelimitadorInvalidoException {
		if (delimiter.length() > 1) {
			throw new DelimitadorInvalidoException("Delimiter should not have more than 1 character: " + delimiter);
		}
	}

	private void verifyIsExistentFile(final String path, final File file) throws ArquivoNaoEncontradoException {
		if (!file.exists()) {
			throw new ArquivoNaoEncontradoException("File not found on path: " + path);
		}
	}

}