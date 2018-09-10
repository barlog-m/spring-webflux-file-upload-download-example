Spring WebFlux Upload/Download example

Exists of:
* Controller with two endpoints: upload and download
* Service with methods for save, load and check hash sum
* Unit test for controller
* Integration test for controller with examples, how to user WebClient for upload and download file
* Integration tests used [Jimfs](https://github.com/google/jimfs)
* For working with non default file system provided delegate of FilePart class
