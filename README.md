It is a reddit-wide bot that parses comments and checks if they have Twitter link in them. 

Bot profile: http://www.reddit.com/user/TweetsInCommentsBot

If a comment contains a link then bot checks if user already submitted body of a tweet within comment and if not it posts value of a tweet as a child response.

It is written entirely in Java. I started writing it in python but I do not know this language very well (if at all), so with great support for Java on RPi and after reading this blog: http://www.teamten.com/lawrence/writings/java-for-everything.html I decided to pick that language. I wanted it to be as modular and generic so I can reuse the main frame of the appliation if I wanted to create another bot.

I run into some difficulites, mostly caused by reddit and twitter api, their respective limitations and what not. Also JRAW introduced two major bugs to my application and they were rather hard to debug and solve.

It is run on Raspberry PI, but can be run on any machine. I have automated deploying process for linux system.
