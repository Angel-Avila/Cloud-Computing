package mx.iteso.desi.cloud.lp1;

import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;
import mx.iteso.desi.cloud.keyvalue.IKeyValueStorage;
import mx.iteso.desi.cloud.keyvalue.PorterStemmer;

public class QueryImages {
  IKeyValueStorage imageStore;
  IKeyValueStorage titleStore;
	
  public QueryImages(IKeyValueStorage imageStore, IKeyValueStorage titleStore) 
  {
	  this.imageStore = imageStore;
	  this.titleStore = titleStore;
  }
	
  public Set<String> query(String word)
  {
      Set<String> imagesSet = new HashSet<>();

      Iterator<String> iter = titleStore.get(word).iterator();

      while (iter.hasNext()){
          Iterator<String> iter2 = imageStore.get(iter.next()).iterator();
          while (iter2.hasNext())
              imagesSet.add(iter2.next());
      }
      return imagesSet;
  }
        
  public void close()
  {
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

        QueryImages myQuery = new QueryImages(indexer.imageStore, indexer.titleStore);

        int count = 20;

        while(count > 0) {
            Scanner sc = new Scanner(System.in);
            String[] keys = sc.nextLine().split("\\s+");

            for (String key : keys) {
                String term = PorterStemmer.stem(key);

                if (term.equals("Invalid term")) {
                    term = key;
                }

                Set<String> result = myQuery.query(term);
                Iterator<String> iter = result.iterator();
                while (iter.hasNext())
                    System.out.println("  - " + iter.next());
            }

            count--;
        }


        myQuery.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

  }
}

