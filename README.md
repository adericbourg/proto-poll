# Proto Poll [![Build Status](https://travis-ci.org/adericbourg/proto-poll.png?branch=dev)](https://travis-ci.org/adericbourg/proto-poll)

Proto Poll is a free poll system. It is released under the GNU GPL3 (see *LICENCE*). 

## Deployment

This software uses *Play! framework* 2.1.0 ([download](http://downloads.typesafe.com/play/2.1.0/play-2.1.0.zip "Download Play! Framework 2.1.0")).
You may find more informations about installing *Play!* on [its website](http://www.playframework.com/documentation/2.1.0/Installing "Play! Framework website") .

### Development

If you want to use the embedded H2 database, just launch:
```
play run
``` 
For debug mode, run:
```
play debug run
```

To use a MySQL database, just add ```-Dconfig.file=conf/mysql.conf``` to command line. You may want to specify other credentials by overriding the default one using ```-Ddb.default.url``` parameter.

### Production

Create production package running:
```
play dist
```
and deploy running the generated package in ```dist``` folder.
