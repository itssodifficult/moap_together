package com.example.myapplication;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.io.*;

public class MyHandler extends DefaultHandler {
    boolean isRow = false;
    boolean isBizplcNm = false;

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("row")) {
            isRow = true;
        }
        if (qName.equalsIgnoreCase("BIZPLC_NM")) {
            isBizplcNm = true;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (isRow && isBizplcNm) {
            System.out.println("편의점 이름: " + new String(ch, start, length));
            // 다른 필요한 작업 수행
            isBizplcNm = false;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("row")) {
            isRow = false;
        }
    }

    public void parseXMLUsingSAX() {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(new MyHandler());
            xmlReader.parse(new InputSource(new FileInputStream("파일경로.xml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}