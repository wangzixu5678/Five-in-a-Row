package com.vhly.upsys.plist;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-8
 */
public class AbsSAXHandler extends DefaultHandler {
    protected Stack<String> level;
    protected String prevTag;
    protected StringBuilder currentText;

    @Override
    public void startDocument() throws SAXException {
        if (level == null) {
            level = new Stack<String>();
        }
        level.clear();
        if (currentText == null) {
            currentText = new StringBuilder();
        }
    }

    /**
     * 子类必须 在 startElement 中 最上方 调用 super.startElement
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     * @throws SAXException
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (!level.isEmpty()) {
            prevTag = level.peek();
        }
        if (localName != null) {
            level.push(localName);
        } else if (qName != null) {
            level.push(qName);
        }
        currentText.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentText.append(ch, start, length);
    }

    /**
     * 子类必须在 endElement 最下方调用 super.endElement
     *
     * @param uri
     * @param localName
     * @param qName
     * @throws SAXException
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!level.isEmpty()) {
            level.pop();
            if (!level.isEmpty()) {
                prevTag = level.peek();
            } else {
                prevTag = null;
            }
        }
        if (currentText != null) {
            currentText.setLength(0);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        if (level != null) {
            level.clear();
        }
        if (currentText != null) {
            currentText.setLength(0);
        }
    }
}
