# Citibike Availability Visualized with Clojure/Quil

New York City recently launched a
[public bike share scheme](http://citibikenyc.com/) very much in the
spirit of
[similar systems around the world](http://en.wikipedia.org/wiki/Bicycle_sharing_system).
As an avid cyclist and fan of public transportation, I've been eager
to see how the program would do. Luckily,
[they've got an API](http://citibikenyc.com/stations/json) that
delivers on-demand station status, so I set up a
[cron](https://en.wikipedia.org/wiki/Cron) job to collect system-wide
information every 15 minutes and built a visualization.

[This video](https://vimeo.com/67653956) shows the first five business
days during which the Citibike service was available to the public.
Every second of the video depicts roughly one hour of real time. Pale
blue circles represent stations, with the circle's size scaled to the
capacity of that station. Dark blue circles represent how many bikes
are available at that station.

## Observations

There are 307 active stations reporting a total parking capacity of
10,680 slots. The data feed doesn't give per bike information, so I
was forced to estimate the number of bicycles in the system using the
greatest number parked at one time: 4,744. My data collection script
only ran every 15 minutes, but it managed to detect 32,947 trips over
the five days, with an estimated high of 844 bikes on the road at
once.

Monday was the first day on which the program was open to the public.
While there was considerable use of Citibikes that day, it was
substantially less than on subsequent days.

Commuters took to the system in earnest on Tuesday. Bicycles gathered
in various business districts -- Midtown, Downtown Brooklyn and Wall
Street -- between eight and nine in the morning, then dispersed to
more residential neighborhoods, like the Lower East Side and
Williamsburg, between five and seven in the evening. The same
pattern persists Wednesday and Thursday, but on Friday the bikes are
nearly static, presumably because it rained hard all day.

The busiest stations were all in Manhattan, and were all close to
major transportation hubs:

1. Broadway and W 57 Street (Columbus Circle)
2. 8 Avenue and W 31 Street (Penn Station)
3. W 41 Street and 8 Avenue (Port Authority)
4. W 33 Street and 7 Avenue (Penn Station, again)
5. Broadway and E 14 Street (Union Square)

I find it interesting to compare this with
[Alastair Coote](http://blogging.alastair.is)'s
[projection of which neighborhoods would most benefit from](http://experimenting.alastair.is/citibike/)
Citibikes.

## Technical Details

One obvious pair of tools for this sort of visualization is
[Processing](http://processing.org) and
[Till Nagel](http://tillnagel.com)'s tile map library,
[Unfolding](http://unfoldingmaps.org). Processing is great, and its
library ecosystem is immense, but I find the Processing language
frustrating for anything other than specifying the details of a
sketch. This led me to a [Clojure](http://clojure.org) wrapper for
Processing called [Quil](https://github.com/quil/quil). Quil allows
one to take advantage of Processing and related libraries in the
context of a much more expressive language. In addition, operating
Quil from [emacs](http://www.gnu.org/software/emacs/) via
[nrepl](https://github.com/clojure/tools.nrepl) makes it possible to
modify the "draw loop" of a sketch from the editor while the sketch is
running, which massively reduces development time.

When I looked into how one might go about using a supplemental
Processing library in Quil, all I found was
[this question](https://groups.google.com/forum/#!topic/clj-processing/wKzUljb_i4M)
in a Google Group, so I thought it might be worth documenting the
process here.

The easiest way to add Java libraries that are not available in
[Clojars](https://clojars.org) to a Clojure project is to install the
[lein-localrepo plugin](https://github.com/kumarshantanu/lein-localrepo).
Adding Unfolding with the plugin is as simple as:

``` bash
$ lein localrepo install lib/Unfolding.jar unfolding 0.9.3
```

After which one can add Unfolding to one's project.clj dependencies as
if it were in Clojars:

``` clojure
    [unfolding "0.9.3"]
```

The complete
[source code and data](https://github.com/jackrusher/citibikes) for
this visualization can be found at GitHub.
