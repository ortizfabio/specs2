[![Build Status](https://travis-ci.org/etorreborre/specs2.png?branch=master)](https://travis-ci.org/etorreborre/specs2)
[![Join the chat at https://gitter.im/etorreborre/specs2](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/etorreborre/specs2?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Go to [specs2.org](http://specs2.org) to learn more about ***specs2***!

Installation instructions
=========================

You need to download and install sbt. Then execute the following command:
```
sbt update publishLocal
```
Then you can generate the User Guide with:
```
sbt testOnly org.specs2.guide.UserGuide -- html
```
This should create html files in the target/specs2-reports directory. 
