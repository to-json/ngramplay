# ngramplay

A short experiment in ngram analysis, as a 'solution' to:

http://codekata.com/kata/kata14-tom-swift-under-the-milkwood/

## Usage

Well, it's Clojure, so you have a few options. What I prefer most is:

```
cd ngramplay
lein uberjar
bin/ngramplay resources/grimm.txt 3
```

where 3 is the n in n-gram, and the path is a path to a Project Gutenberg
text file.

A single Gutenberg text is provided (Grimms Fairy Tales). This is the text
the line filtration regexes are optimized for, so while it'll technically
work with others, the results might not be as pleasing.

## License

Copyright © 2014 to-json

Distributed under WTFPL because it's not important enough for a 'real' license
