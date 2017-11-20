package com.aptana.rdt.internal.core.gems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

import com.aptana.rdt.AptanaRDTTests;
import com.aptana.rdt.core.gems.Gem;

public class GemParserTest extends TestCase {
	
	public void testParsingLocalGems() {
		GemParser parser = new GemParser();
		String contents = getContents("src/com/aptana/rdt/internal/core/gems/local.txt");
		Set<Gem> gems = parser.parse(contents);
		assertEquals(31, gems.size());
	}
	
	public void testEndsWithTwoLineDescription() {
		GemParser parser = new GemParser();
		String contents = getContents("src/com/aptana/rdt/internal/core/gems/2line_description_end.txt");
		Set<Gem> gems = parser.parse(contents);
		assertEquals(21, gems.size());
	}
	
	public void testMattsBrokenList() {
		GemParser parser = new GemParser();
		String contents = getContents("src/com/aptana/rdt/internal/core/gems/matt.txt");
		Set<Gem> gems = parser.parse(contents);
		assertEquals(22, gems.size());
	}

	private String getContents(String path) {
		File file = AptanaRDTTests.getFileInPlugin(new Path(path));
		BufferedReader reader = null;
		StringBuffer buffer;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			buffer = new StringBuffer();
			while((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			buffer.deleteCharAt(buffer.length() - 1); // delete last newline
			return buffer.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return null;
	}

}
