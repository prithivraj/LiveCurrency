# Notes :

1. The app uses the contents of the ViewModel as the single source of truth. This truth is independently modified both locally and via the network and all such mutations cascade to the UI.
In theory, this truth could come also from a database or from another server in which case we may have to show loaders for every mutation.

2.  The polling mechanism can alternatively be done using RxJava's interval operator.

3. EditText is a tricky widget to implement this way because it was originally designed to take inputs from one source.
This can cause confusion in the events that it fires because we don't really know if the event was user generated or caused by a render operation.
For example this causes an infinite loop : https://github.com/prithivraj-toast/EventLoop