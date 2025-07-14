package com.github.damiano1996.jetbrains.incoder.tool.window.chat.body.messages.ai.markdown.blocks;

import java.util.regex.Pattern;

public class PatternFinder {

    public int getFirstMatchIndex(String pattern, String match) throws PatternNotFound {
        var matcher = Pattern.compile(pattern).matcher(match);

        if (matcher.find()) {
            return matcher.start();
        }

        throw new PatternNotFound();
    }

    public int getLastMatchIndex(String pattern, String match) throws PatternNotFound {
        var matcher = Pattern.compile(pattern).matcher(match);

        int matchStartIndex = -1;

        while (matcher.find()) {
            matchStartIndex = matcher.start();
        }

        if (matchStartIndex == -1) throw new PatternNotFound();

        return matchStartIndex;
    }

    public int getNumberOfMatches(String pattern, String match) {
        var matcher = Pattern.compile(pattern).matcher(match);

        int matches = 0;

        while (matcher.find()) {
            matches++;
        }

        return matches;
    }

    public static class PatternNotFound extends Exception {}
}
