package com.example.musictest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import android.util.Log;


public class XMLReader extends DefaultHandler {
 
    private final org.xml.sax.XMLReader xmlreader;
 
    public XMLReader() throws SAXException, ParserConfigurationException {
    	SAXParserFactory factory = SAXParserFactory.newInstance();

    	SAXParser parser = factory.newSAXParser();

    	xmlreader = parser.getXMLReader();
    	xmlreader.setContentHandler(this);
    	xmlreader.setErrorHandler(this);
        
    }
 
    public void read(final String url)
             throws IOException,
                       SAXException {
    	xmlreader.parse(new InputSource(new URL(url).openStream()));
    }
 
    @Override
    public void startDocument() {
        Log.w("hello","Comienzo del Documento XML");
    }
 
    @Override
    public void endDocument() {
    	Log.w("hello","Final del Documento XML");
    }
 
    @Override
    public void startElement(String uri, String name,
              String qName, Attributes atts) {
    	Log.w("hello","tElemento: " + name);
    	Log.w("hello","tElemento: " + qName);
 
        for (int i = 0; i < atts.getLength(); i++) {
        	Log.w("hello","ttAtributo: " +
          atts.getLocalName(i) + " = "+ atts.getValue(i));
        }
    }
 
    @Override
    public void endElement(String uri, String name,
                                 String qName) {
    	Log.w("hello","tFin Elemento: " + name);
    }
}