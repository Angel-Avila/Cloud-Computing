package mx.iteso.desi.cloud.lp1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.ParseTriples;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;
import mx.iteso.desi.cloud.keyvalue.Triple;

public class IndexImages {
  ParseTriples parser;
  IKeyValueStorage imageStore, titleStore;
    
  public IndexImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) {
	  this.imageStore = imageStore;
	  this.titleStore = titleStore;
  }
      
  public void run(String imageFileName, String titleFileName) throws IOException
  {
      if (Config.storeType == KeyValueStoreFactory.STORETYPE.DYNAMODB){

          parser = new ParseTriples(imageFileName);
          Triple t;

          List<Item> dictionary = new ArrayList<>();
          int inx = 0;

          while ((t = parser.getNextTriple()) != null ){

              if (t.getPredicate().equals("http://xmlns.com/foaf/0.1/depiction") && t.getSubject().startsWith("http://dbpedia.org/resource/" + Config.filter)) {
                  //if(!imageStore.exists(t.get(0)))
                  dictionary.add(new Item().withPrimaryKey("Keyword", t.getSubject())
                          .withNumber("inx", inx++)
                          .withString("Value", t.get(2)));
                  System.out.println(inx + ": " + t.getSubject() + " " + t.getPredicate() + " "+ t.getObject());
              }
              if (dictionary.size() == 25) {
                  imageStore.addToSet(dictionary);
                  dictionary.clear();
              }
          }

          if (dictionary.size() > 0) {
              imageStore.addToSet(dictionary);
              dictionary.clear();
          }

          parser.close();

          inx = 0;

          parser = new ParseTriples(titleFileName);
          HashSet<String> itemList = new HashSet<>();
          while ((t = parser.getNextTriple()) != null ){
              if (t.getPredicate().equals("http://www.w3.org/2000/01/rdf-schema#label") && t.getObject().startsWith(Config.filter)){
                  if (!itemList.contains(t.getSubject()))
                      if(imageStore.exists(t.getSubject()))
                          itemList.add(t.getSubject());



                  if (itemList.contains(t.getSubject())){
                      String[] keys = t.getObject().split("\\s+");
                      for (String key : keys) {
                          String term = PorterStemmer.stem(key);
                          System.out.println(inx+ ": "+ term);
                          if (term.equals("Invalid term"))
                              term = key;
                          dictionary.add(new Item().withPrimaryKey("Keyword", term)
                                  .withNumber("inx", inx++)
                                  .withString("Value", t.get(0)));
                          if (dictionary.size() == 25) {
                              titleStore.addToSet(dictionary);
                              dictionary.clear();
                          }
                      }
                  }
              }
          }
          if (dictionary.size() > 0) {
              titleStore.addToSet(dictionary);
              dictionary.clear();
          }

          parser.close();

          return;
      }

      parser = new ParseTriples(imageFileName);

      Triple tr = parser.getNextTriple();

      while(tr != null) {
          if (tr.getPredicate().equals("http://xmlns.com/foaf/0.1/depiction") && tr.getSubject().startsWith("http://dbpedia.org/resource/" + Config.filter)) {
              imageStore.addToSet(tr.getSubject(), tr.getObject());
          }
          tr = parser.getNextTriple();
      }

      parser.close();
      parser = new ParseTriples(titleFileName);

      tr = parser.getNextTriple();

      while(tr != null) {
          String object = tr.getObject();
          String value = tr.getSubject();

          if (tr.getPredicate().equals("http://www.w3.org/2000/01/rdf-schema#label") && object.startsWith(Config.filter) && imageStore.exists(value)) {

              String[] keys = object.split("\\s+");
              for (String key : keys) {

                  String term = PorterStemmer.stem(key);

                  if (term.equals("Invalid term")) {
                      term = key;
                  }

                  if(!titleStore.exists(term)) {
                      titleStore.addToSet(term, value);
                  }
              }
          }

          tr = parser.getNextTriple();
      }

      parser.close();
  }
  
  public void close() {
	  imageStore.close();
	  titleStore.close();
  }
  
  public static void main(String args[])
  {
    System.out.println("*** Alumno: Que Parres ponga esto aquí casi me hace poner una referencia a mi nombre HMMMMM");
    try {

      IKeyValueStorage imageStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType, 
    			"images");
      IKeyValueStorage titleStore = KeyValueStoreFactory.getNewKeyValueStore(Config.storeType, 
  			"terms");


      IndexImages indexer = new IndexImages(imageStore, titleStore);
      indexer.run(Config.imageFileName, Config.titleFileName);
      System.out.println("Indexing completed");
      indexer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed to complete the indexing pass -- exiting");
    }
  }
}

