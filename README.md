sfstest
=======

Having problems with Smart Fox Server 2x regarding threading and extension requests

My initial question was posted here:
http://www.smartfoxserver.com/forums/viewtopic.php?f=18&t=15281&p=64542#p64542

I never did fix the problem, and took suggestions that using a thread per room was probably a bad idea.

I had to refactor the game so it wouldn't be dependent on a thread.

I presume if I had implemented a queue class then that would have fixed the problem.

Please feel free to experiment with the code and push updates as you like.

Cheers,
Daniel Downes
http://www.danieldownes.co.uk