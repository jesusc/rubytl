package com.aptana.rdt.internal.core.gems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.rdt.core.gems.Gem;

public class GemParser {

	public Set<Gem> parse(String string) {
		if (string == null || string.trim().length() == 0) return new HashSet<Gem>();
		String[] raw = string.split("\n");
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < raw.length; i++) {
			lines.add(raw[i]);
		}
		if (lines.size() > 2) {
			lines.remove(0); // Remove first 3 lines from local list
			lines.remove(0);
			lines.remove(0);
		}
		return parseOutGems(lines);
	}

	public Set<Gem> parseOutGems(List<String> lines) {
		Set<Gem> gems = new HashSet<Gem>();
		for (int i = 0; i < lines.size();) {
			String nameAndVersion = lines.get(i);
			String description = "";
			if ((i + 1) < lines.size()) {
				description = lines.get(i + 1);
			}
			int j = 2;
			while (true) {
				if ((i + j) >= lines.size())
					break; // if there is no next line, break out
				String nextLine = lines.get(i + j);
				if (nextLine.trim().length() == 0)
					break; // if line is empty, break out
				description += " " + nextLine.trim(); // add line to
														// description
				j++; // move to next line
			}
			int openParen = nameAndVersion.indexOf('(');
			int closeParen = nameAndVersion.indexOf(')');
			String name = nameAndVersion.substring(0, openParen);
			String version = nameAndVersion
					.substring(openParen + 1, closeParen);
			gems.add(new Gem(name.trim(), version, description.trim()));
			i += (j + 1);
		}
		return gems;
	}

}
