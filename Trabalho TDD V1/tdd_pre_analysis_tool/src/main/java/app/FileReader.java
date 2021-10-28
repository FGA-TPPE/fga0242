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
		existentFileVerifier(path, file);

		//verify delimiter length
		verifyInvalidDelimiterLength(delimiter);

		//verify input disposition - 'linhas' or 'colunas'
		verifyInputDisposition(disposition);

		//verify if file is writable
		verifyIsWritableFile(path, outputPath);

		String st;
		String evolution;
		
		int counter = 1;
		
		BufferedReader br = new BufferedReader(new java.io.FileReader(file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, false));
		

		while ((st = br.readLine()) != null) {

			if (!st.contains("-")) {
				writer.append(delimiter);
				writer.append(st);
			} else {
				final String pattern = "[^0-9]";
				final String emptyReplacement = "";
					
				if (counter > 1 && st.contains("-")) {
					writer.append(System.lineSeparator());
					evolution = st.replaceAll(pattern, emptyReplacement);
					writer.append(evolution);
					counter++;
				} else if (counter == 1) {
					evolution = st.replaceAll(pattern, emptyReplacement);
					writer.append(evolution);
					counter++;
				}
			}
		}

		writer.close();

		if ("colunas".equals(disposition)) {

			Scanner s = new Scanner(new File(outputPath));
			ArrayList< String > arrayDeLinhas = new ArrayList < String > ();
			while (s.hasNextLine()) {
				arrayDeLinhas.add(s.nextLine());
			}
			s.close();

			int i = 0;
			int j = 0;
			String[][] newArray = new String[100][100];

			for (String linha: arrayDeLinhas) {
				for (String palavra: linha.split(delimiter)) {
					newArray[i][j] = palavra;
					j++;
				}
				i++;
				j = 0;
			}

			System.out.println(Arrays.toString(newArray[0]));
			System.out.println(Arrays.toString(newArray[1]));

			file = new File(path);
			writer = new BufferedWriter(new FileWriter(outputPath, false));

			i = 0;
			j = 0;

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

	private void existentFileVerifier(final String path, final File file) throws ArquivoNaoEncontradoException {
		if (!file.exists()) {
			throw new ArquivoNaoEncontradoException("File not found on path: " + path);
		}
	}

}