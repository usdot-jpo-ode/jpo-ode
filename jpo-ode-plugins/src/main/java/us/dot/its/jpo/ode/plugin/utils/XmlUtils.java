package us.dot.its.jpo.ode.plugin.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * XMl Utilities
 */
public class XmlUtils {

    @SneakyThrows
    public static List<XmlToken> tokenize(String xml) {
        var factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(xml));
        return readTokens(reader);
    }

    public static List<XmlToken> readTokens(XMLStreamReader xmlReader) throws XMLStreamException {
        var tokens = new ArrayList<XmlToken>();
        var firstToken = addToken(xmlReader, tokens);
        while (xmlReader.hasNext()) {
            xmlReader.next();
            var thisToken = addToken(xmlReader, tokens);
            if (firstToken == null) {
                firstToken = thisToken;
            }
            if (thisToken != null && thisToken.isLast && thisToken.text.equals(firstToken.text)){
                break;
            }
        }
        return mergeEmptyElements(tokens);
    }

    @SneakyThrows
    public static List<XmlToken> readTokens(XMLStreamReader xmlReader, String endElement) {
        var tokens = new ArrayList<XmlToken>();
        addToken(xmlReader, tokens);

        while (xmlReader.hasNext()) {
            xmlReader.next();
            XmlToken token = addToken(xmlReader, tokens);
            if (token != null && token.isLast && token.text.equals(endElement)) {
                tokens.removeLast();
                break;
            }
        }
        return mergeEmptyElements(tokens);
    }

    private static XmlToken addToken(XMLStreamReader xmlReader, List<XmlToken> tokens) {
        XmlToken token = null;
        if (xmlReader.hasName()) {
            QName name = xmlReader.getName();
            if (xmlReader.isStartElement()) {
                token = new XmlToken(name.getLocalPart(), true, false);
                tokens.add(token);
            } else if (xmlReader.isEndElement()) {
                token = new XmlToken(name.getLocalPart(), false, true);
                tokens.add(token);
            }
        } else if (xmlReader.hasText() && !xmlReader.isWhiteSpace()) {
            token = new XmlToken(xmlReader.getText(), false, false);
            tokens.add(token);
        }
        return token;
    }

    // Merge adjacent elements with no text between into empty elements to
    // match the asn.1 style for enumerations and booleans.
    private static List<XmlToken> mergeEmptyElements(final List<XmlToken> xmlTokens) {
        final var mergedList = new ArrayList<XmlToken>();
        int i = 0;
        while (i < xmlTokens.size()) {
            XmlToken token1 = xmlTokens.get(i);
            if (i == xmlTokens.size() - 1) {
                mergedList.add(token1);
                break;
            }
            XmlToken token2 = xmlTokens.get(i + 1);
            if (token1.isFirst  && token2.isLast && token1.text.equals(token2.text)) {
                // Combine into empty element and skip 2
                mergedList.add(new XmlToken(token1.text, true, true));
                i += 2;
            } else {
                // Don't change
                mergedList.add(token1);
                i++;
            }
        }
        return mergedList;
    }

    public static String stringifyTokens(List<XmlToken> tokens) {
        var f = new Formatter();
        for (XmlToken token : tokens) {
            final String text = token.text;
            if (token.isFirst && token.isLast) {
                // Empty element
                f.format("<%s/>", text);
            } else if (token.isFirst) {
                // Start element
                f.format("<%s>", text);
            } else if (token.isLast) {
                // End element
                f.format("</%s>", text);
            } else {
                // Text value
                f.format(text);
            }
        }
        return f.toString();
    }

    public static List<List<XmlToken>> groupTopLevelTokens(final List<XmlToken> tokens) {
        XmlToken topLevel = null;
        var tokenLists = new ArrayList<List<XmlToken>>();
        List<XmlToken> tokenList = null;
        for (XmlToken token : tokens) {
            if (topLevel == null && token.isFirst) {
                // Start list
                topLevel = token;
                tokenList = new ArrayList<XmlToken>();
                tokenList.add(token);
            } else if (topLevel != null && token.isLast && token.text.equals(topLevel.text)) {
                // complete list
                tokenList.add(token);
                topLevel = null;
                tokenLists.add(tokenList);
            } else if (tokenList != null) {
                tokenList.add(token);
            }
        }
        return tokenLists;
    }

    public static List<XmlToken> unwrap(final List<XmlToken> tokens) {
        // Remove first and last
        if (tokens.size() > 2) {
            return tokens.subList(1, tokens.size() - 1);
        } else {
            return tokens;
        }
    }

    public static List<XmlToken> wrap(final List<XmlToken> tokens, String wrapper) {
        var wrapped = new ArrayList<XmlToken>();
        wrapped.add(new XmlToken(wrapper, true, false));
        wrapped.addAll(tokens);
        wrapped.add(new XmlToken(wrapper, false, true));
        return wrapped;
    }

    @AllArgsConstructor
    @Data
    public static class XmlToken {
        String text;
        boolean isFirst;
        boolean isLast;
    }
}
