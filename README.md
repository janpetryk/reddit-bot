# reddit-bot
Java Reddit bot project for raspberry pi.

This is my personal project of generic reddit bot that replies specific message to comments that fullfill given criteria.

At first, I was going to write this in python (since I read that python is great for this job and has raspberry pi offers great support for this language), but then I read this: http://www.teamten.com/lawrence/writings/java-for-everything.html. It is still work in progress but at the moment you can actually use it (by extending AbstractRedditBot.java - as in HelloRedditBot.java).

My goal for it is to be a bot that replies tweets to comments that contain twitter links. I want this project to be easy to set up (maven dependencies) and easy to deploy on any linux machine (ant task).
