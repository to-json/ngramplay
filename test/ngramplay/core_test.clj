(ns ngramplay.core-test
  (:require [clojure.test :refer :all]
            [ngramplay.core :refer :all]))

(deftest gutenberg-content-strips-gutenberg-metadata
  (is (= (gutenberg-content "fixtures/gutentest.txt") ["The truth"])))

(deftest useful-returns-false-for-matching-regexes
  (is (= (useful? [#"hat"] "All that") false))
  (is (= (useful? [#"This wont match" #"but this will"]
                  "Bullets wont kill it, but this will")
         false)))

;;; These tests are a bit of a straightjacket, but I really would like the
;;; data format to be consistent. 
(def example-results {["a" "cat"] #{"ran" "walked"} ["cat" "ran"] #{"up"}})

(deftest assoc-trigram-merges-a-trigram-into-results
  (is (= (assoc-trigram example-results ["a" "cat" "jumped"])
         {["cat" "ran"] #{"up"}, ["a" "cat"] #{"ran" "jumped" "walked"}}))
  (is (= (assoc-trigram example-results ["a" "cat" "ran"])
         {["cat" "ran"] #{"up"}, ["a" "cat"] #{"ran" "walked"}}))
  (is (= (assoc-trigram example-results ["no" "cat" "ran"])
         {["cat" "ran"] #{"up"}, ["a" "cat"] #{"ran" "walked"}, ["no" "cat"] #{"ran"}})))

(deftest assoc-line-merges-a-line-into-results
  (let [results example-results]
    (is (= (assoc-line 3 example-results "A cat walked over the bridge")
           {["cat" "walked"] #{"over"}, ["cat" "ran"] #{"up"}, ["over" "the"] #{"bridge"}, :holdover ["the" "bridge"], ["walked" "over"] #{"the"}, ["a" "cat"] #{"ran" "walked"}, ["A" "cat"] #{"walked"}})))
  (let [results (assoc-line 3 example-results "A cat walked over the bridge")]
    (is (= (assoc-line 3 results "to a cat it knew")
           {["cat" "walked"] #{"over"}, ["cat" "ran"] #{"up"}, ["over" "the"] #{"bridge"}, :holdover ["it" "knew"], ["walked" "over"] #{"the"}, ["a" "cat"] #{"it" "ran" "walked"}, ["cat" "it"] #{"knew"}, ["A" "cat"] #{"walked"}, ["to" "a"] #{"cat"}, ["the" "bridge"] #{"to"}, ["bridge" "to"] #{"a"}})))) 

(deftest words-breaks-strings-into-vectors
  (is (= (words "This is a: somewhat Silly TEST")
         ["This" "is" "a:" "somewhat" "Silly" "TEST"])))
