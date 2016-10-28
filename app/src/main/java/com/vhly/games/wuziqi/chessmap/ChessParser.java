package com.vhly.games.wuziqi.chessmap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-14
 */
public class ChessParser {
    public static void parseSteps(InputStream in, DefaultHandler handler) {
        if (in != null) {
            if (handler != null) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                try {
                    SAXParser parser = factory.newSAXParser();
                    parser.parse(in, handler);
                    parser = null;
                    factory = null;
                } catch (SAXException e) {

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
