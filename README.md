a [yay](http://ianhenderson.org/yay.html) parser, for making file formats that look and feel like YAML, but are simple to parse and have none of the unneeded complexity.

all the actual functionality is in `yay.YayString`, a self-contained class that you can copy and use alone; the gradle setup is just for nullability annotations and testing. see the test for an example.