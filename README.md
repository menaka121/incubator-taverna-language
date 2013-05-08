Data bundles API
================

API for Taverna Data Bundles

Work in progress.

See [Data bundle requirements](http://dev.mygrid.org.uk/wiki/display/TAVOSGI/2013-02+Data+bundle+requirements)
and [TestDataBundles.java](src/test/java/uk/org/taverna/databundle/TestDataBundles.java)

This API is built on the Java 7 NIO Files and uses the [Java 7 ZIP file provider](http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) to generate the Data Bundle.

The class [org.purl.wf4ever.robundle.Bundle](src/main/java/uk/org/taverna/databundle/DataBundle.java) complements the Java 7 [java.nio.Files](http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html) API with more specific helper methods to work with Data Bundles.


Example of use
--------------

Example in full is at [org.purl.wf4ever.robundle.TestExample](src/test/java/uk/org/taverna/databundle/TestExample.java)


Create a new (temporary) data bundle:
```java
        DataBundle dataBundle = DataBundles.createDataBundle();
```

Get the input ports and the port "in1":
```java        
        Path inputs = DataBundles.getInputs(dataBundle);
        Path portIn1 = DataBundles.getPort(inputs, "in1");
```

Setting a string value for the input port:
```java
        DataBundles.setStringValue(portIn1, "Hello");
```

And retrieving it:
```java
        if (DataBundles.isValue(portIn1)) {
            System.out.println(DataBundles.getStringValue(portIn1));
        }
```
```
Hello
```


Alternatively, use the regular Files methods:
```java
        for (String line : Files
                .readAllLines(portIn1, Charset.forName("UTF-8"))) {
            System.out.println(line);
        }
```


Binaries and large files are done through the Files API
```java
        try (OutputStream out = Files.newOutputStream(portIn1,
                StandardOpenOption.APPEND)) {
            out.write(32);
        }
```

Or Java 7 style:
```java
        Path localFile = Files.createTempFile("", ".txt");
        Files.copy(portIn1, localFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Written to: " + localFile);
```
```
Written to: C:\Users\stain\AppData\Local\Temp\2009921746200670789.txt
```

Either direction of copy works, of course:
```java
        Files.copy(localFile,
                DataBundles.getPort(DataBundles.getOutputs(dataBundle), "out1"));
```

   
When you get a port, it can become either a value or a list:
```java        
        Path port2 = DataBundles.getPort(inputs, "port2");
        DataBundles.createList(port2); // empty list
        List<Path> list = DataBundles.getList(port2);
        assertTrue(list.isEmpty());
```

Adding items sequentially:
```java
        Path item0 = DataBundles.newListItem(port2);
        DataBundles.setStringValue(item0, "item 0");
        DataBundles.setStringValue(DataBundles.newListItem(port2), "item 1");
        DataBundles.setStringValue(DataBundles.newListItem(port2), "item 2");
```
        
Set and get by explicit position:
```java
        DataBundles.setStringValue(DataBundles.getListItem(port2, 12), "item 12");
        System.out.println(DataBundles.getStringValue(DataBundles.getListItem(port2, 2)));
```
```
item 2
```
        
The list is sorted numerically (e.g. 2, 5, 10) and will contain nulls for empty slots:
```java
        System.out.println(DataBundles.getList(port2));
```
```
[/inputs/port2/0, /inputs/port2/1, /inputs/port2/2, null, null, null, null, null,
 null, null, null, null, /inputs/port2/12]
```

Ports can be browsed as a map by port name:
```java
        NavigableMap<String, Path> ports = DataBundles.getPorts(inputs);
        System.out.println(ports.keySet());
```
```
[in1, port2]
```
    
Saving a data bundle:    
```java
        Path zip = Files.createTempFile("databundle", ".zip");
        DataBundles.closeAndSaveDataBundle(dataBundle, zip);
        // NOTE: From now dataBundle and its Path's are CLOSED 
        // and can no longer be accessed
        System.out.println("Saved to " + zip);
```
```
Saved to C:\Users\stain\AppData\Local\Temp\databundle6905894602121718151.zip
```
        
Inspecting the zip:
```java        
        if (Desktop.isDesktopSupported()) {
            // Open ZIP file for browsing
            Desktop.getDesktop().open(zip.toFile());
        }
```        
        
Loading a data bundle back from disk:
```java
        try (DataBundle dataBundle2 = DataBundles.openDataBundle(zip)) {
            // Any modifications here will be saved on (here automatic) close            
        }     
```