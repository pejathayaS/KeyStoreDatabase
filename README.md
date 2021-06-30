# KeyStoreDatabase
A look at implementation of Key-Value Store Service which should persist its state across restarts. 

A Persistent store is something that retains its state across restarts.

So, I have created a JAVA service to perform CRUD perations on key-value pair. I have implemented the API's tp work in O(1) time complexisty  ,except for the boot up time .This service is fast just like any key-value store should be!!  and retains back the state when restarted.
