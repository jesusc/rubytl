package org.rubypeople.rdt.internal.core.pmd;

import java.io.File;
import java.io.FilenameFilter;

public class RubyLanguage implements Language {

    public static class RubyFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith("rb") || filename.endsWith("cgi") ||
                    filename.endsWith("class") ||
                    (new File(dir.getAbsolutePath() + fileSeparator + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new RubyTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new RubyFileOrDirectoryFilter();
    }
}