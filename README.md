TourMe, a [CS194][CS194] Spring 2012 Project
--------------------------

* Members :
  - [Hee Min Yoo]
  - [Ji Hun Cho]
  - [Anastasia Ingargiola]
  - [Clint Anderson]


### Project Proposal : [link][link to proposal]


### Coding Style : [link][link to coding style]


Iteration By Iteration
-----------------------

* First Iteration Achievements:
  - Map locates user's position using *GPS*
  - Basic layouts (Menus, Icons, Maps, etc...) 
  - Created Basic DB locally (using phpmyadmin, mysql)
  - Instruction Page
  - Basic Multi-Language Support
* Second Iteration Achievements:
  - Map Markers (POI and Current Location)
  - Current Location and POI is shows up dynamically on map
  - Using Google API, gets your current location address
  - Attractions are now represented with map markers and path is drawn
  - From Attraction Route, spinner allows you to go detailed page
  - Add/Change/Remove in DB Tables
  - Default (safety) values for images and descriptions in DB
  - Timeouts in connecting the DB
  - Various UI Improvements
* Third Iteration Achievements:
  - Moved DB to the web (Amazon EC2) : [link][ec2db]
  - Sort Attraction List by geolocation (GPS)
  - TourMyMemory Server (rails) : [link][TourMyMemoryUser]
  - Taking picture and uploading to the server, using [this][CameraUpload] library
  - UI Improvements
  - TTS (Text To Speech) enabled for description
  - Date : 4/16/12 Monday
* Fourth Iteration Achievements:
  - TTS Modified (pause, playback works after first iteration)
  - Changed uploading picture method (from html library to using native HTTP POST)
  - Modified where GPS turns on and off 
    (By Android convention, only way to properly turn off app is to use back button)
  - TourMyMemory integration : [TourMyMemoryUser], [TourMyMemoryAll]
  - Date : 5/2/12 Wednesday (1-4PM, Soda 5th Floor), Poster Session

### * [DB][ec2db] is online


```
  Last Updated on 5/1/12 by hmy
```

  [Hee Min Yoo]: https://github.com/hmy "GitHub Page"
  [Ji Hun Cho]: https://github.com/creamsoup "GitHub Page"
  [Anastasia Ingargiola]: https://github.com/velvet117 "GitHub Page"
  [Clint Anderson]: https://github.com/clintanderson "GitHub Page"
  [link to proposal]: http://ec2-23-20-205-81.compute-1.amazonaws.com/TourMe/TourMeProposal.pdf 
  [link to coding style]: https://github.com/hmy/TourMeReadMe/blob/master/CODINGSTYLE.md
  [CS194]: http://phone.cs.berkeley.edu/dokuwiki/doku.php?id=194-22:sp2012
  [most recent version]: https://github.com/hmy/TourMeReadMe/blob/master/README.md
  [ec2db]: http://ec2-23-20-205-81.compute-1.amazonaws.com/phpmyadmin/ "EC2 DB"
  [webservice]: http://ec2-23-20-205-81.compute-1.amazonaws.com:2222/ "New Web Service"
  [TourMyMemoryUser]: http://ec2-23-20-205-81.compute-1.amazonaws.com:3000/tour_my_memory
  [TourMyMemoryAll]: http://ec2-23-20-205-81.compute-1.amazonaws.com:3000/tour_my_memory/showallmaps
  [CameraUpload]: https://github.com/brycecurtis/articles/tree/master/CameraUpload