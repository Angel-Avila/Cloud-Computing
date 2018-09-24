/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.iteso.desi.cloud.lp1;

import com.amazonaws.regions.Regions;
import mx.iteso.desi.cloud.keyvalue.KeyValueStoreFactory;

import java.io.File;

/**
 *
 * @author YO
 */
public class Config {

  private static String s = File.separator;
  public static final KeyValueStoreFactory.STORETYPE storeType = KeyValueStoreFactory.STORETYPE.DYNAMODB;
  public static final String pathToDatabase = System.getProperty("user.home") + s + "Documents" + s + "practica1";
    
  // Set to your Amazon REGION tu be used
  public static final Regions amazonRegion = Regions.US_EAST_1;
  
    
  // Restrict the topics that should be indexed. For example, when this is
  // set to 'X', you should only index topics that start with an X.
  // Set this to "A" for local work, and to "Ar" for cloud tests..
  public static final String filter = "Ar";
  
  public static final String titleFileName = "labels_en.ttl";
  public static final String imageFileName = "images_en.ttl";
    
}
