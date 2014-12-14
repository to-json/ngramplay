(ns ngramplay.core
  (:require [clojure.string :as s])
  (:gen-class :main true))

(defn gutenberg-content [filename]
  "Strips useless front-matter and appendices from a gutenberg text,
  returning a seq of the core content"
  (with-open [rdr (clojure.java.io/reader filename)]
    (letfn [(separators [line]
              (not (re-find #"^\*\*\*" line)))
            (between [pred data]
              (take-while pred (drop 1 (drop-while pred data))))]
      (doall (between separators (line-seq rdr))))))

(defn useful? [regexes line]
  "Provided a list of regexes that match garbage data, maps that list
  over a provided string, returning false if the line is matched."
  (not (some #(re-find % line) regexes)))

(def noise-regexes
  "Any old vector of regexes (regexen?) will do but since this is a
  single purpose tool I can benefit from codifying the list for this
  particular task. Here we discard lines that are empty and lines that
  do not contain a lowercase letter"
  [#"^$" #"^[^a-z]*$"])

(defn words [line]
  "Splits a line into individual 'words', treating punctuation as letters"
  (s/split (s/trim line) #"\s"))

(defn assoc-trigram [results ngram]
  "Merge an individual trigram into the result set so far"
  (let [key (vec (butlast ngram))
        val (last ngram)]
    (if (contains? results key)
      (assoc results key (conj (results key) val))
      (assoc results key #{val}))))

(defn assoc-line [ngram-size results line]
  "This thing does the real work, taking a hash containing our ngram
  data and a line to add to it, breaking up the line, and joining the
  words in to the appropriate sets within the existing results"
  (let [holdover (:holdover results)
        base-words (words line)
        last-n (vec (take-last (- ngram-size 1) base-words))
        wordlist (remove nil? (concat holdover base-words))]
    (assoc (reduce assoc-trigram results (partition ngram-size 1 wordlist))
      :holdover last-n)))

(defn build-data-set [filename ngram-size]
  "Given a filename and an ngram size, builds a data set to babble from"
  (reduce (partial assoc-line ngram-size) {:holdover []}
          (filter (partial useful? noise-regexes)
                  (gutenberg-content filename))))

(defn babble [data-set]
  "Given a crafted set of n-gram data, generate a string of nonsense"
  (let [base (rand-nth (keys data-set))]
    (s/join " "
            (loop [result base
                   key base
                   wordlist (data-set key)]
              (if (empty? wordlist)
                result
                (let [new-word (rand-nth (vec wordlist))
                      new-key (flatten [(rest key) new-word])]
                  (recur (conj result new-word)
                         new-key
                         (data-set new-key))))))))

(defn validate-args [args]
  "Ensure my CLI args are at least somewhat tolerable"
  (let [[path n] args
        n (read-string n)]
    (assert (some #(= % (count args)) [1 2]) "Wrong number of arguments")
    (assert (= (type path) java.lang.String) "First argument isn't a string")
    (if n
      (assert (= (type n) java.lang.Long) "Second argument isn't an integer"))))

(defn -main
  "Spew generated text to stdout"
  [& args]
  (do
    (validate-args args)
    (let [[path n] args
          n (read-string n)]
      (println (babble (build-data-set path (or n 3)))))))
